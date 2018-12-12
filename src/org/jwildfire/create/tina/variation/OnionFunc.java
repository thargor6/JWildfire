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

import static org.jwildfire.base.mathlib.MathLib.exp;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

public class OnionFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_CENTRE_X = "centre_x";
  private static final String PARAM_CENTRE_Y = "centre_y";
  private static final String[] paramNames = {PARAM_CENTRE_X, PARAM_CENTRE_Y};

  private double centre_x = 0.0;
  private double centre_y = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // onion by chronologicaldot, http://jwildfire.org/forum/viewtopic.php?f=23&t=1136
    // onion radius == pAmount

    double r0 = pAmount;
    double x0 = pAffineTP.x;
    double y0 = pAffineTP.y;

    if (r0 == 0.0) {
      r0 = 1.0;
    }

    // center == (a,b)
    x0 -= centre_x;
    y0 -= centre_y;

    double d0 = (x0 * x0) + (y0 * y0); // actual radius squared
    double dr = sqrt(d0);

    // final location
    double x1 = 0.0;
    double y1 = 0.0;
    double z1 = 0.0;

    // use a circular curve, intersecting with a y-axis-flipped exponential curve at x == r/sqrt(2) (plus or minus)
    // which is where the circle derivative == 1 (slope == 1), so it intersects perfectly with the exponential curve

    // curl upwards along circle while less than radius
    if (d0 <= r0 * r0) // actual radius ^2 <= onion radius ^2
    {
      z1 -= sqrt((r0 * r0) - d0); // use bottom of circle
      x1 = x0;
      y1 = y0;
    } else if (2 * r0 - dr > r0 / 1.41421356) // dist > r0 / sqrt(2), the intersection point
    {
      // curl inwards along circle while not at circle-exponential meeting point

      x1 = (2 * r0 - dr) * x0 / dr; // new radius length times unit vector in x-direction
      y1 = (2 * r0 - dr) * y0 / dr; // new radius length times unit vector in y-direction
      z1 = sqrt(r0 * r0 - ((x1 * x1) + (y1 * y1)));

      // slower equivalent:
      // NOTE: r0 - (dr - r0) == 2*r0 - dr , i.e. the radius minus how much the actual distance is beyond the radius
      //z1 = sqrt( (r0 * r0) - ((2*r0 - dr) * (2*r0 - dr)) ); // use top of circle

    } else {
      // exponential curve mirrored over z-axis with origin shifted to r / sqrt(2)
      // recall we invert the direction of travel
      // NOTE: The intersection point occurs at r0 / sqrt(2) for the circle and 1 for the exponential curve, so shift the exponential curve
      // x shift = r0 - r0/1.414213569
      z1 = exp(dr - r0 - (r0 - r0 / 1.414213569)) - 1.0 + (r0 / 1.414213569);

      x1 = (2 * r0 - dr) * x0 / dr; // new radius length times unit vector in x-direction
      y1 = (2 * r0 - dr) * y0 / dr; // new radius length times unit vector in y-direction
    }

    // reset center
    x1 += centre_x;
    y1 += centre_y;

    pVarTP.x += x1;
    pVarTP.y += y1;
    pVarTP.z += z1 + pAffineTP.z; // for now
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{centre_x, centre_y};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_CENTRE_X.equalsIgnoreCase(pName))
      centre_x = pValue;
    else if (PARAM_CENTRE_Y.equalsIgnoreCase(pName))
      centre_y = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "onion";
  }

}
