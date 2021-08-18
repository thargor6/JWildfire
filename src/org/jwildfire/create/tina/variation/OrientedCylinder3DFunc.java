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

public class OrientedCylinder3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  
	private static final String PARAM_P1 = "dx";
	private static final String PARAM_P2 = "dy";
	private static final String PARAM_P3 = "dz";
	private static final String PARAM_P4 = "tx";
	private static final String PARAM_P5 = "ty";
	private static final String PARAM_P6 = "tz";
	private static final String PARAM_P7 = "r";



// ocylinder3D with signed distance functions
// Author: Jesus Sosa
// Date:   17/july/ 2020
// Reference: 	https://www.iquilezles.org/www/articles/distfunctions/distfunctions.htm
//              https://www.shadertoy.com/view/Xds3zN
	
  double p1=1.,p2=0.0,p3=0.0,p4=0.,p5=0.,p6=0.0,p7=0.5;;

  private static final String[] additionalParamNames = { PARAM_P1,PARAM_P2,PARAM_P3,PARAM_P4,PARAM_P5,PARAM_P6,PARAM_P7};
  
//// arbitrary orientation
double sdCylinder(vec3 p, vec3 a, vec3 b, double r)
{
    vec3 pa = p.minus(a);
    vec3 ba = b.minus(a);
    double baba = G.dot(ba,ba);
    double paba = G.dot(pa,ba);

    double x = G.length(pa.multiply(baba).minus(ba.multiply(paba)))-( r*baba);
    double y = G.abs(paba-baba*0.5)-baba*0.5;
    double x2 = x*x;
    double y2 = y*y*baba;
    double d = (G.max(x,y)<0.0)?-G.min(x2,y2):(((x>0.0)?x2:0.0)+((y>0.0)?y2:0.0));
    return G.sign(d)*G.sqrt(G.abs(d))/baba;
}	
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = (pContext.random() - 0.5);
    double y = (pContext.random() - 0.5);
    double z = (pContext.random() - 0.5);
    
    vec3 p=new vec3(x,y,z);


    double distance=sdCylinder(p,new vec3(p1,p2,p3),new vec3(p4,p5,p6),p7);
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
    return "ocylinder3D";
  }
  
	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() {
		return new Object[] {p1,p2,p3,p4,p5,p6,p7};
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
	    		+"    float distance=ocylinder3D_sdCylinder(p,make_float3(__ocylinder3D_dx,__ocylinder3D_dy,__ocylinder3D_dz),make_float3(__ocylinder3D_tx,__ocylinder3D_ty,__ocylinder3D_tz),__ocylinder3D_r);"
	    		+"    __doHide=true;"
	    		+"    if(distance <0.0)"
	    		+"    {"
	    		+" 	    __doHide=false;"
	    		+"    	__px=__ocylinder3D*x;"
	    		+"    	__py=__ocylinder3D*y;"
	    		+"    	__pz=__ocylinder3D*z;"
	    		+"    }";
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
		    return   "__device__ float  ocylinder3D_sdCylinder (float3 p, float3 a, float3 b, float r)"
		    		+"{"
		    		+"    float3 pa = p-(a);"
		    		+"    float3 ba = b-(a);"
		    		+"    float baba = dot(ba,ba);"
		    		+"    float paba = dot(pa,ba);"
		    		+"    float x = length(pa*(baba)-(ba*(paba)))-( r*baba);"
		    		+"    float y = fabsf(paba-baba*0.5)-baba*0.5;"
		    		+"    float x2 = x*x;"
		    		+"    float y2 = y*y*baba;"
		    		+"    float d = (fmaxf(x,y)<0.0)?-fminf(x2,y2):(((x>0.0)?x2:0.0)+((y>0.0)?y2:0.0));"
		    		+"    return sign(d)*sqrtf(fabsf(d))/baba;"
		    		+"}	";
	  }
}
