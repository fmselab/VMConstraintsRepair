package constraintsmanipulation;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import constraintsmanipulation.manipulator.Manipulator;
import constraintsmanipulation.manipulator.Manipulators;
import constraintsmanipulation.manipulator.Mutation;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.FICType;

/** Classe per eseguire l'esperimento 2. Da lanciare con JUnit */
public class Experiment2 {
	
	//static final private Logger LOG = Logger.getLogger(Experiment2.class);
	
	/** Il numero di esecuzioni */
	static int N=10;
	
	static Models[] models = {
//			Models.EXAMPLE,
//			Models.REGISTER,
//			Models.DJANGO,
			Models.TIGHT_VNC,
			//Models.GPL,
			//Models.RHISCOM3
	};
	static Mutation[] muts = {
			Mutation.RC,
			Mutation.RL,
			Mutation.SL,
	};
	static Manipulator[] mans = {
			Manipulators.NAIVE.getManipulator(),
//			Manipulators.ONLY_SELECTION.getManipulator(),
//			Manipulators.ATGT.getManipulator(),
//			Manipulators.ESPRESSO.getManipulator(),
//			Manipulators.JBOOL.getManipulator(),
//			Manipulators.QM.getManipulator(),
	};
	
	@org.junit.Test
	public void exp2() {
		try {
			Experiment3.flog = new PrintWriter(new FileWriter("output/exp2_flog.txt"));
			for (Models m : models) {
				PrintWriter fmodels = new PrintWriter(new FileWriter("output/exp2_models_" + m + ".txt"));
				List<Stats> stats = new ArrayList<>();
				for (Mutation mut : muts){
					Stats[] inner = new Stats[mans.length];
					System.out.println(m+" - "+mut);
					for (int i=0; i<N; i++) {
						Configuration c = m.loadConfiguration();
						Configuration[] c2 = new Configuration[mans.length]; 
						c2[0] = mut.mutate(c);
						fmodels.println("Mutated "+m+" by "+mut+":\n"+c2[0]+"\n");
						for (int j=1; j<mans.length; j++) c2[j] = c2[0];
						for (int j=0; j<mans.length; j++) {
							Stats s = Experiment3.performExperiment3(c, c2[j], mans[j], FICType.AND);
							s.setIdAndMutationAndManipulator(i, mut, mans[j]);
							if (inner[j]==null) 	inner[j]=s;
							else inner[j].records.addAll(s.records);
							fmodels.println("Repaired " + m + " with "+ s.manipulator +", mutated by "+ s.mutation + " in " + s.records.size() + " steps.\nStatsOriginal: " + c.model.getStatistics() + " StatsRepaired: "+ (s.repairedModel!=null && s.repairedModel.model!=null ? s.repairedModel.model.getStatistics() : "isNull") +"\n"+s.repairedModel +"\n");
						}
					}
					for (Stats s : inner) stats.add(s);
				}
			
				PrintWriter fout = new PrintWriter(new FileWriter("output/exp2_"+m.getName()+".csv"));
				fout.println(Stats.HEADER_EXP2);
				for (Stats s : stats) {
					fout.println(s.toStringForExp2());
				}
				fout.close();
				fmodels.close();
			}
			Experiment3.flog.close();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	/*public static void performExperiment2(final Configuration c, File outputStats) {
		try {
			Writer fout=null;
			if (outputStats!=null) fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputStats), "UTF-8"));
			if (fout!=null) fout.write("step,method,"+ConfigurationUtils.getStatisticsHeader()+"\n");
			System.out.println("Initial Model:\nParameters: "+c.model.parameters+"\n"+c.model+"\n");
			Manipulator[] mans = Manipulators.mansExceptQM;
			//Manipulator[] mans = new Manipulator[] {Manipulator.mans[1]};
			for (int n=1; n<=N; n++) {
				Mutation m = Mutation.getRandomMutation();
				Configuration c1 = m.mutate(c);
				LOG.debug(n+")  Mutated Model:\n"+c1.model+"\n");
				System.out.println(checkEquivalence(c,c1));
				
				Configuration[] c2 = new Configuration[mans.length];
				for (int i=0; i<c2.length; i++) c2[i]=c1.clone();
				Expression alpha=null;
				List<Map<IdExpression,Boolean>> a1 = new ArrayList<>(), a2 = new ArrayList<>();
				int step=0;
				FICType faultRimasto=null;
				boolean continua = false;
				
				do {
					if (continua) continua=false;
					//Difference d = new Difference(c, c2[0]);
					//alpha = Sat4j.getInstance().getSATExpression(AndExpression.mkBinExpr(getExpression(new ArrayList<Expression>(d.commonConstraints.values())), Operator.AND, XOrExpression.mkBinExpr(getExpression(new ArrayList<Expression>(d.differentConstraints1.values())), Operator.XOR, getExpression(new ArrayList<Expression>(d.differentConstraints2.values())))), c1.model.parameters);
					//alpha = SATSolver.toExpr(Yices.getInstance().getSAT(AndExpression.mkBinExpr(getExpression(new ArrayList<Expression>(d.commonConstraints.values())), Operator.AND, XOrExpression.mkBinExpr(getExpression(new ArrayList<Expression>(d.differentConstraints1.values())), Operator.XOR, getExpression(new ArrayList<Expression>(d.differentConstraints2.values()))))));
					FICType type = faultRimasto!=null ? faultRimasto : FICType.getRandomFault();
					Map<IdExpression,Boolean> test = YicesJNA.getInstance().getSAT(getSATQueryForAlphaClever(type, c2[0], c));
					alpha = Combination.toExpr(test);
					if (alpha!=null) {
						if (!a1.contains(test)) a1.add(test); else System.err.println("Test originale uguale!"); 	// check if it has generated identical alpha
						System.out.println("ALPHA "+step+": "+test.size()+" - "+alpha);
						test = getReducedAlpha(test, c2[0], c, type);
						if (!a2.contains(test)) a2.add(test); else System.err.println("Test ridotto uguale!"); 	// check if it has generated identical alpha
						alpha = Combination.toExpr(test);
						System.out.println("Reduced alpha: "+test.size()+" - "+alpha);
						
						FIC f = new FIC(alpha, type);
						LOG.debug("Alpha: "+f);
						
						for (int i=0; i<c2.length; i++) {
							c2[i].setFIC(f);
							c2[i] = mans[i].repair(c2[i]);
						}
					} else if (faultRimasto==null) {
						faultRimasto = type==FICType.AND ? FICType.OR : FICType.AND;
						continua=true;
					}
					if (!continua) step++;
				} while (alpha!=null || continua);
				for (int i=0; i<c2.length; i++) {
					if (fout!=null) fout.write(n+","+m+","+step+","+mans[i].getSimplifierName()+","+ConfigurationUtils.getStatistics(c2[i],c, mans[i])+"\n");
				}
				if (fout!=null) fout.flush();
				LOG.debug("Repaired Model:\n"+c2[0].model);
				
				//checkEquivalence(c,c2);
			}
			if (fout!=null) fout.close();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public static Map<IdExpression,Boolean> getReducedAlpha(Map<IdExpression,Boolean> alpha, Configuration c1, Configuration c2, FICType type) {
		Map<IdExpression,Boolean> res = new HashMap<>(alpha);
		RandomSelectionInList<IdExpression> a = new RandomSelectionInList<>(alpha.keySet());
		IdExpression id;
		int i=0;
		while ((id=a.nextRandomElement())!=null) {
			Map<IdExpression,Boolean> temp = new HashMap<>(res);
			temp.remove(id);
			if (isFICConsideringOracle(Combination.toExpr(temp), c1, c2, type)) {
				res.remove(id);
			}
			i++;
			if (i%100==0) LOG.debug("Minim. iteration "+i);
		}
		return res;
	}
	
	public static Map<IdExpression,Boolean> getReducedAlphaOptimized(Map<IdExpression,Boolean> alpha, Configuration c1, Configuration c2, FICType type) {
		Map<IdExpression,Boolean> res = new HashMap<>(alpha);
		RandomSelectionInList<IdExpression> a = new RandomSelectionInList<>(alpha.keySet());
		IdExpression id;
		int i=0;
		while ((id=a.nextRandomElement())!=null) {
			Map<IdExpression,Boolean> temp = new HashMap<>(res);
			temp.remove(id);
			if (isFICConsideringOracle(Combination.toExpr(temp), c1, c2, type)) {
				res.remove(id);
			}
			i++;
			if (i%100==0) LOG.debug("Minim. iteration "+i);
		}
		return res;
	}
	
	
	static Expression getSATQueryForAlphaClever(FICType type, Configuration toRepairConf, Configuration correctConf) {
		if (type==FICType.AND) return getC1AndNotC2Clever(toRepairConf, correctConf);
		else if (type==FICType.OR) return getC1AndNotC2Clever(correctConf, toRepairConf);
		return null;
	}
	
	static Expression getSATQueryForAlpha(FICType type, Configuration toRepairConf, Configuration correctConf) {
		if (type==FICType.AND) return getC1AndNotC2(toRepairConf, correctConf);
		else if (type==FICType.OR) return getC1AndNotC2(correctConf, toRepairConf);
		return null;
	}*/
	
	/** @return if alpha is a FIC between C1 and C2, given the fault type 
	 * C1 � il modello mutato, faulty, da correggere, a cui applico alpha 
	 * C2 � il modello corretto, da raggiungere */
	/* Old version (with restricted definition of fic):
	public static boolean isFICBetweenC1AndC2(Expression alpha, Configuration c1, Configuration c2, FICType type) {
		Expression c = null;
		if (type==FICType.AND) {
			c = AndExpression.mkBinExpr(c1.model.toSingleExpression(), Operator.AND, NotExpression.createNotExpression(c2.model.toSingleExpression()));
		} else if (type==FICType.OR) {
			c = AndExpression.mkBinExpr(NotExpression.createNotExpression(c1.model.toSingleExpression()), Operator.AND, c2.model.toSingleExpression());
		}
		return !Yices.getInstance().isSAT(AndExpression.mkBinExpr(NotExpression.createNotExpression(c), Operator.AND, alpha));
	}*/
	
	/*
	public static boolean isFICConsideringOracle(Expression alpha, Configuration model, Configuration oracle, FICType type) {
		if (type==FICType.AND) {
			return !YicesJNA.getInstance().isSAT(Util.mkExpr(oracle.model.toSingleExpression(), Operator.AND, alpha));  // oracolo sempre falso
					// && YicesJNA.getInstance().isSAT(Util.mkExpr(model.model.toSingleExpression(), Operator.AND, alpha));  // esistenza test vero nel modello
		} else if (type==FICType.OR) {
			return !YicesJNA.getInstance().isSAT(Util.mkExpr(NotExpression.createNotExpression(oracle.model.toSingleExpression()), Operator.AND, alpha));  // oracolo sempre vero
					// && YicesJNA.getInstance().isSAT(Util.mkExpr(NotExpression.createNotExpression(model.model.toSingleExpression()), Operator.AND, alpha));  // esistenza test falso nel modello
		}
		return false;
	}
	
	public static Expression getC1XorC2Clever(Configuration c1, Configuration c2) {
		Difference d = new Difference(c1, c2);
		//return AndExpression.mkBinExpr(c1.model.toSingleExpression(), Operator.AND, NotExpression.createNotExpression(c2.model.toSingleExpression()));
		return AndExpression.mkBinExpr(getExpression(new ArrayList<Expression>(d.commonConstraints.values())), Operator.AND, XOrExpression.mkBinExpr(getExpression(new ArrayList<Expression>(d.differentConstraints1.values())), Operator.XOR, NotExpression.createNotExpression(getExpression(new ArrayList<Expression>(d.differentConstraints2.values())))));
	}
	
	public static Expression getC1AndNotC2Clever(Configuration c1, Configuration c2) {
		Difference d = new Difference(c1, c2);
		//return AndExpression.mkBinExpr(c1.model.toSingleExpression(), Operator.AND, NotExpression.createNotExpression(c2.model.toSingleExpression()));
		return AndExpression.mkBinExpr(getExpression(new ArrayList<Expression>(d.commonConstraints.values())), Operator.AND, AndExpression.mkBinExpr(getExpression(new ArrayList<Expression>(d.differentConstraints1.values())), Operator.AND, NotExpression.createNotExpression(getExpression(new ArrayList<Expression>(d.differentConstraints2.values())))));
	}
	
	public static Expression getC1AndNotC2(Configuration c1, Configuration c2) {
		return AndExpression.mkBinExpr(c1.model.toSingleExpression(), Operator.AND, NotExpression.createNotExpression(c2.model.toSingleExpression()));
	}
	
	private static Expression getExpression(List<Expression> c) {
		if (c==null || c.size()==0) return BoolType.TRUE_CONST;
		if (c.size()==1) return c.get(0);
		return AndExpression.makeAndExpression(c);
	}
	
	public static void checkEquivalence(Configuration baseConf, Configuration[] c2) throws RuntimeException {
		System.out.print("CHECKING EQUIVALENCE...");
		for (int i=0; i<c2.length; i++) {
			if (i==0 && YicesJNA.getInstance().isSAT(XOrExpression.mkBinExpr(baseConf.model.toSingleExpression(), Operator.XOR, c2[i].model.toSingleExpression()))) {
				throw new RuntimeException("Models not equivalent: 0");
			}
			else if (i>0 && YicesJNA.getInstance().isSAT(XOrExpression.mkBinExpr(c2[i-1].model.toSingleExpression(), Operator.XOR, c2[i].model.toSingleExpression()))) {
				throw new RuntimeException("Models not equivalent: "+i+" "+Manipulators.mans[i].getSimplifierName()+"\n"+c2[i-1]+"\n"+c2[i]+"\n"+Combination.toExpr(Sat4j.getInstance().getSAT(XOrExpression.mkBinExpr(c2[i-1].model.toSingleExpression(), Operator.XOR, c2[i].model.toSingleExpression()))));
			}
		}
		System.out.println("OK");
	}
	
	public static void checkEquivalence(List<Configuration> configurations) throws RuntimeException {
		Configuration[] a = configurations.toArray(new Configuration[0]);
		Experiment2.checkEquivalence(a[0], a);
	}
	
	public static boolean checkEquivalence(Configuration c1, Configuration c2) {
		return !Sat4j.getInstance().isSAT(XOrExpression.mkBinExpr(c1.model.toSingleExpression(), Operator.XOR, c2.model.toSingleExpression()));
	}*/
	
	
	/*@org.junit.Test
	public void testRegister() {
		//performExperiment2(Configuration.newConfigurationFromFile(new File("data/example/register.txt")), new File("data/example/stats2_register.csv"));
		performExperiment2(Models.REGISTER.loadConfiguration(), new File("data/output/stats2_register.csv"));
	}
	@org.junit.Test
	public void testGPL() {
		//performExperiment2(ConfigurationFromFeatureIDE.loadConfigurationFromFeatureIDEModel(new File("data/featureide/gpl_ahead.m")), new File("data/output/stats2_gpl.csv"));
		performExperiment2(Models.GPL.loadConfiguration(), null);
	}
	@org.junit.Test
	public void testTightVNC() {
		performExperiment2(Models.TIGHT_VNC.loadConfiguration(), new File("data/output/stats2_TightVNC.csv"));
	}*/
	
}
