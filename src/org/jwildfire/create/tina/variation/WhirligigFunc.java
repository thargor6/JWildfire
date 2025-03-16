/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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
import org.jwildfire.base.Tools;
import static org.jwildfire.base.mathlib.MathLib.*;

public class WhirligigFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_MODE = "Mode";

  private static final String[] paramNames = {PARAM_MODE};
  private int Mode = 0;



  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* inspired by complex vars by cothe */


    double csin = sin(pAffineTP.x);
    double ccos = cos(pAffineTP.x);
    double csinh = sinh(pAffineTP.y);
    double ccosh = cosh(pAffineTP.y);

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
        } 
   switch (Mode) {
      case 0:
    pVarTP.x += pAmount * ccos / ccosh;
    pVarTP.y -= pAmount * csin / csinh;
        break;
      case 1:
    
    pVarTP.x -= pAmount * ccos / ccosh;
    pVarTP.y += pAmount * csin / csinh;
        break;
      case 2:
    
    pVarTP.x += pAmount * ccos / ccosh;
    pVarTP.y += pAmount * csin / csinh;
        break;
      case 3:
    
    pVarTP.x -= pAmount * ccos / ccosh;
    pVarTP.y -= pAmount * csin / csinh;
        break;
      case 4:
    
    pVarTP.x += pAmount * csin / ccosh;
    pVarTP.y -= pAmount * ccos / csinh;
        break;
      case 5:
    
    pVarTP.x -= pAmount * csin / ccosh;
    pVarTP.y += pAmount * ccos / csinh;
        break;
      case 6:
    
    pVarTP.x += pAmount * csin / ccosh;
    pVarTP.y += pAmount * ccos / csinh;
        break;
      case 7:
    
    pVarTP.x -= pAmount * csin / ccosh;
    pVarTP.y -= pAmount * ccos / csinh;
	   break;
      case 8:
    pVarTP.x += pAmount * csin / csinh;
    pVarTP.y -= pAmount * ccos / ccosh;
        break;
      case 9:
    
    pVarTP.x -= pAmount * csin / csinh;
    pVarTP.y += pAmount * ccos / ccosh;
        break;
      case 10:
    
    pVarTP.x += pAmount * csin / csinh;
    pVarTP.y += pAmount * ccos / ccosh;
        break;
      case 11:
    
    pVarTP.x -= pAmount * csin / csinh;
    pVarTP.y -= pAmount * ccos / ccosh;
        break;		
      case 12:
    pVarTP.x += pAmount * ccos / csinh;
    pVarTP.y -= pAmount * csin / ccosh;
        break;
      case 13:
    
    pVarTP.x -= pAmount * ccos / csinh;
    pVarTP.y += pAmount * csin / ccosh;
        break;
      case 14:
    
    pVarTP.x += pAmount * ccos / csinh;
    pVarTP.y += pAmount * csin / ccosh;
        break;
      case 15:
    
    pVarTP.x -= pAmount * ccos / csinh;
    pVarTP.y -= pAmount * csin / ccosh;


        break;
      default:
        break;

    }

  }
 @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{Mode};
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"whirligigMode"};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_MODE.equalsIgnoreCase(pName))
      Mode = limitIntVal(Tools.FTOI(pValue), 0, 15);


    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "whirligig";
  }

}
