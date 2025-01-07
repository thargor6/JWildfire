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

import js.glsl.G;
import js.glsl.vec3;

public class Box3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  
	private static final String PARAM_DX = "dx";
	private static final String PARAM_DY = "dy";
	private static final String PARAM_DZ = "dz";

// box3D with signed distance functions
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double dx=0.50,dy=0.50,dz=0.50;


  private static final String[] additionalParamNames = { PARAM_DX,PARAM_DY,PARAM_DZ};
	
  double sdBox( vec3 p, vec3 b )
  {
    vec3 q = G.abs(p).minus(b);
    return G.length(G.max(q,0.0)) + G.min(G.max(q.x,G.max(q.y,q.z)),0.0);
  }  
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = (pContext.random() - 0.5);
    double y = (pContext.random() - 0.5);
    double z = (pContext.random() - 0.5);
    
    vec3 p=new vec3(x,y,z);
    vec3 d=new vec3(dx,dy,dz);
    double distance=sdBox(p,d);
    
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
    return "box3D";
  }
  
	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] {dx,dy,dz};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_DX)) {
			dx =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_DY)) {
			dy = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_DZ)) {
			dz =pValue;
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
      dx = Math.random() * 0.5;
      dy = Math.random() * 0.5;
      dz = Math.random() * 0.5;
    }

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	  @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "    float x = (RANDFLOAT() - 0.5);"
	    		+"    float y = (RANDFLOAT() - 0.5);"
	    		+"    float z = (RANDFLOAT() - 0.5);"
	    		+"    "
	    		+"    float3 p=make_float3(x,y,z);"
	    		+"    float3 d=make_float3( __box3D_dx ,__box3D_dy,__box3D_dz);"
	    		+"    float distance=box3D_sdBox(p,d);"
	    		+"    __doHide=true;"
	    		+"    if(distance <0.0)"
	    		+"    {"
	    		+" 	    __doHide=false;"
	    		+"    	__px=__box3D *x;"
	    		+"    	__py=__box3D *y;"
	    		+"    	__pz=__box3D *z;"
	    		+"    }";
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
		    return   "__device__  float  box3D_sdBox ( float3 p, float3 b )"
		    		+"  {"
		    		+"    float3 q = abs(p)-b;"
		    		+"    return length(max(q,0.0)) + fminf(fmaxf(q.x,fmaxf(q.y,q.z)),0.0);"
		    		+"    return 1.0f;"
		    		+"  }  ";
	  }
}
