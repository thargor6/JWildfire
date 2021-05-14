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


public class PostBulbTorusCropFunc extends VariationFunc {
private static final long serialVersionUID = 1L;

private static final String PARAM_POWER1 = "power1";
private static final String PARAM_POWER2 = "power2";
private static final String PARAM_ITERATIONS = "iterations";
private static final String PARAM_BAILOUT = "bailout";
private static final String PARAM_SCALEX = "scaleX";
private static final String PARAM_SCALEY = "scaleY";
private static final String PARAM_SCALEZ = "ScaleZ";
private static final String PARAM_SCOLOR = "color";
    
private static final String[] paramNames = { PARAM_POWER1, PARAM_POWER2, PARAM_ITERATIONS, PARAM_BAILOUT,PARAM_SCALEX,PARAM_SCALEY,PARAM_SCALEZ,PARAM_SCOLOR};
private double power1 = 9.0;
private double power2 = 9.0;
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

 double r = Math.sqrt(x*x + y*y + z*z);

for (int i = 0; i < iterations; i++)
{

	double rh = Math.sqrt(x * x + y * y);
	double phi = Math.atan2(y, x);
	double phipow = phi * power1;
	double theta = Math.atan2(rh, z);
	double px = x - Math.cos(phi) * 1.5;
	double py = y - Math.sin(phi) * 1.5;
	double rhrad= Math.sqrt(px * px + py * py + z * z);
	double rh1 = Math.pow(rhrad, power2);
	double rh2 = Math.pow(rhrad, power1);
 
	double thetapow = theta * power2; 
	double sintheta = Math.sin(thetapow) * rh2; 

	x = sintheta * Math.cos(phipow);
	y = sintheta * Math.sin(phipow);
	z = Math.cos(thetapow) * rh1;      
	
	x += x1;
	y += y1;
	z += z1; 
	 
	r = Math.sqrt(x*x + y*y + z*z);
	
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
  return new Object[] {power1, power2, iterations, bailout,scalex,scaley,scalez,pcolor};
}

@Override
public void setParameter(String pName, double pValue) {
  if (PARAM_POWER1.equalsIgnoreCase(pName))
    power1 = pValue;
  else if (PARAM_POWER2.equalsIgnoreCase(pName))
    power2 = pValue;    
  else if (PARAM_ITERATIONS.equalsIgnoreCase(pName))
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
  pcolor = pValue;
  else
    throw new IllegalArgumentException(pName);
}

@Override
public String getName() {
  return "post_bulbtorus_crop";
}

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_ESCAPE_TIME_FRACTAL,VariationFuncType.VARTYPE_DC};
  }
}