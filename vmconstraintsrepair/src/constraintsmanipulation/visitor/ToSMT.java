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

public class ToSMT implements ExpressionVisitor<String> {

	private ToSMT() {}
	private static ToSMT instance;
	public static ToSMT getInstance() {return instance==null ? instance=new ToSMT() : instance;}
	
	
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
		return "(and " + andExpression.getFirstOperand().accept(this) + " " + andExpression.getSecondOperand().accept(this) + ")";
	}

	@Override
	public String forOrExpression(OrExpression orExpression) {
		return "(or " + orExpression.getFirstOperand().accept(this) + " " + orExpression.getSecondOperand().accept(this) + ")";
	}

	@Override
	public String forXOrExpression(XOrExpression xOrExpression) {
		return "(xor " + xOrExpression.getFirstOperand().accept(this) + " " + xOrExpression.getSecondOperand().accept(this) + ")";
	}

	@Override
	public String forNotExpression(NotExpression notExpression) {
		return "(not "+notExpression.getOperand().accept(this)+")";
	}

	@Override
	public String forImpliesExpression(ImpliesExpression impliesExpression) {
		return "(=> " + impliesExpression.getFirstOperand().accept(this) + " " + impliesExpression.getSecondOperand().accept(this) + ")";
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
