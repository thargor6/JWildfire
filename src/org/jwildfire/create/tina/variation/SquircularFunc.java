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

public class SquircularFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double u = pAffineTP.x;
    double v = pAffineTP.y;
    double r = u * u + v * v;
    double rs = sqrt(r);
    double xs = u > 0 ? 1.0 : -1.0;

    r = sqrt(pAmount * pAmount * r - 4 * u * u * v * v);
    r = sqrt(1 + u * u / (v * v) - rs / (pAmount * v * v) * r);
    r = r / sqrt(2);

    pVarTP.x += xs * r;
    pVarTP.y += v / u * r;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "squircular";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float u = __x;\n"
        + "    float v = __y;\n"
        + "    float r = u * u + v * v;\n"
        + "    float rs = sqrtf(r);\n"
        + "    float xs = u > 0 ? 1.0 : -1.0;\n"
        + "\n"
        + "    r = sqrtf(__squircular * __squircular * r - 4 * u * u * v * v);\n"
        + "    r = sqrtf(1 + u * u / (v * v) - rs / (__squircular * v * v) * r);\n"
        + "    r = r / sqrtf(2);\n"
        + "\n"
        + "    __px += xs * r;\n"
        + "    __py += v / u * r;\n"
        + "\n"
        + (context.isPreserveZCoordinate() ? "      __pz += __squircular * __z;\n" : "");
  }
}
