/*
 * 
 */
package constraintsmanipulation.visitor;

import java.util.HashSet;
import java.util.Set;

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
public class FeatureCollector implements ExpressionVisitor<Set<IdExpression>> {

	Set<IdExpression> features;
	public FeatureCollector() {
		features = new HashSet<>();
	}
	
	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forIdExpression(tgtlib.definitions.expression.IdExpression)
	 */
	@Override
	public Set<IdExpression> forIdExpression(IdExpression idExpression) {
		if (!features.contains(idExpression)) features.add(idExpression);
		return features;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forPrimedIdExpression(tgtlib.definitions.expression.PrimedIdExpression)
	 */
	@Override
	public Set<IdExpression> forPrimedIdExpression(PrimedIdExpression primedIdExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNextExpression(tgtlib.definitions.expression.NextExpression)
	 */
	@Override
	public Set<IdExpression> forNextExpression(NextExpression nextExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forFunctionTerm(tgtlib.definitions.expression.FunctionTerm)
	 */
	@Override
	public Set<IdExpression> forFunctionTerm(FunctionTerm ft) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forAndExpression(tgtlib.definitions.expression.AndExpression)
	 */
	@Override
	public Set<IdExpression> forAndExpression(AndExpression andExpression) {
		andExpression.getFirstOperand().accept(this);
		andExpression.getSecondOperand().accept(this);
		return features;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forOrExpression(tgtlib.definitions.expression.OrExpression)
	 */
	@Override
	public Set<IdExpression> forOrExpression(OrExpression orExpression) {
		orExpression.getFirstOperand().accept(this);
		orExpression.getSecondOperand().accept(this);
		return features;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forXOrExpression(tgtlib.definitions.expression.XOrExpression)
	 */
	@Override
	public Set<IdExpression> forXOrExpression(XOrExpression xOrExpression) {
		xOrExpression.getFirstOperand().accept(this);
		xOrExpression.getSecondOperand().accept(this);
		return features;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNotExpression(tgtlib.definitions.expression.NotExpression)
	 */
	@Override
	public Set<IdExpression> forNotExpression(NotExpression notExpression) {
		return notExpression.getOperand().accept(this);
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forImpliesExpression(tgtlib.definitions.expression.ImpliesExpression)
	 */
	@Override
	public Set<IdExpression> forImpliesExpression(ImpliesExpression impliesExpression) {
		impliesExpression.getFirstOperand().accept(this);
		impliesExpression.getSecondOperand().accept(this);
		return features;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forGreaterEqualExpression(tgtlib.definitions.expression.GreaterEqualExpression)
	 */
	@Override
	public Set<IdExpression> forGreaterEqualExpression(GreaterEqualExpression greaterEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forEqualsExpression(tgtlib.definitions.expression.EqualsExpression)
	 */
	@Override
	public Set<IdExpression> forEqualsExpression(EqualsExpression equalsExpression) {
		equalsExpression.getFirstOperand().accept(this);
		equalsExpression.getSecondOperand().accept(this);
		return features;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forGreaterThanExpression(tgtlib.definitions.expression.GreaterThanExpression)
	 */
	@Override
	public Set<IdExpression> forGreaterThanExpression(GreaterThanExpression greaterThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forLessEqualExpression(tgtlib.definitions.expression.LessEqualExpression)
	 */
	@Override
	public Set<IdExpression> forLessEqualExpression(LessEqualExpression lessEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forLessThanExpression(tgtlib.definitions.expression.LessThanExpression)
	 */
	@Override
	public Set<IdExpression> forLessThanExpression(LessThanExpression lessThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNotEqualsExpression(tgtlib.definitions.expression.NotEqualsExpression)
	 */
	@Override
	public Set<IdExpression> forNotEqualsExpression(NotEqualsExpression notEqualsExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forDivExpression(tgtlib.definitions.expression.DivExpression)
	 */
	@Override
	public Set<IdExpression> forDivExpression(DivExpression divExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forPlusExpression(tgtlib.definitions.expression.PlusExpression)
	 */
	@Override
	public Set<IdExpression> forPlusExpression(PlusExpression plusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forMinusExpression(tgtlib.definitions.expression.MinusExpression)
	 */
	@Override
	public Set<IdExpression> forMinusExpression(MinusExpression minusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forMultExpression(tgtlib.definitions.expression.MultExpression)
	 */
	@Override
	public Set<IdExpression> forMultExpression(MultExpression multExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNegExpression(tgtlib.definitions.expression.NegExpression)
	 */
	@Override
	public Set<IdExpression> forNegExpression(NegExpression negExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forModuloExpression(tgtlib.definitions.expression.ModuloExpression)
	 */
	@Override
	public Set<IdExpression> forModuloExpression(ModuloExpression moduloExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forCaseExpression(tgtlib.definitions.expression.CaseExpression)
	 */
	@Override
	public Set<IdExpression> forCaseExpression(CaseExpression caseExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IdExpression> forConditionalExpression(CondExpression cond) {
		// TODO Auto-generated method stub
		return null;
	}

}
