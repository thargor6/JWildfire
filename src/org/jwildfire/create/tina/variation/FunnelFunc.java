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

import static org.jwildfire.base.mathlib.MathLib.*;

public class FunnelFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_EFFECT = "effect";
  private static final String[] paramNames = {PARAM_EFFECT};

  private int effect = 8;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // funnel by Raykoid666, http://raykoid666.deviantart.com/art/re-pack-1-new-plugins-100092186 
    pVarTP.x += pAmount * tanh(pAffineTP.x) * (1.0 / cos(pAffineTP.x) + effect * M_PI);
    pVarTP.y += pAmount * tanh(pAffineTP.y) * (1.0 / cos(pAffineTP.y) + effect * M_PI);
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
    return new Object[]{effect};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_EFFECT.equalsIgnoreCase(pName))
      effect = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "funnel";
  }

}
