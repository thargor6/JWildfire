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

import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

public class Panorama1Func extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;
  private final static double M_1_PI = 1.0 / Math.PI; // it's not in mathlib why?

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // author Tatyana Zabanova 2017. Implemented by DarkBeam 2017
    double aux = 1.0 / sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y + 1.0);
    double x1 = pAffineTP.x * aux;
    double y1 = pAffineTP.y * aux;
    aux = sqrt(x1 * x1 + y1 * y1);
    pVarTP.x += pAmount * (atan2(x1, y1)) * M_1_PI;
    pVarTP.y += pAmount * (aux - 0.5);
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "panorama1";
  }

}
