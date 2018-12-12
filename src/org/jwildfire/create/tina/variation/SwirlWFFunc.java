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

public class SwirlWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_N = "n";
  private static final String PARAM_RADIUS = "r";

  private static final String[] paramNames = {PARAM_N};

  private double N = 0;


  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    double rad = pAffineTP.getPrecalcSqrt();
    double ang = pAffineTP.getPrecalcAtanYX();   // + log(rad)*shift;


    //   double ang = pContext.random()*360.0;
    //   double rad=  -1.0 + pContext.random()*2.0;

    pVarTP.x += pAmount * (rad * Math.cos(ang));
    pVarTP.y += pAmount * (rad * Math.sin(ang));
    pVarTP.z += pAmount * (Math.sin(6.0 * Math.cos(rad) - N * ang));
    pVarTP.color = Math.abs(Math.sin(6.0 * Math.cos(rad) - N * ang));

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{N};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_N.equalsIgnoreCase(pName))
      N = (int) pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "swirl3D_wf";
  }

}
