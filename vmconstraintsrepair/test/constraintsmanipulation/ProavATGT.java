/*
 * 
 */
package constraintsmanipulation;

import java.util.Calendar;
import java.util.HashSet;

import constraintsmanipulation.sat.ATGTYices;
import constraintsmanipulation.simplifier.ATGTSimplifier;
import constraintsmanipulation.simplifier.ATGTYicesSimplifier;
import constraintsmanipulation.visitor.IdExpressionFrequency;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.parser.ExpressionParser;
import tgtlib.definitions.expression.parser.ParseException;
import tgtlib.definitions.expression.type.BooleanVar;
import tgtlib.definitions.expression.type.EnumConstCreator;
import tgtlib.definitions.expression.visitors.IDExprCollector;

// TODO: Auto-generated Javadoc
/**
 * The Class ProavATGT.
 */
public class ProavATGT {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws ParseException the parse exception
	 */
	public static void main(String[] args) throws ParseException {
		EnumConstCreator idc = new EnumConstCreator();
		Expression expr = ExpressionParser.parseAsBooleanExpression("(a and b) and a", idc);
		HashSet<BooleanVar> ids = new HashSet<>(IDExprCollector.getBoolVarsFromId(expr));
		System.out.println(ids.toString());
		IdExpressionFrequency idf = new IdExpressionFrequency();
		System.out.println(expr.accept(idf));

		Expression expr2 = ExpressionParser.parseAsBooleanExpression("(a and b) and a",idc);
		ids.addAll(IDExprCollector.getBoolVarsFromId(expr2));
		System.out.println(ids.toString());

	}
	
	@org.junit.Test
	public void provaATGTYices() {
		try {
			Expression expr = ExpressionParser.parseAsNewBooleanExpression("(a and b and a)");
			long start=Calendar.getInstance().getTimeInMillis();
			System.out.println(ATGTSimplifier.getInstance().simplify(expr));
			System.out.println(Calendar.getInstance().getTimeInMillis()-start); start = Calendar.getInstance().getTimeInMillis();
			System.out.println(ATGTYices.applyMutations(expr));
			System.out.println(Calendar.getInstance().getTimeInMillis()-start); start = Calendar.getInstance().getTimeInMillis();
			System.out.println(ATGTYicesSimplifier.getInstance().simplify(expr));
			System.out.println(Calendar.getInstance().getTimeInMillis()-start);
		} catch (Exception e) {e.printStackTrace();}
	}
	
	@org.junit.Test
	public void provaATGTYicesComplicated() {
		try {
			Expression expr = ExpressionParser.parseAsNewBooleanExpression("((not((not(((not(((((not(((((((not(not(((not((not((not(not(not((((not((((((((not(((((((((not(((not(not(not(not(not((not(e_5 || e_1) || e_3) || e_1) || e_4) && e_0))) || e_5) || e_0) || e_5) || e_3) && e_3) && e_4) || e_3) || e_1) || e_0) || e_4) && e_4) || e_2) && e_4) || e_2) || e_3) && e_0) || e_3) && e_1) || e_5) && e_2) && e_5) && e_5) && e_1) || e_0) && e_0) || e_4) || e_1) || e_1) || e_1) || e_4) && e_3) || e_3) || e_4) || e_3) && e_2) && e_2) || e_5) && e_3) || e_2) && e_0) || e_1) && e_0) || e_3) && e_1) && e_4) || e_0) && e_1) && e_1) && e_2) || e_5) || e_3) && e_1) && e_2) && e_2");
			long start=Calendar.getInstance().getTimeInMillis();
			System.out.println(ATGTSimplifier.getInstance().simplify(expr));
			System.out.println(Calendar.getInstance().getTimeInMillis()-start); start = Calendar.getInstance().getTimeInMillis();
			System.out.println(ATGTYices.applyMutations(expr));
			System.out.println(Calendar.getInstance().getTimeInMillis()-start); start = Calendar.getInstance().getTimeInMillis();
			System.out.println(ATGTYicesSimplifier.getInstance().simplify(expr));
			System.out.println(Calendar.getInstance().getTimeInMillis()-start);
			//System.out.println(ATGTSlowSimplifier.getInstance().simplify(expr));
		} catch (Exception e) {e.printStackTrace();}
	}

}
