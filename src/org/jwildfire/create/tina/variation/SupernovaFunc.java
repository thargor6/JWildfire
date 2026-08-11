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
import static org.jwildfire.base.mathlib.MathLib.exp;

public class SupernovaFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_BOOST = "boost";
  private static final String PARAM_SPIN = "spin";
  private static final String[] paramNames = {PARAM_RADIUS, PARAM_BOOST, PARAM_SPIN};

  private double radius = 0.5;
  private double boost = 2.0;
  private double spin = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double sn_radius = Math.max(fabs(this.radius), 0.01);
    double sn_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double sn_theta = atan2(pAffineTP.y, pAffineTP.x);
    double sn_ring = sn_r - sn_radius;
    double sn_env = exp(-sn_ring * sn_ring / (sn_radius * sn_radius * 0.1 + 1e-6));
    double sn_new_r = sn_r + sn_env * this.boost * sn_r;
    double sn_new_theta = sn_theta + this.spin * sn_env;

    pVarTP.x += pAmount * sn_new_r * cos(sn_new_theta);
    pVarTP.y += pAmount * sn_new_r * sin(sn_new_theta);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{radius, boost, spin}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName)) radius = pValue;
    else if (PARAM_BOOST.equalsIgnoreCase(pName)) boost = pValue;
    else if (PARAM_SPIN.equalsIgnoreCase(pName)) spin = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "supernova"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float sn_radius = fmaxf(fabsf(__supernova_radius), 0.01f);\n"
         + "  float sn_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float sn_theta = atan2f(__y, __x);\n"
         + "  float sn_ring = sn_r - sn_radius;\n"
         + "  float sn_env = expf(-sn_ring * sn_ring / (sn_radius * sn_radius * 0.1f + 1e-6f));\n"
         + "  float sn_new_r = sn_r + sn_env * __supernova_boost * sn_r;\n"
         + "  float sn_new_theta = sn_theta + __supernova_spin * sn_env;\n"
         + "  __px += __supernova * sn_new_r * cosf(sn_new_theta);\n"
         + "  __py += __supernova * sn_new_r * sinf(sn_new_theta);\n";
  }
}