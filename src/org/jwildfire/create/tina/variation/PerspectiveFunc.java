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

public class PerspectiveFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_DIST = "dist";
  private static final String[] paramNames = {PARAM_ANGLE, PARAM_DIST};

  private double angle = 0.62;
  private double dist = 2.2;

  private double vsin;
  private double vfcos;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double d = (this.dist - pAffineTP.y * this.vsin);
    if (d == 0) {
      return;
    }
    double t = 1.0 / d;
    pVarTP.x += pAmount * this.dist * pAffineTP.x * t;
    pVarTP.y += pAmount * this.vfcos * pAffineTP.y * t;
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
    return new Object[]{angle, dist};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ANGLE.equalsIgnoreCase(pName))
      angle = pValue;
    else if (PARAM_DIST.equalsIgnoreCase(pName))
      dist = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "perspective";
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    double ang = this.angle * M_PI / 2.0;
    vsin = sin(ang);
    vfcos = this.dist * cos(ang);
  }

}
