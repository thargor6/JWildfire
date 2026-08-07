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
import static org.jwildfire.base.mathlib.MathLib.exp;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class SchwarzChristoffelFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String[] paramNames = {PARAM_SCALE};

  private double scale = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double sc_s = Math.max(fabs(this.scale), 0.01);
    double sc_ex = exp(Math.PI * pAffineTP.x / sc_s);

    pVarTP.x += pAmount * sc_ex * cos(Math.PI * pAffineTP.y / sc_s);
    pVarTP.y += pAmount * sc_ex * sin(Math.PI * pAffineTP.y / sc_s);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "schwarz_christoffel"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float sc_s = fmaxf(fabsf(__schwarz_christoffel_scale), 0.01f);\n"
         + "  float sc_ex = expf(3.14159265f * __x / sc_s);\n"
         + "  __px += __schwarz_christoffel * sc_ex * cosf(3.14159265f * __y / sc_s);\n"
         + "  __py += __schwarz_christoffel * sc_ex * sinf(3.14159265f * __y / sc_s);\n";
  }
}