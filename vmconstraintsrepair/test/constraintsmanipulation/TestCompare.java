/*
 * 
 */
package constraintsmanipulation;

import java.io.File;

import org.junit.Test;

import constraintsmanipulation.distance.BFCD;
import constraintsmanipulation.distance.BFED;
import constraintsmanipulation.distance.SyntaxTreeDistance;
import constraintsmanipulation.manipulator.ManipulatorSAS;
import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.simplifier.ATGTSimplifier;
import constraintsmanipulation.simplifier.EspressoSimplifier;
import constraintsmanipulation.simplifier.JBoolSimplifier;
import constraintsmanipulation.simplifier.KmkeenCNFSimplifier;
import constraintsmanipulation.simplifier.QMSimplifier;
import tgtlib.definitions.expression.parser.ExpressionParser;
import tgtlib.definitions.expression.parser.ParseException;

// TODO: Auto-generated Javadoc
/**
 * The Class TestCompare.
 *
 * @author Marco
 */
public class TestCompare {
	
	/**
	 * Test distance tree.
	 */
	@org.junit.Test
	public void testDistanceTree() {
		Configuration c = Configuration.newConfigurationFromFile(new File("data/example.txt"));
		Configuration c1 = new ManipulatorSAS(JBoolSimplifier.getInstance()).repair(c); //pick one
		Configuration c2 = new ManipulatorSAS(ATGTSimplifier.getInstance()).repair(c);
		Configuration c3 = new ManipulatorSAS(EspressoSimplifier.getInstance()).repair(c);
		Configuration c4 = new ManipulatorSAS(KmkeenCNFSimplifier.getInstance()).repair(c);
		Configuration c5 = new ManipulatorSAS(QMSimplifier.getInstance()).repair(c);
		
		SyntaxTreeDistance d = SyntaxTreeDistance.getInstance();
		System.out.println(d.getDistance(c, c1));
		System.out.println(d.getDistance(c, c2));
		System.out.println(d.getDistance(c, c3));
		System.out.println(d.getDistance(c, c4));
		System.out.println(d.getDistance(c, c5));
	}
	
	/**
	 * Test or.
	 */
	@org.junit.Test
	public void testOr() {
		Configuration c = Configuration.newConfigurationFromFile(new File("data/example_or.txt"));
		Configuration c1 = new ManipulatorSAS(JBoolSimplifier.getInstance()).repair(c); //pick one
		
		System.out.println(SyntaxTreeDistance.getInstance().getDistance(c, c1));
	}
	
	@org.junit.Test
	public void testDistance() throws ParseException {
		Configuration c1 = Configuration.newEmptyConfiguration();
		c1.model.constraints.add(ExpressionParser.parse("A or (B and C)", Configuration.idc));
		
		Configuration c2 = c1.clone();
		c2.model.constraints.set(0, ExpressionParser.parse("A and B and C", Configuration.idc));
				
		System.out.println(SyntaxTreeDistance.getInstance().getDistance(c1, c2));
	}
	
	@Test
	public void testCompareRhiscom() {
		Configuration c1 = Models.RHISCOM1.loadConfiguration();
		Configuration c2 = Models.RHISCOM2.loadConfiguration();
		Configuration c3 = Models.RHISCOM3.loadConfiguration();
		System.out.println(BFCD.instance.getDistance(c1, c2) + " " + BFCD.instance.getDistance(c2, c3) + " " +  BFCD.instance.getDistance(c1, c3));
		System.out.println(BFED.instance.getDistance(c1, c2) + " " + BFED.instance.getDistance(c2, c3) + " " +  BFED.instance.getDistance(c1, c3));
	}
	
	@org.junit.Test
	public void testNewBFED() throws ParseException {
		Configuration c1 = Configuration.newEmptyConfiguration();
		c1.model.constraints.add(ExpressionParser.parse("A or B or C", Configuration.idc));
		c1.model.constraints.add(ExpressionParser.parse("B or C", Configuration.idc));
		c1.model.computeParametersFromConstraints();
		
		Configuration c2 = c1.clone();
		c2.model.constraints.set(1, ExpressionParser.parse("B", Configuration.idc));
		c2.model.constraints.set(0, ExpressionParser.parse("A or B", Configuration.idc));
		c2.model.computeParametersFromConstraints();
		//c2.model.constraints.add(ExpressionParser.parse("A or B", Configuration.idc));
		
		System.out.println(BFED.instance.computeEditDistance(c1.model, c2.model));
	}
	
}
