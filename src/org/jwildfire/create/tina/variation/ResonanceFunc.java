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

public class ResonanceFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_DECAY = "decay";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_DECAY};

  private double freq = 3.0;
  private double decay = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double rs_freq = Math.max(fabs(this.freq), 0.01);
    double rs_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double rs_theta = atan2(pAffineTP.y, pAffineTP.x);
    double rs_ring = Math.round(rs_r * rs_freq) / rs_freq;
    double rs_dist = rs_r - rs_ring;
    double rs_pull = this.decay * exp(-rs_dist * rs_dist * rs_freq * rs_freq * 4.0);
    double rs_new_r = rs_r - rs_pull * rs_dist;

    pVarTP.x += pAmount * rs_new_r * cos(rs_theta);
    pVarTP.y += pAmount * rs_new_r * sin(rs_theta);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, decay}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_DECAY.equalsIgnoreCase(pName)) decay = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "resonance"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float rs_freq = fmaxf(fabsf(__resonance_freq), 0.01f);\n"
         + "  float rs_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float rs_theta = atan2f(__y, __x);\n"
         + "  float rs_ring = roundf(rs_r * rs_freq) / rs_freq;\n"
         + "  float rs_dist = rs_r - rs_ring;\n"
         + "  float rs_pull = __resonance_decay * expf(-rs_dist * rs_dist * rs_freq * rs_freq * 4.0f);\n"
         + "  float rs_new_r = rs_r - rs_pull * rs_dist;\n"
         + "  __px += __resonance * rs_new_r * cosf(rs_theta);\n"
         + "  __py += __resonance * rs_new_r * sinf(rs_theta);\n";
  }
}