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
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.exp;

public class AfterShockFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String PARAM_DECAY = "decay";

  private static final String[] paramNames = {PARAM_FREQ, PARAM_AMP, PARAM_DECAY};

  private double freq = 5.0;
  private double amp = 0.3;
  private double decay = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double as_decay = Math.max(fabs(this.decay), 0.01);
    double as_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double as_theta = atan2(pAffineTP.y, pAffineTP.x);
    
    double as_ring = this.amp * sin(this.freq * as_r) * exp(-as_decay * as_r);
    double as_new_r = as_r + as_ring;

    pVarTP.x += pAmount * as_new_r * cos(as_theta);
    pVarTP.y += pAmount * as_new_r * sin(as_theta);

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{freq, amp, decay};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) {
      freq = pValue;
    } else if (PARAM_AMP.equalsIgnoreCase(pName)) {
      amp = pValue;
    } else if (PARAM_DECAY.equalsIgnoreCase(pName)) {
      decay = pValue;
    } else {
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "aftershock";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // __aftershock represents variation amount
    // __aftershock_freq, __aftershock_amp, __aftershock_decay represent parameters
    return "  float as_decay = fmaxf(fabsf(__aftershock_decay), 0.01f);\n"
         + "  float as_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float as_theta = atan2f(__y, __x);\n"
         + "  float as_ring = __aftershock_amp * sinf(__aftershock_freq * as_r) * expf(-as_decay * as_r);\n"
         + "  float as_new_r = as_r + as_ring;\n"
         + "  __px += __aftershock * as_new_r * cosf(as_theta);\n"
         + "  __py += __aftershock * as_new_r * sinf(as_theta);\n";
  }
}