package constraintsmanipulation.simplifier;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.RuleSet;

import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.Formula;
import constraintsmanipulation.visitor.ToStandardSymbols;
import tgtlib.definitions.expression.parser.ExpressionParser;

/**
 * The Class JBoolSimplifier.
 *
 * @author Marco
 */
public class JBoolSimplifier extends Simplifier {

	/** The instance. */
	private static JBoolSimplifier instance;
	
	/**
	 * Instantiates a new j bool simplifier.
	 */
	private JBoolSimplifier() {}
	
	/**
	 * Gets the single instance of JBoolSimplifier.
	 *
	 * @return single instance of JBoolSimplifier
	 */
	public static JBoolSimplifier getInstance() {return instance==null ? instance=new JBoolSimplifier() : instance;}
	
	/* (non-Javadoc)
	 * @see constraintsmanipulation.simplifier.Simplifier#getName()
	 */
	@Override
	public String getName() {
		return "JBool";
	}

	/* (non-Javadoc)
	 * @see constraintsmanipulation.simplifier.Simplifier#simplify(tgtlib.definitions.expression.Expression)
	 */
	@Override
	public tgtlib.definitions.expression.Expression simplify(tgtlib.definitions.expression.Expression f) {
		try {
			String printed = f.accept(tgtlib.definitions.expression.visitors.ImpliesRemover.instance).accept(new ToStandardSymbols());
			if (LOG) System.out.println(printed);
			String simplified = executeJBoolSimplifier(printed);
			if (LOG) System.out.println(simplified);
			return ExpressionParser.parseAsBooleanExpression(simplified, Configuration.idc);
		} catch (Exception e) {e.printStackTrace(); return null;}
	}
	
	/**
	 * Execute J bool simplifier.
	 *
	 * @param s the s
	 * @return the simplified expressions equivalent to s
	 */
	public static String executeJBoolSimplifier(String s) {
		try {
			if (s.contains("!!")) s=s.replace("!!", "");
			Expression<String> expr = ExprParser.parse(s);
			String t = RuleSet.simplify(expr).toString();
			Formula simpl = new Formula(t);
			Formula s2 = new Formula(RuleSet.toCNF(expr).toString());  // sometimes, converting to CNF does more simplification than the "simplify" method
			// removed because it is quite slow (si bloccava sul Register nell'esperimento 2)
			//Formula s3 = new Formula(RuleSet.toDNF(expr).toString());
			if (s2.isSimplerThan(simpl)) simpl = s2;
			//if (s3.isSimplerThan(s2)) simpl = s3;
			return simpl.toString();
		} catch (Exception e) {
			System.err.println("JBOOL INPUT CAUSING PROBLEM: "+s);
			e.printStackTrace(); 
			return s;
		}
	}

}
