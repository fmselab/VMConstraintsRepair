package constraintsmanipulation.sat;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import constraintsmanipulation.utils.Util;

/**
 * The Class PicoSAT.
 */
public class PicoSAT {
	
	/** The Constant FOLDER. */
	public static final String FOLDER = "picosat/";
	
	/**
	 * Compute MUS.
	 *
	 * @param dimacs the dimacs
	 * @return the index of the clauses that are part of the MUS
	 */
	public static List<Integer> computeMUS(String dimacs) {
		List<Integer> list = new ArrayList<>();
		try {
			int timeout = 5000;
			long startingTime = Calendar.getInstance().getTimeInMillis();
			
			Process process = Runtime.getRuntime().exec(FOLDER + "picomus - -");;
			
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			writer.write(dimacs, 0, dimacs.length());
			writer.close();
			if (!process.waitFor(timeout-(Calendar.getInstance().getTimeInMillis()-startingTime), TimeUnit.MILLISECONDS)) {	// wait for completion
				process.destroyForcibly();
				Util.kill(process);
				return list;
			}
			BufferedInputStream in = new BufferedInputStream(process.getInputStream());
			byte[] bytes = new byte[4096];
			StringBuilder sb = new StringBuilder();
			while (in.read(bytes) != -1) {
				sb.append(new String(bytes, StandardCharsets.UTF_8));
			}
			String s = sb.toString();
			for (String line : s.split("\n")) if (line.startsWith("v ") && !line.startsWith("v 0")) list.add(Integer.parseInt(line.split(" ")[1])-1);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}
	
}
