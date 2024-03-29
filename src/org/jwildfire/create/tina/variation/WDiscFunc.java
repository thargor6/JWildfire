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

import org.jwildfire.base.mathlib.DoubleWrapperWF;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class WDiscFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private DoubleWrapperWF sina = new DoubleWrapperWF();
  private DoubleWrapperWF cosa = new DoubleWrapperWF();

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // wdisc my Michael Faber, http://michaelfaber.deviantart.com/art/The-Lost-Variations-258913970 */
    double a = M_PI / (sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)) + 1.0);
    double r = atan2(pAffineTP.y, pAffineTP.x) * M_1_PI;

    if (r > 0.0)
      a = M_PI - a;

    sinAndCos(a, sina, cosa);

    pVarTP.x += pAmount * r * cosa.value;
    pVarTP.y += pAmount * r * sina.value;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "wdisc";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float a = M_PI_F/(__r + 1.f);\n"
        + "float r = atan2f(__y, __x) * M_1_PI_F;\n"
        + "if (r > 0.f)\n"
        + "    a = M_PI_F - a;\n"
        + "float c;\n"
        + "float s;\n"
        + "sincosf(a, &s, &c);\n"
        + "\n"
        + "__px += __wdisc * r * c;\n"
        + "__py += __wdisc * r * s;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __wdisc*__z;\n" : "");
  }
}
