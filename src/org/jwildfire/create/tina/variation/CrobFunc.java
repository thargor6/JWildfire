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

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.pow;

public class CrobFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_TOP = "top";
  private static final String PARAM_BOTTOM = "bottom";
  private static final String PARAM_LEFT = "left";
  private static final String PARAM_RIGHT = "right";
  private static final String PARAM_BLUR = "blur";
  private static final String PARAM_RATIOBLUR = "ratioBlur";
  private static final String PARAM_DIRECTBLUR = "directBlur";

  private static final String[] paramNames = {PARAM_TOP, PARAM_BOTTOM, PARAM_LEFT, PARAM_RIGHT, PARAM_BLUR, PARAM_RATIOBLUR, PARAM_DIRECTBLUR};

  private double left = -1.0;
  private double top = -1.0;
  private double right = 1.0;
  private double bottom = 1.0;
  private int blur = 1;
  private double ratioblur = 0.05;
  private double directblur = 2.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // crob by FractalDesire, https://fractaldesire.deviantart.com/art/crob-plugin-260173724

    double gradTmp, secTmp;
    double xTmp = pAffineTP.x;
    double yTmp = pAffineTP.y;

    if ((pAffineTP.x < left_border) || (pAffineTP.x > right_border) || (pAffineTP.y < top_border) || (pAffineTP.y > bottom_border)) {
      //***** Case: no edge *****
      if (blur == 0) {
        xTmp = 0;
        yTmp = 0;
      }
      //***** Case: with edge *****
      else {
        secTmp = pContext.random();

        // ***** Drawing top and bottom *****
        if (secTmp < setProb) {
          do {
            yTmp = top + pContext.random() * yInt_2;
            xTmp = right - pow(pContext.random(), directblur) * ratioblur * minInt_2;
            gradTmp = (yTmp - y0c) / (xTmp - x0c);
          } while (gradTmp < -1.0);

          if (secTmp < setProbH) xTmp = left + right - xTmp;
          if ((secTmp > setProbQ) && (secTmp < setProbTQ)) yTmp = bottom + top - yTmp;
        }

        // ***** Drawing left and right *****
        else {
          do {
            xTmp = right - pContext.random() * xInt_2;
            yTmp = top + pow(pContext.random(), directblur) * ratioblur * minInt_2;
            gradTmp = (yTmp - y0c) / (xTmp - x0c);
          } while ((gradTmp <= 0.0) && (gradTmp > -1.0));

          if (secTmp > setCompProbH) yTmp = bottom + top - yTmp;
          if ((secTmp > setCompProbQ) && (secTmp < setCompProbTQ)) xTmp = left + right - xTmp;
        }
      }
    }

    pVarTP.x += xTmp;
    pVarTP.y += yTmp;
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
    return new Object[]{top, bottom, left, right, blur, ratioblur, directblur};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_LEFT.equalsIgnoreCase(pName))
      left = pValue;
    else if (PARAM_RIGHT.equalsIgnoreCase(pName))
      right = pValue;
    else if (PARAM_TOP.equalsIgnoreCase(pName))
      top = pValue;
    else if (PARAM_BOTTOM.equalsIgnoreCase(pName))
      bottom = pValue;
    else if (PARAM_BLUR.equalsIgnoreCase(pName))
      blur = Tools.FTOI(pValue);
    else if (PARAM_RATIOBLUR.equalsIgnoreCase(pName))
      ratioblur = pValue;
    else if (PARAM_DIRECTBLUR.equalsIgnoreCase(pName))
      directblur = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "crob";
  }

  private double top_border, bottom_border, left_border, right_border;
  private double xInterval, yInterval, xInt_2, yInt_2, minInt_2;
  private double x0, y0, x0c, y0c;
  private double setProb, setProbH, setProbQ, setProbTQ, setCompProb, setCompProbH, setCompProbQ, setCompProbTQ;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    double tmp;

    // ***** Preventing bad inputs *****
    if (top > bottom) {
      tmp = top;
      top = bottom;
      bottom = tmp;
    }
    if (top == bottom) {
      top = -1.0;
      bottom = 1.0;
    }
    if (left > right) {
      tmp = left;
      left = right;
      right = tmp;
    }
    if (left == right) {
      left = -1.0;
      right = 1.0;
    }
    if (directblur < 0.0) directblur = 0.0;

    // ***** Initializing intervals *****
    if (blur != 0) blur = 1;
    xInterval = fabs(right - left);
    yInterval = fabs(bottom - top);
    xInt_2 = xInterval / 2.0;
    yInt_2 = yInterval / 2.0;
    minInt_2 = (xInt_2 > yInt_2) ? yInt_2 : xInt_2;
    x0 = right - xInt_2;
    y0 = top + yInt_2;

    // ***** Initializing reference point *****
    if (xInt_2 > yInt_2) {
      x0c = right - minInt_2;
      y0c = y0;
    } else if (xInt_2 < yInt_2) {
      x0c = x0;
      y0c = top + minInt_2;
    } else {
      x0c = x0;
      y0c = y0;
    }

    // ***** Initializing probabilities *****
    setProb = yInterval / (xInterval + yInterval);
    setProbQ = 0.25 * setProb;
    setProbH = 0.50 * setProb;
    setProbTQ = 0.75 * setProb;
    setCompProb = 1.0 - setProb;
    setCompProbQ = setProb + 0.25 * setCompProb;
    setCompProbH = setProb + 0.50 * setCompProb;
    setCompProbTQ = setProb + 0.75 * setCompProb;

    // ***** Defining inner area *****
    if (blur == 0) {
      top_border = top;
      bottom_border = bottom;
      left_border = left;
      right_border = right;
    } else {
      top_border = top + minInt_2 * ratioblur;
      bottom_border = bottom - minInt_2 * ratioblur;
      left_border = left + minInt_2 * ratioblur;
      right_border = right - minInt_2 * ratioblur;
    }
  }

}
