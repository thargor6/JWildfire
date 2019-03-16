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

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.fmod;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class DCCarpet3DFunc extends VariationFunc {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_ORIGIN = "origin";
	private static final String PARAM_COLOR_A = "color_a";
	private static final String PARAM_COLOR_B = "color_b";
	private static final String PARAM_COLOR_C = "color_c";
	private static final String PARAM_COLOR_D = "color_d";
	private static final String PARAM_COLOR_E = "color_e";
	private static final String PARAM_COLOR_F = "color_f";
	private static final String PARAM_STRETCH_X = "stretch_x";
	private static final String PARAM_STRETCH_Y = "stretch_y";
	private static final String PARAM_SCALE_X = "scale_x";
	private static final String PARAM_SCALE_Y = "scale_y";
	private static final String PARAM_SCALEZ = "scale_z";
	private static final String PARAM_OFFSETZ = "offset_z";
	private static final String PARAM_RESETZ = "reset_z";

	private static final String[] paramNames = { PARAM_ORIGIN, PARAM_COLOR_A, PARAM_COLOR_B, PARAM_COLOR_C,
			PARAM_COLOR_D, PARAM_COLOR_E, PARAM_COLOR_F, PARAM_STRETCH_X, PARAM_STRETCH_Y, PARAM_SCALE_X, PARAM_SCALE_Y,
			PARAM_SCALEZ, PARAM_OFFSETZ, PARAM_RESETZ };
	private double origin = 0.5;
	private double color_a = 0.5;
	private double color_b = 1.0;
	private double color_c = 1.0;
	private double color_d = 1.0;
	private double color_e = 0.5;
	private double color_f = 1.0;
	private double stretch_x = 1.0;
	private double stretch_y = 1.0;
	private double scale_x = 1.0;
	private double scale_y = 1.0;
	private double scale_z = 1.0;
	private double offset_z = 0.0;
	private double reset_z = 0.0;

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
		/*
		 * dc_carpet by Xyrus02, http://apophysis-7x.org/extensions 
		 * added variables by Brad Stefanov
		 */
		int x0 = (pContext.random() < 0.5) ? -1 : 1;
		int y0 = (pContext.random() > 0.5) ? -1 : 1;

		double x = pAffineTP.x + x0 * stretch_x, y = pAffineTP.y + y0 * stretch_y;
		double x0_xor_y0 = (double) (x0 ^ y0);
		double hh = -H + (color_b - x0_xor_y0) * H;

		pVarTP.x += pAmount * (pXForm.getXYCoeff00() * x + pXForm.getXYCoeff10() * y + pXForm.getXYCoeff20()) * scale_x;
		pVarTP.y += pAmount * (pXForm.getXYCoeff01() * x + pXForm.getXYCoeff11() * y + pXForm.getXYCoeff21()) * scale_y;
		pVarTP.color = fmod(fabs(pVarTP.color * color_a * (color_c + hh) + x0_xor_y0 * (color_d - hh) * color_e),
				color_f);

		if (pContext.isPreserveZCoordinate()) {
			pVarTP.z += pAmount * pAffineTP.z;
		}
		double dz = pVarTP.color * scale_z + offset_z;
		if (reset_z > 0) {
			pVarTP.z = dz;
		} else {
			pVarTP.z += dz;
		}
	}

	@Override
	public String[] getParameterNames() {
		return paramNames;
	}

	@Override
	public Object[] getParameterValues() {
		return new Object[] { origin, color_a, color_b, color_c, color_d, color_e, color_f, stretch_x, stretch_y,
				scale_x, scale_y, scale_z, offset_z, reset_z };
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_ORIGIN.equalsIgnoreCase(pName)) {
			origin = pValue;
		} else if (PARAM_COLOR_A.equalsIgnoreCase(pName)) {
			color_a = pValue;
		} else if (PARAM_COLOR_B.equalsIgnoreCase(pName)) {
			color_b = pValue;
		} else if (PARAM_COLOR_C.equalsIgnoreCase(pName)) {
			color_c = pValue;
		} else if (PARAM_COLOR_D.equalsIgnoreCase(pName)) {
			color_d = pValue;
		} else if (PARAM_COLOR_E.equalsIgnoreCase(pName)) {
			color_e = pValue;
		} else if (PARAM_COLOR_F.equalsIgnoreCase(pName)) {
			color_f = pValue;
		} else if (PARAM_STRETCH_X.equalsIgnoreCase(pName)) {
			stretch_x = pValue;
		} else if (PARAM_STRETCH_Y.equalsIgnoreCase(pName)) {
			stretch_y = pValue;
		} else if (PARAM_SCALE_X.equalsIgnoreCase(pName)) {
			scale_x = pValue;
		} else if (PARAM_SCALE_Y.equalsIgnoreCase(pName)) {
			scale_y = pValue;
		} else if (PARAM_SCALEZ.equalsIgnoreCase(pName))
			scale_z = pValue;
		else if (PARAM_OFFSETZ.equalsIgnoreCase(pName))
			offset_z = pValue;
		else if (PARAM_RESETZ.equalsIgnoreCase(pName))
			reset_z = pValue;
		else {
			System.out.println("pName not recognized: " + pName);
			throw new IllegalArgumentException(pName);
		}
	}

	@Override
	public String getName() {
		return "dc_carpet3D";
	}

	private double H;

	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		H = 0.1 * origin;

	}

}
