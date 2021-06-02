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

public class Atan2_SpiralsFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_R_MULT = "r_mult";
  private static final String PARAM_R_ADD = "r_add";
  private static final String PARAM_XY2_MULT = "xy2_mult";
  private static final String PARAM_XY2_ADD = "xy2_add";
  private static final String PARAM_X_MULT = "x_mult";
  private static final String PARAM_X_ADD = "x_add";
  private static final String PARAM_YX_DIV = "yx_div";
  private static final String PARAM_YX_ADD = "yx_add";
  private static final String PARAM_YY_DIV = "yy_div";
  private static final String PARAM_YY_ADD = "yy_add";
  private static final String PARAM_SIN_ADD = "sin_add";
  private static final String PARAM_Y_MULT = "y_mult";
  private static final String PARAM_R_POWER = "r_power";
  private static final String PARAM_X2Y2_POW = "x2y2_power";

  private static final String[] paramNames = {PARAM_R_MULT, PARAM_R_ADD, PARAM_XY2_MULT, PARAM_XY2_ADD, PARAM_X_MULT, PARAM_X_ADD, PARAM_YX_DIV, PARAM_YX_ADD, PARAM_YY_DIV, PARAM_YY_ADD, PARAM_SIN_ADD, PARAM_Y_MULT, PARAM_R_POWER, PARAM_X2Y2_POW};
  private double r_mult = 1.5;
  private double r_add = 1;
  private double xy2_mult = 1.125;
  private double xy2_add = 0.132;
  private double x_mult = 2;
  private double x_add = 0.25;
  private double yx_div = 1;
  private double yx_add = 0;
  private double yy_div = 1.25;
  private double yy_add = 0;
  private double sin_add = 0;
  private double y_mult = 1;
  private double r_power = 0.5;
  private double x2y2_power = 1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    //Atan2_Spirals by Whittaker Courtney 8-8-2018

    // x,y variables
    double x = pAffineTP.x;
    double y = pAffineTP.y;
    double xs = x * x;
    double ys = y * y;

    // final formulas
    double xy2 = pow(xs + ys, x2y2_power);
    double r = pow(xs + ys, r_power);

    double fx = x_mult * atan2(r * r_mult + r_add, xy2 * xy2_mult + xy2_add) + x_add;
    double fy = sin(atan2(y / yy_div + yy_add, (x / yx_div) + yx_add) + sin_add) * y_mult;

    if (x >= 0) {
      pVarTP.x += pAmount * (-fx + M_PI);
    } else {
      pVarTP.x += pAmount * (fx - M_PI);
    }

    pVarTP.y += pAmount * fy;


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
    return new Object[]{r_mult, r_add, xy2_mult, xy2_add, x_mult, x_add, yx_div, yx_add, yy_div, yy_add, sin_add, y_mult, r_power, x2y2_power};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_R_MULT.equalsIgnoreCase(pName))
      r_mult = pValue;
    else if (PARAM_R_ADD.equalsIgnoreCase(pName))
      r_add = pValue;
    else if (PARAM_XY2_MULT.equalsIgnoreCase(pName))
      xy2_mult = pValue;
    else if (PARAM_XY2_ADD.equalsIgnoreCase(pName))
      xy2_add = pValue;
    else if (PARAM_X_MULT.equalsIgnoreCase(pName))
      x_mult = pValue;
    else if (PARAM_X_ADD.equalsIgnoreCase(pName))
      x_add = pValue;
    else if (PARAM_YX_DIV.equalsIgnoreCase(pName))
      yx_div = pValue;
    else if (PARAM_YX_ADD.equalsIgnoreCase(pName))
      yx_add = pValue;
    else if (PARAM_YY_DIV.equalsIgnoreCase(pName))
      yy_div = pValue;
    else if (PARAM_YY_ADD.equalsIgnoreCase(pName))
      yy_add = pValue;
    else if (PARAM_SIN_ADD.equalsIgnoreCase(pName))
      sin_add = pValue;
    else if (PARAM_Y_MULT.equalsIgnoreCase(pName))
      y_mult = pValue;
    else if (PARAM_R_POWER.equalsIgnoreCase(pName))
      r_power = pValue;
    else if (PARAM_X2Y2_POW.equalsIgnoreCase(pName))
      x2y2_power = pValue;
    else
      throw new IllegalArgumentException(pName);
  }


  @Override
  public String getName() {
    return "atan2_spirals";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float x = __x;\n"
        + "    float y = __y;\n"
        + "    float xs = x * x;\n"
        + "    float ys = y * y;\n"
        + "    float xy2 = powf(xs + ys, varpar->atan2_spirals_x2y2_power);\n"
        + "    float r = powf(xs + ys, varpar->atan2_spirals_r_power);\n"
        + "\n"
        + "    float fx = varpar->atan2_spirals_x_mult * atan2f(r * varpar->atan2_spirals_r_mult + varpar->atan2_spirals_r_add, xy2 * varpar->atan2_spirals_xy2_mult + varpar->atan2_spirals_xy2_add) + varpar->atan2_spirals_x_add;\n"

        + "    float fy = sinf(atan2f(y / varpar->atan2_spirals_yy_div + varpar->atan2_spirals_yy_add, (x / varpar->atan2_spirals_yx_div) + varpar->atan2_spirals_yx_add) + varpar->atan2_spirals_sin_add) * varpar->atan2_spirals_y_mult;\n"
        + "\n"
        + "    if (x >= 0) {\n"
        + "      __px += varpar->atan2_spirals * (-fx + PI);\n"
        + "    } else {\n"
        + "      __px += varpar->atan2_spirals * (fx - PI);\n"
        + "    }\n"
        + "\n"
        + "    __py += varpar->atan2_spirals * fy;\n"
        + (context.isPreserveZCoordinate() ? "__pz += varpar->atan2_spirals * __z;\n" : "");
  }
}
