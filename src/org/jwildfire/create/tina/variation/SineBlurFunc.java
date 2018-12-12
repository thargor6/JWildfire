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

public class SineBlurFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_POWER = "power";
  private static final String[] paramNames = {PARAM_POWER};

  private double power = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // sineblur by Zyorg, http://zy0rg.deviantart.com/art/Blur-Package-347648919
    double power = this.power;
    if (power < 0.0)
      power = 0.0;
    double ang = pContext.random() * M_2PI;
    double r = pAmount * (power == 1.0 ? acos(pContext.random() * 2.0 - 1.0) / M_PI : acos(exp(log(pContext.random()) * power) * 2.0 - 1.0) / M_PI);
    double s = sin(ang);
    double c = cos(ang);
    pVarTP.x += r * c;
    pVarTP.y += r * s;
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
    return new Object[]{power};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "sineblur";
  }

}
