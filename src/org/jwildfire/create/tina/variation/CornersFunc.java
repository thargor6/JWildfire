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
import static org.jwildfire.base.mathlib.MathLib.pow;
import static org.jwildfire.base.mathlib.MathLib.log;


public class CornersFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  
  private static final String PARAM_XWIDTH = "x";
  private static final String PARAM_YWIDTH = "y";
  private static final String PARAM_MULTX = "mult x";
  private static final String PARAM_MULTY = "mult y";
  private static final String PARAM_POWER = "power";
  private static final String PARAM_LOGMODE = "log mode (0/1)";
  private static final String PARAM_LOGMULT = "log mult";
  
  private static final String[] paramNames = { PARAM_XWIDTH, PARAM_YWIDTH, PARAM_MULTX, PARAM_MULTY, PARAM_POWER, PARAM_LOGMODE, PARAM_LOGMULT };
  private double xwidth = 1.0;
  private double ywidth = 1.0;
  private double multx = 1.0;
  private double multy = 1.0;
  private double power = 0.75;
  private double logmode = 0;
  private double logmult = 1;
  private double ex = 0;
  private double ey = 0;
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
   // corners by Whittaker Courtney 8-8-2018
      
      double x = pAffineTP.x;
      double y = pAffineTP.y;
      double xs = pAffineTP.x * pAffineTP.x;
      double ys = pAffineTP.y * pAffineTP.y;
      
  //log mode    
    if (logmode == 0){
    ex = pow(xs, power) * multx;
    ey = pow(ys, power) * multy;
  }
    else{
    ex = pow(log((xs * logmult) +3), multx +2)-1.33;
    ey = pow(log((ys * logmult) +3), multy +2)-1.33;
    }

          
    if (pAffineTP.x > 0) {
      pVarTP.x += pAmount * ex + xwidth;
    }
    else {
      pVarTP.x += pAmount * -ex - xwidth;                  
    }

    if (pAffineTP.y > 0) {
      pVarTP.y += pAmount * ey + ywidth;
    }
    else {
      pVarTP.y += pAmount * -ey - ywidth;
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
    return new Object[] { xwidth, ywidth, multx, multy, power, logmode, logmult };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_XWIDTH.equalsIgnoreCase(pName))
      xwidth = pValue;
    else if (PARAM_YWIDTH.equalsIgnoreCase(pName))
      ywidth = pValue;
    else if (PARAM_MULTX.equalsIgnoreCase(pName))
      multx = pValue;
    else if (PARAM_MULTY.equalsIgnoreCase(pName))
      multy = pValue;
    else if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else if (PARAM_LOGMODE.equalsIgnoreCase(pName))
      logmode = pValue;
    else if (PARAM_LOGMULT.equalsIgnoreCase(pName))
      logmult = pValue;
    else
      throw new IllegalArgumentException(pName);
  }
 

  @Override
  public String getName() {
    return "corners";
  }

}
