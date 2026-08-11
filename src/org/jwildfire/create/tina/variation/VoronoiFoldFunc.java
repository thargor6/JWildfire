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
import static org.jwildfire.base.mathlib.MathLib.sqrt;

public class VoronoiFoldFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_FOLD = "fold";
  private static final String[] paramNames = {PARAM_SCALE, PARAM_FOLD};

  private double scale = 1.0;
  private double fold = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double vf_scale = Math.max(fabs(this.scale), 0.01);
    double vf_cx = Math.floor(pAffineTP.x / vf_scale);
    double vf_cy = Math.floor(pAffineTP.y / vf_scale);
    
    double nearest_x = 0.0;
    double nearest_y = 0.0;
    double vf_min_d = 1e10;

    for (int di = -1; di <= 1; di++) {
      for (int dj = -1; dj <= 1; dj++) {
        double cell_x = vf_cx + (double)di;
        double cell_y = vf_cy + (double)dj;
        
        // Fixed: Explicitly mirrored the immutable dot-products to match the GPU exactly
        double vf_hx = sin(cell_x * 127.1 + cell_y * 311.7) * 43758.5453;
        vf_hx = vf_hx - Math.floor(vf_hx);

        double vf_hy = sin(cell_x * 269.5 + cell_y * 183.3) * 43758.5453;
        vf_hy = vf_hy - Math.floor(vf_hy);

        double site_x = (cell_x + vf_hx) * vf_scale;
        double site_y = (cell_y + vf_hy) * vf_scale;
        
        double dx = pAffineTP.x - site_x;
        double dy = pAffineTP.y - site_y;
        double vf_d = sqrt(dx * dx + dy * dy);

        if (vf_d < vf_min_d) {
          vf_min_d = vf_d;
          nearest_x = site_x;
          nearest_y = site_y;
        }
      }
    }

    pVarTP.x += pAmount * (nearest_x + (pAffineTP.x - nearest_x) * (1.0 - this.fold));
    pVarTP.y += pAmount * (nearest_y + (pAffineTP.y - nearest_y) * (1.0 - this.fold));
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale, fold}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_FOLD.equalsIgnoreCase(pName)) fold = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "voronoi_fold"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float vf_scale = fmaxf(fabsf(__voronoi_fold_scale), 0.01f);\n"
         + "  float vf_cx = floorf(__x / vf_scale);\n"
         + "  float vf_cy = floorf(__y / vf_scale);\n"
         + "  float nearest_x = 0.0f;\n"
         + "  float nearest_y = 0.0f;\n"
         + "  float vf_min_d = 1e10f;\n"
         + "  for (int di = -1; di <= 1; di++) {\n"
         + "    for (int dj = -1; dj <= 1; dj++) {\n"
         + "      float cell_x = vf_cx + (float)di;\n"
         + "      float cell_y = vf_cy + (float)dj;\n"
         + "      float vf_hx = sinf(cell_x * 127.1f + cell_y * 311.7f) * 43758.5453f;\n"
         + "      vf_hx = vf_hx - floorf(vf_hx);\n"
         + "      float vf_hy = sinf(cell_x * 269.5f + cell_y * 183.3f) * 43758.5453f;\n"
         + "      vf_hy = vf_hy - floorf(vf_hy);\n"
         + "      float site_x = (cell_x + vf_hx) * vf_scale;\n"
         + "      float site_y = (cell_y + vf_hy) * vf_scale;\n"
         + "      float dx = __x - site_x;\n"
         + "      float dy = __y - site_y;\n"
         + "      float vf_d = sqrtf(dx * dx + dy * dy);\n"
         + "      if (vf_d < vf_min_d) { vf_min_d = vf_d; nearest_x = site_x; nearest_y = site_y; }\n"
         + "    }\n"
         + "  }\n"
         + "  __px += __voronoi_fold * (nearest_x + (__x - nearest_x) * (1.0f - __voronoi_fold_fold));\n"
         + "  __py += __voronoi_fold * (nearest_y + (__y - nearest_y) * (1.0f - __voronoi_fold_fold));\n";
  }
}