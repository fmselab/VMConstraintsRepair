package constraintsmanipulation.utils;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import constraintsmanipulation.model.Model;
import constraintsmanipulation.visitor.ToMedici;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.visitors.ImpliesRemover;

public class Medici {
	
	private Medici() {}
	
	static final protected Logger LOG = Logger.getLogger(Medici.class);
	
	/** The Constant FOLDER. */
	private static final String FOLDER = "medici/";
	
	private static final String TEMP_FILE = "temp.medici";
	
	/** The Constant TIMEOUT. */
	private static final int TIMEOUT = 120000;
	
	
	public static String toMedici(Model m) {
		StringBuilder sb = new StringBuilder();
		sb.append("2\n"); //t-wise, for us now does not matter
		sb.append(m.parameters.size()+"\n");
		for (int i=0; i<m.parameters.size(); i++) sb.append("2 ");
		sb.append("\n");
		if (m.constraints!=null && m.constraints.size()>0) {
			sb.append(m.constraints.size()+"\n");
			ToMedici visitor = new ToMedici(m.parameters);
			for (Expression e : m.constraints) sb.append(e.accept(ImpliesRemover.instance).accept(visitor)+"\n");
		}
		return sb.toString();
	}
	
	public static BigDecimal getCardinality(Model m) {
		try {
			long startingTime = Calendar.getInstance().getTimeInMillis();
			
			PrintWriter fout = new PrintWriter(new FileWriter(TEMP_FILE));
			fout.println(toMedici(m));
			fout.close();
			
			String cmd = FOLDER + "medici" + (Path.OSValidator.isWindows()? ".exe" : "") + " --donotgenerate --m " + TEMP_FILE;
			if (Path.OSValidator.isWindows()) cmd = cmd.replace("/", "\\");
			Process process = Runtime.getRuntime().exec(cmd);
			StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream());
			outputGobbler.start();
			
			if (!process.waitFor(TIMEOUT-(Calendar.getInstance().getTimeInMillis()-startingTime), TimeUnit.MILLISECONDS)) {	// wait for completion
				process.destroyForcibly();
				Util.kill(process);
				return null;
			}
			LOG.debug(process.exitValue());
			
			return outputGobbler.getCardinality();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	
	/** Per evitare il riempimento del buffer, faccio un thread che continuamente legge l'output del processo 
	 * https://gist.github.com/szydan/2bfbf0743bbfe39b1fef e http://stackoverflow.com/questions/12258243/handle-input-using-streamgobbler */
	private static class StreamGobbler extends Thread {
		
		private InputStream is;
		
		public final StringBuilder result;
		
		private BigDecimal cardinality;
		
		private StreamGobbler(InputStream is) {
			this.is = is;
			result = new StringBuilder();
		}
	
		@Override
		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				
				String line = null;
				while ((line = br.readLine()) != null) {
					LOG.debug(line);
					result.append(line);
					if (line.startsWith("Cardinalita finale ")) cardinality = new BigDecimal(Double.parseDouble(line.substring(line.lastIndexOf(' ')+1)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public BigDecimal getCardinality() {return cardinality;}
	}
	
	public static BigDecimal getValidityRatio(Model model) {
		BigDecimal cardinality = getCardinality(model);
		if (cardinality==null) return null;
		return cardinality.divide(new BigDecimal(new BigInteger("2").pow(model.parameters.size())), 4, BigDecimal.ROUND_HALF_UP);
	}
}
