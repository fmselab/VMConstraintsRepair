package constraintsmanipulation;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;

import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.utils.Medici;
import tgtlib.definitions.expression.parser.ParseException;

public class TestMedici {
	
	@BeforeClass
	public static void setup(){
		Logger.getLogger(Medici.class).setLevel(Level.DEBUG);
	}
	
	@org.junit.Test
	public void test1() throws ParseException {
		Configuration c = Models.FREEBSD.loadConfiguration();
		System.out.println(c);
		System.out.println(Medici.getValidityRatio(c.model));
	}
}
