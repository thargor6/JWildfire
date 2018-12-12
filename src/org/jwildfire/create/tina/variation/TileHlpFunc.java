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

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;


public class TileHlpFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_WIDTH = "width";

  private static final String[] paramNames = {PARAM_WIDTH};

  private double width = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // "tile_hlp" variation created by Zy0rg implemented into JWildfire by Tatyana Zabanova and Brad Stefanov
    double width2 = width * pAmount;
    double x = pAffineTP.x / width;
    double aux = 0;
    if (x > 0.0) {
      aux = x - (int) x;
    } else {
      aux = x + (int) x;
    }
    aux = cos(aux * M_PI);
    double aux2 = 0;
    if (aux < pContext.random() * 2.0 - 1.0) {
      if (x > 0) {
        aux2 = -width2;
      } else {
        aux2 = width2;
      }
    }

    pVarTP.x += pAffineTP.x * pAmount + aux2;
    pVarTP.y += pAmount * pAffineTP.y;
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
    return new Object[]{width};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_WIDTH.equalsIgnoreCase(pName))
      width = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "tile_hlp";
  }

}

