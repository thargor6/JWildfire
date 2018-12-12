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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class DCCubeFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_C1 = "c1";
  private static final String PARAM_C2 = "c2";
  private static final String PARAM_C3 = "c3";
  private static final String PARAM_C4 = "c4";
  private static final String PARAM_C5 = "c5";
  private static final String PARAM_C6 = "c6";
  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";
  private static final String PARAM_Z = "z";

  private static final String[] paramNames = {PARAM_C1, PARAM_C2, PARAM_C3, PARAM_C4, PARAM_C5, PARAM_C6, PARAM_X, PARAM_Y, PARAM_Z};

  private double c1 = 0.1;
  private double c2 = 0.2;
  private double c3 = 0.3;
  private double c4 = 0.4;
  private double c5 = 0.5;
  private double c6 = 0.6;
  private double x = 1.0;
  private double y = 1.0;
  private double z = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* dc_cube by Xyrus02, http://xyrus02.deviantart.com/art/DC-Cube-plugin-update-170465423 */
    double p = 2 * pContext.random() - 1.0;
    double q = 2 * pContext.random() - 1;
    int i = pContext.random(Integer.MAX_VALUE) % 3;
    boolean j = (pContext.random(Integer.MAX_VALUE) & 1) == 1;
    double x = 0.0, y = 0.0, z = 0.0;
    switch (i) {
      case 0:
        x = pAmount * (j ? -1 : 1);
        y = pAmount * p;
        z = pAmount * q;
        if (j)
          pVarTP.color = c1;
        else
          pVarTP.color = c2;
        break;
      case 1:
        x = pAmount * p;
        y = pAmount * (j ? -1 : 1);
        z = pAmount * q;
        if (j)
          pVarTP.color = c3;
        else
          pVarTP.color = c4;
        break;
      case 2:
        x = pAmount * p;
        y = pAmount * q;
        z = pAmount * (j ? -1 : 1);
        if (j)
          pVarTP.color = c5;
        else
          pVarTP.color = c6;
        break;
      default: // nothing to do
        break;
    }
    pVarTP.x += x * this.x;
    pVarTP.y += y * this.y;
    pVarTP.z += z * this.z;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{c1, c2, c3, c4, c5, c6, x, y, z};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_C1.equalsIgnoreCase(pName))
      c1 = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_C2.equalsIgnoreCase(pName))
      c2 = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_C3.equalsIgnoreCase(pName))
      c3 = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_C4.equalsIgnoreCase(pName))
      c4 = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_C5.equalsIgnoreCase(pName))
      c5 = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_C6.equalsIgnoreCase(pName))
      c6 = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else if (PARAM_Z.equalsIgnoreCase(pName))
      z = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "dc_cube";
  }

}
