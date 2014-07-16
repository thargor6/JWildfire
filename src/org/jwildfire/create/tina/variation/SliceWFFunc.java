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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class SliceWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final int AXIS_X = 0;
  private static final int AXIS_Y = 1;
  private static final int AXIS_Z = 2;

  private static final String PARAM_POSITION = "position";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_INVERT = "invert";
  private static final String PARAM_AXIS = "axis";
  private static final String PARAM_DIRECT_COLOR = "direct_color";
  private static final String PARAM_DC_RED = "dc_red";
  private static final String PARAM_DC_GREEN = "dc_green";
  private static final String PARAM_DC_BLUE = "dc_blue";
  private static final String PARAM_HIDE_OUTSIDE = "hide_outside";

  private static final String[] paramNames = { PARAM_POSITION, PARAM_THICKNESS, PARAM_INVERT, PARAM_AXIS, PARAM_DIRECT_COLOR, PARAM_DC_RED, PARAM_DC_GREEN, PARAM_DC_BLUE, PARAM_HIDE_OUTSIDE };

  private double position = 0.0;
  private double thickness = 0.01;
  private int invert = 0;
  private int axis = AXIS_Z;
  private int direct_color = 0;
  private int dc_red = 255;
  private int dc_green = 0;
  private int dc_blue = 0;
  private int hide_outside = 1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double value;
    switch (axis) {
      case AXIS_X:
        value = pVarTP.x;
        break;
      case AXIS_Y:
        value = pVarTP.y;
        break;
      default:
        value = pVarTP.z;
    }
    boolean dontPlot = pAmount >= 0 && (((invert == 0) && (value < _min || value > _max)) || ((invert == 1) && !(value < _min || value > _max)));
    if (!dontPlot && direct_color == 1) {
      pVarTP.rgbColor = true;
      pVarTP.redColor = dc_red;
      pVarTP.greenColor = dc_green;
      pVarTP.blueColor = dc_blue;
    }
    if (dontPlot && hide_outside == 1) {
      pVarTP.dontPlot = true;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { position, thickness, invert, axis, direct_color, dc_red, dc_green, dc_blue, hide_outside };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POSITION.equalsIgnoreCase(pName))
      position = pValue;
    else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
      thickness = pValue;
    else if (PARAM_INVERT.equalsIgnoreCase(pName))
      invert = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_AXIS.equalsIgnoreCase(pName))
      axis = limitIntVal(Tools.FTOI(pValue), AXIS_X, AXIS_Z);
    else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName))
      direct_color = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_DC_RED.equalsIgnoreCase(pName))
      dc_red = limitIntVal(Tools.FTOI(pValue), 0, Integer.MAX_VALUE);
    else if (PARAM_DC_GREEN.equalsIgnoreCase(pName))
      dc_green = limitIntVal(Tools.FTOI(pValue), 0, Integer.MAX_VALUE);
    else if (PARAM_DC_BLUE.equalsIgnoreCase(pName))
      dc_blue = limitIntVal(Tools.FTOI(pValue), 0, Integer.MAX_VALUE);
    else if (PARAM_HIDE_OUTSIDE.equalsIgnoreCase(pName))
      hide_outside = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "slice_wf";
  }

  private double _min, _max;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _min = position - 0.5 * thickness;
    _max = position + 0.5 * thickness;
  }

  @Override
  public int getPriority() {
    return 1;
  }

}
