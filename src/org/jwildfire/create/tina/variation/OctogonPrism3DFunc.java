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

public class OctogonPrism3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

	private static final String PARAM_R = "R";
	private static final String PARAM_H = "H";


// octogonprism3D with signed distance functions
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double r=0.25,h=0.50;


  private static final String[] additionalParamNames = { PARAM_R,PARAM_H};
	

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		for (int i=1; i<=50; i++) {
			double x = (pContext.random() - 0.5);
	    double y = (pContext.random() - 0.5);
	    double z = (pContext.random() - 0.5);
	    
	    vec3 p=new vec3(x,y,z);
	    double distance=sdOctogonPrism(p,r,h);
	    
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

  double sdOctogonPrism(  vec3 p,  double r, double h )
  {
    vec3 k = new vec3(-0.9238795325,   // sqrt(2+sqrt(2))/2 
                         0.3826834323,   // sqrt(2-sqrt(2))/2
                         0.4142135623 ); // sqrt(2)-1 
    // reflections
    p = G.abs(p);
    vec2 t0=new vec2(p.x,p.y).minus(new vec2( k.x,k.y).multiply(2.0*G.min( G.dot(new vec2( k.x,k.y),new vec2 (p.x,p.y))  ,0.0)));
    p.x = t0.x;
    p.y = t0.y;
    vec2 t1= new vec2(p.x,p.y).minus( new vec2(-k.x,k.y).multiply(2.0*G.min(G.dot(new vec2(-k.x,k.y),new vec2(p.x,p.y)),0.0)));
    p.x = t1.x;
    p.y = t1.y;
    // polygon side
    vec2 t2= new vec2(p.x,p.y).minus(new vec2(G.clamp(p.x, -k.z*r, k.z*r), r));
    p.x = t2.x;
    p.y = t2.y;
    vec2 d = new vec2( G.length(new vec2(p.x,p.y))*G.sign(p.y), p.z-h );
    return G.min(G.max(d.x,d.y),0.0) + G.length(G.max(d,0.0));
  }

  @Override
  public String getName() {
    return "octogonprism3D";
  }
  
	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] {r,h};
	}
//
	public void setParameter(String pName, double pValue) {
		 if (pName.equalsIgnoreCase(PARAM_R)) {
			r =Tools.limitValue(pValue, 0.0, 0.5);
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
      r = Math.random() * 0.5;
      h = Math.random() * 0.5;
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
    		+"    float distance=octogonprism3D_sdOctogonPrism(p,__octogonprism3D_R,__octogonprism3D_H);"
    		+"    __doHide=true;"
    		+"    if(distance <0.0)"
    		+"    {"
    		+" 	    __doHide=false;"
    		+"    	__px=__octogonprism3D*x;"
    		+"    	__py=__octogonprism3D*y;"
    		+"    	__pz=__octogonprism3D*z;"
    		+"    }";
  }
  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__    float  octogonprism3D_sdOctogonPrism (  float3 p,  float r, float h )"
	    		+"  {"
	    		+"    float3 k = make_float3(-0.9238795325,"
	    		+"                         0.3826834323,"
	    		+"                         0.4142135623 );"
	    		+"    "
	    		+"    p = abs(p);"
	    		+"    float2 t0=make_float2(p.x,p.y)-(make_float2( k.x,k.y)*(2.0*fminf( dot(make_float2( k.x,k.y),make_float2 (p.x,p.y))  ,0.0)));"
	    		+"    p.x = t0.x;"
	    		+"    p.y = t0.y;"
	    		+"    float2 t1= make_float2(p.x,p.y)-( make_float2(-k.x,k.y)*(2.0*fminf(dot(make_float2(-k.x,k.y),make_float2(p.x,p.y)),0.0)));"
	    		+"    p.x = t1.x;"
	    		+"    p.y = t1.y;"
	    		+"    "
	    		+"    float2 t2= make_float2(p.x,p.y)-(make_float2(clamp(p.x, -k.z*r, k.z*r), r));"
	    		+"    p.x = t2.x;"
	    		+"    p.y = t2.y;"
	    		+"    float2 d = make_float2( length(make_float2(p.x,p.y))*sign(p.y), p.z-h );"
	    		+"    return fminf(fmaxf(d.x,d.y),0.0) + length(fmaxf(d,0.0));"
	    		+"  }";
  }

}
