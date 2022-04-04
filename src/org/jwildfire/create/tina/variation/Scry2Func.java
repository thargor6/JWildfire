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

public class Scry2Func extends VariationFunc implements SupportsGPU {
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
    /* scry2 by dark-beam */
    double xrt = pAffineTP.x, yrt = pAffineTP.y, swp;
    // r2 = xrt; // normal
    double r2 = xrt * _coss + fabs(yrt) * _sins; // +star
    double r1 = 0; // placeholder
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
    r1 = r2;
    if (i > 1) {
      r2 = sqr(r2); // we want it squared, for the pretty effect
    } else {
      r2 = fabs(r2) * r2; // 2-faces effect JUST FOR i=1
    }
    // scry effect:
    double d = (r1 * (r2 + 1.0 / pAmount));
    if (d == 0) {
      return;
    }
    double r = 1.0 / d;
    pVarTP.x += pAffineTP.x * r;
    pVarTP.y += pAffineTP.y * r;
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
    return "scry2";
  }

  private double _sina, _cosa, _sins, _coss, _sinc, _cosc;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
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
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float _sina, _cosa, _sins, _coss, _sinc, _cosc;\n"
        + "int sides = lroundf(__scry2_sides);\n"
        + "float a = 2.0f*PI / sides;\n"
        + "    _sina = sinf(a);\n"
        + "    _cosa = cosf(a);\n"
        + "\n"
        + "    a = -PI*0.5f * __scry2_star;\n"
        + "    _sins = sinf(a);\n"
        + "    _coss = cosf(a);\n"
        + "\n"
        + "    a = PI*0.5f * __scry2_circle;\n"
        + "    _sinc = sinf(a);\n"
        + "    _cosc = cosf(a);\n"
        + "    float xrt = __x, yrt = __y, swp;\n"
        + "    float r2 = xrt * _coss + fabsf(yrt) * _sins;\n"
        + "    float r1 = 0;\n"
        + "    float circle = sqrtf(xrt*xrt + yrt*yrt);\n"
        + "\n"
        + "    int i;\n"
        + "\n"
        + "    for (i = 0; i < sides - 1; i++) {\n"
        + "      swp = xrt * _cosa - yrt * _sina;\n"
        + "      yrt = xrt * _sina + yrt * _cosa;\n"
        + "      xrt = swp;\n"
        + "\n"
        + "      r2 = fmaxf(r2, xrt * _coss + fabsf(yrt) * _sins);\n"
        + "    }\n"
        + "    r2 = r2 * _cosc + circle * _sinc;\n"
        + "    r1 = r2;\n"
        + "    if (i > 1) {\n"
        + "      r2 = r2*r2;\n"
        + "    } else {\n"
        + "      r2 = fabsf(r2) * r2;\n"
        + "    }\n"
        + "    float d = (r1 * (r2 + 1.0f / __scry2));\n"
        + "    if (d != 0) {\n"
        + "      float r = 1.0f / d;\n"
        + "      __px += __x * r;\n"
        + "      __py += __y * r;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __scry2 * __z;\n" : "")
        + "    }\n";
  }
}
