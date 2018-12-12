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

public class CircleTrans1Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SC = "Sc";
  private static final String PARAM_DENS = "Dens";
  private static final String PARAM_X = "X";
  private static final String PARAM_Y = "Y";
  private static final String PARAM_SEED = "Seed";
  private static final String[] paramNames = {PARAM_SC, PARAM_DENS, PARAM_X, PARAM_Y, PARAM_SEED};

  private double Sc = 1.0;
  private double Dens = 0.5;
  private double X = 10.0;
  private double Y = 10.0;
  private int Seed = 0;

  private static final double AM = 1.0 / 2147483647;

  private static class vec2 {
    public double x, y;
  }

  private double DiscretNoise2(int X, int Y) {
    int n = X + Y * 57;
    n = (n << 13) ^ n;
    return ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) * AM;
  }

  private void CircleR(FlameTransformationContext pContext, vec2 uv) {
    double X, Y, s, c, alpha, U;
    int M, N;
    final int maxIter = 100;
    int iter = 0;
    do {

      X = fabs(this.X) * (1 - 2 * pContext.random());
      Y = fabs(this.Y) * (1 - 2 * pContext.random());
      M = (int) floor(0.5 * X / this.Sc);
      N = (int) floor(0.5 * Y / this.Sc);
      alpha = 2 * M_PI * pContext.random();
      s = sin(alpha);
      c = cos(alpha);
      U = 0.3 + 0.7 * DiscretNoise2(M + 10, N + 3);
      X = U * c;
      Y = U * s;
      if (++iter > maxIter) {
        break;
      }

    }
    while (DiscretNoise2(M + this.Seed, N) > this.Dens);

    uv.x = X + (M * 2 + 1) * this.Sc;
    uv.y = Y + (N * 2 + 1) * this.Sc;
  }

  void Trans(double A, double B, double X, double Y, vec2 v) {
    v.x = (X - A) * 0.5 + A;
    v.y = (Y - B) * 0.5 + B;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* CircleTrans1 by eralex, http://eralex61.deviantart.com/art/Circles-Plugins-126273412 */
    double U, X, Y;
    int M, N;
    vec2 Uxy = new vec2();

    Trans(this.X, this.Y, pAffineTP.x, pAffineTP.y, Uxy);

    M = (int) floor(0.5 * Uxy.x / this.Sc);
    N = (int) floor(0.5 * Uxy.y / this.Sc);
    X = Uxy.x - (M * 2 + 1) * this.Sc;
    Y = Uxy.y - (N * 2 + 1) * this.Sc;
    U = sqrt(X * X + Y * Y);

    if (!((DiscretNoise2(M + this.Seed, N) > this.Dens) || (U > (0.3 + 0.7 * DiscretNoise2(M + 10, N + 3)) * this.Sc))) {
      CircleR(pContext, Uxy);
    }
    pVarTP.x += pAmount * Uxy.x;
    pVarTP.y += pAmount * Uxy.y;
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
    return new Object[]{Sc, Dens, X, Y, Seed};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SC.equalsIgnoreCase(pName))
      Sc = pValue;
    else if (PARAM_DENS.equalsIgnoreCase(pName))
      Dens = pValue;
    else if (PARAM_X.equalsIgnoreCase(pName))
      X = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      Y = pValue;
    else if (PARAM_SEED.equalsIgnoreCase(pName))
      Seed = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "circleTrans1";
  }

}
