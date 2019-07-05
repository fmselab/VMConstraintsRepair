package constraintsmanipulation.model;

import java.util.List;

import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.parser.ExpressionParser;
import tgtlib.definitions.expression.parser.ParseException;


/**
 * Represents a Failure Inducing Combination (ALPHA) and its type (AND o OR)
 */
public class FIC {
	
	private final Expression alpha;
	private final FICType type;
	
	/** the context of alpha: normally just the variables involved in the configuration, 
	 * but there can be more variables, i.e. when the fault has higher interaction strength */
	protected List<String> context;

	public FIC() {
		alpha=null;
		type=FICType.NONE;
	}
	
	/**
	 * Instantiates a new fault.
	 *
	 * @param alpha the failure inducing combination alpha
	 * @param the fault type
	 */
	public FIC(Expression e, FICType type) {
		this.alpha=e;
		this.type=type;
	}
	
	public FIC(String expression, FICType type) throws ParseException {
		alpha = ExpressionParser.parseAsBooleanExpression(expression, Configuration.idc);
		this.type=type;
	}

	public Expression getAlpha() {return alpha;}
	public FICType getFaultType() {return type;}
	
	@Override
	public String toString() {
		return (type == FICType.OR ? "+" : "*") + " " + alpha;
	}
	
}
