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

import static org.jwildfire.base.mathlib.MathLib.*;

public class Fan2Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";
  private static final String[] paramNames = {PARAM_X, PARAM_Y};

  private double x = 0.5;
  private double y = 1.2;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double angle;
    if ((pAffineTP.x < -SMALL_EPSILON) || (pAffineTP.x > SMALL_EPSILON) || (pAffineTP.y < -SMALL_EPSILON) || (pAffineTP.y > SMALL_EPSILON)) {
      angle = atan2(pAffineTP.x, pAffineTP.y);
    } else {
      angle = 0.0;
    }

    double dy = y;
    double dx = M_PI * (x * x) + SMALL_EPSILON;
    double dx2 = dx * 0.5;

    double t = angle + dy - (int) ((angle + dy) / dx) * dx;
    double a;
    if (t > dx2) {
      a = angle - dx2;
    } else {
      a = angle + dx2;
    }

    pVarTP.x += pAmount * r * sin(a);
    pVarTP.y += pAmount * r * cos(a);
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
    return new Object[]{x, y};
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
    return "fan2";
  }

}
