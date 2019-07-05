package constraintsmanipulation.sat;

import java.util.List;

import constraintsmanipulation.manipulator.Manipulators;
import constraintsmanipulation.model.Combination;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.FICType;
import constraintsmanipulation.model.Model;
import tgtlib.definitions.expression.AndExpression;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.NotExpression;
import tgtlib.definitions.expression.Operator;
import tgtlib.definitions.expression.XOrExpression;

public class SATUtils {
	
	
	/** @return the FIC type, in particular FICType.AND also in case it can be of both fault types 
	 * (depending on the oracle, which we don't know) */
	public static boolean canBeFICType(Model model, Expression alpha, FICType type, YicesJNA yices) {
		if (alpha==null || model==null) return false;
		return (type==FICType.AND && yices.isSAT(AndExpression.makeAndExpression(model.toSingleExpression(), alpha))) 
			|| (type==FICType.OR && yices.isSAT(AndExpression.makeAndExpression(NotExpression.createNotExpression(model.toSingleExpression()), alpha)));
	}
	
	/** Compute equivalence using YicesJNA provided by the user */
	public static boolean isEquivalent(Model m1, Model m2, YicesJNA yices) {
		return !yices.isSAT(XOrExpression.mkBinExpr(m1.toSingleExpression(),Operator.XOR,m2.toSingleExpression()));
	}
	
	public static void checkEquivalence(List<Configuration> configurations, YicesJNA yices) throws RuntimeException {
		Configuration[] a = configurations.toArray(new Configuration[0]);
		checkEquivalence(a[0], a, yices);
	}
	
	public static void checkEquivalence(Configuration baseConf, Configuration[] c2, YicesJNA yices) throws RuntimeException {
		System.out.print("CHECKING EQUIVALENCE...");
		for (int i=0; i<c2.length; i++) {
			if (i==0 && yices.isSAT(XOrExpression.mkBinExpr(baseConf.model.toSingleExpression(), Operator.XOR, c2[i].model.toSingleExpression()))) {
				throw new RuntimeException("Models not equivalent: 0");
			}
			else if (i>0 && yices.isSAT(XOrExpression.mkBinExpr(c2[i-1].model.toSingleExpression(), Operator.XOR, c2[i].model.toSingleExpression()))) {
				throw new RuntimeException("Models not equivalent: "+i+" "+Manipulators.mans[i].getSimplifierName()+"\n"+c2[i-1]+"\n"+c2[i]+"\n"+Combination.toExpr(Sat4j.getInstance().getSAT(XOrExpression.mkBinExpr(c2[i-1].model.toSingleExpression(), Operator.XOR, c2[i].model.toSingleExpression()))));
			}
		}
		System.out.println("OK");
	}
	
	/** check equivalence using Sat4j (may be slow for large models) */
	public static boolean checkEquivalence(Configuration c1, Configuration c2) {
		return !Sat4j.getInstance().isSAT(XOrExpression.mkBinExpr(c1.model.toSingleExpression(), Operator.XOR, c2.model.toSingleExpression()));
	}
}
