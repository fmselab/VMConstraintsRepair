package constraintsmanipulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;

import constraintsmanipulation.Stats.Record;
import constraintsmanipulation.distance.DistanceCriterion;
import constraintsmanipulation.manipulator.Manipulator;
import constraintsmanipulation.manipulator.ManipulatorSAS2;
import constraintsmanipulation.manipulator.Manipulators;
import constraintsmanipulation.model.Combination;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.FIC;
import constraintsmanipulation.model.FICType;
import constraintsmanipulation.sat.FindFIC;
import constraintsmanipulation.sat.SATUtils;
import constraintsmanipulation.sat.YicesJNA;
import constraintsmanipulation.utils.ConfigurationUtils;
import constraintsmanipulation.visitor.ParameterRemover;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;

/**
 * Experiment3: repair of a model against its oracle.
 * @author marcoradavelli
 *
 */
public class Experiment3 {
	
	static final private Logger LOG = Logger.getLogger(Experiment3.class);
	static final private String OUTPUT = "output/exp3_"; // The location and first part of filename
	static final private String EXT = ".csv"; // The file extension
	static public PrintWriter flog;
	
	@BeforeClass
	public static void setup(){
		Logger.getLogger(Experiment3.class).setLevel(Level.DEBUG);
		//Logger.getLogger(Experiment2.class).setLevel(Level.DEBUG);
		//Logger.getLogger(YicesJNA.class).setLevel(Level.DEBUG);
		//Logger.getLogger(FindFIC.class).setLevel(Level.DEBUG);
		Logger.getLogger(DistanceCriterion.class).setLevel(Level.DEBUG);
		Logger.getLogger(ManipulatorSAS2.class).setLevel(Level.DEBUG);
	}
	
	/*@org.junit.Test
	public void testToybox() {
		//performExperiment2(Configuration.newConfigurationFromFile(new File("data/example/register.txt")), new File("data/example/stats2_register.csv"));
		performExperiment3(Models.TOYBOX.loadConfiguration(), Models.TOYBOX2.loadConfiguration(), new File(OUTPUT + Models.TOYBOX.getName() + EXT));
	}*/

	@org.junit.Test
	public void testRegister() {exp3(Models.REGISTER, Models.REGISTER2);}
	
	/** To be run with -Xss2m option for Java VM, to avoid StackOverflow Exception in the visitors */
	@org.junit.Test
	public void testFreeBSD() {exp3(Models.FREEBSD, Models.FREEBSD2);}
	
	/** To be run with -Xss2m option for Java VM, to avoid StackOverflow Exception in the visitors */
	@org.junit.Test
	public void testLinux() {exp3(Models.LINUX32, Models.LINUX33);}
	
	/** To be run with -Xss64m option for Java VM, to avoid StackOverflow Exception in the visitors */
	@org.junit.Test
	public void testLinuxReduced() {exp3(Models.LINUX32_REDUCED, Models.LINUX33_REDUCED);}

	/** To be run with -Xss64m option for Java VM, to avoid StackOverflow Exception in the visitors */
	@org.junit.Test
	public void testRhiscom() {exp3(Models.RHISCOM1, Models.RHISCOM2);}
	
	@org.junit.Test
	public void testEcos() {exp3(Models.ECOS2, Models.ECOS1);}
	
	@org.junit.Test
	public void testWindows() {exp3(Models.WINDOWS80, Models.WINDOWS70);}

	static Models[][] models = {
			//{Models.RHISCOM1, Models.RHISCOM2},
			//{Models.WINDOWS80, Models.WINDOWS70},
			{Models.ERP_SPL_1, Models.ERP_SPL_2},
	};
	
	@org.junit.Test
	public void execExp3() {
		for (int i=0; i<models.length; i++) exp3(models[i][0], models[i][1]);
	}
	
	public void exp3(Models model, Models oracle) {
		Manipulator[] mans = {
				Manipulators.NAIVE.getManipulator(),
				Manipulators.ONLY_SELECTION.getManipulator(),
				//Manipulators.JBOOL.getManipulator(),
				Manipulators.ESPRESSO.getManipulator(),
		};
		FICType[] repairTypes = {
				FICType.AND,
				//FICType.OR,
				//null,
		};
		try {
			flog = new PrintWriter(new FileWriter("output/exp3_log.txt"));
			Map<String,Stats> stats = new HashMap<>();
			for (FICType initRepairType : repairTypes) {try {
				for (Manipulator m : mans) {
					stats.put(m+" & "+toString(initRepairType),performExperiment3(model.loadConfiguration(), oracle.loadConfiguration(), m, initRepairType));
				}
				Writer fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(OUTPUT + model.getName() + "_" + toString(initRepairType) + EXT)), "UTF-8"));
				fout.write(Stats.HEADER+"\n");
				for (Entry<String,Stats> s : stats.entrySet()) if (s.getKey().contains(" & "+toString(initRepairType))) fout.write(s.getValue().toString());
				fout.close();
			} catch (Exception e) {e.printStackTrace();}}
			PrintWriter fmodels = new PrintWriter(new FileWriter("output/exp3_models_"+model.getName()+"_"+oracle.getName()+".txt"));
			for (Entry<String, Stats> s : stats.entrySet()) {
				fmodels.println("Model "+model.getName()+" repaired into "+oracle.getName()+" with "+s.getKey()+":\n"+s.getValue().repairedModel+"\n");
			}
			fmodels.close();
		} catch (Exception e) {e.printStackTrace();}
		flog.close();
	}
	
	public static String toString(FICType initRepairType) {
		return (initRepairType==null?"rand":(initRepairType==FICType.AND?"andor":"orand"));
	}
	
	/**
	 * Perform experiment 3
	 * Warning: it modifies the model
	 * @param model the configuration to which apply the modifications
	 * @param oracle the oracle (not modified)
	 */
	public static Stats performExperiment3(final Configuration oracle, final Configuration model, final Manipulator manipulator, final FICType initRepairType) {
		Stats stat = new Stats();
		try {
			//PrintWriter fout = new PrintWriter(new FileWriter("output/exp3_onlySelec_windows.csv"));
			PrintWriter fout=null;
			Set<IdExpression> removedParameters = ConfigurationUtils.getRemovedParameters(model.model.parameters, oracle.model.parameters);
			//model.model.parameters=commonParameters;
			if (!removedParameters.isEmpty()) {
				LOG.debug("Removed "+removedParameters.size()+" parameters");
				removedParameters.forEach(e -> System.out.println(e));
				ParameterRemover visitor = new ParameterRemover(removedParameters);
				for (int i=0; i<model.model.constraints.size(); i++) {
					if (model.model.constraints.get(i).accept(visitor)==null) {
						LOG.debug("Rimosso");
						model.model.constraints.remove(i--);
					}
				}
				model.model.computeParametersFromConstraints();
				LOG.debug("Model:\n"+model);
			}
			YicesJNA yices = new YicesJNA(oracle.model.parameters); //YicesJNA yices = new YicesJNA(commonParameters);
			model.setYices(yices);
			oracle.setYices(yices);
			
			Configuration c2 = model.clone();

			int step=0;
			FICType faultRimasto=null;
			boolean continua = true;
			boolean checking=false; // it has to make sure that no faults of the other type still exist, and double-check that the repair model is equivalent
			FICType type = (initRepairType==null ? FICType.getRandomFault() : initRepairType);//FICType.getRandomFault();
			Combination test = getMinimalFIC(type, c2, oracle, yices);
			Expression alpha = test.toExpr();

			while (continua) {
				//type = faultRimasto!=null ? faultRimasto : (initRepairType==null ? FICType.getRandomFault() : initRepairType);
				if (alpha!=null) {
					if (checking) {
						checking=false;
						faultRimasto=null;
					}
					//if (!a1.contains(test.getMap())) a1.add(test.getMap()); else System.err.println("Test originale uguale!"); 	// check if it has generated identical alpha
					
					FIC f = new FIC(alpha, type);
					LOG.debug("Alpha "+step+ " : "+f);
					if (flog!=null) flog.println(manipulator+" "+type+" "+f);

					c2.setFIC(f);
					c2 = manipulator.repair(c2);
				} else {
					if (faultRimasto==null) {
						faultRimasto = type==FICType.AND ? FICType.OR : FICType.AND;
					} else {
						if (!checking) {
							faultRimasto = type==FICType.AND ? FICType.OR : FICType.AND;
							checking=true;
						} else {
							continua=false;
						}
					}
				}
				LOG.debug("Repaired. Calculating statistics....");
				if (alpha!=null) {
					Record r = new Record(step, manipulator, alpha, type, c2, oracle, false);
					if (fout!=null) fout.println(r.toString());
					stat.records.add(r);
				}
				LOG.debug("Go to next step...");
				if (fout!=null) fout.flush();
				if (flog!=null) flog.flush();
				if (continua) {
					step++;

					// Calculate next fic
					type = faultRimasto!=null ? faultRimasto : (initRepairType==null ? FICType.getRandomFault() : initRepairType);//FICType.getRandomFault();
					test = getMinimalFIC(type, c2, oracle, yices);
					alpha = test==null ? null : test.toExpr();
				}				
			}
			stat.repairedModel=c2;
			if (flog!=null) flog.println("Repaired Model:\n"+stat.repairedModel.model);
			LOG.debug("Model repaired in "+(step-1)+" steps. Repaired model available in log file."); // l'ultimo step è solo di controllo
			LOG.debug("CHECK EQUIVALENCE: "+SATUtils.isEquivalent(stat.repairedModel.model, oracle.model, yices));
		} catch (Exception e) {e.printStackTrace();}
		return stat;
	}
	
	public static Combination getFailingTest(FICType type, Configuration model, Configuration oracle, YicesJNA yices) {
		//LOG.debug("Initial. Building SAT query...");
		LOG.debug("Starting SAT solver...");
		Expression query = FindFIC.getSATQueryForAlpha(type, model, oracle); // Experiment2.getSATQueryForAlpha(type, c2[0], c);
		//LOG.debug("Query: "+query);
		Combination test = new Combination(yices.getSAT(query));
		LOG.debug("SAT Obtained. Converting to Expression...");
		return test;
	}
	
	public static Combination getMinimalFIC(FICType type, Configuration model, Configuration oracle, YicesJNA yices) {
		Combination test = getFailingTest(type, model, oracle, yices);
		if (test==null || test.getMap()==null) {
	 		LOG.debug("Null fic: do not reduce.");
			return test;
		}
 		LOG.debug("Reducing fic...");
		//boolean yicesJNA = Logger.getLogger(YicesJNA.class).getLevel()==Level.DEBUG;
		//if (yicesJNA) Logger.getLogger(YicesJNA.class).setLevel(Level.ERROR);
		test = new FindFIC(model, oracle).getReducedAlphaOtimized(test, type);
		//if (yicesJNA) Logger.getLogger(YicesJNA.class).setLevel(Level.DEBUG);
		//if (!a2.contains(test.getMap())) a2.add(test.getMap()); else System.err.println("Test ridotto uguale!"); 	// check if it has generated identical alpha
		LOG.debug("Reduced alpha: "+test.getMap().size()+". Repairing...");
		return test;
	}
	
}
