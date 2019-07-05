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
 * The Class ToTreeRTED.
 *
 * @author Marco
 */
public class ToTreeRTED implements ExpressionVisitor<String> {

	/**
	 * Instantiates a new to tree RTED.
	 */
	public ToTreeRTED() {}
	
	/**  1-> insideOR, 2-> insideAND, 3-> insideIMPLY. */
	public int inside;
	
	/**
	 * Instantiates a new to tree RTED.
	 *
	 * @param inside the inside
	 */
	public ToTreeRTED(int inside) {
		this.inside = inside;
	}
	
	/** The in or. */
	public static ToTreeRTED inOr = new ToTreeRTED(1);
	
	/** The in and. */
	public static ToTreeRTED inAnd = new ToTreeRTED(2);
	
	/** The in imply. */
	public static ToTreeRTED inImply = new ToTreeRTED(3);
	
	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forIdExpression(tgtlib.definitions.expression.IdExpression)
	 */
	@Override
	public String forIdExpression(IdExpression idExpression) {
		return "{"+idExpression.getIdString()+"}";
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forPrimedIdExpression(tgtlib.definitions.expression.PrimedIdExpression)
	 */
	@Override
	public String forPrimedIdExpression(PrimedIdExpression primedIdExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNextExpression(tgtlib.definitions.expression.NextExpression)
	 */
	@Override
	public String forNextExpression(NextExpression nextExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forFunctionTerm(tgtlib.definitions.expression.FunctionTerm)
	 */
	@Override
	public String forFunctionTerm(FunctionTerm ft) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forAndExpression(tgtlib.definitions.expression.AndExpression)
	 */
	@Override
	public String forAndExpression(AndExpression andExpression) {
		return (inside!=2 ? "{*" : "") + andExpression.getFirstOperand().accept(inAnd) + andExpression.getSecondOperand().accept(inAnd) + (inside!=2 ? "}" : "");
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forOrExpression(tgtlib.definitions.expression.OrExpression)
	 */
	@Override
	public String forOrExpression(OrExpression orExpression) {
		return (inside!=1 ? "{+" : "") + orExpression.getFirstOperand().accept(inOr) + orExpression.getSecondOperand().accept(inOr) + (inside!=1 ? "}" : "");
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forXOrExpression(tgtlib.definitions.expression.XOrExpression)
	 */
	@Override
	public String forXOrExpression(XOrExpression xOrExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNotExpression(tgtlib.definitions.expression.NotExpression)
	 */
	@Override
	public String forNotExpression(NotExpression notExpression) {
		if (notExpression.getOperand()==null) return "";
		return "{!"+notExpression.getOperand().accept(this)+"}";
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forImpliesExpression(tgtlib.definitions.expression.ImpliesExpression)
	 */
	@Override
	public String forImpliesExpression(ImpliesExpression impliesExpression) {
		return (inside!=3 ? "{=>" : "") + impliesExpression.getFirstOperand().accept(inImply) + impliesExpression.getSecondOperand().accept(inImply) + (inside!=3 ? "}" : "");
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forGreaterEqualExpression(tgtlib.definitions.expression.GreaterEqualExpression)
	 */
	@Override
	public String forGreaterEqualExpression(GreaterEqualExpression greaterEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forEqualsExpression(tgtlib.definitions.expression.EqualsExpression)
	 */
	@Override
	public String forEqualsExpression(EqualsExpression equalsExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forGreaterThanExpression(tgtlib.definitions.expression.GreaterThanExpression)
	 */
	@Override
	public String forGreaterThanExpression(GreaterThanExpression greaterThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forLessEqualExpression(tgtlib.definitions.expression.LessEqualExpression)
	 */
	@Override
	public String forLessEqualExpression(LessEqualExpression lessEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forLessThanExpression(tgtlib.definitions.expression.LessThanExpression)
	 */
	@Override
	public String forLessThanExpression(LessThanExpression lessThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNotEqualsExpression(tgtlib.definitions.expression.NotEqualsExpression)
	 */
	@Override
	public String forNotEqualsExpression(NotEqualsExpression notEqualsExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forDivExpression(tgtlib.definitions.expression.DivExpression)
	 */
	@Override
	public String forDivExpression(DivExpression divExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forPlusExpression(tgtlib.definitions.expression.PlusExpression)
	 */
	@Override
	public String forPlusExpression(PlusExpression plusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forMinusExpression(tgtlib.definitions.expression.MinusExpression)
	 */
	@Override
	public String forMinusExpression(MinusExpression minusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forMultExpression(tgtlib.definitions.expression.MultExpression)
	 */
	@Override
	public String forMultExpression(MultExpression multExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forNegExpression(tgtlib.definitions.expression.NegExpression)
	 */
	@Override
	public String forNegExpression(NegExpression negExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forModuloExpression(tgtlib.definitions.expression.ModuloExpression)
	 */
	@Override
	public String forModuloExpression(ModuloExpression moduloExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see tgtlib.definitions.expression.ExpressionVisitor#forCaseExpression(tgtlib.definitions.expression.CaseExpression)
	 */
	@Override
	public String forCaseExpression(CaseExpression caseExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forConditionalExpression(CondExpression cond) {
		// TODO Auto-generated method stub
		return null;
	}

}
