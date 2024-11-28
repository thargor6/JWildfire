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

import org.jwildfire.base.mathlib.Complex;
import static org.jwildfire.base.mathlib.MathLib.*;

public class RingTileFunc extends VariationFunc  {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";
  private static final String PARAM_TILESIZE = "tilesize";
  private static final String PARAM_WIDTH1 = "width1";
  private static final String PARAM_WIDTH2 = "width2";
  private static final String PARAM_OUTLINE = "outline";


  private static final String[] paramNames = {PARAM_POWER, PARAM_TILESIZE, PARAM_WIDTH1, PARAM_WIDTH2, PARAM_OUTLINE};


  private double power = 5.0;
  private double tilesize = 0.50;
  private double width1 = 0.50;
  private double width2 = 0.30;
  private double outline = 0.000001;


  @Override
  public void invtransform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* ringtile by Brad Stefanov and Whittaker Courtney   */
     double _yr1 = outline * width2;
     double yh = -power;
      if (pContext.random() < 0.5)
        yh = power;
      pVarTP.y += tilesize * (pAffineTP.y + Math.round(yh * log(pContext.random())));
      if (pAffineTP.x > width2) {
        pVarTP.x += width1 * (-width2 + fmod(pAffineTP.x + width2, _yr1));
      } 
       else if (pAffineTP.x < -width2) {
        pVarTP.x += width1 * (width2 - fmod(width2 - pAffineTP.x, _yr1));
      } 
       else {
        pVarTP.x += width1 * pAffineTP.x;
      }
  }
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
  //wc_prepost_template by Whittaker Courtney. 
  double xr = pVarTP.x, yr = pVarTP.y;
       Complex z = new Complex(xr, yr);
      z.Exp();
       pVarTP.x = pAmount * z.re;  
       pVarTP.y = pAmount * z.im;
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
    return new Object[]{power, tilesize, width1, width2, outline};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else if (PARAM_TILESIZE.equalsIgnoreCase(pName))
      tilesize = pValue;
    else if (PARAM_WIDTH1.equalsIgnoreCase(pName))
      width1 = pValue;
    else if (PARAM_WIDTH2.equalsIgnoreCase(pName))
      width2 = pValue;
    else if (PARAM_OUTLINE.equalsIgnoreCase(pName))
      outline = pValue;

    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "ringtile";
  }

  @Override
  public int getPriority() {
    return 2;
  }  
}
