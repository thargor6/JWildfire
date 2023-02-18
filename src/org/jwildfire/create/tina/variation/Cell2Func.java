/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke
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
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.floor;

public class Cell2Func extends VariationFunc {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_SIZE = "size";
	private static final String PARAM_XAXIS = "mirror_x";
	private static final String PARAM_YAXIS = "mirror_y";
	private static final String PARAM_A = "a";
	private static final String SPACE_YA = "space_ya";
	private static final String SPACE_XA = "space_xa";
	private static final String SPACE_YB = "space_yb";
	private static final String SPACE_XB = "space_xb";
	private static final String MOVE_XA = "move_xa";
	private static final String SPACE_YC = "space_yc";
	private static final String MOVE_YA = "move_ya";
	private static final String SPACE_XC = "space_xc";
	private static final String SPACE_YD = "space_yd";
	private static final String MOVE_YB = "move_yb";
	private static final String SPACE_XD = "space_xd";
	private static final String MOVE_XB= "move_xb";

	private static final String[] paramNames = { PARAM_SIZE, PARAM_XAXIS, PARAM_YAXIS, PARAM_A, SPACE_YA, SPACE_XA,
			SPACE_YB, SPACE_XB, MOVE_XA, SPACE_YC, MOVE_YA, SPACE_XC, SPACE_YD, MOVE_YB, SPACE_XD, MOVE_XB };

	private double size = 0.60;
	private int mirror_x = 0;
	private int mirror_y = 0;
	private double a = 1.0;
	private double space_ya = 2.0;
	private double space_xa = 2.0;
	private double space_yb = 2.0;
	private double space_xb = 2.0;
	private double move_xa = 1.0;
	private double space_yc = 2.0;
	private double move_ya = 1.0;
	private double space_xc = 2.0;
	private double space_yd = 2.0;
	private double move_yb = 1.0;
	private double space_xd = 2.0;
	private double move_xb = 1.0;

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
		/* Cell in the Apophysis Plugin Pack 
		 * Variables added by Brad Stefanov*/

		double inv_cell_size = a / size;

		/* calculate input cell */
		double x = (int) floor(pAffineTP.x * inv_cell_size);
		double y = (int) floor(pAffineTP.y * inv_cell_size);

		/* Offset from cell origin */
		double dx = pAffineTP.x - x * size;
		double dy = pAffineTP.y - y * size;

		/* interleave cells */
		if (y >= 0) {
			if (x >= 0) {
				y *= space_ya;
				x *= space_xa;
			} else {
				y *= space_yb;
				x = -(space_xb * x + move_xa);
			}
		} else {
			if (x >= 0) {
				y = -(space_yc * y + move_ya);
				x *= space_xc;
			} else {
				y = -(space_yd * y + move_yb);
				x = -(space_xd * x + move_xb);
			}
		}

		pVarTP.x += pAmount * (dx + x * size);

		pVarTP.y -= pAmount * (dy + y * size);

		if (mirror_x > 0 && pContext.random() < 0.5) {
			pVarTP.x = -pVarTP.x;
		}

		if (mirror_y > 0 && pContext.random() < 0.5) {
			pVarTP.y = -pVarTP.y;
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
		return new Object[] { size, mirror_x, mirror_y, a, space_ya, space_xa, space_yb, space_xb, move_xa, space_yc, move_ya, space_xc, space_yd, move_yb, space_xd, move_xb };
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_SIZE.equalsIgnoreCase(pName))
			size = pValue;
		else if (PARAM_XAXIS.equalsIgnoreCase(pName))
			mirror_x = Tools.FTOI(pValue);
		else if (PARAM_YAXIS.equalsIgnoreCase(pName))
			mirror_y = Tools.FTOI(pValue);
		else if (PARAM_A.equalsIgnoreCase(pName))
			a = pValue;
		else if (SPACE_YA.equalsIgnoreCase(pName))
			space_ya = pValue;
		else if (SPACE_XA.equalsIgnoreCase(pName))
			space_xa = pValue;
		else if (SPACE_YB.equalsIgnoreCase(pName))
			space_yb = pValue;
		else if (SPACE_XB.equalsIgnoreCase(pName))
			space_xb = pValue;
		else if (MOVE_XA.equalsIgnoreCase(pName))
			move_xa = pValue;
		else if (SPACE_YC.equalsIgnoreCase(pName))
			space_yc = pValue;
		else if (MOVE_YA.equalsIgnoreCase(pName))
			move_ya = pValue;
		else if (SPACE_XC.equalsIgnoreCase(pName))
			space_xc = pValue;
		else if (SPACE_YD.equalsIgnoreCase(pName))
			space_yd = pValue;
		else if (MOVE_YB.equalsIgnoreCase(pName))
			move_yb = pValue;
		else if (SPACE_XD.equalsIgnoreCase(pName))
			space_xd = pValue;
		else if (MOVE_XB.equalsIgnoreCase(pName))
			move_xb = pValue;
		else
			throw new IllegalArgumentException(pName);
	}

	@Override
	public String getName() {
		return "cell2";
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[] { VariationFuncType.VARTYPE_2D };
	}
}
