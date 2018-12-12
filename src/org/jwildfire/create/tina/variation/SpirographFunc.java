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

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class SpirographFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_D = "d";
  private static final String PARAM_TMIN = "tmin";
  private static final String PARAM_TMAX = "tmax";
  private static final String PARAM_YMIN = "ymin";
  private static final String PARAM_YMAX = "ymax";
  private static final String PARAM_C1 = "c1";
  private static final String PARAM_C2 = "c2";

  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_D, PARAM_TMIN, PARAM_TMAX, PARAM_YMIN, PARAM_YMAX, PARAM_C1, PARAM_C2};

  private double a = 3.0;
  private double b = 2.0;
  private double d = 0.0;
  private double c1 = 0.0;
  private double c2 = 0.0;
  private double tmin = -1.0;
  private double tmax = 1.0;
  private double ymin = -1.0;
  private double ymax = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double t = (tmax - tmin) * pContext.random() + tmin;
    double y = (ymax - ymin) * pContext.random() + ymin;
    double x1 = (a + b) * cos(t) - c1 * cos((a + b) / b * t);
    double y1 = (a + b) * sin(t) - c2 * sin((a + b) / b * t);
    pVarTP.x += pAmount * (x1 + d * cos(t) + y);
    pVarTP.y += pAmount * (y1 + d * sin(t) + y);
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
    return new Object[]{a, b, d, tmin, tmax, ymin, ymax, c1, c2};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName))
      d = pValue;
    else if (PARAM_TMIN.equalsIgnoreCase(pName))
      tmin = pValue;
    else if (PARAM_TMAX.equalsIgnoreCase(pName))
      tmax = pValue;
    else if (PARAM_YMIN.equalsIgnoreCase(pName))
      ymin = pValue;
    else if (PARAM_YMAX.equalsIgnoreCase(pName))
      ymax = pValue;
    else if (PARAM_TMIN.equalsIgnoreCase(pName))
      tmin = pValue;
    else if (PARAM_C1.equalsIgnoreCase(pName))
      c1 = pValue;
    else if (PARAM_C2.equalsIgnoreCase(pName))
      c2 = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "spirograph";
  }

}
