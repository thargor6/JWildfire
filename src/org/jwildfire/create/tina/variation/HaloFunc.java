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
import static org.jwildfire.base.mathlib.MathLib.sqrt;
import static org.jwildfire.base.mathlib.MathLib.exp;

public class HaloFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_STRENGTH = "strength";
  private static final String[] paramNames = {PARAM_RADIUS, PARAM_WIDTH, PARAM_STRENGTH};

  private double radius = 0.5;
  private double width = 0.2;
  private double strength = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double hl_rad = Math.max(fabs(this.radius), 0.01);
    double hl_w = Math.max(fabs(this.width), 0.01);
    double hl_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double hl_ring = hl_r - hl_rad;
    double hl_env = exp(-(hl_ring * hl_ring) / (hl_w * hl_w));
    double hl_scale = 1.0 + this.strength * hl_env / (hl_r + 1e-6);

    pVarTP.x += pAmount * pAffineTP.x * hl_scale;
    pVarTP.y += pAmount * pAffineTP.y * hl_scale;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{radius, width, strength}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName)) radius = pValue;
    else if (PARAM_WIDTH.equalsIgnoreCase(pName)) width = pValue;
    else if (PARAM_STRENGTH.equalsIgnoreCase(pName)) strength = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "halo"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float hl_rad = fmaxf(fabsf(__halo_radius), 0.01f);\n"
         + "  float hl_w = fmaxf(fabsf(__halo_width), 0.01f);\n"
         + "  float hl_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float hl_ring = hl_r - hl_rad;\n"
         + "  float hl_env = expf(-(hl_ring * hl_ring) / (hl_w * hl_w));\n"
         + "  float hl_scale = 1.0f + __halo_strength * hl_env / (hl_r + 1e-6f);\n"
         + "  __px += __halo * __x * hl_scale;\n"
         + "  __py += __halo * __y * hl_scale;\n";
  }
}