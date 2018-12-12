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
 * @author Nic Anderson, chronologicaldot
 * @date March 2, 2013
 */
public class LineFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ANGLE_DELTA = "delta";
  private static final String PARAM_ANGLE_PHI = "phi";
  private static final String[] paramNames = {PARAM_ANGLE_DELTA, PARAM_ANGLE_PHI};

  double delta = 0.0;
  double phi = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // pAmount == line length

    // Unit vector of the line
    double ux = cos(delta * M_PI) * cos(phi * M_PI);
    double uy = sin(delta * M_PI) * cos(phi * M_PI);
    double uz = sin(phi * M_PI);

    double r = sqrt(ux * ux + uy * uy + uz * uz);
    // Normalize
    ux /= r;
    uy /= r;
    uz /= r;

    // Project point onto a random spot on the line
    double rand = pContext.random() * pAmount;
    pVarTP.x += ux * rand;
    pVarTP.y += uy * rand;
    pVarTP.z += uz * rand;
  }

  @Override
  public String getName() {
    return "line";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{delta, phi};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_ANGLE_DELTA)) {
      delta = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_ANGLE_PHI)) {
      phi = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }

}
