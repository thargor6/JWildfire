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
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Hypershift2Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_P = "p";
  private static final String PARAM_Q = "q";


  private static final String[] paramNames = {PARAM_P, PARAM_Q};

  private int p = 3;
  private int q = 7;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    // "Hypershift2" variation created by tatasz implemented into JWildfire by Brad Stefanov https://www.deviantart.com/tatasz/art/Hyperstuff-721510796
    double pq = M_PI / q;
    double pp = M_PI / p;
    double spq = sin(pq);
    double spp = sin(pp);
    double shift = sin(M_PI * 0.5 - pq - pp);
    shift = shift / sqrt(1 - sqr(spq) - sqr(spp));
    double scale2 = (1 / sqrt(sqr(sin(M_PI / 2 + pp)) / sqr(spq) - 1.0));
    scale2 = scale2 * (sin(M_PI / 2 + pp) / spq - 1.0);
    double scale = 1 - shift * shift;

    double FX = pAffineTP.x * scale2;
    double FY = pAffineTP.y * scale2;

    double rad = 1 / (FX * FX + FY * FY);
    double x = rad * FX + shift;
    double y = rad * FY;
    rad = pAmount * scale / (x * x + y * y);
    double angle = ((pContext.random(Integer.MAX_VALUE) % p) * 2.0 + 1.0) * M_PI / p;
    double X = rad * x + shift;
    double Y = rad * y;
    double cosa = cos(angle);
    double sina = sin(angle);
    pVarTP.x = cosa * X - sina * Y;
    pVarTP.y = sina * X + cosa * Y;
    pVarTP.z = pAffineTP.z * rad;
  }


  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{p, q};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_P.equalsIgnoreCase(pName))
      p = Tools.FTOI(pValue);
    else if (PARAM_Q.equalsIgnoreCase(pName))
      q = Tools.FTOI(pValue);

    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "hypershift2";
  }

}
