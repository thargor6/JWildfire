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

import static org.jwildfire.base.mathlib.MathLib.floor;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

public class CircleLinearFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SC = "Sc";
  private static final String PARAM_K = "K";
  private static final String PARAM_DENS1 = "Dens1";
  private static final String PARAM_DENS2 = "Dens2";
  private static final String PARAM_REVERSE = "Reverse";
  private static final String PARAM_X = "X";
  private static final String PARAM_Y = "Y";
  private static final String PARAM_SEED = "Seed";
  private static final String[] paramNames = {PARAM_SC, PARAM_K, PARAM_DENS1, PARAM_DENS2, PARAM_REVERSE, PARAM_X, PARAM_Y, PARAM_SEED};

  private double Sc = 1.0;
  private double K = 0.5;
  private double Dens1 = 0.5;
  private double Dens2 = 0.5;
  private double Reverse = 1.0;
  private double X = 10.0;
  private double Y = 10.0;
  private int Seed = 0;

  private static final double AM = 1.0 / 2147483647;

  private double DiscretNoise2(int X, int Y) {
    int n = X + Y * 57;
    n = (n << 13) ^ n;
    return ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) * AM;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* CircleLinear by eralex, http://eralex61.deviantart.com/art/Circles-Plugins-126273412 */

    double X, Y, Z, Z1, U, V;
    int M, N;

    M = (int) floor(0.5 * pAffineTP.x / this.Sc);
    N = (int) floor(0.5 * pAffineTP.y / this.Sc);
    X = pAffineTP.x - (M * 2 + 1) * this.Sc;
    Y = pAffineTP.y - (N * 2 + 1) * this.Sc;
    U = sqrt(X * X + Y * Y);
    V = (0.3 + 0.7 * DiscretNoise2(M + 10, N + 3)) * this.Sc;
    Z1 = DiscretNoise2(M + this.Seed, N);
    if ((Z1 < this.Dens1) && (U < V)) {
      if (this.Reverse > 0) {
        if (Z1 < this.Dens1 * this.Dens2) {
          X = this.K * X;
          Y = this.K * Y;
        } else {
          Z = V / U * (1 - this.K) + this.K;
          X = Z * X;
          Y = Z * Y;
        }
      } else {
        if (Z1 > this.Dens1 * this.Dens2) {
          X = this.K * X;
          Y = this.K * Y;
        } else {
          Z = V / U * (1 - this.K) + this.K;
          X = Z * X;
          Y = Z * Y;
        }
      }
    }

    pVarTP.x += pAmount * (X + (M * 2 + 1) * this.Sc);
    pVarTP.y += pAmount * (Y + (N * 2 + 1) * this.Sc);

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
    return new Object[]{Sc, K, Dens1, Dens2, Reverse, X, Y, Seed};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SC.equalsIgnoreCase(pName))
      Sc = pValue;
    else if (PARAM_K.equalsIgnoreCase(pName))
      K = pValue;
    else if (PARAM_DENS1.equalsIgnoreCase(pName))
      Dens1 = pValue;
    else if (PARAM_DENS2.equalsIgnoreCase(pName))
      Dens2 = pValue;
    else if (PARAM_REVERSE.equalsIgnoreCase(pName))
      Reverse = pValue;
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
    return "circleLinear";
  }

}
