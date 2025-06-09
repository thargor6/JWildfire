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

import js.glsl.G;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class OrientedCappedCone3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  
	private static final String PARAM_P1 = "dx";
	private static final String PARAM_P2 = "dy";
	private static final String PARAM_P3 = "dz";
	private static final String PARAM_P4 = "tx";
	private static final String PARAM_P5 = "ty";
	private static final String PARAM_P6 = "tz";
	private static final String PARAM_P7 = "r1";
	private static final String PARAM_P8 = "r2";


// ocappedcone3D with signed distance functions
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double p1=0.0,p2=0.0,p3=0.1,p4=0.0,p5=0.0,p6=0.50,p7=0.25,p8=0.05;

  private static final String[] additionalParamNames = { PARAM_P1,PARAM_P2,PARAM_P3,PARAM_P4,PARAM_P5,PARAM_P6,PARAM_P7,PARAM_P8};
  
  double sdCappedCone(vec3 p, vec3 a, vec3 b, double ra, double rb)
  {
      double rba  = rb-ra;
      double baba = G.dot(b.minus(a),b.minus(a));
      double papa = G.dot(p.minus(a),p.minus(a));
      double paba = G.dot(p.minus(a),b.minus(a))/baba;

      double x = Math.sqrt( papa - paba*paba*baba );

      double cax = G.max(0.0,x-((paba<0.5)?ra:rb));
      double cay = G.abs(paba-0.5)-0.5;

      double k = rba*rba + baba;
      double f = G.clamp( (rba*(x-ra)+paba*baba)/k, 0.0, 1.0 );

      double cbx = x-ra - f*rba;
      double cby = paba - f;
      
      double s = (cbx < 0.0 && cay < 0.0) ? -1.0 : 1.0;
      
      return s*Math.sqrt( G.min(cax*cax + cay*cay*baba,
                         cbx*cbx + cby*cby*baba) );
  }
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		for (int i=1; i<=50; i++) {
			double x = (pContext.random() - 0.5);
	    double y = (pContext.random() - 0.5);
	    double z = (pContext.random() - 0.5);
	    
	    vec3 p=new vec3(x,y,z);
	    double distance=sdCappedCone(p,new vec3(p1,p2,p3),new vec3(p4,p5,p6),p7,p8);
	    
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
    return "ocappedcone3D";
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
			p4 =Tools.limitValue(pValue, -1.0, 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_P5)) {
			p5 =Tools.limitValue(pValue, -1.0, 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_P6)) {
			p6 =Tools.limitValue(pValue, -1.0, 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_P7)) {
			p7 =Tools.limitValue(pValue, 0.0, 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_P8)) {
			p8 =Tools.limitValue(pValue, 0.0, 1.0);
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
      p7 = Math.random() * 0.5;
      p8 = Math.random() * 0.5;
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
	    		+"    float distance = ocappedcone3D_sdCappedCone(p,make_float3( __ocappedcone3D_dx ,__ocappedcone3D_dy,__ocappedcone3D_dz),make_float3( __ocappedcone3D_tx ,__ocappedcone3D_ty,__ocappedcone3D_tz),__ocappedcone3D_r1,__ocappedcone3D_r2);"
	    		+"  "
	    		+"    __doHide=true;"
	    		+"    if(distance <0.0)"
	    		+"    {"
	    		+" 	    __doHide=false;"
	    		+"    	__px=__ocappedcone3D*x;"
	    		+"    	__py=__ocappedcone3D*y;"
	    		+"    	__pz=__ocappedcone3D*z;"
	    		+"    }";
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
		    return   " __device__   float  ocappedcone3D_sdCappedCone (float3 p, float3 a, float3 b, float ra, float rb)"
		    		+"  {"
		    		+"      float rba  = rb-ra;"
		    		+"      float baba = dot(b-(a),b-(a));"
		    		+"      float papa = dot(p-(a),p-(a));"
		    		+"      float paba = dot(p-(a),b-(a))/baba;"
		    		+"      float x = sqrtf( papa - paba*paba*baba );"
		    		+"      float cax = fmaxf(0.0,x-((paba<0.5)?ra:rb));"
		    		+"      float cay = fabsf(paba-0.5)-0.5;"
		    		+"      float k = rba*rba + baba;"
		    		+"      float f = clamp( (rba*(x-ra)+paba*baba)/k, 0.0, 1.0 );"
		    		+"      float cbx = x-ra - f*rba;"
		    		+"      float cby = paba - f;"
		    		+"      "
		    		+"      float s = (cbx < 0.0 && cay < 0.0) ? -1.0 : 1.0;"
		    		+"      "
		    		+"      return s*sqrtf( fminf(cax*cax + cay*cay*baba,"
		    		+"                         cbx*cbx + cby*cby*baba) );"
		    		+"  }";
	  }	
}
