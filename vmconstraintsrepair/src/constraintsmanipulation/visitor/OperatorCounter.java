/*
 * 
 */
package constraintsmanipulation.visitor;

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
public class OperatorCounter implements ExpressionVisitor<Integer> {
	
	public static OperatorCounter instance = new OperatorCounter();

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forIdExpression(tgtlib.definitions.expression.IdExpression)
	 */
	@Override
	public Integer forIdExpression(IdExpression idExpression) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forPrimedIdExpression(tgtlib.definitions.expression.PrimedIdExpression)
	 */
	@Override
	public Integer forPrimedIdExpression(PrimedIdExpression primedIdExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNextExpression(tgtlib.definitions.expression.NextExpression)
	 */
	@Override
	public Integer forNextExpression(NextExpression nextExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forFunctionTerm(tgtlib.definitions.expression.FunctionTerm)
	 */
	@Override
	public Integer forFunctionTerm(FunctionTerm ft) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forAndExpression(tgtlib.definitions.expression.AndExpression)
	 */
	@Override
	public Integer forAndExpression(AndExpression andExpression) {
		return andExpression.getFirstOperand().accept(this) + andExpression.getSecondOperand().accept(this) + 1;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forOrExpression(tgtlib.definitions.expression.OrExpression)
	 */
	@Override
	public Integer forOrExpression(OrExpression orExpression) {
		return orExpression.getFirstOperand().accept(this) + orExpression.getSecondOperand().accept(this) + 1;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forXOrExpression(tgtlib.definitions.expression.XOrExpression)
	 */
	@Override
	public Integer forXOrExpression(XOrExpression xOrExpression) {
		return xOrExpression.getFirstOperand().accept(this) + xOrExpression.getSecondOperand().accept(this) + 1;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNotExpression(tgtlib.definitions.expression.NotExpression)
	 */
	@Override
	public Integer forNotExpression(NotExpression notExpression) {
		return notExpression.getOperand().accept(this) + 1;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forImpliesExpression(tgtlib.definitions.expression.ImpliesExpression)
	 */
	@Override
	public Integer forImpliesExpression(ImpliesExpression impliesExpression) {
		return impliesExpression.getFirstOperand().accept(this) + impliesExpression.getSecondOperand().accept(this) + 1;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forGreaterEqualExpression(tgtlib.definitions.expression.GreaterEqualExpression)
	 */
	@Override
	public Integer forGreaterEqualExpression(GreaterEqualExpression greaterEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forEqualsExpression(tgtlib.definitions.expression.EqualsExpression)
	 */
	@Override
	public Integer forEqualsExpression(EqualsExpression equalsExpression) {
		return equalsExpression.getFirstOperand().accept(this) + equalsExpression.getSecondOperand().accept(this);
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forGreaterThanExpression(tgtlib.definitions.expression.GreaterThanExpression)
	 */
	@Override
	public Integer forGreaterThanExpression(GreaterThanExpression greaterThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forLessEqualExpression(tgtlib.definitions.expression.LessEqualExpression)
	 */
	@Override
	public Integer forLessEqualExpression(LessEqualExpression lessEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forLessThanExpression(tgtlib.definitions.expression.LessThanExpression)
	 */
	@Override
	public Integer forLessThanExpression(LessThanExpression lessThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNotEqualsExpression(tgtlib.definitions.expression.NotEqualsExpression)
	 */
	@Override
	public Integer forNotEqualsExpression(NotEqualsExpression notEqualsExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forDivExpression(tgtlib.definitions.expression.DivExpression)
	 */
	@Override
	public Integer forDivExpression(DivExpression divExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forPlusExpression(tgtlib.definitions.expression.PlusExpression)
	 */
	@Override
	public Integer forPlusExpression(PlusExpression plusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forMinusExpression(tgtlib.definitions.expression.MinusExpression)
	 */
	@Override
	public Integer forMinusExpression(MinusExpression minusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forMultExpression(tgtlib.definitions.expression.MultExpression)
	 */
	@Override
	public Integer forMultExpression(MultExpression multExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNegExpression(tgtlib.definitions.expression.NegExpression)
	 */
	@Override
	public Integer forNegExpression(NegExpression negExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forModuloExpression(tgtlib.definitions.expression.ModuloExpression)
	 */
	@Override
	public Integer forModuloExpression(ModuloExpression moduloExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forCaseExpression(tgtlib.definitions.expression.CaseExpression)
	 */
	@Override
	public Integer forCaseExpression(CaseExpression caseExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer forConditionalExpression(CondExpression cond) {
		// TODO Auto-generated method stub
		return null;
	}

}
