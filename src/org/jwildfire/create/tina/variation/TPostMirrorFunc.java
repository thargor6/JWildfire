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

public class TPostMirrorFunc extends VariationFunc {

  private static final String PARAM_XAXIS = "xaxis";
  private static final String PARAM_YAXIS = "yaxis";
  private static final String PARAM_ZAXIS = "zaxis";
  private static final String PARAM_XSHIFT = "xshift";
  private static final String PARAM_YSHIFT = "yshift";
  private static final String PARAM_ZSHIFT = "zshift";

  private static final String[] paramNames = { PARAM_XAXIS, PARAM_YAXIS, PARAM_ZAXIS, PARAM_XSHIFT, PARAM_YSHIFT, PARAM_ZSHIFT };

  private int xAxis = 1;
  private int yAxis = 0;
  private int zAxis = 0;
  private double xShift = 0.0;
  private double yShift = 0.0;
  private double zShift = 0.0;

  @Override
  public void transform(XFormTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (xAxis > 0 && pContext.getRandomNumberGenerator().random() < 0.5) {
      pVarTP.x = -pVarTP.x - xShift;
    }

    if (yAxis > 0 && pContext.getRandomNumberGenerator().random() < 0.5) {
      pVarTP.y = -pVarTP.y - yShift;
    }

    if (zAxis > 0 && pContext.getRandomNumberGenerator().random() < 0.5) {
      pVarTP.z = -pVarTP.z - zShift;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { xAxis, yAxis, zAxis, xShift, yShift, zShift };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_XAXIS.equalsIgnoreCase(pName))
      xAxis = Tools.FTOI(pValue);
    else if (PARAM_YAXIS.equalsIgnoreCase(pName))
      yAxis = Tools.FTOI(pValue);
    else if (PARAM_ZAXIS.equalsIgnoreCase(pName))
      zAxis = Tools.FTOI(pValue);
    else if (PARAM_XSHIFT.equalsIgnoreCase(pName))
      xShift = pValue;
    else if (PARAM_YSHIFT.equalsIgnoreCase(pName))
      yShift = pValue;
    else if (PARAM_ZSHIFT.equalsIgnoreCase(pName))
      zShift = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "t_post_mirror";
  }

}
