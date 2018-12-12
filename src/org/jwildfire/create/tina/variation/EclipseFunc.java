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

public class EclipseFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SHIFT = "shift";

  private static final String[] paramNames = {PARAM_SHIFT};

  private double shift = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm,
                        XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /*
     * eclipse by Michael Faber,
     * http://michaelfaber.deviantart.com/art/Eclipse-268362046
     */

    if (fabs(pAffineTP.y) <= pAmount) {
      double c_2 = sqrt(sqr(pAmount)
              - sqr(pAffineTP.y));

      if (fabs(pAffineTP.x) <= c_2) {
        double x = pAffineTP.x + this.shift * pAmount;
        if (fabs(x) >= c_2) {
          pVarTP.x -= pAmount * pAffineTP.x;
        } else {
          pVarTP.x += pAmount * x;
        }
      } else {
        pVarTP.x += pAmount * pAffineTP.x;
      }
      pVarTP.y += pAmount * pAffineTP.y;
    } else {
      pVarTP.x += pAmount * pAffineTP.x;
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
    return new Object[]{shift};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SHIFT.equalsIgnoreCase(pName))
      shift = limitVal(pValue, -2.0, 2.0);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "eclipse";
  }

}
