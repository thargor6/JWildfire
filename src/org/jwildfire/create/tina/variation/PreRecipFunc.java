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
import org.jwildfire.base.mathlib.Complex;
import static org.jwildfire.base.mathlib.MathLib.M_2_PI;

public class PreRecipFunc extends VariationFunc implements SupportsGPU {

	private static final long serialVersionUID = 1L;

	private static final String PARAM_RECIPROCALPOW = "reciprocalpow";
	private static final String PARAM_DIVIDEPOW = "dividepow";
	private static final String PARAM_SQRTPOW = "sqrtpow";

	private static final String PARAM_ASINHPOW = "asinhpow";
	private static final String PARAM_ACOSHPOW = "acoshpow";
	private static final String PARAM_ATANHPOW = "atanhpow";

	private static final String PARAM_ASECHPOW = "asechpow";
	private static final String PARAM_ACOSECHPOW = "acosechpow";
	private static final String PARAM_ACOTHPOW = "acothpow";
	private static final String PARAM_LOGPOW = "logpow";
	private static final String PARAM_EXPPOW = "exppow";
	private static final String PARAM_ZXMULT = "zx_mult";
	private static final String PARAM_ZYMULT = "zy_mult";
	private static final String PARAM_ZXADD = "zx_add";
	private static final String PARAM_ZYADD = "zy_add";
	private static final String[] paramNames = { PARAM_RECIPROCALPOW, PARAM_DIVIDEPOW, PARAM_SQRTPOW, PARAM_ASINHPOW,
			PARAM_ACOSHPOW, PARAM_ATANHPOW, PARAM_ASECHPOW, PARAM_ACOSECHPOW, PARAM_ACOTHPOW, PARAM_LOGPOW,
			PARAM_EXPPOW, PARAM_ZXMULT, PARAM_ZYMULT, PARAM_ZXADD, PARAM_ZYADD };

	private double reciprocalpow = 1.0;
	private double dividepow = 0.0;
	private double sqrtpow = 0.0;

	private double asinhpow = 0.0;
	private double acoshpow = 0.0;
	private double atanhpow = 0.0;

	private double asechpow = 0.0;
	private double acosechpow = 0.0;
	private double acothpow = 0.0;
	private double logpow = 0.0;
	private double exppow = 0.0;

	private double zx_mult = 1.0;
	private double zy_mult = 1.0;
	private double zx_add = 0.0;
	private double zy_add = 0.0;

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
		// pre_recip by Whittaker Courtney
		double xx = 0.0, yy = 0.0;
		double xr = pAffineTP.x, yr = pAffineTP.y;

		if (reciprocalpow != 0) {
			Complex z = new Complex(pAffineTP.x * zx_mult + zx_add, pAffineTP.y * zy_mult + zy_add);

			z.Recip();

			z.Scale(pAmount);

			xr = reciprocalpow * z.re;
			yr = reciprocalpow * z.im;
		}

		if (dividepow != 0) {
			Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);
			Complex z2 = new Complex(xr, yr);

			z2.Dec();
			z.Inc();
			z.Div(z2);

			z.Scale(pAmount * M_2_PI);

			xr = dividepow * z.re;
			yr = dividepow * z.im;
		}

		if (sqrtpow != 0) {
			Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);

			z.Sqrt();

			z.Scale(pAmount);

			if (pContext.random() < 0.5) {
				xr = sqrtpow * z.re;
				yr = sqrtpow * z.im;
			} else {
				xr = sqrtpow * -z.re;
				yr = sqrtpow * -z.im;
			}
		}

		if (asinhpow != 0) {
			Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);

			z.AsinH();

			z.Scale(pAmount * M_2_PI);

			xx += asinhpow * z.re;
			yy += asinhpow * z.im;

		}

		if (acoshpow != 0) {
			Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);

			z.AcosH();

			z.Scale(pAmount * M_2_PI);

			if (pContext.random() < 0.5) {
				xx += acoshpow * z.re;
				yy += acoshpow * z.im;
			} else {
				xx += acoshpow * -z.re;
				yy += acoshpow * -z.im;
			}
		}

		if (atanhpow != 0) {
			Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);

			z.AtanH();

			z.Scale(pAmount * M_2_PI);

			xx += atanhpow * z.re;
			yy += atanhpow * z.im;

		}

		if (asechpow != 0) {
			Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);

			z.AsecH();

			z.Scale(pAmount * M_2_PI);

			xx += asechpow * z.re;
			yy += asechpow * z.im;

		}

		if (acosechpow != 0) {
			Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);

			z.AcosecH();

			z.Scale(pAmount * M_2_PI);

			if (pContext.random() < 0.5) {
				xx += acosechpow * z.re;
				yy += acosechpow * z.im;
			} else {
				xx += acosechpow * -z.re;
				yy += acosechpow * -z.im;
			}
		}

		if (acothpow != 0) {
			Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);

			z.AcotH();

			z.Scale(pAmount * M_2_PI);

			xx += acothpow * z.re;
			yy += acothpow * z.im;

		}

		if (logpow != 0) {
			Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);

			z.Log();

			z.Scale(pAmount * M_2_PI);

			xx += logpow * z.re;
			yy += logpow * z.im;

		}

		if (exppow != 0) {
			Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);

			z.Exp();

			z.Scale(pAmount * M_2_PI);

			xx += exppow * z.re;
			yy += exppow * z.im;

		}

		if (asinhpow + acoshpow + atanhpow + asechpow + acosechpow + acothpow + logpow + exppow == 0) {
			pAffineTP.x = xr;
			pAffineTP.y = yr;
		}

		else {
			pAffineTP.x = xx;
			pAffineTP.y = yy;
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
		return new Object[] { reciprocalpow, dividepow, sqrtpow, asinhpow, acoshpow, atanhpow, asechpow, acosechpow,
				acothpow, logpow, exppow, zx_mult, zy_mult, zx_add, zy_add };
	}

	@Override
	public void setParameter(String pName, double pValue) {

		if (PARAM_RECIPROCALPOW.equalsIgnoreCase(pName))
			reciprocalpow = pValue;
		else if (PARAM_DIVIDEPOW.equalsIgnoreCase(pName))
			dividepow = pValue;
		else if (PARAM_SQRTPOW.equalsIgnoreCase(pName))
			sqrtpow = pValue;
		else if (PARAM_ASINHPOW.equalsIgnoreCase(pName))
			asinhpow = pValue;
		else if (PARAM_ACOSHPOW.equalsIgnoreCase(pName))
			acoshpow = pValue;
		else if (PARAM_ATANHPOW.equalsIgnoreCase(pName))
			atanhpow = pValue;
		else if (PARAM_ASECHPOW.equalsIgnoreCase(pName))
			asechpow = pValue;
		else if (PARAM_ACOSECHPOW.equalsIgnoreCase(pName))
			acosechpow = pValue;
		else if (PARAM_ACOTHPOW.equalsIgnoreCase(pName))
			acothpow = pValue;
		else if (PARAM_LOGPOW.equalsIgnoreCase(pName))
			logpow = pValue;
		else if (PARAM_EXPPOW.equalsIgnoreCase(pName))
			exppow = pValue;
		else if (PARAM_ZXMULT.equalsIgnoreCase(pName))
			zx_mult = pValue;
		else if (PARAM_ZYMULT.equalsIgnoreCase(pName))
			zy_mult = pValue;
		else if (PARAM_ZXADD.equalsIgnoreCase(pName))
			zx_add = pValue;
		else if (PARAM_ZYADD.equalsIgnoreCase(pName))
			zy_add = pValue;
		else
			throw new IllegalArgumentException(pName);
	}

	@Override
	public String getName() {
		return "pre_recip";
	}

	@Override
	public int getPriority() {
		return -1;
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_PRE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}

	@Override
	public String getGPUCode(FlameTransformationContext context) {
    return "float xx = 0.0, yy = 0.0;\n"
        + "float xr = __x, yr = __y;\n"
        + "\n"
        + "if (__pre_recip_reciprocalpow != 0) {\n"
        + "   Complex z; Complex_Init(&z, __x * __pre_recip_zx_mult + __pre_recip_zx_add, __y * __pre_recip_zy_mult + __pre_recip_zy_add);\n"
        + "   Complex_Recip(&z);\n"
        + "   Complex_Scale(&z, __pre_recip);\n"
        + "   xr = __pre_recip_reciprocalpow * z.re;\n"
        + "   yr = __pre_recip_reciprocalpow * z.im;\n"
        + "}\n"
        + "if (__pre_recip_dividepow != 0) {\n"
        + "   Complex z; Complex_Init(&z, xr * __pre_recip_zx_mult + __pre_recip_zx_add, yr * __pre_recip_zy_mult + __pre_recip_zy_add);\n"
        + "   Complex z2; Complex_Init(&z2, xr, yr);\n"
        + "   Complex_Dec(&z2);\n"
        + "   Complex_Inc(&z);\n"
        + "   Complex_Div(&z, &z2);\n"
        + "   Complex_Scale(&z, __pre_recip * (2.0f / PI));\n"
        + "   xr = __pre_recip_dividepow * z.re;\n"
        + "   yr = __pre_recip_dividepow * z.im;\n"
        + "}\n"
        + "if (__pre_recip_sqrtpow != 0) {\n"
        + "   Complex z; Complex_Init(&z, xr * __pre_recip_zx_mult + __pre_recip_zx_add, yr * __pre_recip_zy_mult + __pre_recip_zy_add);\n"
        + "   Complex_Sqrt(&z);\n"
        + "   Complex_Scale(&z, __pre_recip);\n"
        + "   if (RANDFLOAT() < 0.5) {\n"
        + "    xr = __pre_recip_sqrtpow * z.re;\n"
        + "    yr = __pre_recip_sqrtpow * z.im;\n"
        + "   } else {\n"
        + "    xr = __pre_recip_sqrtpow * -z.re;\n"
        + "    yr = __pre_recip_sqrtpow * -z.im;\n"
        + "   }\n"
        + "}\n"
        + "if (__pre_recip_asinhpow != 0) {\n"
        + "   Complex z; Complex_Init(&z, xr * __pre_recip_zx_mult + __pre_recip_zx_add, yr * __pre_recip_zy_mult + __pre_recip_zy_add);\n"
        + "   Complex_AsinH(&z);\n"
        + "   Complex_Scale(&z, __pre_recip * (2.0f / PI));\n"
        + "   xx += __pre_recip_asinhpow * z.re;\n"
        + "   yy += __pre_recip_asinhpow * z.im;\n"
        + "}\n"
        + "if (__pre_recip_acoshpow != 0) {\n"
        + "   Complex z; Complex_Init(&z, xr * __pre_recip_zx_mult + __pre_recip_zx_add, yr * __pre_recip_zy_mult + __pre_recip_zy_add);\n"
        + "   Complex_AcosH(&z);\n"
        + "   Complex_Scale(&z, __pre_recip * (2.0f / PI));\n"
        + "   if (RANDFLOAT() < 0.5) {\n"
        + "    xx += __pre_recip_acoshpow * z.re;\n"
        + "    yy += __pre_recip_acoshpow * z.im;\n"
        + "   } else {\n"
        + "    xx += __pre_recip_acoshpow * -z.re;\n"
        + "    yy += __pre_recip_acoshpow * -z.im;\n"
        + "   }\n"
        + "}\n"
        + "if (__pre_recip_atanhpow != 0) {\n"
        + "   Complex z; Complex_Init(&z, xr * __pre_recip_zx_mult + __pre_recip_zx_add, yr * __pre_recip_zy_mult + __pre_recip_zy_add);\n"
        + "   Complex_AtanH(&z);\n"
        + "   Complex_Scale(&z, __pre_recip * (2.0f / PI));\n"
        + "   xx += __pre_recip_atanhpow * z.re;\n"
        + "   yy += __pre_recip_atanhpow * z.im;\n"
        + "}\n"
        + "if (__pre_recip_asechpow != 0) {\n"
        + "   Complex z; Complex_Init(&z, xr * __pre_recip_zx_mult + __pre_recip_zx_add, yr * __pre_recip_zy_mult + __pre_recip_zy_add);\n"
        + "   Complex_AsecH(&z);\n"
        + "   Complex_Scale(&z, __pre_recip * (2.0f / PI));\n"
        + "   xx += __pre_recip_asechpow * z.re;\n"
        + "   yy += __pre_recip_asechpow * z.im;\n"
        + "}\n"
        + "if (__pre_recip_acosechpow != 0) {\n"
        + "   Complex z; Complex_Init(&z, xr * __pre_recip_zx_mult + __pre_recip_zx_add, yr * __pre_recip_zy_mult + __pre_recip_zy_add);\n"
        + "   Complex_AcosecH(&z);\n"
        + "   Complex_Scale(&z, __pre_recip * (2.0f / PI));\n"
        + "   if (RANDFLOAT() < 0.5) {\n"
        + "    xx += __pre_recip_acosechpow * z.re;\n"
        + "    yy += __pre_recip_acosechpow * z.im;\n"
        + "   } else {\n"
        + "    xx += __pre_recip_acosechpow * -z.re;\n"
        + "    yy += __pre_recip_acosechpow * -z.im;\n"
        + "   }\n"
        + "}\n"
        + "if (__pre_recip_acothpow != 0) {\n"
        + "   Complex z; Complex_Init(&z, xr * __pre_recip_zx_mult + __pre_recip_zx_add, yr * __pre_recip_zy_mult + __pre_recip_zy_add);\n"
        + "   Complex_AcotH(&z);\n"
        + "   Complex_Scale(&z, __pre_recip * (2.0f / PI));\n"
        + "   xx += __pre_recip_acothpow * z.re;\n"
        + "   yy += __pre_recip_acothpow * z.im;\n"
        + "}\n"
        + "if (__pre_recip_logpow != 0) {\n"
        + "   Complex z; Complex_Init(&z, xr * __pre_recip_zx_mult + __pre_recip_zx_add, yr * __pre_recip_zy_mult + __pre_recip_zy_add);\n"
        + "   Complex_Log(&z);\n"
        + "   Complex_Scale(&z, __pre_recip * (2.0f / PI));\n"
        + "   xx += __pre_recip_logpow * z.re;\n"
        + "   yy += __pre_recip_logpow * z.im;\n"
        + "}\n"
        + "if (__pre_recip_exppow != 0) {\n"
        + "   Complex z; Complex_Init(&z, xr * __pre_recip_zx_mult + __pre_recip_zx_add, yr * __pre_recip_zy_mult + __pre_recip_zy_add);\n"
        + "   Complex_Exp(&z);\n"
        + "   Complex_Scale(&z, __pre_recip * (2.0f / PI));\n"
        + "   xx += __pre_recip_exppow * z.re;\n"
        + "   yy += __pre_recip_exppow * z.im;\n"
        + "}\n"
        + "if (__pre_recip_asinhpow + __pre_recip_acoshpow + __pre_recip_atanhpow + __pre_recip_asechpow + __pre_recip_acosechpow + __pre_recip_acothpow + __pre_recip_logpow + __pre_recip_exppow == 0) {\n"
        + "   __x = xr;\n"
        + "   __y = yr;\n"
        + "}\n"
        + "else {\n"
        + "   __x = xx;\n"
        + "   __y = yy;\n"
        + "}\n"
        + "\n"
        + (context.isPreserveZCoordinate() ? "   __pz += __pre_recip * __z;\n" : "");
	}
}
