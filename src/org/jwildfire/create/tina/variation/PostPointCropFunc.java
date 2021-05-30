/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2021 Andreas Maschke
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
import org.jwildfire.base.Tools;

public class PostPointCropFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X = "x offset";
  private static final String PARAM_Y = "y offset";
  private static final String PARAM_Z = "z offset";
  private static final String PARAM_HIDE = "point hide enable";
  private static final String PARAM_CROP = "point crop enable";
  private static final String PARAM_XWIDTH = "x width";
  private static final String PARAM_YWIDTH = "y width";
  private static final String PARAM_ZWIDTH = "z width";

  private static final String[] paramNames = {PARAM_X, PARAM_Y, PARAM_Z, PARAM_HIDE, PARAM_CROP, PARAM_XWIDTH, PARAM_YWIDTH, PARAM_ZWIDTH};
  private double x = 0.0;
  private double y = 0.0;
  private double z = 0.0;
  private int hide = 1;
  private int crop = 1;
  private double xWidth = 0.0;
  private double yWidth = 0.0;
  private double zWidth = 0.0;
  private double post_scrop_x, post_scrop_y, post_scrop_z, post_scrop_c;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // post_point_crop by Whittaker Courtney
    // Based on post_smartcrop by Zy0rg, http://zy0rg.deviantart.com/art/SmartCrop-267036043
    // Isolated the post_smartcrop's ability to remove dots while
    // increasing density(?) and combined with another dot removal method.
    // I'm not sure how exactly it works but it makes an improvement with both implemented.
    // x, y and z are point offsets and the widths are for removing lines.

    boolean zero = pVarTP.x == (0+x) && pVarTP.y == (0+y) && pVarTP.z == (0+z);
    boolean boundaryX = (pVarTP.x - x) <= xWidth && (pVarTP.x - x) >= -xWidth;
    boolean boundaryY = (pVarTP.y - y) <= yWidth && (pVarTP.y - y) >= -yWidth;
    boolean boundaryZ = (pVarTP.z - z) <= zWidth && (pVarTP.z - z) >= -zWidth && pVarTP.z != 0;
    boolean boundaries;

    // Determines whether to calculate the boundaries based on point or width location.
    if (xWidth + yWidth + zWidth != 0)
    {
      boundaries = boundaryX || boundaryY || boundaryZ;
    }
    else
    {
      boundaries = zero;
    }

    // Hide and(or) crop if within boundaries
    if (boundaries) {
      if (crop == 1)
      {
        pVarTP.x = post_scrop_x;
        pVarTP.y = post_scrop_y;
        pVarTP.z = post_scrop_z;
        pVarTP.color = post_scrop_c;
      }
      if (hide == 1)
      {
        pVarTP.doHide = true;
      }
      return;
    }

    else
    {
      if (hide == 1)
      {
        pVarTP.doHide = false;
      }
      if (crop == 1)
      {
        post_scrop_x = pVarTP.x;
        post_scrop_y = pVarTP.y;
        post_scrop_z = pVarTP.z;
        post_scrop_c = pVarTP.color;
      }
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
    return new Object[] {x, y, z, hide, crop, xWidth, yWidth, zWidth};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else if (PARAM_Z.equalsIgnoreCase(pName))
      z = pValue;
    else if (PARAM_HIDE.equalsIgnoreCase(pName))
      hide = (int) Tools.limitValue(pValue, 0, 1);
    else if (PARAM_CROP.equalsIgnoreCase(pName))
      crop = (int) Tools.limitValue(pValue, 0, 1);
    else if (PARAM_XWIDTH.equalsIgnoreCase(pName))
      xWidth = pValue;
    else if (PARAM_YWIDTH.equalsIgnoreCase(pName))
      yWidth = pValue;
    else if (PARAM_ZWIDTH.equalsIgnoreCase(pName))
      zWidth = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "post_point_crop";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_POST};
  }

  @Override
  public int getPriority() {
    return 1;
  }

}
