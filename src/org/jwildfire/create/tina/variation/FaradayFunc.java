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

public class FaradayFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_MODES = "modes";
  private static final String PARAM_AMP = "amp";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_MODES, PARAM_AMP};

  private double freq = 4.0;
  private int modes = 4;
  private double amp = 0.2;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double fa_modes = Math.max(fabs((double) this.modes), 1.0);
    double fa_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double fa_theta = atan2(pAffineTP.y, pAffineTP.x);
    double fa_wave = this.amp * cos(this.freq * fa_r) * cos(fa_modes * fa_theta);
    double fa_new_r = fa_r + fa_wave;

    pVarTP.x += pAmount * fa_new_r * cos(fa_theta);
    pVarTP.y += pAmount * fa_new_r * Math.sin(fa_theta);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, modes, amp}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_MODES.equalsIgnoreCase(pName)) modes = (int) pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "faraday"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float fa_modes = fmaxf(fabsf((float)__faraday_modes), 1.0f);\n"
         + "  float fa_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float fa_theta = atan2f(__y, __x);\n"
         + "  float fa_wave = __faraday_amp * cosf(__faraday_freq * fa_r) * cosf(fa_modes * fa_theta);\n"
         + "  float fa_new_r = fa_r + fa_wave;\n"
         + "  __px += __faraday * fa_new_r * cosf(fa_theta);\n"
         + "  __py += __faraday * fa_new_r * sinf(fa_theta);\n";
  }
}