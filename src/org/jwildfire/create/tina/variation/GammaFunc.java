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

import odk.lang.FastMath;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.lgamma;


public class GammaFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;


  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // "gamma" variation created by zephyrtronium implemented into JWildfire by darkbeam

    pVarTP.x += lgamma(FastMath.hypot(pAffineTP.y, pAffineTP.x)) * pAmount;
    pVarTP.y += atan2(pAffineTP.y, pAffineTP.x) * pAmount;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }


  @Override
  public String getName() {
    return "gamma";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "__px += gamma_lgamma(hypotf(__y, __x)) * varpar->gamma;\n"
        + " __py += atan2f(__y, __x) * varpar->gamma;\n"
        + (context.isPreserveZCoordinate() ? "__pz += varpar->gamma * __z;\n" : "");
  }

  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return "__device__ float gamma_lgamma(float x) {\n"
        + "     float tmp = (x - 0.5f) * logf(x + 4.5f) - (x + 4.5f);\n"
        + "     float ser = 1.0f + 76.18009173f    / (x + 0.f)   - 86.50532033f    / (x + 1.f)\n"
        + "                      + 24.01409822f    / (x + 2.f)   -  1.231739516f   / (x + 3.f)\n"
        + "                      +  0.00120858003f / (x + 4.f)   -  0.00000536382f / (x + 5.f);\n"
        + "     return tmp + logf(ser * sqrtf(2.f * PI));\n"
        + "  }";
  }
}