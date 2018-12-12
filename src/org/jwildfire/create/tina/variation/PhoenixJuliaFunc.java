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

public class PhoenixJuliaFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";
  private static final String PARAM_DIST = "dist";
  private static final String PARAM_X_DISTORT = "x_distort";
  private static final String PARAM_Y_DISTORT = "y_distort";
  private static final String[] paramNames = {PARAM_POWER, PARAM_DIST, PARAM_X_DISTORT, PARAM_Y_DISTORT};

  private double power = genRandomPower();
  private double dist = 1.0;
  private double x_distort = -0.5;
  private double y_distort = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // phoenix_julia by TyrantWave, http://tyrantwave.deviantart.com/art/PhoenixJulia-Apophysis-Plugin-121246658
    double preX = pAffineTP.x * (this.x_distort + 1.0);
    double preY = pAffineTP.y * (this.y_distort + 1.0);

    double a = atan2(preY, preX) * this._invN + pContext.random(Integer.MAX_VALUE) * this._inv2PI_N;
    double sina = sin(a);
    double cosa = cos(a);
    double r = pAmount * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), this._cN);

    pVarTP.x += r * cosa;
    pVarTP.y += r * sina;

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
    return new Object[]{power, dist, x_distort, y_distort};
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"phoenix_power", "phoenix_dist", "x_distort", "y_distort"};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else if (PARAM_DIST.equalsIgnoreCase(pName))
      dist = pValue;
    else if (PARAM_X_DISTORT.equalsIgnoreCase(pName))
      x_distort = pValue;
    else if (PARAM_Y_DISTORT.equalsIgnoreCase(pName))
      y_distort = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "phoenix_julia";
  }

  private double genRandomPower() {
    int res = (int) (Math.random() * 5.0 + 2.5);
    return Math.random() < 0.5 ? res : -res;
  }

  private double _invN, _inv2PI_N, _cN;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _invN = dist / power;
    _inv2PI_N = M_2PI / power;
    _cN = dist / power / 2.0;
  }

}
