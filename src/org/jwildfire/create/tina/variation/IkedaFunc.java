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

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class IkedaFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_U = "u";
  private static final String[] paramNames = {PARAM_U};

  private double u = 0.9;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double t = 0.4 - 6.0 / (1.0 + pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double cos_t = cos(t);
    double sin_t = sin(t);
    double nx = 1.0 + this.u * (pAffineTP.x * cos_t - pAffineTP.y * sin_t);
    double ny = this.u * (pAffineTP.x * sin_t + pAffineTP.y * cos_t);

    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{u}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_U.equalsIgnoreCase(pName)) u = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "ikeda"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float t = 0.4f - 6.0f / (1.0f + __x * __x + __y * __y);\n"
         + "  float cos_t = cosf(t);\n"
         + "  float sin_t = sinf(t);\n"
         + "  float nx = 1.0f + __ikeda_u * (__x * cos_t - __y * sin_t);\n"
         + "  float ny = __ikeda_u * (__x * sin_t + __y * cos_t);\n"
         + "  __px += __ikeda * nx;\n"
         + "  __py += __ikeda * ny;\n";
  }
}