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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class CscSquaredFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_CSC_DIV = "csc_div";
  private static final String PARAM_COS_DIV = "cos_div";
  private static final String PARAM_TAN_DIV = "tan_div";
  private static final String PARAM_CSC_POW = "csc_pow";
  private static final String PARAM_PI_MULT = "pi_mult";
  private static final String PARAM_CSC_ADD = "csc_add";
  private static final String PARAM_SCALEY = "scale_y";

  private static final String[] paramNames = {PARAM_CSC_DIV, PARAM_COS_DIV, PARAM_TAN_DIV, PARAM_CSC_POW, PARAM_PI_MULT, PARAM_CSC_ADD, PARAM_SCALEY};
  private double csc_div = 1.0;
  private double cos_div = 1.0;
  private double tan_div = 1.0;
  private double csc_pow = 0.5;
  private double pi_mult = 0.5;
  private double csc_add = 0.25;
  private double scale_y = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // csc_squared by Whittaker Courtney 8-7-2018

    // x,y variables
    double x = pAffineTP.x;
    double y = pAffineTP.y;

    // final formulas
    // csc = 1 / cos(x) / tan(x);

    double csc = csc_div / cos(x / cos_div) / tan(x / tan_div);
    double fx = pow(csc * csc + (M_PI * pi_mult), csc_pow) + csc_add;

    pVarTP.x += pAmount * x * fx;
    pVarTP.y += pAmount * y * fx * scale_y;

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
    return new Object[]{csc_div, cos_div, tan_div, csc_pow, pi_mult, csc_add, scale_y};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_CSC_DIV.equalsIgnoreCase(pName))
      csc_div = pValue;
    else if (PARAM_COS_DIV.equalsIgnoreCase(pName))
      cos_div = pValue;
    else if (PARAM_TAN_DIV.equalsIgnoreCase(pName))
      tan_div = pValue;
    else if (PARAM_CSC_POW.equalsIgnoreCase(pName))
      csc_pow = pValue;
    else if (PARAM_PI_MULT.equalsIgnoreCase(pName))
      pi_mult = pValue;
    else if (PARAM_CSC_ADD.equalsIgnoreCase(pName))
      csc_add = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scale_y = pValue;
    else
      throw new IllegalArgumentException(pName);
  }


  @Override
  public String getName() {
    return "csc_squared";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "    float x = __x;"
    		+"    float y = __y;"
    		+"    float csc =  __csc_squared_csc_div  / cosf(x /  __csc_squared_cos_div ) / tanf(x /  __csc_squared_tan_div );"
    		+"    float fx = powf(csc * csc + (PI *  __csc_squared_pi_mult ),  __csc_squared_csc_pow ) +  __csc_squared_csc_add ;"
    		+"    __px += __csc_squared * x * fx;"
    		+"    __py += __csc_squared * y * fx * __csc_squared_scale_y;"
            + (context.isPreserveZCoordinate() ? "__pz += __csc_squared * __z;": "");
  }
}
