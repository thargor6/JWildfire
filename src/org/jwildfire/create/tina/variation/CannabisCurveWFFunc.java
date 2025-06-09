/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class CannabisCurveWFFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_FILLED = "filled";
  private static final String[] paramNames = {PARAM_FILLED};

  private double filled = 0.85;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double a = pAffineTP.getPrecalcAtan();

    // cannabis curve (http://mathworld.wolfram.com/CannabisCurve.html)
    double r = (1 + 9.0 / 10.0 * cos(8.0 * a)) * (1 + 1.0 / 10.0 * cos(24.0 * a)) * (9.0 / 10.0 + 1.0 / 10.0 * cos(200.0 * a)) * (1.0 + sin(a));
    a += M_PI / 2.0;

    if (filled > 0 && filled > pContext.random()) {
      r *= pContext.random();
    }



    double nx = sin(a) * r;
    double ny = cos(a) * r;

    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{filled};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FILLED.equalsIgnoreCase(pName))
      filled = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "cannabiscurve_wf";
  }
  
  @Override
  public void randomize() {
  	filled = Math.random();
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float a = __phi;\n"
        + "float r = (1.f + 9.0f / 10.0f * cosf(8.0f * a)) * (1.f + 1.0f / 10.0f * cosf(24.0f * a)) * (9.0f / 10.0f + 1.0f / 10.0f * cosf(200.0f * a)) * (1.0f + sinf(a));\n"
        + "a += PI / 2.0f;\n"
        + "if (__cannabiscurve_wf_filled > 0 && __cannabiscurve_wf_filled>RANDFLOAT()) {\n"
        + "   r *= RANDFLOAT();\n"
        + "}\n"
        + "float nx = sinf(a) * r;\n"
        + "float ny = cosf(a) * r;\n"
        + "__px += __cannabiscurve_wf * nx;\n"
        + "__py += __cannabiscurve_wf * ny;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __cannabiscurve_wf * __z;\n": "");
  }
}
