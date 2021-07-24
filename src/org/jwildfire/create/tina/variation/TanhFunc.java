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

public class TanhFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* complex vars by cothe */
    /* exp log sin cos tan sec csc cot sinh cosh tanh sech csch coth */
    //Hyperbolic Tangent TANH
    double tanhsin = sin(2.0 * pAffineTP.y);
    double tanhcos = cos(2.0 * pAffineTP.y);
    double tanhsinh = sinh(2.0 * pAffineTP.x);
    double tanhcosh = cosh(2.0 * pAffineTP.x);
    double d = (tanhcos + tanhcosh);
    if (d == 0) {
      return;
    }
    double tanhden = 1.0 / d;
    pVarTP.x += pAmount * tanhden * tanhsinh;
    pVarTP.y += pAmount * tanhden * tanhsin;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "tanh";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float d = (cosf(2.f*__y)+coshf(2.f*__x));"
        + "if(d==0.f) return;"
        +  "float tanhden = 1.f/d;\n"
        + "__px += __tanh*tanhden*sinhf(2.f*__x);\n"
        + "__py += __tanh*tanhden*sinf(2.f*__y);\n"
        + (context.isPreserveZCoordinate() ? "__pz += __tanh*__z;\n" : "");
  }
}
