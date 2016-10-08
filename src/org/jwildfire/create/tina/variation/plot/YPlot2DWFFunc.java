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

package org.jwildfire.create.tina.variation.plot;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import java.util.HashMap;
import java.util.Map;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.base.mathlib.VecMathLib.VectorD;
import org.jwildfire.base.mathparser.JEPWrapper;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.variation.ColorMapHolder;
import org.jwildfire.create.tina.variation.DisplacementMapHolder;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceType;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.nfunk.jep.Node;

public class YPlot2DWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PRESET_ID = "preset_id";
  private static final String PARAM_XMIN = "xmin";
  private static final String PARAM_XMAX = "xmax";
  private static final String PARAM_YMIN = "ymin";
  private static final String PARAM_YMAX = "ymax";
  private static final String PARAM_ZMIN = "zmin";
  private static final String PARAM_ZMAX = "zmax";
  private static final String PARAM_DIRECT_COLOR = "direct_color";
  private static final String PARAM_COLOR_MODE = "color_mode";
  private static final String PARAM_BLEND_COLORMAP = "blend_colormap";
  private static final String PARAM_DISPL_AMOUNT = "displ_amount";
  private static final String PARAM_BLEND_DISPLMAP = "blend_displ_map";

  private static final String RESSOURCE_FORMULA = "formula";
  private static final String RESSOURCE_COLORMAP_FILENAME = "colormap_filename";
  private static final String RESSOURCE_DISPL_MAP_FILENAME = "displ_map_filename";

  private static final String[] paramNames = { PARAM_PRESET_ID, PARAM_XMIN, PARAM_XMAX, PARAM_YMIN, PARAM_YMAX, PARAM_ZMIN, PARAM_ZMAX, PARAM_DIRECT_COLOR, PARAM_COLOR_MODE, PARAM_BLEND_COLORMAP, PARAM_DISPL_AMOUNT, PARAM_BLEND_DISPLMAP };

  private static final String[] ressourceNames = { RESSOURCE_FORMULA, RESSOURCE_COLORMAP_FILENAME, RESSOURCE_DISPL_MAP_FILENAME };

  private int preset_id = -1;

  private double xmin = -3.0;
  private double xmax = 2.0;
  private double ymin = -4.0;
  private double ymax = 4.0;
  private double zmin = -2.0;
  private double zmax = 2.0;
  private int direct_color = 1;
  private int color_mode = CM_X;

  private static final int CM_COLORMAP = 0;
  private static final int CM_X = 1;
  private static final int CM_Y = 2;

  private String formula;

  private ColorMapHolder colorMapHolder = new ColorMapHolder();
  private DisplacementMapHolder displacementMapHolder = new DisplacementMapHolder();

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double randU = pContext.random();
    double randV = pContext.random();
    double x = _xmin + randU * _dx;
    double z = _zmin + randV * _dz;
    _parser.setVarValue("x", x);
    double y = (Double) _parser.evaluate(_node);

    if (displacementMapHolder.isActive()) {
      double eps = _dx / 100.0;
      double x1 = x + eps;
      _parser.setVarValue("x", x1);
      double y1 = (Double) _parser.evaluate(_node);

      VectorD av = new VectorD(eps, y1 - y, 0);
      VectorD bv = new VectorD(0.0, 0.0, 1.0);
      VectorD n = VectorD.cross(av, bv);
      n.normalize();
      double iu = GfxMathLib.clamp(randU * (displacementMapHolder.getDisplacementMapWidth() - 1.0), 0.0, displacementMapHolder.getDisplacementMapWidth() - 1.0);
      double iv = GfxMathLib.clamp(displacementMapHolder.getDisplacementMapHeight() - 1.0 - randV * (displacementMapHolder.getDisplacementMapHeight() - 1.0), 0, displacementMapHolder.getDisplacementMapHeight() - 1.0);
      int ix = (int) MathLib.trunc(iu);
      int iy = (int) MathLib.trunc(iv);
      double d = displacementMapHolder.calculateImageDisplacement(ix, iy, iu, iv) * _displ_amount;
      pVarTP.x += pAmount * n.x * d;
      pVarTP.y += pAmount * n.y * d;
      pVarTP.z += pAmount * n.z * d;
    }

    if (direct_color > 0) {
      switch (color_mode) {
        case CM_COLORMAP:
          if (colorMapHolder.isActive()) {
            double iu = GfxMathLib.clamp(randU * (colorMapHolder.getColorMapWidth() - 1.0), 0.0, colorMapHolder.getColorMapWidth() - 1.0);
            double iv = GfxMathLib.clamp(colorMapHolder.getColorMapHeight() - 1.0 - randV * (colorMapHolder.getColorMapHeight() - 1.0), 0, colorMapHolder.getColorMapHeight() - 1.0);
            int ix = (int) MathLib.trunc(iu);
            int iy = (int) MathLib.trunc(iv);
            colorMapHolder.applyImageColor(pVarTP, ix, iy, iu, iv);
            pVarTP.color = getUVColorIdx(Tools.FTOI(pVarTP.redColor), Tools.FTOI(pVarTP.greenColor), Tools.FTOI(pVarTP.blueColor));
          }
          break;
        case CM_Y:
          pVarTP.color = (y - _ymin) / _dy;
          break;
        default:
        case CM_X:
          pVarTP.color = (x - _xmin) / _dx;
          break;
      }
      if (pVarTP.color < 0.0)
        pVarTP.color = 0.0;
      else if (pVarTP.color > 1.0)
        pVarTP.color = 1.0;
    }
    pVarTP.x += pAmount * x;
    pVarTP.y += pAmount * y;
    pVarTP.z += pAmount * z;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { preset_id, xmin, xmax, ymin, ymax, zmin, zmax, direct_color, color_mode, colorMapHolder.getBlend_colormap(), displacementMapHolder.getDispl_amount(), displacementMapHolder.getBlend_displ_map() };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PRESET_ID.equalsIgnoreCase(pName)) {
      preset_id = Tools.FTOI(pValue);
      if (preset_id >= 0) {
        refreshFormulaFromPreset(preset_id);
      }
    }
    else if (PARAM_XMIN.equalsIgnoreCase(pName)) {
      xmin = pValue;
      validatePresetId();
    }
    else if (PARAM_XMAX.equalsIgnoreCase(pName)) {
      xmax = pValue;
      validatePresetId();
    }
    else if (PARAM_YMIN.equalsIgnoreCase(pName)) {
      ymin = pValue;
    }
    else if (PARAM_YMAX.equalsIgnoreCase(pName)) {
      ymax = pValue;
    }
    else if (PARAM_ZMIN.equalsIgnoreCase(pName)) {
      zmin = pValue;
    }
    else if (PARAM_ZMAX.equalsIgnoreCase(pName)) {
      zmax = pValue;
    }
    else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName)) {
      direct_color = Tools.FTOI(pValue);
    }
    else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) {
      color_mode = Tools.FTOI(pValue);
    }
    else if (PARAM_BLEND_COLORMAP.equalsIgnoreCase(pName)) {
      colorMapHolder.setBlend_colormap(limitIntVal(Tools.FTOI(pValue), 0, 1));
    }
    else if (PARAM_DISPL_AMOUNT.equalsIgnoreCase(pName)) {
      displacementMapHolder.setDispl_amount(pValue);
    }
    else if (PARAM_BLEND_DISPLMAP.equalsIgnoreCase(pName)) {
      displacementMapHolder.setBlend_displ_map(limitIntVal(Tools.FTOI(pValue), 0, 1));
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "yplot2d_wf";
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][] { (formula != null ? formula.getBytes() : null), (colorMapHolder.getColormap_filename() != null ? colorMapHolder.getColormap_filename().getBytes() : null), (displacementMapHolder.getDispl_map_filename() != null ? displacementMapHolder.getDispl_map_filename().getBytes() : null) };
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_FORMULA.equalsIgnoreCase(pName)) {
      formula = pValue != null ? new String(pValue) : "";
      validatePresetId();
    }
    else if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      colorMapHolder.setColormap_filename(pValue != null ? new String(pValue) : "");
      colorMapHolder.clear();
      uvIdxMap.clear();
    }
    else if (RESSOURCE_DISPL_MAP_FILENAME.equalsIgnoreCase(pName)) {
      displacementMapHolder.setDispl_map_filename(pValue != null ? new String(pValue) : "");
      displacementMapHolder.clear();
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_FORMULA.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    }
    else if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    }
    else if (RESSOURCE_DISPL_MAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    }
    else
      throw new IllegalArgumentException(pName);
  }

  private JEPWrapper _parser;
  private Node _node;
  private double _xmin, _xmax, _dx;
  private double _ymin, _ymax, _dy;
  private double _zmin, _zmax, _dz;
  private double _displ_amount;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    colorMapHolder.init();
    uvColors = pLayer.getPalette().createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    displacementMapHolder.init();
    _displ_amount = displacementMapHolder.getDispl_amount() / 255.0;

    _parser = new JEPWrapper();
    _parser.addVariable("x", 0.0);
    _node = _parser.parse(formula);

    _xmin = xmin;
    _xmax = xmax;
    if (_xmin > _xmax) {
      double t = _xmin;
      _xmin = _xmax;
      _xmax = t;
    }
    _dx = _xmax - _xmin;

    _ymin = ymin;
    _ymax = ymax;
    if (_ymin > _ymax) {
      double t = _ymin;
      _ymin = _ymax;
      _ymax = t;
    }
    _dy = _ymax - _ymin;

    _zmin = zmin;
    _zmax = zmax;
    if (_zmin > _zmax) {
      double t = _zmin;
      _zmin = _zmax;
      _zmax = t;
    }
    _dz = _zmax - _zmin;
  }

  public YPlot2DWFFunc() {
    super();
    preset_id = WFFuncPresetsStore.getYPlot2DWFFuncPresets().getRandomPresetId();
    refreshFormulaFromPreset(preset_id);
  }

  private void validatePresetId() {
    if (preset_id >= 0) {
      YPlot2DWFFuncPreset preset = WFFuncPresetsStore.getYPlot2DWFFuncPresets().getPreset(preset_id);
      if (!preset.getFormula().equals(formula) ||
          (fabs(xmin - preset.getXmin()) > EPSILON) || (fabs(xmax - preset.getXmax()) > EPSILON)) {
        preset_id = -1;
      }
    }
  }

  private void refreshFormulaFromPreset(int presetId) {
    YPlot2DWFFuncPreset preset = WFFuncPresetsStore.getYPlot2DWFFuncPresets().getPreset(presetId);
    formula = preset.getFormula();
    xmin = preset.getXmin();
    xmax = preset.getXmax();
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
