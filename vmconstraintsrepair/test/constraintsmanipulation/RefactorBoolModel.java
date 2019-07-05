package constraintsmanipulation;

import java.util.Set;

import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.visitor.LiteralCounter;
import constraintsmanipulation.visitor.SliceExpression;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;
import tgtlib.definitions.expression.visitors.IDExprCollector;

/**
 * It's still to complicated: too many parameters, how to treat the _m parameters?
 * @author marcoradavelli
 *
 */
public class RefactorBoolModel {
	@org.junit.Test
	public void testBusyBox() {
		Configuration v1 = Models.BUSYBOX1_80.loadConfiguration(); // wrong: to fix
		Configuration v2 = Models.BUSYBOX1_85.loadConfiguration(); // correct
		Expression e1 = v1.model.toSingleExpression(), e2 = v2.model.toSingleExpression();
		System.out.println("Size: " + IDExprCollector.getIdsAsList(e1).size() + " " + IDExprCollector.getIdsAsList(e2).size());
		Set<IdExpression> ids = v2.model.parameters;
		//ExpressionSimplifierSubExpressionsRemover visitor = new tgtlib.definitions.expression.visitors.ExpressionSimplifierSubExpressionsRemover(ids);
		
		//e1 = v1.model.constraints.get(6000);  // +6797
		e1 = v1.model.constraints.get(1);  // +6797
		//System.out.println(e1);
		e1 = e1.accept(new SliceExpression(ids));
		System.out.println(e1==null ? null : e1.accept(LiteralCounter.instance));
		//System.out.println(e2);
		System.out.println("Size: " + IDExprCollector.getIdsAsList(e1).size() + " " + IDExprCollector.getIdsAsList(e2).size());
		
	}
}
