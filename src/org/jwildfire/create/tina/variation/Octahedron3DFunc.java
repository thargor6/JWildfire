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

import js.glsl.G;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class Octahedron3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  
	private static final String PARAM_H = "h";


// octahedron3D with signed distance functions
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double h=0.250;


  private static final String[] additionalParamNames = { PARAM_H};
	
  
  
  double sdOctahedron(vec3 p, double s)  
  {
      p = G.abs(p);
      double m = p.x + p.y + p.z - s;

   	vec3 q;
           if( 3.0*p.x < m ) 
        	   q = new vec3(p.x,p.y,p.z);
      else if( 3.0*p.y < m ) 
    	  q = new vec3(p.y,p.z,p.x);
      else if( 3.0*p.z < m ) 
    	  q = new vec3(p.z,p.x,p.y);
      else 
    	  return m*0.57735027;
      double k = G.clamp(0.5*(q.z-q.y+s),0.0,s); 
      return G.length(new vec3(q.x,q.y-s+k,q.z-k)); 
  }
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		for (int i=1; i<=50; i++) {
			double x = (pContext.random() - 0.5);
	    double y = (pContext.random() - 0.5);
	    double z = (pContext.random() - 0.5);
	    
	    vec3 p=new vec3(x,y,z);
	    double distance=sdOctahedron(p,h);
	    
	    if(distance <0.0)
	    {
	    	pVarTP.doHide=false;
	    	pVarTP.x+=pAmount*x;
	    	pVarTP.y+=pAmount*y;
	    	pVarTP.z+=pAmount*z;
	    	return;
	    }
		}
    pVarTP.doHide=true;
  }

  @Override
  public String getName() {
    return "octahedron3D";
  }
  
	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() {
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
	
	@Override
	public void randomize() {
	  h = Math.random() * 0.49 + 0.1;
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	  @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "    float x = (RANDFLOAT() - 0.5);"
	    		+"    float y = (RANDFLOAT() - 0.5);"
	    		+"    float z = (RANDFLOAT() - 0.5);"
	    		+"    "
	    		+"    float3 p=make_float3(x,y,z);"
	    		+"    float distance=octahedron3D_sdOctahedron(p,__octahedron3D_h);"
	    		+"    __doHide=true;"
	    		+"    if(distance <0.0)"
	    		+"    {"
	    		+" 	    __doHide=false;"
	    		+"    	__px=__octahedron3D*x;"
	    		+"    	__py=__octahedron3D*y;"
	    		+"    	__pz=__octahedron3D*z;"
	    		+"    }";
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
		    return   "__device__   float  octahedron3D_sdOctahedron (float3 p, float s)  "
		    		+"  {"
		    		+"      p = abs(p);"
		    		+"      float m = p.x + p.y + p.z - s;"
		    		+"   	float3 q;"
		    		+"           if( 3.0*p.x < m ) "
		    		+"        	   q = make_float3(p.x,p.y,p.z);"
		    		+"      else if( 3.0*p.y < m ) "
		    		+"    	  q = make_float3(p.y,p.z,p.x);"
		    		+"      else if( 3.0*p.z < m ) "
		    		+"    	  q = make_float3(p.z,p.x,p.y);"
		    		+"      else "
		    		+"    	  return m*0.57735027;"
		    		+"      float k = clamp(0.5*(q.z-q.y+s),0.0,s); "
		    		+"      return length(make_float3(q.x,q.y-s+k,q.z-k)); "
		    		+"  }";
	  }
}
