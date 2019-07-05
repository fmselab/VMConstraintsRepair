package constraintsmanipulation.simplifier;

import tgtlib.definitions.expression.Expression;

/**
 * The Class BestSimplifier.
 *
 * @author Marco
 */
public class NoneSimplifier extends Simplifier {
	
	private static NoneSimplifier instance;

	private NoneSimplifier() {}
	
	public static NoneSimplifier getInstance() {return instance==null ? instance=new NoneSimplifier() : instance;}
	
	@Override
	public String getName() {
		return "onlySelection";
	}

	@Override
	public Expression simplify(Expression f) {
		return f;
	}
}
