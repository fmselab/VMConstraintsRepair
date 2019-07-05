package constraintsmanipulation.sat;

import java.nio.IntBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.sun.jna.ptr.PointerByReference;

import atgt.yices2.generator.ExprToYicesPtr;
import constraintsmanipulation.model.Combination;
import constraintsmanipulation.utils.PrettyPrintingMap;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;
import yices2.Yices2Library;

/** To use Yices.as SMT Solver push pop, optimized when multiple queries with a constant part of constraints */
public class YicesJNAPushPop {

	static final private Logger LOG = Logger.getLogger(YicesJNAPushPop.class);
	
	public YicesJNAPushPop(Collection<IdExpression> params) {
		y = Yices2Library.INSTANCE;
		y.yices_init();    // Always call this first
		ctx = createNewContext();
		setIds(params);
	}
	
	PointerByReference ctx;
	Yices2Library y;
	Map<IdExpression,Integer> parameterMap;
	ExprToYicesPtr converter;
	Collection<IdExpression> ids;
	
	public void reset() {
		ctx=null; y=null; parameterMap=null;
	}
	
	public void setContext(PointerByReference ctx) {this.ctx=ctx;}
	public PointerByReference getContext() {return ctx;}
	
	public PointerByReference createNewContext() {
		PointerByReference config = y.yices_new_config();
		y.yices_default_config_for_logic(config, "NONE"); // set the propositional logic
		return y.yices_new_context(config);
	}
	
	public void setIds(Collection<IdExpression> params) {
		this.ids=params;
		parameterMap = new HashMap<>();
		for (IdExpression parameter : ids) {
			int p = y.yices_new_uninterpreted_term(y.yices_bool_type());
			parameterMap.put(parameter, p);
		}
		converter = new ExprToYicesPtr(y, parameterMap);
	}
	
	public Map<IdExpression,Boolean> getSATWithPush(Expression e, boolean getModel) {
		LOG.debug("Converting expression...");
		LOG.debug("Map: "+new PrettyPrintingMap<IdExpression, Integer>(parameterMap));
		int f = e.accept(converter);
		int code = y.yices_assert_formula(ctx, f);
		if (code < 0) LOG.debug("errore " + y.yices_error_code());
		else y.yices_push(ctx);
		return printResult(y, ctx, parameterMap, getModel);
	}
	
	public Map<IdExpression,Boolean> getSATWithPushPop(Combination c, boolean getModel) {
		LOG.debug("Converting expression...");
		int f = toYices(c, ctx);
		y.yices_pop(ctx);
		y.yices_push(ctx);
		int code = y.yices_assert_formula(ctx, f);
		if (code < 0) LOG.debug("errore " + y.yices_error_code());
		return printResult(y, ctx, parameterMap, getModel);
	}
	
	public static Map<IdExpression,Boolean> printResult(Yices2Library y, PointerByReference ctx, Map<IdExpression, Integer> parameterMap, boolean getModel) {
		LOG.debug("Checking SAT...");
		int result = y.yices_check_context(ctx, null);
		LOG.debug("SAT checked..."+(result==Yices2Library.smt_status.STATUS_SAT));
		if (result==Yices2Library.smt_status.STATUS_SAT) {
			if (getModel) {
				LOG.debug("Getting model....");
				PointerByReference model = y.yices_get_model(ctx, 0);
				LOG.debug("Mapping model...");
				Map<IdExpression,Boolean> modelMap = new HashMap<>();
				for (Entry<IdExpression, Integer> p : parameterMap.entrySet()) {
					int value = y.yices_formula_true_in_model(model, p.getValue());
					if (value!=-1) modelMap.put(p.getKey(), value==1);
				}
				return modelMap;
			} else return new HashMap<>(); // just return an empty HashMap if the model is not needed
		} 
		else return null;
	}
	
	public int toYices(Combination c, PointerByReference context) {
		int[] values = new int[c.getMap().size()];
		int i=0;
		for (Entry<IdExpression, Boolean> e : c.getMap().entrySet()) {
			if (e.getValue()) values[i]=parameterMap.get(e.getKey());
			else values[i]=y.yices_not(parameterMap.get(e.getKey()));
			i++;
		}
		IntBuffer array = IntBuffer.wrap(values);
		return y.yices_and(values.length, array);
	}

}
