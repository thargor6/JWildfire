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

public class Loonie_3DFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* loonie_3D by Larry Berlin, http://aporev.deviantart.com/art/New-3D-Plugins-136484533?q=gallery%3Aaporev%2F8229210&qo=22 */
    double sqrvvar = pAmount * pAmount;
    double efTez = pAffineTP.z;
    double kikr;
    kikr = Math.atan2(pAffineTP.y, pAffineTP.x);

    if (efTez == 0.0) {
      efTez = kikr;
    }

    double r2 = sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(efTez); // added the z element
    if (r2 < EPSILON) {
      return;
    }
    if (r2 < sqrvvar) {
      double r = pAmount * sqrt(sqrvvar / r2 - 1.0);
      pVarTP.x += r * pAffineTP.x;
      pVarTP.y += r * pAffineTP.y;
      pVarTP.z += r * efTez * 0.5;
    } else {
      pVarTP.x += pAmount * pAffineTP.x;
      pVarTP.y += pAmount * pAffineTP.y;
      pVarTP.z += pAmount * efTez * 0.5;
    }
  }

  @Override
  public String getName() {
    return "loonie_3D";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float sqrvvar = __loonie_3D * __loonie_3D;\n"
        + "float efTez = __z;\n"
        + "float kikr;\n"
        + "kikr = atan2f(__y, __x);\n"
        + "if (efTez == 0.0) {\n"
        + "  efTez = kikr;\n"
        + "}\n"
        + "float r2 = __x*__x + __y*__y + efTez*efTez;\n"
        + "if (r2 < 1.e-6f) {\n"
        + "  __doHide=true;\n"
        + "}\n"
        + "else {\n"
        + "  __doHide = false;\n"
        + "  if (r2 < sqrvvar) {\n"
        + "    float r = __loonie_3D * sqrtf(sqrvvar / r2 - 1.0f);\n"
        + "    __px += r * __x;\n"
        + "    __py += r * __y;\n"
        + "    __pz += r * efTez * 0.5;\n"
        + "  } else {\n"
        + "     __px += __loonie_3D * __x;\n"
        + "     __py += __loonie_3D * __y;\n"
        + "     __pz += __loonie_3D * efTez * 0.5;\n"
        + "  }\n"
        + "}\n";
  }
}
