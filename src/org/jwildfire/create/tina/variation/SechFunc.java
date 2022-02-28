/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-20211 Andreas Maschke

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

public class SechFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* complex vars by cothe */
    /* exp log sin cos tan sec csc cot sinh cosh tanh sech csch coth */
    //Hyperbolic Secant SECH
    double sechsin = sin(pAffineTP.y);
    double sechcos = cos(pAffineTP.y);
    double sechsinh = sinh(pAffineTP.x);
    double sechcosh = cosh(pAffineTP.x);
    double d = (cos(2.0 * pAffineTP.y) + cosh(2.0 * pAffineTP.x));
    if (d == 0) {
      return;
    }
    double sechden = 2.0 / d;
    pVarTP.x += pAmount * sechden * sechcos * sechcosh;
    pVarTP.y -= pAmount * sechden * sechsin * sechsinh;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "sech";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float d = (cosf(2.f * __y) + coshf(2.f * __x));\n"
        + "if(d==0) return;"
        + "float sechden = 2.f/d;\n"
        + "__px += __sech * sechden * cosf(__y) * coshf(__x);\n"
        + "__py -= __sech * sechden * sinf(__y) * sinhf(__x);\n"
        + (context.isPreserveZCoordinate() ? "__pz += __sech*__z;\n" : "");
  }
}
