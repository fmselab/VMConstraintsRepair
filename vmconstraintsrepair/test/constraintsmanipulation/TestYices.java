/*
 * 
 */
package constraintsmanipulation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jna.ptr.PointerByReference;

import atgt.yices2.generator.ExprToYicesPtr;
import constraintsmanipulation.manipulator.Mutation;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.sat.Sat4j;
import constraintsmanipulation.sat.Yices;
import constraintsmanipulation.sat.YicesJNA;
import constraintsmanipulation.sat.YicesJNAPushPop;
import constraintsmanipulation.visitor.ToSMT;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;
import tgtlib.definitions.expression.Operator;
import tgtlib.definitions.expression.XOrExpression;
import tgtlib.definitions.expression.parser.ExpressionParser;
import tgtlib.definitions.expression.parser.ParseException;
import tgtlib.definitions.expression.visitors.IDExprCollector;
import yices2.Yices2Library;

public class TestYices {
	
	static final private Logger LOG = Logger.getLogger(TestYices.class);
	
	@BeforeClass
	public static void setup(){
		Logger.getLogger(TestYices.class).setLevel(Level.DEBUG);
	}
	
	@org.junit.Test
	public void test1() throws ParseException {
		System.out.println((Yices.getInstance()).isSAT(ExpressionParser.parseAsNewBooleanExpression("A and B")));

		System.out.println((Yices.getInstance()).getSAT(ExpressionParser.parseAsNewBooleanExpression("A and B")).keySet());
		System.out.println((Yices.getInstance()).getSAT(ExpressionParser.parseAsNewBooleanExpression("A or (B and (C => D))")).keySet());
		System.out.println((Sat4j.getInstance()).getSAT(ExpressionParser.parseAsNewBooleanExpression("A or (B and (C => D))")).keySet());
		
		System.out.println((Yices.getInstance()).isSAT(ExpressionParser.parseAsNewBooleanExpression("A and !A")));
	}
	
	/** Test del funzionamento di Yices per restituire una combinazione che sia SAT */
	@org.junit.Test
	public void test2() throws ParseException {
		Configuration c = Models.REGISTER.loadConfiguration();
		Configuration c1 = Mutation.RC.mutate(c);
		System.out.println(c+"\n\n"+c1);
		Expression e = XOrExpression.mkBinExpr(c.model.toSingleExpression(), Operator.XOR, c1.model.toSingleExpression());
		System.out.println(e.accept(ToSMT.getInstance()));
		System.out.println(Yices.getInstanceWithLog().getSAT(e));
	}
	
	@org.junit.Test
	public void test1JNA() throws ParseException {
		Expression e = ExpressionParser.parseAsNewBooleanExpression("A and B");
		YicesJNA y = new YicesJNA(e.accept(IDExprCollector.instance));
		System.out.println(y.isSAT(ExpressionParser.parseAsNewBooleanExpression("A and B")));

		System.out.println(y.getSAT(ExpressionParser.parseAsNewBooleanExpression("A and B")).keySet());
		System.out.println(y.getSAT(ExpressionParser.parseAsNewBooleanExpression("A or (B and (C => D))")).keySet());
		System.out.println((Sat4j.getInstance()).getSAT(ExpressionParser.parseAsNewBooleanExpression("A or (B and (C => D))")).keySet());
		
		System.out.println(y.isSAT(ExpressionParser.parseAsNewBooleanExpression("A and !A")));
	}
	
	@Test
	public void testMultipleContextsJNA() throws ParseException {
		Expression e = ExpressionParser.parseAsNewBooleanExpression("A or B");
		
		Yices2Library y = Yices2Library.INSTANCE;
		y.yices_init();    // Always call this first
		PointerByReference ctx = new YicesJNAPushPop(e.accept(IDExprCollector.instance)).createNewContext();
		
		LOG.debug("Collecting parameters...");
		Collection<IdExpression> ids = IDExprCollector.getIdsAsList(e);
		LOG.debug("Adding parameters...");
		
		Map<IdExpression, Integer> parameterMap = new HashMap<>();
		for (IdExpression parameter : ids) {
			int p = y.yices_new_uninterpreted_term(y.yices_bool_type());
			parameterMap.put(parameter, p);
		}
		LOG.debug("Converting expression...");
		ExprToYicesPtr converter = new ExprToYicesPtr(y, parameterMap);
		int f = e.accept(converter);
		//LOG.debug("Adding expression to SMT...");
		int code = y.yices_assert_formula(ctx, f);
		y.yices_push(ctx);
		if (code < 0) {
			System.out.println("errore " + y.yices_error_code());
		}
		//LOG.debug("Checking SAT...");
		printResult(y, ctx, parameterMap);
		
		int t = ExpressionParser.parseAsNewBooleanExpression("!A and !B").accept(converter);
		y.yices_assert_formula(ctx, t);
		printResult(y, ctx, parameterMap);
		
		// reset the context and reuse the same formula
		y.yices_pop(ctx);
		y.yices_push(ctx);
		t = ExpressionParser.parseAsNewBooleanExpression("!B").accept(converter);
		y.yices_assert_formula(ctx, t);
		printResult(y, ctx, parameterMap);
		
		y.yices_pop(ctx);
		t = ExpressionParser.parseAsNewBooleanExpression("!A and !B").accept(converter);
		y.yices_assert_formula(ctx, t);
		printResult(y, ctx, parameterMap);
		
	}
	
	public static void printResult(Yices2Library y, PointerByReference ctx, Map<IdExpression, Integer> parameterMap) {
		int result = y.yices_check_context(ctx, null);
		LOG.debug("SAT checked..."+(result==Yices2Library.smt_status.STATUS_SAT));
		if (result==Yices2Library.smt_status.STATUS_SAT) {
			LOG.debug("Getting model....");
			PointerByReference model = y.yices_get_model(ctx, 0);
			LOG.debug("Mapping model...");
			Map<IdExpression,Boolean> modelMap = new HashMap<>();
			for (Entry<IdExpression, Integer> p : parameterMap.entrySet()) {
				int value = y.yices_formula_true_in_model(model, p.getValue());
				if (value!=-1) {
					modelMap.put(p.getKey(), value==1);
					LOG.debug(p.getKey()+": "+value);
				}
			}
		}
	}

}
