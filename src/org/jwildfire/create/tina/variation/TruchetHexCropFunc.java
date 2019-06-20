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

import static java.lang.Math.abs;
import static org.jwildfire.base.mathlib.MathLib.*;

public class TruchetHexCropFunc extends VariationFunc {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_WD = "wd";
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_INV = "inv";
	private static final String PARAM_SEED = "seed";
	private static final String[] paramNames = { PARAM_WD, PARAM_MODE, PARAM_INV, PARAM_SEED };

	private double wd = 0.05;
	private int mode = 0;
	private int inv = 0;
	private int seed = 13;

	private final static double M_SQRT3_2 = 0.86602540378443864676372317075249;
	private final static double M_SQRT3 = 1.7320508075688772935274463415059;

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
		/*
		 * truchet_hex_crop from Tatyana Zabanova converted by Brad Stefanov
		 * https://www.deviantart.com/tatasz/art/Truchet-Hex-Crop-and-Truchet-Hex-Fill-Plugins-801687540
		 */

		// get hex
		double x = 0.57735026918962576450914878050196 * pAffineTP.x - pAffineTP.y / 3.0; // sqrt(3) / 3
		double z = 2.0 * pAffineTP.y / 3.0;
		double y = -x - z;

		// round
		double rx = round(x);
		double ry = round(y);
		double rz = round(z);

		double x_diff = abs(rx - x);
		double y_diff = abs(ry - y);
		double z_diff = abs(rz - z);

		if ((x_diff > y_diff) && (x_diff > z_diff)) { // only &
			rx = -ry - rz;
		} else if (y_diff > z_diff) {
			ry = -rx - rz;
		} else {
			rz = -rx - ry;
		}

		double FX_h = M_SQRT3 * rx + M_SQRT3_2 * rz;
		double FY_h = 1.5 * rz;

		double FX = pAffineTP.x - FX_h;
		double FY = pAffineTP.y - FY_h;

		double add = 0.0;

		if (seed == 1) {
			if (((rx) % 2 == 0) && ((rz) % 2 == 0)) { // && , %? if ((int(rx) % 2 == 0) && (int(rz) % 2 == 0))
				add = 1.0471975511965977461542144610932;
			}
		}
		if (seed >= 2) {
			double hash_f = sin(FX_h * 12.9898 + FY_h * 78.233 + (seed)) * 43758.5453; // double(seed) causing issue)
			hash_f = hash_f - floor(hash_f);
			if (hash_f < 0.5) {
				add = 1.0471975511965977461542144610932; // pi/3
			}
		}

		double angle = atan2(FY, FX) + 0.52359877559829887307710723054658 - add; // pi/6
		double coef = 0.47746482927568600730665129011754; // 1.5 / pi
		double angle2 = floor(angle * coef) / coef + 0.52359877559829887307710723054658 + add; // or subtract 0.5
		double x0 = cos(angle2);
		double y0 = sin(angle2);
		double dist = sqrt(sqr(FX - x0) + sqr(FY - y0));
		double d1 = 0.5 + wd;
		double d2 = 0.5 - wd;
		if (inv == 1) { // no condition?
			if ((dist > d1) || (dist < d2)) { // only |
				if (mode < 0.5) {
					FX = 0.0;
					FY = 0.0;
				} else {
					if (mode < 1.5) {
						FX = x0;
						FY = y0;
					} else {
						double rangle = atan2(FY - y0, FX - x0);
						double D = 0; // D equals?
						if (pContext.random() < 0.5) {
							D = d1;
						} else {
							D = d2;
						}
						FX = x0 + cos(rangle) * D;
						FY = y0 + sin(rangle) * D;
					}
				}
			}

		} else {
			if ((dist < d1) && (dist > d2)) {
				if (mode < 0.5) {
					FX = 0.0;
					FY = 0.0;
				} else {
					if (mode < 1.5) { // missing (
						FX = x0;
						FY = y0;
					} else {
						double rangle = atan2(FY - y0, FX - x0);
						double D = 0; // D equals?
						if (pContext.random() < 0.5) {
							D = d1;
						} else {
							D = d2;
						}
						FX = x0 + cos(rangle) * D;
						FY = y0 + sin(rangle) * D;
					}
				}
			}
		}

		pVarTP.x += (FX + FX_h) * pAmount;
		pVarTP.y += (FY + FY_h) * pAmount;
	}

	@Override
	public String[] getParameterNames() {
		return paramNames;
	}

	@Override
	public Object[] getParameterValues() {
		return new Object[] { wd, mode, inv, seed };
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_WD.equalsIgnoreCase(pName))
			wd = limitVal(pValue, 0, 5);
		else if (PARAM_MODE.equalsIgnoreCase(pName))
			mode = (int) limitVal(pValue, 0, 2);
		else if (PARAM_INV.equalsIgnoreCase(pName))
			inv = (int) limitVal(pValue, 0, 1);
		else if (PARAM_SEED.equalsIgnoreCase(pName))
			seed = (int) pValue;
		else
			throw new IllegalArgumentException(pName);
	}

	@Override
	public String getName() {
		return "truchet_hex_crop";
	}

}
