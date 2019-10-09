/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2019 Andreas Maschke
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
import static org.jwildfire.base.mathlib.MathLib.*;

public class ShreddedFunc extends VariationFunc {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_X1 = "x1";
	private static final String PARAM_X2 = "x2";
	private static final String PARAM_X3 = "x3";
	private static final String PARAM_XBLURAMOUNT = "x_blur_amount";
	private static final String PARAM_Y1 = "y1";
	private static final String PARAM_Y2 = "y2";
	private static final String PARAM_Y3 = "y3";
	private static final String PARAM_YBLURAMOUNT = "y_blur_amount";
	private static final String PARAM_Z1 = "z1";
	private static final String PARAM_Z2 = "z2";
	private static final String PARAM_Z3 = "z3";
	private static final String PARAM_ZBLURAMOUNT = "z_blur_amount";
	private static final String PARAM_TYPE = "type";
	private static final String PARAM_BLUR = "blur";

	private static final String[] paramNames = { PARAM_X1, PARAM_X2, PARAM_X3, PARAM_XBLURAMOUNT, PARAM_Y1, PARAM_Y2,
			PARAM_Y3, PARAM_YBLURAMOUNT, PARAM_Z1, PARAM_Z2, PARAM_Z3, PARAM_ZBLURAMOUNT, PARAM_TYPE, PARAM_BLUR };
	private double x1 = 1.0;
	private double x2 = 3.0;
	private double x3 = 1.0;
	private double x_blur_amount = 0.1;
	private double y1 = 1.0;
	private double y2 = 3.0;
	private double y3 = 1.0;
	private double y_blur_amount = 0.1;
	private double z1 = 1.0;
	private double z2 = 3.0;
	private double z3 = 1.0;
	private double z_blur_amount = 0.1;
	private int type = 0;
	private int blur = 0;

	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {

		// Shredded by Brad Stefanov and Rick Sidwell

		if (blur != 0) {
			if (type == 2) {
				pVarTP.x += pAmount * x3 * (sin(x1 * floor(x2 * pAffineTP.x)))
						+ (x_blur_amount * sin(M_2PI * pContext.random())) * pAffineTP.x;
				pVarTP.y += pAmount * y3 * (cos(y1 * floor(y2 * pAffineTP.y)))
						+ (y_blur_amount * sin(M_2PI * pContext.random())) * pAffineTP.y;
				pVarTP.z += pAmount * z3 * (sin(z1 * floor(z2 * pAffineTP.z)))
						+ (z_blur_amount * sin(M_2PI * pContext.random()));
			} else if (type == 1) {
				pVarTP.x += pAmount * x3 * (sin(x1 * floor(x2 * pAffineTP.x)))
						+ (x_blur_amount * sin(M_2PI * pContext.random())) * pAffineTP.y;
				pVarTP.y += pAmount * y3 * (cos(y1 * floor(y2 * pAffineTP.y)))
						+ (y_blur_amount * sin(M_2PI * pContext.random())) * pAffineTP.x;
				pVarTP.z += pAmount * z3 * (sin(z1 * floor(z2 * pAffineTP.z)))
						+ (z_blur_amount * sin(M_2PI * pContext.random()));
			} else if (type == 0) {
				pVarTP.x += pAmount * x3 * (sin(x1 * floor(x2 * pAffineTP.x)) + cos(x1 * floor(x2 * pAffineTP.y)))
						+ (x_blur_amount * sin(M_2PI * pContext.random())) * pAffineTP.y;
				pVarTP.y += pAmount * y3 * (cos(y1 * floor(y2 * pAffineTP.y)) + sin(y1 * floor(y2 * pAffineTP.x)))
						+ (y_blur_amount * sin(M_2PI * pContext.random())) * pAffineTP.x;
				pVarTP.z += pAmount * z3 * (sin(z1 * floor(z2 * pAffineTP.z)) + cos(z1 * floor(z2 * pAffineTP.z)))
						+ (z_blur_amount * sin(M_2PI * pContext.random())) * pAffineTP.z;
			}

		} else {
			if (type == 2) {
				pVarTP.x += pAmount * x3 * sin(x1 * floor(x2 * pAffineTP.x)) * pAffineTP.x;
				pVarTP.y += pAmount * y3 * cos(y1 * floor(y2 * pAffineTP.y)) * pAffineTP.y;
				pVarTP.z += pAmount * z3 * sin(z1 * floor(z2 * pAffineTP.z)) * pAffineTP.z;
			} else if (type == 1) {
				pVarTP.x += pAmount * x3 * sin(x1 * floor(x2 * pAffineTP.x)) * pAffineTP.y;
				pVarTP.y += pAmount * y3 * cos(y1 * floor(y2 * pAffineTP.y)) * pAffineTP.x;
				pVarTP.z += pAmount * z3 * sin(z1 * floor(z2 * pAffineTP.z)) * pAffineTP.z;
			} else if (type == 0) {
				pVarTP.x += pAmount * x3 * (sin(x1 * floor(x2 * pAffineTP.x)) + cos(x1 * floor(x2 * pAffineTP.y)))
						* pAffineTP.y;
				pVarTP.y += pAmount * y3 * (cos(y1 * floor(y2 * pAffineTP.y)) + sin(y1 * floor(y2 * pAffineTP.x)))
						* pAffineTP.x;
				pVarTP.z += pAmount * z3 * (sin(z1 * floor(z2 * pAffineTP.z)) + cos(z1 * floor(z2 * pAffineTP.z)))
						* pAffineTP.z;
			}

		}
	}

	@Override
	public String[] getParameterNames() {
		return paramNames;
	}

	@Override
	public Object[] getParameterValues() {
		return new Object[] { x1, x2, x3, x_blur_amount, y1, y2, y3, y_blur_amount, z1, z2, z3, z_blur_amount, type,
				blur };
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_X1.equalsIgnoreCase(pName)) {
			x1 = pValue;
		} else if (PARAM_X2.equalsIgnoreCase(pName)) {
			x2 = pValue;
		} else if (PARAM_X3.equalsIgnoreCase(pName)) {
			x3 = pValue;
		} else if (PARAM_XBLURAMOUNT.equalsIgnoreCase(pName)) {
			x_blur_amount = pValue;
		} else if (PARAM_Y1.equalsIgnoreCase(pName)) {
			y1 = pValue;
		} else if (PARAM_Y2.equalsIgnoreCase(pName)) {
			y2 = pValue;
		} else if (PARAM_Y3.equalsIgnoreCase(pName)) {
			y3 = pValue;
		} else if (PARAM_YBLURAMOUNT.equalsIgnoreCase(pName)) {
			y_blur_amount = pValue;
		} else if (PARAM_Z1.equalsIgnoreCase(pName)) {
			z1 = pValue;
		} else if (PARAM_Z2.equalsIgnoreCase(pName)) {
			z2 = pValue;
		} else if (PARAM_Z3.equalsIgnoreCase(pName)) {
			z3 = pValue;
		} else if (PARAM_ZBLURAMOUNT.equalsIgnoreCase(pName)) {
			z_blur_amount = pValue;
		} else if (PARAM_TYPE.equalsIgnoreCase(pName)) {
			type = (int) limitVal(pValue, 0, 2);
		} else if (PARAM_BLUR.equalsIgnoreCase(pName)) {
			blur = (int) limitVal(pValue, 0, 1);
		} else
			throw new IllegalArgumentException(pName);
	}

	public String getName() {
		return "shredded";
	}

}
