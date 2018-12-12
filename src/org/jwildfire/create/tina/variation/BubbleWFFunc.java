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

public class BubbleWFFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r = ((pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y) / 4.0 + 1.0);
    double t = pAmount / r;
    pVarTP.x += t * pAffineTP.x;
    pVarTP.y += t * pAffineTP.y;
    if (pContext.random() < 0.5) {
      pVarTP.z -= pAmount * (2 / r - 1);
    } else {
      pVarTP.z += pAmount * (2 / r - 1);
    }
  }

  @Override
  public String getName() {
    return "bubble_wf";
  }

}
