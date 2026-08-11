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

public class RewindFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SPEED = "speed";
  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_DECAY = "decay";
  private static final String[] paramNames = {PARAM_SPEED, PARAM_RADIUS, PARAM_DECAY};

  private double speed = 1.0;
  private double radius = 0.5;
  private double decay = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r2 = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y;
    double rew_r = sqrt(r2 + 1.0e-10);
    double rew_theta = atan2(pAffineTP.y, pAffineTP.x);
    double rew_turn = this.speed / (rew_r + this.radius);
    double rew_nr = rew_r * (1.0 - this.decay * 0.05);
    double rew_nt = rew_theta + rew_turn;

    pVarTP.x += pAmount * rew_nr * cos(rew_nt);
    pVarTP.y += pAmount * rew_nr * sin(rew_nt);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{speed, radius, decay}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SPEED.equalsIgnoreCase(pName)) speed = pValue;
    else if (PARAM_RADIUS.equalsIgnoreCase(pName)) radius = pValue;
    else if (PARAM_DECAY.equalsIgnoreCase(pName)) decay = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "rewind"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float rew_r = sqrtf(__x * __x + __y * __y + 1.0e-10f);\n"
         + "  float rew_theta = atan2f(__y, __x);\n"
         + "  float rew_turn = __rewind_speed / (rew_r + __rewind_radius);\n"
         + "  float rew_nr = rew_r * (1.0f - __rewind_decay * 0.05f);\n"
         + "  float rew_nt = rew_theta + rew_turn;\n"
         + "  __px += __rewind * rew_nr * cosf(rew_nt);\n"
         + "  __py += __rewind * rew_nr * sinf(rew_nt);\n";
  }
}