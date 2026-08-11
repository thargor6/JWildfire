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

public class AfterimageFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_LEN = "len";
  private static final String PARAM_DECAY = "decay";

  private static final String[] paramNames = {PARAM_FREQ, PARAM_LEN, PARAM_DECAY};

  private double freq = 3.0;
  private double len = 0.4;
  private double decay = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double ai_decay = Math.max(fabs(this.decay), 0.01);
    double ai_smear = this.len * sin(this.freq * pAffineTP.y) * exp(-pAffineTP.y * pAffineTP.y * ai_decay);

    pVarTP.x += pAmount * (pAffineTP.x + ai_smear);
    pVarTP.y += pAmount * pAffineTP.y;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{freq, len, decay};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) {
      freq = pValue;
    } else if (PARAM_LEN.equalsIgnoreCase(pName)) {
      len = pValue;
    } else if (PARAM_DECAY.equalsIgnoreCase(pName)) {
      decay = pValue;
    } else {
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "afterimage";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // __afterimage represents variation amount
    // __afterimage_freq, __afterimage_len, __afterimage_decay represent parameters
    return "  float ai_decay = fmaxf(fabsf(__afterimage_decay), 0.01f);\n"
         + "  float ai_smear = __afterimage_len * sinf(__afterimage_freq * __y) * expf(-__y * __y * ai_decay);\n"
         + "  __px += __afterimage * (__x + ai_smear);\n"
         + "  __py += __afterimage * __y;\n";
  }
}