/*
 * 
 */
package constraintsmanipulation;

import java.io.File;

import constraintsmanipulation.distance.DistanceCriterion;
import constraintsmanipulation.distance.SyntaxTreeDistance;
import constraintsmanipulation.manipulator.ManipulatorSAS;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.FIC;
import constraintsmanipulation.model.FICType;

// TODO: Auto-generated Javadoc
/**
 * The Class FarceExperiments.
 */
public class FarceExperiments {
	
	/**
	 * Test ecos.
	 *
	 * @throws Exception the exception
	 */
	@org.junit.Test
	public void testEcos() throws Exception {
		Configuration c = Configuration.newConfigurationFromFile(new File("data/farce/ecosPreprocessorErrors.txt"));
		c.setFIC(new FIC("CYGPKG_PPP_CHAP & !CYGPKG_ERROR", FICType.OR));
		Configuration c1 = new ManipulatorSAS().repair(c);
		DistanceCriterion d = SyntaxTreeDistance.getInstance();
		System.out.println(d.getDistance(c1.model, c.model));
		
		c1.setFIC(new FIC("CYGPKG_PPP_CHAP & CYGPKG_ERROR", FICType.AND));
		Configuration c2 = new ManipulatorSAS().repair(c);
		System.out.println(d.getDistance(c2.model, c1.model));
	}
	
	/**
	 * Test uclibc.
	 *
	 * @throws Exception the exception
	 */
	@org.junit.Test
	public void testUclibc() throws Exception {
		Configuration c = Configuration.newConfigurationFromFile(new File("data/farce/ecosPreprocessorErrors.txt"));
		c.setFIC(new FIC("CYGPKG_PPP_CHAP & !CYGPKG_ERROR", FICType.OR));
		Configuration c1 = new ManipulatorSAS().repair(c);
		DistanceCriterion d = SyntaxTreeDistance.getInstance();
		System.out.println(d.getDistance(c1.model, c.model));
		
		c1.setFIC(new FIC("CYGPKG_PPP_CHAP & CYGPKG_ERROR", FICType.AND));
		Configuration c2 = new ManipulatorSAS().repair(c1);
		System.out.println(d.getDistance(c2.model, c1.model));
	}
	
}
