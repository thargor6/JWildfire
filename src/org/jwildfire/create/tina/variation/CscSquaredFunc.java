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

public class CscSquaredFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_CSC_DIV = "csc div";
  private static final String PARAM_COS_DIV = "cos div";
  private static final String PARAM_TAN_DIV = "tan div";
  private static final String PARAM_CSC_POW = "csc pow";
  private static final String PARAM_PI_MULT = "pi mult";
  private static final String PARAM_CSC_ADD = "csc add";
  private static final String PARAM_SCALEY = "scale y";

  private static final String[] paramNames = {PARAM_CSC_DIV, PARAM_COS_DIV, PARAM_TAN_DIV, PARAM_CSC_POW, PARAM_PI_MULT, PARAM_CSC_ADD, PARAM_SCALEY};
  private double csc_div = 1.0;
  private double cos_div = 1.0;
  private double tan_div = 1.0;
  private double csc_pow = 0.5;
  private double pi_mult = 0.5;
  private double csc_add = 0.25;
  private double scaley = 1;

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
    pVarTP.y += pAmount * y * fx * scaley;

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
    return new Object[]{csc_div, cos_div, tan_div, csc_pow, pi_mult, csc_add, scaley};
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
      scaley = pValue;
    else
      throw new IllegalArgumentException(pName);
  }


  @Override
  public String getName() {
    return "csc_squared";
  }

}
