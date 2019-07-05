/*
 * 
 */
package constraintsmanipulation.manipulator;

import constraintsmanipulation.model.Configuration;
import tgtlib.definitions.expression.AndExpression;
import tgtlib.definitions.expression.CaseExpression;
import tgtlib.definitions.expression.CondExpression;
import tgtlib.definitions.expression.DivExpression;
import tgtlib.definitions.expression.EqualsExpression;
import tgtlib.definitions.expression.Expression;
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
import tgtlib.definitions.expression.Operator;
import tgtlib.definitions.expression.OrExpression;
import tgtlib.definitions.expression.PlusExpression;
import tgtlib.definitions.expression.PrimedIdExpression;
import tgtlib.definitions.expression.XOrExpression;

public class MutationVisitor implements ExpressionVisitor<Expression> {

	public MutationVisitor(Mutation m, Configuration c, int nMax) {
		this.m=m;
		this.c=c;
		this.nMax=nMax;
	}
	
	private Mutation m;
	private Configuration c;
	private int nMax;
	private int n;
	public void reset() {n=0;}
	public int getN() {return n;}
	public boolean mutationPerformed() {return n==nMax;}
	
	@Override
	public Expression forIdExpression(IdExpression idExpression) {
		if (m==Mutation.RC) return null;
		if (n<nMax && (m==Mutation.RL || m==Mutation.SL) && Math.random()<0.25) {
			
			n++;
			System.out.println(n+" "+m);
			if (m==Mutation.RL) return null;
			if (m==Mutation.SL) return c.model.getParametersAsList().get((int)(Math.random()*c.model.parameters.size()));
		}
		return idExpression;
	}
	@Override
	public Expression forPrimedIdExpression(PrimedIdExpression primedIdExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forNextExpression(NextExpression nextExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forFunctionTerm(FunctionTerm ft) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forAndExpression(AndExpression andExpression) {
		Expression a = andExpression.getFirstOperand().accept(this);
		Expression b = andExpression.getSecondOperand().accept(this);
		if (a==null) return b;
		if (b==null) return a;
		return AndExpression.mkBinExpr(a, Operator.AND, b);
	}
	@Override
	public Expression forOrExpression(OrExpression orExpression) {
		Expression a = orExpression.getFirstOperand().accept(this);
		Expression b = orExpression.getSecondOperand().accept(this);
		if (a==null) return b;
		if (b==null) return a;
		return OrExpression.mkBinExpr(a, Operator.OR, b);
	}
	@Override
	public Expression forXOrExpression(XOrExpression xOrExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forNotExpression(NotExpression notExpression) {
		Expression e = notExpression.getOperand().accept(this);
		if (e==null) return null;
		return NotExpression.createNotExpression(e);
	}
	@Override
	public Expression forImpliesExpression(ImpliesExpression impliesExpression) {
		Expression a = impliesExpression.getFirstOperand().accept(this);
		Expression b = impliesExpression.getSecondOperand().accept(this);
		if (a==null) return b;
		if (b==null) return a;
		return ImpliesExpression.mkBinExpr(a, Operator.IMPLIES, b);
	}
	@Override
	public Expression forGreaterEqualExpression(GreaterEqualExpression greaterEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forEqualsExpression(EqualsExpression equalsExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forGreaterThanExpression(GreaterThanExpression greaterThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forLessEqualExpression(LessEqualExpression lessEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forLessThanExpression(LessThanExpression lessThanExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forNotEqualsExpression(NotEqualsExpression notEqualsExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forDivExpression(DivExpression divExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forPlusExpression(PlusExpression plusExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forMinusExpression(MinusExpression minusExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forMultExpression(MultExpression multExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forNegExpression(NegExpression negExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forModuloExpression(ModuloExpression moduloExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forCaseExpression(CaseExpression caseExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Expression forConditionalExpression(CondExpression cond) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
