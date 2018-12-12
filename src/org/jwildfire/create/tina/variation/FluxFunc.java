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

public class FluxFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SPREAD = "spread";

  private static final String[] paramNames = {PARAM_SPREAD};

  private double spread = 0.30;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Flux, by meckie
    double xpw = pAffineTP.x + pAmount;
    double xmw = pAffineTP.x - pAmount;
    double avgr = pAmount * (2 + spread) * sqrt(sqrt(pAffineTP.y * pAffineTP.y + xpw * xpw) / sqrt(pAffineTP.y * pAffineTP.y + xmw * xmw));
    double avga = (atan2(pAffineTP.y, xmw) - atan2(pAffineTP.y, xpw)) * 0.5;

    pVarTP.x += avgr * cos(avga);
    pVarTP.y += avgr * sin(avga);

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
    return new Object[]{spread};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SPREAD.equalsIgnoreCase(pName))
      spread = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "flux";
  }

}
