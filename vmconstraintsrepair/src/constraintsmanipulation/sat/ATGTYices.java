package constraintsmanipulation.sat;

import static extgt.coverage.fault.mutators.foms.MissingSubExpressionFault.MSF;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import constraintsmanipulation.utils.Util;
import tgtlib.definitions.expression.BinaryExpression;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;
import tgtlib.definitions.expression.Operator;
import tgtlib.definitions.expression.parser.ExpressionParser;
import tgtlib.definitions.expression.parser.ParseException;
import tgtlib.definitions.expression.visitors.IDExprCollector;
import tgtlib.util.Pair;

public class ATGTYices {
	
	public static long timeout=-1;
	private static long startingTime;
	private static YicesJNA y;
	
	public static void main(String[] args) {
		try {
			if (args.length>0) timeout = Long.parseLong(args[0]);
			System.out.println("Type formula to be simplified with ATGT method:");
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			String s=input.readLine();
			//System.out.println(applyMutations(s));
			applyMutations(ExpressionParser.parseAsNewBooleanExpression(s));
		} catch (Exception e) {e.printStackTrace();}
	}
	
	/** @return the simplified expressions equivalent to e */
	public static Expression applyMutations(Expression e) throws ParseException {
		startingTime = Calendar.getInstance().getTimeInMillis();
		
		// collect the variables
		Set<IdExpression> ids = e.accept(IDExprCollector.instance);
		y = new YicesJNA(ids);
		
		//List<Pair<Integer, Expression>> mutations = StuckAt.STUCK_AT1.getExpressionMutator().getMutations(e);
		List<Pair<Integer, Expression>> mutations = MSF.getExpressionMutator().getMutations(e);

		//System.out.print("\nok");
		Expression f=null, simple=e;
		for (Pair<Integer, Expression> m : mutations) {
			if (timeout>0 && Calendar.getInstance().getTimeInMillis()-startingTime >= timeout) {
				System.out.print("\n1 "+simple.toString());
				return simple;
			}
			// check equivalence
			if (checkEquivalence(ids,e,m.getSecond())){
				//f = new Formula(m.getSecond().toString());
				f = m.getSecond();
				if (Util.isSimplerThan(f, simple)) {
				//if (f.isSimplerThan(simple)) {
					simple = f; 
				}
			}
		}
		System.out.print("\n0 "+(simple==null ? "" : simple.toString()));
		return simple;
	}
	/** check if equivalent, they're equivalent if e1!=e2 � unsat */
	public static boolean checkEquivalence(Set<IdExpression> ids, Expression e1, Expression e2) {
		// e1 != e2
		BinaryExpression condition = BinaryExpression.mkBinExpr(e1, Operator.NEQ, e2);
		return !y.isSAT(condition);
	}

}
