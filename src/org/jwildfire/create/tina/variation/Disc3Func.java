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

public class Disc3Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_E = "e";
  private static final String PARAM_F = "f";
  private static final String PARAM_G = "g";
  private static final String PARAM_H = "h";
  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F, PARAM_G,
          PARAM_H};
  private double a = 1.0;
  private double b = 1.0;
  private double c = 1.0;
  private double d = 1.0;
  private double e = 1.0;
  private double f = 1.0;
  private double g = 1.0;
  private double h = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
                        double pAmount) {
    /*
     * Added variables to original disc by Brad Stefanov
     */
    double rPI = M_PI * sqrt(pAffineTP.x * d * pAffineTP.x * e + pAffineTP.y * f * pAffineTP.y * g);
    double sinr = sin(rPI) * a;
    double cosr = cos(rPI) * b;
    double r = pAmount * pAffineTP.getPrecalcAtan() / M_PI * c;

    pVarTP.x += sinr * h * r;
    pVarTP.y += cosr * h * r;
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
    return new Object[]{a, b, c, d, e, f, g, h};
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
      d = pValue;
    } else if (PARAM_E.equalsIgnoreCase(pName)) {
      e = pValue;
    } else if (PARAM_F.equalsIgnoreCase(pName)) {
      f = pValue;
    } else if (PARAM_G.equalsIgnoreCase(pName)) {
      g = pValue;
    } else if (PARAM_H.equalsIgnoreCase(pName)) {
      h = pValue;
    } else {
      System.out.println("pName not recognized: " + pName);
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "disc3";
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

  }

}
