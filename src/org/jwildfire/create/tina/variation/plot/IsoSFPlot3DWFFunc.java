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

package org.jwildfire.create.tina.variation.plot;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.render.gpu.farender.FARenderTools;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.variation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jwildfire.base.mathlib.MathLib.*;

public class IsoSFPlot3DWFFunc extends VariationFunc implements SupportsGPU {
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
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_MAX_ITER = "max_iter";

  private static final String PARAM_PARAM_A = "param_a";
  private static final String PARAM_PARAM_B = "param_b";
  private static final String PARAM_PARAM_C = "param_c";
  private static final String PARAM_PARAM_D = "param_d";
  private static final String PARAM_PARAM_E = "param_e";
  private static final String PARAM_PARAM_F = "param_f";

  private static final String RESSOURCE_FORMULA = "formula";
  private static final String RESSOURCE_COLORMAP_FILENAME = "colormap_filename";
  private static final String RESSOURCE_ID_REFERENCE = "preset_id_reference";
  
  private static final String[] paramNames = {
    PARAM_PRESET_ID,
    PARAM_XMIN,
    PARAM_XMAX,
    PARAM_YMIN,
    PARAM_YMAX,
    PARAM_ZMIN,
    PARAM_ZMAX,
    PARAM_THICKNESS,
    PARAM_MAX_ITER,
    PARAM_DIRECT_COLOR,
    PARAM_COLOR_MODE,
    PARAM_BLEND_COLORMAP,
    PARAM_PARAM_A,
    PARAM_PARAM_B,
    PARAM_PARAM_C,
    PARAM_PARAM_D,
    PARAM_PARAM_E,
    PARAM_PARAM_F
  };

  private static final String[] ressourceNames = {RESSOURCE_FORMULA, RESSOURCE_COLORMAP_FILENAME, RESSOURCE_ID_REFERENCE};

  private static final int CM_COLORMAP_X = 0;
  private static final int CM_COLORMAP_Y = 1;
  private static final int CM_COLORMAP_Z = 2;
  private static final int CM_X = 3;
  private static final int CM_Y = 4;
  private static final int CM_Z = 5;
  private static final int CM_XY = 6;
  private static final int CM_YZ = 7;
  private static final int CM_ZX = 8;
  private static final int CM_XYZ = 9;

  private int preset_id = -1;

  private double xmin = -Math.PI;
  private double xmax = Math.PI;
  private double ymin = -Math.PI;
  private double ymax = Math.PI;
  private double zmin = -Math.PI;
  private double zmax = Math.PI;

  private int direct_color = 1;
  private int color_mode = CM_XYZ;

  private double param_a = 0.0;
  private double param_b = 0.0;
  private double param_c = 0.0;
  private double param_d = 0.0;
  private double param_e = 0.0;
  private double param_f = 0.0;

  private double thickness = 0.05;
  private int max_iter = 160;

  private String formula;

  private ColorMapHolder colorMapHolder = new ColorMapHolder();

  private IsoSFPlot3DFormulaEvaluator evaluator;

  private String id_reference = "org.jwildfire.create.tina.variation.reference.ReferenceFile isosfplot3d-presets.pdf";
  
  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] {
      preset_id,
      xmin,
      xmax,
      ymin,
      ymax,
      zmin,
      zmax,
      thickness,
      max_iter,
      direct_color,
      color_mode,
      colorMapHolder.getBlend_colormap(),
      param_a,
      param_b,
      param_c,
      param_d,
      param_e,
      param_f
    };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PRESET_ID.equalsIgnoreCase(pName)) {
      preset_id = Tools.FTOI(pValue);
      if (preset_id >= 0) {
        refreshFormulaFromPreset(preset_id);
      }
    } else if (PARAM_XMIN.equalsIgnoreCase(pName)) {
      xmin = pValue;
      validatePresetId();
    } else if (PARAM_XMAX.equalsIgnoreCase(pName)) {
      xmax = pValue;
      validatePresetId();
    } else if (PARAM_YMIN.equalsIgnoreCase(pName)) {
      ymin = pValue;
      validatePresetId();
    } else if (PARAM_YMAX.equalsIgnoreCase(pName)) {
      ymax = pValue;
      validatePresetId();
    } else if (PARAM_ZMIN.equalsIgnoreCase(pName)) {
      zmin = pValue;
      validatePresetId();
    } else if (PARAM_ZMAX.equalsIgnoreCase(pName)) {
      zmax = pValue;
      validatePresetId();
    } else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName)) {
      direct_color = Tools.FTOI(pValue);
    } else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) {
      color_mode = Tools.FTOI(pValue);
    } else if (PARAM_BLEND_COLORMAP.equalsIgnoreCase(pName)) {
      colorMapHolder.setBlend_colormap(limitIntVal(Tools.FTOI(pValue), 0, 1));
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
    } else if (PARAM_MAX_ITER.equalsIgnoreCase(pName)) {
      max_iter = Tools.FTOI(pValue);
      if (max_iter < 1) {
        max_iter = 1;
      }
    } else if (PARAM_THICKNESS.equalsIgnoreCase(pName)) {
      thickness = pValue;
      if (thickness < EPSILON) {
        thickness = EPSILON;
      }
    } else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "isosfplot3d_wf";
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][] {
      (formula != null ? formula.getBytes() : null),
      (colorMapHolder.getColormap_filename() != null
          ? colorMapHolder.getColormap_filename().getBytes()
          : null),
      id_reference.getBytes()
    };
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_FORMULA.equalsIgnoreCase(pName)) {
      formula = pValue != null ? new String(pValue) : "";
      validatePresetId();
    } else if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      colorMapHolder.setColormap_filename(pValue != null ? new String(pValue) : "");
      colorMapHolder.clear();
      uvIdxMap.clear();
    } else if (RESSOURCE_ID_REFERENCE.equalsIgnoreCase(pName)) {
    	// ignore read-only parameter
    } else throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_FORMULA.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    } else if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_ID_REFERENCE.equalsIgnoreCase(pName)) {
      return RessourceType.REFERENCE;
    } else throw new IllegalArgumentException(pName);
  }

  private double _xmin, _xmax, _dx;
  private double _ymin, _ymax, _dy;
  private double _zmin, _zmax, _dz;

  @Override
  public void init(
      FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    colorMapHolder.init();
    uvColors =
        pLayer
            .getPalette()
            .createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    hits = new AtomicInteger(0);
    misses = new AtomicInteger(0);
    super.init(pContext, pLayer, pXForm, pAmount);

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

    String code =
        "import static org.jwildfire.base.mathlib.MathLib.*;\r\n"
            + "\r\n"
            + "  public double evaluate(double x,double y,double z) {\r\n"
            + "    double pi = M_PI;\r\n"
            + "    double param_a = "
            + param_a
            + ";\r\n"
            + "    double param_b = "
            + param_b
            + ";\r\n"
            + "    double param_c = "
            + param_c
            + ";\r\n"
            + "    double param_d = "
            + param_d
            + ";\r\n"
            + "    double param_e = "
            + param_e
            + ";\r\n"
            + "    double param_f = "
            + param_f
            + ";\r\n"
            + "    return "
            + (formula != null && !formula.isEmpty() ? formula : "0.0")
            + ";\r\n"
            + "  }\r\n";
    try {
      evaluator = IsoSFPlot3DFormulaEvaluator.compile(code);
    } catch (Exception e) {
      evaluator = null;
      e.printStackTrace();
      System.out.println(code);
      throw new IllegalArgumentException(e);
    }
  }

  public IsoSFPlot3DWFFunc() {
    super();
    preset_id = WFFuncPresetsStore.getIsoSFPlot3DWFFuncPresets().getRandomPresetId();
    refreshFormulaFromPreset(preset_id);
  }

  private void validatePresetId() {
    if (preset_id >= 0) {
      IsoSFPlot3DWFFuncPreset preset =
          WFFuncPresetsStore.getIsoSFPlot3DWFFuncPresets().getPreset(preset_id);
      if (!preset.getFormula().equals(formula)
          || (fabs(xmin - preset.getXmin()) > EPSILON)
          || (fabs(xmax - preset.getXmax()) > EPSILON)
          || (fabs(ymin - preset.getYmin()) > EPSILON)
          || (fabs(ymax - preset.getYmax()) > EPSILON)
          || (fabs(zmin - preset.getZmin()) > EPSILON)
          || (fabs(zmax - preset.getZmax()) > EPSILON)) {
        preset_id = -1;
      }
    }
  }

  private void refreshFormulaFromPreset(int presetId) {
    IsoSFPlot3DWFFuncPreset preset =
        WFFuncPresetsStore.getIsoSFPlot3DWFFuncPresets().getPreset(presetId);
    formula = preset.getFormula();
    xmin = preset.getXmin();
    xmax = preset.getXmax();
    ymin = preset.getYmin();
    ymax = preset.getYmax();
    zmin = preset.getZmin();
    zmax = preset.getZmax();
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

  private static AtomicInteger hits, misses;

  @Override
  public void transform(
      FlameTransformationContext pContext,
      XForm pXForm,
      XYZPoint pAffineTP,
      XYZPoint pVarTP,
      double pAmount) {
    if (evaluator == null) {
      return;
    }

    pVarTP.doHide = true;
    double x = 0.0, y = 0.0, z = 0.0;
    for (int i = 0; i < max_iter; i++) {
      x = _xmin + pContext.random() * _dx;
      y = _ymin + pContext.random() * _dy;
      z = _zmin + pContext.random() * _dz;
      double e = evaluator.evaluate(x, y, z);
      if (fabs(e) <= thickness) {
        pVarTP.x += pAmount * x;
        pVarTP.y += pAmount * y;
        pVarTP.z += pAmount * z;
        pVarTP.doHide = false;
        break;
      }
    }

    //    if (pVarTP.doHide) {
    //      misses.addAndGet(1);
    //    }
    //    else {
    //      hits.addAndGet(1);
    //    }
    //    int m = misses.get();
    //    int h = hits.get();
    //    int sum = m + h;
    //    if (sum % 100000000 == 0) {
    //      System.out.println("h: " + h + ", m: " + m + ", misses: " + Tools.FTOI(100.0 * (double)
    // m / (double) (m + h)) + "%");
    //    }

    if (!pVarTP.doHide && direct_color > 0) {
      switch (color_mode) {
        case CM_X:
          pVarTP.color = (x - _xmin) / _dx;
          break;
        case CM_Y:
          pVarTP.color = (y - _ymin) / _dy;
          break;
        case CM_Z:
          pVarTP.color = (z - _zmin) / _dz;
          break;
        case CM_COLORMAP_X:
        case CM_COLORMAP_Y:
        case CM_COLORMAP_Z:
          if (colorMapHolder.isActive()) {
            double iu, iv;
            switch (color_mode) {
              case CM_COLORMAP_X:
                iu =
                    GfxMathLib.clamp(
                        ((y - _ymin) / _dy) * (colorMapHolder.getColorMapWidth() - 1.0),
                        0.0,
                        colorMapHolder.getColorMapWidth() - 1.0);
                iv =
                    GfxMathLib.clamp(
                        colorMapHolder.getColorMapHeight()
                            - 1.0
                            - ((z - _zmin) / _dz) * (colorMapHolder.getColorMapHeight() - 1.0),
                        0,
                        colorMapHolder.getColorMapHeight() - 1.0);
                break;
              case CM_COLORMAP_Y:
                iu =
                    GfxMathLib.clamp(
                        ((z - _zmin) / _dz) * (colorMapHolder.getColorMapWidth() - 1.0),
                        0.0,
                        colorMapHolder.getColorMapWidth() - 1.0);
                iv =
                    GfxMathLib.clamp(
                        colorMapHolder.getColorMapHeight()
                            - 1.0
                            - ((x - _xmin) / _dx) * (colorMapHolder.getColorMapHeight() - 1.0),
                        0,
                        colorMapHolder.getColorMapHeight() - 1.0);
                break;
              case CM_COLORMAP_Z:
              default:
                iu =
                    GfxMathLib.clamp(
                        ((x - _xmin) / _dx) * (colorMapHolder.getColorMapWidth() - 1.0),
                        0.0,
                        colorMapHolder.getColorMapWidth() - 1.0);
                iv =
                    GfxMathLib.clamp(
                        colorMapHolder.getColorMapHeight()
                            - 1.0
                            - ((y - _ymin) / _dy) * (colorMapHolder.getColorMapHeight() - 1.0),
                        0,
                        colorMapHolder.getColorMapHeight() - 1.0);
                break;
            }
            int ix = (int) MathLib.trunc(iu);
            int iy = (int) MathLib.trunc(iv);
            colorMapHolder.applyImageColor(pVarTP, ix, iy, iu, iv);
            pVarTP.color =
                getUVColorIdx(
                    Tools.FTOI(pVarTP.redColor),
                    Tools.FTOI(pVarTP.greenColor),
                    Tools.FTOI(pVarTP.blueColor));
          }
          break;
        case CM_XY:
          pVarTP.color = (x - _xmin) / _dx * (y - _ymin) / _dy;
          break;
        case CM_YZ:
          pVarTP.color = (y - _ymin) / _dy * (z - _zmin) / _dz;
          break;
        case CM_ZX:
          pVarTP.color = (z - _zmin) / _dz * (x - _xmin) / _dx;
          break;
        default:
        case CM_XYZ:
          pVarTP.color = (x - _xmin) / _dx * (y - _ymin) / _dy * (z - _zmin) / _dz;
          break;
      }
      if (pVarTP.color < 0.0) pVarTP.color = 0.0;
      else if (pVarTP.color > 1.0) pVarTP.color = 1.0;
    }
  }
  
  @Override
  public void randomize() {
    if (Math.random() < 0.5) {
      preset_id = WFFuncPresetsStore.getIsoSFPlot3DWFFuncPresets().getRandomPresetId();
      refreshFormulaFromPreset(preset_id);
    } else {
      preset_id = -1;
      xmin = Math.random() * 3.0 - 4.0;
      xmax = Math.random() * 3.0 + 1.0;
      ymin = Math.random() * 3.0 - 4.0;
      ymax = Math.random() * 3.0 + 1.0;
      zmin = Math.random() * 3.0 - 4.0;
      zmax = Math.random() * 3.0 + 1.0;
    }
    param_a = Math.random() * 5.0 - 2.5;
    param_b = Math.random() * 5.0 - 2.5;
    param_c = Math.random() * 5.0 - 2.5;
    param_d = Math.random() * 5.0 - 2.5;
    param_e = Math.random() * 5.0 - 2.5;
    param_f = Math.random() * 5.0 - 2.5;
    thickness = Math.random() * 0.99 + 0.01;
    max_iter = (int) (Math.random() * 200 + 1);
    direct_color = (int) (Math.random() * 2);
    if (colorMapHolder.getColormap_filename() == null) 
      color_mode = (int) (Math.random() * 7 + 3);
    else {
      color_mode = (int) (Math.random() * 10);
      colorMapHolder.setBlend_colormap((int) (Math.random() * 2));
    }
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

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[] {
      VariationFuncType.VARTYPE_BASE_SHAPE,
      VariationFuncType.VARTYPE_DC,
      VariationFuncType.VARTYPE_EDIT_FORMULA,
      VariationFuncType.VARTYPE_SUPPORTS_GPU
    };
  }

  @Override
  public boolean isStateful() {
    return true;
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float _xmin, _xmax, _dx;\n"
        + "float _ymin, _ymax, _dy;\n"
        + "float _zmin, _zmax, _dz;\n"
        + "_xmin = __isosfplot3d_wf_xmin;\n"
        + "    _xmax = __isosfplot3d_wf_xmax;\n"
        + "    if (_xmin > _xmax) {\n"
        + "      float t = _xmin;\n"
        + "      _xmin = _xmax;\n"
        + "      _xmax = t;\n"
        + "    }\n"
        + "    _dx = _xmax - _xmin;\n"
        + "\n"
        + "    _ymin = __isosfplot3d_wf_ymin;\n"
        + "    _ymax = __isosfplot3d_wf_ymax;\n"
        + "    if (_ymin > _ymax) {\n"
        + "      float t = _ymin;\n"
        + "      _ymin = _ymax;\n"
        + "      _ymax = t;\n"
        + "    }\n"
        + "    _dy = _ymax - _ymin;\n"
        + "\n"
        + "    _zmin = __isosfplot3d_wf_zmin;\n"
        + "    _zmax = __isosfplot3d_wf_zmax;\n"
        + "    if (_zmin > _zmax) {\n"
        + "      float t = _zmin;\n"
        + "      _zmin = _zmax;\n"
        + "      _zmax = t;\n"
        + "    }\n"
        + "    _dz = _zmax - _zmin;\n"
        + "\n"
        + "    __doHide = true;\n"
        + "    float x = 0.0, y = 0.0, z = 0.0;\n"
//        + "    int max_iter = lroundf(__isosfplot3d_wf_max_iter);\n"
// high values lead to strange effects on GPU
        + "    int max_iter = lroundf(__isosfplot3d_wf_max_iter / 10.0);\n"
        + "    if(max_iter<3) max_iter=3;\n"
        + "    else if(max_iter>1000) max_iter=1000;\n"
        + "    for (int i = 0; i < max_iter; i++) {\n"
        + "      x = _xmin + RANDFLOAT() * _dx;\n"
        + "      y = _ymin + RANDFLOAT() * _dy;\n"
        + "      z = _zmin + RANDFLOAT() * _dz;\n"
        + "      float e = eval%d_isosfplot3d_wf(x, y, z, __isosfplot3d_wf_param_a, __isosfplot3d_wf_param_b, __isosfplot3d_wf_param_c, __isosfplot3d_wf_param_d, __isosfplot3d_wf_param_e, __isosfplot3d_wf_param_f);\n"
        + "      if (fabsf(e) <= __isosfplot3d_wf_thickness) {\n"
        + "        __px += __isosfplot3d_wf * x;\n"
        + "        __py += __isosfplot3d_wf * y;\n"
        + "        __pz += __isosfplot3d_wf * z;\n"
        + "        __doHide = false;\n"
        + "        break;\n"
        + "      }\n"
        + "    }\n"
        + "if(!__doHide && lroundf(__isosfplot3d_wf_direct_color)>0) {\n"
        + "  switch (lroundf(__isosfplot3d_wf_color_mode)) {\n"
            + "        case 3:\n"
            + "          __pal = (x - _xmin) / _dx;\n"
            + "          break;\n"
            + "        case 4:\n"
            + "          __pal = (y - _ymin) / _dy;\n"
            + "          break;\n"
            + "        case 5:\n"
            + "          __pal = (z - _zmin) / _dz;\n"
            + "          break;\n"
            + "        case 0:\n"
            + "          break;\n"
            + "        case 1:\n"
            + "          break;\n"
            + "        case 2:\n"
            + "          break;\n"
            + "        case 6:\n"
            + "          __pal = (x - _xmin) / _dx * (y - _ymin) / _dy;\n"
            + "          break;\n"
            + "        case 7:\n"
            + "          __pal = (y - _ymin) / _dy * (z - _zmin) / _dz;\n"
            + "          break;\n"
            + "        case 8:\n"
            + "          __pal = (z - _zmin) / _dz * (x - _xmin) / _dx;\n"
            + "          break;\n"
            + "        default:\n"
            + "        case 9:\n"
            + "          __pal = (x - _xmin) / _dx * (y - _ymin) / _dy * (z - _zmin) / _dz;\n"
            + "          break;\n"
        + "      }\n"
        + "      if (__pal < 0.0) __pal = 0.0;\n"
        + "      else if (__pal > 1.0) __pal = 1.0;\n"
        + "}\n";
  }

  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return "__device__ float eval%d_isosfplot3d_wf(float x, float y, float z, float param_a,float param_b, float param_c, float param_d, float param_e, float param_f) {\n"
            +"  float pi = PI;\n"
            +"  return "+ FARenderTools.rewriteJavaFormulaForCUDA(formula) +";\n"
            +"}\n";
  }

}
