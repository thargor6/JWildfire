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

import static org.jwildfire.base.mathlib.MathLib.*;

public class Popcorn2_3DFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";
  private static final String PARAM_Z = "z";
  private static final String PARAM_C = "c";
  private static final String[] paramNames = {PARAM_X, PARAM_Y, PARAM_Z, PARAM_C};

  private double x = 0.1;
  private double y = 0.1;
  private double z = 0.1;
  private double c = 3.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* popcorn2_3D by Larry Berlin, http://aporev.deviantart.com/art/3D-Plugins-Collection-One-138514007?q=gallery%3Aaporev%2F8229210&qo=15 */
    double inZ, otherZ, tempTZ, tempPZ, tmpVV;
    inZ = pAffineTP.z;
    otherZ = pVarTP.z;

    if (fabs(pAmount) <= 1.0) {
      tmpVV = fabs(pAmount) * pAmount; //sqr(pAmount) value retaining sign
    } else {
      tmpVV = pAmount;
    }
    if (otherZ == 0.0) {
      tempPZ = tmpVV * sin(tan(this.c)) * atan2(pAffineTP.y, pAffineTP.x);
    } else {
      tempPZ = pVarTP.z;
    }
    if (inZ == 0.0) {
      tempTZ = tmpVV * sin(tan(this.c)) * atan2(pAffineTP.y, pAffineTP.x);
    } else {
      tempTZ = pAffineTP.z;
    }

    pVarTP.x += pAmount * 0.5 * (pAffineTP.x + this.x * sin(tan(this.c * pAffineTP.y)));
    pVarTP.y += pAmount * 0.5 * (pAffineTP.y + this.y * sin(tan(this.c * pAffineTP.x)));
    pVarTP.z = tempPZ + tmpVV * (this.z * sin(tan(this.c)) * tempTZ);

    /*
        Original code:  
      pVarTP.x += pAmount * (pAffineTP.x + VAR(popcorn2_x) * sin(tan(VAR(popcorn2_c)*pAffineTP.y)));
      pVarTP.y += pAmount * (pAffineTP.y + VAR(popcorn2_y) * sin(tan(VAR(popcorn2_c)*pAffineTP.x)));
    */
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{x, y, z, c};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else if (PARAM_Z.equalsIgnoreCase(pName))
      z = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "popcorn2_3D";
  }

}
