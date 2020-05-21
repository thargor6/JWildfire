/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke
  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
  General Public License as published by the Free Software Foundation; either version 2.1 of the 
  License, or (at your option) any later version.
 
  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
  Lesser General Public License for more details.
  You should have received a copy of the GNU Lesser General Public License along with this software; 
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import static org.jwildfire.base.mathlib.MathLib.*;
import org.jwildfire.base.Tools;

public class PixelFlowFunc extends VariationFunc {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_ANGLE = "angle";
	private static final String PARAM_LEN = "len";
	private static final String PARAM_WIDTH = "width";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_ENABLE_DC = "enable_dc";
	

	private static final String[] paramNames = { PARAM_ANGLE, PARAM_LEN, PARAM_WIDTH, PARAM_SEED, PARAM_ENABLE_DC };
	private double angle = 90.0;
	private double len = 0.1;
	private double width = 200.0;
	private int seed = 42;
	private int enable_dc = 0;

	private double hash(int a) { // http://burtleburtle.net/bob/hash/integer.html
		a = (a ^ 61) ^ (a >> 16);
		a = a + (a << 3);
		a = a ^ (a >> 4);
		a = a * 0x27d4eb2d;
		a = a ^ (a >> 15);
		return (double) a / (double) Integer.MAX_VALUE;
	}

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
	    /*
	     * pixel_flow by bezo97,
	     * https://bezo97.tk/plugins.html
	     */
		double a_rad = angle * 0.0174532925;// deg to rad, *pi/180
		double sina = sin(a_rad);
		double cosa = cos(a_rad);
		int blockx = (int) floor(pAffineTP.x * width);
		blockx += (2.0 - 4.0 * hash(blockx * seed + 1));// varying width and length
		int blocky = (int) floor(pAffineTP.y * width);
		blocky += (2.0 - 4.0 * hash(blocky * seed + 1));
		double fLen = (hash(blocky + blockx * -seed) + hash(blockx + blocky * seed / 2)) * 0.5;// Doesn't matter just
																								// needs to be random
																								// enough
		double r01 = pContext.random();
		double fade = fLen * r01 * r01 * r01 * r01;// fading effect
		pVarTP.x += pAmount * len * cosa * fade;
		pVarTP.y += pAmount * len * sina * fade;

		if (enable_dc == 1)
			pVarTP.color = r01;// direct color
		if (pContext.isPreserveZCoordinate()) {
			pVarTP.z += pAmount * pAffineTP.z;
		}
	}

	@Override
	public String[] getParameterNames() {
		return paramNames;
	}

	@Override
	public Object[] getParameterValues() {
		return new Object[] { angle, len, width, seed, enable_dc };
	}

	@Override
	public String[] getParameterAlternativeNames() {
		return new String[] { "angle", "len", "width", "seed", "enable_dc" };
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_ANGLE.equalsIgnoreCase(pName)) {
			angle = pValue;
		} else if (PARAM_LEN.equalsIgnoreCase(pName)) {
			len = pValue;
		} else if (PARAM_WIDTH.equalsIgnoreCase(pName)) {
			width = pValue;
		} else if (PARAM_SEED.equalsIgnoreCase(pName)) {
			seed = Tools.FTOI(pValue);
		} else if (PARAM_ENABLE_DC.equalsIgnoreCase(pName)) {
			enable_dc = (int) limitVal(pValue, 0, 1);

		} else
			throw new IllegalArgumentException(pName);
	}

	@Override
	public String getName() {
		return "pixel_flow";
	}

}
