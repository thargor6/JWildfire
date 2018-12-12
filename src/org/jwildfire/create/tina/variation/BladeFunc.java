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

public class BladeFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Z+ variation Jan 07
    procedure TXForm.Blade;
    var
      r, sinr, cosr: double;
    begin
      r := sqrt(sqr(FTx) + sqr(FTy))*vars[33];
      SinCos(r*random, sinr, cosr);
      FPx := FPx + vars[33] * FTx * (cosr + sinr);
      FPy := FPy + vars[33] * FTx * (cosr - sinr);
    end;
    */

    double r = pContext.random() * pAmount * sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double sinr = sin(r);
    double cosr = cos(r);
    pVarTP.x += pAmount * pAffineTP.x * (cosr + sinr);
    pVarTP.y += pAmount * pAffineTP.x * (cosr - sinr);
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "blade";
  }

}
