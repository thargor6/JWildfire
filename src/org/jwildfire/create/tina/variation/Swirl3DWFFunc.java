/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2023 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class Swirl3DWFFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_N = "n";

  private static final String[] paramNames = {PARAM_N};

  private double n = 0;


  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double rad = pAffineTP.getPrecalcSqrt();
    double ang = pAffineTP.getPrecalcAtanYX();   // + log(rad)*shift;

    pVarTP.x += pAmount * (rad * cos(ang));
    pVarTP.y += pAmount * (rad * sin(ang));
    pVarTP.z += pAmount * (sin(6.0 * cos(rad) - n * ang));
    pVarTP.color = Math.abs(sin(6.0 * cos(rad) - n * ang));
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{n};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_N.equalsIgnoreCase(pName))
      n = (int) pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "swirl3D_wf";
  }
  
  @Override
  public void randomize() {
    n = (int) (Math.random() * 50 - 25);
  }
  
  @Override
  public void mutate(double pAmount) {
    n = (int) n + pAmount;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTS_BACKGROUND};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float rad = __r;\n"
            + "float ang = __theta;\n"
            + "__px += __swirl3D_wf * (rad * cosf(ang));\n"
            + "__py += __swirl3D_wf * (rad * sinf(ang));\n"
            + "__pz += __swirl3D_wf * (sinf(6.0f * cosf(rad) - __swirl3D_wf_n * ang));\n"
            + "__pal = fabsf(sinf(6.0 * cosf(rad) - __swirl3D_wf_n * ang));";
  }
}
