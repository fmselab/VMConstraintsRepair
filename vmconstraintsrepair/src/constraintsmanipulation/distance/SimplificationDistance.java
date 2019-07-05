package constraintsmanipulation.distance;

import constraintsmanipulation.model.Formula;
import constraintsmanipulation.visitor.ToString;
import tgtlib.definitions.expression.Expression;

/**
 * The Class SimplificationDistance.
 *
 * @author Marco
 */
public class SimplificationDistance extends DistanceCriterion {
	
	/**
	 * Instantiates a new simplification distance.
	 */
	private SimplificationDistance() {}
	
	/** The instance. */
	private static SimplificationDistance instance;
	
	/**
	 * Gets the single instance of SimplificationDistance.
	 *
	 * @return single instance of SimplificationDistance
	 */
	public static SimplificationDistance getInstance() {
		return instance==null ? instance=new SimplificationDistance() : instance;
	}
	
	public double getDistance(Expression e1, Expression e2) {
		if (e1!=null && e2!=null && e1.equals(e2)) return 0;
		Formula f1 = new Formula(e1==null?"":e1.accept(new ToString()));
		Formula f2 = new Formula(e2==null?"":e2.accept(new ToString()));
		return Math.max(f1.getNumOperators(), f2.getNumOperators())==0?0 : f1.getNumFeatures()-f2.getNumFeatures() + (f1.getNumOperators()-f2.getNumOperators())/Math.max(f1.getNumOperators(), f2.getNumOperators());
	}
	
	@Override
	public String getName() {return "SIMPL";}
}
