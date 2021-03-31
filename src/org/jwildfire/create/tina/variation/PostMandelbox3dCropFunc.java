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
import org.jwildfire.base.Tools;
import static org.jwildfire.base.mathlib.MathLib.*;


public class PostMandelbox3dCropFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_F = "f";  
  private static final String PARAM_ITERATIONS = "iterations";
  private static final String PARAM_BAILOUT = "bailout";
      
  private static final String[] paramNames = { PARAM_SCALE, PARAM_RADIUS, PARAM_F, PARAM_ITERATIONS, PARAM_BAILOUT};
  private double scale = 2.0;
  private double radius = 0.5;
  private double f = 1.0;
  private int iterations = 12;
  private double bailout = 16.0;
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
  // crop_mandelbox3d by Whittaker Courtney. 
  // Implementation of the formula: https://www.geeks3d.com/20100427/do-you-know-the-mandelbox-fractal/
  // Works best with a square3d.
  
  double x = pVarTP.x*6;
  double y = pVarTP.y*6;
  double z = pVarTP.z*6;
  double x1 = x;
  double y1 = y;
  double z1 = z;
  
  double mag;
  double r = radius;
  
  for (int i = 0; i < iterations; i++)
  {
   // boxfold   
   if (x > 1)
   {
       x = 2 - x;
   }   
   else if (x < -1)
   {
       x = -2 - x;
   }
   
   if (y > 1)
   {
       y = 2 - y;
   }   
   else if (y < -1)
   {
       y = -2 - y;
   }   

   if (z > 1)
   {
       z = 2 - z;
   }   
   else if (z < -1)
   {
       z = -2 - z;
   }
   x *= f;
   y *= f;
   z *= f;
   
   // ballfold  
   mag = sqrt(x*x + y*y + z*z);
   
   if (mag < r)
   {
       x = x/(r*r);
       y = y/(r*r);
       z = z/(r*r);
   }
   else if (mag < 1)
   {
       x = x/(mag*mag);
       y = y/(mag*mag);   
       z = z/(mag*mag);             
   }
   x *= scale;
   y *= scale;
   z *= scale;
   // Add c
   x += x1;
   y += y1;
   z += z1; 
   
   if (mag < bailout)
   {
       pVarTP.doHide = false;
       pVarTP.x = pAmount * pAffineTP.x;
       pVarTP.y = pAmount * pAffineTP.y;
       pVarTP.z = pAmount * pAffineTP.z;    
   }
   else
   {
       pVarTP.doHide = true;
       pVarTP.x = pVarTP.y = pVarTP.z = 0;
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
    return new Object[] {scale, radius, f, iterations, bailout};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = pValue;
    else if (PARAM_F.equalsIgnoreCase(pName))
      f = pValue;      
    else if (PARAM_ITERATIONS.equalsIgnoreCase(pName))
      iterations = limitIntVal(Tools.FTOI(pValue), 0, 250);   
    else if (PARAM_BAILOUT.equalsIgnoreCase(pName))
      bailout = pValue;    
    else
      throw new IllegalArgumentException(pName);
  }
 
  @Override
  public String getName() {
    return "post_mandelbox3d_crop";
  }
  
  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_ESCAPE_TIME_FRACTAL};
  }
}
