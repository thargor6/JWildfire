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

public class StandingWaveFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQX = "freq_x";
  private static final String PARAM_FREQY = "freq_y";
  private static final String PARAM_AMP = "amp";
  private static final String[] paramNames = {PARAM_FREQX, PARAM_FREQY, PARAM_AMP};

  private double freqx = 3.0;
  private double freqy = 3.0;
  private double amp = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Correct orthogonal wave superposition math
    double dx = this.amp * sin(this.freqx * Math.PI * pAffineTP.x) * cos(this.freqy * Math.PI * pAffineTP.y);
    double dy = this.amp * cos(this.freqx * Math.PI * pAffineTP.x) * sin(this.freqy * Math.PI * pAffineTP.y);

    pVarTP.x += pAmount * (pAffineTP.x + dx);
    pVarTP.y += pAmount * (pAffineTP.y + dy);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freqx, freqy, amp}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQX.equalsIgnoreCase(pName)) freqx = pValue;
    else if (PARAM_FREQY.equalsIgnoreCase(pName)) freqy = pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "standing_wave"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // Balanced additive logic with literal floats to safeguard strict OpenCL platforms
    return "  float dx = __standing_wave_amp * sinf(__standing_wave_freqx * 3.14159265f * __x) * cosf(__standing_wave_freqy * 3.14159265f * __y);\n"
         + "  float dy = __standing_wave_amp * cosf(__standing_wave_freqx * 3.14159265f * __x) * sinf(__standing_wave_freqy * 3.14159265f * __y);\n"
         + "  __px += __standing_wave * (__x + dx);\n"
         + "  __py += __standing_wave * (__y + dy);\n";
  }
}