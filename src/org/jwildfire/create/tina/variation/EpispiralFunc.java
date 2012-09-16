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

import static org.jwildfire.base.MathLib.cos;
import static org.jwildfire.base.MathLib.fabs;
import static org.jwildfire.base.MathLib.sin;
import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_CUDA;
import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_JWILDFIRE;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class EpispiralFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_N = "n";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_HOLES = "holes";
  private static final String[] paramNames = { PARAM_N, PARAM_THICKNESS, PARAM_HOLES };

  private double n = 6.0;
  private double thickness = 0.0;
  private double holes = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // epispiral by cyberxaos, http://cyberxaos.deviantart.com/journal/Epispiral-Plugin-240086108   
    //    double theta = Math.atan2(pAffineTP.y, pAffineTP.x);
    //    double t = -holes;
    //    if (fabs(thickness) > EPSILON) {
    //      t += (pContext.random() * thickness) * (1.0 / cos(n * theta));
    //    }
    //    pVarTP.x += pAmount * t * cos(theta);
    //    pVarTP.y += pAmount * t * sin(theta);
    double theta = Math.atan2(pAffineTP.y, pAffineTP.x);
    double EPSILON = 0.000001;
    if (thickness < EPSILON)
      thickness = EPSILON;
    if (holes < EPSILON)
      holes = EPSILON;

    double t = (pContext.random() * thickness);
    t *= (1 / cos(n * theta));
    t -= holes;
    if (fabs(t) == 0) {

    }
    else {

      pVarTP.x += pAmount * t * cos(theta);
      pVarTP.y += pAmount * t * sin(theta);
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
    return new Object[] { n, thickness, holes };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_N.equalsIgnoreCase(pName))
      n = pValue;
    else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
      thickness = pValue;
    else if (PARAM_HOLES.equalsIgnoreCase(pName))
      holes = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "epispiral";
  }

  @Override
  public int getAvailability() {
    return AVAILABILITY_JWILDFIRE | AVAILABILITY_CUDA;
  }

}
