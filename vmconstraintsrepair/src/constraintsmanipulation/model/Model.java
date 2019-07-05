package constraintsmanipulation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constraintsmanipulation.visitor.LiteralCounter;
import constraintsmanipulation.visitor.OperatorCounter;
import constraintsmanipulation.visitor.ToString;
import tgtlib.definitions.expression.AndExpression;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;
import tgtlib.definitions.expression.type.BoolType;
import tgtlib.definitions.expression.visitors.IDExprCollector;

/**
 * The Class Model.
 */
public class Model {
	
	/**  list of parameters. */
	public Set<IdExpression> parameters = new HashSet<>();
	
	/**  list of constraints. */
	public List<Expression> constraints = new ArrayList<>();

	// ID replaced because they begin with numbers
	
	public Map<String,String> replacedID = new HashMap<>();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Model clone() {
		Model m = new Model();
		m.parameters=new HashSet<>(parameters);
		m.constraints = new ArrayList<>(constraints);
		return m;
	}
	
	public Model() {}
	
	public Model(List<Expression> constraints) {
		this.constraints = constraints;
		computeParametersFromConstraints();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Expression c : constraints) if (c!=null) try {sb.append(c+"\n");} catch (Exception e) {}
		return sb.toString();
	}
	
	public List<IdExpression> getParametersAsList() {
		return new ArrayList<IdExpression>(parameters);
	}
	
	public Expression toSingleExpression() {
		//List<Expression> c = new ArrayList<Expression>();
		//for (Expression e : constraints) if (e!=null) c.add(e);
		List<Expression> c = constraints;
		Expression result=null;
		try {
			result=	(c==null || c.isEmpty() ? 
						BoolType.TRUE_CONST : 
							(c.size()==1 ? 
									c.get(0) : 
										AndExpression.makeAndExpression(c)));
		} catch (Error e) {
			System.err.println("Model: "+this.toString()+"\n"+checkConsistency());
			e.printStackTrace();
		}
		return result;
	}
	
	public static Expression toSingleExpression(List<Expression> c) {
		Expression result=null;
		try {
			result=	(c==null || c.isEmpty() ? 
						BoolType.TRUE_CONST : 
							(c.size()==1 ? 
									c.get(0) : 
										AndExpression.makeAndExpression(c)));
		} catch (Error e) {e.printStackTrace();}
		return result;
	}
	
	/** 
	 * 
	 * @param begin the index of the initial constraint in the list
	 * @param end the index of the final constraint in the list
	 * @return all the constraints between begin (included) and end (excluded) in a single expression (in conjunction)
	 */
	public Expression toSingleExpression(int begin, int end) {
		if (begin>=constraints.size()) return null;
		List<Expression> c = constraints.subList(begin, Math.min(end, constraints.size()));
		Expression result=null;
		try {
			result=	(c==null || c.isEmpty() ? 
						BoolType.TRUE_CONST : 
							(c.size()==1 ? 
									c.get(0) : 
										AndExpression.makeAndExpression(c)));
		} catch (Error e) {
			System.err.println("Model: "+this.toString()+"\n"+checkConsistency());
			e.printStackTrace();
		}
		return result;
	}
	
	public String toSingleString() {
		if (constraints.size()==0) return "";
		StringBuilder sb = new StringBuilder();
		sb.append(constraints.get(0));
		for (int i=1; i<constraints.size(); i++) if (constraints.get(i)!=null) sb.append(") and ("+constraints.get(i).accept(new ToString()));
		return "("+sb.toString()+")";
	}
	
	public IdExpression getParameter(int n) {
		int i=0;
		for (IdExpression e : parameters) if (i++==n) return e;
		return null;
	}
	public int getParameterPosition(IdExpression id) {
		int i=0;
		for (IdExpression e : parameters) {if (e==id) return i; i++;}
		return -1;
	}
	
	public boolean checkConsistency() {
		for (Expression e: constraints) if (e==null) return false; return true;
	}
	
	public double getAverageConstraintSize() {
		return (double)getTotalLiterals()/(double)constraints.size();
	}
	
	public int getTotalLiterals() {
		int sum=0;
		for (Expression e : constraints) sum+=e.accept(LiteralCounter.instance);
		return sum;
	}
	
	public int getTotalOperators() {
		int sum=0;
		for (Expression e : constraints) sum+=e.accept(OperatorCounter.instance);
		return sum +1; // the big end		
	}
	
	public int getTotalNodes() {
		return getTotalLiterals()+getTotalOperators();
	}
	
	public int getTotalChars() {
		int sum=0; for (Expression e : constraints) sum+=e.toString().length(); return sum;
	}
	
	public String getStatistics() {
		return constraints.size() +";"+ getTotalChars() + ";" + getTotalNodes() + ";" + getTotalOperators() + ";" + getTotalLiterals() +";" + getAverageConstraintSize();
	}
	
	public void computeParametersFromConstraints() {
		parameters = new HashSet<>(IDExprCollector.collectIds(constraints));
	}
	
	/** removes constraints that are set to null */
	public void cleanFromNullConstraints() {
		for (int i=0; i<constraints.size(); i++) if (constraints.get(i)==null) constraints.remove(i--);
	}
	
}
