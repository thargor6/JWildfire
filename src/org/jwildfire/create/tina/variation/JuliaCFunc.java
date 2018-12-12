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

public class JuliaCFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RE = "re";
  private static final String PARAM_IM = "im";
  private static final String PARAM_DIST = "dist";
  private static final String[] paramNames = {PARAM_RE, PARAM_IM, PARAM_DIST};

  private double re = genRandomPower();
  private double im = 0.0;
  private double dist = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // juliac by David Young, http://sc0t0ma.deviantart.com/
    double re = 1.0 / (this.re + SMALL_EPSILON);
    double im = this.im / 100.0;
    double arg = atan2(pAffineTP.y, pAffineTP.x) + fmod(pContext.random(Integer.MAX_VALUE), this.re) * 2.0 * M_PI;
    double lnmod = dist * 0.5 * log(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double a = arg * re + lnmod * im;
    double s = sin(a);
    double c = cos(a);
    double mod2 = exp(lnmod * re - arg * im);

    pVarTP.x += pAmount * mod2 * c;
    pVarTP.y += pAmount * mod2 * s;

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
    return new Object[]{re, im, dist};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RE.equalsIgnoreCase(pName)) {
      re = pValue;
    } else if (PARAM_IM.equalsIgnoreCase(pName))
      im = pValue;
    else if (PARAM_DIST.equalsIgnoreCase(pName))
      dist = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "juliac";
  }

  private double genRandomPower() {
    double res = (int) (Math.random() * 5.0 + 2.5) + 0.5;
    return Math.random() < 0.5 ? res : -res;
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"juliac_re", "juliac_im", "juliac_dist"};
  }

}
