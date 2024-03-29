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

public class PreDisc3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PI = "pi";
  private static final String[] paramNames = {PARAM_PI};

  private double pi = M_PI;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* pre_disc by gossamer light */
    double r = sqrt(pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + SMALL_EPSILON);
    double a = this.pi * r;
    double sr = sin(a);
    double cr = cos(a);
    double vv = pAmount * atan2(pAffineTP.x, pAffineTP.y) / (this.pi + SMALL_EPSILON);
    pAffineTP.x = vv * sr;
    pAffineTP.y = vv * cr;
    pAffineTP.z = vv * (r * cos(pAffineTP.z));
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{pi};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PI.equalsIgnoreCase(pName))
      pi = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "pre_disc3d";
  }

  @Override
  public int getPriority() {
    return -1;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_PRE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "   float r = sqrtf(__y * __y + __x * __x + 1.e-6f);\n"
        + "    float a = __pre_disc3d_pi * r;\n"
        + "    float sr = sinf(a);\n"
        + "    float cr = cosf(a);\n"
        + "    float vv = __pre_disc3d * atan2f(__x, __y) / (__pre_disc3d_pi + 1.e-6f);\n"
        + "    __x = vv * sr;\n"
        + "    __y = vv * cr;\n"
        + "    __z = vv * (r * cosf(__z));\n"
            + "__r2 = __x*__x+__y*__y;\n"
            + "__r = sqrtf(__r2);\n"
            + "__rinv = 1.f/__r;\n"
            + "__phi = atan2f(__x,__y);\n"
            + "__theta = .5f*PI-__phi;\n"
            + "if (__theta > PI)\n"
            + "    __theta -= 2.f*PI;";
  }
}
