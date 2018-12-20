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

public class Tile_Reverse_Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SPACE = "space";
  private static final String PARAM_REVERSAL = "reversal";
  private static final String PARAM_VERTICAL = "vertical";

  private static final String[] paramNames = { PARAM_SPACE, PARAM_REVERSAL, PARAM_VERTICAL};
  private double space = 1.0;
  private double reversal = 1.0;
  private int vertical = 0;
  private double rev = -1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // tile_space by Whittaker Courtney 12-14-2018

    double n = pContext.random();

    if (reversal == 1){
      rev = -1;
    }
    else{
      rev = 1;
    }

    if (n < 0.5 && vertical != 1) {
      pVarTP.x = pAmount * (rev * pAffineTP.x) + (space);
      pVarTP.y = pAmount * pAffineTP.y;
    }
    else if (n > 0.5 && vertical != 1){
      pVarTP.x = pAmount * (rev * pAffineTP.x) - (space);
      pVarTP.y = pAmount * pAffineTP.y;
    }

    else if (n < 0.5 && vertical == 1){
      pVarTP.y = pAmount * (rev * pAffineTP.y) + (space);
      pVarTP.x = pAmount * pAffineTP.x;
    }
    else if (n > 0.5 && vertical == 1){
      pVarTP.y = pAmount * (rev * pAffineTP.y) - (space);
      pVarTP.x = pAmount * pAffineTP.x;
    }

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { space, reversal, vertical};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SPACE.equalsIgnoreCase(pName))
      space = pValue;
    else if (PARAM_REVERSAL.equalsIgnoreCase(pName))
      reversal = pValue;
    else if (PARAM_VERTICAL.equalsIgnoreCase(pName))
      vertical = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }


  @Override
  public String getName() {
    return "tile_reverse";
  }

}
