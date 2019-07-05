package constraintsmanipulation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import constraintsmanipulation.visitor.ToCombination;
import tgtlib.definitions.expression.AndExpression;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;
import tgtlib.definitions.expression.NotExpression;

public class Combination {
	private Map<IdExpression, Boolean> map;
	
	public Combination(Expression combExpression) {
		map = combExpression.accept(new ToCombination());
	}
	
	public Combination(Combination c) {
		map = new HashMap<>(c.getMap());
	}
	
	public Combination(Map<IdExpression,Boolean> map) {
		this.map=map;
	}
	
	public Map<IdExpression, Boolean> getMap() {return map;}
	
	public static Expression toExpr(Map<IdExpression,Boolean> assignment) {
		if (assignment==null || assignment.isEmpty()) return null;
		List<Expression> exps = new ArrayList<>();
		for (Entry<IdExpression, Boolean> a : assignment.entrySet()) {
			exps.add(a.getValue() ? a.getKey() : NotExpression.createNotExpression(a.getKey()));
		}
		return AndExpression.makeAndExpression(exps);
	}
	
	public Expression toExpr() {
		return toExpr(map);
	}
}
