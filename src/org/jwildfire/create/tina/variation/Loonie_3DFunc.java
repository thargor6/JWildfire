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

public class Loonie_3DFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* loonie_3D by Larry Berlin, http://aporev.deviantart.com/art/New-3D-Plugins-136484533?q=gallery%3Aaporev%2F8229210&qo=22 */
    double sqrvvar = pAmount * pAmount;
    double efTez = pAffineTP.z;
    double kikr;
    // TODO: check if this solves certain crashes
    kikr = Math.atan2(pAffineTP.y, pAffineTP.x);

    if (efTez == 0.0) {
      efTez = kikr;
    }

    double r2 = sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(efTez); // added the z element
    if (r2 < EPSILON) {
      return;
    }
    if (r2 < sqrvvar) {
      double r = pAmount * sqrt(sqrvvar / r2 - 1.0);
      pVarTP.x += r * pAffineTP.x;
      pVarTP.y += r * pAffineTP.y;
      pVarTP.z += r * efTez * 0.5;
    } else {
      pVarTP.x += pAmount * pAffineTP.x;
      pVarTP.y += pAmount * pAffineTP.y;
      pVarTP.z += pAmount * efTez * 0.5;
    }
  }

  @Override
  public String getName() {
    return "loonie_3D";
  }

}
