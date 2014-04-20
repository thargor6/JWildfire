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

import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.pow;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.sqr;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class Murl2Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  /*
   Original function written in C by Peter Sdobnov (Zueuk).
   Transcribed into Java by Nic Anderson (chronologicaldot)
   */

  private double murl_c;
  private int murl_power;
  private double p2, invp, vp;

  // temp variables (instantiated here to speed up processing)
  private double sina, cosa, a, r, re, im, rl;

  private static final String PARAM_C = "c";
  public static final String PARAM_POWER = "power";

  private static final String[] paramNames = { PARAM_C, PARAM_POWER };

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    p2 = (double) murl_power / 2.0;

    if (murl_power != 0) {
      invp = 1.0 / (double) murl_power;
      if (murl_c == -1) {
        vp = 0;
      }
      else {
        vp = pAmount * pow(murl_c + 1, 2.0 / ((double) murl_power));
      }
    }
    else {
      invp = 100000000000.0;
      vp = pAmount * pow(murl_c + 1, 4 /*Normally infinity, but we let this be a special case*/);
    }

    a = atan2(pAffineTP.y, pAffineTP.x) * (double) murl_power;
    sina = sin(a);
    cosa = cos(a);

    r = murl_c * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), p2);

    re = r * cosa + 1;
    im = r * sina;

    r = pow(sqr(re) + sqr(im), invp);
    a = atan2(im, re) * 2.0 * invp;
    re = r * cos(a);
    im = r * sin(a);

    rl = vp / sqr(r);

    pVarTP.x += rl * (pAffineTP.x * re + pAffineTP.y * im);
    pVarTP.y += rl * (pAffineTP.y * re - pAffineTP.x * im);
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "murl2";
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
