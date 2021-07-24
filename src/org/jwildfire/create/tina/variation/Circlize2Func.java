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

public class Circlize2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_HOLE = "hole";

  private static final String[] paramNames = {PARAM_HOLE};

  private double hole = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // circlize2 by MichaelFaber - The angle pack: http://michaelfaber.deviantart.com/art/The-Angle-Pack-277718538
    double absx = fabs(pAffineTP.x);
    double absy = fabs(pAffineTP.y);
    double side;
    double perimeter;
    double r, sina, cosa;

    if (absx >= absy) {
      if (pAffineTP.x >= absy) {
        perimeter = absx + pAffineTP.y;
      } else {
        perimeter = 5.0 * absx - pAffineTP.y;
      }

      side = absx;
    } else {
      if (pAffineTP.y >= absx) {
        perimeter = 3.0 * absy - pAffineTP.x;
      } else {
        perimeter = 7.0 * absy + pAffineTP.x;
      }

      side = absy;
    }

    r = pAmount * (side + hole);
    double a = M_PI_4 * perimeter / side - M_PI_4;
    sina = sin(a);
    cosa = cos(a);

    pVarTP.x += r * cosa;
    pVarTP.y += r * sina;

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
    return new Object[]{hole};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_HOLE.equalsIgnoreCase(pName))
      hole = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "circlize2";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float absx = fabsf(__x);\n"
        + "    float absy = fabsf(__y);\n"
        + "    float side;\n"
        + "    float perimeter;\n"
        + "    float r, sina, cosa;\n"
        + "\n"
        + "    if (absx >= absy) {\n"
        + "      if (__x >= absy) {\n"
        + "        perimeter = absx + __y;\n"
        + "      } else {\n"
        + "        perimeter = 5.0 * absx - __y;\n"
        + "      }\n"
        + "\n"
        + "      side = absx;\n"
        + "    } else {\n"
        + "      if (__y >= absx) {\n"
        + "        perimeter = 3.0 * absy - __x;\n"
        + "      } else {\n"
        + "        perimeter = 7.0 * absy + __x;\n"
        + "      }\n"
        + "\n"
        + "      side = absy;\n"
        + "    }\n"
        + "\n"
        + "    r = __circlize2 * (side + __circlize2_hole);\n"
        + "    float a = (0.25f*PI) * perimeter / side - (0.25f*PI);\n"
        + "    sina = sinf(a);\n"
        + "    cosa = cosf(a);\n"
        + "\n"
        + "    __px += r * cosa;\n"
        + "    __py += r * sina;\n"
        + "\n"
        + (context.isPreserveZCoordinate() ? "      __pz += __circlize2 * __z;\n" : "");
  }
}
