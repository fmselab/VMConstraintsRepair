package constraintsmanipulation.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import constraintsmanipulation.sat.YicesJNA;
import tgtlib.definitions.expression.AndExpression;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;
import tgtlib.definitions.expression.NotExpression;
import tgtlib.definitions.expression.OrExpression;
import tgtlib.definitions.expression.parser.ExpressionParser;
import tgtlib.definitions.expression.type.BoolType;
import tgtlib.definitions.expression.type.EnumConstCreator;
import tgtlib.definitions.expression.visitors.IDExprCollector;

/**
 * A class for a fault-based formula manipulation experiment.
 * It represents a configuration, i.e. a state for the {@code Manipulator} class.
 * @author Marco
 */
public class Configuration {
	
	/**  the EnumConstCreator to make sure that throughout the program, the parsed variables are considered the same if they have the same name. */
	public static EnumConstCreator idc = new EnumConstCreator();
	
	/** The model. */
	public Model model;

	/**  configuration to exclude / include in the set of constraints. */
	public FIC fic;
	
	/** The faults: so that we can skip generating them */
	public List<FIC> faults;
	
	/** Instantiates a new configuration. (it's private because it forces the use of safe public static factory methods) */
	private Configuration() {fic = new FIC();}
	
	public YicesJNA yices;
	
	/**
	 * New empty configuration.
	 *
	 * @return the configuration
	 */
	public static Configuration newEmptyConfiguration() {
		Configuration c = new Configuration();
		c.model = new Model();
		//c.fault = new Fault();
		idc = new EnumConstCreator();
		return c;
	}
	
	/**
	 * New configuration from file.
	 *
	 * @param file the file
	 * @return the configuration
	 */
	public static Configuration newConfigurationFromFile(File file) {
		Configuration c = newEmptyConfiguration();
		try {
			BufferedReader fin = new BufferedReader(new FileReader(file));
			String s="";
			while ((s=fin.readLine())!=null) {
				c.editConfiguration(s);
			}
			fin.close();
		} catch (Exception e) {e.printStackTrace();}
		return c;
	}
	
	/**
	 * New configuration from a string representing the file.
	 *
	 * @param configuration the configuration
	 * @return the configuration
	 */
	public static Configuration newConfiguration(String configuration) {
		Configuration c = newEmptyConfiguration();
		try {
			for (String s : configuration.split("\n")) {
				c.editConfiguration(s);
			}
		} catch (Exception e) {e.printStackTrace();}
		return c;
	}
	
	/**
	 * It actually populate the configuration object, from a string representing the input file.
	 *
	 * @param s the string
	 */
	private void editConfiguration(String s) {
		String formulaString = null;
		try {
			if (s.startsWith("@")){
				// a bool file starts with trhe IDs
				String ID = s.substring(1).trim();
				// if id starts with a number add to a replacement table
				if (Character.isDigit(ID.charAt(0))){
					model.replacedID.put(ID, "N"+ID);
				}
				idc.createIdExpression(ID, BoolType.BOOLTYPE);
			} else if (s.startsWith("$ ")) {  // specify the context of alpha
				fic.context = Arrays.asList(s.substring(2).split(","));
			} else if (s.startsWith("+ ") || s.startsWith("* ")) {
				fic = new FIC(ExpressionParser.parseAsBooleanExpression(s.substring(2), idc), s.startsWith("+ ") ? FICType.OR : FICType.AND);
				fic.context = new Formula(fic.toString()).getFeatures();
			} else if (!s.equals("")) {
				//formulaString = new Formula(s).toString();
				for(Entry<String, String> rid : model.replacedID.entrySet()) {
					s = s.replaceAll(rid.getKey(), rid.getValue());
				}
				// replace 1 as true
				s = s.replaceAll("\\s1\\s", " true ");
				s = s.replaceAll("\\(1\\s", "(true ");
				s = s.replaceAll("\\s1\\)", " true)");
				s = s.replaceAll("!1\\)", " ! true)");
				// 0 by false
				s = s.replaceAll("\\s0\\s", " false ");
				s = s.replaceAll("\\(0\\s", "(false ");
				s = s.replaceAll("\\s0\\)", " false)");
				// not not just removed (the parse cannot digest it)
				s = s.replaceAll("!!", " ");
				
				Expression e = ExpressionParser.parseAsBooleanExpression(s, idc);
				model.constraints.add(e);
				if (model.parameters==null) model.parameters= e.accept(IDExprCollector.instance);
				else model.parameters.addAll(e.accept(IDExprCollector.instance));
			} 
		} catch (Throwable e) {
			System.out.println("error reading " + s + " converted as " + formulaString); 
			e.printStackTrace();
			throw new RuntimeException("error reading string " +s);
		}
	}

	/**
	 *  create a new configuration, duplicating from the previous one.
	 *
	 * @return the configuration
	 */
	public Configuration clone() {
		Configuration c = new Configuration();
		c.model=model.clone();
		c.fic=fic;
		c.yices = yices;
		return c;
	}

	/**
	 * Sets the fault.
	 *
	 * @param f the new fault
	 */
	public void setFIC(FIC f) {
		this.fic=f;
	}
	
	/*public void setAlpha(Expression alpha) {
		fault = new Fault(alpha, computeFICType(alpha));
	}*/
	
	public boolean isFIC(Expression alpha) {
		return true; // any expression can be a fic: there exists at least one oracle configuration that makes it a fic.
		//return computeFICType(alpha)!=FaultType.NONE;
	}

	
	/**
	 * Parses the dimacs.
	 *
	 * @param dimacs the dimacs
	 * @param ids the ids
	 * @return the expression
	 * @throws Exception the exception
	 */
	public static Expression parseDimacs(String dimacs, List<IdExpression> ids) throws Exception {
		List<Expression> clauses = new ArrayList<>();
		for (String line : dimacs.split("\n")) {
			if (line.startsWith("c ")) continue;
			if (line.startsWith("p ")) continue;
			String[] t = line.split(" ");
			if (t.length<=1) continue;
			else if (t.length==2) clauses.add(getIdExpression(Integer.parseInt(t[0]), ids));
			else {
				List<Expression> or = new ArrayList<>();
				for (int i=0; i<t.length-1; i++) if (!t[i].equals("0") && !t[i].equals("")) or.add(getIdExpression(Integer.parseInt(t[i]), ids));
				clauses.add(makeOrExpression(or));
			}
		}
		try {
			return AndExpression.makeAndExpression(clauses);
		} catch (Error e) {
			System.err.println("Errore: "+dimacs);
			return null;
		}
	}
	
	/**
	 * Gets the id expression.
	 *
	 * @param dimacsNumber the dimacs number
	 * @param ids the ids
	 * @return the id expression
	 * @throws Exception the exception
	 */
	private static Expression getIdExpression(int dimacsNumber, List<IdExpression> ids) throws Exception {
		if (dimacsNumber>0) return ids.get(dimacsNumber-1);
		else return NotExpression.createNotExpression(ids.get(-dimacsNumber-1));
	}
	
	/**
	 * Make or expression.
	 *
	 * @param terms the terms
	 * @return the or expression
	 */
	private static OrExpression makeOrExpression(List<Expression> terms) {
		if (terms.size()<2) return null;
		OrExpression or = new OrExpression(terms.get(0), terms.get(1));
		for (int i=2; i<terms.size(); i++) {
			or = new OrExpression(or, terms.get(i));
		}
		return or;
	}
	
	/**
	 * To string.
	 *
	 * @return the list of constraints. If alpha is set, it is also printed out after the constraints
	 */
	public String toString() {
		return model+(fic!=null ? "Alpha: "+fic : "");
	}


	public void addFault(FIC f) {
		if (faults==null) faults = new ArrayList<>();
		faults.add(f);
	}
	
	
	
	

	//XXX Marco OLD methods not used anymore
	/*
	public List<Expression> getSelectedFormulas(String filename) {
	 	List<Expression> formulas = new ArrayList<>();
		loadFromFile(filename);
		List<Integer> selectedFormulas = select();
		for (Integer i : selectedFormulas) formulas.add(constraints.get(i));
		return formulas;
	}

	/** Performs the selection and simplification, producing a modified configuration */
	/*public Configuration getMutated(Simplifier simplifier) {
		Configuration c = newConfiguration(this);
		if (alpha.inOr) { // case OR: model overconstraining 
			// obtain Dimacs from all the involved constraints
			List<Expression> exps = new ArrayList<Expression>();
			List<Integer> selected = selectOr();
			for (int i: selected) exps.add(constraints.get(i));
			exps.add(alpha.alpha);
			CNFExpression cnf = CNFExprConverterNaive.instance.getCNFExprConverter().getCNF(AndExpression.makeAndExpression(exps));
			String dimacs = cnf.toDimacs().toString();
			System.out.println(dimacs);
			// get the list of clauses
			List<Integer> nclauses = new ArrayList<>();
			for (Expression e : exps) nclauses.add(CNFExprConverterNaive.instance.getCNFExprConverter().getCNF(e).getTerms().size());
			System.out.println(nclauses);
			// call MUS
			List<Integer> mus = PicoSAT.computeMUS(dimacs);
			System.out.println(mus);
			// update all the Ci' that have to be updated
			List<Integer> affectedConstraints = getInvolvedConstraintIndexFromMUS(nclauses, mus);
			System.out.println(affectedConstraints);
			for (int i : affectedConstraints) {
				List<Expression> terms = notInConflict(cnf, nclauses, mus, i);
				System.out.println(terms);
				c.constraints.set(selected.get(i), terms!=null && terms.size()>0 ? simplifier.simplify(AndExpression.makeAndExpression(terms)) : null);
			}
			//for (int i=0; i<c.constraints.size(); i++) if (c.constraints.get(i)==null) c.constraints.remove(i); // clean up the erased constraints
		} else {  // case AND: model underconstraining
			int i = selectAndOneConstraint();
			Expression e = new AndExpression(c.constraints.get(i), NotExpression.createNotExpression(alpha.alpha));
			c.constraints.set(i, simplifier.simplify(e));
		}
		return c;
	}*/
	
	public YicesJNA getYices() {
		if (yices==null && model!=null) {
			yices = new YicesJNA(model.parameters);
		}
		return yices;
	}
	
	public void setYices(YicesJNA yices) {
		this.yices=yices;
	}
	
	public void updateYices(Set<IdExpression> params) {
		params.addAll(model.parameters);
		yices = new YicesJNA(params);
	}
	
	public void updateYices(Model anotherModel) {
		updateYices(model.parameters);
	}
}
