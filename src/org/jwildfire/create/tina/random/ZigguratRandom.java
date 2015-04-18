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
	 * @author Andreas Schouten (andreas@schouten-blog.de)
	 */
	private static final long serialVersionUID = 1L;

	public ZigguratRandom() {
		this(System.currentTimeMillis());
	}

	public ZigguratRandom(long seed) {
		super();
		setSeed(seed);
		zigset();
	}

	protected int next(int bits) {
		return (bits == 32) ? SHR3() : super.next(bits);
	}

	public boolean nextBoolean() {
		return SHR3() > 0;
	}

	public double nextDouble() {
		return UNI();
	}

	public float nextFloat() {
		return (float) UNI();
	}

	public double nextGaussian() {
		return RNOR();
	}

	public int nextInt() {
		return SHR3();
	}

	public void setSeed(long seed) {
		jsr ^= (int) seed;
		super.setSeed(seed);
	}

	private int jsr = 123456768;

	private static int[] kn;

	private static double[] wn, fn;
	private static boolean initialized = false;

	private double RNOR() {
		int hz = SHR3();
		int iz = hz & 127;
		return (Math.abs(hz) < kn[iz]) ? hz * wn[iz] : nfix(hz, iz);
	}

	private int SHR3() {
		int jz = jsr;
		int jzr = jsr;
		jzr ^= (jzr << 13);
		jzr ^= (jzr >>> 17);
		jzr ^= (jzr << 5);
		jsr = jzr;
		return jz + jzr;
	}

	private double nfix(int hz, int iz) {
		double r = 3.442619855899; /* The start of the right tail */
		double r1 = 1 / r;
		double x, y;
		for (;;) {
			x = hz * wn[iz]; /* iz==0, handles the base strip */
			if (iz == 0) {
				do {
					x = (-Math.log(UNI()) * r1);
					y = -Math.log(UNI());
				} while (y + y < x * x);
				return (hz > 0) ? r + x : -r - x;
			}
			/* iz>0, handle the wedges of other strips */
			if (fn[iz] + UNI() * (fn[iz - 1] - fn[iz]) < Math.exp(-.5 * x * x))
				return x;

			/* initiate, try to exit for(;;) for loop */
			hz = SHR3();
			iz = hz & 127;

			if (Math.abs(hz) < kn[iz])
				return (hz * wn[iz]);
		}

	}

	private double UNI() {
		return 0.5 * (1 + (double) SHR3() / (double) Integer.MIN_VALUE);
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

		for (i = 126; i >= 1; i--) {
			dn = Math.sqrt(-2. * Math.log(vn / dn + Math.exp(-.5 * dn * dn)));
			kn[i + 1] = (int) ((dn / tn) * m1);
			tn = dn;
			fn[i] = Math.exp(-.5 * dn * dn);
			wn[i] = dn / m1;
		}
	}	
}