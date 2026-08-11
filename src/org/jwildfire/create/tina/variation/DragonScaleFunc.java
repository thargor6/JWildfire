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

public class DragonScaleFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_RELIEF = "relief";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_RELIEF};

  private double freq = 3.0;
  private double relief = 0.4;

  private double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double ds_freq = Math.max(fabs(this.freq), 0.01);
    double ds_cx = Math.round(pAffineTP.x * ds_freq) / ds_freq;
    double ds_cy = Math.round(pAffineTP.y * ds_freq) / ds_freq;
    double ds_dx = pAffineTP.x - ds_cx;
    double ds_dy = pAffineTP.y - ds_cy;
    double ds_r2 = ds_dx * ds_dx + ds_dy * ds_dy;
    double ds_cell_r = 0.5 / ds_freq;
    double ds_bump = clamp(this.relief * (1.0 - ds_r2 / (ds_cell_r * ds_cell_r + 1e-6)), 0.0, 1.0);

    pVarTP.x += pAmount * (pAffineTP.x + ds_bump * ds_dx);
    pVarTP.y += pAmount * (pAffineTP.y + ds_bump * ds_dy);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, relief}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_RELIEF.equalsIgnoreCase(pName)) relief = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "dragon_scale"; }
  
  @Override
  public void randomize() {
  	freq = Math.random() * 10.0;
  	relief = Math.random() * 10.0 - 5.0;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float ds_freq = fmaxf(fabsf(__dragon_scale_freq), 0.01f);\n"
         + "  float ds_cx = roundf(__x * ds_freq) / ds_freq;\n"
         + "  float ds_cy = roundf(__y * ds_freq) / ds_freq;\n"
         + "  float ds_dx = __x - ds_cx;\n"
         + "  float ds_dy = __y - ds_cy;\n"
         + "  float ds_r2 = ds_dx * ds_dx + ds_dy * ds_dy;\n"
         + "  float ds_cell_r = 0.5f / ds_freq;\n"
         + "  float ds_bump = clamp(__dragon_scale_relief * (1.0f - ds_r2 / (ds_cell_r * ds_cell_r + 1e-6f)), 0.0f, 1.0f);\n"
         + "  __px += __dragon_scale * (__x + ds_bump * ds_dx);\n"
         + "  __py += __dragon_scale * (__y + ds_bump * ds_dy);\n";
  }
}