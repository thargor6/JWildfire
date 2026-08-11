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
import static org.jwildfire.base.mathlib.MathLib.sin;

public class TweedFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_AMP};

  private double freq = 3.0;
  private double amp = 0.2;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double tw_d1 = ((pAffineTP.x + pAffineTP.y) * this.freq) - Math.floor((pAffineTP.x + pAffineTP.y) * this.freq);
    double tw_d2 = ((pAffineTP.x - pAffineTP.y) * this.freq) - Math.floor((pAffineTP.x - pAffineTP.y) * this.freq);
    double tw_p = sin(tw_d1 * Math.PI);
    double tw_c = sin(tw_d2 * Math.PI);
    
    double sign_tw_c = (tw_c == 0.0) ? 0.0 : (tw_c < 0.0 ? -1.0 : 1.0);
    double sign_tw_p = (tw_p == 0.0) ? 0.0 : (tw_p < 0.0 ? -1.0 : 1.0);

    pVarTP.x += pAmount * (pAffineTP.x + this.amp * tw_p * sign_tw_c);
    pVarTP.y += pAmount * (pAffineTP.y - this.amp * tw_c * sign_tw_p);
    
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
  public String getName() { return "tweed"; }
  
  @Override
  public void randomize() {
  	freq = Math.random() * 5.0;
  	if (Math.random() < 0.6) {
  		amp = Math.random() * 0.5 - 0.25;
  	}
  	else {
  		amp = Math.random() * 4.0 - 2.0;
  	}
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float tw_d1 = (__x + __y) * __tweed_freq - floorf((__x + __y) * __tweed_freq);\n"
         + "  float tw_d2 = (__x - __y) * __tweed_freq - floorf((__x - __y) * __tweed_freq);\n"
         + "  float tw_p = sinf(tw_d1 * 3.14159265f);\n"
         + "  float tw_c = sinf(tw_d2 * 3.14159265f);\n"
         + "  float sign_tw_c = (tw_c == 0.0f) ? 0.0f : (tw_c < 0.0f ? -1.0f : 1.0f);\n"
         + "  float sign_tw_p = (tw_p == 0.0f) ? 0.0f : (tw_p < 0.0f ? -1.0f : 1.0f);\n"
         + "  __px += __tweed * (__x + __tweed_amp * tw_p * sign_tw_c);\n"
         + "  __py += __tweed * (__y - __tweed_amp * tw_c * sign_tw_p);\n";
  }
}