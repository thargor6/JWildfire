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

public class SatinFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_SHEEN = "sheen";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_SHEEN};

  private double freq = 4.0;
  private double sheen = 0.2;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double sa_freq = Math.max(fabs(this.freq), 0.01);
    
    // Diagnostic diagonal projections
    double sa_diag1 = (pAffineTP.x + pAffineTP.y) * 0.7071067811865476;
    double sa_diag2 = (pAffineTP.x - pAffineTP.y) * 0.7071067811865476;
    
    double sa_dx = this.sheen * sin(sa_freq * sa_diag1);
    double sa_dy = this.sheen * cos(sa_freq * sa_diag2);

    // Accumulates onto the existing transform space
    pVarTP.x += pAmount * (pAffineTP.x + sa_dx);
    pVarTP.y += pAmount * (pAffineTP.y + sa_dy);

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, sheen}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_SHEEN.equalsIgnoreCase(pName)) sheen = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "satin"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // Uses additive modification (+=) to accurately align with the CPU transformation depth
    return "  float sa_freq = fmaxf(fabsf(__satin_freq), 0.01f);\n"
         + "  float sa_diag1 = (__x + __y) * 0.70710678f;\n"
         + "  float sa_diag2 = (__x - __y) * 0.70710678f;\n"
         + "  float sa_dx = __satin_sheen * sinf(sa_freq * sa_diag1);\n"
         + "  float sa_dy = __satin_sheen * cosf(sa_freq * sa_diag2);\n"
         + "  __px += __satin * (__x + sa_dx);\n"
         + "  __py += __satin * (__y + sa_dy);\n";
  }
}