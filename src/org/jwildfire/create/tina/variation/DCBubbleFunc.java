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

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.fmod;
import static org.jwildfire.base.mathlib.MathLib.sqr;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class DCBubbleFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_CENTERX = "centerx";
  private static final String PARAM_CENTERY = "centery";
  private static final String PARAM_SCALE = "scale";

  private static final String[] paramNames = { PARAM_CENTERX, PARAM_CENTERY, PARAM_SCALE };

  private double centerx = 0.0;
  private double centery = 0.0;
  private double scale = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* dc_bubble by Xyrus02, http://apophysis-7x.org/extensions */
    double r = (sqr(pAffineTP.x) + sqr(pAffineTP.y));
    double r4_1 = r / 4.0 + 1.0;
    r4_1 = pAmount / r4_1;
    pVarTP.x += pVarTP.x + r4_1 * pAffineTP.x;
    pVarTP.y += pVarTP.y + r4_1 * pAffineTP.y;
    pVarTP.z += pVarTP.z + pAmount * (2.0 / r4_1 - 1.0);

    pVarTP.color = fmod(fabs(bdcs * (sqr(pVarTP.x + centerx) + sqr(pVarTP.y + centery))), 1.0);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { centerx, centery, scale };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_CENTERX.equalsIgnoreCase(pName))
      centerx = pValue;
    else if (PARAM_CENTERY.equalsIgnoreCase(pName))
      centery = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "dc_bubble";
  }

  private double bdcs;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    bdcs = 1.0 / (scale == 0.0 ? 10E-6 : scale);
  }

}
