package constraintsmanipulation.sat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;

import tgtlib.definitions.expression.AndExpression;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;
import tgtlib.definitions.expression.NotExpression;
import tgtlib.definitions.expression.visitors.IDExprCollector;
import tgtlib.definitions.normalform.cnf.CNFExprConverterNaive;
import tgtlib.definitions.normalform.cnf.CNFExpression;
import tgtlib.definitions.normalform.cnf.Dimacs;
import tgtlib.generator.SatExecutionResult;

/**
 * The Class Sat4j.
 */
public class Sat4j implements SATSolver {

	/**
	 * Instantiates a new sat 4 j.
	 */
	private Sat4j() {}

	/** The instance. */
	private static Sat4j instance;

	/**
	 * Gets the single instance of Sat4j.
	 *
	 * @return single instance of Sat4j
	 */
	public static Sat4j getInstance() {
		return instance==null ? instance = new Sat4j() : instance;
	}

	/**
	 * Gets the SAT expression.
	 *
	 * @param e the e
	 * @param parameters the parameters
	 * @return the SAT expression
	 */
	public Expression getSATExpression(Expression e, Set<IdExpression> parameters) {
		Sat4JResult sat = getSAT(e,parameters);
		if (sat==null || !sat.isSatisfiable()) return null;
		List<Expression> exps = new ArrayList<>();
		for (Entry<IdExpression, Boolean> a : sat.getModel().entrySet()) {
			exps.add(a.getValue() ? a.getKey() : NotExpression.createNotExpression(a.getKey()));
		}
		return AndExpression.makeAndExpression(exps);
	}
	
	/**
	 * Gets the sat.
	 *
	 * @param e the e
	 * @param parameters the parameters
	 * @return the sat
	 */
	public Sat4JResult getSAT(Expression e, Set<IdExpression> parameters) {
		// build the solver
		ISolver solver = SolverFactory.newDefault();
		// preparing the solver
		//
		solver.reset();
		// prepare the solver to accept MAXVAR variables. MANDATORY
		// may be an underapproximation due to the new variables
		solver.newVar((parameters==null?e.accept(IDExprCollector.instance):parameters).size());
		// not mandatory for SAT solving. MANDATORY for MAXSAT solving
		// solver.setExpectedNumberOfClauses(dimacs.getnClauses());
		// get the ids for this condition
		// TODO (it may contain more ids because when transformed to CNF, new
		// ids may be introduced)
		// solver.setExpectedNumberOfClauses(variables.size());
		// solver.setExpectedNumberOfClauses(variables.size());

		CNFExpression cnf=null;
		try {
			// reset the computation of the
			cnf = CNFExprConverterNaive.instance.getCNFExprConverter().getCNF(e);
		} catch (Exception ex) {System.err.println("ERROR EMPTY CNF"); return null;}

		// at this point id contains all the ids only once
		Dimacs dimacs = cnf.toDimacs();
		
		//
		try {
			IProblem problem = solver;
			// add the clauses
			for (int[] clause : dimacs.getClauses()) {
				try {
					solver.addClause(new VecInt(clause));
					// null if the model did not change????
				} catch (ContradictionException c) {
					// not a real exception
					return null;
				}
			}
			// we are done. Working now on the IProblem interface
			return new Sat4JResult(problem.isSatisfiable(), problem, dimacs);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}	
	}

	@Override
	public boolean isSAT(Expression e) {
		Sat4JResult sat = getSAT(e,null);
		return sat!=null && sat.isSatisfiable();
	}

	@Override
	public Map<IdExpression, Boolean> getSAT(Expression e) {
		return getSAT(e, new HashSet<IdExpression>(IDExprCollector.getIdsAsList(e))).getModel();
	}

	
	class Sat4JResult extends SatExecutionResult {
		private boolean satisfiable;
		
		private Map<IdExpression, Boolean> model;

		private IProblem problem;
		
		private Dimacs dimacs;
		

		/**
		 * Instantiates a new sat4 j result.
		 *
		 * @param sat if the problem is  satisfiable
		 * @param pro the problem (already solved)
		 * @param ids2 list of id expressions ! (in order is important): empty if the CNF is empty
		 * @param dimacs 
		 */
		public Sat4JResult(boolean sat, IProblem pro, Dimacs dimacs) {
			satisfiable = sat;
			//
			this.dimacs = dimacs;
			//
			this.problem = pro;
			// compute the model
			if (sat) computeModel();
			// the dimacs used 
		}
		
		/** compute the model for the problem
		 * found sat 
		 * */
		void computeModel(){
			// compute the assignments
			model = new HashMap<IdExpression, Boolean>();
			// if there is a model for some ids, get the model from the problem
			if (dimacs == null) return;
			List<IdExpression> iDsinDimacs = dimacs.getIDs();
			if (!iDsinDimacs.isEmpty()) {			
				// be careful: the model may contain more or even fewer ids than the spec
				// in order to allow the incremental call
			    // not the id starts from 1, ids from 0. 
				for(int i = 0 ; i < iDsinDimacs.size(); i++){
					model.put(iDsinDimacs.get(i), problem.model(i+1));
				}
			}
		}

		@Override
		public void close() {
			// do nothing		
		}

		@Override
		public String toString() {
			return "sat:" +satisfiable  + ", model:" + model;
		}

		@Override
		public boolean isValid() {
			// TODO correct???
			return true;
		}

		public Boolean getValue(IdExpression id) {
			return model.get(id);
		}

		public boolean isSatisfiable() {
			return satisfiable;
		}
		
		public Map<IdExpression, Boolean> getModel() {
			return model;
		}
	}
}
