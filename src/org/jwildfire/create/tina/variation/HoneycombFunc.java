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

public class HoneycombFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_PULL = "pull";
  private static final String[] paramNames = {PARAM_SCALE, PARAM_PULL};

  private double scale = 1.0;
  private double pull = 0.7;

  private double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double hc_s = Math.max(fabs(this.scale), 0.01);
    double hc_p = clamp(this.pull, 0.0, 1.0);

    double hc_q = (2.0 / 3.0) * pAffineTP.x / hc_s;
    double hc_r2 = (-1.0 / 3.0) * pAffineTP.x / hc_s + 0.5773502691896257 * pAffineTP.y / hc_s;

    double hc_rq = Math.round(hc_q);
    double hc_rr = Math.round(hc_r2);
    double hc_rs = Math.round(-hc_q - hc_r2);

    double hc_dq = fabs(hc_rq - hc_q);
    double hc_dr = fabs(hc_rr - hc_r2);
    double hc_ds = fabs(hc_rs - (-hc_q - hc_r2));

    if (hc_dq > hc_dr && hc_dq > hc_ds) hc_rq = -hc_rr - hc_rs;
    else if (hc_dr > hc_ds) hc_rr = -hc_rq - hc_rs;

    double hc_cx = hc_s * (hc_rq + 0.5 * hc_rr);
    double hc_cy = hc_s * 0.8660254037844386 * hc_rr;

    pVarTP.x += pAmount * (hc_cx + (pAffineTP.x - hc_cx) * hc_p);
    pVarTP.y += pAmount * (hc_cy + (pAffineTP.y - hc_cy) * hc_p);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale, pull}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_PULL.equalsIgnoreCase(pName)) pull = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "honeycomb"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float hc_s = fmaxf(fabsf(__honeycomb_scale), 0.01f);\n"
         + "  float hc_p = clamp(__honeycomb_pull, 0.0f, 1.0f);\n"
         + "  float hc_q = (2.0f / 3.0f) * __x / hc_s;\n"
         + "  float hc_r2 = (-1.0f / 3.0f) * __x / hc_s + 0.57735027f * __y / hc_s;\n"
         + "  float hc_rq = roundf(hc_q);\n"
         + "  float hc_rr = roundf(hc_r2);\n"
         + "  float hc_rs = roundf(-hc_q - hc_r2);\n"
         + "  float hc_dq = fabsf(hc_rq - hc_q);\n"
         + "  float hc_dr = fabsf(hc_rr - hc_r2);\n"
         + "  float hc_ds = fabsf(hc_rs - (-hc_q - hc_r2));\n"
         + "  if (hc_dq > hc_dr && hc_dq > hc_ds) { hc_rq = -hc_rr - hc_rs; }\n"
         + "  else if (hc_dr > hc_ds) { hc_rr = -hc_rq - hc_rs; }\n"
         + "  float hc_cx = hc_s * (hc_rq + 0.5f * hc_rr);\n"
         + "  float hc_cy = hc_s * 0.86602540f * hc_rr;\n"
         + "  __px += __honeycomb * (hc_cx + (__x - hc_cx) * hc_p);\n"
         + "  __py += __honeycomb * (hc_cy + (__y - hc_cy) * hc_p);\n";
  }
}