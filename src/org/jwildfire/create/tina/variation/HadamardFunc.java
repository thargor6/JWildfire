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

public class HadamardFunc extends SimpleVariationFunc  implements SupportsGPU {

  /**
   * Hadamard IFS
   *
   * @author Jesus Sosa
   * @date November 4, 2017
   * based on a work of:
   * http://paulbourke.net/fractals/pascaltriangle/roger9.c
   */

  private static final long serialVersionUID = 1L;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    //               Hadamard IFS Reference:
    //				 http://paulbourke.net/fractals/pascaltriangle/roger18.c

    double x, y;

    if (pContext.random() < 0.333) {
      x = pAffineTP.x / 2.0;
      y = pAffineTP.y / 2.0;
    } else if (pContext.random() < 0.666) {
      x = pAffineTP.y / 2.0;
      y = -pAffineTP.x / 2.0 - 0.5;
    } else {
      x = -pAffineTP.y / 2.0 - 0.5;
      y = pAffineTP.x / 2.0;
    }

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  public String getName() {
    return "hadamard_js";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float x, y;\n"
        + "\n"
        + "    if (RANDFLOAT() < 0.333) {\n"
        + "      x = __x / 2.0;\n"
        + "      y = __y / 2.0;\n"
        + "    } else if (RANDFLOAT() < 0.666) {\n"
        + "      x = __y / 2.0;\n"
        + "      y = -__x / 2.0 - 0.5;\n"
        + "    } else {\n"
        + "      x = -__y / 2.0 - 0.5;\n"
        + "      y = __x / 2.0;\n"
        + "    }\n"
        + "\n"
        + "    __px += x * __hadamard_js;\n"
        + "    __py += y * __hadamard_js;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __hadamard_js * __z;\n" : "");
  }
}