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
import org.jwildfire.base.Tools;

public class TQMirrorFunc extends VariationFunc {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_PRESET = "preset";
	private static final String PARAM_A = "a";
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
	private static final String PARAM_L = "l";
	private static final String PARAM_M = "m";
	private static final String PARAM_N = "n";
	private static final String PARAM_O = "o";
	private static final String PARAM_P = "p";
	private static final String PARAM_Q = "q";
	private static final String PARAM_R = "r";
	private static final String PARAM_S = "s";
	private static final String PARAM_TYPE = "type";
	private static final String PARAM_MODE = "mode";

	private static final String[] paramNames = { PARAM_PRESET, PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F,
			PARAM_G, PARAM_H, PARAM_I, PARAM_J, PARAM_K, PARAM_L, PARAM_M, PARAM_N, PARAM_O, PARAM_P, PARAM_Q, PARAM_R,
			PARAM_S, PARAM_TYPE, PARAM_MODE };
	private int preset = 0;
	private double a = 1.0;
	private double b = 1.0;
	private double c = 1.0;
	private double d = 1.0;
	private double e = 1.0;
	private double f = 1.0;
	private double g = 1.0;
	private double h = 1.0;
	private double i = 1.0;
	private double j = 1.0;
	private double k = 1.0;
	private double l = 0.0;
	private double m = 0.0;
	private double n = 0.0;
	private double o = 0.0;
	private double p = 0.0;
	private double q = 0.0;
	private double r = 1.0;
	private double s = 1.0;
	private int type = 0;
	private int mode = 0;

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
		// Created by Nic Anderson (chronologicaldot), August 15, 2015
		// variables added by Brad Stefanov
		// Shift a box, whose width and height are pAmount, from the upper left of the
		// graph to the lower right.
		// Then flip everything else over the y=x axis.

		double x = pAffineTP.x;
		double y = pAffineTP.y;

		if (pAmount * d + x < l || pAmount * e + y < m) // too far
		{
			if (type != 0) {
				pVarTP.x += x * r;
				pVarTP.y += y * s;
			} else {
				pVarTP.x += y * r;
				pVarTP.y += x * s;
			}
			return;
		}

		if (x < n && y < o) {
			pVarTP.x += x + pAmount * f;
			pVarTP.y += y + pAmount * g;

		} else {
			if (mode == 0) {
				if (x + q < pAmount && y < pAmount * a && x + pAmount * b > l && y + pAmount * c > p) {

					pVarTP.x -= y * h;
					pVarTP.y -= x * i;
				} else {
					pVarTP.x += x * j;
					pVarTP.y += y * k;
				}
			} else if (mode == 1) {
				if (x + q < pAmount && y < pAmount * a && x + pAmount * b > l && y + pAmount * c > p) {
					pVarTP.x -= y * h;
					pVarTP.y -= x * i;
				} else {
					pVarTP.x -= x * j;
					pVarTP.y -= y * k;
				}
			} else if (mode == 2) {
				if (x + q < pAmount && y < pAmount * a && x + pAmount * b > l && y + pAmount * c > p) {
					pVarTP.x += y * h;
					pVarTP.y -= x * i;
				} else {
					pVarTP.x += x * j;
					pVarTP.y -= y * k;
				}
			}
		}
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
		return new Object[] { preset, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, type, mode };
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_PRESET.equalsIgnoreCase(pName)) {
			preset = Tools.FTOI(pValue);
			switch (preset) {
			case 0:
				a = 1.0;
				b = 1.0;
				c = 1.0;
				d = 1.0;
				e = 1.0;
				f = 1.0;
				g = 1.0;
				h = 1.0;
				i = 1.0;
				j = 1.0;
				k = 1.0;
				l = 0.0;
				m = 0.0;
				n = 0.0;
				o = 0.0;
				p = 0.0;
				q = 0.0;
				r = 1.0;
				s = 1.0;
				break;
			case 1:
				a = 1.0;
				b = 1.0;
				c = 1.0;
				d = 1.0;
				e = 1.0;
				f = 0.0;
				g = 1.0;
				h = -1.0;
				i = 1.0;
				j = 1.0;
				k = 1.0;
				l = 0.0;
				m = 0.0;
				n = 0.0;
				o = 0.0;
				p = 0.0;
				q = 0.0;
				r = 1.0;
				s = 1.0;
				break;
			case 2:
				a = 1.0;
				b = 1.0;
				c = 1.0;
				d = 1.0;
				e = 1.0;
				f = 0.0;
				g = 0.0;
				h = -1.0;
				i = -1.0;
				j = 1.0;
				k = 1.0;
				l = 0.0;
				m = 0.0;
				n = 0.0;
				o = 0.0;
				p = 0.0;
				q = 0.0;
				r = 1.0;
				s = 1.0;
				break;
			case 3:
				a = 1.0;
				b = 1.0;
				c = 1.0;
				d = 1.0;
				e = 1.0;
				f = 1.0;
				g = 0.0;
				h = 1.0;
				i = 1.0;
				j = 1.0;
				k = 1.0;
				l = 0.0;
				m = 0.0;
				n = 0.0;
				o = -1.0;
				p = 0.0;
				q = 0.0;
				r = 1.0;
				s = 1.0;
				break;
			}
		}

		else if (PARAM_A.equalsIgnoreCase(pName)) {
			a = pValue;
		} else if (PARAM_B.equalsIgnoreCase(pName)) {
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
		} else if (PARAM_L.equalsIgnoreCase(pName)) {
			l = pValue;
		} else if (PARAM_M.equalsIgnoreCase(pName)) {
			m = pValue;
		} else if (PARAM_N.equalsIgnoreCase(pName)) {
			n = pValue;
		} else if (PARAM_O.equalsIgnoreCase(pName)) {
			o = pValue;
		} else if (PARAM_P.equalsIgnoreCase(pName)) {
			p = pValue;
		} else if (PARAM_Q.equalsIgnoreCase(pName)) {
			q = pValue;
		} else if (PARAM_R.equalsIgnoreCase(pName)) {
			r = pValue;
		} else if (PARAM_S.equalsIgnoreCase(pName)) {
			s = pValue;
		} else if (PARAM_TYPE.equalsIgnoreCase(pName)) {
			type = (int) limitVal(pValue, 0, 1);
		} else if (PARAM_MODE.equalsIgnoreCase(pName)) {
			mode = (int) limitVal(pValue, 0, 2);
		} else {
			System.out.println("pName not recognized: " + pName);
			throw new IllegalArgumentException(pName);
		}
	}

	@Override
	public String getName() {
		return "tqmirror";
	}

	@Override
	public boolean dynamicParameterExpansion() {
		return true;
	}

	@Override
	public boolean dynamicParameterExpansion(String pName) {
		// preset_id doesn't really expand parameters, but it changes them; this will
		// make them refresh
		return true;
	}

}
