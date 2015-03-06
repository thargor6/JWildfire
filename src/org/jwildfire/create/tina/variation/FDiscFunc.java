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

import static org.jwildfire.base.mathlib.MathLib.*;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

/**
 * fdisc plugin from Apophysis7X: 
 * ported to JWildfire variation by CozyG
 */
public class FDiscFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* calcs from Apophysis7X plugin
    double c, s;
    double a = M_2PI /(sqrt(sqr(FTx) + sqr(FTy)) + 1.0);
    double r = (atan2(FTy, FTx) * M_1_PI + 1.0) * 0.5;
    fsincos( a, &s, &c);
    FPx += VVAR * r * c;
    FPy += VVAR * r * s;
    */

    double a = M_2PI / (pAffineTP.getPrecalcSqrt() + 1.0);
    double r = (atan2(pAffineTP.y, pAffineTP.x) * M_1_PI + 1.0) * 0.5;
    double s = sin(a);
    double c = cos(a);
    pVarTP.x += pAmount * r * c;
    pVarTP.y += pAmount * r * s;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "fdisc";
  }

}
