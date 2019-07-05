package constraintsmanipulation.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import constraintsmanipulation.visitor.FeatureCounter;
import constraintsmanipulation.visitor.LiteralCounter;
import constraintsmanipulation.visitor.OperatorCounter;
import tgtlib.definitions.expression.AndExpression;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.Operator;

/** Funzioni statiche di utilit� varie
 * 
 * @author Marco
 *
 */
public class Util {
	
	/** Fa l'AND tra due espressioni in modo sicuro.
	 * Serve ad evitare errore quando uno dei due termini � null:
	 * java.lang.AssertionError
		at tgtlib.definitions.expression.BinaryExpression.mkBinExpr(BinaryExpression.java:141)
		*/
	public static Expression mkExpr(Expression a, Operator op, Expression b) {
		if (a==null) return b;
		if (b==null) return a;
		return AndExpression.mkBinExpr(a, op, b);
	}
	
	public static void printToFile(File file, String content) {
		try {
			PrintWriter fout = new PrintWriter(new FileWriter(file));
			fout.println(content);
			fout.close();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public static String loadFromFile(File file) {
		try {
			BufferedReader fin = new BufferedReader(new FileReader(file));
			StringBuilder res = new StringBuilder();
			String s = null;
			while ((s=fin.readLine())!=null) res.append(s+"\n");
			fin.close();
			return res.toString();
		} catch (Exception e) {e.printStackTrace(); return null;}
	}
	
	/**
	 * @return if e1 is simpler than e2
	 */
	public static boolean isSimplerThan(Expression e1, Expression e2) {
		if (e1==null) return true;
		if (e2==null) return false;
		return (e1.accept(new FeatureCounter())<=e2.accept(new FeatureCounter()) && e1.accept(LiteralCounter.instance)<=e2.accept(LiteralCounter.instance) && e1.accept(OperatorCounter.instance)<=e2.accept(OperatorCounter.instance)); // && (this.getNumChar()<=f.getNumChar() || this.getNumOperators()<=f.getNumOperators());
		/*int f1 = e1.accept(new FeatureCounter()),
			f2 = e2.accept(new FeatureCounter()),
			l1 = e1.accept(LiteralCounter.instance),
			l2 = e2.accept(LiteralCounter.instance);
		return (f1<f2 || (f1<=f2 && l1<l2) || (f1<=f2 && l1<=l2 && e1.accept(OperatorCounter.instance)<e2.accept(OperatorCounter.instance))); // && (this.getNumChar()<=f.getNumChar() || this.getNumOperators()<=f.getNumOperators());*/
	}
	
	/**
	 *  Not necessarily needed, but better since some processes keep running after.
	 *
	 * @param proc the proc
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws IOException 
	 */
	public static void kill(Process proc) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
		long pid = getProcessID(proc);
		Runtime.getRuntime().exec("kill -9 " +pid);
	}
	
	/** Kills a process by its name. ONLY FOR WINDOWS: it AUTOMATICALLY adds a .exe EXTENSION
	 * Use with caution
	 * @param serviceName the process name (e.g. qm)
	 * @throws IOException
	 */
	public static void kill(String serviceName) throws IOException {
		System.out.println("KILLING "+serviceName);
		if (Path.OSValidator.isWindows()) {
			Runtime.getRuntime().exec("taskkill /F /IM " + serviceName + ".exe");
		} else {
			Runtime.getRuntime().exec("killall" + serviceName);
		}
	}
	
	/** See: {@link http://stackoverflow.com/questions/4750470/how-to-get-pid-of-process-ive-just-started-within-java-program}
	 * Only works for Win32 and Mac/Linux. NOT Win64: waiting for Java 9 */
	public static long getProcessID(Process p) {
		long result = -1;
		try {
			// for windows
			if (p.getClass().getName().equals("java.lang.Win32Process") || p.getClass().getName().equals("java.lang.ProcessImpl")) {
				//FIXME to solve with Java9 or with library JNA for Win32
				/*Field f = p.getClass().getDeclaredField("handle");
				f.setAccessible(true);
				long handl = f.getLong(p);
				Kernel32 kernel = Kernel32.INSTANCE;
				WinNT.HANDLE hand = new WinNT.HANDLE();
				hand.setPointer(Pointer.createConstant(handl));
				result = kernel.GetProcessId(hand);
				f.setAccessible(false);*/
			}
			// for unix based operating systems
			else if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
				Field f = p.getClass().getDeclaredField("pid");
				f.setAccessible(true);
				result = f.getLong(p);
				f.setAccessible(false);
			}
		} catch (Exception ex) {
			result = -1;
		}
		return result;
	}
	
	
}
