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

import static org.jwildfire.base.mathlib.MathLib.sin;

public class Waves2_3DFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_SCALE = "scale";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_SCALE};

  private double freq = 2.0;
  private double scale = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* waves2_3D by Larry Berlin, http://aporev.deviantart.com/art/New-3D-Plugins-136484533?q=gallery%3Aaporev%2F8229210&qo=22 */
    double avgxy = (pAffineTP.x + pAffineTP.y) / 2.0;
    pVarTP.x += pAmount * (pAffineTP.x + this.scale * sin(pAffineTP.y * this.freq));
    pVarTP.y += pAmount * (pAffineTP.y + this.scale * sin(pAffineTP.x * this.freq));
    pVarTP.z += pAmount * (pAffineTP.z + this.scale * sin(avgxy * this.freq)); //Averages the XY to get Z
    //  Cool results, very successful 3D effect
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{freq, scale};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName))
      freq = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "waves2_3D";
  }
}
