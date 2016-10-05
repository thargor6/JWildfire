/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.GradientCreator;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.mesh.AbstractOBJMeshWFFunc;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

public class PlaneWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POSITION = "position";
  private static final String PARAM_SIZE = "size";
  private static final String PARAM_AXIS = "axis";
  private static final String PARAM_DIRECT_COLOR = "direct_color";
  private static final String PARAM_COLOR_MODE = "color_mode";
  private static final String PARAM_COLOR1 = "color1";
  private static final String PARAM_COLOR2 = "color2";
  private static final String PARAM_TEXTURE_PARAM1 = "texture_param1";
  private static final String PARAM_TEXTURE_PARAM2 = "texture_param2";
  private static final String PARAM_BLEND_COLORMAP = "blend_colormap";

  private static final String RESSOURCE_COLORMAP_FILENAME = "colormap_filename";

  private static final String[] paramNames = { PARAM_POSITION, PARAM_SIZE, PARAM_AXIS, PARAM_DIRECT_COLOR, PARAM_COLOR_MODE, PARAM_COLOR1, PARAM_COLOR2, PARAM_TEXTURE_PARAM1, PARAM_TEXTURE_PARAM2, PARAM_BLEND_COLORMAP };

  private static final String[] ressourceNames = { RESSOURCE_COLORMAP_FILENAME };

  private static final int AXIS_XY = 0;
  private static final int AXIS_YZ = 1;
  private static final int AXIS_ZX = 2;

  private static final int CM_U = 0;
  private static final int CM_V = 1;
  private static final int CM_UV = 2;
  private static final int CM_CHECKERBOARD = 3; // uses texture_param1, color1, color2
  private static final int CM_COLORMAP = 4; // no params

  private double position = 3.0;
  private double size = 10.0;
  private int axis = AXIS_ZX;
  private int direct_color = 1;
  private int color_mode = CM_UV;

  private double color1 = Math.random() * 0.5;
  private double color2 = Math.random() * 0.5 + 0.5;
  private double texture_param1 = Math.random() + 0.5;
  private double texture_param2 = Math.random() * 0.25 + 0.75;

  private WFImage colorMap;
  private int colorMapWidth, colorMapHeight;
  private String colormap_filename = null;
  private int blend_colormap = 1;
  private Pixel toolPixel = new Pixel();
  private float[] rgbArray = new float[3];

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = 0.0, y = 0.0, z = 0.0;
    switch (axis) {
      case AXIS_XY:
        x = 0.5 - pContext.random();
        y = 0.5 - pContext.random();
        z = position;
        setColor(pVarTP, x, y);
        x *= size;
        y *= size;
        break;
      case AXIS_YZ:
        x = position;
        y = 0.5 - pContext.random();
        z = 0.5 - pContext.random();
        setColor(pVarTP, y, z);
        y *= size;
        z *= size;
        break;
      case AXIS_ZX:
        x = 0.5 - pContext.random();
        y = position;
        z = 0.5 - pContext.random();
        setColor(pVarTP, z, x);
        x *= size;
        z *= size;
      default:
        break;
    }

    pVarTP.x += pAmount * x;
    pVarTP.y += pAmount * y;
    pVarTP.z += pAmount * z;
  }

  private void setColor(XYZPoint pVarTP, double u, double v) {
    if (direct_color > 0) {
      switch (color_mode) {
        case CM_V:
          pVarTP.color = v + 0.5;
          break;
        case CM_UV:
          pVarTP.color = (v + 0.5) * (u + 0.5);
          break;
        case CM_CHECKERBOARD:
          pVarTP.color = MathLib.fmod(MathLib.floor((u + 0.5) / texture_param1) + MathLib.floor((v + 0.5) / texture_param1), 2) < 1 ? color1 : color2;
          break;
        case CM_COLORMAP: {
          double iu = (u + 0.5) * colorMapWidth;
          double iv = (v + 0.5) * colorMapHeight;
          int ix = (int) MathLib.trunc(iu);
          int iy = (int) MathLib.trunc(iv);
          AbstractOBJMeshWFFunc.applyImageColor(pVarTP, ix, iy, iu, iv, colorMap, colorMapWidth, colorMapHeight, blend_colormap, toolPixel, rgbArray);
        }
          break;
        case CM_U:
        default:
          pVarTP.color = u + 0.5;
          break;
      }
      if (pVarTP.color < 0.0)
        pVarTP.color = 0.0;
      else if (pVarTP.color > 1.0)
        pVarTP.color = 1.0;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { position, size, axis, direct_color, color_mode, color1, color2, texture_param1, texture_param2, blend_colormap };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POSITION.equalsIgnoreCase(pName)) {
      position = pValue;
    }
    else if (PARAM_SIZE.equalsIgnoreCase(pName)) {
      size = pValue;
    }
    else if (PARAM_AXIS.equalsIgnoreCase(pName)) {
      axis = limitIntVal(Tools.FTOI(pValue), AXIS_XY, AXIS_ZX);
    }
    else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName)) {
      direct_color = limitIntVal(Tools.FTOI(pValue), 0, 1);
    }
    else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) {
      color_mode = limitIntVal(Tools.FTOI(pValue), CM_U, CM_COLORMAP);
    }
    else if (PARAM_COLOR1.equalsIgnoreCase(pName)) {
      color1 = limitVal(pValue, 0.0, 1.0);
    }
    else if (PARAM_COLOR2.equalsIgnoreCase(pName)) {
      color2 = limitVal(pValue, 0.0, 1.0);
    }
    else if (PARAM_TEXTURE_PARAM1.equalsIgnoreCase(pName)) {
      texture_param1 = pValue;
    }
    else if (PARAM_TEXTURE_PARAM2.equalsIgnoreCase(pName)) {
      texture_param2 = pValue;
    }
    else if (PARAM_BLEND_COLORMAP.equalsIgnoreCase(pName)) {
      blend_colormap = limitIntVal(Tools.FTOI(pValue), 0, 1);
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "plane_wf";
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][] { (colormap_filename != null ? colormap_filename.getBytes() : null) };
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      colormap_filename = pValue != null ? new String(pValue) : "";
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    colorMap = null;
    if (colormap_filename != null && colormap_filename.length() > 0) {
      try {
        colorMap = RessourceManager.getImage(colormap_filename);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (colorMap == null) {
      colorMap = getDfltImage();
    }
    colorMapWidth = colorMap.getImageWidth();
    colorMapHeight = colorMap.getImageHeight();
  }

  private static SimpleImage dfltImage = null;

  private synchronized SimpleImage getDfltImage() {
    if (dfltImage == null) {
      GradientCreator creator = new GradientCreator();
      dfltImage = creator.createImage(256, 256);
    }
    return dfltImage;
  }

}
