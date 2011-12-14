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

public class THeartFunc extends VariationFunc {
  private static final String PARAM_SCALEX = "scale_x";
  private static final String PARAM_SCALET = "scale_t";
  private static final String PARAM_SHIFTT = "shift_t";
  private static final String PARAM_SCALE_T_LEFT = "scale_r_left";
  private static final String PARAM_SCALE_T_RIGHT = "scale_r_right";
  private static final String[] paramNames = { PARAM_SCALEX, PARAM_SCALET, PARAM_SHIFTT, PARAM_SCALE_T_LEFT, PARAM_SCALE_T_RIGHT };

  private double scaleX = 1.0;
  private double scaleT = 1.0;
  private double shiftT = 0.0;
  private double scaleTLeft = 1.0;
  private double scaleTRight = 1.0;

  private static final double T_MAX = 60.0;

  @Override
  public void transform(XFormTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double a = Math.atan2(pAffineTP.x, pAffineTP.y);
    double r = Math.sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double nx, t;
    if (a < 0) {
      t = -a / Math.PI * T_MAX * scaleTLeft - shiftT;
      if (t > T_MAX) {
        t = T_MAX;
      }
      nx = -0.001 * (-t * t + 40 * t + 1200) * Math.sin(Math.PI * t / 180.0) * r;
    }
    else {
      t = a / Math.PI * T_MAX * scaleTRight - shiftT;
      if (t > T_MAX) {
        t = T_MAX;
      }
      nx = 0.001 * (-t * t + 40 * t + 1200) * Math.sin(Math.PI * t / 180.0) * r;
    }
    double ny = -0.001 * (-t * t + 40 * t + 400) * Math.cos(Math.PI * t / 180.0) * r;
    nx *= scaleX;
    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { scaleX, scaleT, shiftT, scaleTLeft, scaleTRight };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scaleX = pValue;
    else if (PARAM_SCALET.equalsIgnoreCase(pName))
      scaleT = pValue;
    else if (PARAM_SHIFTT.equalsIgnoreCase(pName))
      shiftT = pValue;
    else if (PARAM_SCALE_T_LEFT.equalsIgnoreCase(pName))
      scaleTLeft = pValue;
    else if (PARAM_SCALE_T_RIGHT.equalsIgnoreCase(pName))
      scaleTRight = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "t_heart";
  }
}
