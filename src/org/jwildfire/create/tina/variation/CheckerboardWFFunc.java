/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CheckerboardWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POSITION = "position";
  private static final String PARAM_SIZE = "size";
  private static final String PARAM_AXIS = "axis";
  private static final String PARAM_DISPL_AMOUNT = "displ_amount";
  private static final String PARAM_CHECKER_COLOR1 = "checker_color1";
  private static final String PARAM_CHECKER_COLOR2 = "checker_color2";
  private static final String PARAM_CHECKER_SIZE = "checker_size";

  private static final String[] paramNames = { PARAM_POSITION, PARAM_SIZE, PARAM_AXIS, PARAM_DISPL_AMOUNT, PARAM_CHECKER_COLOR1, PARAM_CHECKER_COLOR2, PARAM_CHECKER_SIZE };

  private static final int AXIS_XY = 0;
  private static final int AXIS_YZ = 1;
  private static final int AXIS_ZX = 2;

  private double position = 3.0;
  private double size = 10.0;
  private int axis = AXIS_ZX;
  private double displ_amount = 0.05;
  private double checker_color1 = Math.random() * 0.5;
  private double checker_color2 = Math.random() * 0.5 + 0.5;
  private double checker_size = 0.1 + Math.random() * 0.2;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = 0.0, y = 0.0, z = 0.0;
    switch (axis) {
      case AXIS_XY:
        x = 0.5 - pContext.random();
        y = 0.5 - pContext.random();
        z = position + getDisplacement(pVarTP, x, y);
        setColor(pVarTP, x, y);
        x *= size;
        y *= size;
        break;
      case AXIS_YZ:
        y = 0.5 - pContext.random();
        z = 0.5 - pContext.random();
        x = position + getDisplacement(pVarTP, y, z);
        setColor(pVarTP, y, z);
        y *= size;
        z *= size;
        break;
      case AXIS_ZX:
        x = 0.5 - pContext.random();
        z = 0.5 - pContext.random();
        y = position + getDisplacement(pVarTP, z, x);
        setColor(pVarTP, z, x);
        x *= size;
        z *= size;
        break;
      default: // nothing to do
        break;
    }

    pVarTP.x += pAmount * x;
    pVarTP.y += pAmount * y;
    pVarTP.z += pAmount * z;
  }

  private void setColor(XYZPoint pVarTP, double u, double v) {
    pVarTP.color = MathLib.fmod(MathLib.floor((u + 0.5) / checker_size) + MathLib.floor((v + 0.5) / checker_size), 2) < 1 ? checker_color1 : checker_color2;
    if (pVarTP.color < 0.0)
      pVarTP.color = 0.0;
    else if (pVarTP.color > 1.0)
      pVarTP.color = 1.0;
  }

  private double getDisplacement(XYZPoint pVarTP, double u, double v) {
    return MathLib.fmod(MathLib.floor((u + 0.5) / checker_size) + MathLib.floor((v + 0.5) / checker_size), 2) < 1 ? displ_amount : 0.0;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { position, size, axis, displ_amount, checker_color1, checker_color2, checker_size };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POSITION.equalsIgnoreCase(pName)) {
      position = pValue;
    }
    else if (PARAM_SIZE.equalsIgnoreCase(pName)) {
      size = pValue;
    }
    else if (PARAM_AXIS.equalsIgnoreCase(pName)) {
      axis = limitIntVal(Tools.FTOI(pValue), AXIS_XY, AXIS_ZX);
    }
    else if (PARAM_DISPL_AMOUNT.equalsIgnoreCase(pName)) {
      displ_amount = pValue;
    }
    else if (PARAM_CHECKER_COLOR1.equalsIgnoreCase(pName)) {
      checker_color1 = limitVal(pValue, 0.0, 1.0);
    }
    else if (PARAM_CHECKER_COLOR2.equalsIgnoreCase(pName)) {
      checker_color2 = limitVal(pValue, 0.0, 1.0);
    }
    else if (PARAM_CHECKER_SIZE.equalsIgnoreCase(pName)) {
      checker_size = pValue;
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "checkerboard_wf";
  }

}
