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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Loonie2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SIDES = "sides";
  private static final String PARAM_STAR = "star";
  private static final String PARAM_CIRCLE = "circle";
  private static final String[] paramNames = {PARAM_SIDES, PARAM_STAR, PARAM_CIRCLE};

  private int sides = 4;
  private double star = 0.0;
  private double circle = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* loonie2 by dark-beam, http://dark-beam.deviantart.com/art/Loonie2-update-2-Loonie3-457414891 */
    double xrt = pAffineTP.x, yrt = pAffineTP.y, swp;
    // r2 = xrt; // normal
    double r2 = xrt * _coss + fabs(yrt) * _sins; // +star
    double circle = sqrt(sqr(xrt) + sqr(yrt));

    int i;

    for (i = 0; i < sides - 1; i++) {
      // rotate around to get the the sides!!! :D
      swp = xrt * _cosa - yrt * _sina;
      yrt = xrt * _sina + yrt * _cosa;
      xrt = swp;

      // r2 = MAX(r2, xrt); // normal
      r2 = Math.max(r2, xrt * _coss + fabs(yrt) * _sins); // +star
    }
    r2 = r2 * _cosc + circle * _sinc; // +circle
    if (i > 1) {
      r2 = sqr(r2); // we want it squared, for the pretty effect
    } else {
      r2 = fabs(r2) * r2; // 2-faces effect JUST FOR i=1
    }

    if (r2 > 0 && (r2 < _sqrvvar)) {
      double r = pAmount * sqrt(fabs(_sqrvvar / r2 - 1.0));
      pVarTP.x += r * pAffineTP.x;
      pVarTP.y += r * pAffineTP.y;
    } else if (r2 < 0) {// 2-faces effect JUST FOR i=1
      double r = pAmount / sqrt(fabs(_sqrvvar / r2) - 1.0);
      pVarTP.x += r * pAffineTP.x;
      pVarTP.y += r * pAffineTP.y;
    } else {
      pVarTP.x += pAmount * pAffineTP.x;
      pVarTP.y += pAmount * pAffineTP.y;
    }
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
    return new Object[]{sides, star, circle};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SIDES.equalsIgnoreCase(pName))
      sides = limitIntVal(Tools.FTOI(pValue), 1, 50);
    else if (PARAM_STAR.equalsIgnoreCase(pName))
      star = limitVal(pValue, -1.0, 1.0);
    else if (PARAM_CIRCLE.equalsIgnoreCase(pName))
      circle = limitVal(pValue, -1.0, 1.0);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "loonie2";
  }

  private double _sqrvvar, _sina, _cosa, _sins, _coss, _sinc, _cosc;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _sqrvvar = pAmount * pAmount;

    double a = M_2PI / sides;
    _sina = sin(a);
    _cosa = cos(a);

    a = -M_PI_2 * star;
    _sins = sin(a);
    _coss = cos(a);

    a = M_PI_2 * circle;
    _sinc = sin(a);
    _cosc = cos(a);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "int sides = lroundf(__loonie2_sides);\n"
        + "float _sqrvvar = __loonie2 * __loonie2;\n"
        + "float a = 2.0f*PI / sides;\n"
        + "float _sina = sinf(a);\n"
        + "float _cosa = cosf(a);\n"
        + "a = -PI*0.5f * __loonie2_star;\n"
        + "float _sins = sinf(a);\n"
        + "float _coss = cosf(a);\n"
        + "a = PI*0.5f * __loonie2_circle;\n"
        + "float _sinc = sinf(a);\n"
        + "float _cosc = cosf(a);\n"
        + "    float xrt = __x, yrt = __y, swp;\n"
        + "    float r2 = xrt * _coss + fabsf(yrt) * _sins;\n"
        + "    float circle = sqrtf(xrt*xrt + yrt*yrt);\n"
        + "    int i;\n"
        + "    for (i = 0; i < sides - 1; i++) {\n"
        + "      swp = xrt * _cosa - yrt * _sina;\n"
        + "      yrt = xrt * _sina + yrt * _cosa;\n"
        + "      xrt = swp;\n"
        + "      r2 = fmaxf(r2, xrt * _coss + fabsf(yrt) * _sins);\n"
        + "    }\n"
        + "    r2 = r2 * _cosc + __loonie2_circle * _sinc;\n"
        + "    if (i > 1) {\n"
        + "      r2 = r2*r2;\n"
        + "    } else {\n"
        + "      r2 = fabsf(r2) * r2;\n"
        + "    }\n"
        + "\n"
        + "    if (r2 > 0 && (r2 < _sqrvvar)) {\n"
        + "      float r = __loonie2 * sqrtf(fabsf(_sqrvvar / r2 - 1.0f));\n"
        + "      __px += r * __x;\n"
        + "      __py += r * __y;\n"
        + "    } else if (r2 < 0) {\n"
        + "      float r = __loonie2 / sqrtf(fabsf(_sqrvvar / r2) - 1.0f);\n"
        + "      __px += r * __x;\n"
        + "      __py += r * __y;\n"
        + "    } else {\n"
        + "      __px += __loonie2 * __x;\n"
        + "      __py += __loonie2 * __y;\n"
        + "    }"
        + (context.isPreserveZCoordinate() ? "__pz += __loonie2 * __z;\n" : "");
  }
}
