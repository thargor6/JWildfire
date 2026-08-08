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
import static org.jwildfire.base.mathlib.MathLib.cos;

public class TurbulenceFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_AMP};

  private double freq = 2.0;
  private double amp = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double tb_f = this.freq;
    double tb_a = this.amp;
    
    double dx = tb_a * (sin(tb_f * pAffineTP.x) * cos(tb_f * 0.7 * pAffineTP.y) + 0.5 * sin(2.0 * tb_f * pAffineTP.x) * cos(1.4 * tb_f * pAffineTP.y));
    double dy = tb_a * (cos(0.8 * tb_f * pAffineTP.x) * sin(tb_f * pAffineTP.y) + 0.5 * cos(1.6 * tb_f * pAffineTP.x) * sin(2.0 * tb_f * pAffineTP.y));

    pVarTP.x += pAmount * (pAffineTP.x + dx);
    pVarTP.y += pAmount * (pAffineTP.y + dy);
    
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
  public String getName() { return "turbulence"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float tb_f = __turbulence_freq;\n"
         + "  float tb_a = __turbulence_amp;\n"
         + "  float dx = tb_a * (sinf(tb_f * __x) * cosf(tb_f * 0.7f * __y) + 0.5f * sinf(2.0f * tb_f * __x) * cosf(1.4f * tb_f * __y));\n"
         + "  float dy = tb_a * (cosf(0.8f * tb_f * __x) * sinf(tb_f * __y) + 0.5f * cosf(1.6f * tb_f * __x) * sinf(2.0f * tb_f * __y));\n"
         + "  __px += __turbulence * (__x + dx);\n"
         + "  __py += __turbulence * (__y + dy);\n";
  }
}