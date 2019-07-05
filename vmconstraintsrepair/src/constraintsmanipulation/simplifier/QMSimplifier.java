package constraintsmanipulation.simplifier;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.bpodgursky.jbool_expressions.parsers.ExprParser;
import com.bpodgursky.jbool_expressions.rules.RuleSet;

import constraintsmanipulation.model.Formula;
import constraintsmanipulation.utils.ConfigurationUtils;
import constraintsmanipulation.utils.Util;
import constraintsmanipulation.visitor.ToStandardSymbols;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.visitors.IDExprCollector;

/**
 * The Class QMSimplifier.
 *
 * @author Marco
 */
public class QMSimplifier extends Simplifier {
	
	/** The instance. */
	private static QMSimplifier instance;
	
	/**
	 * Instantiates a new QM simplifier.
	 */
	private QMSimplifier() {}
	
	/**
	 * Gets the single instance of QMSimplifier.
	 *
	 * @return single instance of QMSimplifier
	 */
	public static QMSimplifier getInstance() {return instance==null ? instance=new QMSimplifier() : instance;}
	
	/** The Constant QM_EXECUTABLE_FOLDER. */
	private static final String QM_EXECUTABLE_FOLDER = "qm/";

	/* (non-Javadoc)
	 * @see constraintsmanipulation.simplifier.Simplifier#getName()
	 */
	@Override
	public String getName() {
		return "QM";
	}

	/* (non-Javadoc)
	 * @see constraintsmanipulation.simplifier.Simplifier#simplify(tgtlib.definitions.expression.Expression)
	 */
	@Override
	public Expression simplify(Expression e) {
		try {
			if (e.accept(IDExprCollector.instance).size()>20) return e; // maximum number of features correctly handled in a reasonable amount of time
			if (LOG) System.out.println(e);
			return executeQMSimplifier(e);
		} catch (Exception ex) {ex.printStackTrace(); return null;}
	}
	
	/**
	 * Execute QM simplifier.
	 *
	 * @param s the s
	 * @return the simplified expressions equivalent to s, calling Quine-McCluskey simplification method.
	 * ATTENTION: The qm executable should be compiled and place in the "qm" folder
	 */
	public Expression executeQMSimplifier(Expression partenza) {
		if (LOG) System.out.println(partenza);
		String s="";
		try {
			long startingTime = Calendar.getInstance().getTimeInMillis();
			s = partenza.accept(tgtlib.definitions.expression.visitors.ImpliesRemover.instance).accept(new ToStandardSymbols());
			
			// Convert to DNF with ATGT: ATGT IS TOO SLOW IN DNF CONVERSION, but we still use it because otherwise JBool also simplifies diring the conversion
			//Expression e = Configuration.parseExpression(s);
			//DNFExpression dnf = DNFExprConverter.getDNF(partenza); // with the current library does by-product printing in the Systemout
			//Formula f = new Formula(dnf.toString(true)); 
			
			// Convert to DNF with JBool: it already performs internal simplifications
			while (s.contains("!!")) s=s.replace("!!", "");
			com.bpodgursky.jbool_expressions.Expression<String> dnf = RuleSet.toDNF(ExprParser.parse(s));
			Formula f = new Formula(dnf.toString());

			if (LOG) System.out.println(f.toString());
			if (new Formula(s).getNumFeatures()> 'Z'-'A') return partenza; //the number of letters is not enough
			//String inputExpression = "f(A, B, C, D) = ~AB~C + A~B~C + AB~C~D + A~BC~D + BC~D + ~ABCD + A~BCD + ~A~BD";
			String inputExpression = "f(" + f.getFeatureNamesAsSingleLetters(", ") + ") = " + f.codeStringForQuineMcCluskey()+"\n";
			if (LOG) System.out.println(inputExpression);
			// Example from: http://dhruba.name/2012/10/16/java-pitfall-how-to-prevent-runtime-getruntime-exec-from-hanging/
			// and from: http://stackoverflow.com/questions/7456613/process-runtime-pass-input
			Process process = Runtime.getRuntime().exec(QM_EXECUTABLE_FOLDER + "qm -q");
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			writer.write(inputExpression, 0, inputExpression.length());
			writer.close();
			if (!process.waitFor(timeout-(Calendar.getInstance().getTimeInMillis()-startingTime), TimeUnit.MILLISECONDS)) {	// wait for completion
				process.destroyForcibly();
				try {Util.kill("qm");} catch (Exception ex) {System.err.println("Error in killing QM");}
				return partenza;
			}
			BufferedInputStream in = new BufferedInputStream(process.getInputStream());
			byte[] bytes = new byte[4096];
			StringBuilder sb = new StringBuilder();
			while (in.read(bytes) != -1) {
				sb.append(new String(bytes, StandardCharsets.UTF_8));
			}
			s = sb.toString().replace("\0","");
			s=s.substring(s.lastIndexOf('=')+1);
			s = f.decodeStringFromMcCluskey(s);
			if (LOG) System.out.println("QM: "+s);
			//if (s!=null && s.startsWith("()")) s="";
			return ConfigurationUtils.parseExpression(s);
			//return ExpressionParser.parseAsBooleanExpression(s, Configuration.idc);
		} catch (Exception ex) {
			System.err.println(s+"Errore in QM"); // output vuoto (probabilmente espressione semplificata in sempre vera)
			ex.printStackTrace();
			return partenza; //return null;
		}
	}

}
