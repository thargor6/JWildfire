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

public class CheckerboardWFFunc extends VariationFunc {

  private static final String PARAM_SIZE = "size";
  private static final String PARAM_PETALS = "petals";

  private static final String[] paramNames = { PARAM_SIZE, PARAM_PETALS };

  private int size = 8;
  private double petals = 7.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    //    pVarTP.x += pAmount * (pContext.random() - 0.5);
    //    pVarTP.y += pAmount * (pContext.random() - 0.5);
    //    pVarTP.z += pAmount * (pContext.random() - 0.5);

    double lx = (pContext.random() - 0.5);
    double ly = (pContext.random() - 0.5);

    int field = pContext.random(size * size / 2) * 2;
    int x = field % size;
    int y = field / size;
    if (y % 2 == 0) {
      x++;
    }

    double fieldsize = pAmount / (double) size;

    pVarTP.x += x * fieldsize + lx;
    pVarTP.y += y * fieldsize + ly;

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { size, petals };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SIZE.equalsIgnoreCase(pName))
      size = Tools.FTOI(pValue);
    else if (PARAM_PETALS.equalsIgnoreCase(pName))
      petals = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "checkerboard_wf";
  }

}
