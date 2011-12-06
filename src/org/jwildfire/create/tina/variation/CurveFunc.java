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

public class CurveFunc extends VariationFunc {

  private static final String PARAM_XAMP = "xamp";
  private static final String PARAM_YAMP = "yamp";
  private static final String PARAM_XLENGTH = "xlength";
  private static final String PARAM_YLENGTH = "ylength";

  private static final String[] paramNames = { PARAM_XAMP, PARAM_YAMP, PARAM_XLENGTH, PARAM_YLENGTH };

  private double xAmp = 0.25;
  private double yAmp = 0.5;
  private double xLength = 1.0;
  private double yLength = 1.0;

  @Override
  public void transform(TransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Curve in the Apophysis Plugin Pack */
    double pc_xlen = xLength * xLength;
    double pc_ylen = yLength * yLength;

    if (pc_xlen < 1E-20)
      pc_xlen = 1E-20;

    if (pc_ylen < 1E-20)
      pc_ylen = 1E-20;

    pVarTP.x += pAmount * (pAffineTP.x + xAmp * Math.exp(-pAffineTP.y * pAffineTP.y / pc_xlen));
    pVarTP.y += pAmount * (pAffineTP.y + yAmp * Math.exp(-pAffineTP.x * pAffineTP.x / pc_ylen));
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { xAmp, yAmp, xLength, yLength };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_XAMP.equalsIgnoreCase(pName))
      xAmp = pValue;
    else if (PARAM_YAMP.equalsIgnoreCase(pName))
      yAmp = pValue;
    else if (PARAM_XLENGTH.equalsIgnoreCase(pName))
      xLength = pValue;
    else if (PARAM_YLENGTH.equalsIgnoreCase(pName))
      yLength = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "curve";
  }

}
