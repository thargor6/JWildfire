/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2026 Andreas Maschke
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

public class CamFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_K1 = "k1";
  private static final String PARAM_K2 = "k2";
  private static final String[] paramNames = {PARAM_K1, PARAM_K2};

  private double k1 = 0.5;
  private double k2 = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double cam_r2 = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y;
    double cam_d = 1.0 + this.k1 * cam_r2 + this.k2 * cam_r2 * cam_r2;

    pVarTP.x += pAmount * pAffineTP.x * cam_d;
    pVarTP.y += pAmount * pAffineTP.y * cam_d;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{k1, k2}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_K1.equalsIgnoreCase(pName)) k1 = pValue;
    else if (PARAM_K2.equalsIgnoreCase(pName)) k2 = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "cam"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float cam_r2 = __x * __x + __y * __y;\n"
         + "  float cam_d = 1.0f + __cam_k1 * cam_r2 + __cam_k2 * cam_r2 * cam_r2;\n"
         + "  __px += __cam * __x * cam_d;\n"
         + "  __py += __cam * __y * cam_d;\n";
  }
}