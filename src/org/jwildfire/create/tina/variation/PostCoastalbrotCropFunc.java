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
import js.glsl.mat3;
import js.glsl.vec3;

import org.jwildfire.base.Tools;
import  org.jwildfire.base.mathlib.Complex;


public class PostCoastalbrotCropFunc extends VariationFunc {
private static final long serialVersionUID = 1L;

private static final String PARAM_ITERATIONS = "iterations";
private static final String PARAM_BAILOUT = "bailout";
private static final String PARAM_SCALEX = "scaleX";
private static final String PARAM_SCALEY = "scaleY";
private static final String PARAM_SCALEZ = "ScaleZ";
private static final String PARAM_SCOLOR = "color";
private static final String PARAM_ROTX = "alpha";
private static final String PARAM_ROTY = "beta";
private static final String PARAM_ROTZ = "gamma";
    
private static final String[] paramNames = {  PARAM_ITERATIONS, PARAM_BAILOUT,PARAM_SCALEX,PARAM_SCALEY,PARAM_SCALEZ,PARAM_SCOLOR,PARAM_ROTX,PARAM_ROTY,PARAM_ROTZ};
private int iterations = 12;
private double bailout = 16.0;
private double scalex = 1.0;
private double scaley = 1.0;
private double scalez = 1.0;
private double pcolor = 1.0;
private double a = 0.0;
private double b = 0.0;
private double c = 0.0;

vec3 radians(vec3 v1)
{
  return new vec3(v1.x*Math.PI/180.0, v1.y*Math.PI/180.0,v1.z*Math.PI/180.0);
}

mat3 multiply (mat3 A, mat3 B) 
{  
	return new mat3(A.a00*B.a00+A.a01*B.a10+A.a02*B.a20,
			    A.a00*B.a01+A.a01*B.a11+A.a02*B.a21,
			    A.a00*B.a02+A.a01*B.a12+A.a02*B.a22,
			    A.a10*B.a00+A.a11*B.a10+A.a12*B.a20,
			    A.a10*B.a01+A.a11*B.a11+A.a12*B.a21,
			    A.a10*B.a02+A.a11*B.a12+A.a12*B.a22,
			    A.a20*B.a00+A.a21*B.a10+A.a22*B.a20,
			    A.a20*B.a01+A.a21*B.a11+A.a22*B.a21,
			    A.a20*B.a02+A.a21*B.a12+A.a22*B.a22); 
}

mat3 euler_rotation(vec3 rot)
{
    vec3 v1=new vec3(-rot.x,-rot.y,-rot.z);
    vec3 v2=radians(v1);
    vec3 rot_rad = new vec3(v2.x,v2.y,v2.z);

    double cosx = Math.cos(rot_rad.x);
    double sinx = Math.sin(rot_rad.x);
    mat3 x = new mat3(1.,            0.,              0.,        
                      0.,            cosx,           -sinx,   
                      0.,            sinx,          cosx           
                      );

    double cosy = Math.cos(rot_rad.y);
    double siny = Math.sin(rot_rad.y);
    mat3 y = new mat3(cosy,         0.,              siny,           
                      0.,            1.,              0.,            
                      -siny,        0.,              cosy
                 );

    double cosz = Math.cos(rot_rad.z);
    double sinz = Math.sin(rot_rad.z);
    mat3 z = new mat3(cosz,         -sinz,          0.,              
                      sinz,         cosz,           0.,        
                      0.,            0.,              1.
                );

    mat3 xform =new mat3(1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0);

    xform = multiply(xform,x);
    xform = multiply(xform,y);
    xform = multiply(xform,z);

    return xform;
}


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
vec3 rotangles=new vec3(a,b,c);

mat3 rot=euler_rotation(rotangles);
for (int i = 0; i < iterations; i++)
{
	double temp ;
	temp = Math.pow(r, 7.7);
	temp = temp* r;

	x = Math.sin(Math.sin(Math.sin(Math.PI / 3 + x * Math.PI)));
	y = Math.sin(Math.sin(Math.sin(Math.PI / 3 + y * Math.PI)));
	z = Math.sin(Math.sin(Math.sin(Math.PI / 3 + z * Math.PI)));

	z = z*temp;
	
	x+=x1;
	y+=y1;
	z+=z1;

	vec3 zz=new vec3(x,y,z);
	zz=zz.times(rot);

	r = Math.sqrt(zz.x*zz.x + zz.y*zz.y + zz.z*zz.z);	
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
  return new Object[] {iterations, bailout,scalex,scaley,scalez,pcolor,a,b,c};
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
  else if (PARAM_ROTX.equalsIgnoreCase(pName))
	    a = pValue;    
else if (PARAM_ROTY.equalsIgnoreCase(pName))
		b = pValue;    
else if (PARAM_ROTZ.equalsIgnoreCase(pName))
	    c = pValue;   
  else
    throw new IllegalArgumentException(pName);
}

@Override
public String getName() {
  return "post_coastalbrot_crop";
}

@Override
public void randomize() {
  iterations = (int) (Math.random() * 19 + 1);
  bailout = Math.random() * 25.0 + 1.0;
  scalex = Math.random() * 3.75 + 0.25;
  scaley = Math.random() * 3.75 + 0.25;
  scalez = Math.random() * 3.75 + 0.25;
  pcolor = (int) (Math.random() * 2);
  a = Math.random() * 360.0 - 180.0;
  b = Math.random() * 360.0 - 180.0;
  c = Math.random() * 360.0 - 180.0;
}

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_ESCAPE_TIME_FRACTAL,VariationFuncType.VARTYPE_DC};
  }
}