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

import static org.jwildfire.base.mathlib.MathLib.EPSILON;

public class ChecksFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";
  private static final String PARAM_SIZE = "size";
  private static final String PARAM_RND = "rnd";

  private static final String[] paramNames = {PARAM_X, PARAM_Y, PARAM_SIZE, PARAM_RND};

  private double x = 5.0;
  private double y = 5.0;
  private double size = 5.0;
  private double rnd = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Fixed checks plugin by Keeps and Xyrus02, http://xyrus02.deviantart.com/art/Checks-The-fixed-version-138967784?q=favby%3Aapophysis-plugins%2F39181234&qo=3
    int isXY = (int) Math.rint(pAffineTP.x * _cs) + (int) Math.rint(pAffineTP.y * _cs);

    // -X- This is just for code readability,
    //     if there is any impact on performance, its minimal :-)
    double rnx = rnd * pContext.random();
    double rny = rnd * pContext.random();
    double dx, dy;
    if (isXY % 2 == 0) {
      // -X- The -VAR(checks_#) stuff caused the error!
      dx = _ncx + rnx;
      dy = _ncy;
    } else {
      dx = this.x;
      dy = this.y + rny;
    }

    pVarTP.x += pAmount * (pAffineTP.x + dx);
    pVarTP.y += pAmount * (pAffineTP.y + dy);
    // -X- and as a little goodie, I pass through FTz so that
    //     neat lil variation does not kill 3Dness in hack & 7X
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
    return new Object[]{x, y, size, rnd};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else if (PARAM_SIZE.equalsIgnoreCase(pName))
      size = pValue;
    else if (PARAM_RND.equalsIgnoreCase(pName))
      rnd = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "checks";
  }

  private double _cs, _ncx, _ncy;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    // Multiplication is faster than division, so divide in precalc, multiply in calc.
    _cs = 1.0 / (size + EPSILON);
    // -X- Then precalculate -checkx_x, -checks_y
    _ncx = x * -1.0;
    _ncy = y * -1.0;
  }

}
