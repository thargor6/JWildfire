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

import static org.jwildfire.base.MathLib.EPSILON;
import static org.jwildfire.base.MathLib.M_PI;
import static org.jwildfire.base.MathLib.cos;
import static org.jwildfire.base.MathLib.exp;
import static org.jwildfire.base.MathLib.fabs;
import static org.jwildfire.base.MathLib.sin;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class Waves4WFFunc extends VariationFunc {

  private static final String PARAM_SCALEX = "scalex";
  private static final String PARAM_SCALEY = "scaley";
  private static final String PARAM_FREQX = "freqx";
  private static final String PARAM_FREQY = "freqy";
  private static final String PARAM_USECOSX = "use_cos_x";
  private static final String PARAM_USECOSY = "use_cos_y";
  private static final String PARAM_DAMPX = "dampx";
  private static final String PARAM_DAMPY = "dampy";
  private static final String[] paramNames = { PARAM_SCALEX, PARAM_SCALEY, PARAM_FREQX, PARAM_FREQY, PARAM_USECOSX, PARAM_USECOSY, PARAM_DAMPX, PARAM_DAMPY };

  private double scalex = 0.25;
  private double scaley = 0.5;
  private double freqx = M_PI / 2;
  private double freqy = M_PI / 4;
  private int useCosX = 1;
  private int useCosY = 0;
  private double dampX = 0.0;
  private double dampY = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Modified version of waves2 from Joel F */
    double dampingX = fabs(dampX) < EPSILON ? 1.0 : exp(dampX);
    double dampingY = fabs(dampY) < EPSILON ? 1.0 : exp(dampY);
    if (useCosX == 1) {
      pVarTP.x += pAmount * (pAffineTP.x + dampingX * scalex * cos(pAffineTP.y * freqx) * sin(pAffineTP.y * freqx) * cos(pAffineTP.y * freqx)) * dampingX;
    }
    else {
      pVarTP.x += pAmount * (pAffineTP.x + dampingX * scalex * sin(pAffineTP.y * freqx) * cos(pAffineTP.y * freqx) * sin(pAffineTP.y * freqx)) * dampingX;
    }
    if (useCosY == 1) {
      pVarTP.y += pAmount * (pAffineTP.y + dampingY * scaley * cos(pAffineTP.x * freqy) * sin(pAffineTP.x * freqy) * cos(pAffineTP.x * freqy)) * dampingY;
    }
    else {
      pVarTP.y += pAmount * (pAffineTP.y + dampingY * scaley * sin(pAffineTP.x * freqy) * cos(pAffineTP.x * freqy) * sin(pAffineTP.x * freqy)) * dampingY;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { scalex, scaley, freqx, freqy, useCosX, useCosY, dampX, dampY };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scalex = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scaley = pValue;
    else if (PARAM_FREQX.equalsIgnoreCase(pName))
      freqx = pValue;
    else if (PARAM_FREQY.equalsIgnoreCase(pName))
      freqy = pValue;
    else if (PARAM_USECOSX.equalsIgnoreCase(pName))
      useCosX = Tools.FTOI(pValue);
    else if (PARAM_USECOSY.equalsIgnoreCase(pName))
      useCosY = Tools.FTOI(pValue);
    else if (PARAM_DAMPX.equalsIgnoreCase(pName))
      dampX = pValue;
    else if (PARAM_DAMPY.equalsIgnoreCase(pName))
      dampY = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "waves4_wf";
  }

}
