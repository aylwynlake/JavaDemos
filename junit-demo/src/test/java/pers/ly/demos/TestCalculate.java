package pers.ly.demos;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class TestCalculate {
	Calculate cal;
	
	@Before
	public void setUp() {
		cal = new Calculate();
	}
	
	@Test
	public void testAdd() {
		int rel = cal.add(10, 20);
		Assert.assertEquals("add wrong", rel, 30);
	}
	
	@Test
	public void testMinus() {
		int rel = cal.minus(30, 20);
		Assert.assertEquals("add wrong", rel, 10);
	}
}

