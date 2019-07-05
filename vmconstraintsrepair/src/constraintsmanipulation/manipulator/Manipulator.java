package constraintsmanipulation.manipulator;

import java.util.Calendar;

import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.FICType;
import constraintsmanipulation.simplifier.Simplifier;

public abstract class Manipulator {
	
	protected Simplifier simplifier;
	public Simplifier getSimplifier() {return simplifier;}
	public Manipulator setSimplifier(Simplifier simplifier) {this.simplifier=simplifier; return this;}
	
	protected abstract Configuration repairAnd(Configuration conf);
	protected abstract Configuration repairOr(Configuration conf);
	
	protected TimeStats timeStats;
	
	/** Performs the selection and simplification, producing a repaired configuration.
	 * @param conf the original configuration, containing the single fault Alpha to be repaired
	 * @return the configuration after the repairing process
	 */
	public Configuration repair(Configuration conf) {
		Configuration c = conf.clone();
		timeStats = new TimeStats();
		long start = Calendar.getInstance().getTimeInMillis();
		if (conf.fic.getFaultType()==FICType.OR) { // case OR: model overconstraining 
			c = repairOr(c);
		} else {  // case AND: model underconstraining
			c = repairAnd(c);
		}
		timeStats.setTimeTotal(Calendar.getInstance().getTimeInMillis()-start);
		return c;
	}
	
	public String getSimplifierName() {return simplifier==null?"Naive":simplifier.getName();}
	
	public TimeStats getTimeStats() {return timeStats;}
	
	public String toString() {
		return getSimplifierName();
	}
}
