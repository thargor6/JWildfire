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

import static org.jwildfire.base.mathlib.MathLib.floor;

import java.util.Random;

public class SymNetG17Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_STEPX = "stepx";
  private static final String PARAM_STEPY = "stepy";


  private static final String[] paramNames = {PARAM_RADIUS,PARAM_STEPX,PARAM_STEPY};

  private double radius = 0.0;
  private double space = 0.0;
  private double stepx = 0.0;
  private double stepy = 0.0;
  
  // Network Symmetry Group 1 
  // Autor: Jesus Sosa Iglesias
  // Date: 05/May/2021
  // Reference: Symmetry Of Bands or stripes 
  // Book: "The Art of Graphics for the IBM PC"
  // Jim McGregor and Alan Watt
  // Addison-Wesley Publishing Company
  // Pages: 162-205
  
  double [][]transfs = {
		     { 1.0 , 0.0   , 0.0  , 0.    ,  1.0 , 0.},  // 1
		     { 1.0 , 0.0   , 0.0  , 0.    , -1.0 , 0.0 },  // 2
		     { 0.5 ,-0.866 , -0. ,  0.866 ,  0.50 , 0.},  // 3 
		     { 0.5 , 0.866 , -0. ,  0.866 , -0.50 ,  0.0},  // 4
		     {-0.5 ,-0.866 ,  -0.0 , 0.866,-0.50 ,0.0},   // 5
		     {-0.5 , 0.866 ,  -0.0 , 0.866, 0.50 , 0.0},  // 6
		     {-1.0 , 0.000 ,  -0.0 , 0.000,-1.00  , 0.0},  // 7
		     {-1.0 , 0.000 ,  -0.0 , 0.000, 1.00  , 0.0},  // 8
		     {-0.5 , 0.866 ,  -0.0 ,-0.866, -0.50 , 0.0},  // 9
		     {-0.5 ,-0.866 ,  -0.0 ,-0.866, 0.50 , 0.0},  // 10
		     { 0.5 , 0.866 ,  -0.0 ,-0.866, 0.50 , 0.0},  // 11
		     { 0.5 ,-0.866 ,  -0.0 ,-0.866,-0.50 , 0.0},  // 12

			  { 1.0 , 0.0   , 0.0  , 0.    ,  1.0 , 0.},  // 13
			  { 1.0 , 0.0   , 0.0  , 0.    , -1.0 , 0.0 },  // 14
			  { 0.5 ,-0.866 , -0. ,  0.866 ,  0.50 , 0.},  // 15
			  { 0.5 , 0.866 , -0. ,  0.866 , -0.50 ,  0.0},  // 16
			  {-0.5 ,-0.866 ,  -0.0 , 0.866,-0.50 ,0.0},   // 17
			  {-0.5 , 0.866 ,  -0.0 , 0.866, 0.50 , 0.0},  // 18  
			  {-1.0 , 0.000 ,  -0.0 , 0.000,-1.00  , 0.0},  // 19
			  {-1.0 , 0.000 ,  -0.0 , 0.000, 1.00  , 0.0},  // 20
			  {-0.5 , 0.866 ,  -0.0 ,-0.866, -0.50 , 0.0},  // 21
			  {-0.5 ,-0.866 ,  -0.0 ,-0.866, 0.50 , 0.0},  // 22
			  { 0.5 , 0.866 ,  -0.0 ,-0.866, 0.50 , 0.0},  // 23
			  { 0.5 ,-0.866 ,  -0.0 ,-0.866,-0.50 , 0.0},  // 24
		      
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
	 
	  space=Math.sqrt(radius*radius/2.0);	
	  
	  
	  double sx=stepx/2;
	  double sy=stepy/2.0;
	  
      
      transfs[0][2]=-sx;
      transfs[0][5]=-sy;
      transfs[1][2]=-sx;
      transfs[1][5]=-sy;
      transfs[2][2]=-sx;
      transfs[2][5]=-sy;
      transfs[3][2]=-sx;
      transfs[3][5]=-sy;
      transfs[4][2]=-sx;
      transfs[4][5]=-sy;
      transfs[5][2]=-sx;
      transfs[5][5]=-sy;     
      transfs[6][2]=-sx;
      transfs[6][5]=-sy;
      transfs[7][2]=-sx;
      transfs[7][5]=-sy;
      transfs[8][2]=-sx;
      transfs[8][5]=-sy;
      transfs[9][2]=-sx;
      transfs[9][5]=-sy;
      transfs[10][2]=-sx;
      transfs[10][5]=-sy;
      transfs[11][2]=-sx;
      transfs[11][5]=-sy;
      
      transfs[12][2]=+sx;
      transfs[12][5]=+sy;
      transfs[13][2]=+sx;
      transfs[13][5]=+sy;
      transfs[14][2]=+sx;
      transfs[14][5]=+sy;
      transfs[15][2]=+sx;
      transfs[15][5]=+sy;
      transfs[16][2]=+sx;
      transfs[16][5]=+sy;
      transfs[17][2]=+sx;
      transfs[17][5]=+sy;     
      transfs[18][2]=+sx;
      transfs[18][5]=+sy;
      transfs[19][2]=+sx;
      transfs[19][5]=+sy;
      transfs[20][2]=+sx;
      transfs[20][5]=+sy;
      transfs[21][2]=+sx;
      transfs[21][5]=+sy;
      transfs[22][2]=+sx;
      transfs[22][5]=+sy;
      transfs[23][2]=+sx;
      transfs[23][5]=+sy;
   }
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
  double x,y;
	    x= pAffineTP.x;
	    y =pAffineTP.y;
	        
	   vec2 z =new vec2(x,y);
	   z=z.plus(new vec2(space,space));
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
    return new Object[]{radius,stepx,stepy};
  }

  @Override
  public void setParameter(String pName, double pValue) {
	    if (PARAM_RADIUS.equalsIgnoreCase(pName))
	        radius = pValue;
	  	else if (PARAM_STEPX.equalsIgnoreCase(pName))
	          stepx = pValue;
	  	else if (PARAM_STEPY.equalsIgnoreCase(pName))
	          stepy = pValue;
	    else
          throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "sym_ng17";
  }
  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D,VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
	@Override
	public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		
	    		+"  float spacex=sqrtf(varpar->sym_ng17_radius*varpar->sym_ng17_radius/2.0);"
	    		+"  float spacey=spacex;"
	    		+"  float sx=varpar->sym_ng17_stepx/2.0;"
	    		+"  float sy=varpar->sym_ng17_stepy/2.0;"

	    		+"Mathc Tx[24]={"
			     +"{ 1.0 , 0.0   , 0.0  , 0.    ,  1.0 , 0.},"
			     +"{ 1.0 , 0.0   , 0.0  , 0.    , -1.0 , 0.0 },"
			     +"{ 0.5 ,-0.866 , -0. ,  0.866 ,  0.50 , 0.}," 
			     +"{ 0.5 , 0.866 , -0. ,  0.866 , -0.50 ,  0.0},"
			     +"{-0.5 ,-0.866 ,  -0.0 , 0.866,-0.50 ,0.0},"
			     +"{-0.5 , 0.866 ,  -0.0 , 0.866, 0.50 , 0.0},"
			     +"{-1.0 , 0.000 ,  -0.0 , 0.000,-1.00  , 0.0},"
			     +"{-1.0 , 0.000 ,  -0.0 , 0.000, 1.00  , 0.0},"
			     +"{-0.5 , 0.866 ,  -0.0 ,-0.866, -0.50 , 0.0},"
			     +"{-0.5 ,-0.866 ,  -0.0 ,-0.866, 0.50 , 0.0},"
			     +"{ 0.5 , 0.866 ,  -0.0 ,-0.866, 0.50 , 0.0},"
			     +"{ 0.5 ,-0.866 ,  -0.0 ,-0.866,-0.50 , 0.0},"
			     +"{ 1.0 , 0.0   , 0.0  , 0.    ,  1.0 , 0.},"
			     +"{ 1.0 , 0.0   , 0.0  , 0.    , -1.0 , 0.0 },"
			     +"{ 0.5 ,-0.866 , -0. ,  0.866 ,  0.50 , 0.},"
			     +"{ 0.5 , 0.866 , -0. ,  0.866 , -0.50 ,  0.0},"
			     +"{-0.5 ,-0.866 ,  -0.0 , 0.866,-0.50 ,0.0},"
			     +"{-0.5 , 0.866 ,  -0.0 , 0.866, 0.50 , 0.0},"  
			     +"{-1.0 , 0.000 ,  -0.0 , 0.000,-1.00  , 0.0},"
			     +"{-1.0 , 0.000 ,  -0.0 , 0.000, 1.00  , 0.0},"
			     +"{-0.5 , 0.866 ,  -0.0 ,-0.866, -0.50 , 0.0},"
			     +"{-0.5 ,-0.866 ,  -0.0 ,-0.866, 0.50 , 0.0},"
			     +"{ 0.5 , 0.866 ,  -0.0 ,-0.866, 0.50 , 0.0},"
			     +"{ 0.5 ,-0.866 ,  -0.0 ,-0.866,-0.50 , 0.0},"
	    		+"};" 	

	    		+"	Tx[0].c =  - sx;"
	    		+"	Tx[0].f =  - sy;"
	    		+"	Tx[1].c =  - sx;"
	    		+"	Tx[1].f =  - sy;"
	    		+"	Tx[2].c =  - sx;"
	    		+"	Tx[2].f =  - sy;"
	    		+"	Tx[3].c =  - sx;"
	    		+"	Tx[3].f =  - sy;"	  
	    		+"	Tx[4].c =  - sx;"
	    		+"	Tx[4].f =  - sy;"
	    		+"	Tx[5].c =  - sx;"
	    		+"	Tx[5].f =  - sy;"	    		
	    		+"	Tx[6].c =  - sx;"
	    		+"	Tx[6].f =  - sy;"
	    		+"	Tx[7].c =  - sx;"
	    		+"	Tx[7].f =  - sy;"	
	    		+"	Tx[8].c =  - sx;"
	    		+"	Tx[8].f =  - sy;"
	    		+"	Tx[9].c =  - sx;"
	    		+"	Tx[9].f =  - sy;"
	    		+"	Tx[10].c = - sx;"
	    		+"	Tx[10].f = - sy;"
	    		+"	Tx[11].c = - sx;"
	    		+"	Tx[11].f = - sy;"	
	    		
	    		+"	Tx[12].c =   sx;"
	    		+"	Tx[12].f =   sy;"
	    		+"	Tx[13].c =   sx;"
	    		+"	Tx[13].f =   sy;"
	    		+"	Tx[14].c =   sx;"
	    		+"	Tx[14].f =   sy;"
	    		+"	Tx[15].c =   sx;"
	    		+"	Tx[15].f =   sy;"	  
	    		+"	Tx[16].c =   sx;"
	    		+"	Tx[16].f =   sy;"
	    		+"	Tx[17].c =   sx;"
	    		+"	Tx[17].f =   sy;"	    		
	    		+"	Tx[18].c =   sx;"
	    		+"	Tx[18].f =   sy;"
	    		+"	Tx[19].c =   sx;"
	    		+"	Tx[19].f =   sy;"	
	    		+"	Tx[20].c =   sx;"
	    		+"	Tx[20].f =   sy;"
	    		+"	Tx[21].c =   sx;"
	    		+"	Tx[21].f =   sy;"
	    		+"	Tx[22].c =   sx;"
	    		+"	Tx[22].f =   sy;"
	    		+"	Tx[23].c =   sx;"
	    		+"	Tx[23].f =   sy;"	
	    		+"    "
	    		+"	x= __x;"
	    		+"  y =__y;"
	    		+"	        "
	    		+"  float2 z =make_float2(x,y);"
	    		+"	z=z+(make_float2(spacex,spacey));"
	    		+"  int index=(int) sizeof(Tx)/sizeof(Tx[0])*RANDFLOAT();"
	    		+"  float2 f = transfhcf(z,Tx[index].a,Tx[index].b,Tx[index].c,Tx[index].d,Tx[index].e,Tx[index].f);"
	    		+"  __px += varpar->sym_ng17 * (f.x);"
	    		+"  __py += varpar->sym_ng17 * (f.y);"
	            + (context.isPreserveZCoordinate() ? "__pz += varpar->sym_ng17 * __z;\n" : "");
	  } 
}
