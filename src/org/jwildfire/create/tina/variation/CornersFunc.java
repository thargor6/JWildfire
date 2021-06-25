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

import static org.jwildfire.base.mathlib.MathLib.log;
import static org.jwildfire.base.mathlib.MathLib.pow;


public class CornersFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";
  private static final String PARAM_MULT_X = "mult_x";
  private static final String PARAM_MULT_Y = "mult_y";
  private static final String PARAM_X_POWER = "x_power";
  private static final String PARAM_Y_POWER = "y_power";
  private static final String PARAM_XY_POWER_ADD = "xy_power_add";
  private static final String PARAM_LOGMODE = "log_mode";
  private static final String PARAM_LOG_BASE = "log_base";

  private static final String[] paramNames = {PARAM_X, PARAM_Y, PARAM_MULT_X, PARAM_MULT_Y, PARAM_X_POWER, PARAM_Y_POWER, PARAM_XY_POWER_ADD, PARAM_LOGMODE, PARAM_LOG_BASE};
  private double x = 1.0;
  private double y = 1.0;
  private double mult_x = 1.0;
  private double mult_y = 1.0;
  private double x_power = 0.75;
  private double y_power = 0.75;
  private double xy_power_add = 0;
  private double log_mode = 0;
  private double log_base = 2.71828;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // corners by Whittaker Courtney 8-14-2018

    double xs = pAffineTP.x * pAffineTP.x;
    double ys = pAffineTP.y * pAffineTP.y;

    double ex, ey;
    //log mode
    if (log_mode == 0) {
      ex = pow(xs, x_power + xy_power_add) * mult_x;
      ey = pow(ys, y_power + xy_power_add) * mult_y;
    } else {
      ex = pow(log((xs * mult_x) + 3) / log(log_base), x_power + 2.25 + xy_power_add) - 1.33;
      ey = pow(log((ys * mult_y) + 3) / log(log_base), y_power + 2.25 + xy_power_add) - 1.33;
    }


    if (pAffineTP.x > 0) {
      pVarTP.x += pAmount * ex + x;
    } else {
      pVarTP.x += pAmount * -ex - x;
    }

    if (pAffineTP.y > 0) {
      pVarTP.y += pAmount * ey + y;
    } else {
      pVarTP.y += pAmount * -ey - y;
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
    return new Object[]{x, y, mult_x, mult_y, x_power, y_power, xy_power_add, log_mode, log_base};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else if (PARAM_MULT_X.equalsIgnoreCase(pName))
      mult_x = pValue;
    else if (PARAM_MULT_Y.equalsIgnoreCase(pName))
      mult_y = pValue;
    else if (PARAM_X_POWER.equalsIgnoreCase(pName))
      x_power = pValue;
    else if (PARAM_Y_POWER.equalsIgnoreCase(pName))
      y_power = pValue;
    else if (PARAM_XY_POWER_ADD.equalsIgnoreCase(pName))
      xy_power_add = pValue;
    else if (PARAM_LOGMODE.equalsIgnoreCase(pName))
      log_mode = pValue;
    else if (PARAM_LOG_BASE.equalsIgnoreCase(pName))
      log_base = pValue;
    else
      throw new IllegalArgumentException(pName);
  }


  @Override
  public String getName() {
    return "corners";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float xs = __x * __x;\n"
        + "    float ys = __y * __y;\n"
        + "\n"
        + "    float ex, ey;\n"
        + "    if (varpar->corners_log_mode == 0) {\n"
        + "      ex = powf(xs, varpar->corners_x_power + varpar->corners_xy_power_add) * varpar->corners_mult_x;\n"
        + "      ey = powf(ys, varpar->corners_y_power + varpar->corners_xy_power_add) * varpar->corners_mult_y;\n"
        + "    } else {\n"
        + "      ex = powf(logf((xs * varpar->corners_mult_x) + 3) / logf(varpar->corners_log_base), varpar->corners_x_power + 2.25 + varpar->corners_xy_power_add) - 1.33;\n"
        + "      ey = powf(logf((ys * varpar->corners_mult_y) + 3) / logf(varpar->corners_log_base), varpar->corners_y_power + 2.25 + varpar->corners_xy_power_add) - 1.33;\n"
        + "    }\n"
        + "\n"
        + "\n"
        + "    if (__x > 0) {\n"
        + "      __px += varpar->corners * ex + varpar->corners_x;\n"
        + "    } else {\n"
        + "      __px += varpar->corners * -ex - varpar->corners_x;\n"
        + "    }\n"
        + "\n"
        + "    if (__y > 0) {\n"
        + "      __py += varpar->corners * ey + varpar->corners_y;\n"
        + "    } else {\n"
        + "      __py += varpar->corners * -ey - varpar->corners_y;\n"
        + "    }\n"
        + (context.isPreserveZCoordinate() ? "      __pz += varpar->corners * __z;\n" : "");
  }
}
