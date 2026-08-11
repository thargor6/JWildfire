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

public class AmoebaFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_LOBES = "lobes";
  private static final String PARAM_AMP = "amp";
  private static final String[] paramNames = {PARAM_LOBES, PARAM_AMP};

  private double lobes = 4.0;
  private double amp = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double am_n = Math.max(fabs(this.lobes), 2.0);
    double am_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double am_theta = atan2(pAffineTP.y, pAffineTP.x);
    double am_new_r = am_r * (1.0 + this.amp * cos(am_n * am_theta) + 0.4 * this.amp * sin((am_n + 1.0) * am_theta + 0.5));

    pVarTP.x += pAmount * am_new_r * cos(am_theta);
    pVarTP.y += pAmount * am_new_r * sin(am_theta);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{lobes, amp}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_LOBES.equalsIgnoreCase(pName)) lobes = pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "amoeba"; }
  
  @Override
  public void randomize() {
  	lobes = Math.random() * 13.0 + 2.0;
  	if (Math.random() < 0.25) {
  		lobes = Math.round(lobes);
  	}
  	amp = Math.random() * Math.random() - 0.5;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float am_n = fmaxf(fabsf(__amoeba_lobes), 2.0f);\n"
         + "  float am_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float am_theta = atan2f(__y, __x);\n"
         + "  float am_new_r = am_r * (1.0f + __amoeba_amp * cosf(am_n * am_theta) + 0.4f * __amoeba_amp * sinf((am_n + 1.0f) * am_theta + 0.5f));\n"
         + "  __px += __amoeba * am_new_r * cosf(am_theta);\n"
         + "  __py += __amoeba * am_new_r * sinf(am_theta);\n";
  }
}