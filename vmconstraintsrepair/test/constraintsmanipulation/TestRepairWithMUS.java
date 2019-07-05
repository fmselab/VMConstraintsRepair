package constraintsmanipulation;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import constraintsmanipulation.manipulator.ManipulatorSAS2;
import constraintsmanipulation.manipulator.Manipulators;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.FICType;
import constraintsmanipulation.sat.YicesJNAPushPop;
import tgtlib.definitions.expression.parser.ExpressionParser;

public class TestRepairWithMUS {
	
	@BeforeClass
	public static void setup(){
		Logger.getLogger(Experiment3.class).setLevel(Level.DEBUG);
		Logger.getLogger(ManipulatorSAS2.class).setLevel(Level.DEBUG);
		Logger.getLogger(YicesJNAPushPop.class).setLevel(Level.DEBUG);
		
	}
	
	@Test
	public void testRepairMUS() { 
		try {
			Configuration oracle = Configuration.newEmptyConfiguration();
			oracle.model.constraints.add(ExpressionParser.parse("A or B or C", Configuration.idc));
			oracle.model.constraints.add(ExpressionParser.parse("B or C", Configuration.idc));
			oracle.model.computeParametersFromConstraints();
			
			Configuration model = oracle.clone();
			model.model.constraints.set(0, ExpressionParser.parse("A or B", Configuration.idc));
			model.model.constraints.set(1, ExpressionParser.parse("B", Configuration.idc));
			//c2.model.constraints.add(ExpressionParser.parse("A or B", Configuration.idc));
			model.model.computeParametersFromConstraints();
			
			Experiment3.performExperiment3(oracle, model, Manipulators.ONLY_SELECTION.getManipulator(), FICType.AND);
		} catch (Exception e) {e.printStackTrace();}
	}
}
