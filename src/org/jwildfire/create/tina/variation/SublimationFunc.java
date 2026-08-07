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
import static org.jwildfire.base.mathlib.MathLib.exp;

public class SublimationFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_LIFT = "lift";
  private static final String PARAM_SPREAD = "spread";
  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String[] paramNames = {PARAM_LIFT, PARAM_SPREAD, PARAM_FREQ, PARAM_AMP};

  private double lift = 0.3;
  private double spread = 1.0;
  private double freq = 3.0;
  private double amp = 0.1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double sub_rise = exp(-fabs(pAffineTP.x) * this.spread) * this.lift;
    double sub_turb = this.amp * sin(this.freq * pAffineTP.y);

    pVarTP.x += pAmount * (pAffineTP.x + sub_turb);
    pVarTP.y += pAmount * (pAffineTP.y + sub_rise);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{lift, spread, freq, amp}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_LIFT.equalsIgnoreCase(pName)) lift = pValue;
    else if (PARAM_SPREAD.equalsIgnoreCase(pName)) spread = pValue;
    else if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "sublimation"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float sub_rise = expf(-fabsf(__x) * __sublimation_spread) * __sublimation_lift;\n"
         + "  float sub_turb = __sublimation_amp * sinf(__sublimation_freq * __y);\n"
         + "  __px += __sublimation * (__x + sub_turb);\n"
         + "  __py += __sublimation * (__y + sub_rise);\n";
  }
}