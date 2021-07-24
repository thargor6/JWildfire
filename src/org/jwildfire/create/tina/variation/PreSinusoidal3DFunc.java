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

public class PreSinusoidal3DFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // pre_sinusoidal by gossamer light
    // a pre variation impacts the FT variables and not the FP
    // they can be additive += or they can overlay =
    pAffineTP.x = pAmount * sin(pAffineTP.x);
    pAffineTP.y = pAmount * sin(pAffineTP.y);
    pAffineTP.z = pAmount * atan2(pAffineTP.x * pAffineTP.x, pAffineTP.y * pAffineTP.y) * cos(pAffineTP.z);
  }

  @Override
  public String getName() {
    return "pre_sinusoidal3d";
  }

  @Override
  public int getPriority() {
    return -1;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_PRE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "__x = __pre_sinusoidal3d * sinf(__x);\n"
        + "__y = __pre_sinusoidal3d * sinf(__y);\n"
        + "__z = __pre_sinusoidal3d * atan2f(__x * __x, __y * __y) * cosf(__z);"
            + "__r2 = __x*__x+__y*__y;\n"
            + "__r = sqrtf(__r2);\n"
            + "__rinv = 1.f/__r;\n"
            + "__phi = atan2f(__x,__y);\n"
            + "__theta = .5f*PI-__phi;\n"
            + "if (__theta > PI)\n"
            + "    __theta -= 2.f*PI;";
  }
}
