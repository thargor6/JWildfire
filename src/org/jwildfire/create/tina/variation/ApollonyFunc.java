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

public class ApollonyFunc extends SimpleVariationFunc implements SupportsGPU {


  /**
   * Apollony IFS
   *
   * @author Jesus Sosa
   * @date January 22, 2018
   * based on a work of:
   * http://paulbourke.net/fractals/apollony/apollony.c
   */


  private static final long serialVersionUID = 1L;


  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
//               Apollony IFS Reference:
//				 http://paulbourke.net/fractals/apollony/apollony.c

    double x, y, a0, b0, f1x, f1y;
    double r = sqrt(3.0);

    a0 = 3.0 * (1.0 + r - pAffineTP.x) / (pow(1.0 + r - pAffineTP.x, 2.0) + pAffineTP.y * pAffineTP.y) - (1.0 + r) / (2.0 + r);
    b0 = 3.0 * pAffineTP.y / (pow(1.0 + r - pAffineTP.x, 2.0) + pAffineTP.y * pAffineTP.y);
    f1x = a0 / (a0 * a0 + b0 * b0);
    f1y = -b0 / (a0 * a0 + b0 * b0);

    int w = (int) (4.0 * pContext.random());

    if ((w % 3) == 0) {
      x = a0;
      y = b0;
    } else if ((w % 3) == 1) {
      x = -f1x / 2.0 - f1y * r / 2.0;
      y = f1x * r / 2.0 - f1y / 2.0;
    } else {
      x = -f1x / 2.0 + f1y * r / 2.0;
      y = -f1x * r / 2.0 - f1y / 2.0;
    }

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  public String getName() {
    return "apollony";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float x, y, a0, b0, f1x, f1y;\n"
        + "    float r = sqrtf(3.0);\n"
        + "\n"
        + "    a0 = 3.0 * (1.0 + r - __x) / (powf(1.0 + r - __x, 2.0) + __y * __y) - (1.0 + r) / (2.0 + r);\n"
        + "    b0 = 3.0 * __y / (powf(1.0 + r - __x, 2.0) + __y * __y);\n"
        + "    f1x = a0 / (a0 * a0 + b0 * b0);\n"
        + "    f1y = -b0 / (a0 * a0 + b0 * b0);\n"
        + "\n"
        + "    int w = (int) (4.0 * RANDFLOAT());\n"
        + "\n"
        + "    if ((w % 3) == 0) {\n"
        + "      x = a0;\n"
        + "      y = b0;\n"
        + "    } else if ((w % 3) == 1) {\n"
        + "      x = -f1x / 2.0 - f1y * r / 2.0;\n"
        + "      y = f1x * r / 2.0 - f1y / 2.0;\n"
        + "    } else {\n"
        + "      x = -f1x / 2.0 + f1y * r / 2.0;\n"
        + "      y = -f1x * r / 2.0 - f1y / 2.0;\n"
        + "    }\n"
        + "\n"
        + "    __px += x * __apollony;\n"
        + "    __py += y * __apollony;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __apollony * __z;\n" : "");
  }
}