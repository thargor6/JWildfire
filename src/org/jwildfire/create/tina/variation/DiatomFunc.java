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

public class DiatomFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SPOKES = "spokes";
  private static final String PARAM_AMPLITUDE = "amplitude";
  private static final String[] paramNames = {PARAM_SPOKES, PARAM_AMPLITUDE};

  private int spokes = 8;
  private double amplitude = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r2 = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y;
    double dt_r = sqrt(r2 + 1.0e-10);
    double dt_theta = atan2(pAffineTP.y, pAffineTP.x);
    double dt_radial = 1.0 + this.amplitude * cos((double) this.spokes * dt_theta);
    double dt_nr = dt_r * dt_radial;

    pVarTP.x += pAmount * dt_nr * cos(dt_theta);
    pVarTP.y += pAmount * dt_nr * sin(dt_theta);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{spokes, amplitude}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SPOKES.equalsIgnoreCase(pName)) spokes = (int) pValue;
    else if (PARAM_AMPLITUDE.equalsIgnoreCase(pName)) amplitude = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "diatom"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float dt_r = sqrtf(__x * __x + __y * __y + 1.0e-10f);\n"
         + "  float dt_theta = atan2f(__y, __x);\n"
         + "  float dt_radial = 1.0f + __diatom_amplitude * cosf((float)__diatom_spokes * dt_theta);\n"
         + "  float dt_nr = dt_r * dt_radial;\n"
         + "  __px += __diatom * dt_nr * cosf(dt_theta);\n"
         + "  __py += __diatom * dt_nr * sinf(dt_theta);\n";
  }
}