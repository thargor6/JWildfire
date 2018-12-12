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

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class AugerFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_WEIGHT = "weight";
  private static final String PARAM_SYM = "sym";
  private static final String PARAM_SCALE = "scale";

  private static final String[] paramNames = {PARAM_FREQ, PARAM_WEIGHT, PARAM_SYM, PARAM_SCALE};

  private double freq = 1.00;
  private double weight = 0.5;
  private double sym = 0.1;
  private double scale = 0.9;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Auger, by Xyrus02
    double s = sin(freq * pAffineTP.x);
    double t = sin(freq * pAffineTP.y);
    double dy = pAffineTP.y + weight * (scale * s * 0.5 + fabs(pAffineTP.y) * s);
    double dx = pAffineTP.x + weight * (scale * t * 0.5 + fabs(pAffineTP.x) * t);

    pVarTP.x += pAmount * (pAffineTP.x + sym * (dx - pAffineTP.x));
    pVarTP.y += pAmount * dy;
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
    return new Object[]{freq, weight, sym, scale};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName))
      freq = pValue;
    else if (PARAM_WEIGHT.equalsIgnoreCase(pName))
      weight = pValue;
    else if (PARAM_SYM.equalsIgnoreCase(pName))
      sym = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "auger";
  }

}
