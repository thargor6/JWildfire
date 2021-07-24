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

public class SigmoidFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SHIFTX = "shiftx";
  private static final String PARAM_SHIFTY = "shifty";
  private static final String[] paramNames = {PARAM_SHIFTX, PARAM_SHIFTY};

  private double shiftx = 1.0;
  private double shifty = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // author Xyrus. Implemented by Brad Stefanov
    double ax = 1.0;
    double ay = 1.0;
    double sx = shiftx;
    double sy = shifty;
    if (sx < 1 && sx > -1) {
      if (sx == 0) {
        sx = SMALL_EPSILON;
        ax = 1.0;
      } else {
        ax = (sx < 0 ? -1 : 1);
        sx = 1 / sx;
      }
    }
    if (sy < 1 && sy > -1) {
      if (sy == 0) {
        sy = SMALL_EPSILON;
        ay = 1.0;
      } else {
        ay = (sy < 0 ? -1 : 1);
        sy = 1 / sy;
      }
    }

    sx *= -5;
    sy *= -5;

    double vv = fabs(pAmount);

    double c0 = ax / (1.0 + exp(sx * pAffineTP.x));
    double c1 = ay / (1.0 + exp(sy * pAffineTP.y));
    double x = (2 * (c0 - 0.5));
    double y = (2 * (c1 - 0.5));

    pVarTP.x += vv * x;
    pVarTP.y += vv * y;
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
    return new Object[]{shiftx, shifty};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SHIFTX.equalsIgnoreCase(pName))
      shiftx = pValue;
    else if (PARAM_SHIFTY.equalsIgnoreCase(pName))
      shifty = pValue;
    else
      throw new IllegalArgumentException(pName);
  }


  @Override
  public String getName() {
    return "sigmoid";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float ax = 1.f; float ay = 1.f;\n"
        + "float sx = __sigmoid_shiftx;\n"
        + "float sy = __sigmoid_shifty;\n"
        + "if (sx < 1.f && sx > -1.f) {\n"
        + "    if (sx == 0.f) {\n"
        + "        sx = 1.e-10f;\n"
        + "    } else {\n"
        + "        ax = sx < 0.f ? -1.f : 1.f;\n"
        + "        sx = 1.f/sx;\n"
        + "    }\n"
        + "}\n"
        + "if (sy < 1.f && sy > -1.f) {\n"
        + "    if (sy == 0.f) {\n"
        + "        sy = 1.e-10f;\n"
        + "    } else {\n"
        + "        ay = sy < 0.f ? -1.f : 1.f;\n"
        + "        sy = 1.f/sy;\n"
        + "    }\n"
        + "}\n"
        + "sx *= -5.f;\n"
        + "sy *= -5.f;\n"
        + "float vv = fabsf(__sigmoid);\n"
        + "float c0 = ax /(1.f + expf(sx*__x));\n"
        + "float c1 = ay /(1.f + expf(sy*__y));\n"
        + "float x = (2.f *(c0 - 0.5f));\n"
        + "float y = (2.f *(c1 - 0.5f));\n"
        + "__px += vv*x;\n"
        + "__py += vv*y;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __sigmoid*__z;\n" : "");
  }
}
