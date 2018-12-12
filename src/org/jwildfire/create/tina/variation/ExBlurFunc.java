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

import static org.jwildfire.base.mathlib.MathLib.*;

public class ExBlurFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_DIST = "dist";
  private static final String PARAM_R = "r";
  private static final String PARAM_XORIGIN = "x_origin";
  private static final String PARAM_YORIGIN = "y_origin";
  private static final String PARAM_ZORIGIN = "z_origin";

  private static final String[] paramNames = {PARAM_DIST, PARAM_R, PARAM_XORIGIN, PARAM_YORIGIN, PARAM_ZORIGIN};

  private double dist = 0.50;
  private double r = 0.00;
  private double x_origin = 0.00;
  private double y_origin = 0.00;
  private double z_origin = 0.00;
  private int _n;
  private double _r[] = new double[4];

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Zephyrtronium ExBlur Apophysis Plugin (11-07-2010)
    Java translation by DarkBeam, 2014 */
    double rr, theta, phi, su, cu, sv, cv, sru, cru, srv, crv, n, rsrv;
    double ox, oy, oz;

    ox = pAffineTP.x - x_origin;
    oy = pAffineTP.y + y_origin;
    oz = pAffineTP.z - z_origin;
    n = sqr(ox) + sqr(oy) + sqr(oz);
    rr = pAmount * pow(n, dist) *
            (_r[0] + _r[1] + _r[2] + _r[3] - 2.0);
    theta = atan2(oy, ox);
    phi = acos(oz / sqrt(n));
    su = sin(theta);
    cu = cos(theta);
    sv = sin(phi);
    cv = cos(phi);
    theta = pContext.random() * M_2PI; // recycled var
    sru = sin(theta);
    cru = cos(theta);
    theta = pContext.random() * M_2PI; // recycled var
    srv = sin(theta);
    crv = cos(theta);
    _r[_n] = pContext.random();
    _n = _n + 1 & 3;
    rsrv = r * srv;

    pVarTP.x += rr * (sv * cu + rsrv * cru);
    pVarTP.y += rr * (sv * su + rsrv * sru);
    pVarTP.z += rr * (cv + r * crv);

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{dist, r, x_origin, y_origin, z_origin};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_DIST.equalsIgnoreCase(pName))
      dist = pValue;
    else if (PARAM_R.equalsIgnoreCase(pName))
      r = pValue;
    else if (PARAM_XORIGIN.equalsIgnoreCase(pName))
      x_origin = pValue;
    else if (PARAM_YORIGIN.equalsIgnoreCase(pName))
      y_origin = pValue;
    else if (PARAM_ZORIGIN.equalsIgnoreCase(pName))
      z_origin = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "exblur";
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _n = 0;
    _r[0] = pContext.random();
    _r[1] = pContext.random();
    _r[2] = pContext.random();
    _r[3] = pContext.random();
  }

}