package constraintsmanipulation.manipulator;

import java.util.ArrayList;
import java.util.List;

import constraintsmanipulation.distance.DistanceCriterion;
import constraintsmanipulation.distance.SimplificationDistance;
import constraintsmanipulation.distance.SyntaxTreeDistance;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.Formula;
import constraintsmanipulation.simplifier.BestSimplifier;
import tgtlib.definitions.expression.AndExpression;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.NotExpression;

public enum SelectorSingleAnd {
	
	BEST_LEV {
		@Override
		int select(Configuration c) {
			return selectBest(c, SyntaxTreeDistance.getInstance());
		}
	},

	BEST_SIMPL {
		@Override
		int select(Configuration c) {
			return selectBest(c, SimplificationDistance.getInstance());
		}
	},
	
	BEST_CPS_RANDOM {
		@Override
		int select(Configuration c) {
			List<Integer> l = selectBestCPS(c);
			if (l==null || l.isEmpty()) return -1; // it has to be inserted at the end
			return l.get((int)(Math.random()*(l.size()+1))-1); // it can be negative: in this case, it is added as a new constraint
		}
	},
	
	BEST_CPS_FIRST {
		@Override
		int select(Configuration c) {
			List<Integer> l = selectBestCPS(c);
			if (l==null || l.isEmpty()) return -1; // it has to be inserted at the end
			return l.get(0);
		}
	};
	
	abstract int select(Configuration c);
	
	List<Integer> selectBestCPS(Configuration c) {
		ArrayList<Integer> best = new ArrayList<Integer>();
		int bestAffinity=0;
		int n;
		for (int i=0; i<c.model.constraints.size(); i++) if ((n=Formula.getAffinity(c.model.constraints.get(i), c.fic.getAlpha()))>=bestAffinity) {
			if (n>bestAffinity) {best.clear(); bestAffinity=n;}
			best.add(i);
		}
		return best;
	}	
	
	int selectBest(Configuration c, DistanceCriterion dc) {
		BestSimplifier simpl = new BestSimplifier(dc);
		double dist=dc.getDistance(null, NotExpression.createNotExpression(c.fic.getAlpha()));
		double t;
		int id=-1;
		for (int i=0; i<c.model.constraints.size(); i++) if (dist > (t=dc.getDistance(simpl.simplify(repairSingleExpressionAnd(c.model.constraints.get(i), c.fic.getAlpha())), c.model.constraints.get(i)))) {
			id = i;
			dist = t;
		}
		return id;
	}
	
	public static Expression repairSingleExpressionAnd(Expression e, Expression alpha) {
		return new AndExpression(e, NotExpression.createNotExpression(alpha));
	}

}
