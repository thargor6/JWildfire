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
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class KleinFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_INNER_RADIUS = "inner_radius";
  private static final String PARAM_TWIST = "twist";
  private static final String[] paramNames = {PARAM_INNER_RADIUS, PARAM_TWIST};

  private double inner_radius = 0.5;
  private double twist = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double kl_ri = Math.max(fabs(this.inner_radius), 0.01);
    double kl_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double kl_theta = atan2(pAffineTP.y, pAffineTP.x);
    double kl_nr, kl_nt;

    if (kl_r < kl_ri) {
      kl_nr = kl_r;
      kl_nt = kl_theta + this.twist * kl_r;
    } else {
      kl_nr = (kl_ri * kl_ri) / Math.max(kl_r, 1e-6);
      kl_nt = -kl_theta + this.twist * kl_r;
    }

    pVarTP.x += pAmount * kl_nr * cos(kl_nt);
    pVarTP.y += pAmount * kl_nr * sin(kl_nt);

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{inner_radius, twist}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_INNER_RADIUS.equalsIgnoreCase(pName)) inner_radius = pValue;
    else if (PARAM_TWIST.equalsIgnoreCase(pName)) twist = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "klein"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float kl_ri = fmaxf(fabsf(__klein_inner_radius), 0.01f);\n"
         + "  float kl_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float kl_theta = atan2f(__y, __x);\n"
         + "  float kl_nr, kl_nt;\n"
         + "  if (kl_r < kl_ri) {\n"
         + "    kl_nr = kl_r;\n"
         + "    kl_nt = kl_theta + __klein_twist * kl_r;\n"
         + "  } else {\n"
         + "    kl_nr = (kl_ri * kl_ri) / fmaxf(kl_r, 1e-6f);\n"
         + "    kl_nt = -kl_theta + __klein_twist * kl_r;\n"
         + "  }\n"
         + "  __px += __klein * kl_nr * cosf(kl_nt);\n"
         + "  __py += __klein * kl_nr * sinf(kl_nt);\n";
  }
}