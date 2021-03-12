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

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.sqr;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class Hypertile3D2bFunc extends VariationFunc {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_P = "p";
	private static final String PARAM_Q = "q";

	private static final String PARAM_B = "b";
	private static final String PARAM_C = "c";
	private static final String PARAM_D = "d";
	private static final String PARAM_E = "e";
	private static final String PARAM_F = "f";
	private static final String PARAM_G = "g";
	private static final String PARAM_H = "h";
	private static final String PARAM_I = "i";
	private static final String PARAM_J = "j";
	private static final String PARAM_K = "k";

	private static final String[] paramNames = { PARAM_P, PARAM_Q, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F, PARAM_G,
			PARAM_H, PARAM_I, PARAM_J, PARAM_K };

	private int p = 3;
	private int q = 7;

	private double b = 2.0;
	private double c = 2.0;
	private double d = 1.0;
	private double e = 1.0;
	private double f = 1.0;
	private double g = 2.0;
	private double h = 1.0;
	private double i = 1.0;
	private double j = 1.0;
	private double k = 1.0;

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
		/*
		 * hypertile3D2 by Zueuk,
		 * http://zueuk.deviantart.com/art/3D-Hyperbolic-tiling-plugins-169047926
		 * Variables added by Brad Stefanov
		 */
		double r2 = sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z);

		double x2cx = c2x * pAffineTP.x;

		double x = pAffineTP.x * s2x - cx * (-r2 - 1);
		double y = pAffineTP.y * s2y;

		double vr = pAmount / (c2 * r2 + x2cx + h);

		double a = pContext.random(Integer.MAX_VALUE) * pa;
		double sina = sin(a);
		double cosa = cos(a);

		pVarTP.x += vr * (x * cosa + y * sina);
		pVarTP.y += vr * (y * cosa - x * sina);
		pVarTP.z += vr * (pAffineTP.z * s2z);
	}

	@Override
	public String[] getParameterNames() {
		return paramNames;
	}

	@Override
	public Object[] getParameterValues() {
		return new Object[] { p, q, b, c, d, e, f, g, h, i, j, k };
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_P.equalsIgnoreCase(pName))
			p = limitIntVal(Tools.FTOI(pValue), 3, Integer.MAX_VALUE);
		else if (PARAM_Q.equalsIgnoreCase(pName))
			q = limitIntVal(Tools.FTOI(pValue), 3, Integer.MAX_VALUE);
		else if (PARAM_B.equalsIgnoreCase(pName)) {
			b = pValue;
		} else if (PARAM_C.equalsIgnoreCase(pName)) {
			c = pValue;
		} else if (PARAM_D.equalsIgnoreCase(pName)) {
			d = pValue;
		} else if (PARAM_E.equalsIgnoreCase(pName)) {
			e = pValue;
		} else if (PARAM_F.equalsIgnoreCase(pName)) {
			f = pValue;
		} else if (PARAM_G.equalsIgnoreCase(pName)) {
			g = pValue;
		} else if (PARAM_H.equalsIgnoreCase(pName)) {
			h = pValue;
		} else if (PARAM_I.equalsIgnoreCase(pName)) {
			i = pValue;
		} else if (PARAM_J.equalsIgnoreCase(pName)) {
			j = pValue;
		} else if (PARAM_K.equalsIgnoreCase(pName)) {
			k = pValue;
		} else
			throw new IllegalArgumentException(pName);
	}

	@Override
	public String getName() {
		return "hypertile3D2b";
	}

	private double pa, qa, cx, c2, c2x, s2x, s2y, s2z;

	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		pa = b * M_PI / p;
		qa = c * M_PI / q;

		double r = -(cos(pa) - d) / (cos(pa) + cos(qa));
		if (r > 0)
			r = e / sqrt(f + r);
		else
			r = 1;

		cx = r;
		c2 = sqr(cx);
		c2x = g * cx;

		s2x = i + sqr(cx);
		s2y = j - sqr(cx);
		s2z = k - sqr(cx);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_3D};
	}

}
