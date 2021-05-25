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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class Petal3DFunc extends VariationFunc implements SupportsGPU {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_MODE = "mode";
	private static final String PARAM_X1 = "x1";
	private static final String PARAM_X2 = "x2";
	private static final String PARAM_X3 = "x3";
	private static final String PARAM_X4 = "x4";
	private static final String PARAM_X5 = "x5";
	private static final String PARAM_X6 = "x6";
	private static final String PARAM_Y1 = "y1";
	private static final String PARAM_Y2 = "y2";
	private static final String PARAM_Y3 = "y3";
	private static final String PARAM_Y4 = "y4";
	private static final String PARAM_Y5 = "y5";
	private static final String PARAM_Y6 = "y6";
	private static final String PARAM_Z1 = "z1";
	private static final String PARAM_Z2 = "z2";
	private static final String PARAM_Z3 = "z3";
	private static final String PARAM_Z4 = "z4";
	private static final String PARAM_Z5 = "z5";
	private static final String PARAM_Z6 = "z6";
	private static final String PARAM_WARP = "warp";

	private int mode = 1;
	private double x1 = 0.0;
	private double x2 = 0.0;
	private double x3 = 0.0;
	private double x4 = 0.0;
	private double x5 = 0.0;
	private double x6 = 0.0;
	private double y1 = 0.0;
	private double y2 = 0.0;
	private double y3 = 0.0;
	private double y4 = 0.0;
	private double y5 = 0.0;
	private double y6 = 0.0;
	private double z1 = 0.0;
	private double z2 = 0.0;
	private double z3 = 0.0;
	private double z4 = 0.0;
	private double z5 = 0.0;
	private double z6 = 0.0;
	private double warp = 1.0;

	private static final String[] paramNames = { PARAM_MODE, PARAM_X1, PARAM_X2, PARAM_X3, PARAM_X4, PARAM_X5, PARAM_X6,
			PARAM_Y1, PARAM_Y2, PARAM_Y3, PARAM_Y4, PARAM_Y5, PARAM_Y6, PARAM_Z1, PARAM_Z2, PARAM_Z3, PARAM_Z4,
			PARAM_Z5, PARAM_Z6, PARAM_WARP };

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
		// petal by Raykoid666,
		// http://raykoid666.deviantart.com/art/re-pack-1-new-plugins-100092186
		// Added variables by Brad Stefanov
		double ax = cos(pAffineTP.x * warp);

		double bx = (cos(pAffineTP.x + x1) * cos(pAffineTP.y + x2)) * (cos(pAffineTP.x + x3) * cos(pAffineTP.y + x4))
				* (cos(pAffineTP.x + x5) * cos(pAffineTP.y + x6));
		double by = (sin(pAffineTP.x + y1) * cos(pAffineTP.y + y2)) * (sin(pAffineTP.x + y3) * cos(pAffineTP.y + y4))
				* (sin(pAffineTP.x + y5) * cos(pAffineTP.y + y6));
		double zy = (sin(pAffineTP.z + z1) * cos(pAffineTP.y + z2)) * (sin(pAffineTP.z + z3) * cos(pAffineTP.y + z4))
				* (sin(pAffineTP.z + z5) * cos(pAffineTP.y + z6));
		double zx = (sin(pAffineTP.z + z1) * cos(pAffineTP.x + z2)) * (sin(pAffineTP.z + z3) * cos(pAffineTP.x + z4))
				* (sin(pAffineTP.z + z5) * cos(pAffineTP.x + z6));
		double yz = (sin(pAffineTP.y + z1) * cos(pAffineTP.z + z2)) * (sin(pAffineTP.y + z3) * cos(pAffineTP.z + z4))
				* (sin(pAffineTP.y + z5) * cos(pAffineTP.z + z6));
		double xz = (sin(pAffineTP.x + z1) * cos(pAffineTP.z + z2)) * (sin(pAffineTP.x + z3) * cos(pAffineTP.z + z4))
				* (sin(pAffineTP.x + z5) * cos(pAffineTP.z + z6));
		pVarTP.x += pAmount * ax * bx;
		pVarTP.y += pAmount * ax * by;
		if (mode == 0) {
			pVarTP.z += pAmount * pAffineTP.z;
		} else if (mode == 1) {
			pVarTP.z += pAmount * ax * zx;
		} else if (mode == 2) {
			pVarTP.z += pAmount * ax * zy;
		} else if (mode == 3) {
			pVarTP.z += pAmount * ax * yz;
		} else if (mode == 4) {
			pVarTP.z += pAmount * ax * xz;
		}
	}

	@Override
	public String[] getParameterNames() {
		return paramNames;
	}

	@Override
	public Object[] getParameterValues() {
		return new Object[] { mode, x1, x2, x3, x4, x5, x6, y1, y2, y3, y4, y5, y6, z1, z2, z3, z4, z5, z6, warp };
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_MODE.equalsIgnoreCase(pName)) {
			mode = (int) limitVal(pValue, 0, 4);
		} else if (PARAM_X1.equalsIgnoreCase(pName)) {
			x1 = pValue;
		} else if (PARAM_X2.equalsIgnoreCase(pName)) {
			x2 = pValue;
		} else if (PARAM_X3.equalsIgnoreCase(pName)) {
			x3 = pValue;
		} else if (PARAM_X4.equalsIgnoreCase(pName)) {
			x4 = pValue;
		} else if (PARAM_X5.equalsIgnoreCase(pName)) {
			x5 = pValue;
		} else if (PARAM_X6.equalsIgnoreCase(pName)) {
			x6 = pValue;
		} else if (PARAM_Y1.equalsIgnoreCase(pName)) {
			y1 = pValue;
		} else if (PARAM_Y2.equalsIgnoreCase(pName)) {
			y2 = pValue;
		} else if (PARAM_Y3.equalsIgnoreCase(pName)) {
			y3 = pValue;
		} else if (PARAM_Y4.equalsIgnoreCase(pName)) {
			y4 = pValue;
		} else if (PARAM_Y5.equalsIgnoreCase(pName)) {
			y5 = pValue;
		} else if (PARAM_Y6.equalsIgnoreCase(pName)) {
			y6 = pValue;
		} else if (PARAM_Z1.equalsIgnoreCase(pName)) {
			z1 = pValue;
		} else if (PARAM_Z2.equalsIgnoreCase(pName)) {
			z2 = pValue;
		} else if (PARAM_Z3.equalsIgnoreCase(pName)) {
			z3 = pValue;
		} else if (PARAM_Z4.equalsIgnoreCase(pName)) {
			z4 = pValue;
		} else if (PARAM_Z5.equalsIgnoreCase(pName)) {
			z5 = pValue;
		} else if (PARAM_Z6.equalsIgnoreCase(pName)) {
			z6 = pValue;
		} else if (PARAM_WARP.equalsIgnoreCase(pName)) {
			warp = pValue;
		} else {
			System.out.println("pName not recognized: " + pName);
			throw new IllegalArgumentException(pName);
		}
	}

	@Override
	public String getName() {
		return "petal3D";
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}

	@Override
	public String getGPUCode(FlameTransformationContext context) {
    return "float ax = cosf(__x * varpar->petal3D_warp);\n"
        + "  float bx = (cosf(__x + varpar->petal3D_x1) * cosf(__y + varpar->petal3D_x2)) * (cosf(__x + varpar->petal3D_x3) * cosf(__y + varpar->petal3D_x4))\n"
        + "    * (cosf(__x + varpar->petal3D_x5) * cosf(__y + varpar->petal3D_x6));\n"
        + "  float by = (sinf(__x + varpar->petal3D_y1) * cosf(__y + varpar->petal3D_y2)) * (sinf(__x + varpar->petal3D_y3) * cosf(__y + varpar->petal3D_y4))\n"
        + "    * (sinf(__x + varpar->petal3D_y5) * cosf(__y + varpar->petal3D_y6));\n"
        + "  float zy = (sinf(__z + varpar->petal3D_z1) * cosf(__y + varpar->petal3D_z2)) * (sinf(__z + varpar->petal3D_z3) * cosf(__y + varpar->petal3D_z4))\n"
        + "    * (sinf(__z + varpar->petal3D_z5) * cosf(__y + varpar->petal3D_z6));\n"
        + "  float zx = (sinf(__z + varpar->petal3D_z1) * cosf(__x + varpar->petal3D_z2)) * (sinf(__z + varpar->petal3D_z3) * cosf(__x + varpar->petal3D_z4))\n"
        + "    * (sinf(__z + varpar->petal3D_z5) * cosf(__x + varpar->petal3D_z6));\n"
        + "  float yz = (sinf(__y + varpar->petal3D_z1) * cosf(__z + varpar->petal3D_z2)) * (sinf(__y + varpar->petal3D_z3) * cosf(__z + varpar->petal3D_z4))\n"
        + "    * (sinf(__y + varpar->petal3D_z5) * cosf(__z + varpar->petal3D_z6));\n"
        + "  float xz = (sinf(__x + varpar->petal3D_z1) * cosf(__z + varpar->petal3D_z2)) * (sinf(__x + varpar->petal3D_z3) * cosf(__z + varpar->petal3D_z4))\n"
        + "    * (sinf(__x + varpar->petal3D_z5) * cosf(__z + varpar->petal3D_z6));\n"
        + "  __px += varpar->petal3D * ax * bx;\n"
        + "  __py += varpar->petal3D * ax * by;\n"
				+ "  int mode = lroundf(varpar->petal3D_mode);\n"
        + "  if (mode == 0) {\n"
        + "   __pz += varpar->petal3D * __z;\n"
        + "  } else if (mode == 1) {\n"
        + "   __pz += varpar->petal3D * ax * zx;\n"
        + "  } else if (mode == 2) {\n"
        + "   __pz += varpar->petal3D * ax * zy;\n"
        + "  } else if (mode == 3) {\n"
        + "   __pz += varpar->petal3D * ax * yz;\n"
        + "  } else if (mode == 4) {\n"
        + "   __pz += varpar->petal3D * ax * xz;\n"
        + "  }\n";
	}
}
