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
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class RoseWFFunc extends VariationFunc {
  private static final String PARAM_AMP = "amp";
  private static final String PARAM_WAVES = "waves";
  private static final String[] paramNames = { PARAM_AMP, PARAM_WAVES };

  private double amp = 0.5;
  private int waves = 4;

  static int cnt = 0;

  @Override
  public void transform(XFormTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double a = pAffineTP.getPrecalcAtan(pContext);
    double r = pAffineTP.getPrecalcSqrt(pContext);

    r = amp * pContext.cos(waves * a);
    // clover leafs
    //    r = amp * (pContext.sin(2 * a) + 0.25 * pContext.sin(6 * a));

    // cannabis curve (http://mathworld.wolfram.com/CannabisCurve.html)
    //    r = amp * (1 + 9.0 / 10.0 * pContext.cos(8.0 * a)) * (1 + 1.0 / 10.0 * pContext.cos(24.0 * a)) * (9.0 / 10.0 + 1.0 / 10.0 * pContext.cos(200.0 * a)) * (1.0 + pContext.sin(a));
    //    a += Math.PI / 2.0;

    r = amp * pContext.cos(waves * a);

    double nx = pContext.sin(a) * r;
    double ny = pContext.cos(a) * r;

    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { amp, waves };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_AMP.equalsIgnoreCase(pName))
      amp = pValue;
    else if (PARAM_WAVES.equalsIgnoreCase(pName))
      waves = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "rose_wf";
  }
}
