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

public class PreRotateYFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double sina = sin(pAmount * M_PI * 0.5);
    double cosa = cos(pAmount * M_PI * 0.5);
    double x = cosa * pAffineTP.x - sina * pAffineTP.z;
    pAffineTP.z = sina * pAffineTP.x + cosa * pAffineTP.z;
    pAffineTP.x = x;
  }

  @Override
  public String getName() {
    return "pre_rotate_y";
  }

  @Override
  public int getPriority() {
    return -1;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_PRE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float ry_cos;\n"
        + "float ry_sin;\n"
        + "sincosf(varpar->pre_rotate_y * M_PI_2_F, &ry_sin, &ry_cos);\n"
        + "float x = ry_cos * __x - ry_sin * __z;\n"
        + " __z = ry_sin * __x + ry_cos * __z;\n"
        + " __x = x;\n"
        + "\n"
        + "__r2 = __x*__x+__y*__y;\n"
        + "__r = sqrtf(__r2);\n"
        + "__rinv = 1.f/__r;\n"
        + "__phi = atan2f(__x,__y);\n"
        + "__theta = .5f*PI-__phi;\n"
        + "if (__theta > PI)\n"
        + "    __theta -= 2.f*PI;";
  }
}
