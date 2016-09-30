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

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathparser.JEPWrapper;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.nfunk.jep.Node;

public class ParPlot2DWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PRESET_ID = "preset_id";
  private static final String PARAM_UMIN = "umin";
  private static final String PARAM_UMAX = "umax";
  private static final String PARAM_VMIN = "vmin";
  private static final String PARAM_VMAX = "vmax";
  private static final String PARAM_DIRECT_COLOR = "direct_color";
  private static final String PARAM_COLOR_MODE = "color_mode";

  private static final String RESSOURCE_XFORMULA = "xformula";
  private static final String RESSOURCE_YFORMULA = "yformula";
  private static final String RESSOURCE_ZFORMULA = "zformula";

  private static final String[] paramNames = { PARAM_PRESET_ID, PARAM_UMIN, PARAM_UMAX, PARAM_VMIN, PARAM_VMAX, PARAM_DIRECT_COLOR, PARAM_COLOR_MODE };

  private static final String[] ressourceNames = { RESSOURCE_XFORMULA, RESSOURCE_YFORMULA, RESSOURCE_ZFORMULA };

  private static final int CM_U = 0;
  private static final int CM_V = 1;
  private static final int CM_UV = 2;

  private int preset_id = -1;

  private double umin = 0.0;
  private double umax = 2.0 * Math.PI;
  private double vmin = 0.0;
  private double vmax = 2.0 * Math.PI;
  private int direct_color = 1;
  private int color_mode = CM_UV;

  private String xformula;
  private String yformula;
  private String zformula;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double u = _umin + pContext.random() * _du;
    double v = _vmin + pContext.random() * _dv;

    _xparser.setVarValue("u", u);
    _xparser.setVarValue("v", v);
    double x = (Double) _xparser.evaluate(_xnode);

    _yparser.setVarValue("u", u);
    _yparser.setVarValue("v", v);
    double y = (Double) _yparser.evaluate(_ynode);

    _zparser.setVarValue("u", u);
    _zparser.setVarValue("v", v);
    double z = (Double) _zparser.evaluate(_znode);

    if (direct_color > 0) {
      switch (color_mode) {
        case CM_V:
          pVarTP.color = (v - _vmin) / _dv;
          break;
        case CM_UV:
          pVarTP.color = (v - _vmin) / _dv * (u - _umin) / _du;
          break;
        case CM_U:
        default:
          pVarTP.color = (u - _umin) / _du;
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
    return new Object[] { preset_id, umin, umax, vmin, vmax, direct_color, color_mode };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PRESET_ID.equalsIgnoreCase(pName)) {
      preset_id = Tools.FTOI(pValue);
      if (preset_id >= 0) {
        refreshFormulaFromPreset(preset_id);
      }
    }
    else if (PARAM_UMIN.equalsIgnoreCase(pName)) {
      umin = pValue;
      validatePresetId();
    }
    else if (PARAM_UMAX.equalsIgnoreCase(pName)) {
      umax = pValue;
      validatePresetId();
    }
    else if (PARAM_VMIN.equalsIgnoreCase(pName)) {
      vmin = pValue;
      validatePresetId();
    }
    else if (PARAM_VMAX.equalsIgnoreCase(pName)) {
      vmax = pValue;
      validatePresetId();
    }
    else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName)) {
      direct_color = Tools.FTOI(pValue);
    }
    else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) {
      color_mode = Tools.FTOI(pValue);
    }
    else
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
    return new byte[][] { (xformula != null ? xformula.getBytes() : null), (yformula != null ? yformula.getBytes() : null), (zformula != null ? zformula.getBytes() : null) };
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_XFORMULA.equalsIgnoreCase(pName)) {
      xformula = pValue != null ? new String(pValue) : "";
      validatePresetId();
    }
    else if (RESSOURCE_YFORMULA.equalsIgnoreCase(pName)) {
      yformula = pValue != null ? new String(pValue) : "";
      validatePresetId();
    }
    else if (RESSOURCE_ZFORMULA.equalsIgnoreCase(pName)) {
      zformula = pValue != null ? new String(pValue) : "";
      validatePresetId();
    }
    else
      throw new IllegalArgumentException(pName);
  }

  private JEPWrapper _xparser, _yparser, _zparser;
  private Node _xnode, _ynode, _znode;
  private double _umin, _umax, _du;
  private double _vmin, _vmax, _dv;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _xparser = new JEPWrapper();
    _xparser.addVariable("u", 0.0);
    _xparser.addVariable("v", 0.0);
    _xnode = _xparser.parse(xformula);

    _yparser = new JEPWrapper();
    _yparser.addVariable("u", 0.0);
    _yparser.addVariable("v", 0.0);
    _ynode = _yparser.parse(yformula);

    _zparser = new JEPWrapper();
    _zparser.addVariable("u", 0.0);
    _zparser.addVariable("v", 0.0);
    _znode = _zparser.parse(zformula);

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
  }
}
