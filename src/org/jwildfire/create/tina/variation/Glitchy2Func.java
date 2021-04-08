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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Glitchy2Func extends VariationFunc {
	private static final long serialVersionUID = 1L;
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_LR_SPIN = "lr_spin";
	private static final String PARAM_LR_X = "lr_x";
	private static final String PARAM_LR_SHIFTX = "lr_shiftx";
	private static final String PARAM_LR_Y = "lr_y";
	private static final String PARAM_LR_SHIFTY = "lr_shifty";
	private static final String PARAM_LR_RE_A = "lr_re_a";
	private static final String PARAM_LR_RE_B = "lr_re_b";
	private static final String PARAM_LR_RE_C = "lr_re_c";
	private static final String PARAM_LR_RE_D = "lr_re_d";
	private static final String PARAM_LR_IM_A = "lr_im_a";
	private static final String PARAM_LR_IM_B = "lr_im_b";
	private static final String PARAM_LR_IM_C = "lr_im_c";
	private static final String PARAM_LR_IM_D = "lr_im_d";
	private static final String PARAM_UR_SPIN = "ur_spin";
	private static final String PARAM_UR_X = "ur_x";
	private static final String PARAM_UR_SHIFTX = "ur_shiftx";
	private static final String PARAM_UR_Y = "ur_y";
	private static final String PARAM_UR_SHIFTY = "ur_shifty";
	private static final String PARAM_UR_RE_A = "ur_re_a";
	private static final String PARAM_UR_RE_B = "ur_re_b";
	private static final String PARAM_UR_RE_C = "ur_re_c";
	private static final String PARAM_UR_RE_D = "ur_re_d";
	private static final String PARAM_UR_IM_A = "ur_im_a";
	private static final String PARAM_UR_IM_B = "ur_im_b";
	private static final String PARAM_UR_IM_C = "ur_im_c";
	private static final String PARAM_UR_IM_D = "ur_im_d";
	private static final String PARAM_LL_SPIN = "ll_spin";
	private static final String PARAM_LL_X = "ll_x";
	private static final String PARAM_LL_SHIFTX = "ll_shiftx";
	private static final String PARAM_LL_Y = "ll_y";
	private static final String PARAM_LL_SHIFTY = "ll_shifty";
	private static final String PARAM_LL_RE_A = "ll_re_a";
	private static final String PARAM_LL_RE_B = "ll_re_b";
	private static final String PARAM_LL_RE_C = "ll_re_c";
	private static final String PARAM_LL_RE_D = "ll_re_d";
	private static final String PARAM_LL_IM_A = "ll_im_a";
	private static final String PARAM_LL_IM_B = "ll_im_b";
	private static final String PARAM_LL_IM_C = "ll_im_c";
	private static final String PARAM_LL_IM_D = "ll_im_d";
	private static final String PARAM_UL_SPIN = "ul_spin";
	private static final String PARAM_UL_X = "ul_x";
	private static final String PARAM_UL_SHIFTX = "ul_shiftx";
	private static final String PARAM_UL_Y = "ul_y";
	private static final String PARAM_UL_SHIFTY = "ul_shifty";
	private static final String PARAM_UL_RE_A = "ul_re_a";
	private static final String PARAM_UL_RE_B = "ul_re_b";
	private static final String PARAM_UL_RE_C = "ul_re_c";
	private static final String PARAM_UL_RE_D = "ul_re_d";
	private static final String PARAM_UL_IM_A = "ul_im_a";
	private static final String PARAM_UL_IM_B = "ul_im_b";
	private static final String PARAM_UL_IM_C = "ul_im_c";
	private static final String PARAM_UL_IM_D = "ul_im_d";

	private static final String[] paramNames = { PARAM_MODE, PARAM_LR_SPIN, PARAM_LR_X, PARAM_LR_SHIFTX, PARAM_LR_Y,
			PARAM_LR_SHIFTY, PARAM_LR_RE_A, PARAM_LR_RE_B, PARAM_LR_RE_C, PARAM_LR_RE_D, PARAM_LR_IM_A, PARAM_LR_IM_B,
			PARAM_LR_IM_C, PARAM_LR_IM_D, PARAM_UR_SPIN, PARAM_UR_X, PARAM_UR_SHIFTX, PARAM_UR_Y, PARAM_UR_SHIFTY,
			PARAM_UR_RE_A, PARAM_UR_RE_B, PARAM_UR_RE_C, PARAM_UR_RE_D, PARAM_UR_IM_A, PARAM_UR_IM_B, PARAM_UR_IM_C,
			PARAM_UR_IM_D, PARAM_LL_SPIN, PARAM_LL_X, PARAM_LL_SHIFTX, PARAM_LL_Y, PARAM_LL_SHIFTY, PARAM_LL_RE_A,
			PARAM_LL_RE_B, PARAM_LL_RE_C, PARAM_LL_RE_D, PARAM_LL_IM_A, PARAM_LL_IM_B, PARAM_LL_IM_C, PARAM_LL_IM_D,
			PARAM_UL_SPIN, PARAM_UL_X, PARAM_UL_SHIFTX, PARAM_UL_Y, PARAM_UL_SHIFTY, PARAM_UL_RE_A, PARAM_UL_RE_B,
			PARAM_UL_RE_C, PARAM_UL_RE_D, PARAM_UL_IM_A, PARAM_UL_IM_B, PARAM_UL_IM_C, PARAM_UL_IM_D };

	private int mode = 0;
	private double lr_spin = 1.0;
	private double lr_x = 0.0;
	private double lr_shiftx = 0.0;
	private double lr_y = 0.0;
	private double lr_shifty = 0.0;
	private double lr_re_a = 0.0;
	private double lr_re_b = 0.0;
	private double lr_re_c = 0.0;
	private double lr_re_d = 1.0;
	private double lr_im_a = 0.0;
	private double lr_im_b = 0.0;
	private double lr_im_c = 0.0;
	private double lr_im_d = 0.0;
	private double ur_spin = -1.0;
	private double ur_x = 0.0;
	private double ur_shiftx = 0.0;
	private double ur_y = 0.0;
	private double ur_shifty = 0.0;
	private double ur_re_a = 0.0;
	private double ur_re_b = 0.0;
	private double ur_re_c = 0.0;
	private double ur_re_d = 1.0;
	private double ur_im_a = 0.;
	private double ur_im_b = 0.0;
	private double ur_im_c = 0.0;
	private double ur_im_d = 0.0;
	private double ll_spin = -1.0;
	private double ll_x = 0.0;
	private double ll_shiftx = 0.0;
	private double ll_y = 0.0;
	private double ll_shifty = 0.0;
	private double ll_re_a = 0.0;
	private double ll_re_b = 0.0;
	private double ll_re_c = 0.0;
	private double ll_re_d = 1.0;
	private double ll_im_a = 0.;
	private double ll_im_b = 0.0;
	private double ll_im_c = 0.0;
	private double ll_im_d = 0.0;
	private double ul_spin = 1.0;
	private double ul_x = 0.0;
	private double ul_shiftx = 0.0;
	private double ul_y = 0.0;
	private double ul_shifty = 0.0;
	private double ul_re_a = 0.0;
	private double ul_re_b = 0.0;
	private double ul_re_c = 0.0;
	private double ul_re_d = 1.0;
	private double ul_im_a = 0.;
	private double ul_im_b = 0.0;
	private double ul_im_c = 0.0;
	private double ul_im_d = 0.0;

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
		/*
		 * Glitchy2 by Brad Stefanov and Rick Sidwell  used idea/code from the fourth variation by
		 * guagapunyaimel,
		 * https://www.deviantart.com/guagapunyaimel/art/Fourth-Plugin-175043938
		 */

		int count = 0;
		double x = 0.0;
		double y = 0.0;

		if (pAffineTP.x > lr_shiftx && pAffineTP.y > lr_shifty) // kuadran IV: LowerRright
		{
			// Mobius, by eralex
			double lr_re_u = lr_re_a * pAffineTP.x - lr_im_a * pAffineTP.y + lr_re_b;
			double lr_im_u = lr_re_a * pAffineTP.y + lr_im_a * pAffineTP.x + lr_im_b;
			double lr_re_v = lr_re_c * pAffineTP.x - lr_im_c * pAffineTP.y + lr_re_d;
			double lr_im_v = lr_re_c * pAffineTP.y + lr_im_c * pAffineTP.x + lr_im_d;

			double d = (lr_re_v * lr_re_v + lr_im_v * lr_im_v);
			if (d == 0) {
				return;
			}

			x += pAmount * (lr_pz_sin * pAffineTP.y + lr_pz_cos * pAffineTP.x) + lr_x
					+ (d * (lr_re_u * lr_re_v + lr_im_u * lr_im_v));
			y += pAmount * (lr_pz_cos * pAffineTP.y - lr_pz_sin * pAffineTP.x) + lr_y
					+ (d * (lr_im_u * lr_re_v - lr_re_u * lr_im_v));
			count += 1;

		}
		if (pAffineTP.x > ur_shiftx && pAffineTP.y < ur_shifty) // kuadran I: UpperRight
		{
			// Mobius, by eralex
			double ur_re_u = ur_re_a * pAffineTP.x - ur_im_a * pAffineTP.y + ur_re_b;
			double ur_im_u = ur_re_a * pAffineTP.y + ur_im_a * pAffineTP.x + ur_im_b;
			double ur_re_v = ur_re_c * pAffineTP.x - ur_im_c * pAffineTP.y + ur_re_d;
			double ur_im_v = ur_re_c * pAffineTP.y + ur_im_c * pAffineTP.x + ur_im_d;

			double d = (ur_re_v * ur_re_v + ur_im_v * ur_im_v);
			if (d == 0) {
				return;
			}


			x += pAmount * (ur_pz_sin * pAffineTP.y + ur_pz_cos * pAffineTP.x) + ur_x
					+ (d * (ur_re_u * ur_re_v + ur_im_u * ur_im_v));
			y += pAmount * (ur_pz_cos * pAffineTP.y - ur_pz_sin * pAffineTP.x) + ur_y
					+ (d * (ur_im_u * ur_re_v - ur_re_u * ur_im_v));
			count += 1;

		}
		if (pAffineTP.x < ll_shiftx && pAffineTP.y > ll_shifty) // kuadran III: LowerLeft
		{
			// Mobius, by eralex
			double ll_re_u = ll_re_a * pAffineTP.x - ll_im_a * pAffineTP.y + ll_re_b;
			double ll_im_u = ll_re_a * pAffineTP.y + ll_im_a * pAffineTP.x + ll_im_b;
			double ll_re_v = ll_re_c * pAffineTP.x - ll_im_c * pAffineTP.y + ll_re_d;
			double ll_im_v = ll_re_c * pAffineTP.y + ll_im_c * pAffineTP.x + ll_im_d;

			double d = (ll_re_v * ll_re_v + ll_im_v * ll_im_v);
			if (d == 0) {
				return;
			}


			x += pAmount * (ll_pz_sin * pAffineTP.y + ll_pz_cos * pAffineTP.x) + ll_x
					+ (d * (ll_re_u * ll_re_v + ll_im_u * ll_im_v));
			y += pAmount * (ll_pz_cos * pAffineTP.y - ll_pz_sin * pAffineTP.x) + ll_y
					+ (d * (ll_im_u * ll_re_v - ll_re_u * ll_im_v));
			count += 1;

		}
		if (pAffineTP.x < ul_shiftx && pAffineTP.y < ul_shifty) // kuadran II: UpperLeft
		{
			// Mobius, by eralex
			double ul_re_u = ul_re_a * pAffineTP.x - ul_im_a * pAffineTP.y + ul_re_b;
			double ul_im_u = ul_re_a * pAffineTP.y + ul_im_a * pAffineTP.x + ul_im_b;
			double ul_re_v = ul_re_c * pAffineTP.x - ul_im_c * pAffineTP.y + ul_re_d;
			double ul_im_v = ul_re_c * pAffineTP.y + ul_im_c * pAffineTP.x + ul_im_d;

			double d = (ul_re_v * ul_re_v + ul_im_v * ul_im_v);
			if (d == 0) {
				return;
			}

			x += pAmount * (ul_pz_sin * pAffineTP.y + ul_pz_cos * pAffineTP.x) + ul_x
					+ (d * (ul_re_u * ul_re_v + ul_im_u * ul_im_v));
			y += pAmount * (ul_pz_cos * pAffineTP.y - ul_pz_sin * pAffineTP.x) + ul_y
					+ (d * (ul_im_u * ul_re_v - ul_re_u * ul_im_v));
			count += 1;

		}
		if (mode != 0) {
		if (count == 0) { // point isn't in any quadrant
			pVarTP.x += pAmount / pAffineTP.y;
			pVarTP.y += pAmount / pAffineTP.x;
		} else {
			pVarTP.x += pAmount / y / count;
			pVarTP.y += pAmount / x / count;
		}
		} else {
		if (count == 0) { // point isn't in any quadrant
			pVarTP.x += pAmount * pAffineTP.x;
			pVarTP.y += pAmount * pAffineTP.y;
		} else {
			pVarTP.x += pAmount * x / count;
			pVarTP.y += pAmount * y / count;
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
		return new Object[] { mode,  lr_spin, lr_x, lr_shiftx, lr_y, lr_shifty, lr_re_a, lr_re_b, lr_re_c, lr_re_d, lr_im_a,
				lr_im_b, lr_im_c, lr_im_d, ur_spin, ur_x, ur_shiftx, ur_y, ur_shifty, ur_re_a, ur_re_b, ur_re_c,
				ur_re_d, ur_im_a, ur_im_b, ur_im_c, ur_im_d, ll_spin, ll_x, ll_shiftx, ll_y, ll_shifty, ll_re_a,
				ll_re_b, ll_re_c, ll_re_d, ll_im_a, ll_im_b, ll_im_c, ll_im_d, ul_spin, ul_x, ul_shiftx, ul_y,
				ul_shifty, ul_re_a, ul_re_b, ul_re_c, ul_re_d, ul_im_a, ul_im_b, ul_im_c, ul_im_d };
	}

	@Override
	public void setParameter(String pName, double pValue) {

		if (PARAM_MODE.equalsIgnoreCase(pName))
			mode = (int) limitVal(pValue, 0, 1);
		else if (PARAM_LR_SPIN.equalsIgnoreCase(pName))
			lr_spin = pValue;
		else if (PARAM_LR_X.equalsIgnoreCase(pName))
			lr_x = pValue;
		else if (PARAM_LR_SHIFTX.equalsIgnoreCase(pName))
			lr_shiftx = pValue;
		else if (PARAM_LR_Y.equalsIgnoreCase(pName))
			lr_y = pValue;
		else if (PARAM_LR_SHIFTY.equalsIgnoreCase(pName))
			lr_shifty = pValue;
		else if (PARAM_LR_RE_A.equalsIgnoreCase(pName))
			lr_re_a = pValue;
		else if (PARAM_LR_RE_B.equalsIgnoreCase(pName))
			lr_re_b = pValue;
		else if (PARAM_LR_RE_C.equalsIgnoreCase(pName))
			lr_re_c = pValue;
		else if (PARAM_LR_RE_D.equalsIgnoreCase(pName))
			lr_re_d = pValue;
		else if (PARAM_LR_IM_A.equalsIgnoreCase(pName))
			lr_im_a = pValue;
		else if (PARAM_LR_IM_B.equalsIgnoreCase(pName))
			lr_im_b = pValue;
		else if (PARAM_LR_IM_C.equalsIgnoreCase(pName))
			lr_im_c = pValue;
		else if (PARAM_LR_IM_D.equalsIgnoreCase(pName))
			lr_im_d = pValue;
		else if (PARAM_UR_SPIN.equalsIgnoreCase(pName))
			ur_spin = pValue;
		else if (PARAM_UR_X.equalsIgnoreCase(pName))
			ur_x = pValue;
		else if (PARAM_UR_SHIFTX.equalsIgnoreCase(pName))
			ur_shiftx = pValue;
		else if (PARAM_UR_Y.equalsIgnoreCase(pName))
			ur_y = pValue;
		else if (PARAM_UR_SHIFTY.equalsIgnoreCase(pName))
			ur_shifty = pValue;
		else if (PARAM_UR_RE_A.equalsIgnoreCase(pName))
			ur_re_a = pValue;
		else if (PARAM_UR_RE_B.equalsIgnoreCase(pName))
			ur_re_b = pValue;
		else if (PARAM_UR_RE_C.equalsIgnoreCase(pName))
			ur_re_c = pValue;
		else if (PARAM_UR_RE_D.equalsIgnoreCase(pName))
			ur_re_d = pValue;
		else if (PARAM_UR_IM_A.equalsIgnoreCase(pName))
			ur_im_a = pValue;
		else if (PARAM_UR_IM_B.equalsIgnoreCase(pName))
			ur_im_b = pValue;
		else if (PARAM_UR_IM_C.equalsIgnoreCase(pName))
			ur_im_c = pValue;
		else if (PARAM_UR_IM_D.equalsIgnoreCase(pName))
			ur_im_d = pValue;
		else if (PARAM_LL_SPIN.equalsIgnoreCase(pName))
			ll_spin = pValue;
		else if (PARAM_LL_X.equalsIgnoreCase(pName))
			ll_x = pValue;
		else if (PARAM_LL_SHIFTX.equalsIgnoreCase(pName))
			ll_shiftx = pValue;
		else if (PARAM_LL_Y.equalsIgnoreCase(pName))
			ll_y = pValue;
		else if (PARAM_LL_SHIFTY.equalsIgnoreCase(pName))
			ll_shifty = pValue;
		else if (PARAM_LL_RE_A.equalsIgnoreCase(pName))
			ll_re_a = pValue;
		else if (PARAM_LL_RE_B.equalsIgnoreCase(pName))
			ll_re_b = pValue;
		else if (PARAM_LL_RE_C.equalsIgnoreCase(pName))
			ll_re_c = pValue;
		else if (PARAM_LL_RE_D.equalsIgnoreCase(pName))
			ll_re_d = pValue;
		else if (PARAM_LL_IM_A.equalsIgnoreCase(pName))
			ll_im_a = pValue;
		else if (PARAM_LL_IM_B.equalsIgnoreCase(pName))
			ll_im_b = pValue;
		else if (PARAM_LL_IM_C.equalsIgnoreCase(pName))
			ll_im_c = pValue;
		else if (PARAM_LL_IM_D.equalsIgnoreCase(pName))
			ll_im_d = pValue;
		else if (PARAM_UL_SPIN.equalsIgnoreCase(pName))
			ul_spin = pValue;
		else if (PARAM_UL_X.equalsIgnoreCase(pName))
			ul_x = pValue;
		else if (PARAM_UL_SHIFTX.equalsIgnoreCase(pName))
			ul_shiftx = pValue;
		else if (PARAM_UL_Y.equalsIgnoreCase(pName))
			ul_y = pValue;
		else if (PARAM_UL_SHIFTY.equalsIgnoreCase(pName))
			ul_shifty = pValue;
		else if (PARAM_UL_RE_A.equalsIgnoreCase(pName))
			ul_re_a = pValue;
		else if (PARAM_UL_RE_B.equalsIgnoreCase(pName))
			ul_re_b = pValue;
		else if (PARAM_UL_RE_C.equalsIgnoreCase(pName))
			ul_re_c = pValue;
		else if (PARAM_UL_RE_D.equalsIgnoreCase(pName))
			ul_re_d = pValue;
		else if (PARAM_UL_IM_A.equalsIgnoreCase(pName))
			ul_im_a = pValue;
		else if (PARAM_UL_IM_B.equalsIgnoreCase(pName))
			ul_im_b = pValue;
		else if (PARAM_UL_IM_C.equalsIgnoreCase(pName))
			ul_im_c = pValue;
		else if (PARAM_UL_IM_D.equalsIgnoreCase(pName))
			ul_im_d = pValue;
		else
			throw new IllegalArgumentException(pName);
	}

	@Override
	public String getName() {
		return "glitchy2";
	}

	private double lr_pz_sin, lr_pz_cos, ur_pz_sin, ur_pz_cos, ll_pz_sin, ll_pz_cos, ul_pz_sin, ul_pz_cos;

	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		lr_pz_sin = sin(lr_spin * M_PI_2);
		lr_pz_cos = cos(lr_spin * M_PI_2);
		ur_pz_sin = sin(ur_spin * M_PI_2);
		ur_pz_cos = cos(ur_spin * M_PI_2);
		ll_pz_sin = sin(ll_spin * M_PI_2);
		ll_pz_cos = cos(ll_spin * M_PI_2);
		ul_pz_sin = sin(ul_spin * M_PI_2);
		ul_pz_cos = cos(ul_spin * M_PI_2);
	}
	
	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D};
	}

}
