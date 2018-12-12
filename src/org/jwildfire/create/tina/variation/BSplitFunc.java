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

/**
 * @author Raykoid666, transcribed and modded by Nic Anderson, chronologicaldot
 * @date July 19, 2014 (transcribe)
 */
public class BSplitFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SHIFT_X = "x";
  private static final String PARAM_SHIFT_Y = "y";
  private static final String[] paramNames = {PARAM_SHIFT_X, PARAM_SHIFT_Y};

  double x = 0.0;
  double y = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Prevent divide by zero error
    if (pAffineTP.x + x == 0 || pAffineTP.x + x == M_PI) {
      pVarTP.doHide = true;
    } else {
      pVarTP.doHide = false;
      pVarTP.x += pAmount / tan(pAffineTP.x + x) * cos(pAffineTP.y + y);
      pVarTP.y += pAmount / sin(pAffineTP.x + x) * (-1 * pAffineTP.y + y);
    }
  }

  @Override
  public String getName() {
    return "bsplit";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{x, y};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_SHIFT_X)) {
      x = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_SHIFT_Y)) {
      y = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }
}
