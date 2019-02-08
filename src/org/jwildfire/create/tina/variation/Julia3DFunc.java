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

import org.jwildfire.base.mathlib.DoubleWrapperWF;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Julia3DFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";
  private static final String[] paramNames = {PARAM_POWER};

  private int power = genRandomPower();

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
    double z = pAffineTP.z / 2;
    double r2d = sqr(pAffineTP.x) + sqr(pAffineTP.y);
    double r = pAmount / sqrt(sqrt(r2d + sqr(z))); // pAmount * sqrt(r3d) / r3d  -->  pAmount / sqrt(r3d)

    pVarTP.z = pVarTP.z + r * z;

    double tmp = r * sqrt(r2d);
    double a = atan2(pAffineTP.y, pAffineTP.x) / 2 + M_PI * pContext.random(2);

    sinAndCos(a, sina, cosa);

    pVarTP.x = pVarTP.x + tmp * cosa.value;
    pVarTP.y = pVarTP.y + tmp * sina.value;
  }

  public void transformPowerMinus2(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double z = pAffineTP.z / 2;
    double r2d = sqr(pAffineTP.x) + sqr(pAffineTP.y);
    double r3d = sqrt(r2d + sqr(z));
    double r = pAmount / (sqrt(r3d) * r3d);

    pVarTP.z = pVarTP.z + r * z;

    double tmp = r * sqrt(r2d);
    double a = atan2(pAffineTP.y, pAffineTP.x) / 2 + M_PI * pContext.random(2);
    sinAndCos(a, sina, cosa);

    pVarTP.x = pVarTP.x + tmp * cosa.value;
    pVarTP.y = pVarTP.y - tmp * sina.value;
  }

  public void transformPower1(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    pVarTP.x = pVarTP.x + pAmount * pAffineTP.x;
    pVarTP.y = pVarTP.y + pAmount * pAffineTP.y;
    pVarTP.z = pVarTP.z + pAmount * pAffineTP.z;
  }

  public void transformPowerMinus1(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r = pAmount / (sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z));
    pVarTP.x = pVarTP.x + r * pAffineTP.x;
    pVarTP.y = pVarTP.y + r * pAffineTP.y;
    pVarTP.z = pVarTP.z + r * pAffineTP.z;
  }

  public void transformFunction(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double z = pAffineTP.z / absPower;
    double r2d = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y;
    double r = pAmount * pow(r2d + z * z, cPower);

    double r2 = r * sqrt(r2d);
    int rnd = (int) (pContext.random() * absPower);
    double angle = (atan2(pAffineTP.y, pAffineTP.x) + 2 * M_PI * rnd) / (double) power;
    sinAndCos(angle, sina, cosa);

    pVarTP.x += r2 * cosa.value;
    pVarTP.y += r2 * sina.value;
    pVarTP.z += r * z;
  }

  private int genRandomPower() {
    int res = (int) (Math.random() * 5.0 + 2.5);
    return Math.random() < 0.5 ? res : -res;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{power};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "julia3D";
  }

  private double absPower, cPower;
  private DoubleWrapperWF sina = new DoubleWrapperWF(), cosa = new DoubleWrapperWF();

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    absPower = fabs(power);
    cPower = (1.0 / power - 1.0) * 0.5;
  }

}
