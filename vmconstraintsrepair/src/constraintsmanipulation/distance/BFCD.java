package constraintsmanipulation.distance;

import constraintsmanipulation.visitor.LiteralCounter;
import tgtlib.definitions.expression.Expression;

/**
 * 
 * @author marcoradavelli
 *
 */
public class BFCD extends DistanceCriterion {

	public static BFCD instance = new BFCD();
	
	public double getDistance(Expression e1, Expression e2) {
		return getAbsoluteValue(e1)-getAbsoluteValue(e2);
	}
	
	public double getAbsoluteValue(Expression e) {
		return e==null ? 0 : e.accept(LiteralCounter.instance); // new Formula(e==null?"":e.accept(new ToString())).getNumOperators();
	}
	
	@Override
	public String getName() {return "BFCD";}
}
