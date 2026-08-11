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

public class LabyrinthFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String[] paramNames = {PARAM_SCALE};

  private double scale = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double lb_scale = Math.max(fabs(this.scale), 0.01);
    double lb_ri = Math.floor(pAffineTP.x / lb_scale + 0.5);
    double lb_ci = Math.floor(pAffineTP.y / lb_scale + 0.5);
    double lb_lx = pAffineTP.x - lb_ri * lb_scale;
    double lb_ly = pAffineTP.y - lb_ci * lb_scale;

    double row_mod = fabs(lb_ci) % 2.0;
    double col_mod = fabs(lb_ri) % 2.0;

    double lb_fx = row_mod > 0.5 ? -lb_lx : lb_lx;
    double lb_fy = col_mod > 0.5 ? -lb_ly : lb_ly;

    pVarTP.x += pAmount * (lb_ri * lb_scale + lb_fx);
    pVarTP.y += pAmount * (lb_ci * lb_scale + lb_fy);
    
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
  public String getName() { return "labyrinth"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float lb_scale = fmaxf(fabsf(__labyrinth_scale), 0.01f);\n"
         + "  float lb_ri = floorf(__x / lb_scale + 0.5f);\n"
         + "  float lb_ci = floorf(__y / lb_scale + 0.5f);\n"
         + "  float lb_lx = __x - lb_ri * lb_scale;\n"
         + "  float lb_ly = __y - lb_ci * lb_scale;\n"
         + "  float lb_fx = (fmod(fabsf(lb_ci), 2.0f) > 0.5f) ? -lb_lx : lb_lx;\n"
         + "  float lb_fy = (fmod(fabsf(lb_ri), 2.0f) > 0.5f) ? -lb_ly : lb_ly;\n"
         + "  __px += __labyrinth * (lb_ri * lb_scale + lb_fx);\n"
         + "  __py += __labyrinth * (lb_ci * lb_scale + lb_fy);\n";
  }
}