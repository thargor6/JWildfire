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

public class LazySensenFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALEX = "scale_x";
  private static final String PARAM_SCALEY = "scale_y";
  private static final String PARAM_SCALEZ = "scale_z";

  private static final String[] paramNames = {PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ};

  private double scale_x = 1;
  private double scale_y = 1;
  private double scale_z = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
                        double pAmount) {
    /*
     * lazysensen by bezo97,
     * https://bezo97.tk/plugins.html
     */
    if (scale_x != 0.0) {
      double nr = (int) floor(pAffineTP.x * scale_x);
      if (nr >= 0) {
        if (nr % 2 == 1)
          pAffineTP.x = -pAffineTP.x;
      } else {
        if (nr % 2 == 0)
          pAffineTP.x = -pAffineTP.x;
      }
    }
    if (scale_y != 0.0) {
      double nr = (int) floor(pAffineTP.y * scale_y);
      if (nr >= 0) {
        if (nr % 2 == 1)
          pAffineTP.y = -pAffineTP.y;
      } else {
        if (nr % 2 == 0)
          pAffineTP.y = -pAffineTP.y;
      }
    }
    if (scale_z != 0.0) {
      double nr = (int) floor(pAffineTP.z * scale_z);
      if (nr >= 0) {
        if (nr % 2 == 1)
          pAffineTP.z = -pAffineTP.z;
      } else {
        if (nr % 2 == 0)
          pAffineTP.z = -pAffineTP.z;
      }
    }

    pVarTP.x += pAmount * pAffineTP.x;
    pVarTP.y += pAmount * pAffineTP.y;
    pVarTP.z += pAmount * pAffineTP.z;

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{scale_x, scale_y, scale_z};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scale_x = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scale_y = pValue;
    else if (PARAM_SCALEZ.equalsIgnoreCase(pName))
      scale_z = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "lazysensen";
  }

}
