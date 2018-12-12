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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class YinYangFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_ANG1 = "ang1";
  private static final String PARAM_ANG2 = "ang2";
  private static final String PARAM_DUAL_T = "dual_t";
  private static final String PARAM_OUTSIDE = "outside";

  private static final String[] paramNames = {PARAM_RADIUS, PARAM_ANG1, PARAM_ANG2, PARAM_DUAL_T, PARAM_OUTSIDE};
  private double radius = 0.5;
  private double ang1 = 0.0;
  private double ang2 = 0.0;
  private int dual_t = 1;
  private int outside = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* yin_yang by dark-beam */
    double xx = pAffineTP.x;
    double yy = pAffineTP.y;
    double inv = 1;
    double RR = radius;
    double R2 = (xx * xx + yy * yy);

    if (R2 < 1.0) {

      double nx = xx * cosa - yy * sina;
      double ny = xx * sina + yy * cosa;
      if (dual_t == 1 && pContext.random() > 0.5) {
        inv = -1;
        RR = 1 - radius;
        nx = xx * cosb - yy * sinb;
        ny = xx * sinb + yy * cosb;
      }

      xx = nx;
      yy = ny;
      if (yy > 0) {
        double t = sqrt(1 - yy * yy);
        double k = xx / t;
        double t1 = (t - 0.5) * 2;
        double alfa = (1. - k) * 0.5;
        double beta = (1. - alfa);
        double dx = alfa * (RR - 1);
        double k1 = alfa * (RR) + beta * 1;
        pVarTP.x += pAmount * (t1 * k1 + dx) * inv;
        pVarTP.y += pAmount * sqrt(1 - t1 * t1) * k1 * inv;
      } else {
        pVarTP.x += pAmount * (xx * (1 - RR) + RR) * inv;
        pVarTP.y += pAmount * (yy * (1 - RR)) * inv;
      }
    } else if (outside == 1) {
      pVarTP.x += pAmount * pAffineTP.x;
      pVarTP.y += pAmount * pAffineTP.y;
    } else {
      pVarTP.x += 0.0; // out!
      pVarTP.y += 0.0; // out!
    }

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
    return new Object[]{radius, ang1, ang2, dual_t, outside};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_ANG1.equalsIgnoreCase(pName))
      ang1 = pValue;
    else if (PARAM_ANG2.equalsIgnoreCase(pName))
      ang2 = pValue;
    else if (PARAM_DUAL_T.equalsIgnoreCase(pName))
      dual_t = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_OUTSIDE.equalsIgnoreCase(pName))
      outside = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "yin_yang";
  }

  private double sina, cosa;
  private double sinb, cosb;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    sina = sin(M_PI * ang1);
    cosa = cos(M_PI * ang1);
    sinb = sin(M_PI * ang2);
    cosb = cos(M_PI * ang2);
  }

}
