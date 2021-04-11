package org.jwildfire.create.tina.variation;



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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import js.glsl.G;
import js.glsl.vec3;
import js.glsl.vec4;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.Complex;


public class PostAexion3dCropFunc extends VariationFunc {
	private static final long serialVersionUID = 1L;

	private static final String PARAM_ITERS = "Iters";  
	private static final String PARAM_CADD= "cadd";
	private static final String PARAM_BAILOUT = "bailout";
	private static final String PARAM_SCALEX = "scaleX";
	private static final String PARAM_SCALEY = "scaleY";
	private static final String PARAM_SCALEZ = "ScaleZ";
	private static final String PARAM_SCOLOR = "color";

	private static final String[] paramNames = { PARAM_ITERS, PARAM_CADD, PARAM_BAILOUT,PARAM_SCALEX,PARAM_SCALEY,PARAM_SCALEZ,PARAM_SCOLOR};;
	private int Iters = 100;
	private double cadd = -0.8;
	private double bailout = 10.0;
	private double scalex = 1.0;
	private double scaley = 1.0;
	private double scalez = 1.0;
	private double pcolor = 1.0;


	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		//Post Aexion Crop by Jesus Sosa.

		vec4 z=new vec4(pVarTP.x,pVarTP.y,pVarTP.z,1.0);
	//	vec4 p0=new vec4(scalex,scaley,scalez,1.0);
		
		vec4 auxc = new vec4(pVarTP.x,pVarTP.y,pVarTP.z,1.0);

		double r = G.length(z);

		double cx=Math.abs( auxc.x+auxc.y+auxc.z) + cadd;
		double cy=Math.abs(-auxc.x-auxc.y+auxc.z) + cadd;
		double cz=Math.abs(-auxc.x+auxc.y-auxc.z) + cadd;
		double cw =Math.abs(auxc.x-auxc.y-auxc.z) + cadd;

		vec4 p0=new vec4(cx,cy,cz,cw);
		auxc.x=cx*scalex;
		auxc.y=cy*scaley;
		auxc.z=cz*scalez;
		auxc.w=cw*1.0;

		double tempX=Math.abs( z.x+z.y+z.z) + cadd;
		double tempY=Math.abs(-z.x-z.y+z.z) + cadd;
		double tempZ=Math.abs(-z.x+z.y-z.z) + cadd;
		double tempW=Math.abs( z.x-z.y-z.z) + cadd;

		z.x=tempX*p0.x;
		z.y=tempY*p0.y;
		z.z=tempZ*p0.z;
		z.w=tempW*p0.w;

		for (int i=0; i< Iters;i++)
		{
			tempX = z.x * z.x - z.y * z.y + 2.0 * z.w  * z.z + auxc.x;		
			tempY = z.y * z.y - z.x * z.x + 2.0 * z.w  * z.z + auxc.y;
			tempZ = z.z * z.z - z.w  * z.w + 2.0 * z.x * z.y + auxc.z;

			z.x = tempX;
			z.y = tempY;
			z.z = tempZ;
			z.w = tempW;

			z=z.plus(p0);
			r= G.length(z);

			if(r<bailout)
			{
				pVarTP.x = pAmount*pVarTP.x;
				pVarTP.y = pAmount*pVarTP.y;  
				pVarTP.z = pAmount*pVarTP.z;
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
		return new Object[] {Iters, cadd, bailout,scalex,scaley,scalez,pcolor};
	}

	@Override
	public void setParameter(String pName, double pValue) {
		if (PARAM_ITERS.equalsIgnoreCase(pName))
			Iters = limitIntVal(Tools.FTOI(pValue), 1, 250);
		else if (PARAM_CADD.equalsIgnoreCase(pName))
			cadd = pValue;
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
		return "post_aexion_crop";
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_ESCAPE_TIME_FRACTAL};
	}


}

	
	
