package constraintsmanipulation.simplifier;

import constraintsmanipulation.distance.DistanceCriterion;
import constraintsmanipulation.distance.SyntaxTreeDistance;
import tgtlib.definitions.expression.Expression;

/**
 * The Class BestSimplifier.
 *
 * @author Marco
 */
public class BestSimplifier extends Simplifier {
	
	public DistanceCriterion dc = SyntaxTreeDistance.getInstance();
	
	public BestSimplifier() {}
	public BestSimplifier(DistanceCriterion dc) {this.dc = dc;}
	public void setDistanceCriterion(DistanceCriterion dc) {this.dc=dc;}
	
	@Override
	public String getName() {return "best"+dc.getName();}
	
	@Override
	public Expression simplify(Expression f) {
		Expression simpl = f;
		Expression maxSimpl = simpl;
		double minDistance = 100;
		boolean minDistanceComputed=false;
		for (Simplifier s : Simplifiers.simplifiers) if (!minDistanceComputed || dc.getDistance(simpl = s.simplify(f), f)<minDistance) {
			minDistance = dc.getDistance(simpl, f);
			maxSimpl=simpl;
		}
		return maxSimpl;
	}
	
	public Expression simplify(Expression f, DistanceCriterion dc) {
		setDistanceCriterion(dc);
		return simplify(f);
	}
}
