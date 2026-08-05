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

public class KarmanVortexFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_STRENGTH = "strength";
  private static final String PARAM_SEP = "sep";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_STRENGTH, PARAM_SEP};

  private double freq = 2.0;
  private double strength = 0.3;
  private double sep = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double kv_freq = Math.max(fabs(this.freq), 0.01);
    double kv_xi = Math.round(pAffineTP.x * kv_freq);
    
    double kv_parity = kv_xi % 2.0;
    if (kv_parity < 0) kv_parity += 2.0;

    double kv_vcx = kv_xi / kv_freq;
    double kv_vcy = this.sep * 0.5 * (2.0 * kv_parity - 1.0);
    double kv_dx = pAffineTP.x - kv_vcx;
    double kv_dy = pAffineTP.y - kv_vcy;
    double kv_r2 = kv_dx * kv_dx + kv_dy * kv_dy + 1e-4;
    double kv_spin = this.strength * (2.0 * kv_parity - 1.0) / kv_r2;

    pVarTP.x += pAmount * (pAffineTP.x + kv_spin * (-kv_dy));
    pVarTP.y += pAmount * (pAffineTP.y + kv_spin * kv_dx);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, strength, sep}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_STRENGTH.equalsIgnoreCase(pName)) strength = pValue;
    else if (PARAM_SEP.equalsIgnoreCase(pName)) sep = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "karman_vortex"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float kv_freq = fmaxf(fabsf(__karman_vortex_freq), 0.01f);\n"
         + "  float kv_xi = roundf(__x * kv_freq);\n"
         + "  float kv_parity = fmod(kv_xi, 2.0f);\n"
         + "  if (kv_parity < 0.0f) kv_parity += 2.0f;\n"
         + "  float kv_vcx = kv_xi / kv_freq;\n"
         + "  float kv_vcy = __karman_vortex_sep * 0.5f * (2.0f * kv_parity - 1.0f);\n"
         + "  float kv_dx = __x - kv_vcx;\n"
         + "  float kv_dy = __y - kv_vcy;\n"
         + "  float kv_r2 = kv_dx * kv_dx + kv_dy * kv_dy + 1e-4f;\n"
         + "  float kv_spin = __karman_vortex_strength * (2.0f * kv_parity - 1.0f) / kv_r2;\n"
         + "  __px += __karman_vortex * (__x + kv_spin * (-kv_dy));\n"
         + "  __py += __karman_vortex * (__y + kv_spin * kv_dx);\n";
  }
}