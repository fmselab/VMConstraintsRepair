package constraintsmanipulation.distance;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import constraintsmanipulation.visitor.ToTreeRTED;
import tgtlib.definitions.expression.Expression;

/**
 * The Class SyntaxTreeDistance.
 *
 * @author Marco
 */
public class SyntaxTreeDistance extends DistanceCriterion {
	
	/** The Constant TIMEOUT. */
	protected static final int TIMEOUT = 5000;
	
	/** The has timed out. */
	public static boolean hasTimedOut=false;
	
	/**
	 * Instantiates a new syntax tree distance.
	 */
	private SyntaxTreeDistance() {}
	
	/** The instance. */
	private static SyntaxTreeDistance instance;
	
	/**
	 * Gets the single instance of SyntaxTreeDistance.
	 *
	 * @return single instance of SyntaxTreeDistance
	 */
	public static SyntaxTreeDistance getInstance() {
		return instance==null ? instance=new SyntaxTreeDistance() : instance;
	}
	
	/** The cd. */
	protected double CD = 1;
	
	/** The ci. */
	protected double CI = 1;
	
	/** The cr. */
	protected double CR = 2;
	
	
	/**
	 * Gets the distance.
	 *
	 * @param f1 the f 1
	 * @param f2 the f 2
	 * @return the Levenshtein distance between the syntactic tree of the two expression. If a parameter is null, an empty tree is taken for comparison.
	 */
	public double getDistance(Expression f1, Expression f2) {
		if (f1!=null && f2!=null && f1.equals(f2)) return 0;
		String s1 = f1==null ? "{}" : f1.accept(new ToTreeRTED()), s2 = f2==null ? "{}" : f2.accept(new ToTreeRTED());
		if (s2==null || s2.trim().equals("")) s2="{}";
		System.out.println(s1+ " " +s2);
		try {
			Process process = Runtime.getRuntime().exec("java -jar libs/RTED_v1.2.jar -t \""+s1+"\" \""+s2+"\" -c "+CD+" "+CI+" "+CR);
			// wait for completion
			if (!process.waitFor(TIMEOUT, TimeUnit.MILLISECONDS)) {
				process.destroyForcibly();
				hasTimedOut=true;
				return -1;
			}
			Scanner sc = new Scanner(process.getInputStream());
			//String s="";
			//while ((s=sc.next())!=null) System.out.println(s);
			double d = Double.parseDouble(sc.next());
			sc.close();
			return d;
		} catch (Exception e) {e.printStackTrace();}
		return -1;
	}
	
	@Override
	public String getName() {return "BFED";}
}
