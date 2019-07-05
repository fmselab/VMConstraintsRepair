package constraintsmanipulation.simplifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.utils.Path;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.normalform.cnf.CNFExprConverterNaive;
import tgtlib.definitions.normalform.cnf.CNFExpression;

/**
 * The Class KmkeenCNFSimplifier.
 *
 * @author Marco
 */
public class KmkeenCNFSimplifier extends Simplifier {

	/** The instance. */
	private static KmkeenCNFSimplifier instance;
	
	/**
	 * Instantiates a new kmkeen CNF simplifier.
	 */
	private KmkeenCNFSimplifier() {}
	
	/**
	 * Gets the single instance of KmkeenCNFSimplifier.
	 *
	 * @return single instance of KmkeenCNFSimplifier
	 */
	public static KmkeenCNFSimplifier getInstance() {return instance==null ? instance=new KmkeenCNFSimplifier() : instance;}
	
	/* (non-Javadoc)
	 * @see constraintsmanipulation.simplifier.Simplifier#getName()
	 */
	@Override
	public String getName() {
		return "kmkeencnf";
	}

	/* (non-Javadoc)
	 * @see constraintsmanipulation.simplifier.Simplifier#simplify(tgtlib.definitions.expression.Expression)
	 */
	@Override
	public tgtlib.definitions.expression.Expression simplify(tgtlib.definitions.expression.Expression f) {
		try {
			return executeKmkeenCNFSimplifier(f);
		} catch (Exception e) {e.printStackTrace(); return null;}
	}
	
	/** The Constant FOLDER. */
	private static final String FOLDER = "kmkeen_simplifycnf";
	
	/**
	 * Execute kmkeen CNF simplifier.
	 *
	 * @param e the e
	 * @return the simplified expressions equivalent to s, calling Kmkeen CNF simplifier. Python needed.
	 * Creates a temporary DIMACS-ish file to give as input to the simplifier program (without the first line)
	 */
	public static Expression executeKmkeenCNFSimplifier(Expression e) {
		try {
			int timeout = 30000;
			long startingTime = Calendar.getInstance().getTimeInMillis();
			// Convert to CNF with ATGT and print the DIMACS
			CNFExpression cnf = CNFExprConverterNaive.instance.getCNFExprConverter().getCNF(e);
			String t = cnf.toDimacs().toString();
			t = t.substring(t.indexOf("\n")+1);
			
			PrintWriter fout = new PrintWriter(new FileWriter(FOLDER+"/temp_input.cnf"));
			fout.println(t);
			fout.close();
			
			if (LOG) System.out.println(t);
			String script = "simplify.py";
			if (LOG) System.out.println("Simplifying "+t+" with "+script);
			
			ProcessBuilder pb = new ProcessBuilder(new String[] {Path.getPython3(),script, "temp_input.cnf"});
			pb.directory(new File(FOLDER));
			Process process = pb.start();
			if (!process.waitFor(timeout-(Calendar.getInstance().getTimeInMillis()-startingTime), TimeUnit.MILLISECONDS)) {	// wait for completion
				process.destroyForcibly();
				System.out.println("Timed Out");
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String s="";
			while ((t=in.readLine())!=null) {if (LOG) System.out.println(s=t); else s=t;}
			//String s = "Or(!A, And(B,C))";
			Expression simplified = Configuration.parseDimacs(s, cnf.getLiterals());//IDExprCollector.getIdsAsList(e));
			
			//String simplified = new Formula(Formula.convertFromPreNotation(s)).flatten();
			if (LOG) System.out.println("Simplified formula: "+simplified);
			
			return simplified;
		} catch (Exception ex) {ex.printStackTrace(); return null;}
	}

}
