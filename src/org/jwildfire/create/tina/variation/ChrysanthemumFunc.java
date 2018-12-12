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

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class ChrysanthemumFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    // Autor: Jesus Sosa
    // Date: 01/feb/2018
    // Reference:
    // http://paulbourke.net/geometry/chrysanthemum/

    double u = 21.0 * MathLib.M_PI * Math.random();
    double p4 = Math.sin(17.0 * u / 3.0);
    double p8 = Math.sin(2.0 * Math.cos(3.0 * u) - 28.0 * u);
    double r = 5.0 * (1 + Math.sin(11.0 * u / 5.0)) - 4.0 * p4 * p4 * p4 * p4 * p8 * p8 * p8 * p8 * p8 * p8 * p8 * p8;
    r *= pAmount;
    pVarTP.x += r * Math.cos(u);
    pVarTP.y += r * Math.sin(u);

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "chrysanthemum";
  }

}
