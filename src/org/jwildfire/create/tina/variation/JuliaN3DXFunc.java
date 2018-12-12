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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class JuliaN3DXFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";
  private static final String PARAM_DIST = "dist";
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_E = "e";
  private static final String PARAM_F = "f";

  private static final String[] paramNames = {PARAM_POWER, PARAM_DIST, PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F};

  private int power = genRandomPower();
  private double dist = 1.0;
  private double a = 1.0;
  private double b = 0.0;
  private double c = 0.0;
  private double d = 1.0;
  private double e = 0.0;
  private double f = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // julian3Dx by Xyrus02, http://xyrus-02.deviantart.com/art/julian3Dx-Plugin-for-Apophysis-455502519
    double z = pAffineTP.x / absN;
    double radius_square = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y;

    double radius_out = pAmount * pow(radius_square + z * z, cN);

    pVarTP.z += radius_out * z;

    double c00 = a;
    double c01 = b;
    double c10 = c;
    double c11 = d;
    double c20 = e;
    double c21 = f;

    double x = c00 * pAffineTP.x + c01 * pAffineTP.y + c20;
    double y = c10 * pAffineTP.x + c11 * pAffineTP.y + c21;

    double rand = (int) (pContext.random() * absN);

    double alpha = (atan2(y, x) + M_2PI * rand) / power;
    double gamma = radius_out * sqrt(radius_square);

    double sina = sin(alpha);
    double cosa = cos(alpha);

    pVarTP.x += gamma * cosa;
    pVarTP.y += gamma * sina;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{power, dist, a, b, c, d, e, f};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = Tools.FTOI(pValue);
    else if (PARAM_DIST.equalsIgnoreCase(pName))
      dist = pValue;
    else if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName))
      d = pValue;
    else if (PARAM_E.equalsIgnoreCase(pName))
      e = pValue;
    else if (PARAM_F.equalsIgnoreCase(pName))
      f = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "julian3Dx";
  }

  private int genRandomPower() {
    int res = (int) (Math.random() * 5.0 + 2.5);
    return Math.random() < 0.5 ? res : -res;
  }

  private double absN, cN;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    absN = fabs((double) power);
    cN = (dist / power - 1.0) / 2.0;
  }
}
