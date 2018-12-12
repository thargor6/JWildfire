/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
  General Public License as published by the Free Software Foundation; either version 2.1 of the 
  License, or (at your option) any later version.
 
  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
  even the implied warranty of MERCHANTABILITY or FITNESS FOR a PARTICULAR PURPOSE. See the GNU 
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this software; 
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.sin;

public class PulseFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQX = "freqx";
  private static final String PARAM_FREQY = "freqy";
  private static final String PARAM_SCALEX = "scalex";
  private static final String PARAM_SCALEY = "scaley";

  private static final String[] paramNames = {PARAM_FREQX, PARAM_FREQY, PARAM_SCALEX, PARAM_SCALEY};
  private double freqx = 2.0;
  private double freqy = 2.0;
  private double scalex = 1.0;
  private double scaley = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
                        // Lozi by TyrantWave
                        // https://www.deviantart.com/tyrantwave/art/Henon-and-Lozi-Apo-Plugins-125039554
                        double pAmount) {


    pVarTP.x += pAmount * (pAffineTP.x + scalex * sin(pAffineTP.x * freqx));
    pVarTP.y += pAmount * (pAffineTP.y + scaley * sin(pAffineTP.y * freqy));

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
    return new Object[]{freqx, freqy, scalex, scaley};
  }


  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQX.equalsIgnoreCase(pName)) {
      freqx = pValue;
    } else if (PARAM_FREQY.equalsIgnoreCase(pName)) {
      freqy = pValue;
    } else if (PARAM_SCALEX.equalsIgnoreCase(pName)) {
      scalex = pValue;
    } else if (PARAM_SCALEY.equalsIgnoreCase(pName)) {
      scaley = pValue;
    } else {
      System.out.println("pName not recognized: " + pName);
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "pulse";
  }

}
