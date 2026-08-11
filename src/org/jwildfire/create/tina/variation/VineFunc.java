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
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class VineFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_DENSITY = "density";
  private static final String PARAM_SCALE = "scale";
  private static final String[] paramNames = {PARAM_DENSITY, PARAM_SCALE};

  private double density = 1.0;
  private double scale = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double vn_density = Math.max(fabs(this.density), 0.01);
    double vn_scale = Math.max(fabs(this.scale), 0.01);
    double vn_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double vn_theta = atan2(pAffineTP.y, pAffineTP.x);
    double vn_n = vn_r * vn_density;
    double vn_spiral = vn_theta + vn_n * 2.39996323;
    double vn_new_r = sqrt(vn_n) * vn_scale;

    pVarTP.x += pAmount * vn_new_r * cos(vn_spiral);
    pVarTP.y += pAmount * vn_new_r * sin(vn_spiral);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{density, scale}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_DENSITY.equalsIgnoreCase(pName)) density = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "vine"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float vn_density = fmaxf(fabsf(__vine_density), 0.01f);\n"
         + "  float vn_scale = fmaxf(fabsf(__vine_scale), 0.01f);\n"
         + "  float vn_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float vn_theta = atan2f(__y, __x);\n"
         + "  float vn_n = vn_r * vn_density;\n"
         + "  float vn_spiral = vn_theta + vn_n * 2.39996323f;\n"
         + "  float vn_new_r = sqrtf(vn_n) * vn_scale;\n"
         + "  __px += __vine * vn_new_r * cosf(vn_spiral);\n"
         + "  __py += __vine * vn_new_r * sinf(vn_spiral);\n";
  }
}