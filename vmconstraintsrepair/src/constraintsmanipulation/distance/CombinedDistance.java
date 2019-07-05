package constraintsmanipulation.distance;

import tgtlib.definitions.expression.Expression;

public class CombinedDistance extends DistanceCriterion {
	public double alpha;
	
	@Override
	public double getDistance(Expression e1, Expression e2) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String getName() {return "COMBINED";}
}
