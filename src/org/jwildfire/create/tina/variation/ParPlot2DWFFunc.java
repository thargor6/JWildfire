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

public class ParPlot2DWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_UMIN = "umin";
  private static final String PARAM_UMAX = "umax";
  private static final String PARAM_VMIN = "vmin";
  private static final String PARAM_VMAX = "vmax";
  private static final String PARAM_DIRECT_COLOR = "direct_color";
  private static final String PARAM_COLOR_MODE = "color_mode";

  private static final String RESSOURCE_XFORMULA = "xformula";
  private static final String RESSOURCE_YFORMULA = "yformula";
  private static final String RESSOURCE_ZFORMULA = "zformula";

  private static final String[] paramNames = { PARAM_UMIN, PARAM_UMAX, PARAM_VMIN, PARAM_VMAX, PARAM_DIRECT_COLOR, PARAM_COLOR_MODE };

  private static final String[] ressourceNames = { RESSOURCE_XFORMULA, RESSOURCE_YFORMULA, RESSOURCE_ZFORMULA };

  private static final int CM_U = 0;
  private static final int CM_V = 1;
  private static final int CM_UV = 2;

  private double umin = -3.0;
  private double umax = 2.0;
  private double vmin = -4.0;
  private double vmax = 4.0;
  private int direct_color = 1;
  private int color_mode = CM_UV;

  private String xformula;
  private String yformula;
  private String zformula;

  public ParPlot2DWFFunc() {
    super();
    setRandomParams();
  }

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

  private void setRandomParams() {
    switch (11 /*(Tools.FTOI(Math.random() * 12))*/) {
      case 0:
        umin = 0.0;
        umax = 2.0 * Math.PI;
        vmin = 0.0;
        vmax = 2.0 * Math.PI;
        xformula = "cos(u)*(4+cos(v))";
        yformula = "sin(u)*(4+cos(v))";
        zformula = "4*sin(2*u)+sin(v)*(1.2-sin(v))";
        break;
      case 1:
        umin = 0.0;
        umax = Math.PI;
        vmin = 0.0;
        vmax = Math.PI;
        xformula = "cos(v)*sin(2*u)";
        yformula = "sin(v)*sin(2*u)";
        zformula = "sin(2*v)*(cos(u))^2";
        break;
      case 2:
        umin = 0.0;
        umax = 5 * Math.PI;
        vmin = 0.0;
        vmax = 2 * Math.PI;
        xformula = "cos(u)*(exp(u/10)-1)*(cos(v)+0.8)";
        yformula = "sin(u)*(exp(u/10)-1)*(cos(v)+0.8)";
        zformula = "(exp(u/10)-1)*sin(v)";
        break;
      case 3:
        umin = 0.0;
        umax = 2 * Math.PI;
        vmin = 0.0;
        vmax = 2 * Math.PI;
        xformula = "cos(v)*(2+sin(u+v/3))";
        yformula = "sin(v)*(2+sin(u+v/3))";
        zformula = "cos(u+v/3)";
        break;
      case 4:
        umin = 0.0;
        umax = 4.0 * Math.PI;
        vmin = 0.0;
        vmax = 2.0 * Math.PI;
        xformula = "cos(u)*(2+cos(v))";
        yformula = "sin(u)*(2+cos(v))";
        zformula = "(u-2*pi)+sin(v)";
        break;
      case 5:
        umin = 0.0;
        umax = Math.PI;
        vmin = 0.0;
        vmax = 2.0 * Math.PI;
        xformula = "u*cos(v)";
        yformula = "u*sin(v)";
        zformula = "(cos(4*u))^2*exp(0-u)";
        break;
      case 6:
        umin = 0.0 - Math.PI;
        umax = Math.PI;
        vmin = 0.0 - Math.PI;
        vmax = 2.0 * Math.PI;
        xformula = "cos(u)*(2+(cos(u/2))^2*sin(v))";
        yformula = "sin(u)*(2+(cos(u/2))^2*sin(v))";
        zformula = "(cos(u/2))^2*cos(v)";
        break;
      case 7:
        umin = 0.0;
        umax = 2.0 * Math.PI;
        vmin = 0.0;
        vmax = 2.0 * Math.PI;
        xformula = "cos(u)*(4+cos(v))";
        yformula = "sin(u)*(4+cos(v))";
        zformula = "3*sin(u)+(sin(3*v)*(1.2+sin(3*v)))";
        break;
      case 8:
        umin = 0.0 - Math.PI;
        umax = Math.PI;
        vmin = 0.0 - Math.PI;
        vmax = Math.PI;
        xformula = "u*cos(v)";
        yformula = "v*cos(u)";
        zformula = "u*v*sin(u)*sin(v)";
        break;
      case 9:
        umin = 0.0;
        umax = 2.0 * Math.PI;
        vmin = 0.0;
        vmax = Math.PI;
        xformula = "cos(u)*sin(v^3/(3.1415926^2))";
        yformula = "sin(u)*sin(v)";
        zformula = "cos(v)";
        break;
      case 10:
        umin = 0.0;
        umax = 2.0 * Math.PI;
        vmin = 0.0;
        vmax = 2.0 * Math.PI;
        xformula = "cos(u)*((cos(3*u)+2)*sin(v)+0.5)";
        yformula = "sin(u)*((cos(3*u)+2)*sin(v)+0.5)";
        zformula = "(cos(3*u)+2)*cos(v)";
        break;
      case 11:
      default:
        // source: https://reference.wolfram.com/language/tutorial/ParametricPlots.html
        umin = -Math.PI;
        umax = Math.PI;
        vmin = -Math.PI;
        vmax = Math.PI;
        xformula = "sin(u)*sin(v)+0.05*cos(20*v)";
        yformula = "cos(u)*sin(v)+0.05*cos(20*u)";
        zformula = "cos(v)";
        break;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { umin, umax, vmin, vmax, direct_color, color_mode };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_UMIN.equalsIgnoreCase(pName))
      umin = pValue;
    else if (PARAM_UMAX.equalsIgnoreCase(pName))
      umax = pValue;
    else if (PARAM_VMIN.equalsIgnoreCase(pName))
      vmin = pValue;
    else if (PARAM_VMAX.equalsIgnoreCase(pName))
      vmax = pValue;
    else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName))
      direct_color = Tools.FTOI(pValue);
    else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName))
      color_mode = Tools.FTOI(pValue);
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
    }
    else if (RESSOURCE_YFORMULA.equalsIgnoreCase(pName)) {
      yformula = pValue != null ? new String(pValue) : "";
    }
    else if (RESSOURCE_ZFORMULA.equalsIgnoreCase(pName)) {
      zformula = pValue != null ? new String(pValue) : "";
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

    if (umin < umax) {
      _umin = umin;
      _umax = umax;
    }
    else {
      _umin = umax;
      _umax = umin;
    }
    _du = _umax - _umin;

    if (vmin < vmax) {
      _vmin = vmin;
      _vmax = vmax;
    }
    else {
      _vmin = vmax;
      _vmax = vmin;
    }
    _dv = _vmax - _vmin;
  }

}
