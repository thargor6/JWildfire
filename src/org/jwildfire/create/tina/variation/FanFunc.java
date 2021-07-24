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

public class FanFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double dx = M_PI * pXForm.getXYCoeff20() * pXForm.getXYCoeff20() + SMALL_EPSILON;
    double dx2 = dx / 2;
    double a;
    if ((pAffineTP.getPrecalcAtan() + pXForm.getXYCoeff21() - ((int) ((pAffineTP.getPrecalcAtan() + pXForm.getXYCoeff21()) / dx)) * dx) > dx2)
      a = pAffineTP.getPrecalcAtan() - dx2;
    else
      a = pAffineTP.getPrecalcAtan() + dx2;
    double sinr = sin(a);
    double cosr = cos(a);
    double r = pAmount * sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    pVarTP.x += r * cosr;
    pVarTP.y += r * sinr;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "fan";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float t = PI*xform->c*xform->c ADD_EPSILON;\n"
        + "float dx = (fmodf(__phi+xform->f,t)>.5f*t)\n"
        + "?__r*cosf(__phi-.5f*t)\n"
        + ":__r*cosf(__phi+.5f*t);\n"
        + "__px += __fan*dx;\n"
        + "float dy = (fmodf(__phi+xform->f,t)>.5f*t)\n"
        + "?__r*sinf(__phi-.5f*t)\n"
        + ":__r*sinf(__phi+.5f*t);\n"
        + "__py += __fan*dy;\n"
        + (context.isPreserveZCoordinate() ?  "__pz += __fan*__z;\n" : "");
  }
}
