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

public class RocheFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SEP = "sep";
  private static final String PARAM_MASS = "mass";
  private static final String[] paramNames = {PARAM_SEP, PARAM_MASS};

  private double sep = 0.5;
  private double mass = 0.5;

  private double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double ro_sep = Math.max(fabs(this.sep), 0.01);
    double ro_mass = clamp(this.mass, 0.01, 0.99);
    double ro_mx = ro_sep * 0.5;
    double ro_dx1 = pAffineTP.x - ro_mx;
    double ro_dx2 = pAffineTP.x + ro_mx;
    double ro_r1 = Math.max(sqrt(ro_dx1 * ro_dx1 + pAffineTP.y * pAffineTP.y), 0.01);
    double ro_r2 = Math.max(sqrt(ro_dx2 * ro_dx2 + pAffineTP.y * pAffineTP.y), 0.01);
    double ro_r1_3 = ro_r1 * ro_r1 * ro_r1;
    double ro_r2_3 = ro_r2 * ro_r2 * ro_r2;
    double ro_px = -ro_mass * ro_dx1 / ro_r1_3 - (1.0 - ro_mass) * ro_dx2 / ro_r2_3;
    double ro_py = -ro_mass * pAffineTP.y / ro_r1_3 - (1.0 - ro_mass) * pAffineTP.y / ro_r2_3;

    pVarTP.x += pAmount * (pAffineTP.x - ro_py * 0.5);
    pVarTP.y += pAmount * (pAffineTP.y + ro_px * 0.5);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{sep, mass}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SEP.equalsIgnoreCase(pName)) sep = pValue;
    else if (PARAM_MASS.equalsIgnoreCase(pName)) mass = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "roche"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float ro_sep = fmaxf(fabsf(__roche_sep), 0.01f);\n"
         + "  float ro_mass = clamp(__roche_mass, 0.01f, 0.99f);\n"
         + "  float ro_mx = ro_sep * 0.5f;\n"
         + "  float ro_dx1 = __x - ro_mx;\n"
         + "  float ro_dx2 = __x + ro_mx;\n"
         + "  float ro_r1 = fmaxf(sqrtf(ro_dx1 * ro_dx1 + __y * __y), 0.01f);\n"
         + "  float ro_r2 = fmaxf(sqrtf(ro_dx2 * ro_dx2 + __y * __y), 0.01f);\n"
         + "  float ro_r1_3 = ro_r1 * ro_r1 * ro_r1;\n"
         + "  float ro_r2_3 = ro_r2 * ro_r2 * ro_r2;\n"
         + "  float ro_px = -ro_mass * ro_dx1 / ro_r1_3 - (1.0f - ro_mass) * ro_dx2 / ro_r2_3;\n"
         + "  float ro_py = -ro_mass * __y / ro_r1_3 - (1.0f - ro_mass) * __y / ro_r2_3;\n"
         + "  __px += __roche * (__x - ro_py * 0.5f);\n"
         + "  __py += __roche * (__y + ro_px * 0.5f);\n";
  }
}