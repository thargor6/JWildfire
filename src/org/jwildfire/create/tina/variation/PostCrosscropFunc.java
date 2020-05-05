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
import org.jwildfire.base.Tools;

public class PostCrosscropFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  
  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";
  private static final String PARAM_Z = "z";
  private static final String PARAM_ROTATION = "rotation";
  private static final String PARAM_REVERSE = "reverse";
    
  private static final String[] paramNames = { PARAM_X, PARAM_Y, PARAM_Z, PARAM_ROTATION, PARAM_REVERSE};
  private double x = 1.0;
  private double y = 1.0;
  private double z = 1.0;
  private double rotation = 0.0;
  private int reverse = 0;
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
//post_crosscrop by Whittaker Courtney

   double rotation_r = rotation * M_PI / 180;
   double rx = pVarTP.x * cos(rotation_r) - pVarTP.y * sin(rotation_r);
   double ry = pVarTP.y * cos(rotation_r) + pVarTP.x * sin(rotation_r);
  
 if (reverse == 0){
     if ( ((rx <= x && rx >= -x || ry <= y && ry >= -y) && pVarTP.z <= z && pVarTP.z >= -z )){
       pVarTP.doHide = false;
       pVarTP.x = pAmount * pVarTP.x;  
       pVarTP.y = pAmount * pVarTP.y; 
       pVarTP.z = pAmount * pVarTP.z;     
        }
     else{
       pVarTP.x = pVarTP.y = pVarTP.z = 0;   
       pVarTP.doHide = true;
       return;
         }
 }
         
 else if (reverse == 1){        
     if ( ((rx <= x && rx >= -x || ry <= y && ry >= -y) && pVarTP.z <= z && pVarTP.z >= -z )){    
       pVarTP.x = pVarTP.y = pVarTP.z = 0;   
       pVarTP.doHide = true;
       return;
     }
     else{
       pVarTP.doHide = false;
       pVarTP.x = pAmount * pVarTP.x;  
       pVarTP.y = pAmount * pVarTP.y; 
       pVarTP.z = pAmount * pVarTP.z;  
         } 
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
    return new Object[] {x, y, z, rotation, reverse};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else if (PARAM_Z.equalsIgnoreCase(pName))
      z = pValue; 
    else if (PARAM_ROTATION.equalsIgnoreCase(pName))
      rotation = pValue;                            
    else if (PARAM_REVERSE.equalsIgnoreCase(pName))
      reverse = (int) Tools.limitValue(pValue, 0, 1);             
    else
      throw new IllegalArgumentException(pName);
  }
 

  @Override
  public String getName() {
    return "post_crosscrop";
  }
  
  @Override
  public int getPriority() {
    return 1;
  }  

}
