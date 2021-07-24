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

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class KaleidoscopeFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PULL = "pull";
  private static final String PARAM_ROTATE = "rotate";
  private static final String PARAM_LINE_UP = "line_up";
  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";

  private static final String[] paramNames = {PARAM_PULL, PARAM_ROTATE, PARAM_LINE_UP, PARAM_X, PARAM_Y};

  private double pull = 0.0;
  private double rotate = 1.0;
  private double line_up = 1.0;
  private double x = 0.0;
  private double y = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Kaleidoscope by Will Evans, http://eevans1.deviantart.com/art/kaleidoscope-plugin-122185469  */
    pVarTP.x += ((_w * pAffineTP.x) * cos(45.0) - pAffineTP.y * sin(45.0) + _e) + _r;
    // the if function splits the plugin in two.
    if (pAffineTP.y > 0) {
      pVarTP.y += ((_w * pAffineTP.y) * cos(45.0) + pAffineTP.x * sin(45.0) + _q + _e) + _t;
    } else {
      pVarTP.y += (_w * pAffineTP.y) * cos(45.0) + pAffineTP.x * sin(45.0) - _q - _e;
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
    return new Object[]{pull, rotate, line_up, x, y};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PULL.equalsIgnoreCase(pName))
      pull = pValue;
    else if (PARAM_ROTATE.equalsIgnoreCase(pName))
      rotate = pValue;
    else if (PARAM_LINE_UP.equalsIgnoreCase(pName))
      line_up = pValue;
    else if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "kaleidoscope";
  }

  double _q, _w, _e, _r, _t, _i;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _q = pull; // pulls apart the 2 sections of the plugin
    _w = rotate; // rotates both halves of the plugin
    _e = line_up;
    _r = x; // changes x co-ordinates
    _t = y; // changes y co-ordinates for 1 part of the plugin
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float q = __kaleidoscope_pull;\n"
        + "float w = __kaleidoscope_rotate;\n"
        + "float e = __kaleidoscope_line_up;\n"
        + "float r = __kaleidoscope_x;\n"
        + "float t = __kaleidoscope_y;\n"
        + "__px += ((w * __x) *cosf(45.f)- __y *sinf(45.f) +  e ) + r;\n"
        + "if (__y > 0)\n"
        + "    __py += (w * __y) * cosf(45.f) + __x *sinf(45.f)  + q + e + t;\n"
        + "else\n"
        + "    __py += (w * __y) * cosf(45.f) + __x *sinf(45.f)  - q  - e;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __kaleidoscope * __z;\n" : "");
  }
}
