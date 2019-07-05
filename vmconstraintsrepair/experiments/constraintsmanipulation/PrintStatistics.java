package constraintsmanipulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import constraintsmanipulation.distance.BFCD;
import constraintsmanipulation.distance.BFED;
import constraintsmanipulation.manipulator.Manipulators;
import constraintsmanipulation.manipulator.Mutation;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.Model;

/**
 * Print statistics of models. Needs medici to compute validity ratio.
 * 
 * @author marcoradavelli
 *
 */
public class PrintStatistics {
	
	static final String SEP = " & ";
	
	@org.junit.Test
	public void printModelStats() {
		System.out.println("System & \\# var & \\# & \\#lit & size & nodes & & validity ratio \\\\");
		for (Models m : Models.values()) {
			Model model = m.loadConfiguration().model;
			BigDecimal validityRatio = new BigDecimal("0");
			//try {validityRatio = Medici.getValidityRatio(model);} catch (Exception e) {System.out.println("Error: medici not present");}
			System.out.println(
					m(m.getName())
					+m(model.parameters.size())
					+m(model.constraints.size())
					+m(model.getTotalLiterals())
					+m(model.getAverageConstraintSize())
					+m(model.getTotalNodes())
					+m(validityRatio)
					+" \\\\");
		}
	}
	
	public static Map<String, Configuration> loadMutatedConfigurations(String input) {
		Map<String,Configuration> res = new HashMap<String, Configuration>();
		String currentConfigurationName = null;
		StringBuilder sb = new StringBuilder();
		for (String s : input.split("\n")) {
			if (s!=null && !s.isEmpty() && s.startsWith("mutated_") && s.endsWith(".txt")) {
				if (currentConfigurationName!=null) res.put(currentConfigurationName, Configuration.newConfiguration(sb.toString()));
				currentConfigurationName = s;
				sb = new StringBuilder();
			} else if (s!=null && !s.isEmpty() && !s.startsWith("Alpha: ")) {
				sb.append(s+"\n");
			}
		}
		return res;
	}
	
	/** make sure that the mutated models are present, otherwise generates the missing ones */
	@org.junit.Test
	public void mutatedModelsStats() throws Exception {
		File file = new File("output/mutatedModels.txt");
		Map<String,Configuration> savedModels = new HashMap<>();
		if (file.exists()) {
			savedModels = loadMutatedConfigurations(constraintsmanipulation.utils.Util.loadFromFile(file));
		}
		PrintWriter fout = new PrintWriter(new FileWriter("output/mutatedModelsStats.csv"));
		fout.println("model,mut,features,constraints,literals");
		for (Models m : Experiment2.models) {
			for (Mutation mut : Experiment2.muts){
				//System.out.println(m+" - "+mut);
				for (int i=0; i<Experiment2.N; i++) {
					String key = "mutated_"+m+"_"+mut+"_"+i+".txt";
					Configuration c2 = null;
					if (savedModels.containsKey(key)) {
						c2 = savedModels.get(key);
					} else {
						Configuration c = m.loadConfiguration();
						c2 = mut.mutate(c);
						savedModels.put(key, c2);
					}
					Model model = c2.model;
					String out = key.split("_")[1]+","+key.split("_")[2]+","+model.parameters.size()+","+model.constraints.size()+","+model.getTotalLiterals();
					System.out.println(out);
					fout.println(out);
				}
			}
		}
		fout.close();
		// (re-)prints the model
		fout = new PrintWriter(new FileWriter(file));
		for (Entry<String,Configuration> e : savedModels.entrySet()) {
			fout.println(e.getKey());
			fout.println(e.getValue());
			fout.println();
		}
		fout.close();
	}
	
	public static String m(Number n) {
		if (n instanceof Double) n = (double)Math.round((Double)n*10)/10;
		return m(""+n);
	}
	
	public static String m(String s) {
		return SEP+"\\multirow{2}{*}{"+s+"}";
	}
	
	@org.junit.Test
	public void printExp2() throws Exception {
		try {
			Models[] models = Experiment2.models;
			for (Models m : models)  {
				String[] remember=null;
				BufferedReader fin = new BufferedReader(new FileReader("output/exp2_"+m.getName()+".csv"));
				fin.readLine();
				for (int j=0; j<Mutation.values().length; j++) {//Mutation mutation : Mutation.values()) {
					String s=null;
					for (int i=0; i<Manipulators.mans.length; i++) {
						String[] st=null;
						if (remember!=null) st=remember;
						else {
							String line = fin.readLine();
							if (line==null) st=null;
							else st = line.split(",");
						}
						if (st==null || !st[2].equals(Manipulators.mans[i].getSimplifierName())) {
							s+=" & -- & -- & -- ";
							remember=st;
						} else {
							remember=null;
							if (s==null) s=(j>0 ? " & & & " : "") + Mutation.values()[j].name()+" & "+f(st[1])+" & "+f(Double.parseDouble(st[3])/Double.parseDouble(st[1])*100)+" \\% & "+f(st[5]);
							s+=" & "+f(st[7])+" & "+f(st[6])+" & "+f(st[12]);
						}
					}
					System.out.println(s+" \\\\");
				}
				fin.close();
			}
		} catch (Exception e) {e.printStackTrace();}
	}
	
	/** fixes double representation */
	public static String f(double n) {
		//return ""+(double)Math.round(n*10)/10;
		DecimalFormatSymbols d = new DecimalFormatSymbols(); d.setDecimalSeparator('.');
		DecimalFormat f = new DecimalFormat("##0.00"); f.setDecimalFormatSymbols(d);
		//String s = f.format(n);
		//System.out.println("\ns: "+s+" N: "+n);
		return f.format(n);
	}
	
	/** fixes double number from String */
	public static String f(String s) {
		return f(Double.parseDouble(s));
	}
	
	/** fixes double representation */
	public static String i(double n) {
		return ""+Math.round(n);
	}
	
	/** fixes double number from String */
	public static String i(String s) {
		return i(Double.parseDouble(s));
	}
	
	@Test
	public void printRED2() {
		for (Models m : Models.values()) {
			try {
				File f = new File("output/exp2_models_"+m.getName()+".txt");
				if (f.exists()) {
					BufferedReader fin = new BufferedReader(new FileReader(f));
					String s=null;
					StringBuffer st = new StringBuffer();
					String manipulator="", mutation="", steps="";
					Configuration mutated = null;
					Configuration oracle = m.loadConfiguration();
					Map<String, Double> avgEDRO = new HashMap<>(), avgEDNO = new HashMap<>();
					Map<String, Double> avgCDRO = new HashMap<>(), avgCDNO = new HashMap<>();
					boolean theMutated=false;
					boolean printed=false;
					while ((s=fin.readLine())!=null) {
						if (s.startsWith("Repaired "+m+" with")) {
							manipulator = s.split("with ")[1].split(",")[0];
							mutation = s.split(" by ")[1].split(" in ")[0];
							steps = s.split(" in ")[1].split(" steps.")[0];
						} else if (s.startsWith("Mutated "+m)) {
							theMutated=true;
						} else if (!s.contains(";") && !s.trim().isEmpty()) {
							if (!s.startsWith("Alpha: ")) //s = s.replace("Alpha: ", "");
							st.append(s+"\n");
							printed=false;
						} else if (!printed) {
							//System.out.println("ST: "+st.toString()+" :ST");
							printed=true;
							Configuration c = Configuration.newConfiguration(st.toString());
							if (theMutated) {
								mutated = c;
								theMutated=false;
							} else {
								if (c==null) System.out.println("c null");
								else if (mutated==null) System.out.println("mutated null");
								else {
									double edRO = BFED.instance.computeEditDistance(c.model, mutated.model); 
									double edNO = BFED.instance.computeEditDistance(oracle.model, mutated.model);
									double cdRO = BFCD.instance.getDistance(c.model, mutated.model); 
									double cdNO = BFCD.instance.getDistance(oracle.model, mutated.model);
									String key = manipulator+" & "+mutation;
									avgEDRO.put(key, avgEDRO.getOrDefault(key, 0d)+edRO);
									avgEDNO.put(key, avgEDNO.getOrDefault(key, 0d)+edNO);
									avgCDRO.put(key, avgCDRO.getOrDefault(key, 0d)+cdRO);
									avgCDNO.put(key, avgCDNO.getOrDefault(key, 0d)+cdNO);
									/*System.out.println(
										manipulator
										+" & "+mutation
										+" & "+steps
										+" & "+edRO
										+"/"+edNO
										+" = "+ (edRO==0 && edNO==0 ? 1 : edNO / edRO)
										+" & "+cdRO
										+"/"+cdNO
										+" = "+ (cdRO==0 && cdNO==0 ? 1 : cdNO / cdRO)
									);*/
								}
							}
							st = new StringBuffer();
						}
					}
					fin.close();
					for (String key : avgCDNO.keySet()) {						
//						System.out.println(m+" & "+key+" & "+f(avgEDRO.get(key)/avgEDNO.get(key))+" & "+f(avgCDRO.get(key)/avgCDNO.get(key)));
						System.out.println(m+" & "+key+" & "+ f(avgEDRO.get(key)/10.0) +"/"+f(avgEDNO.get(key)/10.0)+" & "+f(avgCDRO.get(key)/10.0)+"/"+f(avgCDNO.get(key)/10.0));
					}
				}
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	/** prints relative edit distances for experiment 3 */
	@Test
	public void printRED3() {
		//Models[] models = Models.values();
		Models[] models = {
			Models.ERP_SPL_1, Models.ERP_SPL_2,
			//Models.WINDOWS70, Models.WINDOWS80,
			//Models.RHISCOM1, Models.RHISCOM2,
		};
		
		for (Models m : models) for (Models o : models) {
			try {
				File f = new File("output/exp3_models_"+m.getName()+"_"+o.getName()+".txt");
				if (f.exists()) {
					BufferedReader fin = new BufferedReader(new FileReader(f));
					String s=null;
					StringBuffer st = new StringBuffer();
					String manipulator="", order="";
					Configuration old = m.loadConfiguration();
					Configuration oracle = o.loadConfiguration();
					Map<String, Double> avgEDRO = new HashMap<>(), avgEDNO = new HashMap<>();
					Map<String, Double> avgCDRO = new HashMap<>(), avgCDNO = new HashMap<>();
					Map<String, Double> avgEDRN = new HashMap<>(), avgCDRN = new HashMap<>();
					boolean printed=false;
					while ((s=fin.readLine())!=null) {
						if (s.startsWith("Model "+m+" repaired ")) {
							manipulator = s.split("with ")[1].split(" & ")[0];
							order = s.split(" & ")[1].split(":")[0];
						} else if (!s.contains(";") && !s.trim().isEmpty()) {
							if (!s.startsWith("Alpha: ")) //s = s.replace("Alpha: ", "");
							st.append(s+"\n");
							printed=false;
						} else if (!printed) {
							//System.out.println("ST: "+st.toString()+" :ST");
							printed=true;
							Configuration c = Configuration.newConfiguration(st.toString());
							double edRO = BFED.instance.computeEditDistance(c.model, old.model); 
							//double edNO = BFED.instance.computeEditDistance(oracle.model, old.model);
							double cdRO = BFCD.instance.getDistance(c.model, old.model); 
							//double cdNO = BFCD.instance.getDistance(oracle.model, old.model);
							//double edRN = BFED.instance.getDistance(oracle.model, c.model);
							//double cdRN = BFCD.instance.getDistance(oracle.model, c.model);
							String key = manipulator+" & "+order;
							avgEDRO.put(key, avgEDRO.getOrDefault(key, 0d)+edRO);
							//avgEDNO.put(key, avgEDNO.getOrDefault(key, 0d)+edNO);
							avgCDRO.put(key, avgCDRO.getOrDefault(key, 0d)+cdRO);
							//avgCDNO.put(key, avgCDNO.getOrDefault(key, 0d)+cdNO);
							//avgEDRN.put(key, avgEDRN.getOrDefault(key, 0d)+edRN);
							//avgCDRN.put(key, avgCDRN.getOrDefault(key, 0d)+cdRN);
							/*System.out.println(
								manipulator
								+" & "+mutation
								+" & "+steps
								+" & "+edRO
								+"/"+edNO
								+" = "+ (edRO==0 && edNO==0 ? 1 : edNO / edRO)
								+" & "+cdRO
								+"/"+cdNO
								+" = "+ (cdRO==0 && cdNO==0 ? 1 : cdNO / cdRO)
							);*/
							st = new StringBuffer();
						}
					}
					fin.close();
					for (String key : avgCDRO.keySet()) {						
					//	oldold System.out.println(m+" & "+key+" & "+ f(avgEDRN.get(key))+" & "+f(avgCDRN.get(key))+" & "+ f(avgEDRO.get(key)/avgEDNO.get(key))+" & "+f(avgCDRO.get(key)/avgCDNO.get(key)));
					//	System.out.println(m+" & "+key+" & "+ f(avgEDRN.get(key))+" & "+f(avgCDRN.get(key))+" & "+ f(avgEDRN.get(key)) +"/"+f(avgEDNO.get(key))+" & "+f(avgCDRN.get(key))+"/"+f(avgCDNO.get(key)));
						System.out.println(m+" & "+key+" & "+ f(avgCDRO.get(key))+" & "+f(avgEDRO.get(key)));//+" & "+ f(avgEDRO.get(key)) +"/"+f(avgEDNO.get(key))+" & "+f(avgCDRO.get(key))+"/"+f(avgCDNO.get(key)));
					}
				}
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	// FOR exp3 see R script (function stats3)
	// FOR exp1 see R script (function stats1)
	/*@org.junit.Test
	public void printExp1() throws Exception {
		try {
			Models[] models = Models.values();
			for (Models m : models)  {
				String[] remember=null;
				File f = new File("output/exp1_"+m.getName()+".csv");
				if(!f.exists() || f.isDirectory()) continue;
				BufferedReader fin = new BufferedReader(new FileReader(f));
				fin.readLine();
				Map<Manipulator,double[][]> sum = new HashMap<>();
				for (int j=0; j<10; j++) { // the number of times
					String s=null;
					for (int i=0; i<Manipulators.mans.length; i++) {
						String[] st=null;
						if (remember!=null) st=remember;
						else {
							String line = fin.readLine();
							if (line==null) st=null;
							else st = line.split(",");
						}
						if (st==null || !st[2].equals(Manipulators.mans[i].getSimplifierName())) {
							s+=" & -- & -- & -- ";
							remember=st;
						} else {
							remember=null;
							if (s==null) s=(j>0 ? " & & & " : "") + Mutation.values()[j].name()+" & "+f(st[1])+" & "+f(Double.parseDouble(st[3])/Double.parseDouble(st[1])*100)+" \\% & "+f(st[5]);
							s+=" & "+f(st[7])+" & "+f(st[6])+" & "+f(st[12]);
						}
					}
					System.out.println(s+" \\\\");
				}
				fin.close();
				String s="";
				for (Entry<Manipulator, double[][]> e : sum.entrySet()) {
					s+=" & "+f(st[7])+" & "+f(st[6])+" & "+f(st[12]);
				}
			}
		} catch (Exception e) {e.printStackTrace();}
	}*/
}
