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

public class TEllipticFunc extends SimpleVariationFunc {

  @Override
  public void transform(XFormTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Modified version of Elliptic from the Apophysis Plugin Pack */

    double tmp = pAffineTP.getPrecalcSumsq() + 1.0;
    double x2 = 2.0 * pAffineTP.x;
    double xmax = 0.5 * (Math.sqrt(tmp + x2) + Math.sqrt(tmp - x2));
    double a = pAffineTP.x / xmax;
    double b = 1.0 - a * a;
    double ssx = xmax - 1.0;
    double w = pAmount / Constants.M_PI_2;

    if (b < 0.0) {
      b = 0.0;
    }
    else {
      b = Math.sqrt(b);
    }

    if (ssx < 0.0) {
      ssx = 0.0;
    }
    else {
      ssx = Math.sqrt(ssx);
    }
    if (pContext.getRandomNumberGenerator().random() < 0.5) {
      pVarTP.x += w * Math.atan2(a, b);
    }
    else {
      pVarTP.x -= w * Math.atan2(a, b);
    }
    if (pContext.getRandomNumberGenerator().random() < 0.5) {
      pVarTP.y += w * Math.log(xmax + ssx);
    }
    else {
      pVarTP.y -= w * Math.log(xmax + ssx);
    }

  }

  @Override
  public String getName() {
    return "t_elliptic";
  }

}
