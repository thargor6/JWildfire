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
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.fmod;

public class PostMirrorWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_XAXIS = "xaxis";
  private static final String PARAM_YAXIS = "yaxis";
  private static final String PARAM_ZAXIS = "zaxis";
  private static final String PARAM_XSHIFT = "xshift";
  private static final String PARAM_YSHIFT = "yshift";
  private static final String PARAM_ZSHIFT = "zshift";
  private static final String PARAM_XSCALE = "xscale";
  private static final String PARAM_YSCALE = "yscale";
  private static final String PARAM_XCOLORSHIFT = "xcolorshift";
  private static final String PARAM_YCOLORSHIFT = "ycolorshift";
  private static final String PARAM_ZCOLORSHIFT = "zcolorshift";

  private static final String[] paramNames = {PARAM_XAXIS, PARAM_YAXIS, PARAM_ZAXIS, PARAM_XSHIFT, PARAM_YSHIFT, PARAM_ZSHIFT, PARAM_XSCALE, PARAM_YSCALE, PARAM_XCOLORSHIFT, PARAM_YCOLORSHIFT, PARAM_ZCOLORSHIFT};

  private int xaxis = 1;
  private int yaxis = 0;
  private int zaxis = 0;
  private double xshift = 0.0;
  private double yshift = 0.0;
  private double zshift = 0.0;
  private double xscale = 1.0;
  private double yscale = 1.0;
  private double xcolorshift = 0.0;
  private double ycolorshift = 0.0;
  private double zcolorshift = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (xaxis > 0 && pContext.random() < 0.5) {
      pVarTP.x = xscale * (-pVarTP.x - xshift);
      pVarTP.y = yscale * pVarTP.y;
      pVarTP.color = fmod(pVarTP.color + xcolorshift, 1.0);
    }

    if (yaxis > 0 && pContext.random() < 0.5) {
      pVarTP.x = xscale * pVarTP.x;
      pVarTP.y = yscale * (-pVarTP.y - yshift);
      pVarTP.color = fmod(pVarTP.color + ycolorshift, 1.0);
    }

    if (zaxis > 0 && pContext.random() < 0.5) {
      pVarTP.z = -pVarTP.z - zshift;
      pVarTP.color = fmod(pVarTP.color + zcolorshift, 1.0);
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{xaxis, yaxis, zaxis, xshift, yshift, zshift, xscale, yscale, xcolorshift, ycolorshift, zcolorshift};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_XAXIS.equalsIgnoreCase(pName))
      xaxis = Tools.FTOI(pValue);
    else if (PARAM_YAXIS.equalsIgnoreCase(pName))
      yaxis = Tools.FTOI(pValue);
    else if (PARAM_ZAXIS.equalsIgnoreCase(pName))
      zaxis = Tools.FTOI(pValue);
    else if (PARAM_XSHIFT.equalsIgnoreCase(pName))
      xshift = pValue;
    else if (PARAM_YSHIFT.equalsIgnoreCase(pName))
      yshift = pValue;
    else if (PARAM_ZSHIFT.equalsIgnoreCase(pName))
      zshift = pValue;
    else if (PARAM_XSCALE.equalsIgnoreCase(pName))
      xscale = pValue;
    else if (PARAM_YSCALE.equalsIgnoreCase(pName))
      yscale = pValue;
    else if (PARAM_XCOLORSHIFT.equalsIgnoreCase(pName))
      xcolorshift = pValue;
    else if (PARAM_YCOLORSHIFT.equalsIgnoreCase(pName))
      ycolorshift = pValue;
    else if (PARAM_ZCOLORSHIFT.equalsIgnoreCase(pName))
      zcolorshift = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "post_mirror_wf";
  }

  @Override
  public int getPriority() {
    return 1;
  }

}
