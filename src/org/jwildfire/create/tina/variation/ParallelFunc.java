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

public class ParallelFunc extends VariationFunc {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_X1WIDTH = "x1width";
	private static final String PARAM_X1TILESIZE = "x1tilesize";
	private static final String PARAM_X1MOD1 = "x1mod1";
	private static final String PARAM_X1MOD2 = "x1mod2";
	private static final String PARAM_X1HEIGHT = "x1height";
	private static final String PARAM_X1MOVE = "x1move";
	private static final String PARAM_X2WIDTH = "x2width";
	private static final String PARAM_X2TILESIZE = "x2tilesize";
	private static final String PARAM_X2MOD1 = "x2mod1";
	private static final String PARAM_X2MOD2 = "x2mod2";
	private static final String PARAM_X2HEIGHT = "x2height";
	private static final String PARAM_X2MOVE = "x2move";

	private static final String[] paramNames = { PARAM_X1WIDTH, PARAM_X1TILESIZE, PARAM_X1MOD1, PARAM_X1MOD2,
			PARAM_X1HEIGHT, PARAM_X1MOVE, PARAM_X2WIDTH, PARAM_X2TILESIZE, PARAM_X2MOD1, PARAM_X2MOD2, PARAM_X2HEIGHT,
			PARAM_X2MOVE };

	private double x1width = 5.0;
	private double x1tilesize = 0.50;
	private double x1mod1 = 0.30;
	private double x1mod2 = 1.0;
	private double x1height = 0.50;
	private double x1move = 1.0;
	private double x2width = 5.0;
	private double x2tilesize = 0.50;
	private double x2mod1 = 0.30;
	private double x2mod2 = 1.0;
	private double x2height = 0.50;
	private double x2move = 1.0;

	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
			double pAmount) {
		/* parallel by Brad Stefanov */

		if (pContext.random() < 0.5) {

			double x1 = -x1width;
			if (pContext.random() < 0.5)
				x1 = x1width;

			pVarTP.x += x1tilesize * (pAffineTP.x + round(x1 * log(pContext.random())));

			if (pAffineTP.y > x1mod1) {
				pVarTP.y += x1height * (-x1mod1 + fmod(pAffineTP.y + x1mod1, _xr1)) + pAmount * x1move;
			} else if (pAffineTP.y < -x1mod1) {
				pVarTP.y += x1height * (x1mod1 - fmod(x1mod1 - pAffineTP.y, _xr1)) + pAmount * x1move;
			} else {

				pVarTP.y += x1height * pAffineTP.y + pAmount * x1move;
			}
		} else {

			double x2 = -x2width;
			if (pContext.random() < 0.5)
				x2 = x2width;

			pVarTP.x += x2tilesize * (pAffineTP.x + round(x2 * log(pContext.random())));

			if (pAffineTP.y > x2mod1) {
				pVarTP.y += x2height * (-x2mod1 + fmod(pAffineTP.y + x2mod1, _xr2)) - pAmount * x2move;
			} else if (pAffineTP.y < -x2mod1) {
				pVarTP.y += x2height * (x2mod1 - fmod(x2mod1 - pAffineTP.y, _xr2)) - pAmount * x2move;
			} else {

				pVarTP.y += x2height * pAffineTP.y - pAmount * x2move;
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
		return new Object[] { x1width, x1tilesize, x1mod1, x1mod2, x1height, x1move, x2width, x2tilesize, x2mod1,
				x2mod2, x2height, x2move };
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_X1WIDTH.equalsIgnoreCase(pName))
			x1width = pValue;
		else if (PARAM_X1TILESIZE.equalsIgnoreCase(pName))
			x1tilesize = pValue;
		else if (PARAM_X1MOD1.equalsIgnoreCase(pName))
			x1mod1 = pValue;
		else if (PARAM_X1MOD2.equalsIgnoreCase(pName))
			x1mod2 = pValue;
		else if (PARAM_X1HEIGHT.equalsIgnoreCase(pName))
			x1height = pValue;
		else if (PARAM_X1MOVE.equalsIgnoreCase(pName))
			x1move = pValue;
		else if (PARAM_X2WIDTH.equalsIgnoreCase(pName))
			x2width = pValue;
		else if (PARAM_X2TILESIZE.equalsIgnoreCase(pName))
			x2tilesize = pValue;
		else if (PARAM_X2MOD1.equalsIgnoreCase(pName))
			x2mod1 = pValue;
		else if (PARAM_X2MOD2.equalsIgnoreCase(pName))
			x2mod2 = pValue;
		else if (PARAM_X2HEIGHT.equalsIgnoreCase(pName))
			x2height = pValue;
		else if (PARAM_X2MOVE.equalsIgnoreCase(pName))
			x2move = pValue;
		else
			throw new IllegalArgumentException(pName);
	}

	@Override
	public String getName() {
		return "parallel";
	}

	private double _xr1, _xr2;

	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		_xr1 = x1mod2 * x1mod1;
		_xr2 = x2mod2 * x2mod1;

	}

}
