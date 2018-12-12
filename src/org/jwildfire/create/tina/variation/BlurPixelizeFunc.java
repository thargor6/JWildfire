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

import static org.jwildfire.base.mathlib.MathLib.floor;

public class BlurPixelizeFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SIZE = "size";
  private static final String PARAM_SCALE = "scale";
  private static final String[] paramNames = {PARAM_SIZE, PARAM_SCALE};

  private double size = 0.1;
  private double scale = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* blur_pixelize from Apo7X15C */
    double x = floor(pAffineTP.x * (_inv_size));
    double y = floor(pAffineTP.y * (_inv_size));

    pVarTP.x += _v * (x + (this.scale) * (pContext.random() - 0.5) + 0.5);
    pVarTP.y += _v * (y + (this.scale) * (pContext.random() - 0.5) + 0.5);
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
    return new Object[]{size, scale};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SIZE.equalsIgnoreCase(pName))
      size = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "blur_pixelize";
  }

  private double _inv_size, _v;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _inv_size = 1.0 / this.size;
    _v = pAmount * this.size;
  }

}
