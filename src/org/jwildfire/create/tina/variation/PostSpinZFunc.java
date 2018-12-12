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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class PostSpinZFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* post_spin_z by Larry Berlin, http://aporev.deviantart.com/art/New-3D-Plugins-136484533?q=gallery%3Aaporev%2F8229210&qo=22 */
    double y = _pz_cos * pVarTP.y - _pz_sin * pVarTP.x;
    pVarTP.x = _pz_sin * pVarTP.y + _pz_cos * pVarTP.x;
    pVarTP.y = y;
  }

  @Override
  public String getName() {
    return "post_spin_z";
  }

  private double _pz_sin, _pz_cos;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _pz_sin = sin(pAmount * M_PI_2);
    _pz_cos = cos(pAmount * M_PI_2);
  }

  @Override
  public int getPriority() {
    return 1;
  }

}
