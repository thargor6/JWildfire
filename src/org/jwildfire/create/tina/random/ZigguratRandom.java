package org.jwildfire.create.tina.random;

import java.util.Random;

public class ZigguratRandom extends Random
{
	/**
	 * This class was found here  /github.com/OliverColeman/bain/blob/master/src/com/ojcoleman/bain/misc/Ziggurat.java
	 * permission was asked here: /github.com/OliverColeman/bain/issues/1
	 * 
	 * Assuming this is public domain until we can find Andreas Schouten or a similar Ziggurat algo is implemented
	 * schouten-blog.de is unaccessible as of April 18 2015
	 * original:
	 * 
	 * This class implements the ziggurat algorithm for normal distributed random numbers as provided by George Marsaglia and Wai Wan Tsang in 2000.
	 * 
	 * @author Andreas Schouten (andreas@schouten-blog.de) ?
	 * @author George Marsaglia
	 * @author Wai Wan Tsang
	 * 
	 * The constants seem to be based on the original paper here, so this fits the JWildFire license
	 * Credit / Citation:
	 * [Oct 2000 George Marsaglia, Wai Wan Tsang] The ziggurat method for generating random variables 
	 * http://www.jstatsoft.org/v05/i08
	 * http://creativecommons.org/licenses/by/3.0/
	 * 
	 * === enhancements to original ===
	 * Converted from int to long in SHR3, stripped out methods not needed by JWildFire
	 */
	private static final long serialVersionUID = 1L;

	public ZigguratRandom() {
		this(System.nanoTime());
	}

	public ZigguratRandom(long seed) {
		super();
		setSeed(seed);
		zigset();
	}

	public double nextDouble() {
		return UNI();
	}

	public void setSeed(long seed) {
		jsr ^= (int) seed;
		super.setSeed(seed);
	}

	private long jsr = 123456768;

	private static int[] kn;

	private static double[] wn, fn;
	private static boolean initialized = false;

	private long SHR3() {
		long jz = jsr;
		long jzr = jsr;
		jzr ^= (jzr << 13);
		jzr ^= (jzr >>> 17);
		jzr ^= (jzr << 5);
		jsr = jzr;
		return jz + jzr;
	}


	private double UNI() {
		return 0.5 * (1.0 + (double)SHR3() / (double)Long.MIN_VALUE);
	}

	private static synchronized void zigset() {
		if (initialized)
			return;
		initialized = true;

		wn = new double[128];
		fn = new double[128];
		kn = new int[128];

		double m1 = 2147483648.0;
		double dn = 3.442619855899, tn = dn, vn = 9.91256303526217e-3, q;
		int i;

		/* Set up tables for RNOR */
		q = vn / Math.exp(-.5 * dn * dn);
		kn[0] = (int) ((dn / q) * m1);
		kn[1] = 0;

		wn[0] = q / m1;
		wn[127] = dn / m1;

		fn[0] = 1.;
		fn[127] = Math.exp(-.5 * dn * dn);

		for (i = 126; i >= 0; i--) {
			dn = Math.sqrt(-2. * Math.log(vn / dn + Math.exp(-.5 * dn * dn)));
			kn[i + 1] = (int) ((dn / tn) * m1);
			tn = dn;
			fn[i] = Math.exp(-.5 * dn * dn);
			wn[i] = dn / m1;
		}
	}	
}