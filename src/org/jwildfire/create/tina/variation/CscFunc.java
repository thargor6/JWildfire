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

public class CscFunc extends SimpleVariationFunc {

  @Override
  public void transform(TransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* complex vars by cothe */
    /* exp log sin cos tan sec csc cot sinh cosh tanh sech csch coth */
    //Cosecant CSC
    double cscsin = Math.sin(pAffineTP.x);
    double csccos = Math.cos(pAffineTP.x);
    double cscsinh = Math.sinh(pAffineTP.y);
    double csccosh = Math.cosh(pAffineTP.y);
    double cscden = 2.0 / (Math.cosh(2.0 * pAffineTP.y) - Math.cos(2.0 * pAffineTP.x));
    pVarTP.x += pAmount * cscden * cscsin * csccosh;
    pVarTP.y -= pAmount * cscden * csccos * cscsinh;
  }

  @Override
  public String getName() {
    return "csc";
  }

}
