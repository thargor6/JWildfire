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

import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.cos;

public class PickoverFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_D};

  private double a = 1.0;
  private double b = 2.0;
  private double c = 0.5;
  private double d = -0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double nx = sin(this.a * pAffineTP.y) + this.c * cos(this.a * pAffineTP.x);
    double ny = sin(this.b * pAffineTP.x) + this.d * cos(this.b * pAffineTP.y);

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
  public String getName() { return "pickover"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float nx = sinf(__pickover_a * __y) + __pickover_c * cosf(__pickover_a * __x);\n"
         + "  float ny = sinf(__pickover_b * __x) + __pickover_d * cosf(__pickover_b * __y);\n"
         + "  __px += __pickover * nx;\n"
         + "  __py += __pickover * ny;\n";
  }
}