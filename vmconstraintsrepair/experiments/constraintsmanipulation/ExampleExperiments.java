package constraintsmanipulation;

import java.io.File;

import constraintsmanipulation.manipulator.ManipulatorSAS;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.model.FIC;
import constraintsmanipulation.model.FICType;
import constraintsmanipulation.sat.FICGenerator;
import constraintsmanipulation.simplifier.Simplifier;
import constraintsmanipulation.simplifier.Simplifiers;
import tgtlib.definitions.expression.parser.ParseException;

/**
 * The Class ExampleExperiments.
 *
 * @author marcoradavelli
 */
public class ExampleExperiments {
	
	/** The c. */
	private static Configuration c;
	
	/**
	 * Read model.
	 */
	private static void readModel() {
		c = Configuration.newConfigurationFromFile(new File("data/example/example_paper2.txt"));
	}
	
	/**
	 * Test and.
	 * @throws ParseException 
	 */
	@org.junit.Test
	public void testAnd() throws ParseException {
		readModel();
		//c.setAlpha("!A and B and !C");
		c.setFIC(new FIC("!B and C", FICType.AND));
		System.out.println("Initial Model: ");
		System.out.println(c);
		for (Simplifier s : Simplifiers.simplifiers) {
			Configuration c1 = new ManipulatorSAS(s).repair(c);
			System.out.println("\nSimplified Model with "+s.getName()+": ");
			System.out.println(c1);			
		}
	}

	/**
	 * Test or.
	 * @throws ParseException 
	 */
	@org.junit.Test
	public void testOr() throws ParseException {
		readModel();
		//c.setAlpha("A and B and !C");
		c.setFIC(new FIC("A and !B", FICType.OR));
		System.out.println("Initial Model: ");
		System.out.println(c);
		for (Simplifier s : Simplifiers.simplifiers) {
			Configuration c1 = new ManipulatorSAS(s).repair(c);
			System.out.println("\nSimplified Model with "+s.getName()+": ");
			System.out.println(c1);			
		}
	}

	/**
	 * Test generated.
	 */
	@org.junit.Test
	public void testGenerated() {
		readModel();
		//c.setAlpha("A and B and !C");
		for (int i=0; i<10; i++) {
			System.out.println(i);
			System.out.println("Initial Model: ");
			System.out.println(c);
			
			System.out.println(c.model.constraints.size());
			c.fic = new FICGenerator(c).generateFIC(false);
			//for (Simplifier s : Simplifier.simplifiers) {
				c = new ManipulatorSAS().repair(c);
				System.out.println("\nSimplified Model: ");
				System.out.println(c);			
			//}
		}
	}

	/**
	 * Generate fault.
	 */
	@org.junit.Test
	public void generateFault() {
		readModel();
		FIC f = new FICGenerator(c).generateFIC(false);
		System.out.println(f);
	}
	
}
