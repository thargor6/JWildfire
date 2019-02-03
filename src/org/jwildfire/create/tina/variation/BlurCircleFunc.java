/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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

import org.jwildfire.base.mathlib.DoubleWrapperWF;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.M_PI_4;
import static org.jwildfire.base.mathlib.MathLib.sinAndCos;

public class BlurCircleFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  private DoubleWrapperWF sina = new DoubleWrapperWF();
  private DoubleWrapperWF cosa = new DoubleWrapperWF();

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = 2.0 * pContext.random() - 1.0;
    double y = 2.0 * pContext.random() - 1.0;

    double absx = x;
    if (absx < 0)
      absx = absx * -1.0;
    double absy = y;
    if (absy < 0)
      absy = absy * -1.0;

    double perimeter, side;
    if (absx >= absy) {
      if (x >= absy) {
        perimeter = absx + y;
      } else {
        perimeter = 5.0 * absx - y;
      }
      side = absx;
    } else {
      if (y >= absx) {
        perimeter = 3.0 * absy - x;
      } else {
        perimeter = 7.0 * absy + x;
      }
      side = absy;
    }

    double r = pAmount * side;
    sinAndCos(M_PI_4 * perimeter / side - M_PI_4, sina, cosa);
    pVarTP.x += r * cosa.value;
    pVarTP.y += r * sina.value;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  @Override
  public String getName() {
    return "blur_circle";
  }

}
