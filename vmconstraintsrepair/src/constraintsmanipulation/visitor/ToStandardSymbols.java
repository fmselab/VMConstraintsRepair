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
 * The Class ToStandardSymbols.
 *
 * @author Marco
 */
public class ToStandardSymbols implements ExpressionVisitor<String> {

	/**
	 * The Enum Inside.
	 */
	static enum Inside {/** The inside and. */
INSIDE_AND, /** The inside or. */
 INSIDE_OR};
	
	/**
	 * Instantiates a new to standard symbols.
	 */
	public ToStandardSymbols() {}
	
	/** The inside. */
	Inside inside;
	
	/**
	 * Instantiates a new to standard symbols.
	 *
	 * @param inside the inside
	 */
	public ToStandardSymbols(Inside inside) {
		this.inside = inside;
	}
	
	/** The ins and. */
	static ToStandardSymbols insAnd = new ToStandardSymbols(Inside.INSIDE_AND);
	
	/** The ins or. */
	static ToStandardSymbols insOr = new ToStandardSymbols(Inside.INSIDE_OR);
	
	/** The ins default. */
	static ToStandardSymbols insDefault = new ToStandardSymbols();
		
	@Override
	public String forIdExpression(IdExpression idExpression) {
		return idExpression.getIdString();
	}

	@Override
	public String forPrimedIdExpression(PrimedIdExpression primedIdExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forNextExpression(NextExpression nextExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forFunctionTerm(FunctionTerm ft) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forAndExpression(AndExpression andExpression) {
		return (inside!=Inside.INSIDE_AND ? "(" : "") + andExpression.getFirstOperand().accept(insAnd) + " & " + andExpression.getSecondOperand().accept(insAnd) + (inside!=Inside.INSIDE_AND ? ")" : "");
	}

	@Override
	public String forOrExpression(OrExpression orExpression) {
		return (inside!=Inside.INSIDE_OR ? "(" : "") + orExpression.getFirstOperand().accept(insOr) + " | " + orExpression.getSecondOperand().accept(insOr) + (inside!=Inside.INSIDE_OR ? ")" : "");
	}

	@Override
	public String forXOrExpression(XOrExpression xOrExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forNotExpression(NotExpression notExpression) {
		return "!"+notExpression.getOperand().accept(insDefault);
	}

	@Override
	public String forImpliesExpression(ImpliesExpression impliesExpression) {
		return null;
		//return OrExpression.mkBinExpr(NotExpression.createNotExpression(impliesExpression.getFirstOperand()), Operator.OR, impliesExpression.getSecondOperand()).accept(insDefault);
	}

	@Override
	public String forGreaterEqualExpression(GreaterEqualExpression greaterEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forEqualsExpression(EqualsExpression equalsExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forGreaterThanExpression(GreaterThanExpression greaterThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forLessEqualExpression(LessEqualExpression lessEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forLessThanExpression(LessThanExpression lessThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forNotEqualsExpression(NotEqualsExpression notEqualsExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forDivExpression(DivExpression divExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forPlusExpression(PlusExpression plusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forMinusExpression(MinusExpression minusExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forMultExpression(MultExpression multExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forNegExpression(NegExpression negExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String forModuloExpression(ModuloExpression moduloExpression) {
		// TODO Auto-generated method stub
		return null;
	}

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
