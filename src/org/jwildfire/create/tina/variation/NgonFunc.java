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

public class NgonFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_CIRCLE = "circle";
  private static final String PARAM_CORNERS = "corners";
  private static final String PARAM_POWER = "power";
  private static final String PARAM_SIDES = "sides";

  private static final String[] paramNames = {PARAM_CIRCLE, PARAM_CORNERS, PARAM_POWER, PARAM_SIDES};

  private double circle = 1;
  private double corners = 2;
  private double power = 3;
  private double sides = 5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* ngon by Joel Faber (09/06) */
    double r_factor, theta, phi, b, amp;

    r_factor = pow(pAffineTP.getPrecalcSumsq(), power / 2.0);

    theta = pAffineTP.getPrecalcAtanYX();
    b = 2 * M_PI / sides;

    phi = theta - (b * floor(theta / b));
    if (phi > b / 2)
      phi -= b;

    amp = corners * (1.0 / (cos(phi) + SMALL_EPSILON) - 1.0) + circle;
    amp /= (r_factor + SMALL_EPSILON);

    pVarTP.x += pAmount * pAffineTP.x * amp;
    pVarTP.y += pAmount * pAffineTP.y * amp;
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
    return new Object[]{circle, corners, power, sides};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_CIRCLE.equalsIgnoreCase(pName))
      circle = pValue;
    else if (PARAM_CORNERS.equalsIgnoreCase(pName))
      corners = pValue;
    else if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else if (PARAM_SIDES.equalsIgnoreCase(pName))
      sides = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "ngon";
  }

}
