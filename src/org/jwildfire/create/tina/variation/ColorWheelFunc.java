/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2026 Andreas Maschke free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
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
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.cos;

public class ColorWheelFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SECTORS = "sectors";
  private static final String PARAM_SPEED = "speed";
  private static final String[] paramNames = {PARAM_SECTORS, PARAM_SPEED};

  private double sectors = 6.0;
  private double speed = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r2 = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y;
    double cw_r = sqrt(r2 + 1.0e-10);
    double cw_theta = atan2(pAffineTP.y, pAffineTP.x);
    double cw_new_theta = cw_theta + this.speed * sin(this.sectors * cw_theta);

    pVarTP.x += pAmount * cw_r * cos(cw_new_theta);
    pVarTP.y += pAmount * cw_r * sin(cw_new_theta);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{sectors, speed}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SECTORS.equalsIgnoreCase(pName)) sectors = pValue;
    else if (PARAM_SPEED.equalsIgnoreCase(pName)) speed = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "color_wheel"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float cw_r = sqrtf(__x * __x + __y * __y + 1.0e-10f);\n"
         + "  float cw_theta = atan2f(__y, __x);\n"
         + "  float cw_new_theta = cw_theta + __color_wheel_speed * sinf(__color_wheel_sectors * cw_theta);\n"
         + "  __px += __color_wheel * cw_r * cosf(cw_new_theta);\n"
         + "  __py += __color_wheel * cw_r * sinf(cw_new_theta);\n";
  }
}