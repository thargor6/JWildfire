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

public class TentacleFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_AMP};

  private double freq = 2.0;
  private double amp = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double te_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double te_theta = atan2(pAffineTP.y, pAffineTP.x);
    double te_wave = this.amp * sin(this.freq * te_r);
    double te_new_theta = te_theta + te_wave;

    pVarTP.x += pAmount * te_r * cos(te_new_theta);
    pVarTP.y += pAmount * te_r * sin(te_new_theta);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, amp}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "tentacle"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float te_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float te_theta = atan2f(__y, __x);\n"
         + "  float te_wave = __tentacle_amp * sinf(__tentacle_freq * te_r);\n"
         + "  float te_new_theta = te_theta + te_wave;\n"
         + "  __px += __tentacle * te_r * cosf(te_new_theta);\n"
         + "  __py += __tentacle * te_r * sinf(te_new_theta);\n";
  }
}