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

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.base.mathlib.VecMathLib.VectorD;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.variation.*;

import java.util.HashMap;
import java.util.Map;

import static org.jwildfire.base.mathlib.MathLib.*;

public class ParPlot2DWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PRESET_ID = "preset_id";
  private static final String PARAM_UMIN = "umin";
  private static final String PARAM_UMAX = "umax";
  private static final String PARAM_VMIN = "vmin";
  private static final String PARAM_VMAX = "vmax";
  private static final String PARAM_DIRECT_COLOR = "direct_color";
  private static final String PARAM_COLOR_MODE = "color_mode";
  private static final String PARAM_BLEND_COLORMAP = "blend_colormap";
  private static final String PARAM_DISPL_AMOUNT = "displ_amount";
  private static final String PARAM_BLEND_DISPLMAP = "blend_displ_map";
  private static final String PARAM_SOLID = "solid";

  private static final String PARAM_PARAM_A = "param_a";
  private static final String PARAM_PARAM_B = "param_b";
  private static final String PARAM_PARAM_C = "param_c";
  private static final String PARAM_PARAM_D = "param_d";
  private static final String PARAM_PARAM_E = "param_e";
  private static final String PARAM_PARAM_F = "param_f";

  private static final String RESSOURCE_XFORMULA = "xformula";
  private static final String RESSOURCE_YFORMULA = "yformula";
  private static final String RESSOURCE_ZFORMULA = "zformula";
  private static final String RESSOURCE_COLORMAP_FILENAME = "colormap_filename";
  private static final String RESSOURCE_DISPL_MAP_FILENAME = "displ_map_filename";

  private static final String[] paramNames = {PARAM_PRESET_ID, PARAM_UMIN, PARAM_UMAX, PARAM_VMIN, PARAM_VMAX, PARAM_DIRECT_COLOR, PARAM_COLOR_MODE, PARAM_BLEND_COLORMAP, PARAM_DISPL_AMOUNT, PARAM_BLEND_DISPLMAP, PARAM_SOLID, PARAM_PARAM_A, PARAM_PARAM_B, PARAM_PARAM_C, PARAM_PARAM_D, PARAM_PARAM_E, PARAM_PARAM_F};

  private static final String[] ressourceNames = {RESSOURCE_XFORMULA, RESSOURCE_YFORMULA, RESSOURCE_ZFORMULA, RESSOURCE_COLORMAP_FILENAME, RESSOURCE_DISPL_MAP_FILENAME};

  private static final int CM_COLORMAP = 0;
  private static final int CM_U = 1;
  private static final int CM_V = 2;
  private static final int CM_UV = 3;

  private int preset_id = -1;

  private double umin = 0.0;
  private double umax = 2.0 * Math.PI;
  private double vmin = 0.0;
  private double vmax = 2.0 * Math.PI;
  private int direct_color = 1;
  private int color_mode = CM_UV;
  private int solid = 1;

  private double param_a = 0.0;
  private double param_b = 0.0;
  private double param_c = 0.0;
  private double param_d = 0.0;
  private double param_e = 0.0;
  private double param_f = 0.0;

  private String xformula;
  private String yformula;
  private String zformula;

  private ColorMapHolder colorMapHolder = new ColorMapHolder();
  private DisplacementMapHolder displacementMapHolder = new DisplacementMapHolder();

  private ParPlot2DFormulaEvaluator evaluator;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (evaluator == null) {
      return;
    }
    double randU, randV;
    if (solid == 0) {
      randU = pAffineTP.x;
      randV = pAffineTP.y;
    } else {
      randU = pContext.random();
      randV = pContext.random();
    }
    double u = _umin + randU * _du;
    double v = _vmin + randV * _dv;

    double x = evaluator.evaluateX(u, v);
    double y = evaluator.evaluateY(u, v);
    double z = evaluator.evaluateZ(u, v);

    if (displacementMapHolder.isActive()) {
      double epsu = _du / 100.0;
      double u1 = u + epsu;
      double x1 = evaluator.evaluateX(u1, v);
      double y1 = evaluator.evaluateY(u1, v);
      double z1 = evaluator.evaluateZ(u1, v);

      double epsv = _dv / 100.0;
      double v1 = v + epsv;

      double x2 = evaluator.evaluateX(u, v1);
      double y2 = evaluator.evaluateY(u, v1);
      double z2 = evaluator.evaluateZ(u, v1);

      VectorD av = new VectorD(x1 - x, y1 - y, z1 - z);
      VectorD bv = new VectorD(x2 - x, y2 - y, z2 - z);
      VectorD n = VectorD.cross(av, bv);
      n.normalize();
      double iu = GfxMathLib.clamp(randU * (displacementMapHolder.getDisplacementMapWidth() - 1.0), 0.0, displacementMapHolder.getDisplacementMapWidth() - 1.0);
      double iv = GfxMathLib.clamp(displacementMapHolder.getDisplacementMapHeight() - 1.0 - randV * (displacementMapHolder.getDisplacementMapHeight() - 1.0), 0, displacementMapHolder.getDisplacementMapHeight() - 1.0);
      int ix = (int) trunc(iu);
      int iy = (int) trunc(iv);
      double d = displacementMapHolder.calculateImageDisplacement(ix, iy, iu, iv) * _displ_amount;
      pVarTP.x += pAmount * n.x * d;
      pVarTP.y += pAmount * n.y * d;
      pVarTP.z += pAmount * n.z * d;
    }

    if (direct_color > 0) {
      switch (color_mode) {
        case CM_V:
          pVarTP.color = (v - _vmin) / _dv;
          break;
        case CM_U:
          pVarTP.color = (u - _umin) / _du;
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
        default:
        case CM_UV:
          pVarTP.color = (v - _vmin) / _dv * (u - _umin) / _du;
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
    return new Object[]{preset_id, umin, umax, vmin, vmax, direct_color, color_mode, colorMapHolder.getBlend_colormap(), displacementMapHolder.getDispl_amount(), displacementMapHolder.getBlend_displ_map(), solid, param_a, param_b, param_c, param_d, param_e, param_f};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PRESET_ID.equalsIgnoreCase(pName)) {
      preset_id = Tools.FTOI(pValue);
      if (preset_id >= 0) {
        refreshFormulaFromPreset(preset_id);
      }
    } else if (PARAM_UMIN.equalsIgnoreCase(pName)) {
      umin = pValue;
      validatePresetId();
    } else if (PARAM_UMAX.equalsIgnoreCase(pName)) {
      umax = pValue;
      validatePresetId();
    } else if (PARAM_VMIN.equalsIgnoreCase(pName)) {
      vmin = pValue;
      validatePresetId();
    } else if (PARAM_VMAX.equalsIgnoreCase(pName)) {
      vmax = pValue;
      validatePresetId();
    } else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName)) {
      direct_color = Tools.FTOI(pValue);
    } else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) {
      color_mode = Tools.FTOI(pValue);
    } else if (PARAM_BLEND_COLORMAP.equalsIgnoreCase(pName)) {
      colorMapHolder.setBlend_colormap(limitIntVal(Tools.FTOI(pValue), 0, 1));
    } else if (PARAM_DISPL_AMOUNT.equalsIgnoreCase(pName)) {
      displacementMapHolder.setDispl_amount(pValue);
    } else if (PARAM_BLEND_DISPLMAP.equalsIgnoreCase(pName)) {
      displacementMapHolder.setBlend_displ_map(limitIntVal(Tools.FTOI(pValue), 0, 1));
    } else if (PARAM_SOLID.equalsIgnoreCase(pName)) {
      solid = Tools.FTOI(pValue);
    } else if (PARAM_PARAM_A.equalsIgnoreCase(pName)) {
      param_a = pValue;
    } else if (PARAM_PARAM_B.equalsIgnoreCase(pName)) {
      param_b = pValue;
    } else if (PARAM_PARAM_C.equalsIgnoreCase(pName)) {
      param_c = pValue;
    } else if (PARAM_PARAM_D.equalsIgnoreCase(pName)) {
      param_d = pValue;
    } else if (PARAM_PARAM_E.equalsIgnoreCase(pName)) {
      param_e = pValue;
    } else if (PARAM_PARAM_F.equalsIgnoreCase(pName)) {
      param_f = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "parplot2d_wf";
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(xformula != null ? xformula.getBytes() : null), (yformula != null ? yformula.getBytes() : null), (zformula != null ? zformula.getBytes() : null), (colorMapHolder.getColormap_filename() != null ? colorMapHolder.getColormap_filename().getBytes() : null), (displacementMapHolder.getDispl_map_filename() != null ? displacementMapHolder.getDispl_map_filename().getBytes() : null)};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_XFORMULA.equalsIgnoreCase(pName)) {
      xformula = pValue != null ? new String(pValue) : "";
      validatePresetId();
    } else if (RESSOURCE_YFORMULA.equalsIgnoreCase(pName)) {
      yformula = pValue != null ? new String(pValue) : "";
      validatePresetId();
    } else if (RESSOURCE_ZFORMULA.equalsIgnoreCase(pName)) {
      zformula = pValue != null ? new String(pValue) : "";
      validatePresetId();
    } else if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      colorMapHolder.setColormap_filename(pValue != null ? new String(pValue) : "");
      colorMapHolder.clear();
      uvIdxMap.clear();
    } else if (RESSOURCE_DISPL_MAP_FILENAME.equalsIgnoreCase(pName)) {
      displacementMapHolder.setDispl_map_filename(pValue != null ? new String(pValue) : "");
      displacementMapHolder.clear();
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_XFORMULA.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    } else if (RESSOURCE_YFORMULA.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    } else if (RESSOURCE_ZFORMULA.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    } else if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_DISPL_MAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else
      throw new IllegalArgumentException(pName);
  }

  private double _umin, _umax, _du;
  private double _vmin, _vmax, _dv;
  private double _displ_amount;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    colorMapHolder.init();
    uvColors = pLayer.getPalette().createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    displacementMapHolder.init();
    _displ_amount = displacementMapHolder.getDispl_amount();

    _umin = umin;
    _umax = umax;
    if (_umin > _umax) {
      double t = _umin;
      _umin = _umax;
      _umax = t;
    }
    _du = _umax - _umin;

    _vmin = vmin;
    _vmax = vmax;
    if (_vmin > _vmax) {
      double t = _vmin;
      _vmin = _vmax;
      _vmax = t;
    }
    _dv = _vmax - _vmin;

    String code = "import static org.jwildfire.base.mathlib.MathLib.*;\r\n" +
            "\r\n" +
            "  public double evaluateX(double u,double v) {\r\n" +
            "    double pi = M_PI;\r\n" +
            "    double param_a = " + param_a + ";\r\n" +
            "    double param_b = " + param_b + ";\r\n" +
            "    double param_c = " + param_c + ";\r\n" +
            "    double param_d = " + param_d + ";\r\n" +
            "    double param_e = " + param_e + ";\r\n" +
            "    double param_f = " + param_f + ";\r\n" +
            "    return " + (xformula != null && !xformula.isEmpty() ? xformula : "0.0") + ";\r\n" +
            "  }\r\n" +
            "  public double evaluateY(double u,double v) {\r\n" +
            "    double pi = M_PI;\r\n" +
            "    double param_a = " + param_a + ";\r\n" +
            "    double param_b = " + param_b + ";\r\n" +
            "    double param_c = " + param_c + ";\r\n" +
            "    double param_d = " + param_d + ";\r\n" +
            "    double param_e = " + param_e + ";\r\n" +
            "    double param_f = " + param_f + ";\r\n" +
            "    return " + (yformula != null && !yformula.isEmpty() ? yformula : "0.0") + ";\r\n" +
            "  }\r\n" +
            "  public double evaluateZ(double u,double v) {\r\n" +
            "    double pi = M_PI;\r\n" +
            "    double param_a = " + param_a + ";\r\n" +
            "    double param_b = " + param_b + ";\r\n" +
            "    double param_c = " + param_c + ";\r\n" +
            "    double param_d = " + param_d + ";\r\n" +
            "    double param_e = " + param_e + ";\r\n" +
            "    double param_f = " + param_f + ";\r\n" +
            "    return " + (zformula != null && !zformula.isEmpty() ? zformula : "0.0") + ";\r\n" +
            "  }\r\n";
    try {
      evaluator = ParPlot2DFormulaEvaluator.compile(code);
    } catch (Exception e) {
      evaluator = null;
      e.printStackTrace();
      System.out.println(code);
      throw new IllegalArgumentException(e);
    }
  }

  public ParPlot2DWFFunc() {
    super();
    preset_id = WFFuncPresetsStore.getParPlot2DWFFuncPresets().getRandomPresetId();
    refreshFormulaFromPreset(preset_id);
  }

  private void validatePresetId() {
    if (preset_id >= 0) {
      ParPlot2DWFFuncPreset preset = WFFuncPresetsStore.getParPlot2DWFFuncPresets().getPreset(preset_id);
      if (!preset.getXformula().equals(xformula) || !preset.getYformula().equals(yformula) || !preset.getZformula().equals(zformula) ||
              (fabs(umin - preset.getUmin()) > EPSILON) || (fabs(umax - preset.getUmax()) > EPSILON) ||
              (fabs(vmin - preset.getVmin()) > EPSILON) || (fabs(vmax - preset.getVmax()) > EPSILON)) {
        preset_id = -1;
      }
    }
  }

  private void refreshFormulaFromPreset(int presetId) {
    ParPlot2DWFFuncPreset preset = WFFuncPresetsStore.getParPlot2DWFFuncPresets().getPreset(presetId);
    xformula = preset.getXformula();
    yformula = preset.getYformula();
    zformula = preset.getZformula();
    umin = preset.getUmin();
    umax = preset.getUmax();
    vmin = preset.getVmin();
    vmax = preset.getVmax();
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
  public boolean dynamicParameterExpansion() {
    return true;
  }

  @Override
  public boolean dynamicParameterExpansion(String pName) {
    // preset_id doesn't really expand parameters, but it changes them; this will make them refresh
    if (PARAM_PRESET_ID.equalsIgnoreCase(pName)) return true;
    else return false;
  }

}
