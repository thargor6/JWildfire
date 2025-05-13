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

public class Cylinder3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  
	private static final String PARAM_D = "d";
	private static final String PARAM_H = "H";


// cylinder3D with signed distance functions
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double d=0.30,h=0.250;


  private static final String[] additionalParamNames = { PARAM_D,PARAM_H};
	
  
  // vertical
  double sdCylinder( vec3 p, vec2 h )
  {
      vec2 d = G.abs(new vec2(G.length(new vec2(p.x,p.z)),p.y)).minus(h);
      return G.min(G.max(d.x,d.y),0.0) + G.length(G.max(d,0.0));
  }
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		for (int i=1; i<=50; i++) {
			double x = (pContext.random() - 0.5);
	    double y = (pContext.random() - 0.5);
	    double z = (pContext.random() - 0.5);
	    
	    vec3 p=new vec3(x,y,z);
	    double distance=sdCylinder(p,new vec2(d,h));
	    
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
    return "cylinder3D";
  }
  
	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] {d,h};
	}
//
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_D)) {
			d =Tools.limitValue(pValue, 0.0, 0.5);
		}
		else if (pName.equalsIgnoreCase(PARAM_H)) {
			h =Tools.limitValue(pValue, 0.0, 0.5);
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
	  d = Math.random() * 0.5;
	  h = Math.random() * 0.5;
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	  @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "    float x = (RANDFLOAT() - 0.5);"
	    		+"    float y = (RANDFLOAT() - 0.5);"
	    		+"    float z = (RANDFLOAT() - 0.5);"
	    		+"    "
	    		+"    float3 p=make_float3(x,y,z);"
	    		+"    float distance=cylinder3D_sdCylinder(p,make_float2(__cylinder3D_d,__cylinder3D_H)); "
	    		+"    __doHide=true;"
	    		+"    if(distance <0.0)"
	    		+"    {"
	    		+" 	    __doHide=false;"
	    		+"    	__px=__cylinder3D*x;"
	    		+"    	__py=__cylinder3D*y;"
	    		+"    	__pz=__cylinder3D*z;"
	    		+"    }";
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
		    return   "__device__   float  cylinder3D_sdCylinder ( float3 p, float2 h )"
		    		+"  {"
		    		+"      float2 d = abs(make_float2(length(make_float2(p.x,p.z)),p.y))-(h);"
		    		+"      return fminf(fmaxf(d.x,d.y),0.0) + length(max(d,0.0));"
		    		+"  }";
	  }
}
