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

public class FBMWarpFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_AMP};

  private double freq = 1.0;
  private double amp = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = pAffineTP.x * this.freq;
    double y = pAffineTP.y * this.freq;

    double dx = this.amp * (sin(x + 0.7) * cos(y * 1.3) +
                 0.5 * sin(x * 2.0 + 1.1) * cos(y * 2.6 + 0.2) +
                 0.25 * sin(x * 4.0 + 2.3) * cos(y * 5.2 + 0.9));
                 
    double dy = this.amp * (cos(x * 1.1 + 0.3) * sin(y * 0.9 + 0.5) +
                 0.5 * cos(x * 2.2 + 1.3) * sin(y * 1.8 + 1.1) +
                 0.25 * cos(x * 4.4 + 2.7) * sin(y * 3.6 + 2.1));

    pVarTP.x += pAmount * (pAffineTP.x + dx);
    pVarTP.y += pAmount * (pAffineTP.y + dy);
    
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
  public String getName() { return "fbm_warp"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float x = __x * __fbm_warp_freq;\n"
         + "  float y = __y * __fbm_warp_freq;\n"
         + "  float dx = __fbm_warp_amp * (sinf(x + 0.7f) * cosf(y * 1.3f) +\n"
         + "            0.5f * sinf(x * 2.0f + 1.1f) * cosf(y * 2.6f + 0.2f) +\n"
         + "            0.25f * sinf(x * 4.0f + 2.3f) * cosf(y * 5.2f + 0.9f));\n"
         + "  float dy = __fbm_warp_amp * (cosf(x * 1.1f + 0.3f) * sinf(y * 0.9f + 0.5f) +\n"
         + "            0.5f * cosf(x * 2.2f + 1.3f) * sinf(y * 1.8f + 1.1f) +\n"
         + "            0.25f * cosf(x * 4.4f + 2.7f) * sinf(y * 3.6f + 2.1f));\n"
         + "  __px += __fbm_warp * (__x + dx);\n"
         + "  __py += __fbm_warp * (__y + dy);\n";
  }
}