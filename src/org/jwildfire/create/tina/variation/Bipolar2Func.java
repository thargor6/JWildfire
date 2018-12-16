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

public class Bipolar2Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SHIFT = "shift";
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_E = "e";
  private static final String PARAM_F1 = "f1";
  private static final String PARAM_G1 = "g1";
  private static final String PARAM_H = "h";
  private static final String[] paramNames = {PARAM_SHIFT, PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F1, PARAM_G1, PARAM_H};

  private double shift = 0.0;
  private double a = 1.0;
  private double b = 2.0;
  private double c = 0.5;
  private double d = 1.0;
  private double e = 2.0;
  private double f1 = 0.25;
  private double g1 = 1.0;
  private double h = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Bipolar in the Apophysis Plugin Pack with variables added by Brad Stefanov */

    double x2y2 = (pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y) * g1;
    double t = x2y2 + a;
    double x2 = b * pAffineTP.x;
    double ps = -M_PI_2 * shift;
    double y = c * atan2(e * pAffineTP.y, x2y2 - d) + ps;

    if (y > M_PI_2) {
      y = -M_PI_2 + fmod(y + M_PI_2, M_PI);
    } else if (y < -M_PI_2) {
      y = M_PI_2 - fmod(M_PI_2 - y, M_PI);
    }

    double f = t + x2;
    double g = t - x2;

    if ((g == 0) || (f / g <= 0))
      return;
    pVarTP.x += pAmount * f1 * M_2_PI * log((t + x2) / (t - x2));
    pVarTP.y += pAmount * M_2_PI * y * h;
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
    return new Object[]{shift, a, b, c, d, e, f1, g1, h};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SHIFT.equalsIgnoreCase(pName))
      shift = pValue;
    else if (PARAM_A.equalsIgnoreCase(pName)) {
      a = pValue;
    } else if (PARAM_B.equalsIgnoreCase(pName)) {
      b = pValue;
    } else if (PARAM_C.equalsIgnoreCase(pName)) {
      c = pValue;
    } else if (PARAM_D.equalsIgnoreCase(pName)) {
      d = pValue;
    } else if (PARAM_E.equalsIgnoreCase(pName)) {
      e = pValue;
    } else if (PARAM_F1.equalsIgnoreCase(pName)) {
      f1 = pValue;
    } else if (PARAM_G1.equalsIgnoreCase(pName)) {
      g1 = pValue;
    } else if (PARAM_H.equalsIgnoreCase(pName)) {
      h = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "bipolar2";
  }

}