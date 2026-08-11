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
import static org.jwildfire.base.mathlib.MathLib.sqrt;

public class LagrangeFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SEPARATION = "separation";
  private static final String PARAM_STRENGTH = "strength";
  private static final String[] paramNames = {PARAM_SEPARATION, PARAM_STRENGTH};

  private double separation = 0.5;
  private double strength = 0.2;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double lg_sep = Math.max(fabs(this.separation), 0.01);
    double lg_m1x = -lg_sep * 0.5;
    double lg_m2x = lg_sep * 0.5;
    double lg_dx1 = pAffineTP.x - lg_m1x;
    double lg_dx2 = pAffineTP.x - lg_m2x;

    double lg_r1 = sqrt(lg_dx1 * lg_dx1 + pAffineTP.y * pAffineTP.y) + 0.1;
    double lg_r2 = sqrt(lg_dx2 * lg_dx2 + pAffineTP.y * pAffineTP.y) + 0.1;

    double lg_px = -this.strength * (lg_dx1 / (lg_r1 * lg_r1 * lg_r1) + lg_dx2 / (lg_r2 * lg_r2 * lg_r2));
    double lg_py = -this.strength * (pAffineTP.y / (lg_r1 * lg_r1 * lg_r1) + pAffineTP.y / (lg_r2 * lg_r2 * lg_r2));

    pVarTP.x += pAmount * (pAffineTP.x + lg_px);
    pVarTP.y += pAmount * (pAffineTP.y + lg_py);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{separation, strength}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SEPARATION.equalsIgnoreCase(pName)) separation = pValue;
    else if (PARAM_STRENGTH.equalsIgnoreCase(pName)) strength = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "lagrange"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float lg_sep = fmaxf(fabsf(__lagrange_separation), 0.01f);\n"
         + "  float lg_m1x = -lg_sep * 0.5f;\n"
         + "  float lg_m2x = lg_sep * 0.5f;\n"
         + "  float lg_dx1 = __x - lg_m1x;\n"
         + "  float lg_dx2 = __x - lg_m2x;\n"
         + "  float lg_r1 = sqrtf(lg_dx1 * lg_dx1 + __y * __y) + 0.1f;\n"
         + "  float lg_r2 = sqrtf(lg_dx2 * lg_dx2 + __y * __y) + 0.1f;\n"
         + "  float lg_px = -__lagrange_strength * (lg_dx1 / (lg_r1 * lg_r1 * lg_r1) + lg_dx2 / (lg_r2 * lg_r2 * lg_r2));\n"
         + "  float lg_py = -__lagrange_strength * (__y / (lg_r1 * lg_r1 * lg_r1) + __y / (lg_r2 * lg_r2 * lg_r2));\n"
         + "  __px += __lagrange * (__x + lg_px);\n"
         + "  __py += __lagrange * (__y + lg_py);\n";
  }
}