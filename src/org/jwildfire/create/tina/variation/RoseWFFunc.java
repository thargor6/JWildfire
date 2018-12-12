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

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class RoseWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_AMP = "amp";
  private static final String PARAM_WAVES = "waves";
  private static final String PARAM_FILLED = "filled";
  private static final String[] paramNames = {PARAM_AMP, PARAM_WAVES, PARAM_FILLED};

  private double amp = 0.5;
  private int waves = 4;
  private int filled = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double a = pAffineTP.getPrecalcAtan();
    double r = pAffineTP.getPrecalcSqrt();

    r = amp * cos(waves * a);

    if (filled == 1) {
      r *= pContext.random();
    }

    double nx = sin(a) * r;
    double ny = cos(a) * r;

    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;
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
    return new Object[]{amp, waves, filled};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_AMP.equalsIgnoreCase(pName))
      amp = pValue;
    else if (PARAM_WAVES.equalsIgnoreCase(pName))
      waves = Tools.FTOI(pValue);
    else if (PARAM_FILLED.equalsIgnoreCase(pName))
      filled = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "rose_wf";
  }

}
