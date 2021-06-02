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

import static org.jwildfire.base.mathlib.MathLib.M_PI_2;
import static org.jwildfire.base.mathlib.MathLib.atan2;

public class InflateZ_4Func extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* inflateZ_4 by Larry Berlin, http://aporev.deviantart.com/art/New-3D-Plugins-136484533?q=gallery%3Aaporev%2F8229210&qo=22 */
    double ang1 = atan2(pAffineTP.y, pAffineTP.x);
    double rndm = pContext.random();

    double val1 = (M_PI_2 - ang1);
    if (rndm < 0.5) {
      val1 = -val1;
    }
    pVarTP.z += pAmount * val1 * 0.25;
  }

  @Override
  public String getName() {
    return "inflateZ_4";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_ZTRANSFORM, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float ang1 = atan2f(__y, __x);\n"
        + "    float rndm = RANDFLOAT();\n"
        + "\n"
        + "    float val1 = ((PI*0.5f) - ang1);\n"
        + "    if (rndm < 0.5) {\n"
        + "      val1 = -val1;\n"
        + "    }\n"
        + "    __pz += varpar->inflateZ_4 * val1 * 0.25;\n";
  }
}
