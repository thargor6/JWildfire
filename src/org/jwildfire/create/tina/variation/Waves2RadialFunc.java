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

public class Waves2RadialFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_W2R_SCALEX = "w2r_scalex";
  private static final String PARAM_W2R_SCALEY = "w2r_scaley";
  private static final String PARAM_W2R_FREQX = "w2r_freqx";
  private static final String PARAM_W2R_FREQY = "w2r_freqy";
  private static final String PARAM_W2R_NULL = "w2r_null";
  private static final String PARAM_W2R_DISTANCE = "w2r_distance";
  private static final String[] paramNames = {PARAM_W2R_SCALEX, PARAM_W2R_SCALEY, PARAM_W2R_FREQX, PARAM_W2R_FREQY, PARAM_W2R_NULL, PARAM_W2R_DISTANCE};

  private double w2r_scalex = 0.1;
  private double w2r_scaley = 0.1;
  private double w2r_freqx = 7;
  private double w2r_freqy = 13;
  private double w2r_null = 2;
  private double w2r_distance = 10;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // waves2_radial variation created by Tatyana Zabanova implemented into JWildfire by Brad Stefanov
    double x0 = pAffineTP.x;
    double y0 = pAffineTP.y;
    double dist = sqrt(sqr(x0) + sqr(y0));

    double factor = (dist < w2r_distance) ? (dist - w2r_null) / (w2r_distance - w2r_null) : 1.0;
    factor = (dist < w2r_null) ? 0.0 : factor;

    pVarTP.x += pAmount * (x0 + factor * sin(y0 * w2r_freqx) * w2r_scalex);
    pVarTP.y += pAmount * (y0 + factor * sin(x0 * w2r_freqy) * w2r_scaley);
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
    return new Object[]{w2r_scalex, w2r_scaley, w2r_freqx, w2r_freqy, w2r_null, w2r_distance};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_W2R_SCALEX.equalsIgnoreCase(pName))
      w2r_scalex = pValue;
    else if (PARAM_W2R_SCALEY.equalsIgnoreCase(pName))
      w2r_scaley = pValue;
    else if (PARAM_W2R_FREQX.equalsIgnoreCase(pName))
      w2r_freqx = pValue;
    else if (PARAM_W2R_FREQY.equalsIgnoreCase(pName))
      w2r_freqy = pValue;
    else if (PARAM_W2R_NULL.equalsIgnoreCase(pName))
      w2r_null = pValue;
    else if (PARAM_W2R_DISTANCE.equalsIgnoreCase(pName))
      w2r_distance = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "waves2_radial";
  }

}
