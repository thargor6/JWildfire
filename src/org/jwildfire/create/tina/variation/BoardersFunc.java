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

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.rint;

public class BoardersFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Boarders in the Apophysis Plugin Pack */

    double roundX = rint(pAffineTP.x);
    double roundY = rint(pAffineTP.y);
    double offsetX = pAffineTP.x - roundX;
    double offsetY = pAffineTP.y - roundY;

    if (pContext.random() >= 0.75) {
      pVarTP.x += pAmount * (offsetX * 0.5 + roundX);
      pVarTP.y += pAmount * (offsetY * 0.5 + roundY);
    } else {

      if (fabs(offsetX) >= fabs(offsetY)) {

        if (offsetX >= 0.0) {
          pVarTP.x += pAmount * (offsetX * 0.5 + roundX + 0.25);
          pVarTP.y += pAmount * (offsetY * 0.5 + roundY + 0.25 * offsetY / offsetX);
        } else {
          pVarTP.x += pAmount * (offsetX * 0.5 + roundX - 0.25);
          pVarTP.y += pAmount * (offsetY * 0.5 + roundY - 0.25 * offsetY / offsetX);
        }

      } else {

        if (offsetY >= 0.0) {
          pVarTP.y += pAmount * (offsetY * 0.5 + roundY + 0.25);
          pVarTP.x += pAmount * (offsetX * 0.5 + roundX + offsetX / offsetY * 0.25);
        } else {
          pVarTP.y += pAmount * (offsetY * 0.5 + roundY - 0.25);
          pVarTP.x += pAmount * (offsetX * 0.5 + roundX - offsetX / offsetY * 0.25);
        }
      }
    }
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "boarders";
  }

}
