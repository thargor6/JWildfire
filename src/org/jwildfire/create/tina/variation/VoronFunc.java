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

public class VoronFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_K = "k";
  private static final String PARAM_STEP = "step";
  private static final String PARAM_NUM = "num";
  private static final String PARAM_XSEED = "xseed";
  private static final String PARAM_YSEED = "yseed";
  private static final String[] paramNames = {PARAM_K, PARAM_STEP, PARAM_NUM, PARAM_XSEED, PARAM_YSEED};

  private double k = 0.99;
  private double step = 0.25;
  private int num = 1;
  private int xseed = 3;
  private int yseed = 7;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* voronoi by eralex61, http://eralex61.deviantart.com/art/Voronoi-Diagram-plugin-153126702 */
    int i, j, l, K, M, M1, N, N1;
    double R, Rmin, OffsetX, OffsetY, X0 = 0, Y0 = 0, X, Y;
    Rmin = 20.0;
    M = (int) floor(pAffineTP.x / step);
    N = (int) floor(pAffineTP.y / step);
    for (i = -1; i < 2; i++) {
      M1 = M + i;
      for (j = -1; j < 2; j++) {
        N1 = N + j;
        K = (int) (1 + floor(num * DiscretNoise(19 * M1 + 257 * N1 + xseed)));
        for (l = 0; l < K; l++) {
          X = (DiscretNoise(l + 64 * M1 + 15 * N1 + xseed) + M1) * step;
          Y = (DiscretNoise(l + 21 * M1 + 33 * N1 + yseed) + N1) * step;

          OffsetX = pAffineTP.x - X;
          OffsetY = pAffineTP.y - Y;

          R = sqrt(OffsetX * OffsetX + OffsetY * OffsetY);

          if (R < Rmin) {
            Rmin = R;
            X0 = X;
            Y0 = Y;
          }
        }
      }
    }
    pVarTP.x += pAmount * (k * (pAffineTP.x - X0) + X0);
    pVarTP.y += pAmount * (k * (pAffineTP.y - Y0) + Y0);

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
    return new Object[]{k, step, num, xseed, yseed};
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"Voron_K", "Voron_Step", "Voron_Num", "Voron_XSeed", "Voron_YSeed"};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_K.equalsIgnoreCase(pName))
      k = pValue;
    else if (PARAM_STEP.equalsIgnoreCase(pName))
      step = pValue;
    else if (PARAM_NUM.equalsIgnoreCase(pName))
      num = limitIntVal(Tools.FTOI(pValue), 1, 25);
    else if (PARAM_XSEED.equalsIgnoreCase(pName)) {
      xseed = Tools.FTOI(pValue);
      if (xseed < 1)
        xseed = 1;
    } else if (PARAM_YSEED.equalsIgnoreCase(pName)) {
      yseed = Tools.FTOI(pValue);
      if (yseed < 1)
        yseed = 1;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "voron";
  }

  // private final static int IA = 16807;
  private final static int IM = 2147483647;
  private final static double AM = (1. / IM);

  private double DiscretNoise(int X) {
    int n = X;
    n = (n << 13) ^ n;
    return ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) * AM;
  }

}
