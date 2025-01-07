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

public class HexPrism3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  
	private static final String PARAM_D = "d";
	private static final String PARAM_H = "H";


// hexprism3D with signed distance functions
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double d=0.30,h=0.250;


  private static final String[] additionalParamNames = { PARAM_D,PARAM_H};
	
  
  double sdHexPrism( vec3 p, vec2 h )
  {
      vec3 q = G.abs(p);

      vec3 k = new vec3(-0.8660254, 0.5, 0.57735);
      p = G.abs(p);
      vec2 t0=new vec2(p.x,p.y);
      t0 = t0.minus(new vec2(k.x,k.y).multiply(2.*G.min(G.dot(new vec2(k.x,k.y), new vec2(p.x,p.y)), 0.0)));
      p.x=t0.x;
      p.y=t0.y;
      vec2 d = new vec2(
         G.length(new vec2(p.x,p.y).minus(new  vec2(G.clamp(p.x, -k.z*h.x, k.z*h.x), h.x)))*G.sign(p.y - h.x),
         p.z-h.y );
      return G.min(G.max(d.x,d.y),0.0) + G.length(G.max(d,0.0));
  }
  
   @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = (pContext.random() - 0.5);
    double y = (pContext.random() - 0.5);
    double z = (pContext.random() - 0.5);
    
    vec3 p=new vec3(x,y,z);
    
    double distance=sdHexPrism(p,new vec2(d,h)); 
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
    return "hexprism3D";
  }
  
	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { 
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
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "    float x = (RANDFLOAT() - 0.5);"
    		+"    float y = (RANDFLOAT() - 0.5);"
    		+"    float z = (RANDFLOAT() - 0.5);"
    		+"    "
    		+"    float3 p=make_float3(x,y,z);"
    		+"    "
    		+"    float distance=hexprism3D_sdHexPrism(p,make_float2(__hexprism3D_d,__hexprism3D_H)); "
    		+"    __doHide=true;"
    		+"    if(distance <0.0)"
    		+"    {"
    		+" 	    __doHide=false;"
    		+"    	__px=__hexprism3D*x;"
    		+"    	__py=__hexprism3D*y;"
    		+"    	__pz=__hexprism3D*z;"
    		+"    }";
  }
  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__   float  hexprism3D_sdHexPrism ( float3 p, float2 h )"
	    		+"  {"
	    		+"      float3 q = abs(p);"
	    		+"      float3 k = make_float3(-0.8660254, 0.5, 0.57735);"
	    		+"      p = abs(p);"
	    		+"      float2 t0=make_float2(p.x,p.y);"
	    		+"      t0 = t0-(make_float2(k.x,k.y)*(2.*fminf(dot(make_float2(k.x,k.y), make_float2(p.x,p.y)), 0.0)));"
	    		+"      p.x=t0.x;"
	    		+"      p.y=t0.y;"
	    		+"      float2 d = make_float2("
	    		+"         length(make_float2(p.x,p.y)-( make_float2(clamp(p.x, -k.z*h.x, k.z*h.x), h.x)))*sign(p.y - h.x),"
	    		+"         p.z-h.y );"
	    		+"      return fminf(fmaxf(d.x,d.y),0.0) + length(max(d,0.0));"
	    		+"  }";
  }
}
