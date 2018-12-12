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

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class TaurusFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_R = "r";
  public static final String PARAM_N = "n";
  public static final String PARAM_INV = "inv";
  public static final String PARAM_SOR = "sor";
  private static final String[] paramNames = {PARAM_R, PARAM_N, PARAM_INV, PARAM_SOR};

  private double r = 3.0;
  private double n = 5.0;
  private double inv = 1.5;
  private double sor = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* taurus by gossamer light */
    double sx = sin(pAffineTP.x);
    double cx = cos(pAffineTP.x);
    double sy = sin(pAffineTP.y);
    double cy = cos(pAffineTP.y);
    double ir = (inv * r) + ((1.0 - inv) * (r * cos(n * pAffineTP.x)));
    pVarTP.x += pAmount * (cx * (ir + sy));
    pVarTP.y += pAmount * (sx * (ir + sy));
    pVarTP.z += pAmount * (sor * cy) + ((1.0 - sor) * pAffineTP.y);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{r, n, inv, sor};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_R.equalsIgnoreCase(pName))
      r = pValue;
    else if (PARAM_N.equalsIgnoreCase(pName))
      n = pValue;
    else if (PARAM_INV.equalsIgnoreCase(pName))
      inv = pValue;
    else if (PARAM_SOR.equalsIgnoreCase(pName))
      sor = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "taurus";
  }

}
