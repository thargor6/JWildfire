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

import static org.jwildfire.base.mathlib.MathLib.M_1_PI;


public class Arcsech2Func extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // author Tatyana Zabanova 2017. Implemented by DarkBeam 2018
    Complex z = new Complex(pAffineTP.x, pAffineTP.y);
    z.Recip();
    Complex z2 = new Complex(z);
    z2.Dec(); // z-1
    z2.Sqrt(); // Sqrt(z-1)
    Complex z3 = new Complex(z);
    z3.Inc(); // z+1
    z3.Sqrt(); // Sqrt(z+1)
    z3.Mul(z2);
    z.Add(z3); // z + Sqrt(z-1)*Sqrt(z+1)
    z.Log();
    z.Scale(pAmount * 2.0 * M_1_PI);

    pVarTP.y += z.im;
    if (z.im < 0) {
      pVarTP.x += z.re;
      pVarTP.y += 1.0;
    } else {
      pVarTP.x -= z.re;
      pVarTP.y -= 1.0;
    }
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "arcsech2";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }


  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "Complex z;\n"
        + "Complex_Init(&z, __x, __y);\n"
        + "Complex_Recip(&z);\n"
        + "Complex z2;\n"
        + "Complex_Init(&z2, z.re, z.im);\n"
        + "Complex_Dec(&z2);\n"
        + "Complex_Sqrt(&z2);\n"
        + "Complex z3;\n"
        +" Complex_Init(&z3, z.re, z.im);\n"
        + "Complex_Inc(&z3);\n"
        + "Complex_Sqrt(&z3);\n"
        + "Complex_Mul(&z3, &z2);\n"
        + "Complex_Add(&z, &z3);\n"
        + "Complex_Log(&z);\n"
        + "Complex_Scale(&z, __arcsech2 * 2.0f / PI);\n"
        + "\n"
        + "    __py += z.im;\n"
        + "    if (z.im < 0) {\n"
        + "      __px += z.re;\n"
        + "      __py += 1.0;\n"
        + "    } else {\n"
        + "      __px -= z.re;\n"
        + "      __py -= 1.0;\n"
        + "    }\n"
        + (context.isPreserveZCoordinate() ? "__pz += __arcsech2 * __z;\n" : "");
  }
}
