/*
Post  JWildfire - an image and animation processor written in Java 
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

import org.jwildfire.base.mathlib.Complex;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.M_2_PI;

public class TriangleFunc extends VariationFunc implements SupportsGPU {
	
	  /*
	   * Variation : triangle
	   *
	   * Autor: Jesus Sosa
	   * Date: March 14, 2020
	   * Reference http://math.stackexchange.com/questions/18686/uniform-random-point-in-triangle
	   */	
	
  private static final long serialVersionUID = 1L;
  
	private static final String PARAM_X1 = "x1";
	private static final String PARAM_Y1 = "y1";
	private static final String PARAM_Z1 = "z1";
	private static final String PARAM_X2 = "x2";
	private static final String PARAM_Y2 = "y2";
	private static final String PARAM_Z2 = "z2";
	private static final String PARAM_X3 = "x3";
	private static final String PARAM_Y3 = "y3";
	private static final String PARAM_Z3 = "z3";
	


    double x1=-0.20,y1=-0.10,z1=0.0,x2=0.20,y2=-0.10,z2=0.0,x3=0.20,y3=0.10,z3=0.0;


	private static final String[] additionalParamNames = { PARAM_X1,PARAM_Y1,PARAM_Z1,PARAM_X2,PARAM_Y2,PARAM_Z2,PARAM_X3,PARAM_Y3,PARAM_Z3};

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
      // uniform sampling:  http://math.stackexchange.com/questions/18686/uniform-random-point-in-triangle
      double sqrt_r1 = MathLib.sqrt(pContext.random());
      double r2 = pContext.random();
      double a = 1.0 - sqrt_r1;
      double b = sqrt_r1 * (1.0 - r2);
      double c = r2 * sqrt_r1;
      double dx = a * x1 + b * x2 + c * x3;
      double dy = a * y1 + b * y2 + c * y3;
      double dz = a * z1 + b * z2 + c * z3;

      pVarTP.x = pAmount * dx;
      pVarTP.y = pAmount * dy;
      pVarTP.z = pAmount * dz;
  }

  @Override
  public String getName() {
    return "triangle";
  }

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { x1,y1,z1,x2,y2,z2,x3,y3,z3};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_X1)) {
			x1 =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_Y1)) {
			y1 = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_Z1)) {
			   z1 =  pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_X2)) {
			x2 =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_Y2)) {
			y2 = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_Z2)) {
			   z2 =  pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_X3)) {
			x3 =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_Y3)) {
			y3 = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_Z3)) {
			   z3 =  pValue;
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
	    return   "float sqrt_r1 = sqrtf(RANDFLOAT());"
	    		+"float r2 = RANDFLOAT();"
	    		+"float a = 1.0 - sqrt_r1;"
	    		+"float b = sqrt_r1 * (1.0 - r2);"
	    		+"float c = r2 * sqrt_r1;"
	    		+"float dx = a * varpar->triangle_x1 + b * varpar->triangle_x2 + c * varpar->triangle_x3;"
	    		+"float dy = a * varpar->triangle_y1 + b * varpar->triangle_y2 + c * varpar->triangle_y3;"
	    		+"float dz = a * varpar->triangle_z1 + b * varpar->triangle_z2 + c * varpar->triangle_z3;"
	    		+"__px = __triangle * dx;"
	    		+"__py = __triangle * dy;"
	    		+"__pz = __triangle * dz;";
	  }	
}
