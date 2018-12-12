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

import org.jwildfire.create.tina.base.XYZPoint;

public class DCCrackleWFFunc extends CrackleFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_COLOR_SCALE = "color_scale";
  private static final String PARAM_COLOR_OFFSET = "color_offset";

  protected static final String[] additionalParamNames = {PARAM_COLOR_SCALE, PARAM_COLOR_OFFSET};

  private double colorScale = 0.5;
  private double colorOffset = 0.0;

  // dc_crackle_wf by thargor6
  // This is just a specialization of the crackle plugin originally invented by slobo777, see http://slobo777.deviantart.com/art/Apo-Plugins-Hexes-And-Crackle-99243824
  // It uses the Voronoi-distance as color index making it a "dc_"-variation which may produce very interesting textures

  @Override
  protected void applyCellCalculation(XYZPoint pVarTP, double pAmount, double DXo, double DYo, double L) {
    pVarTP.color = L * colorScale + colorOffset;
    if (pVarTP.color < 0)
      pVarTP.color = 0.0;
    else if (pVarTP.color > 1.0)
      pVarTP.color = 1.0;
    pVarTP.x += pAmount * DXo;
    pVarTP.y += pAmount * DYo;
  }

  @Override
  public String getName() {
    return "dc_crackle_wf";
  }

  @Override
  public String[] getParameterNames() {
    return joinArrays(paramNames, additionalParamNames);
  }

  @Override
  public Object[] getParameterValues() {
    return joinArrays(super.getParameterValues(), new Object[]{colorScale, colorOffset});
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_COLOR_SCALE.equalsIgnoreCase(pName))
      colorScale = pValue;
    else if (PARAM_COLOR_OFFSET.equalsIgnoreCase(pName))
      colorOffset = pValue;
    else
      super.setParameter(pName, pValue);
  }

}
