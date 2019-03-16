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

public class TQMirrorFunc extends VariationFunc {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_A = "a";
	private static final String PARAM_B = "b";
	private static final String PARAM_C = "c";
	private static final String PARAM_D = "d";
	private static final String PARAM_E = "e";
	private static final String PARAM_F = "f";
	private static final String PARAM_G = "g";
	private static final String PARAM_H = "h";
	private static final String PARAM_TYPE = "type";

	private static final String[] paramNames = { PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F, PARAM_G, PARAM_H,
			PARAM_TYPE };
	private double a = 1.0;
	private double b = 1.0;
	private double c = 1.0;
	private double d = 1.0;
	private double e = 1.0;
	private double f = 1.0;
	private double g = 1.0;
	private double h = 1.0;
	private int type = 0;

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
		// Created by Nic Anderson (chronologicaldot), August 15, 2015
		// variables added by Brad Stefanov
		// Shift a box, whose width and height are pAmount, from the upper left of the
		// graph to the lower right.
		// Then flip everything else over the y=x axis.

		double x = pAffineTP.x;
		double y = pAffineTP.y;

		if (d + x < 0 || e + y < 0) // too far
		{
			if (type != 0) {
				pVarTP.x += x;
				pVarTP.y += y;
			} else {
				pVarTP.x += y;
				pVarTP.y += x;
			}
			return;
		}

		if (x < 0 && y < 0) {
			pVarTP.x += x + pAmount * f;
			pVarTP.y += y + pAmount * g;

		} else {
			if (x < pAmount && y < a && x + b > 0 && y + c > 0) {
				pVarTP.x -= y * h;
				pVarTP.y -= x * h;
			} else {
				pVarTP.x += x;
				pVarTP.y += y;
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
		return new Object[] { a, b, c, d, e, f, g, h, type };
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_A.equalsIgnoreCase(pName)) {
			a = pValue;
		} else if (PARAM_B.equalsIgnoreCase(pName)) {
			b = pValue;
		} else if (PARAM_C.equalsIgnoreCase(pName)) {
			c = pValue;
		} else if (PARAM_D.equalsIgnoreCase(pName)) {
			d = pValue;
		} else if (PARAM_E.equalsIgnoreCase(pName)) {
			e = pValue;
		} else if (PARAM_F.equalsIgnoreCase(pName)) {
			f = pValue;
		} else if (PARAM_G.equalsIgnoreCase(pName)) {
			g = pValue;
		} else if (PARAM_H.equalsIgnoreCase(pName)) {
			h = pValue;
		} else if (PARAM_TYPE.equalsIgnoreCase(pName)) {
			type = (int) limitVal(pValue, 0, 1);
		} else {
			System.out.println("pName not recognized: " + pName);
			throw new IllegalArgumentException(pName);
		}
	}

	@Override
	public String getName() {
		return "tqmirror";
	}

}
