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

public class BoundingBox3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  
	private static final String PARAM_DX = "dx";
	private static final String PARAM_DY = "dy";
	private static final String PARAM_DZ = "dz";
	private static final String PARAM_R = "r";

// bbox3D with signed distance functions
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double dx=0.30,dy=0.25,dz=0.20,r=0.05;


  private static final String[] additionalParamNames = { PARAM_DX,PARAM_DY,PARAM_DZ,PARAM_R};
  
  double sdBoundingBox( vec3 p, vec3 b, double e )
  {
         p = G.abs(p  ).minus(b);
    vec3 q = G.abs(p.plus(e)).minus(e);
    return G.min(G.min(
        G.length(G.max(new vec3(p.x,q.y,q.z),0.0))+G.min(G.max(p.x,G.max(q.y,q.z)),0.0),
        G.length(G.max(new vec3(q.x,p.y,q.z),0.0))+G.min(G.max(q.x,G.max(p.y,q.z)),0.0)),
        G.length(G.max(new vec3(q.x,q.y,p.z),0.0))+G.min(G.max(q.x,G.max(q.y,p.z)),0.0));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		for (int i=1; i<=50; i++) {
			double x = (pContext.random() - 0.5);
	    double y = (pContext.random() - 0.5);
	    double z = (pContext.random() - 0.5);
	    
	    vec3 p=new vec3(x,y,z);
	    vec3 d=new vec3(dx,dy,dz);
	    double distance=sdBoundingBox(p,d,r);
	    
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
    return "bbox3D";
  }
  
	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] {dx,dy,dz,r};
	}
//
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_DX)) {
			dx =Tools.limitValue(pValue, 0.0, 0.5);
		}
		else if (pName.equalsIgnoreCase(PARAM_DY)) {
			dy =Tools.limitValue(pValue, 0.0, 0.5);
		}
		else if (pName.equalsIgnoreCase(PARAM_DZ)) {
			dz =Tools.limitValue(pValue, 0.0, 0.5);
		}
		else if (pName.equalsIgnoreCase(PARAM_R)) {
			r =Tools.limitValue(pValue, 0.0, 0.25);
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
      r = Math.random() * 0.25;
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
	    		+"    float3 d=make_float3( __bbox3D_dx ,__bbox3D_dy,__bbox3D_dz);"
	    		+"    float distance=bbox3D_sdBoundingBox(p,d,__bbox3D_r);"
	    		+"    __doHide=true;"
	    		+"    if(distance <0.0)"
	    		+"    {"
	    		+" 	    __doHide=false;"
	    		+"    	__px=__bbox3D*x;"
	    		+"    	__py=__bbox3D*y;"
	    		+"    	__pz=__bbox3D*z;"
	    		+"    }";
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
		    return   "__device__    float  bbox3D_sdBoundingBox ( float3 p, float3 b, float e )"
		    		+"  {"
		    		+"    p = abs(p  )-b;"
		    		+"    float3 q = abs(p+e)-e;"
		    		+"    return fminf(fminf("
		    		+"        length(max(make_float3(p.x,q.y,q.z),0.0))+fminf(fmaxf(p.x,fmaxf(q.y,q.z)),0.0),"
		    		+"        length(max(make_float3(q.x,p.y,q.z),0.0))+fminf(fmaxf(q.x,fmaxf(p.y,q.z)),0.0)),"
		    		+"        length(max(make_float3(q.x,q.y,p.z),0.0))+fminf(fmaxf(q.x,fmaxf(q.y,p.z)),0.0));"
		    		+"  }";
	  }
}
