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

public class SquarizeFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // squarize by MichaelFaber - The angle pack: http://michaelfaber.deviantart.com/art/The-Angle-Pack-277718538

    double s = sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
    double a = atan2(pAffineTP.y, pAffineTP.x);
    if (a < 0.0)
      a += M_2PI;
    double p = 4.0 * s * a * M_1_PI;

    if (p <= 1.0 * s) {
      pVarTP.x += pAmount * s;
      pVarTP.y += pAmount * p;
    } else if (p <= 3.0 * s) {
      pVarTP.x += pAmount * (2.0 * s - p);
      pVarTP.y += pAmount * (s);
    } else if (p <= 5.0 * s) {
      pVarTP.x -= pAmount * (s);
      pVarTP.y += pAmount * (4.0 * s - p);
    } else if (p <= 7.0 * s) {
      pVarTP.x -= pAmount * (6.0 * s - p);
      pVarTP.y -= pAmount * (s);
    } else {
      pVarTP.x += pAmount * (s);
      pVarTP.y -= pAmount * (8.0 * s - p);
    }

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "squarize";
  }

}
