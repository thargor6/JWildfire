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

public class CircularFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_CIRCULAR_ANGLE = "angle";
  private static final String PARAM_CIRCULAR_SEED = "seed";
  private static final String[] paramNames = {PARAM_CIRCULAR_ANGLE, PARAM_CIRCULAR_SEED};
  private double angle = 90.0;
  private double seed = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // "circular" variation created by Tatyana Zabanova implemented into JWildfire by Brad Stefanov

    double c_a = angle * M_PI / 180;
    double aux = (sin(pAffineTP.x * 12.9898 + pAffineTP.y * 78.233 + seed) * 43758.5453);
    aux = aux - (int) aux;
    double rnd = (2 * (Math.random() + aux) - 2.0) * c_a;
    double rad = sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
    double ang = atan2(pAffineTP.y, pAffineTP.x);
    double by = sin(ang + rnd);
    double bx = cos(ang + rnd);


    pVarTP.x += pAmount * (bx * rad);
    pVarTP.y += pAmount * (by * rad);
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{angle, seed};
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"angle, seed"};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_CIRCULAR_ANGLE.equalsIgnoreCase(pName))
      angle = pValue;
    else if (PARAM_CIRCULAR_SEED.equalsIgnoreCase(pName))
      seed = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "circular";
  }
}
