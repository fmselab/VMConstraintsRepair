package constraintsmanipulation.distance;

import org.apache.log4j.Logger;

import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.Model;
import tgtlib.definitions.expression.Expression;

/**
 * The Class DistanceCriterion.
 * @author marcoradavelli
 */
public abstract class DistanceCriterion {
	
	static final protected Logger LOG = Logger.getLogger(DistanceCriterion.class);
	//static public int step = 500; // The BFED is too hard to compute if I don't split the model into pieces of STEP constraints
	
	public abstract double getDistance(Expression e1, Expression e2);
	
	public double getDistance(Model m1, Model m2) {
		LOG.debug("Computing distance between models...");
		// OutOfMemoryError: Java heap space (also over 4GB, it crashes with FreeBSD!!!)
		return getDistance(m1.toSingleExpression(), m2.toSingleExpression());
		/*double d = 0;
		//for (int i=0; i<m1.constraints.size(); i++) d += getDistance(m1.constraints.get(i), i<m2.constraints.size() ? m2.constraints.get(i) : null);
		//for (int i=m1.constraints.size(); i<m2.constraints.size(); i++) d += getDistance(null, m2.constraints.get(i));
		for (int i=0; i<m1.constraints.size() || i<m2.constraints.size(); i+=step) {
			d += getDistance(m1.toSingleExpression(i, i+step), m2.toSingleExpression(i, i+step));
			LOG.debug(i);
		}
		return d;*/
	}
	
	public double getDistance(Configuration c1, Configuration c2) {
		return getDistance(c1.model,c2.model);
	}
	
	public abstract String getName();
}
