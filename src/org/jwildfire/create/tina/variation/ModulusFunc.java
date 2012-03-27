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

import static org.jwildfire.base.MathLib.fmod;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class ModulusFunc extends VariationFunc {

  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";

  private static final String[] paramNames = { PARAM_X, PARAM_Y };

  private double x = 0.20;
  private double y = 0.50;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Modulus in the Apophysis Plugin Pack */
    double xr = 2.0 * x;
    double yr = 2.0 * y;

    if (pAffineTP.x > x) {
      pVarTP.x += pAmount * (-x + fmod(pAffineTP.x + x, xr));
    }
    else if (pAffineTP.x < -x) {
      pVarTP.x += pAmount * (x - fmod(x - pAffineTP.x, xr));
    }
    else {
      pVarTP.x += pAmount * pAffineTP.x;
    }

    if (pAffineTP.y > y) {
      pVarTP.y += pAmount * (-y + fmod(pAffineTP.y + y, yr));
    }
    else if (pAffineTP.y < -y) {
      pVarTP.y += pAmount * (y - fmod(y - pAffineTP.y, yr));
    }
    else {
      pVarTP.y += pAmount * pAffineTP.y;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { x, y };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "modulus";
  }

}
