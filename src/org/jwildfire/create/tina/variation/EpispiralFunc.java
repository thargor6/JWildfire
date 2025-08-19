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

public class EpispiralFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_N = "n";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_HOLES = "holes";
  private static final String[] paramNames = {PARAM_N, PARAM_THICKNESS, PARAM_HOLES};

  private double n = 6.0;
  private double thickness = 0.0;
  private double holes = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // epispiral by cyberxaos, http://cyberxaos.deviantart.com/journal/Epispiral-Plugin-240086108   
    double theta = atan2(pAffineTP.y, pAffineTP.x);
    double t = -holes;
    if (fabs(thickness) > EPSILON) {
      double d = cos(n * theta);
      if (d == 0) {
        return;
      }
      t += (pContext.random() * thickness) * (1.0 / d);
    } else {
      double d = cos(n * theta);
      if (d == 0) {
        return;
      }
      t += 1.0 / d;
    }
    pVarTP.x += pAmount * t * cos(theta);
    pVarTP.y += pAmount * t * sin(theta);
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
    return new Object[]{n, thickness, holes};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_N.equalsIgnoreCase(pName))
      n = pValue;
    else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
      thickness = pValue;
    else if (PARAM_HOLES.equalsIgnoreCase(pName))
      holes = pValue;
    else
      throw new IllegalArgumentException(pName);
  }
  
  @Override
  public void randomize() {
  	if (Math.random() < 0.5) n = floor(Math.random() * 17 + 3);
  	else n = Math.random() * 48.0 + 2.0;
  	thickness = (Math.random() < 0.2) ? 0.0 : Math.random() * 5.0 - 2.5;
  	holes = Math.random() * 10.0 - 5.0;
  }

  @Override
  public String getName() {
    return "epispiral";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float theta = atan2f(__y, __x);\n"
        + "    float t = -__epispiral_holes;\n"
        + "    if (fabsf(__epispiral_thickness) > 1.0e-6f) {\n"
        + "      float d = cosf(__epispiral_n * theta);\n"
        + "      t += (RANDFLOAT() * __epispiral_thickness) * (1.0 / d);\n"
        + "    } else {\n"
        + "      float d = cosf(__epispiral_n * theta);\n"
        + "      t += 1.0 / d;\n"
        + "    }\n"
        + "    __px += __epispiral * t * cosf(theta);\n"
        + "    __py += __epispiral * t * sinf(theta);\n"
        + (context.isPreserveZCoordinate() ? "    __pz += __epispiral*__z;\n" : "");
  }
}
