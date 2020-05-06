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

import org.jwildfire.base.mathlib.Complex;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.M_2_PI;

public class CropTriangleFunc extends VariationFunc {
	
	  /*
	   * Variation : crop_triangle
	   *
	   * Autor: Jesus Sosa
	   * Date: March 14, 2020
	   * Reference https://www.geeksforgeeks.org/check-whether-a-given-point-lies-inside-a-triangle-or-not/
	   */	

	
  private static final long serialVersionUID = 1L;
  
	private static final String PARAM_X1 = "x1";
	private static final String PARAM_Y1 = "y1";

	private static final String PARAM_X2 = "x2";
	private static final String PARAM_Y2 = "y2";

	private static final String PARAM_X3 = "x3";
	private static final String PARAM_Y3 = "y3";

	

 //   double x1=-0.20,y1=-0.10,x2=0.20,y2=-0.10,x3=0.20,y3=0.10;
    double x1=0.00,y1=0.50,x2=0.50,y2=0.50,x3=0.00,y3=-0.50;


	private static final String[] additionalParamNames = { PARAM_X1,PARAM_Y1,PARAM_X2,PARAM_Y2,PARAM_X3,PARAM_Y3};

	
	double TriangleArea(double x1, double y1, double x2,double y2, double x3,double y3)
	{
		return Math.abs((x1*(y2-y3)+x2*(y3-y1)+x3*(y1-y2))/2.0);
	}
	
	boolean isInsideTriangle(double x1,double y1,double x2,double y2, double x3,double y3, double x , double y)
	{
		double a =TriangleArea(x1,y1,x2,y2,x3,y3);
		double a1=TriangleArea(x,y,x2,y2,x3,y3);
		double a2=TriangleArea(x1,y1,x,y,x3,y3);
		double a3=TriangleArea(x1,y1,x2,y2,x,y);
		return (a==a1+a2+a3);
	}
	
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
      // point inside a triangle: https://www.geeksforgeeks.org/check-whether-a-given-point-lies-inside-a-triangle-or-not/
   double x=pAffineTP.x;
   double y=pAffineTP.y;
   
   pVarTP.doHide=true;
   if(isInsideTriangle(x1,y1,x2,y2,x3,y3,x,y))
	    pVarTP.doHide=false;
   
   pVarTP.x += pAmount * x;
   pVarTP.y += pAmount * y;

  }

  @Override
  public String getName() {
    return "crop_triangle";
  }

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { x1,y1,x2,y2,x3,y3};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_X1)) {
			x1 =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_Y1)) {
			y1 = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_X2)) {
			x2 =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_Y2)) {
			y2 = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_X3)) {
			x3 =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_Y3)) {
			y3 = pValue;
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
}
