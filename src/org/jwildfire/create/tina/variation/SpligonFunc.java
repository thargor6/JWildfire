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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class SpligonFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SIDES = "sides";
  private static final String PARAM_R= "r";
  private static final String PARAM_I = "i";

  private static final String[] paramNames = {PARAM_SIDES, PARAM_R, PARAM_I};

  private double sides = 3;
  private double r = 1;
  private double i = 1;
  private double j = 0;
  private double th = 0;
  private double thi = 0;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    th = sides * M_1_2PI;
    thi = 1.0 / th;
    j = 3.14159265358979323846 * i / (-2.0 * sides) ;
  }

  @Override
  // spligon by DarkBeam
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double dx,dy;
    double t = thi * floor(pAffineTP.getPrecalcAtanYX() * th ) + j;
    dx = sin(t); dy = cos(t);
    pVarTP.x += pAmount * (pAffineTP.x + dy * r);
    pVarTP.y += pAmount * (pAffineTP.y + dx * r);

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
    return new Object[]{sides, r, i};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SIDES.equalsIgnoreCase(pName))
      sides = (Tools.FTOI(pValue) == 0) ? 1 : pValue;
    else if (PARAM_R.equalsIgnoreCase(pName))
      r = pValue;
    else if (PARAM_I.equalsIgnoreCase(pName))
      i = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "spligon";
  }

}