package constraintsmanipulation;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import constraintsmanipulation.manipulator.Manipulators;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.FICType;
import constraintsmanipulation.model.Model;
import constraintsmanipulation.utils.ConfigurationUtils;
import tgtlib.definitions.expression.Expression;

public class TestReadModel {

	@Test
	public void testReadCNF() {
		Configuration c = Models.RHISCOM1.loadConfiguration();
		System.out.println(c);
	}
	
	@Test
	public void testStatistics() {
		Configuration c = Models.DJANGO.loadConfiguration();
		System.out.println(c);
		// Test statistics
		System.out.println(c.model.getStatistics());
	}
	
	@Test
	public void modelsWithDifferentParameters() { 
		try {
			Configuration a = Configuration.newEmptyConfiguration();
			a.model = new Model(new ArrayList<>(Arrays.asList(new Expression[] {
					ConfigurationUtils.parseExpression("!A or B"), 
					ConfigurationUtils.parseExpression("!B or C")
			})));

			Configuration b = Configuration.newEmptyConfiguration();
			b.model = new Model(new ArrayList<>(Arrays.asList(new Expression[] {
					ConfigurationUtils.parseExpression("!A or B"), 
					ConfigurationUtils.parseExpression("!B or D")
			})));
			
			Stats stats = Experiment3.performExperiment3(b, a, Manipulators.JBOOL.getManipulator(), FICType.AND);
			
			System.out.println(stats.toString() + "\n" + stats.repairedModel);
		} catch (Exception e) {e.printStackTrace();}		
	}
		
	
}
