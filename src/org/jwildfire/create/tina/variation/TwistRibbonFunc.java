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

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class TwistRibbonFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_WIDTH = "width";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_WIDTH};

  private double freq = 3.0;
  private double width = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double tw_twist = this.freq * pAffineTP.x;
    double tw_c = cos(tw_twist);
    double tw_s = sin(tw_twist);

    pVarTP.x += pAmount * (tw_c * pAffineTP.x - tw_s * pAffineTP.y * this.width);
    pVarTP.y += pAmount * (tw_s * pAffineTP.x + tw_c * pAffineTP.y * this.width);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, width}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_WIDTH.equalsIgnoreCase(pName)) width = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "twist_ribbon"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float tw_twist = __twist_ribbon_freq * __x;\n"
         + "  float tw_c = cosf(tw_twist);\n"
         + "  float tw_s = sinf(tw_twist);\n"
         + "  __px += __twist_ribbon * (tw_c * __x - tw_s * __y * __twist_ribbon_width);\n"
         + "  __py += __twist_ribbon * (tw_s * __x + tw_c * __y * __twist_ribbon_width);\n";
  }
}