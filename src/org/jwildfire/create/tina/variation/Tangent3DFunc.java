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

public class Tangent3DFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Z+ variation Jan 07
    procedure TXForm.Tangent;
    begin
      FPx := FPx + vars[30] * (sin(FTx)/cos(FTy));
      FPy := FPy + vars[30] * (sin(FTy)/cos(FTy));
    end;
    */
    pVarTP.x += pAmount * sin(pAffineTP.x) / cos(pAffineTP.y);
    pVarTP.y += pAmount * tan(pAffineTP.y);
    pVarTP.z += pAmount * tan(pAffineTP.x);
  }

  @Override
  public String getName() {
    return "tangent3D";
  }

}
