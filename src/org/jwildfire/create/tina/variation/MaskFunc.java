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
 * port of Mask Apophysis plugin
 * original Apophysis plugin by Raykoid666
 * ported to JWildfire variation by CozyG (and enhanced with several user-adjustable parameters)
 * [used chronologicaldot's JWildfire variation BSplitFunc.java as initial template]
 */
public class MaskFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_XSHIFT = "xshift";
  private static final String PARAM_YSHIFT = "yshift";
  private static final String PARAM_USHIFT = "ushift";
  private static final String PARAM_XSCALE = "xscale";
  private static final String PARAM_YSCALE = "yscale";
  private static final String[] paramNames = {PARAM_XSHIFT, PARAM_YSHIFT, PARAM_USHIFT, PARAM_XSCALE, PARAM_YSCALE};

  double xshift = 0.0;
  double yshift = 0.0;
  double ushift = 1.0;
  double xscale = 1.0;
  double yscale = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double sumsq = pAffineTP.getPrecalcSumsq();
    // Prevent divide by zero error
    if (sumsq == 0) {
      pVarTP.doHide = true;
    } else {
      double xfactor = xscale * pAffineTP.x + xshift;
      double yfactor = yscale * pAffineTP.y + yshift;
      pVarTP.x += (pAmount / sumsq) * sin(xfactor) * (cosh(yfactor) + ushift) * sqr(sin(xfactor));
      pVarTP.y += (pAmount / sumsq) * cos(xfactor) * (cosh(yfactor) + ushift) * sqr(sin(xfactor));
    }
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "mask";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{xshift, yshift, ushift, xscale, yscale};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_XSHIFT)) {
      xshift = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_YSHIFT)) {
      yshift = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_USHIFT)) {
      ushift = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_XSCALE)) {
      xscale = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_YSCALE)) {
      yscale = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }
}
