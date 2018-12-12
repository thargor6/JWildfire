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

import static org.jwildfire.base.Tools.limitValue;
import static org.jwildfire.base.mathlib.MathLib.*;

public class DCBubbleFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_CENTERX = "centerx";
  private static final String PARAM_CENTERY = "centery";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_INVERT = "invert";

  private static final String[] paramNames = {PARAM_CENTERX, PARAM_CENTERY, PARAM_SCALE, PARAM_INVERT};

  private double centerx = 0.0;
  private double centery = 0.0;
  private double scale = 1.0;
  private int invert = 1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* corrected version of dc_bubble by Xyrus02, http://apophysis-7x.org/extensions */

    double r = ((pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y) / 4.0 + 1.0);
    double t = pAmount / r;
    pVarTP.x += t * pAffineTP.x;
    pVarTP.y += t * pAffineTP.y;
    pVarTP.z += pAmount * (2.0 / (invert == 1 ? t : r) - 1.0);

    pVarTP.color = fmod(fabs(bdcs * (sqr(pVarTP.x + centerx) + sqr(pVarTP.y + centery))), 1.0);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{centerx, centery, scale, invert};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_CENTERX.equalsIgnoreCase(pName))
      centerx = pValue;
    else if (PARAM_CENTERY.equalsIgnoreCase(pName))
      centery = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_INVERT.equalsIgnoreCase(pName))
      invert = limitValue((int) pValue, 0, 1);
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
