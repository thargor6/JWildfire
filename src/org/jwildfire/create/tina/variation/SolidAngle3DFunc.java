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

public class SolidAngle3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  
	private static final String PARAM_P1 = "p1";
	private static final String PARAM_P2 = "p2";
	private static final String PARAM_P3 = "p3";


// solidangle3D with signed distance functions
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double p1=0.250,p2=0.5,p3=0.5;

  private static final String[] additionalParamNames = { PARAM_P1,PARAM_P2,PARAM_P3};
  

  // c is the sin/cos of the desired cone angle
  double sdSolidAngle(vec3 pos, vec2 c, double ra)
  {
      vec2 p = new vec2( G.length(new vec2(pos.x,pos.z)), pos.y );
      double l = G.length(p) - ra;
  	double m = G.length(p.minus( c.multiply(G.clamp(G.dot(p,c),0.0,ra)) ));
      return G.max(l,m*G.sign(c.y*p.x-c.x*p.y));
  }

  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = (pContext.random() - 0.5);
    double y = (pContext.random() - 0.5);
    double z = (pContext.random() - 0.5);
    
    vec3 p=new vec3(x,y,z);

    double distance=sdSolidAngle(p,new vec2(p1,p2),p3);
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
    return "solidangle3D";
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
			p1 =Tools.limitValue(pValue, 0.0, 0.50);
		}
		else if (pName.equalsIgnoreCase(PARAM_P2)) {
			p2 =Tools.limitValue(pValue, 0.0, 0.50);
		}
		else if (pName.equalsIgnoreCase(PARAM_P3)) {
			p3 =Tools.limitValue(pValue, 0.0, 0.50);
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
      p1 = Math.random() * 0.5;
      p2 = Math.random() * 0.5;
      p3 = Math.random() * 0.5;
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
	    		+"    float distance=solidangle3D_sdSolidAngle(p,make_float2( __solidangle3D_p1 ,__solidangle3D_p2),__solidangle3D_p3);"
	    		+"    __doHide=true;"
	    		+"    if(distance <0.0)"
	    		+"    {"
	    		+" 	    __doHide=false;"
	    		+"    	__px=__solidangle3D*x;"
	    		+"    	__py=__solidangle3D*y;"
	    		+"    	__pz=__solidangle3D*z;"
	    		+"    }";
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
		    return   "__device__   float  solidangle3D_sdSolidAngle (float3 pos, float2 c, float ra)"
		    		+"  {"
		    		+"      float2 p = make_float2( length(make_float2(pos.x,pos.z)), pos.y );"
		    		+"      float l = length(p) - ra;"
		    		+"  	float m = length(p-( c*(clamp(dot(p,c),0.0,ra)) ));"
		    		+"      return max(l,m*sign(c.y*p.x-c.x*p.y));"
		    		+"  }	";
	  }
}
