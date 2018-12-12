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

import static org.jwildfire.base.mathlib.MathLib.fmod;

public class ModulusFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";

  private static final String[] paramNames = {PARAM_X, PARAM_Y};

  private double x = 0.20;
  private double y = 0.50;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Modulus in the Apophysis Plugin Pack */
    if (pAffineTP.x > x) {
      pVarTP.x += pAmount * (-x + fmod(pAffineTP.x + x, _xr));
    } else if (pAffineTP.x < -x) {
      pVarTP.x += pAmount * (x - fmod(x - pAffineTP.x, _xr));
    } else {
      pVarTP.x += pAmount * pAffineTP.x;
    }

    if (pAffineTP.y > y) {
      pVarTP.y += pAmount * (-y + fmod(pAffineTP.y + y, _yr));
    } else if (pAffineTP.y < -y) {
      pVarTP.y += pAmount * (y - fmod(y - pAffineTP.y, _yr));
    } else {
      pVarTP.y += pAmount * pAffineTP.y;
    }
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
    return new Object[]{x, y};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "modulus";
  }

  private double _xr, _yr;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _xr = 2.0 * x;
    _yr = 2.0 * y;
  }

}
