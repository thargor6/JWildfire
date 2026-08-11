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

import static org.jwildfire.base.mathlib.MathLib.sin;

public class EmbroideryFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_DEPTH = "depth";
  private static final String[] paramNames = {PARAM_SCALE, PARAM_DEPTH};

  private double scale = 3.0;
  private double depth = 0.2;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double emb_cx = pAffineTP.x * this.scale;
    double emb_cy = pAffineTP.y * this.scale;
    double emb_row = Math.floor(emb_cy);
    double emb_col = Math.floor(emb_cx);
    
    double row_col_mod = (emb_row + emb_col) % 2.0;
    if (row_col_mod < 0) row_col_mod += 2.0;
    double emb_phase = row_col_mod * Math.PI;

    double emb_wx = sin(emb_cx * 6.283185307179586 + emb_phase) * this.depth;
    double emb_wy = sin(emb_cy * 6.283185307179586 + emb_phase) * this.depth;

    pVarTP.x += pAmount * (pAffineTP.x + emb_wx / this.scale);
    pVarTP.y += pAmount * (pAffineTP.y + emb_wy / this.scale);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale, depth}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_DEPTH.equalsIgnoreCase(pName)) depth = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "embroidery"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float emb_cx = __x * __embroidery_scale;\n"
         + "  float emb_cy = __y * __embroidery_scale;\n"
         + "  float emb_row = floorf(emb_cy);\n"
         + "  float emb_col = floorf(emb_cx);\n"
         + "  float emb_phase = fmod(emb_row + emb_col, 2.0f) * 3.14159265f;\n"
         + "  if (emb_phase < 0.0f) emb_phase += 2.0f * 3.14159265f;\n"
         + "  float emb_wx = sinf(emb_cx * 6.2831853f + emb_phase) * __embroidery_depth;\n"
         + "  float emb_wy = sinf(emb_cy * 6.2831853f + emb_phase) * __embroidery_depth;\n"
         + "  __px += __embroidery * (__x + emb_wx / __embroidery_scale);\n"
         + "  __py += __embroidery * (__y + emb_wy / __embroidery_scale);\n";
  }
}