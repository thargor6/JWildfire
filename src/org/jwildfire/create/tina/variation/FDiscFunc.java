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

/**
 * ported from fdisc plugin for Apophysis7X, author unknown (couldn't find author name for plugin)
 * ported to JWildfire variation by CozyG
 * and enhanced with user-adjustable parameters
 */
public class FDiscFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ASHIFT = "ashift";
  private static final String PARAM_RSHIFT = "rshift";
  private static final String PARAM_XSHIFT = "xshift";
  private static final String PARAM_YSHIFT = "yshift";
  private static final String PARAM_TERM1 = "term1";
  private static final String PARAM_TERM2 = "term2";
  private static final String PARAM_TERM3 = "term3";
  private static final String PARAM_TERM4 = "term4";

  private static final String[] paramNames = {PARAM_ASHIFT, PARAM_RSHIFT, PARAM_XSHIFT, PARAM_YSHIFT, PARAM_TERM1, PARAM_TERM2, PARAM_TERM3, PARAM_TERM4};

  private double ashift = 1.0;
  private double rshift = 1.0;
  private double xshift = 0;
  private double yshift = 0;
  private double term1 = 1.0;
  private double term2 = 0;
  private double term3 = 0;
  private double term4 = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* calcs from Apophysis7X plugin
    double c, s;
    double a = M_2PI /(sqrt(sqr(FTx) + sqr(FTy)) + 1.0);
    double r = (atan2(FTy, FTx) * M_1_PI + 1.0) * 0.5;
    fsincos( a, &s, &c);
    FPx += VVAR * r * c;
    FPy += VVAR * r * s;
    */

    double afactor = M_2PI / (pAffineTP.getPrecalcSqrt() + ashift);
    double r = (atan2(pAffineTP.y, pAffineTP.x) * M_1_PI + rshift) * 0.5;
    double xfactor = cos(afactor + xshift);
    double yfactor = sin(afactor + yshift);
    double pr = pAmount * r;
    double prx = pr * xfactor;
    double pry = pr * yfactor;

    pVarTP.x += (term1 * prx) + (term2 * pAffineTP.x * prx) + (term3 * pAffineTP.x * pr) + (term4 * pAffineTP.x);
    pVarTP.y += (term1 * pry) + (term2 * pAffineTP.y * pry) + (term3 * pAffineTP.y * pr) + (term4 * pAffineTP.y);
    // pVarTP.x += pAffineTP.x * pAmount * r * xfactor;
    // pVarTP.y += pAffineTP.y * pAmount * r * yfactor;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "fdisc";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{ashift, rshift, xshift, yshift, term1, term2, term3, term4};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_ASHIFT)) {
      ashift = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_RSHIFT)) {
      rshift = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_XSHIFT)) {
      xshift = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_YSHIFT)) {
      yshift = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_TERM1)) {
      term1 = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_TERM2)) {
      term2 = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_TERM3)) {
      term3 = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_TERM4)) {
      term4 = pValue;
    } else {
      throw new IllegalArgumentException(pName);
    }
  }
}
