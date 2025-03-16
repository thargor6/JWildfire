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

public class Rhombus3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  
	private static final String PARAM_LA = "la";
	private static final String PARAM_LB = "lb";
	private static final String PARAM_H = "h";
	private static final String PARAM_R = "r";

// rhombus3D with signed distance function
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double la=0.50,lb=0.50,h=0.50,r=0.05;


  private static final String[] additionalParamNames = { PARAM_LA,PARAM_LB,PARAM_H,PARAM_R};

  double ndot(  vec2 a, vec2 b )
  { 
	  return a.x*b.x - a.y*b.y; 
  }
  
  // la,lb=semi axis, h=height, ra=corner
  double sdRhombus(vec3 p, double la, double lb, double h, double ra)
  {
      p = G.abs(p);
      vec2 b = new vec2(la,lb);
      double f = G.clamp( (ndot(b,b.minus(new vec2(p.x,p.z).multiply(2.0))))/G.dot(b,b), -1.0, 1.0 );
  	  vec2 q = new vec2(G.length( new vec2(p.x,p.z).minus( b.multiply(new vec2(1.0-f,1.0+f)).multiply(0.5))  )*G.sign(p.x*b.y+p.z*b.x-b.x*b.y)-ra, p.y-h);
      return G.min(G.max(q.x,q.y),0.0) + G.length(G.max(q,0.0));
  }
    
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = (pContext.random() - 0.5);
    double y = (pContext.random() - 0.5);
    double z = (pContext.random() - 0.5);
    
    vec3 p=new vec3(x,y,z);


    double distance=sdRhombus(p,la,lb,h,r);
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
    return "rhombus3D";
  }
  
	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { 
		return new Object[] {la,lb,h,r};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_LA)) {
			la =Tools.limitValue(pValue, 0.0, 0.5);
		}
		else if (pName.equalsIgnoreCase(PARAM_LB)) {
			lb = Tools.limitValue(pValue, 0.0, 0.5);
		}
		else if (pName.equalsIgnoreCase(PARAM_H)) {
			h =Tools.limitValue(pValue, 0.0, 0.5);
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
      la = Math.random() * 0.5;
      lb = Math.random() * 0.5;
      h = Math.random() * 0.5;
      r = Math.random() * 0.25;
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
	    		+"    float distance=rhombus3D_sdRhombus(p,__rhombus3D_la,__rhombus3D_lb,__rhombus3D_h,__rhombus3D_r);"
	    		+"    __doHide=true;"
	    		+"    if(distance <0.0)"
	    		+"    {"
	    		+" 	    __doHide=false;"
	    		+"    	__px=__rhombus3D*x;"
	    		+"    	__py=__rhombus3D*y;"
	    		+"    	__pz=__rhombus3D*z;"
	    		+"    }";
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
		    return   "__device__   float  rhombus3D_ndot (  float2 a, float2 b )"
		    		+"  { "
		    		+"	  return a.x*b.x - a.y*b.y; "
		    		+"  }"
		    		
		    		+"__device__  float  rhombus3D_sdRhombus (float3 p, float la, float lb, float h, float ra)"
		    		+"  {"
		    		+"      p = abs(p);"
		    		+"      float2 b = make_float2(la,lb);"
		    		+"      float f = clamp( ( rhombus3D_ndot (b,b-(make_float2(p.x,p.z)*(2.0))))/dot(b,b), -1.0, 1.0 );"
		    		+"  	float2 q = make_float2(length( make_float2(p.x,p.z)-( b*(make_float2(1.0-f,1.0+f))*(0.5))  )*sign(p.x*b.y+p.z*b.x-b.x*b.y)-ra, p.y-h);"
		    		+"      return fminf(fmaxf(q.x,q.y),0.0) + length(max(q,0.0));"
		    		+"  }";
	  }
}
