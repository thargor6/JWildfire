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

public class GradientWarpFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String PARAM_SEED = "seed";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_AMP, PARAM_SEED};

  private double freq = 2.0;
  private double amp = 0.5;
  private double seed = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double gw_px = this.freq * pAffineTP.x + this.seed;
    double gw_py = this.freq * pAffineTP.y + this.seed * 1.3;
    double gw_dx = this.amp * cos(gw_px) * sin(gw_py);
    double gw_dy = this.amp * sin(gw_px) * cos(gw_py);

    pVarTP.x += pAmount * gw_dx;
    pVarTP.y += pAmount * gw_dy;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, amp, seed}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else if (PARAM_SEED.equalsIgnoreCase(pName)) seed = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "gradient_warp"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float gw_px = __gradient_warp_freq * __x + __gradient_warp_seed;\n"
         + "  float gw_py = __gradient_warp_freq * __y + __gradient_warp_seed * 1.3f;\n"
         + "  float gw_dx = __gradient_warp_amp * cosf(gw_px) * sinf(gw_py);\n"
         + "  float gw_dy = __gradient_warp_amp * sinf(gw_px) * cosf(gw_py);\n"
         + "  __px += __gradient_warp * gw_dx;\n"
         + "  __py += __gradient_warp * gw_dy;\n";
  }
}