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
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class PhyllotaxisFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SPREAD = "spread";
  private static final String PARAM_ANGLE_STEP = "angle_step";
  private static final String[] paramNames = {PARAM_SPREAD, PARAM_ANGLE_STEP};

  private double spread = 0.1;
  private double angle_step = 137.508;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double ph_spread = Math.max(fabs(this.spread), 1e-4);
    double ph_step = this.angle_step * Math.PI / 180.0;
    double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double index = r / ph_spread;
    double theta = index * ph_step;
    double new_r = sqrt(index) * ph_spread;

    pVarTP.x += pAmount * new_r * cos(theta);
    pVarTP.y += pAmount * new_r * sin(theta);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{spread, angle_step}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SPREAD.equalsIgnoreCase(pName)) spread = pValue;
    else if (PARAM_ANGLE_STEP.equalsIgnoreCase(pName)) angle_step = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "phyllotaxis"; }
  
  @Override
  public void randomize() {
  	spread = Math.random() * 3.0;
  	angle_step = Math.random() * 720.0 - 360.0;
  	if (Math.random() < 0.2) {
  		angle_step *= 2.0;
  	}
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float ph_spread = fmaxf(fabsf(__phyllotaxis_spread), 1e-4f);\n"
         + "  float ph_step = __phyllotaxis_angle_step * 3.14159265f / 180.0f;\n"
         + "  float r = sqrtf(__x * __x + __y * __y);\n"
         + "  float index = r / ph_spread;\n"
         + "  float theta = index * ph_step;\n"
         + "  float new_r = sqrtf(index) * ph_spread;\n"
         + "  __px += __phyllotaxis * new_r * cosf(theta);\n"
         + "  __py += __phyllotaxis * new_r * sinf(theta);\n";
  }
}