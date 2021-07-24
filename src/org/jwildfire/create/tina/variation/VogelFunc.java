/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2021 Andreas Maschke

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

import org.jwildfire.base.mathlib.DoubleWrapperWF;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class VogelFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_N = "n";
  private static final String PARAM_SCALE = "scale";
  private static final String[] paramNames = {PARAM_N, PARAM_SCALE};

  private final double M_PHI = 1.61803398874989484820;
  private final double M_2PI_PHI2 = M_2PI / (M_PHI * M_PHI);

  private final DoubleWrapperWF sina = new DoubleWrapperWF();
  private final DoubleWrapperWF cosa = new DoubleWrapperWF();

  private int n = 20;
  private double scale = 1.;

  @Override
  public String getName() {
    return "vogel";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{n, scale};
  }

  @Override
  public void setParameter(final String pName, final double pValue) {
    if (PARAM_N.equalsIgnoreCase(pName)) {
      n = limitIntVal(Tools.FTOI(pValue), 1, Integer.MAX_VALUE);
    } else if (PARAM_SCALE.equalsIgnoreCase(pName)) {
      scale = pValue;
    } else {
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Vogel function by Victor Ganora
    final int i = pContext.random(n) + 1;
    final double a = i * M_2PI_PHI2;
    sinAndCos(a, sina, cosa);
    final double r = pAmount * (pAffineTP.getPrecalcSqrt() + sqrt(i));
    pVarTP.x += r * (cosa.value + (scale * pAffineTP.x));
    pVarTP.y += r * (sina.value + (scale * pAffineTP.y));
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float M_PHI = 1.61803398874989484820f;\n"
        + "float M_2PI_PHI2 = 2.0f * PI / (M_PHI * M_PHI);\n"
        + "int i = lroundf(__vogel_n* RANDFLOAT()) + 1;\n"
        + "float a = i * 2.0f*M_2PI_PHI2;\n"
        + "float sina, cosa;\n"
        + "sincosf(a, &sina, &cosa);\n"
        + "float r = __vogel * (__r + sqrtf(i));\n"
        + "__px += r * (cosa + (__vogel_scale * __x));\n"
        + "__py += r * (sina + (__vogel_scale * __y));\n"
        + (context.isPreserveZCoordinate() ? "__pz += __vogel * __z;\n" : "");
  }
}
