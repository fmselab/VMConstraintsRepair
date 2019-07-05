package constraintsmanipulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constraintsmanipulation.distance.BFCD;
import constraintsmanipulation.distance.BFED;
import constraintsmanipulation.manipulator.Manipulator;
import constraintsmanipulation.manipulator.Mutation;
import constraintsmanipulation.manipulator.TimeStats;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.FICType;
import constraintsmanipulation.visitor.LiteralCounter;
import tgtlib.definitions.expression.Expression;

public class Stats {
	public static String sep = ",";
	public static final String HEADER = "step,method,type,flit,BFED,BFCD,constraints,literals,timeSelection,timeSimplification,timeTotal";
	public static final String HEADER_EXP2 = "id,step,method,type,mutation,flit,BFED,BFCD,constraints,literals,timeSelection,timeSimplification,timeTotal,sizes,sizew";

	public List<Record> records;
	public Configuration repairedModel;
	public Mutation mutation;
	public Manipulator manipulator;
	int id;
	
	public Stats() {
		records = new ArrayList<>();
	}
	
	public void setIdAndMutationAndManipulator(int id, Mutation mutation, Manipulator manipulator) {
		this.id=id;
		this.mutation=mutation;
		this.manipulator=manipulator;
		for (Record r : records) {
			r.numRepExp2=id;
			r.mutation = mutation;
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Record r : records) sb.append(r+"\n");
		return sb.toString();
	}
	
	public String toStringForExp2() {
		if (getMethod()==null) return "";
		return id
			+sep+getSteps()
			+sep+getMethod().getSimplifierName()
			+sep+getCountAnd()
			+sep+mutation.name()
			+sep+getAvgFICSize()
			+sep+getBFED()
			+sep+getBFCD()
			+sep+getAvgConstraints()
			+sep+getAvgLiterals()
			+sep+getAvgTimeSelection()
			+sep+getAvgTimeSimplification()
			+sep+getAvgTime()
			+sep+getAvgFICSizeByType()[0]
			+sep+getAvgFICSizeByType()[1];
	}
	
	public String toStringForExp2Detail() {
		StringBuilder sb = new StringBuilder();
		for (Record r : records) sb.append(r.toStringForExp2()+"\n");
		return sb.toString();
	}
	
	public int getSteps() {
		return records.size();
	}
	
	public int getAvgSteps() {
		if (records.size()==0) return 0;
		Map<Integer,Integer> steps = new HashMap<>();
		for (Record r : records) steps.put(r.numRepExp2, steps.getOrDefault(r.numRepExp2,0)+1);
		int avg=0;
		for (Integer d : steps.values()) avg+=d;
		return avg/steps.size();
	}
	
	public Manipulator getMethod() {return records.size()==0 ? null : records.get(0).m;}
	public int getCountAnd() {
		/*Map<Integer,Integer> ands = new HashMap<>();
		for (Record r : records) ands.put(r.numRepExp2, r.type==FICType.AND ? 1 : 0);
		int avg=0; for (Integer d : ands.values()) avg+=d; return avg/ands.values().size();*/
		int count=0; for (Record r : records) if (r.type==FICType.AND) count++; return count;
	}
	public ArrayList<Integer> getFICLiterals() {
		ArrayList<Integer> flit = new ArrayList<>();
		for (Record r : records) flit.add(r.ficSize);
		return flit;
	}
	
	public double getAvgFICSize() {
		/*Map<Integer,Integer> flits = new HashMap<>();
		for (Record r : records) flits.put(r.numRepExp2, r.ficSize);
		int avg=0;
		for (Integer d : flits.values()) avg+=d;
		return avg/flits.values().size();*/
		double avg=0; for (Record r : records) avg+=r.ficSize; return avg / (double) getSteps();
	}
	public double[] getAvgFICSizeByType() {
		double avgS=0, avgW=0, countS=0, countW=0; 
		for (Record r : records) {
			if (r.type==FICType.AND) {avgS+=r.ficSize; countS++;}
			else {avgW+=r.ficSize; countW++;}
		}
		return new double[] {avgS / (double)countS, avgW / (double)countW};
	}
	
	public double getAvgLiterals() {
		double avg=0;
		for (Record r : records) avg+=r.literals;
		return avg / (double) getSteps();
	}
	public double getAvgConstraints() {
		double avg=0;
		for (Record r : records) avg+=r.constraints;
		return avg / (double) getSteps();
	}
	public double getBFED() {
		//return records.size()==0 ? 0 : records.get(records.size()-1).bfed;
		if (records.size()==0) return 0;
		Map<Integer,Double> bfeds = new HashMap<>();
		for (Record r : records) bfeds.put(r.numRepExp2, r.bfed);
		double avg=0;
		for (Double d : bfeds.values()) avg+=d;
		return avg/bfeds.size();
	}
	public double getBFCD() {
		if (records.size()==0) return 0;
		Map<Integer,Double> bfcds = new HashMap<>();
		for (Record r : records) bfcds.put(r.numRepExp2, r.bfcd);
		double avg=0;
		for (Double d : bfcds.values()) avg+=d;
		return avg/bfcds.size();
	}
	public double getAvgTimeSelection() {
		double avg=0;
		for (Record r : records) avg+=r.t.getTimeSelection();
		return avg / (double) getSteps();
	}
	public double getAvgTimeSimplification() {
		double avg=0;
		for (Record r : records) avg+=r.t.getTimeSimplification();
		return avg / (double) getSteps();
	}
	public double getAvgTime() {
		Map<Integer,Double> times = new HashMap<>();
		for (Record r : records) times.put(r.numRepExp2, times.getOrDefault(r.numRepExp2, 0.0)+r.t.getTimeTotal());
		double avg=0;
		for (Double d : times.values()) avg+=d;
		return avg / (double) times.values().size();
	}
	
	public static class Record {
		int step;
		Manipulator m;
		int ficSize;
		FICType type;
		double bfed;
		double bfcd;
		TimeStats t;
		int literals;
		int constraints;
		int numRepExp2;
		Mutation mutation;
		
		public Record(int step, Manipulator m, Expression fic, FICType type, Configuration model, Configuration oracle) {
			this.step=step;
			this.m=m;
			ficSize = fic.accept(LiteralCounter.instance);
			this.type=type;
			bfed = BFED.instance.computeEditDistance(model.model, oracle.model);
			bfcd = BFCD.instance.getDistance(model, oracle);
			t = m.getTimeStats();
			this.constraints = model.model.constraints.size();
			literals=0;
			for (Expression e : model.model.constraints) literals += e.accept(LiteralCounter.instance);
		}
		
		public Record(int step, Manipulator m, Expression fic, FICType type, Configuration model, Configuration oracle, boolean calculateBFED) {
			this.step=step;
			this.m=m;
			ficSize = fic.accept(LiteralCounter.instance);
			this.type=type;
			if (calculateBFED) {
				bfed = BFED.instance.computeEditDistance(model.model, oracle.model);
			}
			bfcd = BFCD.instance.getDistance(model, oracle);
			t = m.getTimeStats();
			this.constraints = model.model.constraints.size();
			literals=0;
			for (Expression e : model.model.constraints) literals += e.accept(LiteralCounter.instance);
		}
		
		public void computeBFEDandBFCD(Configuration model, Configuration oracle) {
			bfed = BFED.instance.computeEditDistance(model.model, oracle.model);
			bfcd = BFCD.instance.getDistance(model, oracle);
		}
		
		public String toStringForExp2() {
			return numRepExp2+sep+step+sep+m.getSimplifierName()+sep+type+sep+mutation.name()+sep+ficSize+sep+bfed+sep+bfcd+sep+constraints+sep+literals+sep+t.getTimeSelection()+sep+t.getTimeSimplification()+t.getTimeTotal();
		}

		public Record(int step, Manipulator m, Expression fic, FICType type, Configuration model, Configuration oracle, int numRepExp2, Mutation mutation) {
			this(step, m, fic, type, model, oracle);
			this.numRepExp2=numRepExp2;
			this.mutation=mutation;
		}
		
		public String toString() {
			return step+sep+m.getSimplifierName()+sep+type+sep+ficSize+sep+bfed+sep+bfcd+sep+constraints+sep+literals+sep+t.getTimeSelection()+sep+t.getTimeSimplification()+sep+t.getTimeTotal();
		}
	}
}
