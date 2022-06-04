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

import static org.jwildfire.base.mathlib.MathLib.sqr;

public class EnnepersFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // ennepers by Raykoid666, http://raykoid666.deviantart.com/art/re-pack-1-new-plugins-100092186 
    pVarTP.x = pAmount * (pAffineTP.x - ((sqr(pAffineTP.x) * pAffineTP.x) / 3.0)) + pAffineTP.x * sqr(pAffineTP.y);
    pVarTP.y = pAmount * (pAffineTP.y - ((sqr(pAffineTP.y) * pAffineTP.y) / 3.0)) + pAffineTP.y * sqr(pAffineTP.x);
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "ennepers";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "__px = __ennepers * (__x - ((__x*__x*__x)/3.0f)) + __x*__y*__y;\n"
        + "__py = __ennepers * (__y - ((__y*__y*__y)/3.0f)) + __y*__x*__x;\n"
        + "\n"
        + (context.isPreserveZCoordinate() ? "__pz += __ennepers*__z;\n" : "");
  }
}
