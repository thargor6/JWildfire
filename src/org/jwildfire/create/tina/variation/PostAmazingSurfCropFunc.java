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

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import js.glsl.G;
import js.glsl.mat3;
import js.glsl.vec3;

import org.jwildfire.base.Tools;


public class PostAmazingSurfCropFunc extends VariationFunc {
private static final long serialVersionUID = 1L;

private static final String PARAM_ITERATIONS = "iterations";
private static final String PARAM_BAILOUT = "bailout";
private static final String PARAM_SCALEX = "scaleX";
private static final String PARAM_SCALEY = "scaleY";
private static final String PARAM_SCALEZ = "scaleZ";
private static final String PARAM_SCOLOR = "color";
private static final String PARAM_MODE = "mode";
private static final String PARAM_ANGLE = "angle";
private static final String PARAM_ROTVX = "rot_x";
private static final String PARAM_ROTVY = "rot_y";
private static final String PARAM_ROTVZ = "rot_z";
private static final String PARAM_MINR = "minR";
private static final String PARAM_MAXR = "maxR";

private static final String[] paramNames = {  PARAM_ITERATIONS, PARAM_BAILOUT,PARAM_SCALEX,PARAM_SCALEY,PARAM_SCALEZ,PARAM_SCOLOR,PARAM_MODE,PARAM_ANGLE,PARAM_ROTVX,PARAM_ROTVY,PARAM_ROTVZ,PARAM_MINR,PARAM_MAXR};
private int iterations = 12;
private double bailout = 16.0;
private double scalex = 1.0;
private double scaley = 1.0;
private double scalez = 1.0;
private double pcolor = 1.0;
private int mode = 1;
private double angle = 0.0;
private double rotX = 0.0;
private double rotY = 0.0;
private double rotZ = 1.0;
private double minRR = 0.25;
private double maxRR = 1.0;

vec3 radians(vec3 v1)
{
  return new vec3(v1.x*Math.PI/180.0, v1.y*Math.PI/180.0,v1.z*Math.PI/180.0);
}

double radians(double v1)
{
  return v1*Math.PI/180.0;
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

mat3  rotationMatrix3(vec3 v, double angle)
{ double c = Math.cos(radians(angle)); 
  double s = Math.sin(radians(angle));
   return new mat3(c+(1.0-c)*v.x*v.x,
		           (1.0-c)*v.x*v.y-s*v.z, 
		           (1.0-c)*v.x*v.z+s*v.y,
		           (1.0-c)*v.x*v.y+s*v.z,
		           c+(1.0-c)*v.y*v.y,
		           (1.0-c)*v.y*v.z-s*v.x,
		           (1.0-c)*v.x*v.z-s*v.y,
		           (1.0-c)*v.y*v.z+s*v.x,
		           c+(1.0-c)*v.z*v.z); 
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

double rr = MathLib.sqrt(x*x + y*y + z*z);

vec3 rotvec=new vec3(rotX, rotY, rotZ);
mat3 rot = rotationMatrix3(G.normalize(rotvec), angle);

vec3 p=new vec3(x,y,z);
vec3 limit=new vec3(1.,1.,1.);

for (int i = 0; i < iterations; i++)
{

	p.x = Math.abs(p.x + limit.x) - Math.abs(p.x - limit.x) - p.x;
	p.y = Math.abs(p.y + limit.y) - Math.abs(p.y - limit.y) - p.y;
	
	if (mode ==0 ) 
		p.z = Math.abs(p.z + limit.z) - Math.abs(p.z - limit.z) - p.z;

	rr = G.length(p);
	if (rr < minRR)
	{
		p =  p.multiply(maxRR / minRR);
	}
	else if (rr < maxRR)
	{
		p = p.multiply(maxRR / rr);
	}
    p=p.times(rot);
    
	p=p.plus(new vec3(x1,y1,z1));
	
	rr =G.dot(p, p);	

   if (rr < bailout)
   {
     pVarTP.x = pAmount * pVarTP.x;
     pVarTP.y = pAmount * pVarTP.y;
     pVarTP.z = pAmount * pVarTP.z;  
     pVarTP.color=Math.sin(rr)*pcolor;
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
  return new Object[] {iterations, bailout,scalex,scaley,scalez,pcolor,mode, angle, rotX, rotY, rotZ,minRR,maxRR};
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
  else if (PARAM_MODE.equalsIgnoreCase(pName))
	    mode = limitIntVal(Tools.FTOI(pValue), 0, 1); 
  else if (PARAM_ANGLE.equalsIgnoreCase(pName))
	    angle = pValue; 
  else if (PARAM_ROTVX.equalsIgnoreCase(pName))
	    rotX = pValue;
else if (PARAM_ROTVY.equalsIgnoreCase(pName))
		rotY = pValue;
else if (PARAM_ROTVZ.equalsIgnoreCase(pName))
	    rotZ = pValue;
else if (PARAM_MINR.equalsIgnoreCase(pName))
	minRR = pValue;    
else if (PARAM_MAXR.equalsIgnoreCase(pName))
    maxRR = pValue;   
  else
    throw new IllegalArgumentException(pName);
}

@Override
public String getName() {
  return "post_asurf_crop";
}

@Override
public void randomize() {
  iterations = (int) (Math.random() * 25 + 1);
  bailout = Math.random() * 25.0 + 1.0;
  scalex = Math.random() * 3.75 + 0.25;
  scaley = Math.random() * 3.75 + 0.25;
  scalez = Math.random() * 3.75 + 0.25;
  pcolor = (int) (Math.random() * 2);
  mode = (int) (Math.random() * 2);
  angle = Math.random() * 360.0 - 180.0;
  rotX = Math.random() * 5.0;
  rotY = Math.random() * 5.0;
  rotZ = Math.random() * 5.0;
  minRR = Math.random() * 3.0;
  maxRR = Math.random() * 4.0 + 1.0;
}

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_ESCAPE_TIME_FRACTAL,VariationFuncType.VARTYPE_DC};
  }

}