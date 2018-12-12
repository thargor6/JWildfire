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

public class EllipticFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  private double sqrt_safe(double x) {
    return (x < SMALL_EPSILON) ? 0.0 : sqrt(x);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + 1.0;
    double x2 = 2.0 * pAffineTP.x;
    double xmax = 0.5 * (sqrt(tmp + x2) + sqrt(tmp - x2));

    double a = pAffineTP.x / xmax;
    double b = sqrt_safe(1.0 - a * a);

    pVarTP.x += _v * atan2(a, b);

    //    if (pAffineTP.y > 0)
    if (pContext.random() < 0.5)
      pVarTP.y += _v * log(xmax + sqrt_safe(xmax - 1.0));
    else
      pVarTP.y -= _v * log(xmax + sqrt_safe(xmax - 1.0));
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "elliptic";
  }

  private double _v;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _v = pAmount / (M_PI / 2.0);
  }
}
