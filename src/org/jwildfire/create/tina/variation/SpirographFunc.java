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

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class SpirographFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_D = "d";
  private static final String PARAM_TMIN = "tmin";
  private static final String PARAM_TMAX = "tmax";
  private static final String PARAM_YMIN = "ymin";
  private static final String PARAM_YMAX = "ymax";
  private static final String PARAM_C1 = "c1";
  private static final String PARAM_C2 = "c2";

  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_D, PARAM_TMIN, PARAM_TMAX, PARAM_YMIN, PARAM_YMAX, PARAM_C1, PARAM_C2};

  private double a = 3.0;
  private double b = 2.0;
  private double d = 0.0;
  private double c1 = 0.0;
  private double c2 = 0.0;
  private double tmin = -1.0;
  private double tmax = 1.0;
  private double ymin = -1.0;
  private double ymax = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double t = (tmax - tmin) * pContext.random() + tmin;
    double y = (ymax - ymin) * pContext.random() + ymin;
    double x1 = (a + b) * cos(t) - c1 * cos((a + b) / b * t);
    double y1 = (a + b) * sin(t) - c2 * sin((a + b) / b * t);
    pVarTP.x += pAmount * (x1 + d * cos(t) + y);
    pVarTP.y += pAmount * (y1 + d * sin(t) + y);
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
    return new Object[]{a, b, d, tmin, tmax, ymin, ymax, c1, c2};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName))
      d = pValue;
    else if (PARAM_TMIN.equalsIgnoreCase(pName))
      tmin = pValue;
    else if (PARAM_TMAX.equalsIgnoreCase(pName))
      tmax = pValue;
    else if (PARAM_YMIN.equalsIgnoreCase(pName))
      ymin = pValue;
    else if (PARAM_YMAX.equalsIgnoreCase(pName))
      ymax = pValue;
    else if (PARAM_TMIN.equalsIgnoreCase(pName))
      tmin = pValue;
    else if (PARAM_C1.equalsIgnoreCase(pName))
      c1 = pValue;
    else if (PARAM_C2.equalsIgnoreCase(pName))
      c2 = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "spirograph";
  }
  
  @Override
  public void randomize() {
    a = Math.random();
    b = Math.random() * 2.0 - 1.0;
    c1 = Math.random();
    c2 = (Math.random() < 0.8) ? c1 : Math.random();
    d = (Math.random() < 0.75) ? 0.0 : Math.random() * 2.0 - 1.0;
    tmin = -Math.random() * 250.0;
    tmax = Math.random() * 250.0;
    ymin = (Math.random() < 0.75) ? 0.0 : -Math.random() * 0.05;
    ymax = (Math.random() < 0.75) ? 0.0 : Math.random() * 0.05;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float d1 = (__spirograph_tmax - __spirograph_tmin) * RANDFLOAT() + __spirograph_tmin;\n"
        + "float d2 = (__spirograph_ymax - __spirograph_ymin) * RANDFLOAT() + __spirograph_ymin;\n"
        + "float e  = __spirograph_a + __spirograph_b;\n"
        + "float f  = e / __spirograph_b * d1;\n"
        + "float d3 = e * cosf(d1) - __spirograph_c1 * cosf(f);\n"
        + "float d4 = e * sinf(d1) - __spirograph_c2 * sinf(f);\n"
        + "\n"
        + "__px += __spirograph * (d3 + __spirograph_d * cosf(d1) + d2);\n"
        + "__py += __spirograph * (d4 + __spirograph_d * sinf(d1) + d2);\n"
        + (context.isPreserveZCoordinate() ? "__pz += __spirograph*__z;\n" : "");
  }
}
