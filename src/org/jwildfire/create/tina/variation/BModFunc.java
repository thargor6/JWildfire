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

import static org.jwildfire.base.mathlib.MathLib.*;

public class BModFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_DISTANCE = "distance";

  private static final String[] paramNames = {PARAM_RADIUS, PARAM_DISTANCE};

  private double radius = 1.0;
  private double distance = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // bMod by Michael Faber, http://michaelfaber.deviantart.com/art/bSeries-320574477
    double tau, sigma;
    double temp;
    double cosht, sinht;
    double sins, coss;

    tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y)));
    sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x);

    if (tau < radius && -tau < radius) {
      tau = fmod(tau + radius + distance * radius, 2.0 * radius) - radius;
    }

    sinht = sinh(tau);
    cosht = cosh(tau);
    sins = sin(sigma);
    coss = cos(sigma);
    temp = cosht - coss;
    if (temp == 0) {
      return;
    }
    pVarTP.x += pAmount * sinht / temp;
    pVarTP.y += pAmount * sins / temp;
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
    return new Object[]{radius, distance};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = limitVal(pValue, 0.0, Double.MAX_VALUE);
    else if (PARAM_DISTANCE.equalsIgnoreCase(pName))
      distance = limitVal(pValue, 0.0, 2.0);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "bMod";
  }

}
