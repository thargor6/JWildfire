/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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

public class VoronFunc extends VariationFunc implements SupportsGPU {
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

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "int i,j,l,K,M,M1,N,N1;\n"
        + "int n, dX;\n"
        + "float R,Rmin,OffsetX,OffsetY,X0,Y0,X,Y,DiscretNoise;\n"
        + "float AM = 1.f/2147483647.f;\n"
        + "Rmin=20.f;\n"
        + "M=(int)floorf(__x/varpar->voron_step);\n"
        + "N=(int)floorf(__y/varpar->voron_step);\n"
        + "for (i=-1; i<2; i++)\n"
        + "{\n"
        + "    M1=M+i;\n"
        + "    for (j=-1; j<2; j++)\n"
        + "    {\n"
        + "        N1=N+j;\n"
        + "        dX = 19*M1+257*N1+varpar->voron_xseed;\n"
        + "        n = (dX<<13) ^ dX;\n"
        + "        DiscretNoise = (( (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff)*AM);\n"
        + "        K=(1+(int)floorf(varpar->voron_num*DiscretNoise));\n"
        + "        for (l=0; l<K; l++)\n"
        + "        {\n"
        + "            dX=l+64*M1+15*N1+varpar->voron_xseed;\n"
        + "            n = (dX<<13) ^ dX;\n"
        + "            DiscretNoise = (( (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff)*AM);\n"
        + "            X=(DiscretNoise+M1)*varpar->voron_step;\n"
        + "            dX=l+21*M1+33*N1+varpar->voron_yseed;\n"
        + "            n = (dX<<13) ^ dX;\n"
        + "            DiscretNoise = (( (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff)*AM);\n"
        + "            Y=(DiscretNoise+N1)*varpar->voron_step;\n"
        + "        \n"
        + "            OffsetX=__x-X;\n"
        + "            OffsetY=__y-Y;\n"
        + "        \n"
        + "            R=sqrtf(OffsetX*OffsetX+OffsetY*OffsetY);\n"
        + "        \n"
        + "            if (R<Rmin) {\n"
        + "                Rmin=R;\n"
        + "                X0=X;\n"
        + "                Y0=Y;\n"
        + "            }\n"
        + "        }\n"
        + "    }\n"
        + "}\n"
        + "__px += varpar->voron*(varpar->voron_k*(__x-X0)+X0);\n"
        + "__py += varpar->voron*(varpar->voron_k*(__y-Y0)+Y0);\n"
        + (context.isPreserveZCoordinate() ? "__pz += varpar->voron*__z;\n" : "");
  }
}
