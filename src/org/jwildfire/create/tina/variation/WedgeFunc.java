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

public class WedgeFunc extends VariationFunc {

  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_HOLE = "hole";
  private static final String PARAM_COUNT = "count";
  private static final String PARAM_SWIRL = "swirl";
  private static final String[] paramNames = { PARAM_ANGLE, PARAM_HOLE, PARAM_COUNT, PARAM_SWIRL };

  private double angle = Constants.M_PI_2;
  private double hole = 0.0;
  private int count = 1;
  private double swirl = 0.0;

  @Override
  public void transform(TransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Wedge from apo plugins pack */
    double precalc_sumsq = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y;
    double precalc_sqrt = Math.sqrt(precalc_sumsq);
    double precalc_atanyx = Math.atan2(pAffineTP.y, pAffineTP.x);

    double r = precalc_sqrt;
    double a = precalc_atanyx + swirl * r;
    double c = Math.floor((count * a + Constants.M_PI) * Constants.M_1_PI * 0.5);

    double comp_fac = 1 - angle * count * Constants.M_1_PI * 0.5;

    a = a * comp_fac + c * angle;

    double sa = Math.sin(a);
    double ca = Math.cos(a);

    r = pAmount * (r + hole);

    pVarTP.x += r * ca;
    pVarTP.y += r * sa;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { angle, hole, count, swirl };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ANGLE.equalsIgnoreCase(pName))
      angle = pValue;
    else if (PARAM_HOLE.equalsIgnoreCase(pName))
      hole = pValue;
    else if (PARAM_COUNT.equalsIgnoreCase(pName))
      count = Tools.FTOI(pValue);
    else if (PARAM_SWIRL.equalsIgnoreCase(pName))
      swirl = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "wedge";
  }

}
