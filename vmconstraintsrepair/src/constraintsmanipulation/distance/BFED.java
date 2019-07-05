package constraintsmanipulation.distance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import apted.costmodel.StringUnitCostModel;
import apted.distance.APTED;
import apted.node.Node;
import apted.node.StringNodeData;
import apted.parser.BracketStringInputParser;
import constraintsmanipulation.model.Model;
import constraintsmanipulation.visitor.ToAPTED;
import tgtlib.definitions.expression.Expression;

/**
 * 
 * @author marcoradavelli
 *
 */
public class BFED extends DistanceCriterion {
	
	public static BFED instance = new BFED();
	
	protected double CD = 1;
	protected double CI = 1;
	protected double CR = 2;
		
	public double getDistance(Expression e1, Expression e2) {
		//LOG.debug("Computing BFED...");
		if (e1!=null && e2!=null && e1.equals(e2)) return 0;
		String s1 = e1==null ? "{}" : e1.accept(new ToAPTED()), s2 = e2==null ? "{}" : e2.accept(new ToAPTED());
		if (s2==null || s2.trim().equals("")) s2="{}";
		
		// Parse the input.
	    BracketStringInputParser parser = new BracketStringInputParser();
	    Node<StringNodeData> t1 = parser.fromString(s1);
	    Node<StringNodeData> t2 = parser.fromString(s2);
	    // Initialise APTED.
	    APTED<StringUnitCostModel, StringNodeData> apted = new APTED<>(new StringUnitCostModel());
	    // Although we don't need TED value yet, TED must be computed before the
	    // mapping. This cast is safe due to unit cost.
	    return apted.computeEditDistance(t1, t2);
	}
	
	/** @return the distance computed in a clever and faster way */
	public double computeEditDistance(Model a, Model b) {
		List<Expression> ca=new ArrayList<>(a.constraints), cb = new ArrayList<>(b.constraints);
		for (int i=0; i<ca.size(); i++) {
			for (int j=0; j<cb.size(); j++) {
				if (ca.get(i)!=null && ca.get(i).equals(cb.get(j))) {
					ca.remove(i--);
					cb.remove(j--);
					break;
				}
			}
		}
		ArrayList<Edge> edges = new ArrayList<>();
		for (Expression e1 : a.constraints) {
			for (Expression e2 : b.constraints) {
				edges.add(new Edge(e1,e2,(int)this.getDistance(e1, e2)));
			}
		}
		Collections.sort(edges);
		int distance=0;
		for (Edge e : edges) {
			if (ca.contains(e.a) && cb.contains(e.b)) {
				distance+=e.distance;
				ca.remove(e.a);
				cb.remove(e.b);
			}
		}
		for (Expression e : ca) distance+=getDistance(e, null);
		for (Expression e : cb) distance+=getDistance(e, null);
		
		return distance;
	}
	
	@Override
	public String getName() {return "BFED";}
	
	class Edge implements Comparable<Edge> {
		Expression a, b;
		int distance;
		public Edge(Expression a, Expression b, int distance) {
			this.a=a; this.b=b; this.distance=distance;
		}
		@Override
		public int compareTo(Edge o) {
			return distance-o.distance;
		}
	}
}
