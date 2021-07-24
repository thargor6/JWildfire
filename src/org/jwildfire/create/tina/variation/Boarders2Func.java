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

public class Boarders2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_C = "c";
  private static final String PARAM_LEFT = "left";
  private static final String PARAM_RIGHT = "right";
  private static final String[] paramNames = {PARAM_C, PARAM_LEFT, PARAM_RIGHT};

  private double c = 0.4;
  private double left = 0.65;
  private double right = 0.35;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // boarders2 by Xyrus02, http://xyrus02.deviantart.com/art/Boarders2-plugin-for-Apophysis-173427128
    double roundX = rint(pAffineTP.x);
    double roundY = rint(pAffineTP.y);
    double offsetX = pAffineTP.x - roundX;
    double offsetY = pAffineTP.y - roundY;
    if (pContext.random() >= _cr) {
      pVarTP.x += pAmount * (offsetX * _c + roundX);
      pVarTP.y += pAmount * (offsetY * _c + roundY);
    } else {
      if (fabs(offsetX) >= fabs(offsetY)) {
        if (offsetX >= 0.0) {
          pVarTP.x += pAmount * (offsetX * _c + roundX + _cl);
          pVarTP.y += pAmount * (offsetY * _c + roundY + _cl * offsetY / offsetX);
        } else {
          pVarTP.x += pAmount * (offsetX * _c + roundX - _cl);
          pVarTP.y += pAmount * (offsetY * _c + roundY - _cl * offsetY / offsetX);
        }
      } else {
        if (offsetY >= 0.0) {
          pVarTP.y += pAmount * (offsetY * _c + roundY + _cl);
          pVarTP.x += pAmount * (offsetX * _c + roundX + offsetX / offsetY * _cl);
        } else {
          pVarTP.y += pAmount * (offsetY * _c + roundY - _cl);
          pVarTP.x += pAmount * (offsetX * _c + roundX - offsetX / offsetY * _cl);
        }
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
    return new Object[]{c, left, right};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else if (PARAM_LEFT.equalsIgnoreCase(pName))
      left = pValue;
    else if (PARAM_RIGHT.equalsIgnoreCase(pName))
      right = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "boarders2";
  }

  private double _c, _cl, _cr;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _c = fabs(c);
    _cl = fabs(left);
    _cr = fabs(right);
    _c = _c == 0 ? EPSILON : _c;
    _cl = _cl == 0 ? EPSILON : _cl;
    _cr = _cr == 0 ? EPSILON : _cr;
    _cl = _c * _cl;
    _cr = _c + (_c * _cr);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float _c, _cl, _cr;\n"
        + "_c = fabsf(__boarders2_c);\n"
        + "_cl = fabsf(__boarders2_left);\n"
        + "_cr = fabsf(__boarders2_right);\n"
        + "_c = _c == 0 ? 1.e-6f : _c;\n"
        + "_cl = _cl == 0 ? 1.e-6f : _cl;\n"
        + "_cr = _cr == 0 ? 1.e-6f : _cr;\n"
        + "_cl = _c * _cl;\n"
        + "_cr = _c + (_c * _cr);\n"
        + "float roundX = rint(__x);\n"
        + "float roundY = rint(__y);\n"
        + "float offsetX = __x - roundX;\n"
        + "float offsetY = __y - roundY;\n"
        + "if (RANDFLOAT() >= _cr) {\n"
        + "  __px += __boarders2 * (offsetX * _c + roundX);\n"
        + "  __py += __boarders2 * (offsetY * _c + roundY);\n"
        + "} else {\n"
        + "   if (fabsf(offsetX) >= fabsf(offsetY)) {\n"
        + "     if (offsetX >= 0.0f) {\n"
        + "       __px += __boarders2 * (offsetX * _c + roundX + _cl);\n"
        + "       __py += __boarders2 * (offsetY * _c + roundY + _cl * offsetY / offsetX);\n"
        + "     } else {\n"
        + "       __px += __boarders2 * (offsetX * _c + roundX - _cl);\n"
        + "       __py += __boarders2 * (offsetY * _c + roundY - _cl * offsetY / offsetX);\n"
        + "      }\n"
        + "    } else {\n"
        + "      if (offsetY >= 0.0f) {\n"
        + "        __py += __boarders2 * (offsetY * _c + roundY + _cl);\n"
        + "        __px += __boarders2 * (offsetX * _c + roundX + offsetX / offsetY * _cl);\n"
        + "      } else {\n"
        + "        __py += __boarders2 * (offsetY * _c + roundY - _cl);\n"
        + "        __px += __boarders2 * (offsetX * _c + roundX - offsetX / offsetY * _cl);\n"
        + "      }\n"
        + "    }\n"
        + "  }"
        + (context.isPreserveZCoordinate() ? "__pz += __boarders2 * __z;\n": "");
  }
}
