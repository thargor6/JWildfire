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

import static org.jwildfire.base.MathLib.cos;
import static org.jwildfire.base.MathLib.sin;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class KaleidoscopeFunc extends VariationFunc {

  private static final String PARAM_PULL = "pull";
  private static final String PARAM_ROTATE = "rotate";
  private static final String PARAM_LINE_UP = "line_up";
  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";

  private static final String[] paramNames = { PARAM_PULL, PARAM_ROTATE, PARAM_LINE_UP, PARAM_X, PARAM_Y };

  private double pull = 0.0;
  private double rotate = 1.0;
  private double line_up = 1.0;
  private double x = 0.0;
  private double y = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Kaleidoscope by Will Evans, http://eevans1.deviantart.com/art/kaleidoscope-plugin-122185469  */
    pVarTP.x += ((w * pAffineTP.x) * cos(45.0) - pAffineTP.y * sin(45.0) + e) + r;
    // the if function splits the plugin in two.
    if (pAffineTP.y > 0) {
      pVarTP.y += ((w * pAffineTP.y) * cos(45.0) + pAffineTP.x * sin(45.0) + q + e) + t;
    }
    else {
      pVarTP.y += (w * pAffineTP.y) * cos(45.0) + pAffineTP.x * sin(45.0) - q - e;
    }
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
    return new Object[] { pull, rotate, line_up, x, y };
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
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "kaleidoscope";
  }

  double q, w, e, r, t, i;

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm, double pAmount) {
    q = pull; // pulls apart the 2 sections of the plugin
    w = rotate; // rotates both halves of the plugin
    e = line_up;
    r = x; // changes x co-ordinates
    t = y; // changes y co-ordinates for 1 part of the plugin
  }

}
