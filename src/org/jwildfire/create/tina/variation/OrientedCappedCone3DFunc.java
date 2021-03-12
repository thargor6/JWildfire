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

public class OrientedCappedCone3DFunc extends VariationFunc {
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
    double x = (pContext.random() - 0.5);
    double y = (pContext.random() - 0.5);
    double z = (pContext.random() - 0.5);
    
    vec3 p=new vec3(x,y,z);

    double distance=sdCappedCone(p,new vec3(p1,p2,p3),new vec3(p4,p5,p6),p7,p8);
  
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
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE};
	}

}
