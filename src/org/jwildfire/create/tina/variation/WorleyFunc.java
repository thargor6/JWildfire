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

public class WorleyFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_JITTER = "jitter";
  private static final String[] paramNames = {PARAM_SCALE, PARAM_JITTER};

  private double scale = 1.0;
  private double jitter = 0.8;

  private double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double wo_s = Math.max(fabs(this.scale), 0.01);
    double wo_j = clamp(this.jitter, 0.0, 1.0);
    double wo_cx = Math.floor(pAffineTP.x / wo_s);
    double wo_cy = Math.floor(pAffineTP.y / wo_s);
    
    double wo_min_d = 1e10;
    double wo_nx = 0.0;
    double wo_ny = 0.0;

    for (int di = -1; di <= 1; di++) {
      for (int dj = -1; dj <= 1; dj++) {
        double cell_x = wo_cx + (double)di;
        double cell_y = wo_cy + (double)dj;
        
        double dot1 = cell_x * 127.1 + cell_y * 311.7;
        double wo_hx = sin(dot1) * 43758.5453;
        wo_hx = wo_hx - Math.floor(wo_hx);

        double dot2 = cell_x * 269.5 + cell_y * 183.3;
        double wo_hy = sin(dot2) * 43758.5453;
        wo_hy = wo_hy - Math.floor(wo_hy);

        double site_x = (cell_x + wo_hx * wo_j + (1.0 - wo_j) * 0.5) * wo_s;
        double site_y = (cell_y + wo_hy * wo_j + (1.0 - wo_j) * 0.5) * wo_s;
        
        double dx = pAffineTP.x - site_x;
        double dy = pAffineTP.y - site_y;
        double wo_d = Math.sqrt(dx * dx + dy * dy);

        if (wo_d < wo_min_d) {
          wo_min_d = wo_d;
          wo_nx = site_x;
          wo_ny = site_y;
        }
      }
    }

    pVarTP.x += pAmount * ((pAffineTP.x - wo_nx) / (wo_s + 1e-6));
    pVarTP.y += pAmount * ((pAffineTP.y - wo_ny) / (wo_s + 1e-6));
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale, jitter}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_JITTER.equalsIgnoreCase(pName)) jitter = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "worley"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

@Override
  public String getGPUCode(FlameTransformationContext context) {
    // Flattened internal cell vector arrays and distance formulas to ensure 100% scalar compatibility across OpenCL environments
    return "  float wo_s = fmaxf(fabsf(__worley_scale), 0.01f);\n"
         + "  float wo_j = clamp(__worley_jitter, 0.0f, 1.0f);\n"
         + "  float wo_cx = floorf(__x / wo_s);\n"
         + "  float wo_cy = floorf(__y / wo_s);\n"
         + "  float wo_min_d = 1e10f;\n"
         + "  float wo_nx = 0.0f;\n"
         + "  float wo_ny = 0.0f;\n"
         + "  for (int di = -1; di <= 1; di++) {\n"
         + "    for (int dj = -1; dj <= 1; dj++) {\n"
         + "      float cell_x = wo_cx + (float)di;\n"
         + "      float cell_y = wo_cy + (float)dj;\n"
         + "      float wo_hx = sinf(cell_x * 127.1f + cell_y * 311.7f) * 43758.5453f;\n"
         + "      wo_hx = wo_hx - floorf(wo_hx);\n"
         + "      float wo_hy = sinf(cell_x * 269.5f + cell_y * 183.3f) * 43758.5453f;\n"
         + "      wo_hy = wo_hy - floorf(wo_hy);\n"
         + "      float site_x = (cell_x + wo_hx * wo_j + (1.0f - wo_j) * 0.5f) * wo_s;\n"
         + "      float site_y = (cell_y + wo_hy * wo_j + (1.0f - wo_j) * 0.5f) * wo_s;\n"
         + "      float dx = __x - site_x;\n"
         + "      float dy = __y - site_y;\n"
         + "      float wo_d = sqrtf(dx * dx + dy * dy);\n"
         + "      if (wo_d < wo_min_d) { wo_min_d = wo_d; wo_nx = site_x; wo_ny = site_y; }\n"
         + "    }\n"
         + "  }\n"
         + "  __px += __worley * ((__x - wo_nx) / (wo_s + 1e-6f));\n"
         + "  __py += __worley * ((__y - wo_ny) / (wo_s + 1e-6f));\n";
  }
}