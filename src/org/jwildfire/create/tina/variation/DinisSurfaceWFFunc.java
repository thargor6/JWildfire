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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class DinisSurfaceWFFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String[] paramNames = {PARAM_A, PARAM_B};

  private double a = 0.8;
  private double b = 0.2;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Dini's Surface, http://mathworld.wolfram.com/DinisSurface.html  */
    double u = pAffineTP.x;
    double v = pAffineTP.y;
    double sinv = sin(v);
    pVarTP.x += pAmount * a * cos(u) * sinv;
    pVarTP.y += pAmount * a * sin(u) * sinv;
    if (fabs(v) > EPSILON) {
      pVarTP.z += -pAmount * (a * (cos(v) + log(tan(fabs(v) / 2.0))) + b * u);
      pVarTP.doHide = false;
    }
    else {
      pVarTP.doHide = true;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{a, b};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "dinis_surface_wf";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "   float u = __x;\n"
        + "    float v = __y;\n"
        + "    float sinv = sinf(v);\n"
        + "    float t=logf(tanf(v / 2.0));\n"
        + "    __px += varpar->dinis_surface_wf * varpar->dinis_surface_wf_a * cosf(u) * sinv;\n"
        + "    __py += varpar->dinis_surface_wf * varpar->dinis_surface_wf_a * sinf(u) * sinv;\n"
        + " if(fabsf(v)>1.0e-06) {"
        + "    __pz += -varpar->dinis_surface_wf * (varpar->dinis_surface_wf_a * (cosf(v) +  logf(tanf(fabsf(v) / 2.0))) + varpar->dinis_surface_wf_b * u);\n"
        + "    __doHide = false;\n"
        + " }"
        + " else __doHide=true; \n";
  }
}
