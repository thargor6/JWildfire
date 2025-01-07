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


public class PostMandelbulb3dCropFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  
  private static final String PARAM_POWER = "power";
  private static final String PARAM_XZ = "xz";
  private static final String PARAM_ITERATIONS = "iterations";
  private static final String PARAM_BAILOUT = "bailout";
  private static final String PARAM_FILLED = "filled";
        
  private static final String[] paramNames = { PARAM_POWER, PARAM_XZ, PARAM_ITERATIONS, PARAM_BAILOUT, PARAM_FILLED };
  private double power = 9.0;
  private double xz = 0.0;
  private int iterations = 12;
  private double bailout = 2.0;
  private int filled = 0;
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
  // crop_mandelbulb3d by Whittaker Courtney, adapted from the Mandelbulb3d code for IQBulb.
  // Works best with a square3d or filled = 1.
  double x, y, z;
  if ( filled == 1 )
  {
      double fillAmount = (pAmount + 1) * 2;
      pAffineTP.x = pContext.random() * fillAmount - fillAmount / 2;
      pAffineTP.y = pContext.random() * fillAmount - fillAmount / 2;
      pAffineTP.z = pContext.random() * fillAmount - fillAmount / 2;
      x = pAffineTP.x;
      y = pAffineTP.y;
      z = pAffineTP.z;      
  }
  else
  {
      x = pVarTP.x;
      y = pVarTP.y;
      z = pVarTP.z;       
  }

  double xf, yf, zf;
  
  double Power_ = power;
  
  double sq_r;
  double sq_xz;
  double r, theta, zangle;
  int colorIterations = 8;
   
  for (int i = 0; i < iterations; i++)
  {
   sq_r = sqrt(x*x+y*y+z*z);
   sq_xz = sqrt(x*x+z*z);
   r = pow(sq_r, Power_);

   theta = atan2( sq_xz+xz, y) * Power_;

   zangle = atan2(x,z)  * Power_;

   xf = sin(zangle)*sin(theta)*r;
   yf = cos(theta)            *r;
   zf = sin(theta)*cos(zangle)*r;
   xf += x;
   yf += y;
   zf += z;
   
   x = xf;
   y = yf;
   z = zf;
   
   pVarTP.color = min(1.0, (double)colorIterations /(double)(i+1));   
   if (sqrt(xf*xf + yf*yf + zf*zf) < bailout)
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
		pVarTP.z = pAmount * pAffineTP.z;
	}

  }
  
  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { power, xz, iterations, bailout, filled };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else if (PARAM_XZ.equalsIgnoreCase(pName))
      xz = pValue;
    else if (PARAM_ITERATIONS.equalsIgnoreCase(pName))
      iterations = limitIntVal(Tools.FTOI(pValue), 0, 250);   
    else if (PARAM_BAILOUT.equalsIgnoreCase(pName))
      bailout = pValue;  
    else if (PARAM_FILLED.equalsIgnoreCase(pName))
      filled = limitIntVal(Tools.FTOI(pValue), 0, 1);          
    else
      throw new IllegalArgumentException(pName);
  }
 
  @Override
  public String getName() {
    return "post_mandelbulb3d_crop";
  }
  
  @Override
  public int getPriority() {
    return 1;
  }
  
  @Override
  public void randomize() {
    power = Math.random() * 12.0 + 3.0;
    if (Math.random() < 0.5) power = (int) power;
    xz = Math.random() * 5.0 - 2.5;
    iterations = (int) (Math.random() * 20 + 1);
    bailout = Math.random() * 3.5 + 0.5;
    filled = (int) (Math.random() * 2);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_ESCAPE_TIME_FRACTAL};
  }
  
}
