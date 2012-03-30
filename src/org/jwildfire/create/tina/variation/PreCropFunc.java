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

import static org.jwildfire.base.MathLib.max;
import static org.jwildfire.base.MathLib.min;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class PreCropFunc extends VariationFunc {

  private static final String PARAM_LEFT = "left";
  private static final String PARAM_RIGHT = "right";
  private static final String PARAM_TOP = "top";
  private static final String PARAM_BOTTOM = "bottom";
  private static final String PARAM_SCATTER_AREA = "scatter_area";

  private static final String[] paramNames = { PARAM_LEFT, PARAM_RIGHT, PARAM_TOP, PARAM_BOTTOM, PARAM_SCATTER_AREA };

  private double left = -1.0;
  private double top = 1.0;
  private double right = 1.0;
  private double bottom = 1.0;
  private double scatter_area = 0.0;

  private double distribute(FlameTransformationContext pContext, double a, double min, double max) {
    double distance = pContext.random() * 0.5 * A;
    return a < min ? min + distance * (max - min) :
        max - distance * (max - min);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // pre_crop by Xirus02, http://xyrus02.deviantart.com/art/Crop-Plugin-Updated-169958881
    pAffineTP.x += pAmount * ((pAffineTP.x >= xmin) && (pAffineTP.x <= xmax) ? pAffineTP.x : distribute(pContext, pAffineTP.x, xmin, xmax));
    pAffineTP.y += pAmount * ((pAffineTP.y >= ymin) && (pAffineTP.y <= ymax) ? pAffineTP.y : distribute(pContext, pAffineTP.y, ymin, ymax));
    pAffineTP.z += pAmount * pAffineTP.z;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { left, right, top, bottom, scatter_area };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_LEFT.equalsIgnoreCase(pName))
      left = pValue;
    else if (PARAM_RIGHT.equalsIgnoreCase(pName))
      right = pValue;
    else if (PARAM_TOP.equalsIgnoreCase(pName))
      top = pValue;
    else if (PARAM_BOTTOM.equalsIgnoreCase(pName))
      bottom = pValue;
    else if (PARAM_SCATTER_AREA.equalsIgnoreCase(pName))
      scatter_area = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "pre_crop";
  }

  private double xmin, xmax, ymin, ymax, A;

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm, double pAmount) {
    xmin = min(left, right);
    ymin = min(top, bottom);
    xmax = max(left, right);
    ymax = max(top, bottom);
    A = max(-1.0, min(scatter_area, 1.0));
  }

  @Override
  public int getPriority() {
    return -1;
  }

}
