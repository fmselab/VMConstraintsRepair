package constraintsmanipulation.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import constraintsmanipulation.distance.BFCD;
import constraintsmanipulation.distance.BFED;
import constraintsmanipulation.manipulator.Manipulator;
import constraintsmanipulation.manipulator.TimeStats;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.Formula;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;
import tgtlib.definitions.expression.parser.ExpressionParser;
import tgtlib.definitions.expression.visitors.IDExprCollector;

/** A class for Configuration Utils.
 * Note: only Boolean parameters (Enumerative are not supported) in CitLab import 
 * From FeatureIDE, for now it reads only the separate constraints (not the ones implied by the feature model itself) */
public class ConfigurationUtils {
	
	/** The Separator line in the file to indicate the BEGIN_CONSTRAINTS. */
	private static final String FEATUREIDE_BEGIN_CONSTRAINTS = "%%";
	
	/** The Separator Symbol in the file to indicate the END_CONSTRAINTS. */
	private static final String FEATUREIDE_END_CONSTRAINTS = "##";

	/** Load configuration from Citlab format
	 *
	 * @param file the file (.citl file of Citlab)
	 * @return the configuration
	 */
	public static Configuration loadConfigurationFromCitlab(File file) {
		Configuration c = Configuration.newEmptyConfiguration();
		try {
			BufferedReader fin = new BufferedReader(new FileReader(file));
			String s ="";
			boolean inBlock=false, constraints=false;
			while ((s=fin.readLine())!=null) {
				if (s.startsWith("Parameters:")) inBlock=true;
				else if (s.equals("end")) inBlock=false;
				else if (s.startsWith("Constraints:")) {inBlock=true; constraints=true;}
				else if (inBlock && !constraints && s.contains("Boolean ")) {
					Expression e = ExpressionParser.parseAsBooleanExpression(s.split(" ")[1].replace(";", ""), Configuration.idc);
					c.model.parameters.addAll(e.accept(IDExprCollector.instance));
				}
				else if (inBlock && constraints && s.contains("#")) {
					Expression e = ExpressionParser.parseAsBooleanExpression(s.split("#")[1].trim().replace("==true", ""), Configuration.idc);
					c.model.constraints.add(e);
				}
			}
			fin.close();
		} catch (Exception e) {e.printStackTrace();}
		return c;
	}
	
	/**
	 * Load configuration from feature IDE model.
	 * TODO so far it loads only cross-tree constraints, not the feature relationships
	 * @param file the file (.m file of FeatureIDE)
	 * @return the configuration
	 */
	public static Configuration loadConfigurationFromFeatureIDEModel(File file) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader fin = new BufferedReader(new FileReader(file));
			String s ="";
			boolean constraints = false;
			while ((s=fin.readLine())!=null) {
				if (s.startsWith(FEATUREIDE_BEGIN_CONSTRAINTS)) constraints=true;
				else if (s.startsWith(FEATUREIDE_END_CONSTRAINTS)) constraints=false;
				else if (constraints && !s.trim().equals("")) {
					sb.append(s.replace(";", "")+"\n");
				}
			}
			fin.close();
		} catch (Exception e) {e.printStackTrace();}
		return Configuration.newConfiguration(sb.toString());
	}
	
	/**
	 * Load configuration from FeatureIDE CNF formula
	 *
	 * @param file the file (CNF export file of FeatureIDE)
	 * @return the configuration
	 */
	public static Configuration loadConfigurationFromFeatureIDECNF(File file) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader fin = new BufferedReader(new FileReader(file));
			String s ="";
			boolean constraints = false;
			while ((s=fin.readLine())!=null) {
				if (s.startsWith("Java Symbols:")) constraints=true;
				else if (constraints && !s.trim().equals("")) {
					String[] st = s.split("  &&  ");
					for (String t : st) sb.append(t+"\n");
					constraints=false;
				}
			}
			fin.close();
		} catch (Exception e) {e.printStackTrace();}
		return Configuration.newConfiguration(sb.toString());
	}
	
	public static String getStatistics(Configuration config, Configuration baseConfig, Manipulator m) {
		Formula f = new Formula(config.model.toSingleString());
		//Formula f = new Formula(AndExpression.makeAndExpression(model.constraints).toString());
		TimeStats time = (m==null ? new TimeStats() : m.getTimeStats());
		return config.fic.getFaultType()+","+(config.fic.getAlpha()==null?"null":new Formula(config.fic.getAlpha().toString()).getNumLiterals())+","+f.getNumFeatures()+","+f.getNumLiterals()+","+f.getNumOperators()+","+f.getNumChars()
			+","+BFED.instance.getDistance(config, baseConfig)+","+BFCD.instance.getDistance(config, baseConfig)+","+time.getTimeSelection()+","+time.getTimeSimplification()+","+time.getTimeTotal();
	}
	
	public static String getStatisticsFast(Configuration config, Configuration baseConfig, Manipulator m) {
		//Formula f = new Formula(config.model.toSingleString());
		//Formula f = new Formula(AndExpression.makeAndExpression(model.constraints).toString());
		TimeStats time = (m==null ? new TimeStats() : m.getTimeStats());
		return config.fic.getFaultType()+","+(config.fic.getAlpha()==null?"null":new Formula(config.fic.getAlpha().toString()).getNumLiterals())+",0,0,0,0"
			+",-1,-1,"+time.getTimeSelection()+","+time.getTimeSimplification()+","+time.getTimeTotal();
	}
	
	public static String getStatisticsHeader() {
		return "type,flit,feat,lit,op,chars,BFED,BFCD,timeSelection,timeSimplification,timeTotal";
	}
	
	public static IdExpression getIdExpressionFromString(String parameter, List<IdExpression> parameters) {
		for (IdExpression e : parameters) if (parameter.equals(e.getIdString())) return e;
		return null;
	}
	
	/** 
	 * @param s the expression to be parsed by ATGT (tgtlib) as String
	 * @return the parsed Expression
	 * @throws Exception if parsing error
	 */
	public static Expression parseExpression(String s) throws Exception {
		if (s!=null && s.contains("!!")) s=s.replace("!!", "");
		return ExpressionParser.parseAsBooleanExpression(s, Configuration.idc);
	}
	
	public static Set<IdExpression> joinParameters(Set<IdExpression> p1, Set<IdExpression> p2) {
		Set<IdExpression> p = new HashSet<>(p1);
		p.addAll(p2);
		return p;
	}
	
	/** @return the parameters contained in the model but not in the oracle */
	public static Set<IdExpression> getRemovedParameters(Set<IdExpression> modelParameters, Set<IdExpression> oracleParameters) {
		Set<IdExpression> removedParameters = new HashSet<>();
		for (IdExpression e : modelParameters) if (!oracleParameters.contains(e)) removedParameters.add(e);
		return removedParameters;
	}
}
