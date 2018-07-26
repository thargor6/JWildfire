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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.variation.ColorMapHolder;
import org.jwildfire.create.tina.variation.DisplacementMapHolder;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceType;
import org.jwildfire.create.tina.variation.VariationFunc;

public class YPlot3DWFFunc extends VariationFunc {
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

  private static final String PARAM_PARAM_A = "param_a";
  private static final String PARAM_PARAM_B = "param_b";
  private static final String PARAM_PARAM_C = "param_c";
  private static final String PARAM_PARAM_D = "param_d";
  private static final String PARAM_PARAM_E = "param_e";
  private static final String PARAM_PARAM_F = "param_f";

  private static final String[] paramNames = { PARAM_PRESET_ID, PARAM_XMIN, PARAM_XMAX, PARAM_YMIN, PARAM_YMAX, PARAM_ZMIN, PARAM_ZMAX, PARAM_DIRECT_COLOR, PARAM_COLOR_MODE, PARAM_BLEND_COLORMAP, PARAM_DISPL_AMOUNT, PARAM_BLEND_DISPLMAP, PARAM_PARAM_A, PARAM_PARAM_B, PARAM_PARAM_C, PARAM_PARAM_D, PARAM_PARAM_E, PARAM_PARAM_F };

  private static final String[] ressourceNames = { RESSOURCE_FORMULA, RESSOURCE_COLORMAP_FILENAME, RESSOURCE_DISPL_MAP_FILENAME };

  private int preset_id = -1;

  private double xmin = -3.0;
  private double xmax = 2.0;
  private double ymin = -4.0;
  private double ymax = 4.0;
  private double zmin = -2.0;
  private double zmax = 2.0;
  private int direct_color = 1;
  private int color_mode = CM_Z;

  private double param_a = 0.0;
  private double param_b = 0.0;
  private double param_c = 0.0;
  private double param_d = 0.0;
  private double param_e = 0.0;
  private double param_f = 0.0;

  private static final int CM_COLORMAP = 0;
  private static final int CM_X = 1;
  private static final int CM_Y = 2;
  private static final int CM_Z = 3;
  private static final int CM_XZ = 4;

  private String formula;

  private ColorMapHolder colorMapHolder = new ColorMapHolder();
  private DisplacementMapHolder displacementMapHolder = new DisplacementMapHolder();

  private YPlot3DFormulaEvaluator evaluator;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (evaluator == null) {
      return;
    }
    double randU = pContext.random();
    double randV = pContext.random();
    double x = _xmin + randU * _dx;
    double z = _zmin + randV * _dz;
    double y = evaluator.evaluate(x, z);

    if (displacementMapHolder.isActive()) {
      double epsx = _dx / 100.0;
      double x1 = x + epsx;
      double y1 = evaluator.evaluate(x1, z);

      double epsz = _dz / 100.0;
      double z1 = z + epsz;
      double y2 = evaluator.evaluate(x, z1);

      VectorD av = new VectorD(epsx, y1 - y, 0);
      VectorD bv = new VectorD(0.0, y2 - y, epsz);
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
        case CM_X:
          pVarTP.color = (x - _xmin) / _dx;
          break;
        case CM_Y:
          pVarTP.color = (y - _ymin) / _dy;
          break;
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
        case CM_XZ:
          pVarTP.color = (x - _xmin) / _dx * (z - _zmin) / _dz;
          break;
        default:
        case CM_Z:
          pVarTP.color = (z - _zmin) / _dz;
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
    return new Object[] { preset_id, xmin, xmax, ymin, ymax, zmin, zmax, direct_color, color_mode, colorMapHolder.getBlend_colormap(), displacementMapHolder.getDispl_amount(), displacementMapHolder.getBlend_displ_map(), param_a, param_b, param_c, param_d, param_e, param_f };
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
      validatePresetId();
    }
    else if (PARAM_YMAX.equalsIgnoreCase(pName)) {
      ymax = pValue;
      validatePresetId();
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
    else if (PARAM_PARAM_A.equalsIgnoreCase(pName)) {
      param_a = pValue;
    }
    else if (PARAM_PARAM_B.equalsIgnoreCase(pName)) {
      param_b = pValue;
    }
    else if (PARAM_PARAM_C.equalsIgnoreCase(pName)) {
      param_c = pValue;
    }
    else if (PARAM_PARAM_D.equalsIgnoreCase(pName)) {
      param_d = pValue;
    }
    else if (PARAM_PARAM_E.equalsIgnoreCase(pName)) {
      param_e = pValue;
    }
    else if (PARAM_PARAM_F.equalsIgnoreCase(pName)) {
      param_f = pValue;
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "yplot3d_wf";
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

  private double _xmin, _xmax, _dx;
  private double _ymin, _ymax, _dy;
  private double _zmin, _zmax, _dz;
  private double _displ_amount;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    colorMapHolder.init();
    uvColors = pLayer.getPalette().createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    displacementMapHolder.init();
    _displ_amount = displacementMapHolder.getDispl_amount();

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

    evaluator = null;
    if (!formula.isEmpty()) {
      String code = "import static org.jwildfire.base.mathlib.MathLib.*;\r\n" +
          "\r\n" +
          "  public double evaluate(double x,double z) {\r\n" +
          "    double pi = M_PI;\r\n" +
          "    double param_a = " + param_a + ";\r\n" +
          "    double param_b = " + param_b + ";\r\n" +
          "    double param_c = " + param_c + ";\r\n" +
          "    double param_d = " + param_d + ";\r\n" +
          "    double param_e = " + param_e + ";\r\n" +
          "    double param_f = " + param_f + ";\r\n" +
          "    return " + formula + ";\r\n" +
          "  }\r\n";

      try {
        evaluator = YPlot3DFormulaEvaluator.compile(code);
      }
      catch (Exception e) {
        evaluator = null;
        e.printStackTrace();
        System.out.println(code);
        throw new IllegalArgumentException(e);
      }
    }

  }

  public YPlot3DWFFunc() {
    super();
    preset_id = WFFuncPresetsStore.getYPlot3DWFFuncPresets().getRandomPresetId();
    refreshFormulaFromPreset(preset_id);
  }

  private void validatePresetId() {
    if (preset_id >= 0) {
      YPlot3DWFFuncPreset preset = WFFuncPresetsStore.getYPlot3DWFFuncPresets().getPreset(preset_id);
      if (!preset.getFormula().equals(formula) ||
          (fabs(xmin - preset.getXmin()) > EPSILON) || (fabs(xmax - preset.getXmax()) > EPSILON) ||
          (fabs(ymin - preset.getYmin()) > EPSILON) || (fabs(ymax - preset.getYmax()) > EPSILON)) {
        preset_id = -1;
      }
    }
  }

  private void refreshFormulaFromPreset(int presetId) {
    YPlot3DWFFuncPreset preset = WFFuncPresetsStore.getYPlot3DWFFuncPresets().getPreset(presetId);
    formula = preset.getFormula();
    xmin = preset.getXmin();
    xmax = preset.getXmax();
    ymin = preset.getYmin();
    ymax = preset.getYmax();

    param_a = preset.getParam_a();
    param_b = preset.getParam_b();
    param_c = preset.getParam_c();
    param_d = preset.getParam_d();
    param_e = preset.getParam_e();
    param_f = preset.getParam_f();
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

  @Override
  public boolean dynamicParameterExpansion() { return true; }
  
  @Override
  public boolean dynamicParameterExpansion(String pName) {
	  // preset_id doesn't really expand parameters, but it changes them; this will make them refresh
	  if (PARAM_PRESET_ID.equalsIgnoreCase(pName))  return true;
	  else return false;
  }

}
