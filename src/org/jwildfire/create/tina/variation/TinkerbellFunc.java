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

public class TinkerbellFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_D};

  private double a = -0.3;
  private double b = -0.6013;
  private double c = 2.0;
  private double d = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double nx = pAffineTP.x * pAffineTP.x - pAffineTP.y * pAffineTP.y + this.a * pAffineTP.x + this.b * pAffineTP.y;
    double ny = 2.0 * pAffineTP.x * pAffineTP.y + this.c * pAffineTP.x + this.d * pAffineTP.y;

    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{a, b, c, d}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName)) a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName)) b = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName)) c = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName)) d = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "tinkerbell"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float nx = __x * __x - __y * __y + __tinkerbell_a * __x + __tinkerbell_b * __y;\n"
         + "  float ny = 2.0f * __x * __y + __tinkerbell_c * __x + __tinkerbell_d * __y;\n"
         + "  __px += __tinkerbell * nx;\n"
         + "  __py += __tinkerbell * ny;\n";
  }
}