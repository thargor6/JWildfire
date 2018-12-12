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

public class BipolarFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SHIFT = "shift";
  private static final String[] paramNames = {PARAM_SHIFT};

  private double shift = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Bipolar in the Apophysis Plugin Pack */

    double x2y2 = (pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double t = x2y2 + 1.0;
    double x2 = 2 * pAffineTP.x;
    double ps = -M_PI_2 * shift;
    double y = 0.5 * atan2(2.0 * pAffineTP.y, x2y2 - 1.0) + ps;

    if (y > M_PI_2) {
      y = -M_PI_2 + fmod(y + M_PI_2, M_PI);
    } else if (y < -M_PI_2) {
      y = M_PI_2 - fmod(M_PI_2 - y, M_PI);
    }

    double f = t + x2;
    double g = t - x2;

    if ((g == 0) || (f / g <= 0))
      return;
    pVarTP.x += pAmount * 0.25 * M_2_PI * log((t + x2) / (t - x2));
    pVarTP.y += pAmount * M_2_PI * y;
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
    return new Object[]{shift};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SHIFT.equalsIgnoreCase(pName))
      shift = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "bipolar";
  }

}
