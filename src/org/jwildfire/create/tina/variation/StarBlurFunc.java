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

public class StarBlurFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_POWER = "power";
  public static final String PARAM_RANGE = "range";

  private static final String[] paramNames = {PARAM_POWER, PARAM_RANGE};

  private int power = 5;
  private double range = 0.40162283177245455973959534526548;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // starblur by Zyorg, http://zy0rg.deviantart.com/art/Blur-Package-347648919
    double f = pContext.random() * power * 2.0;
    double angle = trunc(f);
    f = f - angle;
    double x = f * starblur_length;
    double z = sqrt(1 + sqr(x) - 2 * x * cos(starblur_alpha));
    if (((int) angle) % 2 == 0)
      angle = M_2PI / (double) power * (((int) angle) / 2) + asin(sin(starblur_alpha) * x / z);
    else
      angle = M_2PI / (double) power * (((int) angle) / 2) - asin(sin(starblur_alpha) * x / z);
    z = z * sqrt(pContext.random());

    double ang = angle - M_PI_2;
    double s = sin(ang);
    double c = cos(ang);

    pVarTP.x += pAmount * z * c;
    pVarTP.y += pAmount * z * s;
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
    return new Object[]{power, range};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = Tools.FTOI(pValue);
    else if (PARAM_RANGE.equalsIgnoreCase(pName))
      range = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "starblur";
  }

  private double starblur_alpha, starblur_length;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    starblur_alpha = M_PI / power;
    starblur_length = sqrt(1.0 + sqr(range) - 2.0 * range * cos(starblur_alpha));
    starblur_alpha = asin(sin(starblur_alpha) * range / starblur_length);
  }

}
