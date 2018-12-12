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

public class DCLinearFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_OFFSET = "offset";
  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_SCALE = "scale";

  private static final String[] paramNames = {PARAM_OFFSET, PARAM_ANGLE, PARAM_SCALE};

  private double offset = 0.0;
  private double angle = 0.3;
  private double scale = 0.8;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* dc_linear by Xyrus02, http://apophysis-7x.org/extensions */
    pVarTP.x += pAmount * pAffineTP.x;
    pVarTP.y += pAmount * pAffineTP.y;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
    double s = sin(angle);
    double c = cos(angle);
    pVarTP.color = fmod(fabs(0.5 * (ldcs * ((c * pVarTP.x + s * pVarTP.y + offset)) + 1.0)), 1.0);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{offset, angle, scale};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_OFFSET.equalsIgnoreCase(pName))
      offset = pValue;
    else if (PARAM_ANGLE.equalsIgnoreCase(pName))
      angle = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "dc_linear";
  }

  private double ldcs;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    ldcs = 1.0 / (scale == 0.0 ? 10E-6 : scale);
  }

}
