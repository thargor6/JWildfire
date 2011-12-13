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

public class EDiscFunc extends SimpleVariationFunc {

  @Override
  public void transform(XFormTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Edisc in the Apophysis Plugin Pack */

    double tmp = pAffineTP.getPrecalcSumsq() + 1.0;
    double tmp2 = 2.0 * pAffineTP.x;
    double r1 = Math.sqrt(tmp + tmp2);
    double r2 = Math.sqrt(tmp - tmp2);
    double xmax = (r1 + r2) * 0.5;
    double a1 = Math.log(xmax + Math.sqrt(xmax - 1.0));
    double a2 = -Math.acos(pAffineTP.x / xmax);
    double w = pAmount / 11.57034632;

    double snv = Math.sin(a1);
    double csv = Math.cos(a1);
    double snhu = Math.sinh(a2);
    double cshu = Math.cosh(a2);

    if (pAffineTP.y > 0.0) {
      snv = -snv;
    }

    pVarTP.x += w * cshu * csv;
    pVarTP.y += w * snhu * snv;
  }

  @Override
  public String getName() {
    return "edisc";
  }

}
