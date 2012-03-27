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

import static org.jwildfire.base.MathLib.SMALL_EPSILON;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class Rings2Func extends VariationFunc {
  private static final String PARAM_VAL = "val";
  private static final String[] paramNames = { PARAM_VAL };

  private double val;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r = pAffineTP.getPrecalcSqrt(pContext);
    double dx = val * val + SMALL_EPSILON;

    r += -2.0 * dx * (int) ((r + dx) / (2.0 * dx)) + r * (1.0 - dx);

    pVarTP.x += pAmount * pAffineTP.getPrecalcSinA(pContext) * r;
    pVarTP.y += pAmount * pAffineTP.getPrecalcCosA(pContext) * r;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { val };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_VAL.equalsIgnoreCase(pName))
      val = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "rings2";
  }
}
