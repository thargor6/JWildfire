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
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class QuadrupoleFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_STRENGTH = "strength";
  private static final String PARAM_SMOOTH = "smooth";
  private static final String[] paramNames = {PARAM_STRENGTH, PARAM_SMOOTH};

  private double strength = 0.3;
  private double smooth = 0.1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r2 = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y;
    double qp_r2 = r2 + this.smooth;
    double qp_theta = atan2(pAffineTP.y, pAffineTP.x);
    double qp_field_r = this.strength * cos(2.0 * qp_theta) / qp_r2;
    double qp_field_t = this.strength * sin(2.0 * qp_theta) / qp_r2;
    double qp_r = sqrt(r2 + 1.0e-10);
    double qp_nr = qp_r + qp_field_r;
    double qp_nt = qp_theta + qp_field_t;

    pVarTP.x += pAmount * qp_nr * cos(qp_nt);
    pVarTP.y += pAmount * qp_nr * sin(qp_nt);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{strength, smooth}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_STRENGTH.equalsIgnoreCase(pName)) strength = pValue;
    else if (PARAM_SMOOTH.equalsIgnoreCase(pName)) smooth = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "quadrupole"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float qp_r2 = __x * __x + __y * __y + __quadrupole_smooth;\n"
         + "  float qp_theta = atan2f(__y, __x);\n"
         + "  float qp_field_r = __quadrupole_strength * cosf(2.0f * qp_theta) / qp_r2;\n"
         + "  float qp_field_t = __quadrupole_strength * sinf(2.0f * qp_theta) / qp_r2;\n"
         + "  float qp_r = sqrtf(__x * __x + __y * __y + 1.0e-10f);\n"
         + "  float qp_nr = qp_r + qp_field_r;\n"
         + "  float qp_nt = qp_theta + qp_field_t;\n"
         + "  __px += __quadrupole * qp_nr * cosf(qp_nt);\n"
         + "  __py += __quadrupole * qp_nr * sinf(qp_nt);\n";
  }
}