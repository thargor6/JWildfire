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

import org.jwildfire.create.tina.base.Constants;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class TwintrianFunc extends SimpleVariationFunc {

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Z+ variation Jan 07 */
    double r = pContext.random() * pAmount * pAffineTP.getPrecalcSqrt(pContext);

    double sinr = pContext.sin(r);
    double cosr = pContext.cos(r);
    double diff = Math.log10(sinr * sinr) + cosr;

    if (Math.abs(diff) < Constants.EPSILON) {
      diff = -30.0;
    }

    pVarTP.x += pAmount * pAffineTP.x * diff;
    pVarTP.y += pAmount * pAffineTP.x * (diff - sinr * Math.PI);
  }

  @Override
  public String getName() {
    return "twintrian";
  }

}
