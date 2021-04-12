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


public class PostSmartDotRemoverFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;  
  
  private static final String[] paramNames = {};
  private double post_scrop_x, post_scrop_y, post_scrop_z, post_scrop_c;   
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Post_smart_dotremover by Whittaker Courtney 
    // Based on post_smartcrop by Zy0rg, http://zy0rg.deviantart.com/art/SmartCrop-267036043
    // Isolated the post_smartcrop's ability to remove dots while
    // increasing density(?)
    // I don't fully understand how this works...
    
    double xi, yi, zi;  
            
    xi = pVarTP.x;
    yi = pVarTP.y;
    zi = pVarTP.z;

      if (pVarTP.x == 0 && pVarTP.y == 0 && pVarTP.z == 0) {
            pVarTP.x = post_scrop_x;
            pVarTP.y = post_scrop_y;
            pVarTP.z = post_scrop_z;
            pVarTP.doHide = true;
            pVarTP.color = post_scrop_c;
            return;                  
      }
      else
      { 
            pVarTP.doHide = false;
            post_scrop_x = pAmount * xi;
            post_scrop_y = pAmount * yi;
            post_scrop_z = pAmount * zi;
            post_scrop_c = pVarTP.color;
      }                     
  }
  
  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] {};
  }

  @Override
  public void setParameter(String pName, double pValue) {

      throw new IllegalArgumentException(pName);
  }
 
  @Override
  public String getName() {
    return "post_smart_dotremover";
  }

  @Override
  public int getPriority() {
    return 1;
  }  
  
}
