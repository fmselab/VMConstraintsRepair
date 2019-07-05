package constraintsmanipulation.distance;

import constraintsmanipulation.visitor.LiteralCounter;
import tgtlib.definitions.expression.Expression;

/** Complexity distance index
 * 
 * @author marcoradavelli
 *
 */
public class Cdi extends DistanceCriterion {

	public static Cdi instance = new Cdi();
	
	/** @return the distance e1-e2 (positive when e1 is more complex than e2) */
	public double getDistance(Expression e1, Expression e2) {
		double v2 = getAbsoluteValue(e2);
		return (getAbsoluteValue(e1)-v2) / v2;
	}
	
	public double getAbsoluteValue(Expression e) {
		return e==null ? 0 : e.accept(LiteralCounter.instance); // new Formula(e==null?"":e.accept(new ToString())).getNumOperators();
	}
	
	@Override
	public String getName() {return "BFCD";}
}
