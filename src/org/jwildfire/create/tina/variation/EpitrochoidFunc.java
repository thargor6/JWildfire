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
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class EpitrochoidFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_R_BIG = "r_big";
  private static final String PARAM_R_SMALL = "r_small";
  private static final String PARAM_D = "d";
  private static final String[] paramNames = {PARAM_R_BIG, PARAM_R_SMALL, PARAM_D};

  private double r_big = 3.0;
  private double r_small = 1.0;
  private double d = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double ep_r = Math.max(fabs(this.r_small), 1e-4);
    double theta = atan2(pAffineTP.y, pAffineTP.x);
    double ratio = (this.r_big + ep_r) / ep_r;

    pVarTP.x += pAmount * ((this.r_big + ep_r) * cos(theta) - this.d * cos(ratio * theta));
    pVarTP.y += pAmount * ((this.r_big + ep_r) * sin(theta) - this.d * sin(ratio * theta));
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{r_big, r_small, d}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_R_BIG.equalsIgnoreCase(pName)) r_big = pValue;
    else if (PARAM_R_SMALL.equalsIgnoreCase(pName)) r_small = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName)) d = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "epitrochoid"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float ep_r = fmaxf(fabsf(__epitrochoid_r_small), 1e-4f);\n"
         + "  float theta = atan2f(__y, __x);\n"
         + "  float ratio = (__epitrochoid_r_big + ep_r) / ep_r;\n"
         + "  __px += __epitrochoid * ((__epitrochoid_r_big + ep_r) * cosf(theta) - __epitrochoid_d * cosf(ratio * theta));\n"
         + "  __py += __epitrochoid * ((__epitrochoid_r_big + ep_r) * sinf(theta) - __epitrochoid_d * sinf(ratio * theta));\n";
  }
}