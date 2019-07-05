package constraintsmanipulation.manipulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.Formula;
import constraintsmanipulation.simplifier.BestSimplifier;
import constraintsmanipulation.simplifier.Simplifier;
import constraintsmanipulation.visitor.LiteralCounter;
import tgtlib.definitions.expression.AndExpression;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.NotExpression;
import tgtlib.definitions.expression.Operator;
import tgtlib.definitions.expression.OrExpression;
import tgtlib.definitions.normalform.cnf.CNFExpression;

/**
 * It repairs the model using the Select-And-Simplify (SAS) technique.
 * The Simplifier must be specified.
 * Call the {@code repair(Configuration)} method to perform the selection and simplification, producing a repaired configuration.
 */
public class ManipulatorSAS extends Manipulator {
	
	static final private Logger LOG = Logger.getLogger(ManipulatorSAS.class);
	
	public ManipulatorSAS() {simplifier = new BestSimplifier();}
	public ManipulatorSAS(Simplifier simplifier) {this.simplifier=simplifier;}
	
	private List<Integer> selectAnd(Configuration c) {
		ArrayList<Integer> best = new ArrayList<Integer>();
		int bestAffinity=0;
		int n;
		for (int i=0; i<c.model.constraints.size(); i++) {
			if ((n=Formula.getAffinity(c.model.constraints.get(i), c.fic.getAlpha()))>=bestAffinity) {
				if (n>bestAffinity) {best.clear(); bestAffinity=n;}
				best.add(i);
			}
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
			//if ( (Formula.getCommonFeatures(c.model.constraints.get(i), c.fault.alpha).size()>0 
			//if(!Sat4j.getInstance().isSAT(new AndExpression(c.model.constraints.get(i), c.fault.alpha))) {
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
			int literalsBefore = c.model.constraints.get(i).accept(LiteralCounter.instance);
			Expression e = new AndExpression(c.model.constraints.get(i), NotExpression.createNotExpression(c.fic.getAlpha()));
			e = simplifier.simplify(e);
			int literalsAfter = c.model.constraints.get(i).accept(LiteralCounter.instance);
			if (literalsBefore<=literalsAfter) {
				// add at the end
				c.model.constraints.add(simplifier.simplify(NotExpression.createNotExpression(c.fic.getAlpha())));
			} else c.model.constraints.set(i, e);
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
		Stack<Integer> toRemove = new Stack<>();
		timeStats.setTimeSelection(Calendar.getInstance().getTimeInMillis()-start);
		start=Calendar.getInstance().getTimeInMillis();
		LOG.debug("SELECTED CONSTRAINTS: "+Arrays.toString(selected.toArray(new Integer[0])));
		for (int i=0; i<c.model.constraints.size(); i++) if (selected.contains(i)) {
			LOG.debug("Simpl "+i+" "+OrExpression.mkBinExpr(c.model.constraints.get(i), Operator.OR, c.fic.getAlpha()));
			Expression simplified = simplifier.simplify(OrExpression.mkBinExpr(c.model.constraints.get(i), Operator.OR, c.fic.getAlpha()));
			if (simplified==null) {LOG.debug("TOREMOVE "+i); toRemove.push(i);}
			else {
				c.model.constraints.set(i, simplified);
				LOG.debug("Simpl "+i+" RESULT="+simplified);
			}
		}
		while (!toRemove.isEmpty()) {
			LOG.debug("REMOVING CONSTRAINT "+toRemove.peek()+" "+getSimplifierName());
			c.model.constraints.remove((int)toRemove.pop());
		}
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
				if (i<cumul) involvedConstraints.add(j);
			}
		}
		return involvedConstraints;
	}
	
	
	
	// methods for mutation generation - moved to Mutator
	/*
	public enum Mutation {REMOVE_CONSTRAINT, REMOVE_LITERAL, SUBSTITUTE_LITERAL};
	
	public Configuration mutate(Configuration conf) {
		Configuration c = conf.clone();
		int idConstraintToMutate = (int)(Math.random()*c.model.constraints.size());
		Mutation m = Mutation.values()[(int)(Math.random()*Mutation.values().length)];
		Expression mutated = mutate(c, c.model.constraints.get(idConstraintToMutate), m);
		if (mutated==null) c.model.constraints.remove(idConstraintToMutate);
		else c.model.constraints.set(idConstraintToMutate, mutated);
		return c;
	}
	
	private Expression mutate(Configuration c, Expression e, Mutation m) {
		switch (m) {
		case REMOVE_CONSTRAINT: return null;
		case REMOVE_LITERAL: 
		case SUBSTITUTE_LITERAL: 
			MutationVisitor mut = new MutationVisitor(m, c, 1);
			Expression e2 = null;
			while (!mut.mutationPerformed()) e2 = e.accept(mut);
			return e2;
		}
		return e;
	}*/
	
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
