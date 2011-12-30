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

public class CrossFunc extends SimpleVariationFunc {

  @Override
  public void transform(XFormTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Z+ variation Jan 07
    procedure TXForm.Cross;
    var
      r: double;
    begin
      r := vars[36]*sqrt(1/(sqr(sqr(FTx)-sqr(FTy))+EPS));
      FPx := FPx + FTx * r;
      FPy := FPy + FTy * r;
    end;
    */

    double s = pAffineTP.x * pAffineTP.x - pAffineTP.y * pAffineTP.y;
    double r = pAmount * pContext.sqrt(1.0 / (s * s + Constants.EPSILON));

    pVarTP.x += pAffineTP.x * r;
    pVarTP.y += pAffineTP.y * r;
  }

  @Override
  public String getName() {
    return "cross";
  }

}
