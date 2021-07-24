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

import static org.jwildfire.base.mathlib.MathLib.SMALL_EPSILON;

public class RingsFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double dx = pXForm.getXYCoeff20() * pXForm.getXYCoeff20() + SMALL_EPSILON;
    double r = pAffineTP.getPrecalcSqrt();
    r = r + dx - ((int) ((r + dx) / (2 * dx))) * 2 * dx - dx + r * (1 - dx);
    pVarTP.x += r * pAffineTP.getPrecalcCosA();
    pVarTP.y += r * pAffineTP.getPrecalcSinA();
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "rings";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float dx = xform->c*xform->c ADD_EPSILON;\n"
         + "float r = sqrtf(__x*__x+__y*__y) ADD_EPSILON;\n"
         +" float d = r + dx - ((int) ((r + dx) / (2 * dx))) * 2 * dx - dx + r * (1 - dx);\n"
        + "__px += d * __y / r;\n"
        + "__py += d* __x / r;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __rings*__z;\n" : "");
  }
}
