package constraintsmanipulation.manipulator;

import constraintsmanipulation.model.Configuration;
import tgtlib.definitions.expression.NotExpression;
import tgtlib.definitions.expression.Operator;
import tgtlib.definitions.expression.OrExpression;

/**
 * @author Marco
 *  It repairs a model in the naive way: no selection nor simplification */
public class ManipulatorNaive extends Manipulator {
	
	/** The instance. */
	private static ManipulatorNaive instance;
	protected ManipulatorNaive() {}
	public static ManipulatorNaive getInstance() {return instance==null?instance=new ManipulatorNaive():instance;}
	
	@Override
	public Configuration repairAnd(Configuration c) {
		c.model.constraints.add(NotExpression.createNotExpression(c.fic.getAlpha()));
		return c;
	}
	
	@Override
	public Configuration repairOr(Configuration c) {
		for (int i=0; i<c.model.constraints.size(); i++) {
			c.model.constraints.set(i, OrExpression.mkBinExpr(c.model.constraints.get(i), Operator.OR, c.fic.getAlpha()));
		}
		return c;
	}
		
}
