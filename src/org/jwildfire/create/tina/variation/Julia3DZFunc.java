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

public class Julia3DZFunc extends VariationFunc {

  private static final String PARAM_POWER = "power";
  private static final String[] paramNames = { PARAM_POWER };

  private int power = 2;

  @Override
  public void transform(TransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double absPower = Math.abs(power);
    double cPower = 1.0 / power * 0.5;

    double r2d = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y;
    double r = pAmount * Math.pow(r2d, cPower);

    int rnd = (int) (pContext.getRandomNumberGenerator().random() * absPower);
    double angle = (Math.atan2(pAffineTP.y, pAffineTP.x) + 2 * Math.PI * rnd) / power;
    double sina = Math.sin(angle);
    double cosa = Math.cos(angle);
    pVarTP.x += r * cosa;
    pVarTP.y += r * sina;
    pVarTP.z += r * pAffineTP.z / (Math.sqrt(r2d) * absPower);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { power };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = (int) Math.round(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "julia3Dz";
  }

}
