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

public class CPow2Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_R = "r";
  private static final String PARAM_A = "a";
  private static final String PARAM_DIVISOR = "divisor";
  private static final String PARAM_RANGE = "range";

  private static final String[] paramNames = {PARAM_R, PARAM_A, PARAM_DIVISOR, PARAM_RANGE};

  private double p_r = 1.0;
  private double p_a = 0.0;
  private double divisor = 1;
  private int range = 1;

  double ang, c, half_c, d, half_d, inv_range, full_range;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    ang = 2.0 * M_PI / divisor;
    c = p_r * cos(M_PI / 2.0 * p_a) / divisor;
    d = p_r * sin(M_PI / 2.0 * p_a) / divisor;
    half_c = c / 2.0;
    half_d = d / 2.0;
    inv_range = 0.5 / range;
    full_range = 2 * M_PI * range;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* cpow2 by Peter Sdobnov (Zueuk) */

    double a = pAffineTP.getPrecalcAtanYX();

    int n = pContext.random(range);
    if (a < 0) n++;
    a += 2 * M_PI * n;
    if (cos(a * inv_range) < pContext.random() * 2.0 - 1.0)
      a -= full_range;

    double lnr2 = log(pAffineTP.getPrecalcSumsq());  // logarithm * 2

    double r = pAmount * exp(half_c * lnr2 - d * a);
    double th = c * a + half_d * lnr2 + ang * floor(divisor * pContext.random());

    pVarTP.x += r * cos(th);
    pVarTP.y += r * sin(th);

    if (pContext.isPreserveZCoordinate()) pVarTP.z += pAmount * pAffineTP.z;

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{p_r, p_a, divisor, range};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_R.equalsIgnoreCase(pName))
      p_r = pValue;
    else if (PARAM_A.equalsIgnoreCase(pName))
      p_a = pValue;
    else if (PARAM_DIVISOR.equalsIgnoreCase(pName))
      divisor = (pValue == 0) ? 1 : pValue;
    else if (PARAM_RANGE.equalsIgnoreCase(pName))
      range = (pValue < 1) ? 1 : Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"cpow_r", "cpow_a", "cpow_divisor", "cpow_spread"};
  }

  @Override
  public String getName() {
    return "cpow2";
  }

}
