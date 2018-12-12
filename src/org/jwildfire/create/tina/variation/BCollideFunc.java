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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class BCollideFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_NUM = "num";
  private static final String PARAM_A = "a";

  private static final String[] paramNames = {PARAM_NUM, PARAM_A};

  private int num = 1;
  private double a = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // bCollide by Michael Faber, http://michaelfaber.deviantart.com/art/bSeries-320574477
    double tau, sigma;
    double temp;
    double cosht, sinht;
    double sins, coss;
    int alt;

    tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y)));
    sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x);

    alt = (int) (sigma * _bCn_pi);
    if (alt % 2 == 0)
      sigma = alt * _pi_bCn + fmod(sigma + _bCa_bCn, _pi_bCn);
    else
      sigma = alt * _pi_bCn + fmod(sigma - _bCa_bCn, _pi_bCn);
    sinht = sinh(tau);
    cosht = cosh(tau);
    sins = sin(sigma);
    coss = cos(sigma);
    temp = cosht - coss;
    pVarTP.x += pAmount * sinht / temp;
    pVarTP.y += pAmount * sins / temp;
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
    return new Object[]{num, a};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_NUM.equalsIgnoreCase(pName))
      num = limitIntVal(Tools.FTOI(pValue), 1, Integer.MAX_VALUE);
    else if (PARAM_A.equalsIgnoreCase(pName))
      a = limitVal(pValue, 0.0, 1.0);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "bCollide";
  }

  private double _bCa, _bCn_pi, _bCa_bCn, _pi_bCn;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _bCn_pi = (double) num * M_1_PI;
    _pi_bCn = M_PI / (double) num;
    _bCa = M_PI * a;
    _bCa_bCn = _bCa / (double) num;
  }

}
