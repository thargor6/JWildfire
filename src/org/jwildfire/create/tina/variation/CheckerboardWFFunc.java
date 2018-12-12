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
import org.jwildfire.create.tina.base.Layer;
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
  private static final String PARAM_SIDE_COLOR = "side_color";
  private static final String PARAM_CHECKER_SIZE = "checker_size";
  private static final String PARAM_WITH_SIDES = "with_sides";

  private static final String[] paramNames = {PARAM_POSITION, PARAM_SIZE, PARAM_AXIS, PARAM_CHECKER_SIZE, PARAM_DISPL_AMOUNT, PARAM_CHECKER_COLOR1, PARAM_CHECKER_COLOR2, PARAM_SIDE_COLOR, PARAM_WITH_SIDES};

  private static final int AXIS_XY = 0;
  private static final int AXIS_YZ = 1;
  private static final int AXIS_ZX = 2;

  private double position = 3.0;
  private double size = 10.0;
  private int axis = AXIS_ZX;
  private double displ_amount = 0.05;
  private double checker_color1 = Math.random() * 0.5;
  private double checker_color2 = Math.random() * 0.5 + 0.5;
  private double side_color = Math.random() * 0.75;
  private double checker_size = 0.1 + Math.random() * 0.2;
  private int with_sides = 1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x, y, z;
    if (with_sides == 0 || MathLib.fabs(displ_amount) < MathLib.EPSILON) {
      x = pContext.random();
      y = pContext.random();
      z = getDisplacement(x, y);
      pVarTP.color = getColor(x, y);
    } else {
      if (_max_checks > 0 && pContext.random() < _side_prob) {
        pVarTP.color = side_color;
        if (pContext.random() < 0.5) {
          x = pContext.random(_max_checks + 1) * checker_size;
          y = pContext.random();
        } else {
          x = pContext.random();
          y = pContext.random(_max_checks + 1) * checker_size;
        }
        z = displ_amount * pContext.random();
      } else {
        x = pContext.random();
        y = pContext.random();
        z = getDisplacement(x, y);
        pVarTP.color = getColor(x, y);
      }
    }

    x = (x - 0.5) * size;
    y = (y - 0.5) * size;
    z = z * size + position;

    switch (axis) {
      case AXIS_XY:
        pVarTP.x += pAmount * x;
        pVarTP.y += pAmount * y;
        pVarTP.z += pAmount * z;
        break;
      case AXIS_YZ:
        pVarTP.y += pAmount * x;
        pVarTP.z += pAmount * y;
        pVarTP.x += pAmount * z;
        break;
      default:
      case AXIS_ZX:
        pVarTP.z += pAmount * x;
        pVarTP.x += pAmount * y;
        pVarTP.y += pAmount * z;
        break;
    }
  }

  private double getColor(double u, double v) {
    double color = MathLib.fmod(MathLib.floor(u / checker_size) + MathLib.floor(v / checker_size), 2) < 1 ? checker_color1 : checker_color2;
    if (color < 0.0)
      color = 0.0;
    else if (color > 1.0)
      color = 1.0;
    return color;
  }

  private double getDisplacement(double u, double v) {
    return MathLib.fmod(MathLib.floor(u / checker_size) + MathLib.floor(v / checker_size), 2) < 1 ? displ_amount : 0.0;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{position, size, axis, checker_size, displ_amount, checker_color1, checker_color2, side_color, with_sides};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POSITION.equalsIgnoreCase(pName)) {
      position = pValue;
    } else if (PARAM_SIZE.equalsIgnoreCase(pName)) {
      size = pValue;
    } else if (PARAM_AXIS.equalsIgnoreCase(pName)) {
      axis = limitIntVal(Tools.FTOI(pValue), AXIS_XY, AXIS_ZX);
    } else if (PARAM_DISPL_AMOUNT.equalsIgnoreCase(pName)) {
      displ_amount = pValue;
    } else if (PARAM_CHECKER_COLOR1.equalsIgnoreCase(pName)) {
      checker_color1 = limitVal(pValue, 0.0, 1.0);
    } else if (PARAM_CHECKER_COLOR2.equalsIgnoreCase(pName)) {
      checker_color2 = limitVal(pValue, 0.0, 1.0);
    } else if (PARAM_SIDE_COLOR.equalsIgnoreCase(pName)) {
      side_color = limitVal(pValue, 0.0, 1.0);
    } else if (PARAM_CHECKER_SIZE.equalsIgnoreCase(pName)) {
      checker_size = pValue;
    } else if (PARAM_WITH_SIDES.equalsIgnoreCase(pName)) {
      with_sides = limitIntVal(Tools.FTOI(pValue), 0, 1);
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "checkerboard_wf";
  }

  private double _side_prob;
  private int _max_checks;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    double side_area = 4.0 * displ_amount;
    _side_prob = side_area / (1.0 + side_area);
    _max_checks = Tools.FTOI(1.0 / checker_size);
    if ((_max_checks) * checker_size >= 1.0) {
      _max_checks--;
    }
  }

}
