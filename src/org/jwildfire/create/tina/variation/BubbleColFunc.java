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
import static org.jwildfire.base.mathlib.MathLib.sin;

public class BubbleColFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String PARAM_RISE = "rise";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_AMP, PARAM_RISE};

  private double freq = 4.0;
  private double amp = 0.3;
  private double rise = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double bc_freq = Math.max(fabs(this.freq), 0.01);
    double bc_col = Math.round(pAffineTP.x * bc_freq) / bc_freq;
    double bc_phase = bc_col * 2.39996323;
    double bc_sway = this.amp * sin(bc_freq * pAffineTP.y + bc_phase);

    pVarTP.x += pAmount * (bc_col + bc_sway);
    pVarTP.y += pAmount * (pAffineTP.y + this.rise / (bc_freq + 1e-4));
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, amp, rise}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else if (PARAM_RISE.equalsIgnoreCase(pName)) rise = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "bubble_col"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float bc_freq = fmaxf(fabsf(__bubble_col_freq), 0.01f);\n"
         + "  float bc_col = roundf(__x * bc_freq) / bc_freq;\n"
         + "  float bc_phase = bc_col * 2.39996323f;\n"
         + "  float bc_sway = __bubble_col_amp * sinf(bc_freq * __y + bc_phase);\n"
         + "  __px += __bubble_col * (bc_col + bc_sway);\n"
         + "  __py += __bubble_col * (__y + __bubble_col_rise / (bc_freq + 1e-4f));\n";
  }
}