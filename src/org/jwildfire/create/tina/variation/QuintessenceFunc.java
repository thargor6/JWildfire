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

import static org.jwildfire.base.mathlib.MathLib.pow;
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class QuintessenceFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_POWER = "power";
  private static final String[] paramNames = {PARAM_SCALE, PARAM_POWER};

  private double scale = 1.0;
  private double power = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double qe_r2 = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y;
    double qe_r = pow(qe_r2 + 1e-10, this.power * 0.5);
    double qe_a = atan2(pAffineTP.y, pAffineTP.x) * 5.0;

    pVarTP.x += pAmount * qe_r * cos(qe_a) * this.scale;
    pVarTP.y += pAmount * qe_r * sin(qe_a) * this.scale;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale, power}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_POWER.equalsIgnoreCase(pName)) power = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "quintessence"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float qe_r2 = __x * __x + __y * __y;\n"
         + "  float qe_r = powf(qe_r2 + 1e-10f, __quintessence_power * 0.5f);\n"
         + "  float qe_a = atan2f(__y, __x) * 5.0f;\n"
         + "  __px += __quintessence * qe_r * cosf(qe_a) * __quintessence_scale;\n"
         + "  __py += __quintessence * qe_r * sinf(qe_a) * __quintessence_scale;\n";
  }
}