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

public class OuroborosFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_TWIST = "twist";
  private static final String[] paramNames = {PARAM_RADIUS, PARAM_TWIST};

  private double radius = 0.5;
  private double twist = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double or_radius = Math.max(fabs(this.radius), 0.01);
    double or_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double or_theta = atan2(pAffineTP.y, pAffineTP.x);
    
    double or_wrapped_r = or_radius * ((or_r / or_radius) - Math.floor(or_r / or_radius));
    double or_nt = or_theta + this.twist * or_r;

    pVarTP.x += pAmount * or_wrapped_r * cos(or_nt);
    pVarTP.y += pAmount * or_wrapped_r * sin(or_nt);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{radius, twist}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName)) radius = pValue;
    else if (PARAM_TWIST.equalsIgnoreCase(pName)) twist = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "ouroboros"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float or_radius = fmaxf(fabsf(__ouroboros_radius), 0.01f);\n"
         + "  float or_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float or_theta = atan2f(__y, __x);\n"
         + "  float or_wrapped_r = or_radius * (__x / or_radius - floorf(__x / or_radius));\n" // normalized coordinate fract mapping
         + "  float or_nt = or_theta + __ouroboros_twist * or_r;\n"
         + "  __px += __ouroboros * or_wrapped_r * cosf(or_nt);\n"
         + "  __py += __ouroboros * or_wrapped_r * sinf(or_nt);\n";
  }
}