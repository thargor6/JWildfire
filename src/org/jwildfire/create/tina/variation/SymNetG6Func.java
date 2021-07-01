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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import js.glsl.vec2;

import static org.jwildfire.base.mathlib.MathLib.floor;
import static org.jwildfire.base.mathlib.MathLib.log;
import static org.jwildfire.base.mathlib.MathLib.round;

import java.util.Random;

public class SymNetG6Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

 
  private static final String PARAM_SEPY   = "sepy";
  private static final String PARAM_SKEWX   = "stepx";
  private static final String PARAM_SKEWY   = "stepy";


  private static final String[] paramNames = {PARAM_SEPY,PARAM_SKEWX,PARAM_SKEWY};

  
  private double sepy = 0.1;
  private double stepx = 1.0;
  private double stepy = 1.0;
  
  
  // Network Symmetry Group 1 
  // Autor: Jesus Sosa Iglesias
  // Date: 05/May/2021
  // Reference: Symmetry Of Bands or stripes 
  // Book: "The Art of Graphics for the IBM PC"
  // Jim McGregor and Alan Watt
  // Addison-Wesley Publishing Company
  // Pages: 162-205
  
  double [][]transfs = {
		     {  1.0 , 0.0 , 0.0  , 0. ,  1.0 , 0.0},  // 1
		     {  1.0 , 0.0 , 0.0  , 0. , -1.0 , 0.0 },  // 2
		     {  1.0 , 0.0 , 0.0  , 0. ,  1.0 , 0.0 },  // 3
		     {  1.0 , 0.0 , 0.0  , 0. , -1.0 , 0.0 },  // 4
		     };
  
  public vec2 transform(vec2 xy,double a,double b,double c,double d,double e,double f)
  {
   double xt=a*xy.x+b*xy.y+c;
   double yt=d*xy.x+e*xy.y+f;
   return new vec2(xt,yt);
  }

  public vec2 f(vec2 z)
  {           
    int pointer=(int)(transfs.length*Math.random()); //peek one transformation
    double[] matrix=transfs[pointer];  // take the coefs for the peeked transformation 

  //  apply to point the transformation and return the transformed point
   return transform(z,matrix[0],matrix[1],matrix[2],matrix[3],matrix[4],matrix[5]);
  }
  
  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
	  
	  transfs[0][2] = -stepx/2.0;
	  transfs[0][5] = -stepy/2.0 +sepy/2.;
	  transfs[1][2] =  -stepx/2.0;
	  transfs[1][5] =  -stepy/2.0-sepy/2.;
	  transfs[2][2] =   stepx/2.0;
	  transfs[2][5] =   stepy/2.0+sepy/2.;
	  transfs[3][2] =   stepx/2.0;
	  transfs[3][5] =   stepy/2.0-sepy/2.0;
   }
    
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
  double x,y;
     x= pAffineTP.x;
     y =pAffineTP.y;
	        
    vec2 z =new vec2(x,y);
    vec2 f=f(z);

    pVarTP.x += pAmount * (f.x);
    pVarTP.y += pAmount * (f.y);
    
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
    return new Object[]{sepy,stepx,stepy};
  }

  @Override
  public void setParameter(String pName, double pValue) {
	if (PARAM_SEPY.equalsIgnoreCase(pName))
	     sepy = pValue;
	else if (PARAM_SKEWX.equalsIgnoreCase(pName))
        stepx = pValue;
	else if (PARAM_SKEWY.equalsIgnoreCase(pName))
        stepy = pValue;
    else
        throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "sym_ng6";
  }
  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D,VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
	@Override
	public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"Mathc Tx[4]={	{ 1.0 , 0.0 , 0.0  , 0. , 1.0 , 0.0 }, "
	    		+"		        { 1.0 , 0.0 , 0.0  , 0. ,-1.0 , 0.0 }, "
	    		+"		        { 1.0 , 0.0 , 0.0  , 0. , 1.0 , 0.0 }, "
	    		+"		        { 1.0 , 0.0 , 0.0  , 0. ,-1.0 , 0.0 }, "
	   		    +" };"
   		      		
	    		+"	Tx[0].c =  -varpar->sym_ng6_stepx/2.0;"
	    		+"	Tx[0].f =  -varpar->sym_ng6_stepy/2.0 +varpar->sym_ng6_sepy/2.;"
	    		+"	Tx[1].c =  -varpar->sym_ng6_stepx/2.0;"
	    		+"	Tx[1].f =  -varpar->sym_ng6_stepy/2.0-varpar->sym_ng6_sepy/2.;"
	    		+"	Tx[2].c =   varpar->sym_ng6_stepx/2.0;"
	    		+"	Tx[2].f =   varpar->sym_ng6_stepy/2.0+varpar->sym_ng6_sepy/2.;"
	    		+"	Tx[3].c =   varpar->sym_ng6_stepx/2.0;"
	    		+"	Tx[3].f =   varpar->sym_ng6_stepy/2.0-varpar->sym_ng6_sepy/2.0;"
	    		+"    "
	    		+"	x= __x;"
	    		+"  y =__y;"
	    		+"	        "
	    		+"  float2 z =make_float2(x,y);"
	    		+"  int index=(int) sizeof(Tx)/sizeof(Tx[0])*RANDFLOAT();"
	    		+"  float2 f = transfhcf(z,Tx[index].a,Tx[index].b,Tx[index].c,Tx[index].d,Tx[index].e,Tx[index].f);"
	    		+"  __px += varpar->sym_ng6 * (f.x);"
	    		+"  __py += varpar->sym_ng6 * (f.y);"
	            + (context.isPreserveZCoordinate() ? "__pz += varpar->sym_ng6 * __z;\n" : "");
	  } 
}
