package constraintsmanipulation.manipulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.Formula;
import constraintsmanipulation.sat.PicoSAT;
import constraintsmanipulation.simplifier.Simplifier;
import tgtlib.definitions.expression.AndExpression;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.NotExpression;
import tgtlib.definitions.normalform.cnf.CNFExprConverterNaive;
import tgtlib.definitions.normalform.cnf.CNFExpression;

/**
 * It repairs the model using the Select-And-Simplify (SAS) technique.
 * The Simplifier must be specified.
 * The WEAKENING repair is NOT sound, but it allows faster repair if new STRENGTHENING repairs are performed, in the loop.
 * TODO this OR repair still does not work. With out fic definition, it is not a MUS to find, but a partial SAT.
 * 
 * Call the {@code repair(Configuration)} method to perform the selection and simplification, producing a repaired configuration.
 */
public class ManipulatorSAS2 extends ManipulatorSAS {
	
	static final private Logger LOG = Logger.getLogger(ManipulatorSAS2.class);
	
	public ManipulatorSAS2(Simplifier simplifier) {super(simplifier);}
	
	private List<Integer> selectAnd(Configuration c) {
		ArrayList<Integer> best = new ArrayList<Integer>();
		int bestAffinity=0;
		int n;
		for (int i=0; i<c.model.constraints.size(); i++) 
			if ((n=Formula.getAffinity(c.model.constraints.get(i), 
					c.fic.getAlpha()))>=bestAffinity) {
			if (n>bestAffinity) {best.clear(); bestAffinity=n;}
			best.add(i);
		}
		return best;
	}
	
	private int selectAndOneConstraint(Configuration c) {
		List<Integer> pos = selectAnd(c);
		return pos==null||pos.isEmpty() ? -1 : pos.get(0);
	}
	
	private List<Integer> selectOr(Configuration c) {
		List<Integer> best = new ArrayList<>();
		for (int i=0; i<c.model.constraints.size(); i++) {
			if (c.getYices().isSAT(new AndExpression(NotExpression.createNotExpression(c.model.constraints.get(i)), c.fic.getAlpha()))) {
				best.add(i);
			}
		}
		return best;
	}
	
	@Override
	public Configuration repairAnd(Configuration c) {
		System.out.print("REPAIRING AND WITH "+this.getSimplifierName()+" ... ");
		long start = Calendar.getInstance().getTimeInMillis();
		int i = selectAndOneConstraint(c);
		timeStats.setTimeSelection(Calendar.getInstance().getTimeInMillis()-start);
		start=Calendar.getInstance().getTimeInMillis();
		System.out.print("SELECTED. SIMPLIFYING... ");
		if (i==-1) c.model.constraints.add(simplifier.simplify(NotExpression.createNotExpression(c.fic.getAlpha())));
		else {
			Expression e = new AndExpression(c.model.constraints.get(i), NotExpression.createNotExpression(c.fic.getAlpha()));
			c.model.constraints.set(i, simplifier.simplify(e));
		}
		timeStats.setTimeSimplification(Calendar.getInstance().getTimeInMillis()-start);
		System.out.println("REPAIRED");
		return c;
	}
	
	@Override
	public Configuration repairOr(Configuration c) {
		System.out.println("REPAIRING OR WITH "+this.getSimplifierName());
		long start = Calendar.getInstance().getTimeInMillis();
		List<Integer> selected = selectOr(c);
		
		// obtain Dimacs from all the involved constraints
		List<Expression> exps = new ArrayList<Expression>();
		for (int i: selected) exps.add(c.model.constraints.get(i));
		exps.add(c.fic.getAlpha());
		Expression toDimacs = AndExpression.makeAndExpression(exps);
		LOG.debug("toDimacs: "+toDimacs);
		CNFExpression cnf = CNFExprConverterNaive.instance.getCNFExprConverter().getCNF(toDimacs);
		LOG.debug("CNFExpression: "+cnf);
		String dimacs = cnf.toDimacs().toString();
		LOG.debug("Dimacs: "+dimacs);
		// get the list of clauses
		List<Integer> nclauses = new ArrayList<>();
		for (Expression e : exps) nclauses.add(CNFExprConverterNaive.instance.getCNFExprConverter().getCNF(e).getTerms().size());
		LOG.debug("nClauses: "+Arrays.toString(nclauses.toArray(new Integer[0])));
		// call MUS
		List<Integer> mus = PicoSAT.computeMUS(dimacs);
		LOG.debug("MUS: "+mus);
		// update all the Ci' that have to be updated
		List<Integer> affectedConstraints = getInvolvedConstraintIndexFromMUS(nclauses, mus);
		LOG.debug("Affected constraints: "+Arrays.toString(affectedConstraints.toArray(new Integer[0])));
		if (Simplifier.LOG) System.out.println(affectedConstraints);
		
		timeStats.setTimeSelection(Calendar.getInstance().getTimeInMillis()-start); start=Calendar.getInstance().getTimeInMillis();

		Stack<Integer> toRemove = new Stack<>();
		for (int i : affectedConstraints) {
			List<Expression> terms = notInConflict(cnf, nclauses, mus, i);
			if (Simplifier.LOG) System.out.println(terms);
			if (terms!=null && terms.size()>0) {
				c.model.constraints.set(selected.get(i), simplifier.simplify(AndExpression.makeAndExpression(terms)));
			} else {
				if (!toRemove.contains(selected.get(i))) {
					toRemove.push(selected.get(i));
					LOG.debug("Push "+selected.get(i));
				}
			}
			//c.model.constraints.set(selected.get(i), terms!=null && terms.size()>0 ? simplifier.simplify(AndExpression.makeAndExpression(terms)) : null);
		}
		while (!toRemove.empty()) c.model.constraints.remove((int)toRemove.pop());
		//System.out.println(c.model.checkConsistency()+ " " + simplifier.getName());
		
		timeStats.setTimeSimplification(Calendar.getInstance().getTimeInMillis()-start);
		
		return c;
	}
	
	public List<Expression> notInConflict(CNFExpression cnf, List<Integer> nclauses, List<Integer> mus, int constraintIndex) {
		List<Expression> terms = new ArrayList<>();
		int cumul = 0;
		for (int i=0; i<nclauses.size()-1; i++) {
			if (i==constraintIndex) {
				for (int j=0; j<nclauses.get(i); j++) if (!mus.contains(cumul+j)) terms.add(cnf.getTerms().get(cumul+j).disjoint());
			}
			cumul+=nclauses.get(i);
		}
		return terms;	
	}
	
	/**
	 * Gets the involved constraint index from MUS.
	 *
	 * @param nclauses the nclauses
	 * @param mus the mus
	 * @return the involved constraint index from MUS
	 */
	public List<Integer> getInvolvedConstraintIndexFromMUS(List<Integer> nclauses, List<Integer> mus) {
		List<Integer> involvedConstraints = new ArrayList<>();
		for (int i : mus) {
			int cumul = 0;
			for (int j=0; j<nclauses.size()-1; j++) { // exclude the alpha constraint, added at the end
				cumul+=nclauses.get(j);
				if (i<cumul) {
					involvedConstraints.add(j);
					break;
				}
			}
		}
		return involvedConstraints;
	}
	
	
	/** TODO: it converts all the selected constraints into CNF using ATGT, which is SLOW: to improve using JBool CNF conversion!!!
	 * @param c the configuration to repair
	 * @return the configuration repaired considering the fault as an OR
	 */
	/* Wrong repair OR: does not produce equivalent models @Override
	public Configuration repairOr(Configuration c) {
		// obtain Dimacs from all the involved constraints
		List<Expression> exps = new ArrayList<Expression>();
		List<Integer> selected = selectOr(c);
		for (int i: selected) exps.add(c.model.constraints.get(i));
		exps.add(c.fault.getAlpha());
		CNFExpression cnf = CNFExprConverterNaive.instance.getCNFExprConverter().getCNF(AndExpression.makeAndExpression(exps));
		String dimacs = cnf.toDimacs().toString();
		if (Simplifier.LOG) System.out.println(dimacs);
		// get the list of clauses
		List<Integer> nclauses = new ArrayList<>();
		for (Expression e : exps) nclauses.add(CNFExprConverterNaive.instance.getCNFExprConverter().getCNF(e).getTerms().size());
		if (Simplifier.LOG) System.out.println(nclauses);
		// call MUS
		List<Integer> mus = PicoSAT.computeMUS(dimacs);
		if (Simplifier.LOG) System.out.println(mus);
		// update all the Ci' that have to be updated
		List<Integer> affectedConstraints = getInvolvedConstraintIndexFromMUS(nclauses, mus);
		if (Simplifier.LOG) System.out.println(affectedConstraints);
		
		c.finishSelection();
		
		Stack<Integer> toRemove = new Stack<>();
		for (int i : affectedConstraints) {
			List<Expression> terms = notInConflict(cnf, nclauses, mus, i);
			if (Simplifier.LOG) System.out.println(terms);
			if (terms!=null && terms.size()>0) {
				c.model.constraints.set(selected.get(i), simplifier.simplify(AndExpression.makeAndExpression(terms)));
			} else {
				toRemove.push(selected.get(i));
			}
			//c.model.constraints.set(selected.get(i), terms!=null && terms.size()>0 ? simplifier.simplify(AndExpression.makeAndExpression(terms)) : null);
		}
		while (!toRemove.empty()) c.model.constraints.remove((int)toRemove.pop());
		System.out.println(c.model.checkConsistency()+ " " + simplifier.getName());
		return c;
	}*/
	
}
