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

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.sqrt;
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.exp;

public class CoronaFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_N = "n";
  private static final String PARAM_AMP = "amp";
  private static final String PARAM_DECAY = "decay";
  private static final String[] paramNames = {PARAM_N, PARAM_AMP, PARAM_DECAY};

  private double n = 8.0;
  private double amp = 0.5;
  private double decay = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double cr_decay = Math.max(fabs(this.decay), 0.01);
    double cr_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double cr_theta = atan2(pAffineTP.y, pAffineTP.x);
    double cr_jet = this.amp * exp(-cr_decay * cr_r) * fabs(cos(this.n * cr_theta * 0.5));
    double cr_new_r = cr_r + cr_jet;

    pVarTP.x += pAmount * cr_new_r * cos(cr_theta);
    pVarTP.y += pAmount * cr_new_r * sin(cr_theta);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{n, amp, decay}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_N.equalsIgnoreCase(pName)) n = pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else if (PARAM_DECAY.equalsIgnoreCase(pName)) decay = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "corona"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float cr_decay = fmaxf(fabsf(__corona_decay), 0.01f);\n"
         + "  float cr_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float cr_theta = atan2f(__y, __x);\n"
         + "  float cr_jet = __corona_amp * expf(-cr_decay * cr_r) * fabsf(cosf(__corona_n * cr_theta * 0.5f));\n"
         + "  float cr_new_r = cr_r + cr_jet;\n"
         + "  __px += __corona * cr_new_r * cosf(cr_theta);\n"
         + "  __py += __corona * cr_new_r * sinf(cr_theta);\n";
  }
}