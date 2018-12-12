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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class MinkQMFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "dd";
  private static final String PARAM_E = "e";
  private static final String PARAM_F = "f";

  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F};
  private double a = 1.0;
  private double b = 1.0;
  private double c = 1.0;
  private double dd = 1.0;
  private double e = 0.5;
  private double f = 20.0;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double mnkX = pAffineTP.x;
    double mnkY = pAffineTP.y;
    int is = 0;
    if (mnkX < 0) {
      mnkX = -mnkX;
      is = 1;
    }
    if ((mnkX > 0.) && (mnkX < 1.)) {
      mnkX = minkowski(mnkX);
    }
    if (is == 1) {
      mnkX = -mnkX;
      is = 0;
    }
    if (mnkY < 0) {
      mnkY = -mnkY;
      is = 1;
    }
    if ((mnkY > 0.) && (mnkY < 1.)) {
      mnkY = minkowski(mnkY);
    }
    if (is == 1) {
      mnkY = -mnkY;
    }
    pVarTP.x += pAmount * mnkX;
    pVarTP.y += pAmount * mnkY;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  /* Minkowski's question mark function from Wikipedia, https://en.wikipedia.org/wiki/Minkowski%27s_question_mark_function adopted by dark-beam and variables by Brad Stefanov */
  private double minkowski(double x) {
    double p = 0;


    double q = a, r = p + b, s = c, m = 0.0, n = 0.;
    double d = dd, y = p;
    for (int it = 0; it < f; it++) {
      d = d * e; //if (d<1e-10) break;
      m = p + r;
      n = q + s;
      if (x < (m / n)) {
        r = m;
        s = n;
      } else {
        y += d;
        p = m;
        q = n;
      }
    }
    return y + d;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{a, b, c, dd, e, f};
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
      dd = pValue;
    } else if (PARAM_E.equalsIgnoreCase(pName)) {
      e = pValue;
    } else if (PARAM_F.equalsIgnoreCase(pName)) {
      f = limitIntVal(Tools.FTOI(pValue), 1, 50);
    } else {
      System.out.println("pName not recognized: " + pName);
      throw new IllegalArgumentException(pName);
    }
  }

  public String getName() {
    return "minkQM";
  }
}
