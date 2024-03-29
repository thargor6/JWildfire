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

public class PostRotateXFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double sina = sin(pAmount * M_PI * 0.5);
    double cosa = cos(pAmount * M_PI * 0.5);
    double z = cosa * pVarTP.z - sina * pVarTP.y;
    pVarTP.y = sina * pVarTP.z + cosa * pVarTP.y;
    pVarTP.z = z;
  }

  @Override
  public String getName() {
    return "post_rotate_x";
  }

  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_POST, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float px_cos;\n"
        + "float px_sin;\n"
        + "sincosf(__post_rotate_x * M_PI_2_F, &px_sin, &px_cos);\n"
        + "float _z = px_cos * __pz - px_sin *__py;\n"
        + "__py = px_sin * __pz + px_cos * __py;\n"
        + "__pz = _z;\n";
  }
}
