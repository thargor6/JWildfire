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

public class WoodGrainFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String PARAM_GRAIN = "grain";
  private static final String PARAM_GRAIN_FREQ = "grain_freq";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_AMP, PARAM_GRAIN, PARAM_GRAIN_FREQ};

  private double freq = 5.0;
  private double amp = 0.2;
  private double grain = 0.5;
  private double grain_freq = 6.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double wg_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double wg_theta = atan2(pAffineTP.y, pAffineTP.x);
    double wg_ring = sin(this.freq * wg_r + this.grain * cos(this.grain_freq * wg_theta));
    double wg_disp = this.amp * wg_ring;

    pVarTP.x += pAmount * (pAffineTP.x + wg_disp * cos(wg_theta));
    pVarTP.y += pAmount * (pAffineTP.y + wg_disp * sin(wg_theta));
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, amp, grain, grain_freq}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else if (PARAM_GRAIN.equalsIgnoreCase(pName)) grain = pValue;
    else if (PARAM_GRAIN_FREQ.equalsIgnoreCase(pName)) grain_freq = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "wood_grain"; }
  
  @Override
  public void randomize() {
  	freq = Math.random() * 15.0;
  	amp = Math.random() * 10.0 - 5.0;
  	grain = Math.random() * 15.0;
  	grain_freq = Math.random() * 30.0;
  	if (Math.random() < 0.5) {
  		grain_freq = Math.round(grain_freq);
  	}
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float wg_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float wg_theta = atan2f(__y, __x);\n"
         + "  float wg_ring = sinf(__wood_grain_freq * wg_r + __wood_grain_grain * cosf(__wood_grain_grain_freq * wg_theta));\n"
         + "  float wg_disp = __wood_grain_amp * wg_ring;\n"
         + "  __px += __wood_grain * (__x + wg_disp * cosf(wg_theta));\n"
         + "  __py += __wood_grain * (__y + wg_disp * sinf(wg_theta));\n";
  }
}