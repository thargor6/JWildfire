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

import static org.jwildfire.base.mathlib.MathLib.SMALL_EPSILON;

public class Ovoid3DFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";
  private static final String PARAM_Z = "z";
  private static final String[] paramNames = {PARAM_X, PARAM_Y, PARAM_Z};

  private double x = 1.0;
  private double y = 1.0;
  private double z = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // ovoid3d by Larry Berlin, http://aporev.deviantart.com/gallery/#/d2blmhg
    double T = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y + pAffineTP.z * pAffineTP.z + SMALL_EPSILON;
    double r = pAmount / T;
    pVarTP.x += pAffineTP.x * r * x;
    pVarTP.y += pAffineTP.y * r * y;
    pVarTP.z += pAffineTP.z * r * z;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{x, y, z};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else if (PARAM_Z.equalsIgnoreCase(pName))
      z = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "ovoid3d";
  }

}
