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

public class TriPrism3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  
	private static final String PARAM_P1 = "d";
	private static final String PARAM_P2 = "h";



// triprism3D with signed distance functions
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double p1=0.250,p2=0.5;

  private static final String[] additionalParamNames = { PARAM_P1,PARAM_P2};
  
  
  
  
  double sdTriPrism( vec3 p, vec2 h )
  {
      double k = Math.sqrt(3.0);
      h.x *= 0.5*k;
      p.x = p.x/h.x;
      p.y = p.y/h.x;
      p.x = G.abs(p.x) - 1.0;
      p.y = p.y + 1.0/k;
      if( p.x+k*p.y>0.0 )
      {
    	  vec2 t0=new vec2(p.x-k*p.y,-k*p.x-p.y).division(2.0);
    	  p.x=t0.x;
    	  p.y=t0.y;
      }
      p.x = p.x- G.clamp( p.x, -2.0, 0.0 );
      double d1 = G.length(new vec2(p.x,p.y))*G.sign(-p.y)*h.x;
      double d2 = G.abs(p.z)-h.y;
      return G.length(G.max(new vec2(d1,d2),0.0)) + G.min(G.max(d1,d2), 0.);
  }
//
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = (pContext.random() - 0.5);
    double y = (pContext.random() - 0.5);
    double z = (pContext.random() - 0.5);
    
    vec3 p=new vec3(x,y,z);

    double distance=sdTriPrism(p,new vec2(p1,p2));
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
    return "triprism3D";
  }
  
	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() {
		return new Object[] {p1,p2};
	}
//
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_P1)) {
			p1 =Tools.limitValue(pValue, 0.0, 0.50);
		}
		else if (pName.equalsIgnoreCase(PARAM_P2)) {
			p2 =Tools.limitValue(pValue, 0.0, 0.50);
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
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_BASE_SHAPE};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "   float x = (RANDFLOAT() - 0.5);"
    		+"    float y = (RANDFLOAT() - 0.5);"
    		+"    float z = (RANDFLOAT() - 0.5);"
    		+"    "
    		+"    float3 p=make_float3(x,y,z);"
    		+"    float distance=triprism3D_sdTriPrism(p,make_float2( __triprism3D_d ,__triprism3D_h));"
    		+"    __doHide=true;"
    		+"    if(distance <0.0)"
    		+"    {"
    		+" 	    __doHide=false;"
    		+"    	__px=__triprism3D*x;"
    		+"    	__py=__triprism3D*y;"
    		+"    	__pz=__triprism3D*z;"
    		+"    }";
  }
  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__    float  triprism3D_sdTriPrism ( float3 p, float2 h )"
	    		+"  {"
	    		+"      float k = sqrtf(3.0);"
	    		+"      h.x *= 0.5*k;"
	    		+"      p.x = p.x/h.x;"
	    		+"      p.y = p.y/h.x;"
	    		+"      p.x = fabsf(p.x) - 1.0;"
	    		+"      p.y = p.y + 1.0/k;"
	    		+"      if( p.x+k*p.y>0.0 )"
	    		+"      {"
	    		+"    	  float2 t0=make_float2(p.x-k*p.y,-k*p.x-p.y)/(2.0);"
	    		+"    	  p.x=t0.x;"
	    		+"    	  p.y=t0.y;"
	    		+"      }"
	    		+"      p.x = p.x- clamp( p.x, -2.0, 0.0 );"
	    		+"      float d1 = length(make_float2(p.x,p.y))*sign(-p.y)*h.x;"
	    		+"      float d2 = fabsf(p.z)-h.y;"
	    		+"      return length(fmaxf(make_float2(d1,d2),0.0)) + fminf(fmaxf(d1,d2), 0.);"
	    		+"  }";
  }
}
