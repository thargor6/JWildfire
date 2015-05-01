/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2014 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.pow;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.sqr;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class MurlFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  /*
   Original function written in C by Peter Sdobnov (Zueuk).
   Transcribed into Java by Nic Anderson (chronologicaldot)
   */

  private double murl_c;
  private int murl_power;
  private double c, p2, vp;

  // temp variables (instantiated here to speed up processing)
  private double sina, cosa, a, r, re, im, rl;

  private static final String PARAM_C = "c";
  public static final String PARAM_POWER = "power";

  private static final String[] paramNames = { PARAM_C, PARAM_POWER };

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    c = murl_c;
    if (murl_power != 1) {
      c /= ((double) murl_power - 1);
    }
    p2 = (double) murl_power / 2.0;
    vp = pAmount * (c + 1);

    a = atan2(pAffineTP.y, pAffineTP.x) * (double) murl_power;
    sina = sin(a);
    cosa = cos(a);

    r = c * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), p2);

    re = r * cosa + 1;
    im = r * sina;
    rl = vp / (sqr(re) + sqr(im));

    pVarTP.x += rl * (pAffineTP.x * re + pAffineTP.y * im);
    pVarTP.y += rl * (pAffineTP.y * re - pAffineTP.x * im);
    pVarTP.z = pAmount * pAffineTP.z;
  }

  @Override
  public String getName() {
    return "murl";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { murl_c, murl_power };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      murl_power = (int) pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      murl_c = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

}