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

public class BwrapsFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_CELLSCALE = "cellscale";
  private static final String[] paramNames = {PARAM_SCALE, PARAM_CELLSCALE};

  private double scale = 0.5;
  private double cellscale = 0.9;

  private double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double bw_scale = Math.max(fabs(this.scale), 0.01);
    double bw_cs = clamp(this.cellscale, 0.1, 1.0);
    
    double bw_cx = Math.round(pAffineTP.x / bw_scale) * bw_scale;
    double bw_cy = Math.round(pAffineTP.y / bw_scale) * bw_scale;
    double bw_dx = pAffineTP.x - bw_cx;
    double bw_dy = pAffineTP.y - bw_cy;
    
    double bw_r2 = bw_dx * bw_dx + bw_dy * bw_dy + 1e-6;
    double bw_br2 = bw_scale * bw_scale * bw_cs * bw_cs * 0.25;
    double bw_inv = bw_br2 / bw_r2;

    pVarTP.x += pAmount * (bw_cx + bw_dx * bw_inv);
    pVarTP.y += pAmount * (bw_cy + bw_dy * bw_inv);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale, cellscale}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_CELLSCALE.equalsIgnoreCase(pName)) cellscale = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "bwraps"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float bw_scale = fmaxf(fabsf(__bwraps_scale), 0.01f);\n"
         + "  float bw_cs = clamp(__bwraps_cellscale, 0.1f, 1.0f);\n"
         + "  float bw_cx = roundf(__x / bw_scale) * bw_scale;\n"
         + "  float bw_cy = roundf(__y / bw_scale) * bw_scale;\n"
         + "  float bw_dx = __x - bw_cx;\n"
         + "  float bw_dy = __y - bw_cy;\n"
         + "  float bw_r2 = bw_dx * bw_dx + bw_dy * bw_dy + 1e-6f;\n"
         + "  float bw_br2 = bw_scale * bw_scale * bw_cs * bw_cs * 0.25f;\n"
         + "  float bw_inv = bw_br2 / bw_r2;\n"
         + "  __px += __bwraps * (bw_cx + bw_dx * bw_inv);\n"
         + "  __py += __bwraps * (bw_cy + bw_dy * bw_inv);\n";
  }
}