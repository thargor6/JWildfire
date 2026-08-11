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

public class SimplexWarpFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_AMP};

  private double freq = 2.0;
  private double amp = 0.4;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double sw_x = this.freq * pAffineTP.x;
    double sw_y = this.freq * pAffineTP.y;
    double sw_v1 = sin(sw_x * 1.3 + sw_y * 0.7 + sin(sw_x * 0.9 - sw_y * 1.1) * 2.0);
    double sw_v2 = sin(sw_y * 1.1 + sw_x * 0.8 + sin(sw_y * 1.2 + sw_x * 0.6) * 2.0);

    pVarTP.x += pAmount * this.amp * sw_v1;
    pVarTP.y += pAmount * this.amp * sw_v2;
    
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
  public String getName() { return "simplex_warp"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float sw_x = __simplex_warp_freq * __x;\n"
         + "  float sw_y = __simplex_warp_freq * __y;\n"
         + "  float sw_v1 = sinf(sw_x * 1.3f + sw_y * 0.7f + sinf(sw_x * 0.9f - sw_y * 1.1f) * 2.0f);\n"
         + "  float sw_v2 = sinf(sw_y * 1.1f + sw_x * 0.8f + sinf(sw_y * 1.2f + sw_x * 0.6f) * 2.0f);\n"
         + "  __px += __simplex_warp * __simplex_warp_amp * sw_v1;\n"
         + "  __py += __simplex_warp * __simplex_warp_amp * sw_v2;\n";
  }
}