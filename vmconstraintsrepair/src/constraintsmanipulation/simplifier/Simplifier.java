package constraintsmanipulation.simplifier;

import tgtlib.definitions.expression.Expression;

/**
 * The Class Simplifier.
 */
public abstract class Simplifier {
	
	/** The log. */
	public static boolean LOG=false;
	
	int timeout=5000;
	
	protected boolean hasTimedOut;
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {return "AsIsSimplifier";}
	
	/**
	 * Simplify.
	 *
	 * @param f the f
	 * @return the expression
	 */
	public Expression simplify(Expression f) {return f;}
	
	public Simplifier setTimeout(int timeout) {this.timeout=timeout; return this;}
	public int getTimeout() {return timeout;}
	
	public boolean hasTimedOut() {return hasTimedOut;}
}
