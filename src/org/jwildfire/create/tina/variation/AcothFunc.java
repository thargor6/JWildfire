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

import org.jwildfire.base.mathlib.Complex;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.M_2_PI;

public class AcothFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    //acoth by Whittaker Courtney,
    //based on the hyperbolic variations by Tatyana Zabanova and DarkBeam's implementation of them.

    Complex z = new Complex(pAffineTP.x, pAffineTP.y);
    z.AcotH();
    z.Flip();

    z.Scale(pAmount * M_2_PI);

    pVarTP.y += z.im;
    pVarTP.x += z.re;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "acoth";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "Complex z;\n"
        + "Complex_Init(&z, __x, __y);\n"
        + "Complex_AcotH(&z);\n"
        + "Complex_Flip(&z);\n"
        + "Complex_Scale(&z, varpar->acoth * 2.f / PI);\n"
        + "__py += z.im;\n"
        + "__px += z.re;\n"
        + (context.isPreserveZCoordinate() ? "__pz += varpar->acoth * __z;\n": "");
  }
}