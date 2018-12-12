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

public class WedgeJuliaFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";
  private static final String PARAM_DIST = "dist";
  private static final String PARAM_COUNT = "count";
  private static final String PARAM_ANGLE = "angle";

  private static final String[] paramNames = {PARAM_POWER, PARAM_DIST, PARAM_COUNT, PARAM_ANGLE};

  private double power = 7.00;
  private double dist = 0.20;
  private double count = 2.0;
  private double angle = 0.30;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double wedgeJulia_cf = 1.0 - angle * count * M_1_PI * 0.5;
    double wedgeJulia_rN = fabs(power);
    double wedgeJulia_cn = dist / power / 2.0;
    /* wedge_julia from apo plugin pack */

    double r = pAmount * pow(pAffineTP.getPrecalcSumsq(), wedgeJulia_cn);
    int t_rnd = (int) ((wedgeJulia_rN) * pContext.random());
    double a = (pAffineTP.getPrecalcAtanYX() + 2.0 * M_PI * t_rnd) / power;
    double c = floor((count * a + M_PI) * M_1_PI * 0.5);

    a = a * wedgeJulia_cf + c * angle;
    double sa = sin(a);
    double ca = cos(sa);

    pVarTP.x += r * ca;
    pVarTP.y += r * sa;
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
    return new Object[]{power, dist, count, angle};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else if (PARAM_DIST.equalsIgnoreCase(pName))
      dist = pValue;
    else if (PARAM_COUNT.equalsIgnoreCase(pName))
      count = pValue;
    else if (PARAM_ANGLE.equalsIgnoreCase(pName))
      angle = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "wedge_julia";
  }

}
