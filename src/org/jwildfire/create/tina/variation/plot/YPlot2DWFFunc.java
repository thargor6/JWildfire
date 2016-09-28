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
import org.jwildfire.base.mathparser.JEPWrapper;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.nfunk.jep.Node;

public class YPlot2DWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_USE_PRESET = "use_preset";
  private static final String PARAM_PRESET_ID = "preset_id";
  private static final String PARAM_XMIN = "xmin";
  private static final String PARAM_XMAX = "xmax";
  private static final String PARAM_YMIN = "ymin";
  private static final String PARAM_YMAX = "ymax";
  private static final String PARAM_ZMIN = "zmin";
  private static final String PARAM_ZMAX = "zmax";
  private static final String PARAM_DIRECT_COLOR = "direct_color";

  private static final String RESSOURCE_FORMULA = "formula";

  private static final String[] paramNames = { PARAM_USE_PRESET, PARAM_PRESET_ID, PARAM_XMIN, PARAM_XMAX, PARAM_YMIN, PARAM_YMAX, PARAM_ZMIN, PARAM_ZMAX, PARAM_DIRECT_COLOR };

  private static final String[] ressourceNames = { RESSOURCE_FORMULA };

  private int use_preset = 1;
  private int preset_id;

  private double xmin = -3.0;
  private double xmax = 2.0;
  private double ymin = -4.0;
  private double ymax = 4.0;
  private double zmin = -2.0;
  private double zmax = 2.0;
  private int direct_color = 1;

  private String formula;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = _xmin + pContext.random() * _dx;
    double z = _zmin + pContext.random() * _dz;
    _parser.setVarValue("x", x);
    double y = (Double) _parser.evaluate(_node);
    if (direct_color > 0) {
      pVarTP.color = (y - _ymin) / _dy;
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
    return new Object[] { use_preset, preset_id, xmin, xmax, ymin, ymax, zmin, zmax, direct_color };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_USE_PRESET.equalsIgnoreCase(pName))
      use_preset = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_PRESET_ID.equalsIgnoreCase(pName)) {
      int new_preset_id = Tools.FTOI(pValue);
      if (use_preset > 0 && new_preset_id != preset_id) {
        refreshFormulaFromPreset(new_preset_id);
      }
      preset_id = new_preset_id;
    }
    else if (PARAM_XMIN.equalsIgnoreCase(pName)) {
      if (use_preset <= 0)
        xmin = pValue;
    }
    else if (PARAM_XMAX.equalsIgnoreCase(pName)) {
      if (use_preset <= 0)
        xmax = pValue;
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
    return new byte[][] { (formula != null ? formula.getBytes() : null) };
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_FORMULA.equalsIgnoreCase(pName)) {
      if (use_preset <= 0)
        formula = pValue != null ? new String(pValue) : "";
    }
    else
      throw new IllegalArgumentException(pName);
  }

  private JEPWrapper _parser;
  private Node _node;
  private double _xmin, _xmax, _dx;
  private double _ymin, _ymax, _dy;
  private double _zmin, _zmax, _dz;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
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

  private void refreshFormulaFromPreset(int presetId) {
    YPlot2DWFFuncPreset preset = WFFuncPresetsStore.getYPlot2DWFFuncPresets().getPreset(presetId);
    formula = preset.getFormula();
    xmin = preset.getXmin();
    xmax = preset.getXmax();
  }
}
