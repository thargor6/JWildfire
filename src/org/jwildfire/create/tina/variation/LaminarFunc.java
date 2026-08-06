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

public class LaminarFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SHEAR = "shear";
  private static final String PARAM_AMP = "amp";
  private static final String PARAM_FREQ = "freq";
  private static final String[] paramNames = {PARAM_SHEAR, PARAM_AMP, PARAM_FREQ};

  private double shear = 0.5;
  private double amp = 0.1;
  private double freq = 2.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double lm_flow = this.shear * (1.0 - pAffineTP.y * pAffineTP.y);

    pVarTP.x += pAmount * (lm_flow + this.amp * sin(this.freq * pAffineTP.y));
    pVarTP.y += pAmount * pAffineTP.y;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{shear, amp, freq}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SHEAR.equalsIgnoreCase(pName)) shear = pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "laminar"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float lm_flow = __laminar_shear * (1.0f - __y * __y);\n"
         + "  __px += __laminar * (lm_flow + __laminar_amp * sinf(__laminar_freq * __y));\n"
         + "  __py += __laminar * __y;\n";
  }
}