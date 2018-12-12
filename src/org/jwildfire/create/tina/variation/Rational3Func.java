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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

/**
 * Ported to JWildfire variation by CozyG
 * from Apophysis7x rational3 plugin by xyrus02 at:
 * http://sourceforge.net/p/apophysis7x/svn/HEAD/tree/trunk/Plugin/rational3.c
 * <p>
 * Explanation from rational3 plugin:
 * Rational3 allows you to customize a rational function
 * involving the complex variable z. It can be represented
 * as the function...
 * az^3 + bz^2 + cz + d
 * ----------------------  division line
 * ez^3 + fz^2 + gz + h
 */
public class Rational3Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_E = "e";
  private static final String PARAM_F = "f";
  private static final String PARAM_G = "g";
  private static final String PARAM_H = "h";
  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F, PARAM_G, PARAM_H};
  private double a = 0.5;
  private double b = 0.0;
  private double c = 0.0;
  private double d = 1.0;
  private double e = 0.0;
  private double f = 1.0;
  private double g = 0.0;
  private double h = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    double xsqr = pAffineTP.x * pAffineTP.x;
    double ysqr = pAffineTP.y * pAffineTP.y;
    double xcb = xsqr * pAffineTP.x;
    double ycb = ysqr * pAffineTP.y;
    double zt3 = xcb - 3 * pAffineTP.x * ysqr;
    double zt2 = xsqr - ysqr;
    double zb3 = 3 * xsqr * pAffineTP.y - ycb;
    double zb2 = 2 * pAffineTP.x * pAffineTP.y;

    double tr = (a * zt3) + (b * zt2) + (c * pAffineTP.x) + d;
    double ti = (a * zb3) + (b * zb2) + (c * pAffineTP.y);

    double br = (e * zt3) + (f * zt2) + (g * pAffineTP.x) + h;
    double bi = (e * zb3) + (f * zb2) + (g * pAffineTP.y);

    double r3den = 1 / (br * br + bi * bi);

    pVarTP.x += pAmount * (tr * br + ti * bi) * r3den;
    pVarTP.y += pAmount * (ti * br - tr * bi) * r3den;

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
    return new Object[]{a, b, c, d, e, f, g, h};
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
    } else if (PARAM_G.equalsIgnoreCase(pName)) {
      g = pValue;
    } else if (PARAM_H.equalsIgnoreCase(pName)) {
      h = pValue;
    } else {
      System.out.println("pName not recognized: " + pName);
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "rational3";
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

  }

}
