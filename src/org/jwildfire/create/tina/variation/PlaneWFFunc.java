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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;

import java.util.HashMap;
import java.util.Map;

import static org.jwildfire.base.mathlib.MathLib.sqrt;

public class PlaneWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POSITION = "position";
  private static final String PARAM_SIZE = "size";
  private static final String PARAM_AXIS = "axis";
  private static final String PARAM_DIRECT_COLOR = "direct_color";
  private static final String PARAM_COLOR_MODE = "color_mode";
  private static final String PARAM_BLEND_COLORMAP = "blend_colormap";
  private static final String PARAM_DISPL_AMOUNT = "displ_amount";
  private static final String PARAM_BLEND_DISPLMAP = "blend_displ_map";
  private static final String PARAM_CALC_COLORIDX = "calc_color_idx";
  private static final String PARAM_RECEIVE_ONLY_SHADOWS = "receive_only_shadows";

  private static final String RESSOURCE_COLORMAP_FILENAME = "colormap_filename";
  private static final String RESSOURCE_DISPL_MAP_FILENAME = "displ_map_filename";

  private static final String[] paramNames = {PARAM_POSITION, PARAM_SIZE, PARAM_AXIS, PARAM_DIRECT_COLOR, PARAM_COLOR_MODE, PARAM_BLEND_COLORMAP, PARAM_DISPL_AMOUNT, PARAM_BLEND_DISPLMAP, PARAM_CALC_COLORIDX, PARAM_RECEIVE_ONLY_SHADOWS};

  private static final String[] ressourceNames = {RESSOURCE_COLORMAP_FILENAME, RESSOURCE_DISPL_MAP_FILENAME};

  private static final int AXIS_XY = 0;
  private static final int AXIS_YZ = 1;
  private static final int AXIS_ZX = 2;

  private static final int CM_COLORMAP = 0;
  private static final int CM_U = 1;
  private static final int CM_V = 2;
  private static final int CM_UV = 3;

  private double position = 3.0;
  private double size = 10.0;
  private int axis = AXIS_ZX;
  private int direct_color = 1;
  private int color_mode = CM_UV;
  private int calc_color_idx = 0;

  private int receive_only_shadows = 0;

  private ColorMapHolder colorMapHolder = new ColorMapHolder();
  private DisplacementMapHolder displacementMapHolder = new DisplacementMapHolder();

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = 0.0, y = 0.0, z = 0.0;
    switch (axis) {
      case AXIS_XY:
        x = 0.5 - pContext.random();
        y = 0.5 - pContext.random();
        z = position + getDisplacement(x, y);
        setColor(pVarTP, x, y);
        x *= size;
        y *= size;
        break;
      case AXIS_YZ:
        y = 0.5 - pContext.random();
        z = 0.5 - pContext.random();
        x = position + getDisplacement(y, z);
        setColor(pVarTP, y, z);
        y *= size;
        z *= size;
        break;
      case AXIS_ZX:
        x = 0.5 - pContext.random();
        z = 0.5 - pContext.random();
        y = position + getDisplacement(z, x);
        setColor(pVarTP, z, x);
        x *= size;
        z *= size;
        break;
      default: // nothing to do
        break;
    }

    pVarTP.x += pAmount * x;
    pVarTP.y += pAmount * y;
    pVarTP.z += pAmount * z;
  }

  private double getDisplacement(double u, double v) {
    if (displacementMapHolder.isActive()) {
      double iu = (u + 0.5) * displacementMapHolder.getDisplacementMapWidth();
      double iv = (v + 0.5) * displacementMapHolder.getDisplacementMapHeight();
      int ix = (int) MathLib.trunc(iu);
      int iy = (int) MathLib.trunc(iv);
      return displacementMapHolder.calculateImageDisplacement(ix, iy, iu, iv) * displacementMapHolder.getDispl_amount();
    }
    return 0.0;
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
        case CM_COLORMAP: {
          double iu = (u + 0.5) * colorMapHolder.getColorMapWidth();
          double iv = (v + 0.5) * colorMapHolder.getColorMapHeight();
          int ix = (int) MathLib.trunc(iu);
          int iy = (int) MathLib.trunc(iv);
          colorMapHolder.applyImageColor(pVarTP, ix, iy, iu, iv);
          if (calc_color_idx == 1) {
            pVarTP.color = getUVColorIdx(Tools.FTOI(pVarTP.redColor), Tools.FTOI(pVarTP.greenColor), Tools.FTOI(pVarTP.blueColor));
          }
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
    if (receive_only_shadows == 1) {
      pVarTP.receiveOnlyShadows = true;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{position, size, axis, direct_color, color_mode, colorMapHolder.getBlend_colormap(), displacementMapHolder.getDispl_amount(), displacementMapHolder.getBlend_displ_map(), calc_color_idx, receive_only_shadows};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POSITION.equalsIgnoreCase(pName)) {
      position = pValue;
    } else if (PARAM_SIZE.equalsIgnoreCase(pName)) {
      size = pValue;
    } else if (PARAM_AXIS.equalsIgnoreCase(pName)) {
      axis = limitIntVal(Tools.FTOI(pValue), AXIS_XY, AXIS_ZX);
    } else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName)) {
      direct_color = limitIntVal(Tools.FTOI(pValue), 0, 1);
    } else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) {
      color_mode = limitIntVal(Tools.FTOI(pValue), CM_COLORMAP, CM_UV);
    } else if (PARAM_BLEND_COLORMAP.equalsIgnoreCase(pName)) {
      colorMapHolder.setBlend_colormap(limitIntVal(Tools.FTOI(pValue), 0, 1));
    } else if (PARAM_BLEND_DISPLMAP.equalsIgnoreCase(pName)) {
      displacementMapHolder.setBlend_displ_map(limitIntVal(Tools.FTOI(pValue), 0, 1));
    } else if (PARAM_DISPL_AMOUNT.equalsIgnoreCase(pName)) {
      displacementMapHolder.setDispl_amount(pValue);
    } else if (PARAM_CALC_COLORIDX.equalsIgnoreCase(pName)) {
      calc_color_idx = limitIntVal(Tools.FTOI(pValue), 0, 1);
    } else if (PARAM_RECEIVE_ONLY_SHADOWS.equalsIgnoreCase(pName)) {
      receive_only_shadows = limitIntVal(Tools.FTOI(pValue), 0, 1);
    } else
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
    return new byte[][]{(colorMapHolder.getColormap_filename() != null ? colorMapHolder.getColormap_filename().getBytes() : null), (displacementMapHolder.getDispl_map_filename() != null ? displacementMapHolder.getDispl_map_filename().getBytes() : null)};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      colorMapHolder.setColormap_filename(pValue != null ? new String(pValue) : "");
    } else if (RESSOURCE_DISPL_MAP_FILENAME.equalsIgnoreCase(pName)) {
      displacementMapHolder.setDispl_map_filename(pValue != null ? new String(pValue) : "");
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_DISPL_MAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    colorMapHolder.init();
    uvColors = pLayer.getPalette().createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    displacementMapHolder.init();
  }

  private RenderColor[] uvColors;
  protected Map<RenderColor, Double> uvIdxMap = new HashMap<RenderColor, Double>();

  private double getUVColorIdx(int pR, int pG, int pB) {
    RenderColor pColor = new RenderColor(pR, pG, pB);
    Double res = uvIdxMap.get(pColor);
    if (res == null) {

      int nearestIdx = 0;
      RenderColor color = uvColors[0];
      double dr, dg, db;
      dr = (color.red - pR);
      dg = (color.green - pG);
      db = (color.blue - pB);
      double nearestDist = sqrt(dr * dr + dg * dg + db * db);
      for (int i = 1; i < uvColors.length; i++) {
        color = uvColors[i];
        dr = (color.red - pR);
        dg = (color.green - pG);
        db = (color.blue - pB);
        double dist = sqrt(dr * dr + dg * dg + db * db);
        if (dist < nearestDist) {
          nearestDist = dist;
          nearestIdx = i;
        }
      }
      res = (double) nearestIdx / (double) (uvColors.length - 1);
      uvIdxMap.put(pColor, res);
    }
    return res;
  }

}
