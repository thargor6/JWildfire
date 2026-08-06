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

public class PenroseFoldFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String[] paramNames = {PARAM_SCALE};

  private double scale = 2.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double pf_phi = 1.618033988749895;
    double pf_x = pAffineTP.x * this.scale;
    double pf_y = pAffineTP.y * this.scale;
    double pf_u = pf_x + pf_phi * pf_y;
    double pf_v = pf_y - pf_x / pf_phi;
    double pf_fu = pf_u - Math.round(pf_u);
    double pf_fv = pf_v - Math.round(pf_v);
    double pf_nx = (pf_fu - pf_fv / pf_phi) / this.scale;
    double pf_ny = (pf_fv + pf_fu / pf_phi) / this.scale;

    pVarTP.x += pAmount * pf_nx;
    pVarTP.y += pAmount * pf_ny;
    
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
  public String getName() { return "penrose_fold"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float pf_phi = 1.618034f;\n"
         + "  float pf_x = __x * __penrose_fold_scale;\n"
         + "  float pf_y = __y * __penrose_fold_scale;\n"
         + "  float pf_u = pf_x + pf_phi * pf_y;\n"
         + "  float pf_v = pf_y - pf_x / pf_phi;\n"
         + "  float pf_fu = pf_u - roundf(pf_u);\n"
         + "  float pf_fv = pf_v - roundf(pf_v);\n"
         + "  float pf_nx = (pf_fu - pf_fv / pf_phi) / __penrose_fold_scale;\n"
         + "  float pf_ny = (pf_fv + pf_fu / pf_phi) / __penrose_fold_scale;\n"
         + "  __px += __penrose_fold * pf_nx;\n"
         + "  __py += __penrose_fold * pf_ny;\n";
  }
}