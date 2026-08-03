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

public class CalcinationFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_INTENSITY = "intensity";
  private static final String PARAM_DECAY = "decay";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_INTENSITY, PARAM_DECAY};

  private double freq = 4.0;
  private double intensity = 0.3;
  private double decay = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double cal_decay = Math.max(fabs(this.decay), 0.01);
    double cal_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double cal_theta = atan2(pAffineTP.y, pAffineTP.x);
    double cal_heat = this.intensity * sin(this.freq * cal_r) * exp(-cal_decay * cal_r);
    double cal_new_r = Math.max(cal_r - cal_heat, 0.0);

    pVarTP.x += pAmount * cal_new_r * cos(cal_theta);
    pVarTP.y += pAmount * cal_new_r * sin(cal_theta);

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
}

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, intensity, decay}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_INTENSITY.equalsIgnoreCase(pName)) intensity = pValue;
    else if (PARAM_DECAY.equalsIgnoreCase(pName)) decay = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "calcination"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float cal_decay = fmaxf(fabsf(__calcination_decay), 0.01f);\n"
         + "  float cal_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float cal_theta = atan2f(__y, __x);\n"
         + "  float cal_heat = __calcination_intensity * sinf(__calcination_freq * cal_r) * expf(-cal_decay * cal_r);\n"
         + "  float cal_new_r = fmaxf(cal_r - cal_heat, 0.0f);\n"
         + "  __px += __calcination * cal_new_r * cosf(cal_theta);\n"
         + "  __py += __calcination * cal_new_r * sinf(cal_theta);\n";
  }
}