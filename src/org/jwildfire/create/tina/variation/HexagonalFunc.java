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

public class HexagonalFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String[] paramNames = {PARAM_SCALE};

  private double scale = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double hx_scale = Math.max(fabs(this.scale), 0.0001);
    double SQRT3 = 1.732050807568877;

    double q = (2.0 / 3.0) * pAffineTP.x / hx_scale;
    // Fixed: Properly grouped the numerators so the entire transformation is uniformly scaled
    double r_hex = ((-1.0 / 3.0) * pAffineTP.x + (1.0 / SQRT3) * pAffineTP.y) / hx_scale;
    double s_hex = -q - r_hex;

    double rq = Math.floor(q + 0.5);
    double rr = Math.floor(r_hex + 0.5);
    double rs = Math.floor(s_hex + 0.5);

    double dq = fabs(rq - q);
    double dr = fabs(rr - r_hex);
    double ds = fabs(rs - s_hex);

    if (dq > dr && dq > ds) rq = -rr - rs;
    else if (dr > ds) rr = -rq - rs;

    double cx = hx_scale * 1.5 * rq;
    double cy = hx_scale * SQRT3 * (rr + rq * 0.5);

    pVarTP.x += pAmount * (pAffineTP.x - cx);
    pVarTP.y += pAmount * (pAffineTP.y - cy);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "hexagonal"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float hx_scale = fmaxf(fabsf(__hexagonal_scale), 0.0001f);\n"
         + "  float q = (2.0f / 3.0f) * __x / hx_scale;\n"
         + "  float r_hex = ((-1.0f / 3.0f) * __x + (1.0f / 1.7320508f) * __y) / hx_scale;\n"
         + "  float s_hex = -q - r_hex;\n"
         + "  float rq = floorf(q + 0.5f);\n"
         + "  float rr = floorf(r_hex + 0.5f);\n"
         + "  float rs = floorf(s_hex + 0.5f);\n"
         + "  float dq = fabsf(rq - q);\n"
         + "  float dr = fabsf(rr - r_hex);\n"
         + "  float ds = fabsf(rs - s_hex);\n"
         + "  if (dq > dr && dq > ds) { rq = -rr - rs; }\n"
         + "  else if (dr > ds) { rr = -rq - rs; }\n"
         + "  float cx = hx_scale * 1.5f * rq;\n"
         + "  float cy = hx_scale * 1.7320508f * (rr + rq * 0.5f);\n"
         + "  __px += __hexagonal * (__x - cx);\n"
         + "  __py += __hexagonal * (__y - cy);\n";
  }
}