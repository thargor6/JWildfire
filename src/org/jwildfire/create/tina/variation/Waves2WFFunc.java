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

public class Waves2WFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALEX = "scalex";
  private static final String PARAM_SCALEY = "scaley";
  private static final String PARAM_FREQX = "freqx";
  private static final String PARAM_FREQY = "freqy";
  private static final String PARAM_USECOSX = "use_cos_x";
  private static final String PARAM_USECOSY = "use_cos_y";
  private static final String PARAM_DAMPX = "dampx";
  private static final String PARAM_DAMPY = "dampy";
  private static final String[] paramNames = {PARAM_SCALEX, PARAM_SCALEY, PARAM_FREQX, PARAM_FREQY, PARAM_USECOSX, PARAM_USECOSY, PARAM_DAMPX, PARAM_DAMPY};

  private double scalex = 0.25;
  private double scaley = 0.5;
  private double freqx = M_PI / 2;
  private double freqy = M_PI / 4;
  private int use_cos_x = 1;
  private int use_cos_y = 0;
  private double dampx = 0.0;
  private double dampy = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Modified version of waves2 from Joel F */
    if (use_cos_x == 1) {
      pVarTP.x += pAmount * (pAffineTP.x + _dampingX * scalex * cos(pAffineTP.y * freqx)) * _dampingX;
    } else {
      pVarTP.x += pAmount * (pAffineTP.x + _dampingX * scalex * sin(pAffineTP.y * freqx)) * _dampingX;
    }
    if (use_cos_y == 1) {
      pVarTP.y += pAmount * (pAffineTP.y + _dampingY * scaley * cos(pAffineTP.x * freqy)) * _dampingY;
    } else {
      pVarTP.y += pAmount * (pAffineTP.y + _dampingY * scaley * sin(pAffineTP.x * freqy)) * _dampingY;
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
    return new Object[]{scalex, scaley, freqx, freqy, use_cos_x, use_cos_y, dampx, dampy};
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
      use_cos_x = Tools.FTOI(pValue);
    else if (PARAM_USECOSY.equalsIgnoreCase(pName))
      use_cos_y = Tools.FTOI(pValue);
    else if (PARAM_DAMPX.equalsIgnoreCase(pName))
      dampx = pValue;
    else if (PARAM_DAMPY.equalsIgnoreCase(pName))
      dampy = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "waves2_wf";
  }

  private double _dampingX, _dampingY;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _dampingX = fabs(dampx) < EPSILON ? 1.0 : exp(dampx);
    _dampingY = fabs(dampy) < EPSILON ? 1.0 : exp(dampy);
  }

}
