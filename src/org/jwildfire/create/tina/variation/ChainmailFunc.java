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

import static org.jwildfire.base.mathlib.MathLib.sqrt;

public class ChainmailFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_RING_RATIO = "ring_ratio";
  private static final String[] paramNames = {PARAM_SCALE, PARAM_RING_RATIO};

  private double scale = 3.0;
  private double ring_ratio = 0.35;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double cm_cx = pAffineTP.x * this.scale;
    double cm_cy = pAffineTP.y * this.scale;
    double cm_row = Math.floor(cm_cy);
    
    double cm_row_mod = cm_row % 2.0;
    if (cm_row_mod < 0) cm_row_mod += 2.0;
    double cm_offset = cm_row_mod < 0.5 ? 0.5 : 0.0;

    double cm_lx = (cm_cx + cm_offset) - Math.floor(cm_cx + cm_offset) - 0.5;
    double cm_ly = cm_cy - Math.floor(cm_cy) - 0.5;
    double cm_r = sqrt(cm_lx * cm_lx + cm_ly * cm_ly);
    double cm_s = cm_r < this.ring_ratio ? this.ring_ratio / (cm_r + 1e-6) : 1.0;

    pVarTP.x += pAmount * (cm_lx * cm_s) / this.scale;
    pVarTP.y += pAmount * (cm_ly * cm_s) / this.scale;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale, ring_ratio}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_RING_RATIO.equalsIgnoreCase(pName)) ring_ratio = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "chainmail"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float cm_cx = __x * __chainmail_scale;\n"
         + "  float cm_cy = __y * __chainmail_scale;\n"
         + "  float cm_row = floorf(cm_cy);\n"
         + "  float cm_offset = fmod(cm_row, 2.0f) < 0.5f ? 0.5f : 0.0f;\n"
         + "  if (cm_offset < 0.0f) cm_offset += 2.0f;\n"
         + "  float cm_lx = (cm_cx + cm_offset) - floorf(cm_cx + cm_offset) - 0.5f;\n"
         + "  float cm_ly = cm_cy - floorf(cm_cy) - 0.5f;\n"
         + "  float cm_r = sqrtf(cm_lx * cm_lx + cm_ly * cm_ly);\n"
         + "  float cm_s = cm_r < __chainmail_ring_ratio ? __chainmail_ring_ratio / (cm_r + 1e-6f) : 1.0f;\n"
         + "  __px += __chainmail * (cm_lx * cm_s) / __chainmail_scale;\n"
         + "  __py += __chainmail * (cm_ly * cm_s) / __chainmail_scale;\n";
  }
}