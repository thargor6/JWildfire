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

public class SphTiling3V2Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_E = "e";
  private static final String PARAM_F = "f";
  private static final String PARAM_DC = "dc";
  private static final String PARAM_DC1 = "dc1";
  private static final String PARAM_DC2 = "dc2";
  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F, PARAM_DC, PARAM_DC1, PARAM_DC2};
  private double a = 0.5;
  private double b = 1.0;
  private double c = 1.0;
  private double d = 0.5;
  private double e = 1.0;
  private double f = 0.5;
  private int dc = 1;
  private double dc1 = 0.3;
  private double dc2 = 0.5;


  private final static double M_SQRT3_2 = 0.86602540378443864676372317075249;

  private XYZPoint xy = new XYZPoint();
  private XYZPoint uv = new XYZPoint();

  private VariationFunc spherical = new SphericalFunc();

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // sphTiling3 by Alexey Ermushev, http://eralex61.deviantart.com/art/Controlled-IFS-beyond-chaos-257891160 transcribed by Rick Sidwell and variables added by Brad Stefanov

    double w = pContext.random();
    uv.assign(xy);
    uv.invalidate();
    uv.x = uv.y = uv.z = 0;
    if (w < a) {
      double n = floor(3 * pContext.random());
      double r = b / M_SQRT3_2;
      double x0 = r * cos(n * M_2PI / 3);
      double y0 = r * sin(n * M_2PI / 3);
      xy.x -= x0;
      xy.y -= y0;
      spherical.transform(pContext, pXForm, xy, uv, c);
      uv.x += x0;
      uv.y += y0;
    } else {
      spherical.transform(pContext, pXForm, xy, uv, e);
      double t = 0.0411259 * (uv.x * f - uv.y * M_SQRT3_2);
      uv.y = 0.0411259 * (uv.x * M_SQRT3_2 + uv.y * d);
      uv.x = t;
    }
    if (dc == 1) {
      if (pAffineTP.x < 0) {
        pVarTP.color = dc2;
      } else {
        pVarTP.color = dc1;
      }
    }
    xy.assign(uv);

    pVarTP.x += pAmount * uv.x;
    pVarTP.y += pAmount * uv.y;
    if (pContext.isPreserveZCoordinate()) pVarTP.z += pAmount * pAffineTP.z;
  }


  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{a, b, c, d, e, f, dc, dc1, dc2};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName)) {
      a = pValue;
    } else if (PARAM_B.equalsIgnoreCase(pName)) {
      b = pValue;
    } else if (PARAM_C.equalsIgnoreCase(pName)) {
      c = pValue;
    } else if (PARAM_D.equalsIgnoreCase(pName)) {
      d = pValue;
    } else if (PARAM_E.equalsIgnoreCase(pName)) {
      e = pValue;
    } else if (PARAM_F.equalsIgnoreCase(pName)) {
      f = pValue;
    } else if (PARAM_DC.equalsIgnoreCase(pName)) {
      dc = limitIntVal(Tools.FTOI(pValue), 0, 1);
    } else if (PARAM_DC1.equalsIgnoreCase(pName)) {
      dc1 = pValue;
    } else if (PARAM_DC2.equalsIgnoreCase(pName)) {
      dc2 = pValue;
    } else {
      System.out.println("pName not recognized: " + pName);
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "sphtiling3v2";
  }

}
