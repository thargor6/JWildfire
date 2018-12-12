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

public class R_CircleblurFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_N = "n";
  private static final String PARAM_SEED = "seed";
  private static final String PARAM_DIST = "dist";
  private static final String PARAM_MIN = "min";
  private static final String PARAM_MAX = "max";
  private static final String[] paramNames = {PARAM_N, PARAM_SEED, PARAM_DIST, PARAM_MIN, PARAM_MAX};
  private double n = 1.0;
  private double seed = 0.0;
  private double dist = 0.5;
  private double min = 0.1;
  private double max = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // "r_circleblur" variation created by Tatyana Zabanova http://tatasz.deviantart.com/art/R-Circular-plugin-697158578 implemented into JWildfire by Brad Stefanov

    //truncate to circle
    double rcn = fabs(n);
    double angle = atan2(pAffineTP.y, pAffineTP.x);
    double rad = sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
    rad = fmod(rad, rcn);
    double by = sin(angle + rad);
    double bx = cos(angle + rad);

    //make circles
    by = round(by * rad);
    bx = round(bx * rad);
    double rad2 = sqrt(Math.random()) * 0.5;
    double angle2 = Math.random() * M_2PI;
    double a1 = sin(bx * 127.1 + by * 311.7 + seed) * 43758.5453;
    a1 = a1 - trunc(a1);
    double a2 = sin(bx * 269.5 + by * 183.3 + seed) * 43758.5453;
    a2 = a2 - trunc(a2);
    double a3 = sin(by * 12.9898 + bx * 78.233 + seed) * 43758.5453;
    a3 = a3 - trunc(a3);
    a3 = a3 * (max - min) + min;
    rad2 *= a3;
    by = by + rad2 * sin(angle2) + a1 * dist;
    bx = bx + rad2 * cos(angle2) + a2 * dist;


    pVarTP.x += pAmount * (bx);
    pVarTP.y += pAmount * (by);
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
    return new Object[]{n, seed, dist, min, max};
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"angle, seed, dist,min,max "};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_N.equalsIgnoreCase(pName))
      n = pValue;
    else if (PARAM_SEED.equalsIgnoreCase(pName))
      seed = pValue;
    else if (PARAM_DIST.equalsIgnoreCase(pName))
      dist = pValue;
    else if (PARAM_MIN.equalsIgnoreCase(pName))
      min = pValue;
    else if (PARAM_MAX.equalsIgnoreCase(pName))
      max = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "r_circleblur";
  }
}
