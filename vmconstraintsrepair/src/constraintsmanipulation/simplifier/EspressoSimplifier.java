package constraintsmanipulation.simplifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import constraintsmanipulation.utils.ConfigurationUtils;
import constraintsmanipulation.utils.Path;
import constraintsmanipulation.visitor.ToStandardSymbols;
import tgtlib.definitions.expression.Expression;

/**
 * The Class EspressoSimplifier.
 *
 * @author Marco
 */
public class EspressoSimplifier extends Simplifier {
	
	/** The instance. */
	private static EspressoSimplifier instance;
	
	/**
	 * Instantiates a new espresso simplifier.
	 */
	private EspressoSimplifier() {}
	
	/**
	 * Gets the single instance of EspressoSimplifier.
	 *
	 * @return single instance of EspressoSimplifier
	 */
	public static EspressoSimplifier getInstance() {return instance==null ? instance=new EspressoSimplifier() : instance;}
	
	/* (non-Javadoc)
	 * @see constraintsmanipulation.simplifier.Simplifier#getName()
	 */
	@Override
	public String getName() {
		return "Espresso";
	}

	/* (non-Javadoc)
	 * @see constraintsmanipulation.simplifier.Simplifier#simplify(tgtlib.definitions.expression.Expression)
	 * XXX: if it ever returns null, it is a FAULTY version
	 */
	@Override
	public tgtlib.definitions.expression.Expression simplify(tgtlib.definitions.expression.Expression f) {
		try {
			String printed = f.accept(tgtlib.definitions.expression.visitors.ImpliesRemover.instance).accept(new ToStandardSymbols());
			if (LOG) System.out.println(printed);
			return executeEspressoSimplifier(printed);
		} catch (Exception e) {e.printStackTrace(); return f;}
	}
	
	/**
	 * Execute espresso simplifier.
	 *
	 * @param s the s
	 * @return the simplified expressions equivalent to s, calling PyEda espresso.
	 * ATTENTION: Python 3 is needed to be installed in the system, and added to PATH to make it callable as "python"
	 */
	public static Expression executeEspressoSimplifier(String s) throws Exception {
		int timeout = 5000;
		long startingTime = Calendar.getInstance().getTimeInMillis();
		String t = s.replace("!","~");
		String script = "espresso.py";
		if (LOG) System.out.println("Simplifying "+t+" with "+script);
		
		//ProcessBuilder pb = new ProcessBuilder(new String[] {Path.getPython3(),script,t});
		ProcessBuilder pb = new ProcessBuilder(new String[] {Path.getPython3(),script});
		pb.directory(new File("pyeda"));
		Process process = pb.start();
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()), 4096);
		writer.write(t);
		//writer.write(t, 0, t.length());
		try {writer.close();} catch (IOException e) {System.err.println("Espresso: "+e.getMessage());}
		
		if (!process.waitFor(timeout-(Calendar.getInstance().getTimeInMillis()-startingTime), TimeUnit.MILLISECONDS)) {	// wait for completion
			process.destroyForcibly();
			System.out.println("Espresso Timed Out");
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
		while ((t=in.readLine())!=null) s=t; //System.out.println(s=t);
		//String s = "Or(!A, And(B,C))";
		Expression simplified = ConfigurationUtils.parseExpression(s);
		//String simplified = new Formula(Formula.convertFromPreNotation(s)).flatten();
		//System.out.println("Simplified formula: "+simplified);
		
		return simplified;
	}

}
