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
import static org.jwildfire.base.mathlib.MathLib.exp;

public class AuroraFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_LAT = "lat";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_AMP, PARAM_WIDTH, PARAM_LAT};

  private double freq = 4.0;
  private double amp = 0.5;
  private double width = 0.3;
  private double lat = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double au_width = Math.max(fabs(this.width), 0.01);
    double au_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double au_theta = atan2(pAffineTP.y, pAffineTP.x);
    double au_ov = 1.0 + this.lat * cos(au_theta);
    double au_band = au_r - au_ov;
    double au_env = exp(-au_band * au_band / (au_width * au_width));
    double au_wave = this.amp * sin(this.freq * au_theta) * au_env;

    pVarTP.x += pAmount * (pAffineTP.x + au_wave * cos(au_theta));
    pVarTP.y += pAmount * (pAffineTP.y + au_wave * sin(au_theta));

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, amp, width, lat}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else if (PARAM_WIDTH.equalsIgnoreCase(pName)) width = pValue;
    else if (PARAM_LAT.equalsIgnoreCase(pName)) lat = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "aurora"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float au_width = fmaxf(fabsf(__aurora_width), 0.01f);\n"
         + "  float au_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float au_theta = atan2f(__y, __x);\n"
         + "  float au_ov = 1.0f + __aurora_lat * cosf(au_theta);\n"
         + "  float au_band = au_r - au_ov;\n"
         + "  float au_env = expf(-au_band * au_band / (au_width * au_width));\n"
         + "  float au_wave = __aurora_amp * sinf(__aurora_freq * au_theta) * au_env;\n"
         + "  __px += __aurora * (__x + au_wave * cosf(au_theta));\n"
         + "  __py += __aurora * (__y + au_wave * sinf(au_theta));\n";
  }
}