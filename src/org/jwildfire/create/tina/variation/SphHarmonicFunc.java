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

import static org.jwildfire.base.mathlib.MathLib.sqrt;
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class SphHarmonicFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_L = "l";
  private static final String PARAM_M = "m";
  private static final String PARAM_STRENGTH = "strength";
  private static final String[] paramNames = {PARAM_L, PARAM_M, PARAM_STRENGTH};

  private int l = 2;
  private int m = 1;
  private double strength = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double sh_theta = atan2(pAffineTP.y, pAffineTP.x);
    double sh_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double sh_harm = cos((double) this.l * sh_theta) * cos((double) this.m * sh_theta);
    double sh_nr = sh_r * (1.0 + this.strength * sh_harm);

    pVarTP.x += pAmount * sh_nr * cos(sh_theta);
    pVarTP.y += pAmount * sh_nr * sin(sh_theta);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{l, m, strength}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_L.equalsIgnoreCase(pName)) l = (int) pValue;
    else if (PARAM_M.equalsIgnoreCase(pName)) m = (int) pValue;
    else if (PARAM_STRENGTH.equalsIgnoreCase(pName)) strength = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "sph_harmonic"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float sh_theta = atan2f(__y, __x);\n"
         + "  float sh_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float sh_harm = cosf((float)__sph_harmonic_l * sh_theta) * cosf((float)__sph_harmonic_m * sh_theta);\n"
         + "  float sh_nr = sh_r * (1.0f + __sph_harmonic_strength * sh_harm);\n"
         + "  __px += __sph_harmonic * sh_nr * cosf(sh_theta);\n"
         + "  __py += __sph_harmonic * sh_nr * sinf(sh_theta);\n";
  }
}