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
import static org.jwildfire.base.mathlib.MathLib.M_2_PI;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class Sqrt_AcothFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
//Sqrt AcotH by Whittaker Courtney 12-19-2018

    Complex z = new Complex(pAffineTP.x, pAffineTP.y);

    z.Sqrt();
    z.AcotH();
    z.Scale(pAmount * M_2_PI);

    if (pContext.random() < 0.5){
      pVarTP.y += z.im;
      pVarTP.x += z.re;
    }
    else{
      pVarTP.y += -z.im;
      pVarTP.x += -z.re;
    }

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "sqrt_acoth";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "Complex z;\n"
            + "Complex_Init(&z, __x, __y);\n"
            + "Complex_Sqrt(&z);\n"
            + "Complex_AcotH(&z);\n"
            + "Complex_Scale(&z, varpar->sqrt_acoth * 2.f / PI);\n"
            + "if(RANDFLOAT()<0.5) {\n"
            + "  __py += z.im;\n"
            + "  __px += z.re;\n"
            + "}\n"
            + "else {\n"
            + "  __py += -z.im;\n"
            + "  __px += -z.re;\n"
            + "}\n"
            + (context.isPreserveZCoordinate() ? "__pz += varpar->sqrt_acoth * __z;\n": "");
  }
}
