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

public class Cone3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  
	private static final String PARAM_P1 = "w";
	private static final String PARAM_P2 = "h";
	private static final String PARAM_P3 = "Ty";


// cone3D with signed distance functions
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double p1=0.25,p2=0.25,p3=0.10;

  private static final String[] additionalParamNames = { PARAM_P1,PARAM_P2,PARAM_P3};
  
  
//vertical
double sdCone( vec3 p, vec2 c, double h )
{
   vec2 q = new vec2(c.x,-c.y).multiply(h/c.y);
   vec2 w = new vec2( G.length(new vec2 (p.x,p.z)), p.y );
   
   vec2 a = w.minus(q.multiply(G.clamp( G.dot(w,q)/G.dot(q,q), 0.0, 1.0 )));
   vec2 b = w.minus(q.multiply(new vec2( G.clamp( w.x/q.x, 0.0, 1.0 ), 1.0 )));
   double k = G.sign( q.y );
   double d = G.min(G.dot( a, a ),G.dot(b, b));
   double s = G.max( k*(w.x*q.y-w.y*q.x),k*(w.y-q.y)  );
	return Math.sqrt(d)*G.sign(s);
}
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = (pContext.random() - 0.5);
    double y = (pContext.random() - 0.5);
    double z = (pContext.random() - 0.5);
    
    vec3 p=new vec3(x,y,z);

    double distance=sdCone(p,new vec2(p1,p2),p3); 
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
    return "cone3D";
  }
  
	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() {
		return new Object[] {p1,p2,p3};
	}
//
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_P1)) {
			p1 =Tools.limitValue(pValue, 0.0, 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_P2)) {
			p2 =Tools.limitValue(pValue, 0.0, 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_P3)) {
			p3 =Tools.limitValue(pValue, -1.0, 1.0);
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	  @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "    float x = (RANDFLOAT() - 0.5);"
	    		+"    float y = (RANDFLOAT() - 0.5);"
	    		+"    float z = (RANDFLOAT() - 0.5);"
	    		+"    "
	    		+"    float3 p=make_float3(x,y,z);"
	    		+"    float distance=cone3D_sdCone(p,make_float2( __cone3D_w ,__cone3D_h),__cone3D_Ty); "
	    		+"    __doHide=true;"
	    		+"    if(distance <0.0)"
	    		+"    {"
	    		+" 	    __doHide=false;"
	    		+"    	__px=__cone3D*x;"
	    		+"    	__py=__cone3D*y;"
	    		+"    	__pz=__cone3D*z;"
	    		+"    }";
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
		    return   " __device__ float  cone3D_sdCone ( float3 p, float2 c, float h )"
		    		+"{"
		    		+"   float2 q = make_float2(c.x,-c.y)*(h/c.y);"
		    		+"   float2 w = make_float2( length(make_float2 (p.x,p.z)), p.y );"
		    		+"   "
		    		+"   float2 a = w-(q*(clamp( dot(w,q)/dot(q,q), 0.0, 1.0 )));"
		    		+"   float2 b = w-(q*(make_float2( clamp( w.x/q.x, 0.0, 1.0 ), 1.0 )));"
		    		+"   float k = sign( q.y );"
		    		+"   float d = fminf(dot( a, a ),dot(b, b));"
		    		+"   float s = fmaxf( k*(w.x*q.y-w.y*q.x),k*(w.y-q.y)  );"
		    		+"	return sqrtf(d)*sign(s);"
		    		+"}";
	  }	
}
