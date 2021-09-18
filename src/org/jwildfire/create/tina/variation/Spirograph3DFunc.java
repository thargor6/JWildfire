/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

public class Spirograph3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_TMIN = "tmin";
  private static final String PARAM_TMAX = "tmax";
  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_MODE = "mode";
  private static final String PARAM_DIRECT_COLOR = "direct_color";

  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_TMIN, PARAM_TMAX, PARAM_WIDTH, PARAM_MODE, PARAM_DIRECT_COLOR};

  private double a = 1.0;
  private double b = -0.3;
  private double c = 0.4;
  private double tmin = 0.0;
  private double tmax = 1000.0;
  private double width = 0.0;
  private int mode = 0;
  private int direct_color = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double t = (tmax - tmin) * pContext.random() + tmin;
    double w1, w2, w3;
    switch (mode) {
      case 0:
        w1 = w2 = w3 = width * pContext.random() - width / 2;
        break;
      case 1:
        w1 = width * pContext.random() - width / 2;
        w2 = w1 * sin(36 * t + M_2PI / 3);
        w3 = w1 * sin(36 * t + 2 * M_2PI / 3);
        w1 = w1 * sin(36 * t);
        break;
      case 2:
        w1 = width * pContext.random() - width / 2;
        w2 = width * pContext.random() - width / 2;
        w3 = width * pContext.random() - width / 2;
        break;
      case 3:
        w1 = width * (pContext.random() + pContext.random() + pContext.random() + pContext.random() - 2) / 2;
        w2 = width * (pContext.random() + pContext.random() + pContext.random() + pContext.random() - 2) / 2;
        w3 = width * (pContext.random() + pContext.random() + pContext.random() + pContext.random() - 2) / 2;
        break;
      case 4:
        w1 = (pContext.random() < 0.5) ? width : -width;
        w2 = w3 = 0;
        break;
      default:
        w1 = w2 = w3 = 0;
    }

    double x1 = (a + b) * cos(t) - c * cos((a + b) / b * t);
    double y1 = (a + b) * sin(t) - c * sin((a + b) / b * t);
    double z1 = c * sin((a + b) / b * t);
    pVarTP.x += pAmount * (x1 + w1);
    pVarTP.y += pAmount * (y1 + w2);
    pVarTP.z += pAmount * (z1 + w3);
    if (direct_color != 0) {
      pVarTP.color = fmod(t / M_2PI, 1);
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{a, b, c, tmin, tmax, width, mode, direct_color};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else if (PARAM_TMIN.equalsIgnoreCase(pName))
      tmin = pValue;
    else if (PARAM_TMAX.equalsIgnoreCase(pName))
      tmax = pValue;
    else if (PARAM_WIDTH.equalsIgnoreCase(pName))
      width = pValue;
    else if (PARAM_MODE.equalsIgnoreCase(pName))
      mode = limitIntVal(Tools.FTOI(pValue), 0, 4);
    else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName))
      direct_color = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "spirograph3D";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_DC,VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "    float t = ( __spirograph3D_tmax  - __spirograph3D_tmin) * RANDFLOAT() + __spirograph3D_tmin;"
    		+"    float w1, w2, w3;"
    		+"    switch ( (int) __spirograph3D_mode ) {"
    		+"      case 0:"
    		+"        w1 = w2 = w3 =  __spirograph3D_width  * RANDFLOAT() -  __spirograph3D_width  / 2;"
    		+"        break;"
    		+"      case 1:"
    		+"        w1 =  __spirograph3D_width  * RANDFLOAT() -  __spirograph3D_width  / 2;"
    		+"        w2 = w1 * sinf(36 * t + (2.0f*PI) / 3);"
    		+"        w3 = w1 * sinf(36 * t + 2 * (2.0f*PI) / 3);"
    		+"        w1 = w1 * sinf(36 * t);"
    		+"        break;"
    		+"      case 2:"
    		+"        w1 =  __spirograph3D_width  * RANDFLOAT() -  __spirograph3D_width  / 2;"
    		+"        w2 =  __spirograph3D_width  * RANDFLOAT() -  __spirograph3D_width  / 2;"
    		+"        w3 =  __spirograph3D_width  * RANDFLOAT() -  __spirograph3D_width  / 2;"
    		+"        break;"
    		+"      case 3:"
    		+"        w1 =  __spirograph3D_width  * (RANDFLOAT() + RANDFLOAT() + RANDFLOAT() + RANDFLOAT() - 2) / 2;"
    		+"        w2 =  __spirograph3D_width  * (RANDFLOAT() + RANDFLOAT() + RANDFLOAT() + RANDFLOAT() - 2) / 2;"
    		+"        w3 =  __spirograph3D_width  * (RANDFLOAT() + RANDFLOAT() + RANDFLOAT() + RANDFLOAT() - 2) / 2;"
    		+"        break;"
    		+"      case 4:"
    		+"        w1 = (RANDFLOAT() < 0.5) ?  __spirograph3D_width  : - __spirograph3D_width ;"
    		+"        w2 = w3 = 0;"
    		+"        break;"
    		+"      default:"
    		+"        w1 = w2 = w3 = 0;"
    		+"    }"
    		+"    float x1 = (  __spirograph3D_a  +  __spirograph3D_b  ) * cosf(t) -  __spirograph3D_c  * cosf((  __spirograph3D_a  +  __spirograph3D_b ) / __spirograph3D_b * t);"
    		+"    float y1 = (  __spirograph3D_a  +  __spirograph3D_b  ) * sinf(t) -  __spirograph3D_c  * sinf((  __spirograph3D_a  +  __spirograph3D_b ) / __spirograph3D_b * t);"
    		+"    float z1 =  __spirograph3D_c  * sinf((  __spirograph3D_a  +  __spirograph3D_b ) / __spirograph3D_b * t);"
    		+"    __px += __spirograph3D * (x1 + w1);"
    		+"    __py += __spirograph3D * (y1 + w2);"
    		+"    __pz += __spirograph3D * (z1 + w3);"
    		+"    if ( __spirograph3D_direct_color  != 0) {"
    		+"      __pal = fmodf(t / (2.0f*PI), 1);"
    		+"     }";
  }
}
