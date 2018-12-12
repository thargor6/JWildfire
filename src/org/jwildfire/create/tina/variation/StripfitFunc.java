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

import static org.jwildfire.base.mathlib.MathLib.fmod;

public class StripfitFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_DX = "dx";

  private static final String[] paramNames = {PARAM_DX};

  private double dx = 1.00;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
                        double pAmount) {
    /* stripfit by dark-beam
     * https://www.deviantart.com/dark-beam/art/Stripfit-764742549
     * converted for JWF by dark_beam and Brad Stefanov		 */
    double fity;
    double dxp = -0.5 * dx;
    if (pAmount != 0.) {
      pVarTP.x += pAmount * pAffineTP.x;

      if (pAffineTP.y > 1.) {
        fity = fmod(pAffineTP.y + 1., 2.);
        pVarTP.y += pAmount * (-1. + fity);
        pVarTP.x += (pAffineTP.y - fity + 1) * dxp;
      } else if (pAffineTP.y < -1.) {
        fity = fmod(1. - pAffineTP.y, 2.);
        pVarTP.y += pAmount * (1. - fity);
        pVarTP.x += (pAffineTP.y + fity - 1) * dxp;
      } else {
        pVarTP.y += pAmount * pAffineTP.y;
      }

      if (pContext.isPreserveZCoordinate()) {
        pVarTP.z += pAmount * pAffineTP.z;
      }
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{dx};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_DX.equalsIgnoreCase(pName))
      dx = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "stripfit";
  }

}
