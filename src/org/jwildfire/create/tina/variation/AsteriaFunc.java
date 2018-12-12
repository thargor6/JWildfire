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

import static org.jwildfire.base.mathlib.MathLib.*;

public class AsteriaFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ALPHA = "alpha";
  private static final String[] paramNames = {PARAM_ALPHA};

  private double alpha = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // asteria by dark-beam, http://jwildfire.org/forum/viewtopic.php?f=23&t=1464
    double x0 = pAmount * pAffineTP.x;
    double y0 = pAmount * pAffineTP.y;
    double xx = x0;
    double yy = y0;
    double r = sqr(xx) + sqr(yy);
    xx = sqr(fabs(xx) - 1.);
    yy = sqr(fabs(yy) - 1.);
    double r2 = sqrt(yy + xx);
    boolean in1 = r < 1.;
    boolean out2 = r2 < 1.;
    if (in1 && out2)
      in1 = ((pContext.random()) > 0.35);
    else
      in1 = !in1;
    if (in1) { // linear
      pVarTP.x += x0;
      pVarTP.y += y0;
      if (pContext.isPreserveZCoordinate()) {
        pVarTP.z += pAmount * pAffineTP.z;
      }
    } else { // asteria
      xx = x0 * cosa - y0 * sina;
      yy = x0 * sina + y0 * cosa;
      double nx = xx / sqrt(1. - yy * yy) * (1. - sqrt(1. - sqr(-fabs(yy) + 1.)));
      xx = nx * cosa + yy * sina;
      yy = -nx * sina + yy * cosa;
      pVarTP.x += xx;
      pVarTP.y += yy;
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
    return new Object[]{alpha};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ALPHA.equalsIgnoreCase(pName))
      alpha = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "asteria";
  }

  private double sina, cosa;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    sina = sin(M_PI * alpha);
    cosa = cos(M_PI * alpha);
  }

}
