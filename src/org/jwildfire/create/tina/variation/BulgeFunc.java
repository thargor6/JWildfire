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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import js.glsl.G;
import js.glsl.vec2;

public class BulgeFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POW = "N";


  private static final String[] paramNames = {PARAM_POW};

  private double N = 2.0;

  
  // Credits: Image Warping- Bulge Effect Algorithm
  // https://stackoverflow.com/questions/5055625/image-warping-bulge-effect-algorithm

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    double r = G.length(new vec2(pAffineTP.x,pAffineTP.y)); //  Math.sqrt(Math.pow(pAffineTP.x-0.5,2.0) +  Math.pow(pAffineTP.y-0.5,2.));
    // double a = G.atan2(pAffineTP.x, pAffineTP.y);
    double rn= Math.pow(r,N); 


    pVarTP.x += pAmount * (rn * (pAffineTP.x )/r);
    pVarTP.y += pAmount * (rn * (pAffineTP.y )/r);

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{N};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POW.equalsIgnoreCase(pName))
      N =  pValue;
    else
    	throw new IllegalArgumentException(pName);
  }

  
  @Override
  public String getName() {
    return "bulge";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "    float r = length(make_float2(__x,__y)); "
	    		+"    float rn= pow(r,__bulge_N); "
	    		+"    __px +=  __bulge * (rn * (__x )/r);"
	    		+"    __py +=  __bulge * (rn * (__y )/r);";
	  }

}
