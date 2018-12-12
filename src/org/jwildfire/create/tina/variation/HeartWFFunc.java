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

public class HeartWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALEX = "scale_x";
  private static final String PARAM_SCALET = "scale_t";
  private static final String PARAM_SHIFTT = "shift_t";
  private static final String PARAM_SCALE_T_LEFT = "scale_r_left";
  private static final String PARAM_SCALE_T_RIGHT = "scale_r_right";
  private static final String[] paramNames = {PARAM_SCALEX, PARAM_SCALET, PARAM_SHIFTT, PARAM_SCALE_T_LEFT, PARAM_SCALE_T_RIGHT};

  private double scale_x = 1.0;
  private double scale_t = 1.0;
  private double shift_t = 0.0;
  private double scale_r_left = 1.0;
  private double scale_r_right = 1.0;

  private static final double T_MAX = 60.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double a = atan2(pAffineTP.x, pAffineTP.y);
    double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double nx, t;
    if (a < 0) {
      t = -a / M_PI * T_MAX * scale_r_left - shift_t;
      if (t > T_MAX) {
        t = T_MAX;
      }
      nx = -0.001 * (-t * t + 40 * t + 1200) * sin(M_PI * t / 180.0) * r;
    } else {
      t = a / M_PI * T_MAX * scale_r_right - shift_t;
      if (t > T_MAX) {
        t = T_MAX;
      }
      nx = 0.001 * (-t * t + 40 * t + 1200) * sin(M_PI * t / 180.0) * r;
    }
    double ny = -0.001 * (-t * t + 40 * t + 400) * cos(M_PI * t / 180.0) * r;
    nx *= scale_x;
    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;
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
    return new Object[]{scale_x, scale_t, shift_t, scale_r_left, scale_r_right};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scale_x = pValue;
    else if (PARAM_SCALET.equalsIgnoreCase(pName))
      scale_t = pValue;
    else if (PARAM_SHIFTT.equalsIgnoreCase(pName))
      shift_t = pValue;
    else if (PARAM_SCALE_T_LEFT.equalsIgnoreCase(pName))
      scale_r_left = pValue;
    else if (PARAM_SCALE_T_RIGHT.equalsIgnoreCase(pName))
      scale_r_right = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "heart_wf";
  }

}
