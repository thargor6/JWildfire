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
import static org.jwildfire.base.mathlib.MathLib.cos;

public class BedheadFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String[] paramNames = {PARAM_A, PARAM_B};

  private double a = -0.81;
  private double b = -0.92;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Replaced sgn() with standard inline conditional to ensure compilation
    double b_safe = (fabs(this.b) < 0.001) ? ((this.b + 0.00001 < 0) ? -1.0 : 1.0) * 0.001 : this.b;
    double nx = sin(pAffineTP.x * pAffineTP.y / b_safe) * pAffineTP.y + cos(this.a * pAffineTP.x - pAffineTP.y);
    double ny = pAffineTP.x + sin(pAffineTP.y) / b_safe;

    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{a, b}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName)) a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName)) b = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "bedhead"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float b_safe = (fabsf(__bedhead_b) < 0.001f) ? (__bedhead_b + 0.00001f < 0.f ? -1.f : 1.f) * 0.001f : __bedhead_b;\n"
         + "  float nx = sinf(__x * __y / b_safe) * __y + cosf(__bedhead_a * __x - __y);\n"
         + "  float ny = __x + sinf(__y) / b_safe;\n"
         + "  __px += __bedhead * nx;\n"
         + "  __py += __bedhead * ny;\n";
  }
}