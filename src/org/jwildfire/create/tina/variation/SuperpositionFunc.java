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
import static org.jwildfire.base.mathlib.MathLib.sin;

public class SuperpositionFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ1 = "freq1";
  private static final String PARAM_FREQ2 = "freq2";
  private static final String PARAM_PHASE = "phase";
  private static final String[] paramNames = {PARAM_FREQ1, PARAM_FREQ2, PARAM_PHASE};

  private double freq1 = 3.0;
  private double freq2 = 5.0;
  private double phase = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double sp_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double sp_wave = 0.5 * (sin(this.freq1 * sp_r) + sin(this.freq2 * sp_r + this.phase));

    pVarTP.x += pAmount * pAffineTP.x * sp_wave;
    pVarTP.y += pAmount * pAffineTP.y * sp_wave;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq1, freq2, phase}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ1.equalsIgnoreCase(pName)) freq1 = pValue;
    else if (PARAM_FREQ2.equalsIgnoreCase(pName)) freq2 = pValue;
    else if (PARAM_PHASE.equalsIgnoreCase(pName)) phase = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "superposition"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float sp_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float sp_wave = 0.5f * (sinf(__superposition_freq1 * sp_r) + sinf(__superposition_freq2 * sp_r + __superposition_phase));\n"
         + "  __px += __superposition * __x * sp_wave;\n"
         + "  __py += __superposition * __y * sp_wave;\n";
  }
}