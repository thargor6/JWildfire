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

public class Splits3DFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";
  private static final String PARAM_Z = "z";

  private static final String[] paramNames = {PARAM_X, PARAM_Y, PARAM_Z};

  private double x = 0.1;
  private double y = 0.3;
  private double z = 0.2;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* splits3D by TyrantWave, http://tyrantwave.deviantart.com/art/Splits3D-Plugin-107262795 */
    if (pAffineTP.x >= 0) {
      pVarTP.x += pAmount * (pAffineTP.x + x);
    } else {
      pVarTP.x += pAmount * (pAffineTP.x - x);
    }

    if (pAffineTP.y >= 0) {
      pVarTP.y += pAmount * (pAffineTP.y + y);
    } else {
      pVarTP.y += pAmount * (pAffineTP.y - y);
    }

    if (pAffineTP.z >= 0) {
      pVarTP.z += pAmount * (pAffineTP.z + z);
    } else {
      pVarTP.z += pAmount * (pAffineTP.z - z);
    }
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
    return "splits3D";
  }

}
