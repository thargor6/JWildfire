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

public class HoleFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_A = "a";
  public static final String PARAM_INSIDE = "inside";
  private static final String[] paramNames = {PARAM_A, PARAM_INSIDE};

  private double a = 1.0;

  private int inside = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm,
                        XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double alpha = atan2(pAffineTP.y,pAffineTP.x);
    double delta = pow(alpha/M_PI + 1.f, a);
    double r;
    if (inside!=0)
      r = pAmount*delta/(pAffineTP.x*pAffineTP.x + pAffineTP.y*pAffineTP.y + delta);
    else
      r = pAmount*sqrt(pAffineTP.x*pAffineTP.x + pAffineTP.y*pAffineTP.y + delta);
    double s = sin(alpha);
    double c = cos(alpha);
    pVarTP.x += r * c;
    pVarTP.y += r * s;
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
    return new Object[]{a, inside};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_INSIDE.equalsIgnoreCase(pName))
      inside = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "hole";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "int hole_inside = (int)varpar->hole_inside;\n"
        + "float alpha = atan2f(__y,__x);\n"
        + "float delta = powf(alpha/M_PI_F + 1.f, varpar->hole_a);\n"
        + "float r;\n"
        + "if (hole_inside)\n"
        + "    r = varpar->hole*delta/(__x*__x + __y*__y + delta);\n"
        + "else\n"
        + "    r = varpar->hole*sqrtf(__x*__x + __y*__y + delta);\n"
        + "float c;\n"
        + "float s;\n"
        + "sincosf(alpha, &s, &c);\n"
        + "\n"
        + "__px += r * c;\n"
        + "__py += r * s;\n"
        + (context.isPreserveZCoordinate() ? "__pz += varpar->hole * __z;\n" : "");
  }
}