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

public class SpiralwingFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // spiralwing by Raykoid666, http://raykoid666.deviantart.com/art/re-pack-1-new-plugins-100092186 
    double c1 = sqr(pAffineTP.x);
    double c2 = sqr(pAffineTP.y);
    double d = pAmount / (c1 + c2 + SMALL_EPSILON);
    c2 = sin(c2); // speedup

    pVarTP.x += d * cos(c1) * c2;
    pVarTP.y += d * sin(c1) * c2;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "spiralwing";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float c1 = __x*__x;\n"
        + "float c2 = __y*__y;\n"
        + "\n"
        + "__px += __spiralwing * ((1.f/__r2 ADD_EPSILON)*cosf(c1 ADD_EPSILON)*sinf(c2 ADD_EPSILON));\n"
        + "__py += __spiralwing * ((1.f/__r2 ADD_EPSILON)*sinf(c1 ADD_EPSILON)*sinf(c2 ADD_EPSILON));\n"
        + "\n"
        + (context.isPreserveZCoordinate() ? "__pz += __spiralwing*__z;\n" : "");
  }
}
