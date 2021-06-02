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

public class Butterfly3DFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* wx is weight*4/sqrt(3*pi) */
    double wx = pAmount * 1.3029400317411197908970256609023;

    double y2 = pAffineTP.y * 2.0;
    double r = wx * sqrt(fabs(pAffineTP.y * pAffineTP.x) / (SMALL_EPSILON + pAffineTP.x * pAffineTP.x + y2 * y2));

    pVarTP.x += r * pAffineTP.x;
    pVarTP.y += r * y2;
    pVarTP.z += r * fabs(y2) * sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y) / 4.0;
  }

  @Override
  public String getName() {
    return "butterfly3D";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float wx = varpar->butterfly3D * 1.3029400317411197908970256609023;\n"
        + "    float y2 = __y * 2.0;\n"
        + "    float r = wx * sqrtf(fabsf(__y * __x) / (1.e-6f + __x * __x + y2 * y2));\n"
        + "    __px += r * __x;\n"
        + "    __py += r * y2;\n"
        + "    __pz += r * fabsf(y2) * sqrtf(__x * __x + __y * __y) / 4.0;\n";
  }
}
