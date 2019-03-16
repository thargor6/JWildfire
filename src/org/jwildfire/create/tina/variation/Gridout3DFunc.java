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

import static org.jwildfire.base.mathlib.MathLib.rint;

public class Gridout3DFunc extends VariationFunc {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_XX = "xx";
	private static final String PARAM_YY = "yy";
	private static final String PARAM_XA = "xa";
	private static final String PARAM_XB = "xb";
	private static final String PARAM_XC = "xc";
	private static final String PARAM_XD = "xd";
	private static final String PARAM_XE = "xe";
	private static final String PARAM_XF = "xf";
	private static final String PARAM_XG = "xg";
	private static final String PARAM_XH = "xh";
	private static final String PARAM_YA = "ya";
	private static final String PARAM_YB = "yb";
	private static final String PARAM_YC = "yc";
	private static final String PARAM_YD = "yd";
	private static final String PARAM_YE = "ye";
	private static final String PARAM_YF = "yf";
	private static final String PARAM_YG = "yg";
	private static final String PARAM_YH = "yh";
	private static final String PARAM_ZA = "za";
	private static final String PARAM_ZB = "zb";
	private static final String PARAM_ZC = "zc";
	private static final String PARAM_ZD = "zd";
	private static final String PARAM_ZE = "ze";
	private static final String PARAM_ZF = "zf";
	private static final String PARAM_ZG = "zg";
	private static final String PARAM_ZH = "zh";
	private static final String[] paramNames = { PARAM_XX, PARAM_YY, PARAM_XA, PARAM_XB, PARAM_XC, PARAM_XD, PARAM_XE,
			PARAM_XF, PARAM_XG, PARAM_XH, PARAM_YA, PARAM_YB, PARAM_YC, PARAM_YD, PARAM_YE, PARAM_YF, PARAM_YG,
			PARAM_YH, PARAM_ZA, PARAM_ZB, PARAM_ZC, PARAM_ZD, PARAM_ZE, PARAM_ZF, PARAM_ZG, PARAM_ZH };

	private double xx = 1.0;
	private double yy = 1.0;
	private double xa = 1.0;
	private double xb = 0.0;
	private double xc = 1.0;
	private double xd = 0.0;
	private double xe = 1.0;
	private double xf = 0.0;
	private double xg = 1.0;
	private double xh = 0.0;
	private double ya = 0.0;
	private double yb = 1.0;
	private double yc = 0.0;
	private double yd = 1.0;
	private double ye = 0.0;
	private double yf = 1.0;
	private double yg = 0.0;
	private double yh = 1.0;
	private double za = 0.0;
	private double zb = 0.0;
	private double zc = 0.0;
	private double zd = 0.0;
	private double ze = 1.0;
	private double zf = 1.0;
	private double zg = 1.0;
	private double zh = 1.0;

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
		// authors Michael Faber, Joel Faber 2007-2008. Implemented by DarkBeam 2017
		// variables added by Brad Stefanov
		double x = rint(pAffineTP.x) * xx;
		double y = rint(pAffineTP.y) * yy;


		if (y <= 0.0) {
			if (x > 0.0) {
				if (-y >= x) {
					pVarTP.x += pAmount * (pAffineTP.x + xa);
					pVarTP.y += pAmount * (pAffineTP.y + ya);
					pVarTP.z += pAmount * (pAffineTP.z + za);
				} else {
					pVarTP.x += pAmount * (pAffineTP.x + xb);
					pVarTP.y += pAmount * (pAffineTP.y + yb);
					pVarTP.z += pAmount * (pAffineTP.z + zb);
				}
			} else {
				if (y <= x) {
					pVarTP.x += pAmount * (pAffineTP.x + xc);
					pVarTP.y += pAmount * (pAffineTP.y + yc);
					pVarTP.z += pAmount * (pAffineTP.z + zc);
				} else {
					pVarTP.x += pAmount * (pAffineTP.x + xd);
					pVarTP.y += pAmount * (pAffineTP.y - yd);
					pVarTP.z += pAmount * (pAffineTP.z + zd);
				}
			}
		} else  {
			if (x > 0.0) {
				if (y >= x ) {
					pVarTP.x += pAmount * (pAffineTP.x - xe);
					pVarTP.y += pAmount * (pAffineTP.y + ye);
					pVarTP.z += pAmount * (pAffineTP.z * ze);
				} else {
					pVarTP.x += pAmount * (pAffineTP.x + xf);
					pVarTP.y += pAmount * (pAffineTP.y + yf);
					pVarTP.z += pAmount * (pAffineTP.z * zf);
				}
			} else {
				if (y > -x) {
					pVarTP.x += pAmount * (pAffineTP.x - xg);
					pVarTP.y += pAmount * (pAffineTP.y + yg);
					pVarTP.z += pAmount * (pAffineTP.z * zg);
				} else {
					pVarTP.x += pAmount * (pAffineTP.x + xh);
					pVarTP.y += pAmount * (pAffineTP.y - yh);
					pVarTP.z += pAmount * (pAffineTP.z * zh);
				}
			}
		}
		

	}

	@Override
	public String[] getParameterNames() {
		return paramNames;
	}

	@Override
	public Object[] getParameterValues() {
		return new Object[] { xx, yy, xa, xb, xc, xd, xe, xf, xg, xh, ya, yb, yc, yd, ye, yf, yg, yh, za, zb, zc, zd,
				ze, zf, zg, zh };
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_XX.equalsIgnoreCase(pName))
			xx = pValue;
		else if (PARAM_YY.equalsIgnoreCase(pName))
			yy = pValue;
		else if (PARAM_XA.equalsIgnoreCase(pName))
			xa = pValue;
		else if (PARAM_XB.equalsIgnoreCase(pName))
			xb = pValue;
		else if (PARAM_XC.equalsIgnoreCase(pName))
			xc = pValue;
		else if (PARAM_XD.equalsIgnoreCase(pName))
			xd = pValue;
		else if (PARAM_XE.equalsIgnoreCase(pName))
			xe = pValue;
		else if (PARAM_XF.equalsIgnoreCase(pName))
			xf = pValue;
		else if (PARAM_XG.equalsIgnoreCase(pName))
			xg = pValue;
		else if (PARAM_XH.equalsIgnoreCase(pName))
			xh = pValue;
		else if (PARAM_YA.equalsIgnoreCase(pName))
			ya = pValue;
		else if (PARAM_YB.equalsIgnoreCase(pName))
			yb = pValue;
		else if (PARAM_YC.equalsIgnoreCase(pName))
			yc = pValue;
		else if (PARAM_YD.equalsIgnoreCase(pName))
			yd = pValue;
		else if (PARAM_YE.equalsIgnoreCase(pName))
			ye = pValue;
		else if (PARAM_YF.equalsIgnoreCase(pName))
			yf = pValue;
		else if (PARAM_YG.equalsIgnoreCase(pName))
			yg = pValue;
		else if (PARAM_YH.equalsIgnoreCase(pName))
			yh = pValue;
		else if (PARAM_ZA.equalsIgnoreCase(pName))
			za = pValue;
		else if (PARAM_ZB.equalsIgnoreCase(pName))
			zb = pValue;
		else if (PARAM_ZC.equalsIgnoreCase(pName))
			zc = pValue;
		else if (PARAM_ZD.equalsIgnoreCase(pName))
			zd = pValue;
		else if (PARAM_ZE.equalsIgnoreCase(pName))
			ze = pValue;
		else if (PARAM_ZF.equalsIgnoreCase(pName))
			zf = pValue;
		else if (PARAM_ZG.equalsIgnoreCase(pName))
			zg = pValue;
		else if (PARAM_ZH.equalsIgnoreCase(pName))
			zh = pValue;
		else
			throw new IllegalArgumentException(pName);

	}

	@Override
	public String getName() {
		return "gridout3D";
	}

}
