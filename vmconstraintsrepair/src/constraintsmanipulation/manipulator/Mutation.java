package constraintsmanipulation.manipulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.Difference;
import tgtlib.definitions.expression.Expression;

public enum Mutation {
	RC {

		@Override
		public Expression mutateExpression(Configuration c, Expression e) {
			return null;
		}
		
	}, RL {

		@Override
		public Expression mutateExpression(Configuration c, Expression e) {
			MutationVisitor mut = new MutationVisitor(this, c, 1);
			Expression e2 = null;
			while (!mut.mutationPerformed()) e2 = e.accept(mut);
			return e2;
		}

		
	}, SL {

		@Override
		public Expression mutateExpression(Configuration c, Expression e) {
			MutationVisitor mut = new MutationVisitor(this, c, 1);
			Expression e2 = null;
			while (!mut.mutationPerformed()) e2 = e.accept(mut);
			return e2;
		}
		
	};
	
	public Configuration mutate(final Configuration conf) {
		Configuration c = conf.clone();
		int idConstraintToMutate = (int)(Math.random()*c.model.constraints.size());
		//Mutation m = Mutation.values()[(int)(Math.random()*Mutation.values().length)];
		Expression mutated = mutateExpression(c, c.model.constraints.get(idConstraintToMutate));
		if (mutated==null) c.model.constraints.remove(idConstraintToMutate);
		else c.model.constraints.set(idConstraintToMutate, mutated);
		System.out.println(c.model.checkConsistency());
		return c;
	}
	
	public abstract Expression mutateExpression(Configuration c, Expression e);
	
	public static Difference computeDifference(Configuration a, Configuration b) {
		Map<Integer,Expression> c = new HashMap<>();
		Map<Integer,Expression> c1=new HashMap<>(), c2=new HashMap<>(); // Expressions which are different in c1 and c2
		List<Integer> eq = new ArrayList<>();
		for (int i=0; i<a.model.constraints.size(); i++) {
			boolean found=false;
			String s = a.model.constraints.get(i).toString();
			for (int j=0; j<b.model.constraints.size(); j++) if (s.equals(b.model.constraints.get(j).toString())) {eq.add(j); found=true; break;}
			if (found) c.put(i, a.model.constraints.get(i));
			else c1.put(i, a.model.constraints.get(i));
		}
		for (int i=0; i<b.model.constraints.size(); i++) if (!eq.contains(i)) c2.put(i, b.model.constraints.get(i)); // add the unique constraints of C2
		return new Difference(c,c1,c2);
	}
	
	public static Mutation getRandomMutation() {
		return Mutation.values()[(int)(Math.random()*Mutation.values().length)];
	}
}
