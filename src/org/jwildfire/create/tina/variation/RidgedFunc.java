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
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class RidgedFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_AMP};

  private double freq = 2.0;
  private double amp = 0.4;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double rg_x = this.freq * pAffineTP.x;
    double rg_y = this.freq * pAffineTP.y;
    double rg_v1 = 1.0 - fabs(sin(rg_x + rg_y * 0.3));
    double rg_v2 = (1.0 - fabs(sin(2.1 * rg_x - rg_y * 0.7))) * 0.5;
    double rg_val = this.amp * (rg_v1 + rg_v2);
    double rg_theta = atan2(pAffineTP.y, pAffineTP.x);

    pVarTP.x += pAmount * rg_val * cos(rg_theta);
    pVarTP.y += pAmount * rg_val * sin(rg_theta);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, amp}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "ridged"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float rg_x = __ridged_freq * __x;\n"
         + "  float rg_y = __ridged_freq * __y;\n"
         + "  float rg_v1 = 1.0f - fabsf(sinf(rg_x + rg_y * 0.3f));\n"
         + "  float rg_v2 = (1.0f - fabsf(sinf(2.1f * rg_x - rg_y * 0.7f))) * 0.5f;\n"
         + "  float rg_val = __ridged_amp * (rg_v1 + rg_v2);\n"
         + "  float rg_theta = atan2f(__y, __x);\n"
         + "  __px += __ridged * rg_val * cosf(rg_theta);\n"
         + "  __py += __ridged * rg_val * sinf(rg_theta);\n";
  }
}