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

public class OrientedRoundCone3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  
	private static final String PARAM_P1 = "dx";
	private static final String PARAM_P2 = "dy";
	private static final String PARAM_P3 = "dz";
	private static final String PARAM_P4 = "tx";
	private static final String PARAM_P5 = "ty";
	private static final String PARAM_P6 = "tz";
	private static final String PARAM_P7 = "r1";
	private static final String PARAM_P8 = "r2";



// oroundcone3D with signed distance functions
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double p1=0.1,p2=0.0,p3=0.0,p4=-0.1,p5=0.35,p6=0.1,p7=0.15,p8=0.05;

  private static final String[] additionalParamNames = { PARAM_P1,PARAM_P2,PARAM_P3,PARAM_P4,PARAM_P5,PARAM_P6,PARAM_P7,PARAM_P8};
  
  
  double dot2(  vec3 v )
  { 
	  return G.dot(v,v);
  }
  
  double sdRoundCone(vec3 p, vec3 a, vec3 b, double r1, double r2)
  {
      // sampling independent computations (only depend on shape)
      vec3  ba = b.minus( a);
      double l2 = G.dot(ba,ba);
      double rr = r1 - r2;
      double a2 = l2 - rr*rr;
      double il2 = 1.0/l2;
      
      // sampling dependant computations
      vec3 pa = p.minus(a);
      double y = G.dot(pa,ba);
      double z = y - l2;
      double x2 = dot2( pa.multiply(l2).minus( ba.multiply(y) ));
      double y2 = y*y*l2;
      double z2 = z*z*l2;

      // single square root!
      double k = G.sign(rr)*rr*rr*x2;
      if( G.sign(z)*a2*z2 > k )
    	  return  Math.sqrt(x2 + z2)        *il2 - r2;
      if( G.sign(y)*a2*y2 < k )
    	  return  Math.sqrt(x2 + y2)        *il2 - r1;
      return (Math.sqrt(x2*a2*il2)+y*rr)*il2 - r1;
  }
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		for (int i=1; i<=50; i++) {
			double x = (pContext.random() - 0.5);
	    double y = (pContext.random() - 0.5);
	    double z = (pContext.random() - 0.5);
	    
	    vec3 p=new vec3(x,y,z);
	    double distance=sdRoundCone(p,new vec3(p1,p2,p3),new vec3(p4,p5,p6),p7,p8);
	    
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
    return "oroundcone3D";
  }
  
	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() {
		return new Object[] {p1,p2,p3,p4,p5,p6,p7,p8};
	}
//
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_P1)) {
			p1 =Tools.limitValue(pValue, -1.0, 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_P2)) {
			p2 =Tools.limitValue(pValue, -1.0, 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_P3)) {
			p3 =Tools.limitValue(pValue, -1.0, 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_P4)) {
			p4 =Tools.limitValue(pValue, -.50, .50);
		}
		else if (pName.equalsIgnoreCase(PARAM_P5)) {
			p5 =Tools.limitValue(pValue, -.50, 0.50);
		}
		else if (pName.equalsIgnoreCase(PARAM_P6)) {
			p6 =Tools.limitValue(pValue, -0.50, 0.50);
		}
		else if (pName.equalsIgnoreCase(PARAM_P7)) {
			p7 =Tools.limitValue(pValue, 0.0, 0.5);
		}
		else if (pName.equalsIgnoreCase(PARAM_P8)) {
			p8 =Tools.limitValue(pValue, 0.0, 0.5);
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
      p1 = Math.random() - 0.5;
      p2 = Math.random() - 0.5;
      p3 = Math.random() - 0.5;
      p4 = Math.random() - 0.5;
      p5 = Math.random() - 0.5;
      p6 = Math.random() - 0.5;
      p7 = Math.random() * 0.25;
      p8 = Math.random() * 0.25;
    }

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}

	  @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "    float x = (RANDFLOAT() - 0.5);"
	    		+"    float y = (RANDFLOAT() - 0.5);"
	    		+"    float z = (RANDFLOAT() - 0.5);"
	    		+"    "
	    		+"    float3 p=make_float3(x,y,z);"
	    		+"    float distance=oroundcone3D_sdRoundCone(p,make_float3( __oroundcone3D_dx ,__oroundcone3D_dy,__oroundcone3D_dz),make_float3( __oroundcone3D_tx ,__oroundcone3D_ty,__oroundcone3D_tz),__oroundcone3D_r1,__oroundcone3D_r2);"
	    		+"    __doHide=true;"
	    		+"    if(distance <0.0)"
	    		+"    {"
	    		+" 	    __doHide=false;"
	    		+"    	__px=__oroundcone3D*x;"
	    		+"    	__py=__oroundcone3D*y;"
	    		+"    	__pz=__oroundcone3D*z;"
	    		+"    }";
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
		    return   "__device__   float  oroundcone3D_dot2 (  float3 v )"
		    		+"  { "
		    		+"	  return dot(v,v);"
		    		+"  }"

		    		+" __device__ float  oroundcone3D_sdRoundCone (float3 p, float3 a, float3 b, float r1, float r2)"
		    		+"  {"
		    		+"      "
		    		+"      float3  ba = b-( a);"
		    		+"      float l2 = dot(ba,ba);"
		    		+"      float rr = r1 - r2;"
		    		+"      float a2 = l2 - rr*rr;"
		    		+"      float il2 = 1.0/l2;"
		    		+"      "
		    		+"      "
		    		+"      float3 pa = p-(a);"
		    		+"      float y = dot(pa,ba);"
		    		+"      float z = y - l2;"
		    		+"      float x2 =  oroundcone3D_dot2 ( pa*(l2)-( ba*(y) ));"
		    		+"      float y2 = y*y*l2;"
		    		+"      float z2 = z*z*l2;"
		    		+"      "
		    		+"      float k = sign(rr)*rr*rr*x2;"
		    		+"      if( sign(z)*a2*z2 > k )"
		    		+"    	  return  sqrtf(x2 + z2)        *il2 - r2;"
		    		+"      if( sign(y)*a2*y2 < k )"
		    		+"    	  return  sqrtf(x2 + y2)        *il2 - r1;"
		    		+"      return (sqrtf(x2*a2*il2)+y*rr)*il2 - r1;"
		    		+"  }";
	  }	  
}
