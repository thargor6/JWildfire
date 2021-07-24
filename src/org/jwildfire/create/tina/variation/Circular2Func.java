/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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

public class Circular2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_CIRCULAR_ANGLE = "angle";
  private static final String PARAM_CIRCULAR_SEED = "seed";
  private static final String PARAM_CIRCULAR_X = "xx";
  private static final String PARAM_CIRCULAR_Y = "yy";
  private static final String[] paramNames = {PARAM_CIRCULAR_ANGLE, PARAM_CIRCULAR_SEED, PARAM_CIRCULAR_X, PARAM_CIRCULAR_Y};
  private double angle = 90.0;
  private double seed = 0.0;
  private double xx = 12.9898;
  private double yy = 78.233;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // "circular" variation created by Tatyana Zabanova implemented into JWildfire and added extra variables by Brad Stefanov

    double c_a = angle * M_PI / 180;
    double aux = (sin(pAffineTP.x * xx + pAffineTP.y * yy + seed) * 43758.5453);
    aux = aux - (int) aux;
    double rnd = (2 * (pContext.random() + aux) - 2.0) * c_a;
    double rad = sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
    double ang = atan2(pAffineTP.y, pAffineTP.x);
    double by = sin(ang + rnd);
    double bx = cos(ang + rnd);


    pVarTP.x += pAmount * (bx * rad);
    pVarTP.y += pAmount * (by * rad);
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
    return new Object[]{angle, seed, xx, yy};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_CIRCULAR_ANGLE.equalsIgnoreCase(pName))
      angle = pValue;
    else if (PARAM_CIRCULAR_SEED.equalsIgnoreCase(pName))
      seed = pValue;
    else if (PARAM_CIRCULAR_X.equalsIgnoreCase(pName))
      xx = pValue;
    else if (PARAM_CIRCULAR_Y.equalsIgnoreCase(pName))
      yy = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "circular2";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float c_a = __circular2_angle * PI / 180.f;\n"
        + "    float aux = (sinf(__x * __circular2_xx + __y * __circular2_yy + __circular2_seed) * 43758.5453);\n"
        + "    aux = aux - (int) aux;\n"
        + "    float rnd = (2 * (RANDFLOAT() + aux) - 2.0) * c_a;\n"
        + "    float rad = sqrtf(__x*__x + __y*__y);\n"
        + "    float ang = atan2f(__y, __x);\n"
        + "    float by = sinf(ang + rnd);\n"
        + "    float bx = cosf(ang + rnd);\n"
        + "\n"
        + "\n"
        + "    __px += __circular2 * (bx * rad);\n"
        + "    __py += __circular2 * (by * rad);\n"
        + (context.isPreserveZCoordinate() ? "      __pz += __circular2 * __z;\n" : "");
  }
}
