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

import static org.jwildfire.base.MathLib.M_PI;
import static org.jwildfire.base.MathLib.atan2;
import static org.jwildfire.base.MathLib.cos;
import static org.jwildfire.base.MathLib.iabs;
import static org.jwildfire.base.MathLib.pow;
import static org.jwildfire.base.MathLib.sin;
import static org.jwildfire.base.MathLib.sqr;
import static org.jwildfire.base.MathLib.sqrt;
import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_CUDA;
import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_JWILDFIRE;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class JuliaNFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";
  private static final String PARAM_DIST = "dist";
  private static final String[] paramNames = { PARAM_POWER, PARAM_DIST };

  private int power = genRandomPower();
  private double dist = 1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    //    if (power == 2)
    //      transformPower2(pContext, pXForm, pAffineTP, pVarTP, pAmount);
    //    else if (power == -2)
    //      transformPowerMinus2(pContext, pXForm, pAffineTP, pVarTP, pAmount);
    //    else if (power == 1)
    //      transformPower1(pContext, pXForm, pAffineTP, pVarTP, pAmount);
    //    else if (power == -1)
    //      transformPowerMinus1(pContext, pXForm, pAffineTP, pVarTP, pAmount);
    //    else
    transformFunction(pContext, pXForm, pAffineTP, pVarTP, pAmount);
  }

  public void transformPower2(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double d = sqrt(sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)) + pAffineTP.x);
    if (pContext.random(2) == 0) {
      pVarTP.x = pVarTP.x + pAmount2 * d;
      pVarTP.y = pVarTP.y + pAmount2 / d * pAffineTP.y;
    }
    else {
      pVarTP.x = pVarTP.x - pAmount2 * d;
      pVarTP.y = pVarTP.y - pAmount2 / d * pAffineTP.y;
    }
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  public void transformPowerMinus2(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r = sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
    double xd = r + pAffineTP.x;

    r = pAmount / sqrt(r * (sqr(pAffineTP.y) + sqr(xd)));

    if (pContext.random(2) == 0) {
      pVarTP.x = pVarTP.x + r * xd;
      pVarTP.y = pVarTP.y - r * pAffineTP.y;
    }
    else {
      pVarTP.x = pVarTP.x - r * xd;
      pVarTP.y = pVarTP.y + r * pAffineTP.y;
    }
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  public void transformPower1(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    pVarTP.x = pVarTP.x + pAmount * pAffineTP.x;
    pVarTP.y = pVarTP.y + pAmount * pAffineTP.y;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  public void transformPowerMinus1(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r = pAmount / (sqr(pAffineTP.x) + sqr(pAffineTP.y));
    pVarTP.x = pVarTP.x + r * pAffineTP.x;
    pVarTP.y = pVarTP.y + r * pAffineTP.y;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  public void transformFunction(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double a = (atan2(pAffineTP.y, pAffineTP.x) + 2 * M_PI * pContext.random(absPower)) / power;
    double sina = sin(a);
    double cosa = cos(a);
    double r = pAmount * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), cPower);

    pVarTP.x = pVarTP.x + r * cosa;
    pVarTP.y = pVarTP.y + r * sina;
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
    return new Object[] { power, dist };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = Tools.FTOI(pValue);
    else if (PARAM_DIST.equalsIgnoreCase(pName))
      dist = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "julian";
  }

  private int genRandomPower() {
    int res = (int) (Math.random() * 5.0 + 2.5);
    return Math.random() < 0.5 ? res : -res;
  }

  private int absPower;
  private double cPower, pAmount2;

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm, double pAmount) {
    absPower = iabs(Tools.FTOI(power));
    cPower = dist / power * 0.5;
    pAmount2 = pAmount * sqrt(2.0) / 2.0;
  }

  @Override
  public int getAvailability() {
    return AVAILABILITY_JWILDFIRE | AVAILABILITY_CUDA;
  }

}
