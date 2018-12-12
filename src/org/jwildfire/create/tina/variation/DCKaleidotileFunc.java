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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class DCKaleidotileFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PULL = "pull";
  private static final String PARAM_ROTATE = "rotate";
  private static final String PARAM_LINE_UP = "line_up";
  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";
  private static final String PARAM_COLOR1 = "color1";
  private static final String PARAM_SPEED1 = "speed1";
  private static final String PARAM_COLOR2 = "color2";
  private static final String PARAM_SPEED2 = "speed2";

  private static final String[] paramNames = {PARAM_PULL, PARAM_ROTATE, PARAM_LINE_UP, PARAM_X, PARAM_Y,
          PARAM_COLOR1, PARAM_SPEED1, PARAM_COLOR2, PARAM_SPEED2};
  private double pull = -1.5;
  private double rotate = 0.0;
  private double line_up = 1.0;

  private double x = 0.0;
  private double y = 0.0;

  private double color1 = 0;
  private double color2 = 1;
  private double speed1 = 0;
  private double speed2 = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
          /*
           * Used Kaleidoscope by Will Evans,
           * http://eevans1.deviantart.com/art/kaleidoscope-plugin-122185469 that mirrors
           * 2 of the variations to create tiles by Brad Stefanov
           */
                        double pAmount) {
    if (pContext.random() < 0.5) {

      double xin = pAffineTP.x * 0.831;
      double yin = pAffineTP.y * 0.831;
      pVarTP.x += (((_w * xin) * cos(45.0) - yin * sin(45.0) + _e) + _r);
      // the if function splits the plugin in two.
      if (yin > 0) {
        pVarTP.y += (((_w * yin) * cos(45.0) + xin * sin(45.0) + _q + _e) + _t);
      } else {
        pVarTP.y += ((_w * yin) * cos(45.0) + xin * sin(45.0) - _q - _e);
      }
      newColor = oldColor * c11 + c12;
    } else {

      double xin = pAffineTP.x * 0.831;
      double yin = pAffineTP.y * 0.831;
      pVarTP.x += (((_w1 * xin) * cos(45.0) - yin * sin(45.0) + _e1) + _r1);
      // the if function splits the plugin in two.
      if (yin > 0) {
        pVarTP.y += (((_w1 * yin) * cos(45.0) + xin * sin(45.0) + _q1 + _e1) + _t1);
      } else {
        pVarTP.y += ((_w1 * yin) * cos(45.0) + xin * sin(45.0) - _q1 - _e1);
      }
      newColor = oldColor * c21 + c22;
    }
    pVarTP.color = fmod(newColor, 1);
    oldColor = newColor;


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
    return new Object[]{pull, rotate, line_up, x, y, color1, speed1, color2, speed2,};
  }

  @Override
  public void setParameter(String pName, double pValue) {

    if (PARAM_PULL.equalsIgnoreCase(pName))
      pull = pValue;
    else if (PARAM_ROTATE.equalsIgnoreCase(pName))
      rotate = pValue;
    else if (PARAM_LINE_UP.equalsIgnoreCase(pName))
      line_up = pValue;
    else if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else if (PARAM_COLOR1.equalsIgnoreCase(pName))
      color1 = pValue;
    else if (PARAM_SPEED1.equalsIgnoreCase(pName))
      speed1 = pValue;
    else if (PARAM_COLOR2.equalsIgnoreCase(pName))
      color2 = pValue;
    else if (PARAM_SPEED2.equalsIgnoreCase(pName))
      speed2 = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "dc_kaleidotile";
  }

  double _q, _q1, _w, _w1, _e, _e1, _r, _r1, _t, _t1, _i, c11, c12, c21, c22, oldColor, newColor;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _q = pull; // pulls apart the 2 sections of the plugin
    _q1 = -pull; // pulls apart the 2 sections of the plugin
    _w = rotate; // rotates both halves of the plugin
    _w1 = -rotate; // rotates both halves of the plugin
    _e = line_up;
    _e1 = -line_up;
    _r = x; // changes x co-ordinates
    _r1 = -x; // changes x co-ordinates
    _t = y; // changes y co-ordinates for 1 part of the plugin
    _t1 = -y; // changes y co-ordinates for 1 part of the plugin
    c11 = (1 + speed1) / 2;
    c12 = color1 * (1 - speed1) / 2;
    c21 = (1 + speed2) / 2;
    c22 = color2 * (1 - speed2) / 2;

    oldColor = 0.5;
  }

}
