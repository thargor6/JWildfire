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

// Variation Plugin DLL for Apophysis
//  Jed Kelsey, 20 June 2007
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class MandelbrotFunc extends VariationFunc {
  private static final String PARAM_ITER = "iter";
  private static final String PARAM_XMIN = "xmin";
  private static final String PARAM_XMAX = "xmax";
  private static final String PARAM_YMIN = "ymin";
  private static final String PARAM_YMAX = "ymax";
  private static final String PARAM_INVERT = "invert";
  private static final String PARAM_SKIN = "skin";
  private static final String PARAM_CX = "cx";
  private static final String PARAM_CY = "cy";

  private static final String[] paramNames = { PARAM_ITER, PARAM_XMIN, PARAM_XMAX, PARAM_YMIN, PARAM_YMAX, PARAM_INVERT, PARAM_SKIN, PARAM_CX, PARAM_CY };

  private int maxIter = 100;
  private double xmin = -1.6;
  private double xmax = 1.6;
  private double ymin = -1.2;
  private double ymax = 1.2;
  private int invertProb = 0;
  private double skin = 0;
  private double cx = 0.0;
  private double cy = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x1 = x0;
    double x = x0;
    double y1 = y0;
    double y = y0;
    int iter;

    boolean inverted = pContext.random() < this.invertProb;
    if (inverted) {
      iter = 0;
    }
    else {
      iter = maxIter;
    }
    while ((inverted && (iter < maxIter)) ||
        ((!inverted) && ((iter >= maxIter) ||
        ((skin < 1) && (iter < 0.1 * maxIter * (1 - skin)))))) {
      if ((x0 == 0) && (y0 == 0)) {
        // Choose a point at random
        x0 = (xmax - xmin) * pContext.random() + xmin;
        y0 = (ymax - ymin) * pContext.random() + ymin;
      }
      else {
        // Choose a point close to previous point
        x0 = (skin + 0.001) * (pContext.random() - 0.5) + x0;
        y0 = (skin + 0.001) * (pContext.random() - 0.5) + y0;
      }
      x1 = x0;
      y1 = y0;
      x = x0;
      y = y0;
      iter = 0;
      while (((x * x + y * y < 2 * 2) && (iter < maxIter))) {
        double xtemp = x * x - y * y + x0;
        y = 2.0 * x * y + y0;
        x = xtemp;
        iter++;
      }
      if ((iter >= maxIter) || (skin == 1) || (iter < 0.1 * (maxIter * (1 - skin)))) {
        // Random point next time
        x0 = 0;
        y0 = 0;
      }
    }
    pVarTP.x += pAmount * (x1 + cx * x); // + FTx^;
    pVarTP.y += pAmount * (y1 + cy * y); // + FTy^;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pVarTP.z + pAmount * pAffineTP.z;
    }

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { maxIter, xmin, xmax, ymin, ymax, invertProb, skin, cx, cy };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ITER.equalsIgnoreCase(pName))
      maxIter = Tools.FTOI(pValue);
    else if (PARAM_XMIN.equalsIgnoreCase(pName))
      xmin = pValue;
    else if (PARAM_XMAX.equalsIgnoreCase(pName))
      xmax = pValue;
    else if (PARAM_YMIN.equalsIgnoreCase(pName))
      ymin = pValue;
    else if (PARAM_YMAX.equalsIgnoreCase(pName))
      ymax = pValue;
    else if (PARAM_INVERT.equalsIgnoreCase(pName))
      invertProb = Tools.FTOI(pValue);
    else if (PARAM_SKIN.equalsIgnoreCase(pName))
      skin = pValue;
    else if (PARAM_CX.equalsIgnoreCase(pName))
      cx = pValue;
    else if (PARAM_CY.equalsIgnoreCase(pName))
      cy = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "mandelbrot";
  }

  private double x0, y0;

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm, double pAmount) {
    x0 = y0 = 0.0;
  }

}
