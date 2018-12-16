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

public class Inverted_JuliaFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";
  private static final String PARAM_Y2_MULT = "y2 mult";
  private static final String PARAM_A2X_MULT = "a2x mult";
  private static final String PARAM_A2Y_MULT = "a2y mult";
  private static final String PARAM_A2Y_ADD = "a2y add";
  private static final String PARAM_COS_MULT = "cos mult";
  private static final String PARAM_Y_MULT = "y mult";
  private static final String PARAM_CENTER = "center";
  private static final String PARAM_X2Y2_ADD = "x2y2 add";

  private static final String[] paramNames = {PARAM_POWER, PARAM_Y2_MULT, PARAM_A2X_MULT, PARAM_A2Y_MULT, PARAM_A2Y_ADD, PARAM_COS_MULT, PARAM_Y_MULT, PARAM_CENTER, PARAM_X2Y2_ADD};
  private double power = 0.25;
  private double y2_mult = 1;
  private double a2x_mult = 1;
  private double a2y_mult = 1;
  private double a2y_add = 0;
  private double cos_mult = 0;
  private double y_mult = 1;
  private double center = 3.14;
  private double x2y2_add = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    //Inverted_Julia by Whittaker Courtney 12-11-2018
    //Based on the Julia variation but with an adjustable inward center and variables.

    double x = pAffineTP.x;
    double y = pAffineTP.y;
    double xs = x * x;
    double ys = y * y;
    double z = pow(xs + (ys * y2_mult), power) + x2y2_add;

    double q = atan2(x * a2x_mult, y * a2y_mult + a2y_add) * 0.5 + M_PI * (int) (2 * pContext.random());

    pVarTP.x += pAmount * cos(z * cos_mult) * (sin(q) / z / center);
    pVarTP.y += pAmount * cos(z * cos_mult) * (cos(q) / z / center) * y_mult;

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
    return new Object[]{power, y2_mult, a2x_mult, a2y_mult, a2y_add, cos_mult, y_mult, center, x2y2_add};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else if (PARAM_Y2_MULT.equalsIgnoreCase(pName))
      y2_mult = pValue;
    else if (PARAM_A2X_MULT.equalsIgnoreCase(pName))
      a2x_mult = pValue;
    else if (PARAM_A2Y_MULT.equalsIgnoreCase(pName))
      a2y_mult = pValue;
    else if (PARAM_A2Y_ADD.equalsIgnoreCase(pName))
      a2y_add = pValue;
    else if (PARAM_COS_MULT.equalsIgnoreCase(pName))
      cos_mult = pValue;
    else if (PARAM_Y_MULT.equalsIgnoreCase(pName))
      y_mult = pValue;
    else if (PARAM_CENTER.equalsIgnoreCase(pName))
      center = pValue;
    else if (PARAM_X2Y2_ADD.equalsIgnoreCase(pName))
      x2y2_add = pValue;
    else
      throw new IllegalArgumentException(pName);
  }


  @Override
  public String getName() {
    return "inverted_julia";
  }

}
