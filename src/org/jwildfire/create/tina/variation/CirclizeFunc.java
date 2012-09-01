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

import static org.jwildfire.base.MathLib.M_PI_4;
import static org.jwildfire.base.MathLib.cos;
import static org.jwildfire.base.MathLib.fabs;
import static org.jwildfire.base.MathLib.sin;
import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_CUDA;
import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_JWILDFIRE;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CirclizeFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_HOLE = "hole";

  private static final String[] paramNames = { PARAM_HOLE };

  private double hole = 0.40;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double var4_PI = pAmount / M_PI_4;

    double absx = fabs(pAffineTP.x);
    double absy = fabs(pAffineTP.y);
    double perimeter, side;
    if (absx >= absy) {
      if (pAffineTP.x >= absy) {
        perimeter = absx + pAffineTP.y;
      }
      else {
        perimeter = 5.0 * absx - pAffineTP.y;
      }
      side = absx;
    }
    else {
      if (pAffineTP.y >= absx) {
        perimeter = 3.0 * absy - pAffineTP.x;
      }
      else {
        perimeter = 7.0 * absy + pAffineTP.x;
      }
      side = absy;
    }

    // tsk tsk... hole is not scaled by vvar.
    double r = var4_PI * side + hole;
    double a = M_PI_4 * perimeter / side - M_PI_4;
    double sina = sin(a);
    double cosa = cos(a);

    pVarTP.x += r * cosa;
    pVarTP.y += r * sina;
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
    return new Object[] { hole };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_HOLE.equalsIgnoreCase(pName))
      hole = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "circlize";
  }

  @Override
  public int getAvailability() {
    return AVAILABILITY_JWILDFIRE | AVAILABILITY_CUDA;
  }

}
