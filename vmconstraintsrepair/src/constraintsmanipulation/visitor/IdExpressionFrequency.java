/*
 * 
 */
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

// TODO: Auto-generated Javadoc
/**
 * The Class IdExpressionFrequency.
 */
public class IdExpressionFrequency implements ExpressionVisitor<Map<IdExpression,Integer>> {

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forIdExpression(tgtlib.definitions.expression.IdExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forIdExpression(IdExpression idExpression) {
		Map<IdExpression,Integer> results = new HashMap<>();
		results.put(idExpression, 1);
		return results;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forPrimedIdExpression(tgtlib.definitions.expression.PrimedIdExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forPrimedIdExpression(PrimedIdExpression primedIdExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNextExpression(tgtlib.definitions.expression.NextExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forNextExpression(NextExpression nextExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forFunctionTerm(tgtlib.definitions.expression.FunctionTerm)
	 */
	@Override
	public Map<IdExpression, Integer> forFunctionTerm(FunctionTerm ft) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forAndExpression(tgtlib.definitions.expression.AndExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forAndExpression(AndExpression andExpression) {
		Map<IdExpression, Integer> r1 = andExpression.getFirstOperand().accept(this);
		Map<IdExpression, Integer> r2 = andExpression.getSecondOperand().accept(this);
		r1.putAll(r2);
		return r1;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forOrExpression(tgtlib.definitions.expression.OrExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forOrExpression(OrExpression orExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forXOrExpression(tgtlib.definitions.expression.XOrExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forXOrExpression(XOrExpression xOrExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNotExpression(tgtlib.definitions.expression.NotExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forNotExpression(NotExpression notExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forImpliesExpression(tgtlib.definitions.expression.ImpliesExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forImpliesExpression(ImpliesExpression impliesExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forGreaterEqualExpression(tgtlib.definitions.expression.GreaterEqualExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forGreaterEqualExpression(GreaterEqualExpression greaterEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forEqualsExpression(tgtlib.definitions.expression.EqualsExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forEqualsExpression(EqualsExpression equalsExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forGreaterThanExpression(tgtlib.definitions.expression.GreaterThanExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forGreaterThanExpression(GreaterThanExpression greaterThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forLessEqualExpression(tgtlib.definitions.expression.LessEqualExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forLessEqualExpression(LessEqualExpression lessEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forLessThanExpression(tgtlib.definitions.expression.LessThanExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forLessThanExpression(LessThanExpression lessThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNotEqualsExpression(tgtlib.definitions.expression.NotEqualsExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forNotEqualsExpression(NotEqualsExpression notEqualsExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forDivExpression(tgtlib.definitions.expression.DivExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forDivExpression(DivExpression divExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forPlusExpression(tgtlib.definitions.expression.PlusExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forPlusExpression(PlusExpression plusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forMinusExpression(tgtlib.definitions.expression.MinusExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forMinusExpression(MinusExpression minusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forMultExpression(tgtlib.definitions.expression.MultExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forMultExpression(MultExpression multExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNegExpression(tgtlib.definitions.expression.NegExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forNegExpression(NegExpression negExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forModuloExpression(tgtlib.definitions.expression.ModuloExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forModuloExpression(ModuloExpression moduloExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forCaseExpression(tgtlib.definitions.expression.CaseExpression)
	 */
	@Override
	public Map<IdExpression, Integer> forCaseExpression(CaseExpression caseExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IdExpression, Integer> forConditionalExpression(CondExpression cond) {
		// TODO Auto-generated method stub
		return null;
	}

}
