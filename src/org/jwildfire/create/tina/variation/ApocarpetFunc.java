/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2011 Andreas Maschke
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

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class ApocarpetFunc extends SimpleVariationFunc implements SupportsGPU {

  /**
   * Roger Bagula Function
   *
   * @author Jesus Sosa
   * @date November 4, 2017
   * based on a work of:
   * http://paulbourke.net/fractals/kissingcircles/roger17.c
   */

  private static final long serialVersionUID = 1L;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Apolonian Carpet Reference:
    //	http://paulbourke.net/fractals/kissingcircles/roger17.c

    double x = 0, y = 0;

    double r = 1.0 / (1.0 + MathLib.sqrt(2.0));

    double denom = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y;

    int weight = (int) (6.0 * pContext.random());

    switch (weight) {
      case 0:
        x = 2.0 * pAffineTP.x * pAffineTP.y / denom;
        y = (pAffineTP.x * pAffineTP.x - pAffineTP.y * pAffineTP.y) / denom;
        break;
      case 1:
        x = pAffineTP.x * r - r;
        y = pAffineTP.y * r - r;
        break;
      case 2:
        x = pAffineTP.x * r + r;
        y = pAffineTP.y * r + r;
        break;
      case 3:
        x = pAffineTP.x * r + r;
        y = pAffineTP.y * r - r;
        break;
      case 4:
        x = pAffineTP.x * r - r;
        y = pAffineTP.y * r + r;
        break;
      case 5:
        x = (pAffineTP.x * pAffineTP.x - pAffineTP.y * pAffineTP.y) / denom;
        y = 2.0 * pAffineTP.x * pAffineTP.y / denom;
        break;
    }

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

    //			    pVarTP.color = fmod(fabs( (sqr(pVarTP.x) + sqr(pVarTP.y ))), 1.0);
  }

  public String getName() {
    return "apocarpet_js";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float x = 0, y = 0;\n"
        + "    float r = 1.0 / (1.0 + sqrtf(2.0));\n"
        + "    float denom = __x * __x + __y * __y;\n"
        + "    int weight = (int) (6.0 * RANDFLOAT());\n"
        + "    switch (weight) {\n"
        + "      case 0:\n"
        + "        x = 2.0 * __x * __y / denom;\n"
        + "        y = (__x * __x - __y * __y) / denom;\n"
        + "        break;\n"
        + "      case 1:\n"
        + "        x = __x * r - r;\n"
        + "        y = __y * r - r;\n"
        + "        break;\n"
        + "      case 2:\n"
        + "        x = __x * r + r;\n"
        + "        y = __y * r + r;\n"
        + "        break;\n"
        + "      case 3:\n"
        + "        x = __x * r + r;\n"
        + "        y = __y * r - r;\n"
        + "        break;\n"
        + "      case 4:\n"
        + "        x = __x * r - r;\n"
        + "        y = __y * r + r;\n"
        + "        break;\n"
        + "      case 5:\n"
        + "        x = (__x * __x - __y * __y) / denom;\n"
        + "        y = 2.0 * __x * __y / denom;\n"
        + "        break;\n"
        + "    }\n"
        + "\n"
        + "    __px += x * varpar->apocarpet_js;\n"
        + "    __py += y * varpar->apocarpet_js;\n"
        + (context.isPreserveZCoordinate() ? "__pz += varpar->apocarpet_js * __z;\n" : "");
  }
}