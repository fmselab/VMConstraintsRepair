package constraintsmanipulation.sat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.FIC;
import constraintsmanipulation.model.FICType;
import constraintsmanipulation.visitor.ToAssignment;
import tgtlib.definitions.expression.AndExpression;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;
import tgtlib.definitions.expression.NotExpression;

/**
 * The Class FaultGen.
 *
 * @author Marco
 */
public class FICGenerator {
	
	private Set<Map<Integer,Boolean>> generatedAlphas;
	
	private Configuration c;
	private int usedFaults=0;
	
	public long TIMEOUT=20000;
	
	/** The starting time of a process */
	private long start;
	
	private YicesJNA y;
	
	public FICGenerator(Configuration c) {
		this.c=c;
		generatedAlphas = new HashSet<>();
		y = new YicesJNA(c.model.parameters);
	}
	
	public FICGenerator(Configuration c, int timeout) {
		this(c);
		this.TIMEOUT=timeout;
	}
	
	private boolean isTimeout() {return TIMEOUT>0 && Calendar.getInstance().getTimeInMillis()>start+TIMEOUT;}
	
	/** Generate a random fault of a random size, that has not been returned yet */
	public FIC generateFIC(boolean unique) {
		start = Calendar.getInstance().getTimeInMillis();
		if (c.faults!=null && usedFaults<c.faults.size()) {
			generatedAlphas.add(asAssignment(c.faults.get(usedFaults).getAlpha()));
			return c.faults.get(usedFaults++);
		}
		Map<Integer,Boolean> m;
		Expression alpha;
		do {
			if (isTimeout()) return null;
			//c.fault.alpha= AndExpression.mkBinExpr(e=randomAssignment(c),Operator.AND,AndExpression.makeAndExpression(c.model.constraints));
			alpha = toExpression(m=randomAssignment());
			//System.out.println(alpha);
		} while (unique && generatedAlphas.contains(m));
		generatedAlphas.add(m);
		//System.out.println("OK:"+ alpha);
		FICType ft = FICType.getRandomFault();
		if (!SATUtils.canBeFICType(c.model,alpha,ft,y)) ft = FICType.getOtherFault(ft);
		return new FIC(alpha, ft);
	}
	
	/** Generates a fault of size n: null if it cannot find one (TIMEOUT) */
	public FIC generateFIC(int n, boolean unique) {
		start = Calendar.getInstance().getTimeInMillis();
		Map<Integer,Boolean> m;
		Expression alpha;
		do {
			if (isTimeout()) return null;
			alpha = toExpression(m=randomAssignment(n));
		} while (unique && generatedAlphas.contains(m));
		generatedAlphas.add(m);
		FICType ft = FICType.getRandomFault();
		if (!SATUtils.canBeFICType(c.model,alpha,ft,y)) ft = FICType.getOtherFault(ft);
		return new FIC(alpha, ft);
	}
	
	/** Sets timeout and generate fault */
	public FIC generateFIC(FICType ft, int size, long timeout, boolean unique) {
		this.TIMEOUT=timeout;
		return generateFIC(ft, size, unique);
	}
	
	/** Generates a fault of type ft and of size n, null if it cannot find any (TIMEOUT) */
	public FIC generateFIC(FICType ft, int n, boolean unique) {
		start = Calendar.getInstance().getTimeInMillis();
		Map<Integer,Boolean> m;
		Expression alpha;
		do {
			if (isTimeout()) return null;
			alpha = toExpression(m=randomAssignment(n));
		} while ((unique && generatedAlphas.contains(m)) || !SATUtils.canBeFICType(c.model,alpha, ft, y));
		generatedAlphas.add(m);
		return new FIC(alpha, ft);
	}
	
	/** Generate faults of Type "AND": TODO */
	public FIC generateAndFIC() {
		start = Calendar.getInstance().getTimeInMillis();
		Map<Integer,Boolean> m = null;
		
		//int n = (int)(Math.random()*c.model.constraints.size());
		Map<IdExpression,Boolean> alpha = Yices.getInstance().getSAT(c.model.toSingleExpression());
		
		
		//TODO
		generatedAlphas.add(m);
		return new FIC(toExpression(asAssignmentPos(alpha)), FICType.AND);
	}
	
	public Map<IdExpression,Boolean> asAssignmentParam(Map<Integer,Boolean> assignment) {
		Map<IdExpression,Boolean> m = new HashMap<>();
		for (Entry<Integer,Boolean> e : assignment.entrySet()) m.put(c.model.getParameter(e.getKey()), e.getValue());
		return m;
	}
	public Map<Integer,Boolean> asAssignmentPos(Map<IdExpression,Boolean> assignment) {
		Map<Integer,Boolean> m = new HashMap<>();
		for (Entry<IdExpression,Boolean> e : assignment.entrySet()) m.put(c.model.getParameterPosition(e.getKey()), e.getValue());
		return m;
	}
	
	public Expression toExpression(Map<Integer,Boolean> assignment) {
		List<Expression> terms = new ArrayList<>();
		List<IdExpression> params = new ArrayList<>(c.model.parameters);
		for (Entry<Integer, Boolean> p : assignment.entrySet()) terms.add(!p.getValue() ? NotExpression.createNotExpression(params.get(p.getKey())) : params.get(p.getKey()));
		return AndExpression.makeAndExpression(terms);
	}
	
	private Map<Integer,Boolean> randomAssignment() {
		Map<Integer,Boolean> assignment = new HashMap<Integer,Boolean>();
		int n = (int)(Math.random()*(c.model.parameters.size()))+1; //c.model.parameters.size(); // (int)(Math.random()*(c.model.parameters.size()))+1;
		int parameters=0;
		while (parameters<n) {
			int j = (int) (Math.random()*(c.model.parameters.size()));
			if (!assignment.containsKey(j)) {
				assignment.put(j, Math.random()<0.5);
				parameters++;
			}
		}
		return assignment;
	}
	
	private Map<Integer,Boolean> randomAssignment(int n) {
		Map<Integer,Boolean> assignment = new HashMap<Integer,Boolean>();
		int parameters=0;
		while (parameters<n) {
			int j = (int) (Math.random()*(c.model.parameters.size()));
			if (!assignment.containsKey(j)) {
				assignment.put(j, Math.random()<0.5);
				parameters++;
			}
		}
		return assignment;
	}
	
	
	public Map<Integer,Boolean> asAssignment(Expression e) {
		return e.accept(new ToAssignment(c.model.getParametersAsList()));
	}

	/*private static Expression randomAssignment(Configuration c) {
		List<Expression> exps = new ArrayList<>();
		for (IdExpression e : c.model.parameters) if (exps.size()==0 || Math.random()<0.8) exps.add(Math.random()<0.5 ? NotExpression.createNotExpression(e) : e);
		return AndExpression.makeAndExpression(exps);
	}*/
	
	/** Handful method to set the timeout and the return the object itself */
	public FICGenerator setTimeout(long timeout) {this.TIMEOUT=timeout; return this;}

}
