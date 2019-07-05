package constraintsmanipulation.sat;

import java.nio.IntBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.sun.jna.ptr.PointerByReference;

import atgt.yices2.generator.ExprToYicesPtr;
import constraintsmanipulation.model.Combination;
import tgtlib.definitions.expression.Expression;
import tgtlib.definitions.expression.IdExpression;
import yices2.Yices2Library;

/** To use Yices.as SMT Solver */
public class YicesJNA implements SATSolver {

	static final private Logger LOG = Logger.getLogger(YicesJNA.class);
	
	PointerByReference ctx;
	Yices2Library y;
	Map<IdExpression,Integer> parameterMap;
	ExprToYicesPtr converter;
	public boolean keepContext;
	Collection<IdExpression> ids;
	
	public YicesJNA(Collection<IdExpression> params) {
		y = Yices2Library.INSTANCE;
		y.yices_init();    // Always call this first
		PointerByReference config = y.yices_new_config();
		y.yices_default_config_for_logic(config, "NONE"); // set the propositional logic
		ctx = y.yices_new_context(config);
		setIds(params);
	}
		
	public void reset() {
		ctx=null; y=null; parameterMap=null;
	}
	
	public void setIds(Collection<IdExpression> params) {
		this.ids=params;
		parameterMap = new HashMap<>();
		for (IdExpression parameter : ids) {
			int p = y.yices_new_uninterpreted_term(y.yices_bool_type());
			parameterMap.put(parameter, p);
		}
		converter = new ExprToYicesPtr(y, parameterMap);
	}
	
	private Map<IdExpression,Boolean> getSAT(Expression e, boolean getModel) {
		try {
			/*if (ids==null) {  // should not happen anymore
				LOG.debug("Collecting parameters...");
				ids = e.accept(IDExprCollector.instance);
			}	
			if (parameterMap==null || converter==null) {
				setIds(ids);
			}*/
			LOG.debug("Resets context...");
			y.yices_reset_context(ctx);
			
			LOG.debug("Converting expression...");
			int f = e.accept(converter);
			int code = y.yices_assert_formula(ctx, f);
			if (code < 0) {
				System.out.println("errore " + y.yices_error_code());
			}
			return printResult(y, ctx, parameterMap, getModel);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static Map<IdExpression,Boolean> printResult(Yices2Library y, PointerByReference ctx, Map<IdExpression, Integer> parameterMap, boolean getModel) {
		LOG.debug("Checking SAT...");
		int result = y.yices_check_context(ctx, null);
		LOG.debug("SAT checked..."+(result==Yices2Library.smt_status.STATUS_SAT));
		if (result==Yices2Library.smt_status.STATUS_SAT) {
			if (getModel) {
				LOG.debug("Getting model....");
				PointerByReference model = y.yices_get_model(ctx, 0);
				LOG.debug("Mapping model...");
				Map<IdExpression,Boolean> modelMap = new HashMap<>();
				for (Entry<IdExpression, Integer> p : parameterMap.entrySet()) {
					int value = y.yices_formula_true_in_model(model, p.getValue());
					if (value!=-1) modelMap.put(p.getKey(), value==1);
				}
				return modelMap;
			} else return new HashMap<>(); // just return an empty HashMap if the model is not needed
		} 
		else return null;
	}
	
	@Override
	public boolean isSAT(Expression e) {
		//if (converter==null || converter.getIdYices()==null) return true;
		//if (converter==null || converter.getIdYices()==null) setIds(e.accept(new FeatureCollection()));
		Map<IdExpression,Boolean> result = getSAT(e, false);
		return result!=null;
	}

	/** @return null if is unsat, otherwise the assignment that makes it SAT */
	@Override
	public Map<IdExpression, Boolean> getSAT(Expression e) {
		return getSAT(e,true);
	}
	
	public int toYices(Combination c, PointerByReference context) {
		int[] values = new int[c.getMap().size()];
		int i=0;
		for (Entry<IdExpression, Boolean> e : c.getMap().entrySet()) {
			if (e.getValue()) values[i]=parameterMap.get(e.getKey());
			else values[i]=y.yices_not(parameterMap.get(e.getKey()));
			i++;
		}
		IntBuffer array = IntBuffer.wrap(values);
		return y.yices_and(values.length, array);
	}

}
