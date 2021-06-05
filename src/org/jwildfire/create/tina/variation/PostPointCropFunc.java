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

public class PostPointCropFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X = "x_offset";
  private static final String PARAM_Y = "y_offset";
  private static final String PARAM_Z = "z_offset";
  private static final String PARAM_HIDE = "point_hide_enable";
  private static final String PARAM_CROP = "point_crop_enable";
  private static final String PARAM_XWIDTH = "x_width";
  private static final String PARAM_YWIDTH = "y_width";
  private static final String PARAM_ZWIDTH = "z_width";

  private static final String[] paramNames = {PARAM_X, PARAM_Y, PARAM_Z, PARAM_HIDE, PARAM_CROP, PARAM_XWIDTH, PARAM_YWIDTH, PARAM_ZWIDTH};
  private double x_offset = 0.0;
  private double y_offset = 0.0;
  private double z_offset = 0.0;
  private int point_hide_enable = 1;
  private int point_crop_enable = 1;
  private double x_width = 0.0;
  private double y_width = 0.0;
  private double z_width = 0.0;
  private double post_scrop_x, post_scrop_y, post_scrop_z, post_scrop_c;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // post_point_crop by Whittaker Courtney
    // Based on post_smartcrop by Zy0rg, http://zy0rg.deviantart.com/art/SmartCrop-267036043
    // Isolated the post_smartcrop's ability to remove dots while
    // increasing density(?) and combined with another dot removal method.
    // I'm not sure how exactly it works but it makes an improvement with both implemented.
    // x, y and z are point offsets and the widths are for removing lines.

    boolean zero = pVarTP.x == (0+ x_offset) && pVarTP.y == (0+ y_offset) && pVarTP.z == (0+ z_offset);
    boolean boundaryX = (pVarTP.x - x_offset) <= x_width && (pVarTP.x - x_offset) >= -x_width;
    boolean boundaryY = (pVarTP.y - y_offset) <= y_width && (pVarTP.y - y_offset) >= -y_width;
    boolean boundaryZ = (pVarTP.z - z_offset) <= z_width && (pVarTP.z - z_offset) >= -z_width && pVarTP.z != 0;
    boolean boundaries;

    // Determines whether to calculate the boundaries based on point or width location.
    if (x_width + y_width + z_width != 0)
    {
      boundaries = boundaryX || boundaryY || boundaryZ;
    }
    else
    {
      boundaries = zero;
    }

    // Hide and(or) crop if within boundaries
    if (boundaries) {
      if (point_crop_enable == 1)
      {
        pVarTP.x = post_scrop_x;
        pVarTP.y = post_scrop_y;
        pVarTP.z = post_scrop_z;
        pVarTP.color = post_scrop_c;
      }
      if (point_hide_enable == 1)
      {
        pVarTP.doHide = true;
      }
    }
    else {
      if (point_hide_enable == 1)
      {
        pVarTP.doHide = false;
      }
      if (point_crop_enable == 1)
      {
        post_scrop_x = pVarTP.x;
        post_scrop_y = pVarTP.y;
        post_scrop_z = pVarTP.z;
        post_scrop_c = pVarTP.color;
      }
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
    return new Object[] {x_offset, y_offset, z_offset, point_hide_enable, point_crop_enable, x_width, y_width, z_width};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X.equalsIgnoreCase(pName))
      x_offset = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y_offset = pValue;
    else if (PARAM_Z.equalsIgnoreCase(pName))
      z_offset = pValue;
    else if (PARAM_HIDE.equalsIgnoreCase(pName))
      point_hide_enable = (int) Tools.limitValue(pValue, 0, 1);
    else if (PARAM_CROP.equalsIgnoreCase(pName))
      point_crop_enable = (int) Tools.limitValue(pValue, 0, 1);
    else if (PARAM_XWIDTH.equalsIgnoreCase(pName))
      x_width = pValue;
    else if (PARAM_YWIDTH.equalsIgnoreCase(pName))
      y_width = pValue;
    else if (PARAM_ZWIDTH.equalsIgnoreCase(pName))
      z_width = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "post_point_crop";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_POST, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "   short zero = __px == (0+ varpar->post_point_crop_x_offset) && __py == (0+ varpar->post_point_crop_y_offset) && __pz == (0+ varpar->post_point_crop_z_offset);\n"
        + "    short boundaryX = (__px - varpar->post_point_crop_x_offset) <= varpar->post_point_crop_x_width && (__px - varpar->post_point_crop_x_offset) >= -varpar->post_point_crop_x_width;\n"
        + "    short boundaryY = (__py - varpar->post_point_crop_y_offset) <= varpar->post_point_crop_y_width && (__py - varpar->post_point_crop_y_offset) >= -varpar->post_point_crop_y_width;\n"
        + "    short boundaryZ = (__pz - varpar->post_point_crop_z_offset) <= varpar->post_point_crop_z_width && (__pz - varpar->post_point_crop_z_offset) >= -varpar->post_point_crop_z_width && __pz != 0;\n"
        + "    short boundaries;\n"
        + "\n"
        + "    if (varpar->post_point_crop_x_width + varpar->post_point_crop_y_width + varpar->post_point_crop_z_width != 0)\n"
        + "    {\n"
        + "      boundaries = boundaryX || boundaryY || boundaryZ;\n"
        + "    }\n"
        + "    else\n"
        + "    {\n"
        + "      boundaries = zero;\n"
        + "    }\n"
        + "\n"
        + "    if (boundaries) {\n"
        + "      if (varpar->post_point_crop_point_crop_enable == 1)\n"
        + "      {\n"
        + "        __px = __jwf%d_post_scrop_x;\n"
        + "        __py = __jwf%d_post_scrop_y;\n"
        + "        __pz = __jwf%d_post_scrop_z;\n"
        + "        __pal = __jwf%d_post_scrop_c;\n"
        + "      }\n"
        + "      if (varpar->post_point_crop_point_hide_enable == 1)\n"
        + "      {\n"
        + "        __doHide = true;\n"
        + "      }\n"
        + "    }\n"
        + "    else {\n"
        + "      if (varpar->post_point_crop_point_hide_enable == 1)\n"
        + "      {\n"
        + "        __doHide = false;\n"
        + "      }\n"
        + "      if (varpar->post_point_crop_point_crop_enable == 1)\n"
        + "      {\n"
        + "        __jwf%d_post_scrop_x = __px;\n"
        + "        __jwf%d_post_scrop_y = __py;\n"
        + "        __jwf%d_post_scrop_z = __pz;\n"
        + "        __jwf%d_post_scrop_c = __pal;\n"
        + "      }\n"
        + (context.isPreserveZCoordinate() ? "__pz += varpar->post_point_crop * __z;\n" : "")
        + "    }";
  }

  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return "__device__ float __jwf%d_post_scrop_x, __jwf%d_post_scrop_y, __jwf%d_post_scrop_z, __jwf%d_post_scrop_c;\n";
  }

  @Override
  public boolean isStateful() {
    return true;
  }
}
