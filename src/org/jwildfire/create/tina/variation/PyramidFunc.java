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

import static org.jwildfire.base.mathlib.MathLib.fabs;

public class PyramidFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // pyramid by Zueuk (transcribed into jwf by Dark)
    double x = pAffineTP.x;
    x = x * x * x;
    double y = pAffineTP.y;
    y = y * y * y;
    double z = pAffineTP.z;
    z = fabs(z * z * z);

    double r = pAmount / (fabs(x) + fabs(y) + z + 0.000000001);

    pVarTP.x += x * r;
    pVarTP.y += y * r;
    pVarTP.z += z * r;
  }

  @Override
  public String getName() {
    return "pyramid";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float x = __x;\n"
        + "x = x * x * x;\n"
        + "float y = __y;\n"
        + "y = y * y * y;\n"
        + "float z = __z;\n"
        + "z = fabsf(z * z * z);\n"
        + "float r = __pyramid / (fabsf(x) + fabsf(y) + z + 0.000000001);\n"
        + "__px += x * r;\n"
        + "__py += y * r;\n"
        + "__pz += z * r;\n";
  }
}
