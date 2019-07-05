package constraintsmanipulation;

import java.io.File;

import constraintsmanipulation.model.Configuration;
import constraintsmanipulation.utils.ReadFaults;

public class TestReadFaults {
	@org.junit.Test
	public void readFault() {
		Configuration c = Configuration.newConfigurationFromFile(new File("data/example/register.txt"));
		ReadFaults.readFaults(c, new File("data/example/register_modified.txt"));
		System.out.println(c.faults);
	}
}
