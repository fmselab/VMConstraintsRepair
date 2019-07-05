/*
 * 
 */
package constraintsmanipulation;

import java.io.File;

import constraintsmanipulation.manipulator.ManipulatorSAS;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.sat.FICGenerator;
import constraintsmanipulation.utils.ConfigurationUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class FeatureIDEExperiments.
 */
public class FeatureIDEExperiments {
	
	/**
	 * Test mutation.
	 */
	@org.junit.Test
	public void testMutation() {
		Configuration c = ConfigurationUtils.loadConfigurationFromFeatureIDEModel(new File("data/featureide/gpl_ahead.m"));
		c.fic = new FICGenerator(c).generateFIC(false);
		System.out.println("Configuration:\n"+c);
		Configuration c1 = new ManipulatorSAS().repair(c);
		//System.out.println(SyntaxTreeDistance.getInstance().getDistance(c1, c));
		System.out.println("Simplified Model: ");
		System.out.println(c1);
	}
}
