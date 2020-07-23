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
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;

public class Pyramid3DFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  
	private static final String PARAM_H = "h";


// pyramid3D with signed distance functions
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double h=0.250;


  private static final String[] additionalParamNames = { PARAM_H};
	 

  double sdPyramid(  vec3 p,  double h )
  {
      double m2 = h*h + 0.25;
      
      // symmetry
      p.x = G.abs(p.x);
      p.z = G.abs(p.z);
      if(p.z>p.x )
      {
    	  p.x=p.z;
    	  p.z=p.x;
      }	  
      else
      {  p.x=p.x;
         p.z=p.z;
     }
      
      p.x =p.x - 0.5;
      p.z= p.z - 0.5;
  	
      // project into face plane (2D)
      vec3 q = new vec3( p.z, h*p.y - 0.5*p.x, h*p.x + 0.5*p.y);
     
      double s = G.max(-q.x,0.0);
      double t = G.clamp( (q.y-0.5*p.z)/(m2+0.25), 0.0, 1.0 );
      
      double a = m2*(q.x+s)*(q.x+s) + q.y*q.y;
  	double b = m2*(q.x+0.5*t)*(q.x+0.5*t) + (q.y-m2*t)*(q.y-m2*t);
      
      double d2 = G.min(q.y,-q.x*m2-q.y*0.5) > 0.0 ? 0.0 : G.min(a,b);
      
      // recover 3D and scale, and add sign
      return Math.sqrt( (d2+q.z*q.z)/m2 ) * G.sign(G.max(q.z,-p.y));
  }
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = (pContext.random() - 0.5);
    double y = (pContext.random() - 0.5);
    double z = (pContext.random() - 0.5);
    
    vec3 p=new vec3(x,y,z);

    double distance=sdPyramid(p,h);
    pVarTP.doHide=true;
    if(distance <0.0)
    {
 	    pVarTP.doHide=false;
    	pVarTP.x=pAmount*x;
    	pVarTP.y=pAmount*y;
    	pVarTP.z=pAmount*z;
    }
  }

  
  @Override
  public String getName() {
    return "pyramid3D";
  }
  
	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] {h};
	}
//
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_H)) {
			h =Tools.limitValue(pValue, 0.0, 0.50);
		}
		else 
		      throw new IllegalArgumentException(pName);
	}

	@Override
	public boolean dynamicParameterExpansion() {
		return true;
	}

	@Override
	public boolean dynamicParameterExpansion(String pName) {
		// preset_id doesn't really expand parameters, but it changes them; this will make them refresh
		return true;
	}	

}
