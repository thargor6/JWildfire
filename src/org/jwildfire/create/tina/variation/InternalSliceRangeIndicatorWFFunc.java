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
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class InternalSliceRangeIndicatorWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_POSITION_1 = "position_1";
  private static final String PARAM_DC_RED_1 = "dc_red_1";
  private static final String PARAM_DC_GREEN_1 = "dc_green_1";
  private static final String PARAM_DC_BLUE_1 = "dc_blue_1";
  private static final String PARAM_POSITION_2 = "position_2";
  private static final String PARAM_DC_RED_2 = "dc_red_2";
  private static final String PARAM_DC_GREEN_2 = "dc_green_2";
  private static final String PARAM_DC_BLUE_2 = "dc_blue_2";
  private static final String PARAM_RANDOM_FILL = "random_fill";

  private static final String[] paramNames = {PARAM_THICKNESS, PARAM_POSITION_1, PARAM_DC_RED_1, PARAM_DC_GREEN_1, PARAM_DC_BLUE_1, PARAM_POSITION_2, PARAM_DC_RED_2, PARAM_DC_GREEN_2, PARAM_DC_BLUE_2, PARAM_RANDOM_FILL};

  private double thickness = 0.01;
  private double position_1 = 0.0;
  private int dc_red_1 = 255;
  private int dc_green_1 = 0;
  private int dc_blue_1 = 0;
  private double position_2 = 0.0;
  private int dc_red_2 = 255;
  private int dc_green_2 = 0;
  private int dc_blue_2 = 0;
  private int random_fill = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double value = pVarTP.z;
    boolean inside_1 = value >= _min_1 && value <= _max_1;
    if (inside_1) {
      pVarTP.rgbColor = true;
      pVarTP.redColor = dc_red_1 * 4;
      pVarTP.greenColor = dc_green_1 * 4;
      pVarTP.blueColor = dc_blue_1 * 4;
      return;
    }
    boolean inside_2 = value >= _min_2 && value <= _max_2;
    if (inside_2) {
      pVarTP.rgbColor = true;
      pVarTP.redColor = dc_red_2 * 4;
      pVarTP.greenColor = dc_green_2 * 4;
      pVarTP.blueColor = dc_blue_2 * 4;
      return;
    }
    if (random_fill == 1 && pContext.random() > 0.75) {
      pVarTP.z = _min_1;
      double a = M_2PI * pContext.random();
      double sina = sin(a);
      double cosa = cos(a);
      double newx = sina * pVarTP.x + cosa * pVarTP.y;
      double newy = cosa * pVarTP.x - sina * pVarTP.y;
      pVarTP.x = newx;
      pVarTP.y = newy;
      pVarTP.rgbColor = true;
      pVarTP.redColor = dc_red_1;
      pVarTP.greenColor = dc_green_1;
      pVarTP.blueColor = dc_blue_1;
      return;
    }
    if (random_fill == 1 && pContext.random() > 0.75) {
      pVarTP.z = _min_2;
      double a = M_2PI * pContext.random();
      double sina = sin(a);
      double cosa = cos(a);
      double newx = sina * pVarTP.x + cosa * pVarTP.y;
      double newy = cosa * pVarTP.x - sina * pVarTP.y;
      pVarTP.x = newx;
      pVarTP.y = newy;
      pVarTP.rgbColor = true;
      pVarTP.redColor = dc_red_2;
      pVarTP.greenColor = dc_green_2;
      pVarTP.blueColor = dc_blue_2;
      return;
    }

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{thickness, position_1, dc_red_1, dc_green_1, dc_blue_1, position_2, dc_red_2, dc_green_2, dc_blue_2, random_fill};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_THICKNESS.equalsIgnoreCase(pName))
      thickness = pValue;
    else if (PARAM_POSITION_1.equalsIgnoreCase(pName))
      position_1 = pValue;
    else if (PARAM_DC_RED_1.equalsIgnoreCase(pName))
      dc_red_1 = limitIntVal(Tools.FTOI(pValue), 0, Integer.MAX_VALUE);
    else if (PARAM_DC_GREEN_1.equalsIgnoreCase(pName))
      dc_green_1 = limitIntVal(Tools.FTOI(pValue), 0, Integer.MAX_VALUE);
    else if (PARAM_DC_BLUE_1.equalsIgnoreCase(pName))
      dc_blue_1 = limitIntVal(Tools.FTOI(pValue), 0, Integer.MAX_VALUE);
    else if (PARAM_POSITION_2.equalsIgnoreCase(pName))
      position_2 = pValue;
    else if (PARAM_DC_RED_2.equalsIgnoreCase(pName))
      dc_red_2 = limitIntVal(Tools.FTOI(pValue), 0, Integer.MAX_VALUE);
    else if (PARAM_DC_GREEN_2.equalsIgnoreCase(pName))
      dc_green_2 = limitIntVal(Tools.FTOI(pValue), 0, Integer.MAX_VALUE);
    else if (PARAM_DC_BLUE_2.equalsIgnoreCase(pName))
      dc_blue_2 = limitIntVal(Tools.FTOI(pValue), 0, Integer.MAX_VALUE);
    else if (PARAM_RANDOM_FILL.equalsIgnoreCase(pName))
      random_fill = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "_slice_indicator_wf";
  }

  private double _min_1, _max_1;
  private double _min_2, _max_2;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    double ath = MathLib.fabs(thickness);
    _min_1 = position_1 - 0.5 * ath;
    _max_1 = position_1 + 0.5 * ath;
    _min_2 = position_2 - 0.5 * ath;
    _max_2 = position_2 + 0.5 * ath;
  }

  @Override
  public int getPriority() {
    return 1;
  }

}
