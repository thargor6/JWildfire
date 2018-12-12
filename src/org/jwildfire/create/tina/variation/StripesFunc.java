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

import static org.jwildfire.base.mathlib.MathLib.floor;

public class StripesFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SPACE = "space";
  private static final String PARAM_WARP = "warp";

  private static final String[] paramNames = {PARAM_SPACE, PARAM_WARP};

  private double space = 0.20;
  private double warp = 0.60;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Stripes from apo plugins pack */
    double roundx = floor(pAffineTP.x + 0.5);
    double offsetx = pAffineTP.x - roundx;

    pVarTP.x += pAmount * (offsetx * (1.0 - space) + roundx);
    pVarTP.y += pAmount * (pAffineTP.y + offsetx * offsetx * warp);
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
    return new Object[]{space, warp};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SPACE.equalsIgnoreCase(pName))
      space = pValue;
    else if (PARAM_WARP.equalsIgnoreCase(pName))
      warp = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "stripes";
  }

}
