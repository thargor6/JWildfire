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

public class PDJv2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_E = "e";
  private static final String PARAM_F = "f";
  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F};

  private double a = -0.7;
  private double b = 1.0;
  private double c = 0.0;
  private double d = 2.0;
  private double e = 0.0;
  private double f = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    pVarTP.x += pAmount * (sin(this.a * pAffineTP.y + this.e) - cos(this.b * pAffineTP.x + this.e));
    pVarTP.y += pAmount * (sin(this.c * pAffineTP.x + this.f) - cos(this.d * pAffineTP.y + this.f));
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{a, b, c, d, e, f}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName)) a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName)) b = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName)) c = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName)) d = pValue;
    else if (PARAM_E.equalsIgnoreCase(pName)) e = pValue;
    else if (PARAM_F.equalsIgnoreCase(pName)) f = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "pdj_v2"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  __px += __pdj_v2 * (sinf(__pdj_v2_a * __y + __pdj_v2_e) - cosf(__pdj_v2_b * __x + __pdj_v2_e));\n"
         + "  __py += __pdj_v2 * (sinf(__pdj_v2_c * __x + __pdj_v2_f) - cosf(__pdj_v2_d * __y + __pdj_v2_f));\n";
  }
}