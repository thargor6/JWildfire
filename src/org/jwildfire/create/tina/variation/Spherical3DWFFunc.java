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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Constants;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class Spherical3DWFFunc extends VariationFunc {
  private static final String PARAM_INVERT = "invert";

  private static final String[] paramNames = { PARAM_INVERT };

  private int invert = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (invert == 0) {
      double r = pAmount / (pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y + pAffineTP.z * pAffineTP.z + Constants.EPSILON);
      pVarTP.x += pAffineTP.x * r;
      pVarTP.y += pAffineTP.y * r;
      pVarTP.z += pAffineTP.z * r;
    }
    else if (invert == 1) {
      double r = pAmount / (pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y + pAffineTP.z * pAffineTP.z + Constants.EPSILON);
      pVarTP.x -= pAffineTP.x * r;
      pVarTP.y -= pAffineTP.y * r;
      pVarTP.z -= pAffineTP.z * r;
    }
    else if (invert == 2) {
      double r = pAmount / Math.pow(Math.sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y + pAffineTP.z * pAffineTP.z + Constants.EPSILON), 2.5);
      double r2 = pAmount / (pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y + pAffineTP.z * pAffineTP.z + Constants.EPSILON);
      pVarTP.x = pAffineTP.x * r;
      pVarTP.y = pAffineTP.y * r;
      pVarTP.z = pAffineTP.z * r;
    }
    else {
      double circle = 1;
      double corners = 2;
      double power = 2;
      double sides = 5;
      double r_factor, theta, phi, b, amp;

      r_factor = Math.pow(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y + pAffineTP.z * pAffineTP.z, power / 2.0);

      theta = pAffineTP.getPrecalcAtanYX(pContext);
      b = 2 * Math.PI / sides;

      phi = theta - (b * Math.floor(theta / b));
      if (phi > b / 2)
        phi -= b;

      amp = corners * (1.0 / (pContext.cos(phi) + Constants.EPSILON) - 1.0) + circle;
      amp /= (r_factor + Constants.EPSILON);

      pVarTP.x += pAmount * pAffineTP.x * amp;
      pVarTP.y += pAmount * pAffineTP.y * amp;
      pVarTP.z += pAmount * pAffineTP.z * amp;

    }
  }

  @Override
  public String getName() {
    return "spherical3D_wf";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { invert };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_INVERT.equalsIgnoreCase(pName))
      invert = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

}
