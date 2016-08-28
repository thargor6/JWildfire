/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

// Variation Plugin DLL for Apophysis
//  Jed Kelsey, 20 June 2007
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathparser.JEPWrapper;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.nfunk.jep.Node;

public class YPlot3DWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_XMIN = "xmin";
  private static final String PARAM_XMAX = "xmax";
  private static final String PARAM_YMIN = "ymin";
  private static final String PARAM_YMAX = "ymax";
  private static final String PARAM_ZMIN = "zmin";
  private static final String PARAM_ZMAX = "zmax";
  private static final String PARAM_DIRECT_COLOR = "direct_color";

  private static final String RESSOURCE_FORMULA = "formula";

  private static final String[] paramNames = { PARAM_XMIN, PARAM_XMAX, PARAM_YMIN, PARAM_YMAX, PARAM_ZMIN, PARAM_ZMAX, PARAM_DIRECT_COLOR };

  private static final String[] ressourceNames = { RESSOURCE_FORMULA };

  private double xmin = -3.0;
  private double xmax = 2.0;
  private double ymin = -4.0;
  private double ymax = 4.0;
  private double zmin = -2.0;
  private double zmax = 2.0;
  private int direct_color = 1;

  private String formula = getRandomFormula();

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = _xmin + pContext.random() * _dx;
    double z = _zmin + pContext.random() * _dz;
    _parser.setVarValue("x", x);
    _parser.setVarValue("z", z);
    double y = (Double) _parser.evaluate(_node);
    if (direct_color > 0) {
      pVarTP.color = (y - _ymin) / _dy;
      if (pVarTP.color < 0.0)
        pVarTP.color = 0.0;
      else if (pVarTP.color > 1.0)
        pVarTP.color = 1.0;
    }
    pVarTP.x += x;
    pVarTP.y += y;
    pVarTP.z += z;
  }

  private String getRandomFormula() {
    switch (Tools.FTOI(Math.random() * 7)) {
      case 0:
        return "sin(2*exp(-4*(x*x+z*z)))";
      case 1:
        return "cos(x*z*12)/6";
      case 2:
        return "cos(sqrt(x*x+z*z)*14)*exp(-2*(x*x+z*z))";
      case 3:
        return "cos(atan(x/z)*8)/4*sin(sqrt(x*x+z*z)*3)";
      case 4:
        return "(sin(x)*sin(x)+cos(z)*cos(z))/(5+x*x+z*z)";
      case 5:
        return "cos(2*pi*(x+z))*(1-sqrt(x*x+z*z))";
      default:
        return "sin(x*x+z*z)";
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { xmin, xmax, ymin, ymax, zmin, zmax, direct_color };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_XMIN.equalsIgnoreCase(pName))
      xmin = pValue;
    else if (PARAM_XMAX.equalsIgnoreCase(pName))
      xmax = pValue;
    else if (PARAM_YMIN.equalsIgnoreCase(pName))
      ymin = pValue;
    else if (PARAM_YMAX.equalsIgnoreCase(pName))
      ymax = pValue;
    else if (PARAM_ZMIN.equalsIgnoreCase(pName))
      zmin = pValue;
    else if (PARAM_ZMAX.equalsIgnoreCase(pName))
      zmax = pValue;
    else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName))
      direct_color = Tools.FTOI(pValue);
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
    return new byte[][] { (formula != null ? formula.getBytes() : null) };
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_FORMULA.equalsIgnoreCase(pName)) {
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
    _parser.addVariable("z", 0.0);
    _node = _parser.parse(formula);
    if (xmin < xmax) {
      _xmin = xmin;
      _xmax = xmax;
    }
    else {
      _xmin = xmax;
      _xmax = xmin;
    }
    _dx = _xmax - _xmin;
    if (ymin < ymax) {
      _ymin = ymin;
      _ymax = ymax;
    }
    else {
      _ymin = ymax;
      _ymax = ymin;
    }
    _dy = _ymax - _ymin;
    _dz = _zmax - _zmin;
    if (zmin < zmax) {
      _zmin = zmin;
      _zmax = zmax;
    }
    else {
      _zmin = zmax;
      _zmax = zmin;
    }
    _dz = _zmax - _zmin;
  }

}
