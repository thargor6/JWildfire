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

import org.jwildfire.create.tina.base.Constants;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class BipolarFunc extends VariationFunc {

  private static final String PARAM_SHIFT = "shift";
  private static final String[] paramNames = { PARAM_SHIFT };

  private double shift = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Bipolar in the Apophysis Plugin Pack */

    double x2y2 = (pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double t = x2y2 + 1.0;
    double x2 = 2 * pAffineTP.x;
    double ps = -Constants.M_PI_2 * shift;
    double y = 0.5 * Math.atan2(2.0 * pAffineTP.y, x2y2 - 1.0) + ps;

    if (y > Constants.M_PI_2) {
      y = -Constants.M_PI_2 + pContext.fmod(y + Constants.M_PI_2, Constants.M_PI);
    }
    else if (y < -Constants.M_PI_2) {
      y = Constants.M_PI_2 - pContext.fmod(Constants.M_PI_2 - y, Constants.M_PI);
    }

    pVarTP.x += pAmount * 0.25 * Constants.M_2_PI * Math.log((t + x2) / (t - x2));
    pVarTP.y += pAmount * Constants.M_2_PI * y;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { shift };
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
