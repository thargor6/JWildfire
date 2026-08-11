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

import static org.jwildfire.base.mathlib.MathLib.sin;

public class LiquefactionFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String PARAM_PHASE = "phase";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_AMP, PARAM_PHASE};

  private double freq = 3.0;
  private double amp = 0.3;
  private double phase = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double liq_dx = this.amp * sin(this.freq * pAffineTP.y + this.phase);
    double liq_dy = this.amp * sin(this.freq * pAffineTP.x + this.phase);

    pVarTP.x += pAmount * (pAffineTP.x + liq_dx);
    pVarTP.y += pAmount * (pAffineTP.y + liq_dy);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, amp, phase}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else if (PARAM_PHASE.equalsIgnoreCase(pName)) phase = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "liquefaction"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float liq_dx = __liquefaction_amp * sinf(__liquefaction_freq * __y + __liquefaction_phase);\n"
         + "  float liq_dy = __liquefaction_amp * sinf(__liquefaction_freq * __x + __liquefaction_phase);\n"
         + "  __px += __liquefaction * (__x + liq_dx);\n"
         + "  __py += __liquefaction * (__y + liq_dy);\n";
  }
}