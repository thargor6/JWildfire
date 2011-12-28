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

import org.jwildfire.create.tina.base.Constants;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class SuperShapeFunc extends VariationFunc {

  private static final String PARAM_RND = "rnd";
  private static final String PARAM_M = "m";
  private static final String PARAM_N1 = "n1";
  private static final String PARAM_N2 = "n2";
  private static final String PARAM_N3 = "n3";
  private static final String PARAM_HOLES = "holes";

  private static final String[] paramNames = { PARAM_RND, PARAM_M, PARAM_N1, PARAM_N2, PARAM_N3, PARAM_HOLES };

  private double rnd = 3.0;
  private double m = 1.0;
  private double n1 = 1.0;
  private double n2 = 1.0;
  private double n3 = 1.0;
  private double holes = 0.0;

  @Override
  public void transform(XFormTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double pm_4 = m / 4.0;
    double pneg1_n1 = -1.0 / n1;

    double theta = pm_4 * pAffineTP.getPrecalcAtanYX() + Constants.M_PI_4;

    double st = pContext.sin(theta);
    double ct = pContext.cos(theta);

    double t1 = Math.abs(ct);
    t1 = Math.pow(t1, n2);

    double t2 = Math.abs(st);
    t2 = Math.pow(t2, n3);

    double myrnd = rnd;

    double r = pAmount * ((myrnd * pContext.random() + (1.0 - myrnd) * pAffineTP.getPrecalcSqrt()) - holes)
        * Math.pow(t1 + t2, pneg1_n1) / pAffineTP.getPrecalcSqrt();

    pVarTP.x += r * pAffineTP.x;
    pVarTP.y += r * pAffineTP.y;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { rnd, m, n1, n2, n3, holes };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RND.equalsIgnoreCase(pName))
      rnd = pValue;
    else if (PARAM_M.equalsIgnoreCase(pName))
      m = pValue;
    else if (PARAM_N1.equalsIgnoreCase(pName))
      n1 = pValue;
    else if (PARAM_N2.equalsIgnoreCase(pName))
      n2 = pValue;
    else if (PARAM_N3.equalsIgnoreCase(pName))
      n3 = pValue;
    else if (PARAM_HOLES.equalsIgnoreCase(pName))
      holes = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "super_shape";
  }

}
