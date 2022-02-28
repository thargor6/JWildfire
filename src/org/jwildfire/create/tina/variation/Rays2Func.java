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

public class Rays2Func extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // rays2 by Raykoid666, http://raykoid666.deviantart.com/art/re-pack-1-new-plugins-100092186 
    double t = sqr(pAffineTP.x) + sqr(pAffineTP.y);
    double u = 1.0 / cos((t + SMALL_EPSILON) * tan(1.0 / t + SMALL_EPSILON));

    pVarTP.x = (pAmount / 10.0) * u * t / pAffineTP.x;
    pVarTP.y = (pAmount / 10.0) * u * t / pAffineTP.y;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "rays2";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float t = __x*__x + __y*__y;\n"
        + "    float u = 1.0f / cosf((t + 1.e-6f) * tan(1.0f / t + 1.e-6f));\n"
        + "    __px = (__rays2 / 10.0f) * u * t / __x;\n"
        + "    __py = (__rays2 / 10.0f) * u * t / __y;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __rays2 * __z;\n" : "");
  }
}
