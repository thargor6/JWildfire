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
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.base.XYZPoint;
import static org.jwildfire.base.mathlib.MathLib.*;

public class Hypershift extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SHIFT = "shift";
  private static final String PARAM_STRETCH = "stretch";


  private static final String[] paramNames = { PARAM_SHIFT, PARAM_STRETCH};

  private double shift = 2.0;
  private double stretch = 1.0;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    // "Hypershift" variation created by Zy0rg implemented into JWildfire by Brad Stefanov
 double scale = 1 - shift * shift;
       double rad = 1/(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
       double x = rad * pAffineTP.x +shift;
       double y = rad * pAffineTP.y;
       rad = pAmount * scale / (x * x + y * y);
       pVarTP.x += rad * x + shift;
       pVarTP.y += rad * y*stretch;
       pVarTP.z += pAmount * pAffineTP.z;
    // ----------------------------------
    // ...
  }

  
  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { shift, stretch };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SHIFT.equalsIgnoreCase(pName))
      shift = pValue;
    else if (PARAM_STRETCH.equalsIgnoreCase(pName))
      stretch = pValue;

    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "Hypershift";
  }

}