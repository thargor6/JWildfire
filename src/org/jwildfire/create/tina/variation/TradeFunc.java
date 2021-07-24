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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class TradeFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_R1 = "r1";
  private static final String PARAM_D1 = "d1";
  private static final String PARAM_R2 = "r2";
  private static final String PARAM_D2 = "d2";
  private static final String[] paramNames = {PARAM_R1, PARAM_D1, PARAM_R2, PARAM_D2};

  private double r1 = 1.0;
  private double d1 = 1.0;
  private double r2 = 1.0;
  private double d2 = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* trade by Michael Faber,  http://michaelfaber.deviantart.com/art/The-Lost-Variations-258913970 */
    if (pAffineTP.x > 0.0) {
      double r = sqrt(sqr(this._c1 - pAffineTP.x) + sqr(pAffineTP.y));
      if (r <= this.r1) {
        r *= this.r2 / this.r1;
        double a = atan2(pAffineTP.y, this._c1 - pAffineTP.x);
        double s = sin(a);
        double c = cos(a);
        pVarTP.x += pAmount * (r * c - this._c2);
        pVarTP.y += pAmount * r * s;
      } else {
        pVarTP.x += pAmount * pAffineTP.x;
        pVarTP.y += pAmount * pAffineTP.y;
      }
    } else {
      double r = sqrt(sqr(-this._c2 - pAffineTP.x) + sqr(pAffineTP.y));

      if (r <= this.r2) {
        r *= this.r1 / this.r2;
        double a = atan2(pAffineTP.y, -this._c2 - pAffineTP.x);
        double s = sin(a);
        double c = cos(a);
        pVarTP.x += pAmount * (r * c + this._c1);
        pVarTP.y += pAmount * r * s;
      } else {
        pVarTP.x += pAmount * pAffineTP.x;
        pVarTP.y += pAmount * pAffineTP.y;
      }
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
    return new Object[]{r1, d1, r2, d2};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_R1.equalsIgnoreCase(pName))
      r1 = limitVal(pValue, EPSILON, Double.MAX_VALUE);
    else if (PARAM_D1.equalsIgnoreCase(pName))
      d1 = limitVal(pValue, 0.0, Double.MAX_VALUE);
    else if (PARAM_R2.equalsIgnoreCase(pName))
      r2 = limitVal(pValue, EPSILON, Double.MAX_VALUE);
    else if (PARAM_D2.equalsIgnoreCase(pName))
      d2 = limitVal(pValue, 0.0, Double.MAX_VALUE);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "trade";
  }

  private double _c1, _c2;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _c1 = r1 + d1;
    _c2 = r2 + d2;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float _c1, _c2;\n"
        + "_c1 = __trade_r1 + __trade_d1;\n"
        + "_c2 = __trade_r2 + __trade_d2;\n"
        + "if (__x > 0.0f) {\n"
        + "   float r = sqrtf((_c1 - __x)*(_c1 - __x) + __y*__y);\n"
        + "   if (r <= __trade_r1) {\n"
        + "     r *= __trade_r2 / __trade_r1;\n"
        + "     float a = atan2f(__y, _c1 - __x);\n"
        + "     float s = sinf(a);\n"
        + "     float c = cosf(a);\n"
        + "      __px += __trade * (r * c - _c2);\n"
        + "      __py += __trade * r * s;\n"
        + "   } else {\n"
        + "      __px += __trade * __x;\n"
        + "      __py += __trade * __y;\n"
        + "   }\n"
        + "} else {\n"
        + "   float r = sqrtf((-_c2 - __x)*(-_c2 - __x) + __y*__y);\n"
        + "   if (r <= __trade_r2) {\n"
        + "     r *= __trade_r1 / __trade_r2;\n"
        + "     float a = atan2f(__y, -_c2 - __x);\n"
        + "     float s = sinf(a);\n"
        + "     float c = cosf(a);\n"
        + "     __px += __trade * (r * c + _c1);\n"
        + "     __py += __trade * r * s;\n"
        + "   } else {\n"
        + "     __px += __trade * __x;\n"
        + "     __py += __trade * __y;\n"
        + "   }\n"
        + "}"
        + (context.isPreserveZCoordinate() ? "__pz += __trade * __z;\n" : "");
  }
}
