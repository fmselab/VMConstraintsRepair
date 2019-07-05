package constraintsmanipulation;

import org.junit.Test;

import constraintsmanipulation.model.Configuration;

public class TestModelsFromFARCE {

	@Test
	public void test1() {
		Configuration c = Models.LINUX_CITLAB.loadConfiguration();
		System.out.println(c);
	}

	@Test
	public void test2() {
		Configuration c = Models.LINUX28.loadConfiguration();
		System.out.println(c);
	}
	
	@Test
	public void test3() {
		Configuration c = Models.TOYBOX.loadConfiguration();
		System.out.println(c);
	}
	
	@Test
	public void test4() {
		Configuration c = Models.FREEBSD.loadConfiguration();
		System.out.println(c);
	}
	
	@Test
	public void testError() {
		Configuration c = Models.LINUX_ERROR.loadConfiguration();
		System.out.println(c);
	}
}
