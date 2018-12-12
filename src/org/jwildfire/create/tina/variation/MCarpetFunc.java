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

import static org.jwildfire.base.mathlib.MathLib.sqr;

public class MCarpetFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";
  private static final String PARAM_TWIST = "twist";
  private static final String PARAM_TILT = "tilt";
  private static final String[] paramNames = {PARAM_X, PARAM_Y, PARAM_TWIST, PARAM_TILT};

  private double x = 1.0;
  private double y = 1.0;
  private double twist = 0.0;
  private double tilt = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* mcarpet from FracFx, http://fracfx.deviantart.com/art/FracFx-Plugin-Pack-171806681 */
    double T = ((sqr(pAffineTP.x) + sqr(pAffineTP.y)) / 4.0 + 1.0);
    double r = pAmount / T;
    pVarTP.x += pAffineTP.x * r * this.x;
    pVarTP.y += pAffineTP.y * r * this.y;
    pVarTP.x += (1.0 - (this.twist * sqr(pAffineTP.x)) + pAffineTP.y) * pAmount;
    pVarTP.y += this.tilt * pAffineTP.x * pAmount;
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
    return new Object[]{x, y, twist, tilt};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else if (PARAM_TILT.equalsIgnoreCase(pName))
      tilt = pValue;
    else if (PARAM_TWIST.equalsIgnoreCase(pName))
      twist = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "mcarpet";
  }

}
