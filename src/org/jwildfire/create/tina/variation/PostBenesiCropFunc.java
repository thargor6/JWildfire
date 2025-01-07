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
import js.glsl.vec3;

import org.jwildfire.base.Tools;
import  org.jwildfire.base.mathlib.Complex;


public class PostBenesiCropFunc extends VariationFunc {
private static final long serialVersionUID = 1L;

private static final String PARAM_ITERATIONS = "iterations";
private static final String PARAM_BAILOUT = "bailout";
private static final String PARAM_SCALEX = "scaleX";
private static final String PARAM_SCALEY = "scaleY";
private static final String PARAM_SCALEZ = "ScaleZ";
private static final String PARAM_SCOLOR = "color";
    
private static final String[] paramNames = {  PARAM_ITERATIONS, PARAM_BAILOUT,PARAM_SCALEX,PARAM_SCALEY,PARAM_SCALEZ,PARAM_SCOLOR};
private int iterations = 12;
private double bailout = 16.0;
private double scalex = 1.0;
private double scaley = 1.0;
private double scalez = 1.0;
private double pcolor = 1.0;



@Override
public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
// post_mandeltorus_crop by Jesus Sosa

double x=pVarTP.x*scalex;
double y=pVarTP.y*scaley;
double z=pVarTP.z*scalez;

double x1 = x;
double y1 = y;
double z1 = z;

for (int i = 0; i < iterations; i++)
{


	  double r1=y*y+z*z;
	  double newx;
	  if(x1<0.0 || x <Math.sqrt(r1))
	    newx=x*x-r1;
	  else
	    newx=-x*x + r1;
	  r1=-1.0/Math.sqrt(r1)*2.0*Math.abs(x);
	  double newy=r1*(y*y-z*z);
	  double newz=r1*2.0*y*z;
	  x=newx;
	  y=newy;
	  z=newz;
	  
	  x+=x1;
	  y+=y1;
	  z+=z1;
	 
	double r = Math.sqrt(x*x + y*y + z*z);
	
   if (r < bailout)
   {
     pVarTP.x = pAmount * pVarTP.x;
     pVarTP.y = pAmount * pVarTP.y;
     pVarTP.z = pAmount * pVarTP.z;  
     pVarTP.color=Math.sin(r)*pcolor;
   }
   else
   {
     pVarTP.x = pVarTP.y = pVarTP.z = 0;
   }
 }          
}

@Override
public String[] getParameterNames() {
  return paramNames;
}

@Override
public Object[] getParameterValues() {
  return new Object[] {iterations, bailout,scalex,scaley,scalez,pcolor};
}

@Override
public void setParameter(String pName, double pValue) {
  if (PARAM_ITERATIONS.equalsIgnoreCase(pName))
    iterations = limitIntVal(Tools.FTOI(pValue), 0, 250);   
  else if (PARAM_BAILOUT.equalsIgnoreCase(pName))
    bailout = pValue;    
  else if (PARAM_SCALEX.equalsIgnoreCase(pName))
		    scalex = pValue;    
  else if (PARAM_SCALEY.equalsIgnoreCase(pName))
			scaley = pValue;    
  else if (PARAM_SCALEZ.equalsIgnoreCase(pName))
		    scalez = pValue;    
  else if (PARAM_SCOLOR.equalsIgnoreCase(pName))
	    pcolor = limitVal(Tools.FTOI(pValue), 0.0, 1.0); 
  else
    throw new IllegalArgumentException(pName);
}

@Override
public String getName() {
  return "post_benesi_crop";
}

@Override
public void randomize() {
  iterations = (int) (Math.random() * 200 + 1);
  bailout = Math.random() * 25.0 + 1.0;
  scalex = Math.random() * 3.75 + 0.25;
  scaley = Math.random() * 3.75 + 0.25;
  scalez = Math.random() * 3.75 + 0.25;
  pcolor = (int) (Math.random() * 2);
}

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_ESCAPE_TIME_FRACTAL,VariationFuncType.VARTYPE_DC};
  }

}