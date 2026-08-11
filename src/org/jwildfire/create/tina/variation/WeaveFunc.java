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

public class WeaveFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_WARP = "warp";
  private static final String[] paramNames = {PARAM_SCALE, PARAM_WARP};

  private double scale = 0.5;
  private double warp = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double wv_scale = Math.max(fabs(this.scale), 0.01);
    double wv_col = Math.floor(pAffineTP.x / wv_scale);
    double wv_row = Math.floor(pAffineTP.y / wv_scale);
    double wv_lx = pAffineTP.x / wv_scale - wv_col - 0.5;
    double wv_ly = pAffineTP.y / wv_scale - wv_row - 0.5;
    
    double col_row_mod = (wv_col + wv_row) % 2.0;
    if (col_row_mod < 0.0) col_row_mod += 2.0;
    double wv_over = 2.0 * col_row_mod - 1.0;

    double wv_dx = this.warp * wv_over * sin(Math.PI * wv_ly);
    double wv_dy = this.warp * (-wv_over) * sin(Math.PI * wv_lx);

    pVarTP.x += pAmount * (pAffineTP.x + wv_dx * wv_scale);
    pVarTP.y += pAmount * (pAffineTP.y + wv_dy * wv_scale);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale, warp}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_WARP.equalsIgnoreCase(pName)) warp = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "weave"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float wv_scale = fmaxf(fabsf(__weave_scale), 0.01f);\n"
         + "  float wv_col = floorf(__x / wv_scale);\n"
         + "  float wv_row = floorf(__y / wv_scale);\n"
         + "  float wv_lx = __x / wv_scale - wv_col - 0.5f;\n"
         + "  float wv_ly = __y / wv_scale - wv_row - 0.5f;\n"
         + "  float col_row_mod = fmod(wv_col + wv_row, 2.0f);\n"
         + "  if (col_row_mod < 0.0f) col_row_mod += 2.0f;\n"
         + "  float wv_over = 2.0f * col_row_mod - 1.0f;\n"
         + "  float wv_dx = __weave_warp * wv_over * sinf(3.14159265f * wv_ly);\n"
         + "  float wv_dy = __weave_warp * (-wv_over) * sinf(3.14159265f * wv_lx);\n"
         + "  __px += __weave * (__x + wv_dx * wv_scale);\n"
         + "  __py += __weave * (__y + wv_dy * wv_scale);\n";
  }
}