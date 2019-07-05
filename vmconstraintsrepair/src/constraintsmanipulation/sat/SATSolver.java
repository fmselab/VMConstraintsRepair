package constraintsmanipulation.sat;

import java.util.Map;

import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;

/**
 * The Interface SATSolver.
 */
public interface SATSolver {
	
	/**
	 * Checks if is sat.
	 *
	 * @param e the e
	 * @return true, if is sat
	 */
	public boolean isSAT(Expression e);

	public Map<IdExpression, Boolean> getSAT(Expression e);
	
}
