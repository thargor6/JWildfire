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

import static org.jwildfire.base.MathLib.M_PI_2;
import static org.jwildfire.base.MathLib.cos;
import static org.jwildfire.base.MathLib.sin;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class PreSpinZFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* pre_spin_z by Larry Berlin, http://aporev.deviantart.com/art/New-3D-Plugins-136484533?q=gallery%3Aaporev%2F8229210&qo=22 */
    double y = rz_cos * pAffineTP.y - rz_sin * pAffineTP.x;
    pAffineTP.x = rz_sin * pAffineTP.y + rz_cos * pAffineTP.x;
    pAffineTP.y = y;
  }

  @Override
  public String getName() {
    return "pre_spin_z";
  }

  private double rz_sin, rz_cos;

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm, double pAmount) {
    rz_sin = sin(pAmount * M_PI_2);
    rz_cos = cos(pAmount * M_PI_2);
  }

  @Override
  public int getPriority() {
    return -1;
  }

}
