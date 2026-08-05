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

public class FlourishFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_N = "n";
  private static final String PARAM_AMP = "amp";
  private static final String PARAM_DAMP = "damp";
  private static final String[] paramNames = {PARAM_N, PARAM_AMP, PARAM_DAMP};

  private double n = 3.0;
  private double amp = 0.5;
  private double damp = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double fl_damp = Math.max(fabs(this.damp), 0.01);
    double fl_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double fl_theta = atan2(pAffineTP.y, pAffineTP.x);
    double fl_loop = this.amp * sin(this.n * fl_theta) * exp(-fl_damp * fl_r);
    double fl_new_r = fl_r + fl_loop;

    pVarTP.x += pAmount * fl_new_r * cos(fl_theta);
    pVarTP.y += pAmount * fl_new_r * sin(fl_theta);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{n, amp, damp}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_N.equalsIgnoreCase(pName)) n = pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else if (PARAM_DAMP.equalsIgnoreCase(pName)) damp = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "flourish"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float fl_damp = fmaxf(fabsf(__flourish_damp), 0.01f);\n"
         + "  float fl_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float fl_theta = atan2f(__y, __x);\n"
         + "  float fl_loop = __flourish_amp * sinf(__flourish_n * fl_theta) * expf(-fl_damp * fl_r);\n"
         + "  float fl_new_r = fl_r + fl_loop;\n"
         + "  __px += __flourish * fl_new_r * cosf(fl_theta);\n"
         + "  __py += __flourish * fl_new_r * sinf(fl_theta);\n";
  }
}