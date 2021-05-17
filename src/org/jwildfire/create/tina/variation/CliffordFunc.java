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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class CliffordFunc extends VariationFunc implements SupportsGPU {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";

  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_D};

  double a = -1.4;
  double b = 1.6;
  double c = 1.0;
  double d = 0.7;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Reference:
    //		  http://paulbourke.net/fractals/clifford/
    //		  xn+1 = sin(a yn) + c cos(a xn)
    //		  yn+1 = sin(b xn) + d cos(b yn)

    double x = sin(a * pAffineTP.y) + c * cos(a * pAffineTP.x);
    double y = sin(b * pAffineTP.x) + d * cos(b * pAffineTP.y);

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  public String getName() {
    return "clifford_js";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{a, b, c, d};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_A)) {
      a = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_B)) {
      b = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_C)) {
      c = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_D)) {
      d = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float x = sinf(varpar->clifford_js_a * __y) + varpar->clifford_js_c * cosf(varpar->clifford_js_a * __x);\n"
        + "float y = sinf(varpar->clifford_js_b * __x) + varpar->clifford_js_d * cosf(varpar->clifford_js_b * __y);\n"
        + "__px += x * varpar->clifford_js;\n"
        + "__py += y * varpar->clifford_js;\n"
        + (context.isPreserveZCoordinate() ? "__pz += varpar->clifford_js * __z;\n" : "");
  }
}
