package constraintsmanipulation.visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tgtlib.definitions.expression.AndExpression;
import tgtlib.definitions.expression.CaseExpression;
import tgtlib.definitions.expression.CondExpression;
import tgtlib.definitions.expression.DivExpression;
import tgtlib.definitions.expression.EqualsExpression;
import tgtlib.definitions.expression.ExpressionVisitor;
import tgtlib.definitions.expression.FunctionTerm;
import tgtlib.definitions.expression.GreaterEqualExpression;
import tgtlib.definitions.expression.GreaterThanExpression;
import tgtlib.definitions.expression.IdExpression;
import tgtlib.definitions.expression.ImpliesExpression;
import tgtlib.definitions.expression.LessEqualExpression;
import tgtlib.definitions.expression.LessThanExpression;
import tgtlib.definitions.expression.MinusExpression;
import tgtlib.definitions.expression.ModuloExpression;
import tgtlib.definitions.expression.MultExpression;
import tgtlib.definitions.expression.NegExpression;
import tgtlib.definitions.expression.NextExpression;
import tgtlib.definitions.expression.NotEqualsExpression;
import tgtlib.definitions.expression.NotExpression;
import tgtlib.definitions.expression.OrExpression;
import tgtlib.definitions.expression.PlusExpression;
import tgtlib.definitions.expression.PrimedIdExpression;
import tgtlib.definitions.expression.XOrExpression;

/**
 * Class to transform an Expression into an Assignment, i.e. a Map<Integer,Boolean> in which
 * Integer is the ID of the parameter in the "parameters" of the model
 * Boolean is true if the parameter is positive in the assignment, otherwise false (if it is negated)
 * @author Marco
 *
 */
public class ToAssignment implements ExpressionVisitor<Map<Integer,Boolean>> {

	protected Map<Integer,Boolean> assignment=new HashMap<>();
	protected List<IdExpression> ids;
	
	public ToAssignment(List<IdExpression> ids) {
		this.ids=ids;
	}
	
	@Override
	public Map<Integer, Boolean> forIdExpression(IdExpression idExpression) {
		if (ids.contains(idExpression)) assignment.put(ids.indexOf(idExpression), true);
		return assignment;
	}

	@Override
	public Map<Integer, Boolean> forPrimedIdExpression(PrimedIdExpression primedIdExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forNextExpression(NextExpression nextExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forFunctionTerm(FunctionTerm ft) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forAndExpression(AndExpression andExpression) {
		assignment = andExpression.getFirstOperand().accept(this);
		assignment = andExpression.getSecondOperand().accept(this);
		return assignment;
	}

	@Override
	public Map<Integer, Boolean> forOrExpression(OrExpression orExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forXOrExpression(XOrExpression xOrExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forNotExpression(NotExpression notExpression) {
		if (ids.contains(notExpression.getOperand())) assignment.put(ids.indexOf(notExpression.getOperand()), false);
		return assignment;
	}

	@Override
	public Map<Integer, Boolean> forImpliesExpression(ImpliesExpression impliesExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forGreaterEqualExpression(GreaterEqualExpression greaterEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forEqualsExpression(EqualsExpression equalsExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forGreaterThanExpression(GreaterThanExpression greaterThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forLessEqualExpression(LessEqualExpression lessEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forLessThanExpression(LessThanExpression lessThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forNotEqualsExpression(NotEqualsExpression notEqualsExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forDivExpression(DivExpression divExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forPlusExpression(PlusExpression plusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forMinusExpression(MinusExpression minusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forMultExpression(MultExpression multExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forNegExpression(NegExpression negExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forModuloExpression(ModuloExpression moduloExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forCaseExpression(CaseExpression caseExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Boolean> forConditionalExpression(CondExpression cond) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
