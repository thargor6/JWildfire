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
import static org.jwildfire.base.mathlib.MathLib.exp;

public class CollisionFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_FORCE = "force";
  private static final String[] paramNames = {PARAM_RADIUS, PARAM_FORCE};

  private double radius = 0.3;
  private double force = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double cl_radius = Math.max(fabs(this.radius), 0.01);
    double cl_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double cl_safe = Math.max(cl_r, 0.0001);
    double cl_force_val = this.force * exp(-cl_r * cl_r / (cl_radius * cl_radius));

    pVarTP.x += pAmount * (pAffineTP.x + cl_force_val * pAffineTP.x / cl_safe);
    pVarTP.y += pAmount * (pAffineTP.y + cl_force_val * pAffineTP.y / cl_safe);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{radius, force}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName)) radius = pValue;
    else if (PARAM_FORCE.equalsIgnoreCase(pName)) force = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "collision"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float cl_radius = fmaxf(fabsf(__collision_radius), 0.01f);\n"
         + "  float cl_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float cl_safe = fmaxf(cl_r, 0.0001f);\n"
         + "  float cl_force_val = __collision_force * expf(-cl_r * cl_r / (cl_radius * cl_radius));\n"
         + "  __px += __collision * (__x + cl_force_val * __x / cl_safe);\n"
         + "  __py += __collision * (__y + cl_force_val * __y / cl_safe);\n";
  }
}