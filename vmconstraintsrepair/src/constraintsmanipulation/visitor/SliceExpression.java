package constraintsmanipulation.visitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

/**
 * Class to transform an Expression into a Combination, i.e. a Map<IdExpression,Boolean> in which
 * IdExpression is the parameter in the "parameters" of the model
 * Boolean is true if the parameter is positive in the assignment, otherwise false (if it is negated)
 * 
 * Note: the Expression must be a Combination
 * @author Marco
 *
 */
public class SliceExpression implements ExpressionVisitor<Expression> {

	protected Collection<IdExpression> ids;
	protected Map<String,IdExpression> params;
	protected Map<IdExpression,Expression> substitutions;
	
	public SliceExpression(Collection<IdExpression> ids) {
		this.ids=ids;
		params = new HashMap<>();
		substitutions = new HashMap<>();
		for (IdExpression id : ids) params.put(id.getIdString(),id);
	}
		
	@Override
	public Expression forIdExpression(IdExpression idExpression) {
		System.out.println(idExpression);
		boolean makeNot=false;
		if (idExpression.getIdString().endsWith("_m")) {
			idExpression = params.get(idExpression.getIdString().substring(0,idExpression.getIdString().length()-2));
			makeNot=true;
		}
		if (ids.contains(idExpression)) return makeNot ? NotExpression.createNotExpression(idExpression) : idExpression;
		if (substitutions.containsKey(idExpression)) return makeNot ? NotExpression.createNotExpression(substitutions.get(idExpression)) : substitutions.get(idExpression);
		return null;
	}

	@Override
	public Expression forPrimedIdExpression(PrimedIdExpression primedIdExpression) {
		System.out.println("PrimedId: "+primedIdExpression);
		return null;
	}

	@Override
	public Expression forNextExpression(NextExpression nextExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression forFunctionTerm(FunctionTerm ft) {
		System.out.println("Function: "+ft);
		return null;
	}

	@Override
	public Expression forAndExpression(AndExpression andExpression) {
		Expression e1 = andExpression.getFirstOperand().accept(this);
		Expression e2 = andExpression.getSecondOperand().accept(this);
		if (e1==null && e2==null) return null;
		if (e1==null) return e2;
		if (e2==null) return e1;
		return AndExpression.makeAndExpression(e1, e2);
	}

	@Override
	public Expression forOrExpression(OrExpression orExpression) {
		Expression e1 = orExpression.getFirstOperand().accept(this);
		Expression e2 = orExpression.getSecondOperand().accept(this);
		if (e1==null || e2==null) return null;
		if (
			(e1 instanceof IdExpression && e2 instanceof NotExpression && ((NotExpression)e2).getOperand() instanceof IdExpression && ((NotExpression)e2).getOperand().equals(e1)) 
			|| (e2 instanceof IdExpression && e1 instanceof NotExpression && ((NotExpression)e1).getOperand() instanceof IdExpression && ((NotExpression)e1).getOperand().equals(e2))
			|| e1.equals(e2)) {
			return null; // remove stupid constraints
		}
		return OrExpression.mkBinExpr(e1, Operator.OR, e2);
	}

	@Override
	public Expression forXOrExpression(XOrExpression xOrExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression forNotExpression(NotExpression notExpression) {
		if (notExpression==null) return null;
		Expression e = notExpression.getOperand().accept(this);
		if (e==null) return null;
		return NotExpression.createNotExpression(e);
	}

	@Override
	public Expression forImpliesExpression(ImpliesExpression impliesExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression forGreaterEqualExpression(GreaterEqualExpression greaterEqualExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression forEqualsExpression(EqualsExpression equalsExpression) {
		if (equalsExpression.getFirstOperand() instanceof IdExpression) {
			substitutions.put((IdExpression)equalsExpression.getFirstOperand(), equalsExpression.getSecondOperand().accept(this));
		}
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
