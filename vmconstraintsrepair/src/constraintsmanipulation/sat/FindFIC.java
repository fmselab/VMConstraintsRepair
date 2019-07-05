package constraintsmanipulation.sat;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import constraintsmanipulation.model.Combination;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.FICType;
import tgtlib.definitions.expression.AndExpression;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;
import tgtlib.definitions.expression.NotExpression;
import tgtlib.definitions.expression.Operator;

public class FindFIC {
	
	static final private Logger LOG = Logger.getLogger(FindFIC.class);
	
	//private PointerByReference ctx1, ctx2;
	private YicesJNAPushPop yices1, yices2;
	
	public FindFIC(Configuration model, Configuration oracle) {
		//Set<IdExpression> commonParameters = ConfigurationUtils.joinParameters(oracle.model.parameters, model.model.parameters);
		yices1 = new YicesJNAPushPop(oracle.model.parameters);
		//PointerByReference ctx1 = yices1.getContext(); //createNewContext();
		yices1.getSATWithPush(oracle.model.toSingleExpression(), false);
		yices2 = new YicesJNAPushPop(oracle.model.parameters);
		//PointerByReference ctx2 = yices1.getContext(); //createNewContext();
		yices2.getSATWithPush(NotExpression.createNotExpression(oracle.model.toSingleExpression()), false);
	}
	
	public Combination getReducedAlphaOtimized(Combination alpha, FICType type) {
		Combination cMin = new Combination(alpha);
		List<IdExpression> params = new ArrayList<>(cMin.getMap().keySet());
		int i=0;
		for (IdExpression id : params) {
			Combination temp = new Combination(cMin);
			temp.getMap().remove(id);
			if (isFICConsideringOracleOptimized(temp, type)) {
				cMin.getMap().remove(id);
			}
			i++;
			if (i%100==0) LOG.debug("Minim. iteration "+i);
		}
		return cMin;
	}

	private boolean isFICConsideringOracleOptimized(Combination c, FICType type) {
		if (type==FICType.AND) {
			return yices1.getSATWithPushPop(c, false)==null;  // oracolo sempre falso
		} else if (type==FICType.OR) {
			return yices2.getSATWithPushPop(c, false)==null;  // oracolo sempre vero
					// && YicesJNA.getInstance().isSAT(Util.mkExpr(NotExpression.createNotExpression(model.model.toSingleExpression()), Operator.AND, alpha));  // esistenza test falso nel modello
		}
		return false;
	}
	
	public static Expression getSATQueryForAlpha(FICType type, Configuration toRepairConf, Configuration correctConf) {
		if (type==FICType.AND) return getC1AndNotC2(toRepairConf, correctConf);
		else if (type==FICType.OR) return getC1AndNotC2(correctConf, toRepairConf);
		return null; // it should not happen
	}
	
	public static Expression getC1AndNotC2(Configuration c1, Configuration c2) {
		return AndExpression.mkBinExpr(c1.model.toSingleExpression(), Operator.AND, NotExpression.createNotExpression(c2.model.toSingleExpression()));
	}
	
}
