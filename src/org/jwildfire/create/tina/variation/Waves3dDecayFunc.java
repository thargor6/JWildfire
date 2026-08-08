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
import static org.jwildfire.base.mathlib.MathLib.exp;

public class Waves3dDecayFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_DECAY = "decay";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_DECAY};

  private double freq = 3.0;
  private double decay = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double wd_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double wd_theta = atan2(pAffineTP.y, pAffineTP.x);
    double wd_nr = wd_r + sin(wd_r * this.freq) * exp(-wd_r * this.decay);

    pVarTP.x += pAmount * wd_nr * cos(wd_theta);
    pVarTP.y += pAmount * wd_nr * sin(wd_theta);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, decay}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_DECAY.equalsIgnoreCase(pName)) decay = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "waves3d_decay"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float wd_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float wd_theta = atan2f(__y, __x);\n"
         + "  float wd_nr = wd_r + sinf(wd_r * __waves3d_decay_freq) * expf(-wd_r * __waves3d_decay_decay);\n"
         + "  __px += __waves3d_decay * wd_nr * cosf(wd_theta);\n"
         + "  __py += __waves3d_decay * wd_nr * sinf(wd_theta);\n";
  }
}