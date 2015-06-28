package org.jwildfire.base.mathlib;

import org.junit.*;

public class ComplexTest 
{
	@Test
	public void testComplexLMean()
	{
		// Logarithmic mean is given by (b-a)/log(b/a) 
		// note - looks like natural log
		// Re 		 (6-3)/log(6/3) -> 3/ln(2)	~-> 4.32
		// imaginary (8-4)/log(8/4) -> 4/ln(2) ~-> 5.77
		Complex c = new Complex(3, 4);
		Complex c2 = new Complex(6, 8);
		System.out.println(c.re+" i"+c.im);
		c.LMean(c2);
		System.out.println(c.re+" i"+c.im);
		Assert.assertEquals(4.32,c.re,.02);
		Assert.assertEquals(5.77,c.im,.02);
	}
}
