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

public class WavesPhaseFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE_X = "scale_x";
  private static final String PARAM_SCALE_Y = "scale_y";
  private static final String PARAM_FREQ_X = "freq_x";
  private static final String PARAM_FREQ_Y = "freq_y";
  private static final String PARAM_PHASE_X = "phase_x";
  private static final String PARAM_PHASE_Y = "phase_y";
  private static final String[] paramNames = {PARAM_SCALE_X, PARAM_SCALE_Y, PARAM_FREQ_X, PARAM_FREQ_Y, PARAM_PHASE_X, PARAM_PHASE_Y};

  private double scale_x = 0.25;
  private double scale_y = 0.25;
  private double freq_x = 2.0;
  private double freq_y = 2.0;
  private double phase_x = 0.0;
  private double phase_y = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double safe_sx2 = (fabs(this.scale_x) < 1e-4) ? ((this.scale_x + 1e-5 < 0.0 ? -1.0 : 1.0) * 1e-4) : this.scale_x * this.scale_x;
    double safe_sy2 = (fabs(this.scale_y) < 1e-4) ? ((this.scale_y + 1e-5 < 0.0 ? -1.0 : 1.0) * 1e-4) : this.scale_y * this.scale_y;

    pVarTP.x += pAmount * (pAffineTP.x + this.scale_x * sin(pAffineTP.y * this.freq_x / safe_sx2 + this.phase_x));
    pVarTP.y += pAmount * (pAffineTP.y + this.scale_y * sin(pAffineTP.x * this.freq_y / safe_sy2 + this.phase_y));
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale_x, scale_y, freq_x, freq_y, phase_x, phase_y}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE_X.equalsIgnoreCase(pName)) scale_x = pValue;
    else if (PARAM_SCALE_Y.equalsIgnoreCase(pName)) scale_y = pValue;
    else if (PARAM_FREQ_X.equalsIgnoreCase(pName)) freq_x = pValue;
    else if (PARAM_FREQ_Y.equalsIgnoreCase(pName)) freq_y = pValue;
    else if (PARAM_PHASE_X.equalsIgnoreCase(pName)) phase_x = pValue;
    else if (PARAM_PHASE_Y.equalsIgnoreCase(pName)) phase_y = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "waves_phase"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // Replaced nested ternary logic with direct branching blocks to avoid driver crashes
    return "  float safe_sx2 = __waves_phase_scale_x * __waves_phase_scale_x;\n"
         + "  if (fabsf(__waves_phase_scale_x) < 1e-4f) {\n"
         + "    float sign_val = (__waves_phase_scale_x + 1e-5f < 0.0f) ? -1.0f : 1.0f;\n"
         + "    safe_sx2 = sign_val * 1e-4f;\n"
         + "  }\n"
         + "  float safe_sy2 = __waves_phase_scale_y * __waves_phase_scale_y;\n"
         + "  if (fabsf(__waves_phase_scale_y) < 1e-4f) {\n"
         + "    float sign_val = (__waves_phase_scale_y + 1e-5f < 0.0f) ? -1.0f : 1.0f;\n"
         + "    safe_sy2 = sign_val * 1e-4f;\n"
         + "  }\n"
         + "  __px += __waves_phase * (__x + __waves_phase_scale_x * sinf(__y * __waves_phase_freq_x / safe_sx2 + __waves_phase_phase_x));\n"
         + "  __py += __waves_phase * (__y + __waves_phase_scale_y * sinf(__x * __waves_phase_freq_y / safe_sy2 + __waves_phase_phase_y));\n";
  }
}