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

public class Foci_3DFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* foci_3D by Larry Berlin, http://aporev.deviantart.com/art/New-3D-Plugins-136484533?q=gallery%3Aaporev%2F8229210&qo=22 */
    double expx = exp(pAffineTP.x) * 0.5;
    double expnx = 0.25 / expx;
    double kikr, boot;
    boot = pAffineTP.z;
    kikr = atan2(pAffineTP.y, pAffineTP.x);
    if (boot == 0.0) {
      boot = kikr;
    }
    double siny = sin(pAffineTP.y);
    double cosy = cos(pAffineTP.y);
    double sinz = sin(boot);
    double cosz = cos(boot);
    double tmp = pAmount / (expx + expnx - (cosy * cosz));

    pVarTP.x += (expx - expnx) * tmp;
    pVarTP.y += siny * tmp;
    pVarTP.z += sinz * tmp;
  }

  @Override
  public String getName() {
    return "foci_3D";
  }

}
