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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class JuliaScope3DbFunc extends VariationFunc {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_POWER = "power";
	private static final String PARAM_ZBIST = "dist";
	private static final String PARAM_TYPE = "type";

	private static final String PARAM_WARP = "warp";
	private static final String PARAM_ZA = "za";
	private static final String PARAM_ZB = "zb";
	private static final String PARAM_ZAMOUNT = "zamount";
	private static final String PARAM_ZDIST = "zdist";
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_WAVE1 = "wave1";
	private static final String PARAM_WAVE2 = "wave2";

	private static final String[] paramNames = { PARAM_POWER, PARAM_ZBIST, PARAM_TYPE, PARAM_WARP, PARAM_ZA, PARAM_ZB,
			PARAM_ZAMOUNT, PARAM_ZDIST, PARAM_MODE, PARAM_WAVE1, PARAM_WAVE2 };

	private double power = genRandomPower();
	private double dist = 1;
	private int type = 0;
	private double warp = 0.0;
	private double za = 1.0;
	private double zb = 1.0;
	private double zamount = 1.0;
	private double zdist = 1.0;
	private int mode = 0;
	private double wave1 = 1.0;
	private double wave2 = 1.0;

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
		// if (power == 2)
		// transformPower2(pContext, pXForm, pAffineTP, pVarTP, pAmount);
		// else if (power == -2)
		// transformPowerMinus2(pContext, pXForm, pAffineTP, pVarTP, pAmount);
		// else if (power == 1)
		// transformPower1(pContext, pXForm, pAffineTP, pVarTP, pAmount);
		// else if (power == -1)
		// transformPowerMinus1(pContext, pXForm, pAffineTP, pVarTP, pAmount);
		// else
		transformFunction(pContext, pXForm, pAffineTP, pVarTP, pAmount);
	}
    // 3D version of juliascope, with added variables by Brad Stefanov. 
    // mode (0,1) type (0,1)  wave1 and wave 2 only work with mode = 1
	public void transformFunction(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP,
			XYZPoint pVarTP, double pAmount) {

		double z = pAffineTP.z / absPower + warp;
		double r2d = pAffineTP.x * pAffineTP.x * za + pAffineTP.y * pAffineTP.y * zb;
		double rz = pAmount * zamount * pow(r2d + z * z, cPower * zdist);

		int rnd = pContext.random(absPower);
		double a;
		if ((rnd & 1) == 0)
			a = (2 * M_PI * rnd + atan2(pAffineTP.y, pAffineTP.x)) / power;
		else
			a = (2 * M_PI * rnd - atan2(pAffineTP.y, pAffineTP.x)) / power;

		double r;
		if (type != 0) {
			r = pAmount * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z), cPower);
		} else {
			r = pAmount * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), cPower);
		}
		double cosa = cos(a);
		double sina = sin(a);

		pVarTP.x = pVarTP.x + r * cosa;
		pVarTP.y = pVarTP.y + r * sina;
		if (mode != 0) {
			double sincosa = sin(wave1) * cos(wave2);
			pVarTP.z += rz * z * sincosa;
		} else {
			pVarTP.z += rz * z;
		}
		// pVarTP.z += rz * z * sincosa;

	}

	@Override
	public String[] getParameterNames() {
		return paramNames;
	}

	@Override
	public Object[] getParameterValues() {
		return new Object[] { power, dist, type, warp, za, zb, zamount, zdist, mode, wave1, wave2 };
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_POWER.equalsIgnoreCase(pName))
			power = pValue;
		else if (PARAM_ZBIST.equalsIgnoreCase(pName))
			dist = pValue;
		else if (PARAM_TYPE.equalsIgnoreCase(pName))
			type = (int) limitVal(pValue, 0, 1);
		else if (PARAM_WARP.equalsIgnoreCase(pName)) {
			warp = pValue;
		} else if (PARAM_ZA.equalsIgnoreCase(pName)) {
			za = pValue;
		} else if (PARAM_ZB.equalsIgnoreCase(pName)) {
			zb = pValue;
		} else if (PARAM_ZAMOUNT.equalsIgnoreCase(pName)) {
			zamount = pValue;
		} else if (PARAM_ZDIST.equalsIgnoreCase(pName)) {
			zdist = pValue;
		} else if (PARAM_MODE.equalsIgnoreCase(pName))
			mode = (int) limitVal(pValue, 0, 1);
		else if (PARAM_WAVE1.equalsIgnoreCase(pName)) {
			wave1 = pValue;
		} else if (PARAM_WAVE2.equalsIgnoreCase(pName)) {
			wave2 = pValue;
		} else {
			System.out.println("pName not recognized: " + pName);
			throw new IllegalArgumentException(pName);
		}
	}

	@Override
	public String getName() {
		return "juliascope3Db";
	}

	private int genRandomPower() {
		int res = (int) (Math.random() * 5.0 + 2.5);
		return Math.random() < 0.5 ? res : -res;
	}

	private int absPower;
	private double cPower;

	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		absPower = iabs(Tools.FTOI(power));
		cPower = dist / power * 0.5;
	}

}
