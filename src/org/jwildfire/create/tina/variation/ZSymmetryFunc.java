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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import js.glsl.vec2;
import js.glsl.G;

import static org.jwildfire.base.mathlib.MathLib.*;

import org.jwildfire.base.Tools;

public class ZSymmetryFunc extends VariationFunc implements SupportsGPU {
	/*
	 * Variation :  c_symmetry
	 * Date: august 29, 2019
	 * Author: Jesus Sosa
	 * Reference & Credits:  https://www.shadertoy.com/view/MdyBW3
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_P1 = "p1";
	private static final String PARAM_P2 = "p2";

	
	double p1=3.0;
	double p2=3.0;

 	

    
	private static final String[] additionalParamNames = { PARAM_P1,PARAM_P2};

	 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {


		  vec2 uv=new vec2(pAffineTP.x,pAffineTP.y);

		    double t = p1 * atan2(uv.y, uv.x);
		    uv=new vec2(Math.cos(t), Math.sin(t)).multiply(Math.pow(G.length(uv), p2)) ;

		    		
		    pVarTP.x = pAmount * (uv.x);
		    pVarTP.y = pAmount * (uv.y);
		    
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z = pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	   }
	  
	public String getName() {
		return "c_symmetry";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  p1,p2});
	}

	public void setParameter(String pName, double pValue) {

		if(pName.equalsIgnoreCase(PARAM_P1))
		{
			   p1=pValue;
		}
		else if(pName.equalsIgnoreCase(PARAM_P2))
		{
			   p2=pValue;
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "	 float2 uv=make_float2(__x,__y);"
	    		+"	 float t =  __c_symmetry_p1  * atan2(uv.y, uv.x);"
	    		+"	 uv=make_float2(cosf(t), sinf(t))*(powf(length(uv),  __c_symmetry_p2 )) ;"
	    		+"		    		"
	    		+"	  __px = __c_symmetry * (uv.x);"
	    		+"	  __py = __c_symmetry * (uv.y);"
	            + (context.isPreserveZCoordinate() ? "__pz += __c_symmetry * __z;\n" : "");
	  }
}
