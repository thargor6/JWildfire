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

import static org.jwildfire.base.mathlib.MathLib.*;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class ChrysanthemumFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    // Autor: Jesus Sosa
    // Date: 01/feb/2018
    // Reference:
    // http://paulbourke.net/geometry/chrysanthemum/

    double u = 21.0 * M_PI * pContext.random();
    double p4 = sin(17.0 * u / 3.0);
    double p8 = sin(2.0 * cos(3.0 * u) - 28.0 * u);
    double r = 5.0 * (1 + sin(11.0 * u / 5.0)) - 4.0 * p4 * p4 * p4 * p4 * p8 * p8 * p8 * p8 * p8 * p8 * p8 * p8;
    r *= pAmount * 0.1;
    pVarTP.x += r * cos(u);
    pVarTP.y += r * sin(u);

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "chrysanthemum";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float u = 21.0 * PI * RANDFLOAT();\n"
        + "    float p4 = sinf(17.0 * u / 3.0);\n"
        + "    float p8 = sinf(2.0 * cosf(3.0 * u) - 28.0 * u);\n"
        + "    float r = 5.0 * (1 + sinf(11.0 * u / 5.0)) - 4.0 * p4 * p4 * p4 * p4 * p8 * p8 * p8 * p8 * p8 * p8 * p8 * p8;\n"
        + "    r *= __chrysanthemum;\n"
        + "    __px += r * cosf(u);\n"
        + "    __py += r * sinf(u);\n"
        + "\n"
        + (context.isPreserveZCoordinate() ? "      __pz += __chrysanthemum * __z;\n" : "");
  }
}
