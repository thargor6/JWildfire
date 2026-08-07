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

public class QuasicrystalFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_AMP};

  private double freq = 3.0;
  private double amp = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double qc_sum_x = 0.0;
    double qc_sum_y = 0.0;
    double qc_step = 1.2566370614359172;

    for (int qc_k = 0; qc_k < 5; qc_k++) {
      double qc_angle = (double) qc_k * qc_step;
      double qc_proj = pAffineTP.x * cos(qc_angle) + pAffineTP.y * sin(qc_angle);
      double qc_wave = cos(this.freq * qc_proj);
      qc_sum_x += qc_wave * cos(qc_angle);
      qc_sum_y += qc_wave * sin(qc_angle);
    }

    pVarTP.x += pAmount * (pAffineTP.x + this.amp * qc_sum_x * 0.2);
    pVarTP.y += pAmount * (pAffineTP.y + this.amp * qc_sum_y * 0.2);
    
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
  public String getName() { return "quasicrystal"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float qc_sum_x = 0.0f;\n"
         + "  float qc_sum_y = 0.0f;\n"
         + "  float qc_step = 1.256637f;\n"
         + "  for (int qc_k = 0; qc_k < 5; qc_k++) {\n"
         + "    float qc_angle = (float)qc_k * qc_step;\n"
         + "    float qc_proj = __x * cosf(qc_angle) + __y * sinf(qc_angle);\n"
         + "    float qc_wave = cosf(__quasicrystal_freq * qc_proj);\n"
         + "    qc_sum_x += qc_wave * cosf(qc_angle);\n"
         + "    qc_sum_y += qc_wave * sinf(qc_angle);\n"
         + "  }\n"
         + "  __px += __quasicrystal * (__x + __quasicrystal_amp * qc_sum_x * 0.2f);\n"
         + "  __py += __quasicrystal * (__y + __quasicrystal_amp * qc_sum_y * 0.2f);\n";
  }
}