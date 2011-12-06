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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class SeparationFunc extends VariationFunc {

  private static final String PARAM_X = "x";
  private static final String PARAM_XINSIDE = "xinside";
  private static final String PARAM_Y = "y";
  private static final String PARAM_YINSIDE = "yinside";

  private static final String[] paramNames = { PARAM_X, PARAM_XINSIDE, PARAM_Y, PARAM_YINSIDE };

  private double x = 0.5;
  private double xInside = 0.05;
  private double y = 0.25;
  private double yInside = 0.025;

  @Override
  public void transform(TransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* separation from the apophysis plugin pack */
    double sx2 = x * x;
    double sy2 = y * y;

    if (pAffineTP.x > 0.0) {
      pVarTP.x += pAmount * (Math.sqrt(pAffineTP.x * pAffineTP.x + sx2) - pAffineTP.x * xInside);
    }
    else {
      pVarTP.x -= pAmount * (Math.sqrt(pAffineTP.x * pAffineTP.x + sx2) + pAffineTP.x * xInside);
    }

    if (pAffineTP.y > 0.0) {
      pVarTP.y += pAmount * (Math.sqrt(pAffineTP.y * pAffineTP.y + sy2) - pAffineTP.y * yInside);
    }
    else {
      pVarTP.y -= pAmount * (Math.sqrt(pAffineTP.y * pAffineTP.y + sy2) + pAffineTP.y * yInside);
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { x, xInside, y, yInside };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_XINSIDE.equalsIgnoreCase(pName))
      xInside = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else if (PARAM_YINSIDE.equalsIgnoreCase(pName))
      yInside = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "separation";
  }

}
