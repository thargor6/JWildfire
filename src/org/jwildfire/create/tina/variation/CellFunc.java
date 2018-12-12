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

import static org.jwildfire.base.mathlib.MathLib.floor;

public class CellFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SIZE = "size";

  private static final String[] paramNames = {PARAM_SIZE};

  private double size = 0.60;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Cell in the Apophysis Plugin Pack */

    double inv_cell_size = 1.0 / size;

    /* calculate input cell */
    int x = (int) floor(pAffineTP.x * inv_cell_size);
    int y = (int) floor(pAffineTP.y * inv_cell_size);

    /* Offset from cell origin */
    double dx = pAffineTP.x - x * size;
    double dy = pAffineTP.y - y * size;

    /* interleave cells */
    if (y >= 0) {
      if (x >= 0) {
        y *= 2;
        x *= 2;
      } else {
        y *= 2;
        x = -(2 * x + 1);
      }
    } else {
      if (x >= 0) {
        y = -(2 * y + 1);
        x *= 2;
      } else {
        y = -(2 * y + 1);
        x = -(2 * x + 1);
      }
    }

    pVarTP.x += pAmount * (dx + x * size);
    pVarTP.y -= pAmount * (dy + y * size);

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
    return new Object[]{size};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SIZE.equalsIgnoreCase(pName))
      size = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "cell";
  }

}
