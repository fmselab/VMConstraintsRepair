package constraintsmanipulation.sat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import constraintsmanipulation.utils.ConfigurationUtils;
import constraintsmanipulation.utils.Path;
import constraintsmanipulation.utils.Util;
import constraintsmanipulation.visitor.ToSMT;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;
import tgtlib.definitions.expression.visitors.IDExprCollector;

/** To use Yices.as SMT Solver */
public class Yices implements SATSolver {

	private static boolean LOG=false;
	
	/** The Constant FOLDER. */
	private static final String FOLDER = "yices/";
	
	/** The Constant TIMEOUT. */
	private static final int TIMEOUT = 10000;
	
	private static Yices instance;
	private Yices() {}
	
	public static Yices getInstance() {resetLog(); return instance==null ? instance=new Yices() : instance;}
	
	public static Yices getInstanceWithLog() {LOG=true; return getInstance();}
	public static void resetLog() {if (LOG) LOG=false;}
	
	private Map<IdExpression,Boolean> getSAT(Expression e, boolean getModel) {
		try {
			long startingTime = Calendar.getInstance().getTimeInMillis();

			Process process = Runtime.getRuntime().exec(FOLDER + "yices-smt2" + (Path.OSValidator.isWindows()? ".exe" : ""));

			List<IdExpression> ids = IDExprCollector.getIdsAsList(e);
			// Start a stream gobbler to read the error stream.
			StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "yices-smt2", ids);
			outputGobbler.start();			

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			writer.write("(set-logic NONE)");
			//for (String s : new Formula(e.toString()).getFeatures()) {
			for (IdExpression parameter : ids) {
				writer.write("(declare-fun "+parameter.getIdString()+" () Bool)");
			}
			writer.write("(assert "+e.accept(ToSMT.getInstance())+")");
			writer.write("(check-sat) ");
			if (getModel) writer.write("(get-model)");
			//writer.write("(exit)");
			writer.close();

			if (!process.waitFor(TIMEOUT-(Calendar.getInstance().getTimeInMillis()-startingTime), TimeUnit.MILLISECONDS)) {	// wait for completion
				process.destroyForcibly();
				Util.kill(process);
				return null;
			}
			return outputGobbler.getSAT();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean isSAT(Expression e) {
		Map<IdExpression,Boolean> result = getSAT(e, false);
		return result!=null;
	}

	/** @return null if is unsat, otherwise the assignment that makes it SAT */
	@Override
	public Map<IdExpression, Boolean> getSAT(Expression e) {
		return getSAT(e,true);
	}

	
	/** Per evitare il riempimento del buffer, faccio un thread che continuamente legge l'output del processo 
	 * https://gist.github.com/szydan/2bfbf0743bbfe39b1fef e http://stackoverflow.com/questions/12258243/handle-input-using-streamgobbler */
	private class StreamGobbler extends Thread {
		
		private InputStream is;
		
		private String type;
		
		private boolean isSAT;
		
		private Map<IdExpression, Boolean> model;

		private List<IdExpression> parameters;
		
		private StreamGobbler(InputStream is, String type, List<IdExpression> parameters) {
			this.is = is;
			this.type = type;
			this.parameters = parameters;
		}
		

		@Override
		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					if (LOG) System.out.println(type + "> " + line);
					if (line.equals("sat")) {isSAT=true; model=new HashMap<>();}
					if (isSAT && line.startsWith("(= ")) {
						line = line.substring(3);
						model.put(ConfigurationUtils.getIdExpressionFromString(line.substring(0, line.indexOf(' ')), parameters), line.substring(line.indexOf(' ')+1).startsWith("true"));
					}
				}
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		public Map<IdExpression,Boolean> getSAT() {
			return model;
		}
	}

}
