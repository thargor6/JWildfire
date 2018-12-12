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

/**
 * Double modulus IFS
 *
 * @author Jesus Sosa
 * @date January 18, 2018
 * based on modulus used as a IFS:
 * <p>
 * Modifications by Brad Stefanov and Rick Sidwell
 */
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.fmod;

public class DCDModulusFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";
  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_SHIFTX = "shiftx";
  private static final String PARAM_SHIFTY = "shifty";
  private static final String PARAM_COLOR1 = "color1";
  private static final String PARAM_SPEED1 = "speed1";
  private static final String PARAM_COLOR2 = "color2";
  private static final String PARAM_SPEED2 = "speed2";
  private static final String PARAM_SQUARE = "square";

  private static final String[] paramNames = {PARAM_X, PARAM_Y, PARAM_ANGLE, PARAM_SHIFTX, PARAM_SHIFTY,
          PARAM_COLOR1, PARAM_SPEED1, PARAM_COLOR2, PARAM_SPEED2, PARAM_SQUARE};

  private double x = 2.0;
  private double y = 2.0;
  private double shiftx = 0.0;
  private double shifty = 0.0;
  private double color1 = 0;
  private double color2 = 1;
  private double speed1 = 0;
  private double speed2 = 0;
  private double angle = 45.0;
  private int square = 1;
  private double coscos, sincos, sinsin, rangle;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
                        double pAmount) {
    double xnew, ynew, xt, yt;

    double p2 = coscos / (coscos + sinsin);
    double p1 = sinsin / (coscos + sinsin);

    if (pAffineTP.x > x) {
      xnew = (-x + fmod(pAffineTP.x + x, _xr));
    } else if (pAffineTP.x < -x) {
      xnew = (x - fmod(x - pAffineTP.x, _xr));
    } else {
      xnew = pAffineTP.x;
    }

    if (pAffineTP.y > y) {
      ynew = (-y + fmod(pAffineTP.y + y, _yr));
    } else if (pAffineTP.y < -y) {
      ynew = (y - fmod(y - pAffineTP.y, _yr));
    } else {
      ynew = pAffineTP.y;
    }

    double p = pContext.random();

    if (angle <= 45.0) {
      if (p < p2) {
        xt = xnew * coscos + ynew * sincos + x;
        yt = -xnew * sincos + ynew * coscos - y;
        newColor = oldColor * c11 + c12;
      } else {
        xt = xnew * sincos + ynew * sinsin;
        yt = -xnew * sinsin + ynew * sincos;
        newColor = oldColor * c21 + c22;
      }
    } else {
      if (p < p1) {
        xt = xnew * sincos + ynew * sinsin;
        yt = -xnew * sinsin + ynew * sincos;
        newColor = oldColor * c21 + c22;
      } else {
        xt = xnew * coscos + ynew * sincos + x;
        yt = -xnew * sincos + ynew * coscos - y;
        newColor = oldColor * c11 + c12;
      }
    }

    xt += shiftx;
    yt += shifty;

    if (square == 0) {
      pVarTP.x += pAmount * xt;
      pVarTP.y += pAmount * yt;
    } else {
      if (xt > x) {
        pVarTP.x += pAmount * (-x + fmod(xt + x, _xr));
      } else if (xt < -x) {
        pVarTP.x += pAmount * (x - fmod(x - xt, _xr));
      } else {
        pVarTP.x += pAmount * xt;
      }

      if (yt > y) {
        pVarTP.y += pAmount * (-y + fmod(yt + y, _yr));
      } else if (yt < -y) {
        pVarTP.y += pAmount * (y - fmod(y - yt, _yr));
      } else {
        pVarTP.y += pAmount * yt;
      }
    }

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

    pVarTP.color = fmod(newColor, 1);
    oldColor = newColor;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{x, y, angle, shiftx, shifty, color1, speed1, color2, speed2, square};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else if (PARAM_ANGLE.equalsIgnoreCase(pName)) {
      angle = pValue;
      rangle = MathLib.M_PI * angle / 180.0;
      coscos = MathLib.cos(rangle) * MathLib.cos(rangle);
      sinsin = MathLib.sin(rangle) * MathLib.sin(rangle);
      sincos = MathLib.sin(rangle) * MathLib.cos(rangle);
    } else if (PARAM_SHIFTX.equalsIgnoreCase(pName))
      shiftx = pValue;
    else if (PARAM_SHIFTY.equalsIgnoreCase(pName))
      shifty = pValue;
    else if (PARAM_COLOR1.equalsIgnoreCase(pName))
      color1 = pValue;
    else if (PARAM_SPEED1.equalsIgnoreCase(pName))
      speed1 = pValue;
    else if (PARAM_COLOR2.equalsIgnoreCase(pName))
      color2 = pValue;
    else if (PARAM_SPEED2.equalsIgnoreCase(pName))
      speed2 = pValue;
    else if (PARAM_SQUARE.equalsIgnoreCase(pName))
      square = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "dc_dmodulus";
  }

  private double _xr, _yr, c11, c12, c21, c22, oldColor, newColor;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _xr = 2.0 * x;
    _yr = 2.0 * y;

    c11 = (1 + speed1) / 2;
    c12 = color1 * (1 - speed1) / 2;
    c21 = (1 + speed2) / 2;
    c22 = color2 * (1 - speed2) / 2;

    oldColor = 0.5;
  }

}