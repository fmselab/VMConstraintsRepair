package constraintsmanipulation.visitor;

import java.util.HashMap;
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
 * Class to transform an Expression into a Combination, i.e. a Map<IdExpression,Boolean> in which
 * IdExpression is the parameter in the "parameters" of the model
 * Boolean is true if the parameter is positive in the assignment, otherwise false (if it is negated)
 * 
 * Note: the Expression must be a Combination
 * @author Marco
 *
 */
public class ToCombination implements ExpressionVisitor<Map<IdExpression,Boolean>> {

	protected Map<IdExpression,Boolean> assignment=new HashMap<>();
		
	@Override
	public Map<IdExpression, Boolean> forIdExpression(IdExpression idExpression) {
		assignment.put(idExpression, true);
		return assignment;
	}

	@Override
	public Map<IdExpression, Boolean> forPrimedIdExpression(PrimedIdExpression primedIdExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forNextExpression(NextExpression nextExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forFunctionTerm(FunctionTerm ft) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forAndExpression(AndExpression andExpression) {
		assignment = andExpression.getFirstOperand().accept(this);
		assignment = andExpression.getSecondOperand().accept(this);
		return assignment;
	}

	@Override
	public Map<IdExpression, Boolean> forOrExpression(OrExpression orExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forXOrExpression(XOrExpression xOrExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forNotExpression(NotExpression notExpression) {
		assignment.put((IdExpression)notExpression.getOperand(), false);
		return assignment;
	}

	@Override
	public Map<IdExpression, Boolean> forImpliesExpression(ImpliesExpression impliesExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forGreaterEqualExpression(GreaterEqualExpression greaterEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forEqualsExpression(EqualsExpression equalsExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forGreaterThanExpression(GreaterThanExpression greaterThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forLessEqualExpression(LessEqualExpression lessEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forLessThanExpression(LessThanExpression lessThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forNotEqualsExpression(NotEqualsExpression notEqualsExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forDivExpression(DivExpression divExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forPlusExpression(PlusExpression plusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forMinusExpression(MinusExpression minusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forMultExpression(MultExpression multExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forNegExpression(NegExpression negExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forModuloExpression(ModuloExpression moduloExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forCaseExpression(CaseExpression caseExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Boolean> forConditionalExpression(CondExpression cond) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
