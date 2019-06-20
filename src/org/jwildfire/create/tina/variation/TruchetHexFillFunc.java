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

import static org.jwildfire.base.mathlib.MathLib.M_PI;
//import static java.lang.Math.abs;
//import static java.lang.Math.*;
import static org.jwildfire.base.mathlib.MathLib.*;

public class TruchetHexFillFunc extends VariationFunc {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_N = "n";
	private static final String PARAM_FLIPX = "flipx";
	private static final String PARAM_FLIPY = "flipy";
	private static final String PARAM_SPREADX = "spreadx";
	private static final String PARAM_SPREADY = "spready";
	private static final String PARAM_SEED = "seed";

	private static final String[] paramNames = { PARAM_N, PARAM_FLIPX, PARAM_FLIPY, PARAM_SPREADX, PARAM_SPREADY,
			PARAM_SEED };

	private int n = 3;
	private int flipx = 1;
	private int flipy = 1;
	private double spreadx = 1.0;
	private double spready = 1.0;
	private int seed = 13;

	private final static double M_SQRT3_2 = 0.86602540378443864676372317075249;
	private final static double M_SQRT3 = 1.7320508075688772935274463415059;

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
		/*
		 * truchet_hex_fill from Tatyana Zabanova converted by Brad Stefanov and
		 * Whittaker Courtney
		 * https://www.deviantart.com/tatasz/art/Truchet-Hex-Crop-and-Truchet-Hex-Fill-Plugins-801687540
		 */

		// round
		double rx = floor(log(pContext.random()) * (pContext.random() < 0.5 ? spreadx : -spreadx));
		double rz = floor(log(pContext.random()) * (pContext.random() < 0.5 ? spready : -spready));

		double FX_h = M_SQRT3 * rx + M_SQRT3_2 * (pContext.random() < 0.5 ? rz : -rz);
		double FY_h = 1.5 * rz;

		boolean add = true;

		if (seed == 1) {
			if (((rx) % 2 == 0) && ((rz) % 2 == 0)) {
				add = false;
			}
		}
		if (seed >= 2) {
			double hash_f = sin(FX_h * 12.9898 + FY_h * 78.233 + seed) * 43758.5453;
			hash_f = hash_f - floor(hash_f);
			if (hash_f < 0.5) {
				add = false; // pi/3
			}
		}
		double nnn = 3.0 * n;
		double nnnn = 1.0 / nnn;
		// normalization constant for radius
		double s1 = exp(M_PI * nnnn);
		double k_factor = 2.0 / (s1 + 1.0 / s1);
		// exponential to make a tiled circle
		double rangle = floor(pContext.random() * nnn) * M_2PI * nnnn;
		double y_aux = (flipy == 0 ? (add ? pAffineTP.y : -pAffineTP.y) : pAffineTP.y);
		double x_aux = (flipx == 0 ? (add ? pAffineTP.x : -pAffineTP.x) : pAffineTP.x);
		double FY = y_aux * nnnn;
		double FX = x_aux * nnnn;
		double sn = sin(FY * M_PI + rangle);
		double cs = cos(FY * M_PI + rangle);

		// fsincos(FY * M_PI + rangle, &sn, &cs);
		// scale down the circle by k_factor so the edges match when we tile it
		// double a = 1 + FX * M_PI; //
		double a = k_factor * exp(FX * M_PI); // Angular polar dimension
		FX = a * cs;
		FY = a * sn;
		// split

		double A = atan2(FY, FX);
		if (A < 0)
			A += M_2PI;
		A = floor(1.5 * A * M_1_PI);
		double ang = (M_PI + A * M_2PI) / 3.0;
		double sn2 = sin(ang);
		double cs2 = cos(ang);
		// fsincos(ang, &sn2, &cs2);
		double FX_new = FX - cs2 * 2.0;
		double FY_new = FY - sn2 * 2.0;
		// rotate by 30 to fit the hex
		if (add) {
			FX = M_SQRT3_2 * FX_new - 0.5 * FY_new;
			FY = 0.5 * FX_new + M_SQRT3_2 * FY_new;
		} else {
			FX = M_SQRT3_2 * FX_new + 0.5 * FY_new;
			FY = -0.5 * FX_new + M_SQRT3_2 * FY_new;
		}

		pVarTP.x += pAmount * (FX * 0.5 + FX_h);
		pVarTP.y += pAmount * (FY * 0.5 + FY_h);

	}

	@Override
	public String[] getParameterNames() {
		return paramNames;
	}

	@Override
	public Object[] getParameterValues() {
		return new Object[] { n, flipx, flipy, spreadx, spready, seed };
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_N.equalsIgnoreCase(pName))
			n = (int) pValue;
		else if (PARAM_FLIPX.equalsIgnoreCase(pName))
			flipx = (int) limitVal(pValue, 0, 1);
		else if (PARAM_FLIPY.equalsIgnoreCase(pName))
			flipy = (int) limitVal(pValue, 0, 1);
		else if (PARAM_SPREADX.equalsIgnoreCase(pName))
			spreadx = pValue;
		else if (PARAM_SPREADY.equalsIgnoreCase(pName))
			spready = pValue;
		else if (PARAM_SEED.equalsIgnoreCase(pName))
			seed = (int) pValue;
		else
			throw new IllegalArgumentException(pName);
	}

	@Override
	public String getName() {
		return "truchet_hex_fill";
	}

}
