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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class UnpolarFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r = exp(pAffineTP.y);
    double s = sin(pAffineTP.x);
    double c = cos(pAffineTP.x);
    pVarTP.y += vvar_2 * r * c;
    pVarTP.x += vvar_2 * r * s;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "unpolar";
  }

  double vvar, vvar_2;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    vvar = pAmount / M_PI;
    vvar_2 = vvar * 0.5;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float vvar = __unpolar / PI;\n"
        + "float vvar_2 = vvar * 0.5;\n"
        + "float r = expf(__y);\n"
        + "    float s = sinf(__x);\n"
        + "    float c = cosf(__x);\n"
        + "    __py += vvar_2 * r * c;\n"
        + "    __px += vvar_2 * r * s;\n"
        + (context.isPreserveZCoordinate() ? "      __pz += __unpolar * __z;\n" : "");
  }
}
