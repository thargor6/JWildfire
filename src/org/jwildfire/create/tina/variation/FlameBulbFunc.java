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
import java.util.Random;

public class FlameBulbFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  
  private static final String PARAM_POWER = "root_power";
  private static final String PARAM_RPOW = "radius_pow";
  private static final String PARAM_ADD_C = "add C";
  private static final String PARAM_COLOR_MODE = "color mode 1-4"; 
  private static final String PARAM_COLOR_SCALE = "color scale";          
  private static final String[] paramNames = { PARAM_POWER, PARAM_RPOW, PARAM_ADD_C, PARAM_COLOR_MODE, PARAM_COLOR_SCALE };
  
  private double power = 8.0;
  private double rpow = 1.0;
  private double add_C = 1.0;
  private int color_mode = 1;
  private double color_scale = 1.0;
    
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
  /* flame_bulb by Whittaker Courtney, adapted from the Mandelbulb3d code for IQBulb.
     Originally an escapetime crop formula but removed bailout to use roots 
     instead of powers to make more of a true flame mandelbulb that 
     responds to affine transforms and flame transformation variations.
     
     Can think of it as the julia3d(complex roots) variation but in spherical form.
     At least that is the attempt, my math is terrible so not sure how accurate this is.
  */ 
        
  double Power_ = 1/power; 
  double x = pAmount * pAffineTP.x;
  double y = pAmount * pAffineTP.y;
  double z = pAmount * pAffineTP.z; 
   
  double xo = x;
  double yo = y;
  double zo = z;    
  
  double mag1 = (sqrt(x*x+y*y+z*z)/2)*add_C;  
  
  // Add C
  if (add_C != 0){     
      x += mag1;
      y += mag1;
      z += mag1;  
  }
  
  // generate random int between 1 and Power_.  
  Random rand = new Random();
  int k = rand.nextInt(((int)(power)+1));
  int k2 = rand.nextInt(((int)(power)+1));
   
  for (int i = 0; i < 2; i++){
      x -= xo;
      y -= yo;
      z -= zo;      
 
      // start mandelbulb calculation        
      double sq_r = sqrt(x*x+y*y+z*z);
      double r = pow(sq_r, Power_);

      double theta = (acos(z/sq_r) + 2*k*M_PI) * Power_;
      double zangle = (atan2(y,x) + i*2*k2*M_PI ) * Power_;

      x = sin(theta)*cos(zangle)*pow(r, rpow);
      y = sin(theta)*sin(zangle)*pow(r, rpow);
      z = cos(theta)            *pow(r, rpow);             
  }
  
   pVarTP.x = x;
   pVarTP.y = y;
   pVarTP.z = z; 
   
   // coloring:
   // 0 = standard gradient coloring, 1 = magnitude coloring enabled, 2 = both
   double mag2 = mag1 / color_scale;
          
       if (color_mode == 1){
         pVarTP.color = mag2;
       }
       else if (color_mode == 2){
         pVarTP.color += mag2;             
       }
       else if (color_mode == 3){
         pVarTP.color = sin(sqrt(x*x+y*y+z*z)+mag2*2);             
       }       
       else if (color_mode == 4){
         pVarTP.color += sin(sqrt(x*x+y*y+z*z)+mag2*2);             
       }  
       else if (color_mode == 5){
         pVarTP.color = log(sqrt(x*x+y*y+z*z)+mag2*2);             
       }       
       else if (color_mode == 6){
         pVarTP.color += log(sqrt(x*x+y*y+z*z)+mag2*2);             
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
    return new Object[] { power, rpow, add_C, color_mode, color_scale};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else if (PARAM_RPOW.equalsIgnoreCase(pName))
      rpow = pValue; 
    else if (PARAM_ADD_C.equalsIgnoreCase(pName))
      add_C = pValue;              
    else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName))
      color_mode = limitIntVal(Tools.FTOI(pValue), 0, 6); 
    else if (PARAM_COLOR_SCALE.equalsIgnoreCase(pName))
      color_scale = pValue;                    
    else
      throw new IllegalArgumentException(pName);
  }
 
  @Override
  public String getName() {
    return "flame_bulb";
  }
}
