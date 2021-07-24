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

public class SymNetG12Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SPACE = "space";
  private static final String PARAM_SPACEX = "spacex";
  private static final String PARAM_SPACEY = "spacey";

  private static final String[] paramNames = {PARAM_SPACE, PARAM_SPACEX,PARAM_SPACEY};

  private double space = 0.0;
  private double spacex = 0.0;
  private double spacey = 0.0;

  // Network Symmetry Group 1 
  // Autor: Jesus Sosa Iglesias
  // Date: 05/May/2021
  // Reference: Symmetry Of Bands or stripes 
  // Book: "The Art of Graphics for the IBM PC"
  // Jim McGregor and Alan Watt
  // Addison-Wesley Publishing Company
  // Pages: 162-205
  
  double [][]transfs = {
		     { 1.0 , 0.0 , 0.0+spacex  , 0. , 1.0 , 0.+spacey},  // 1
		     {-1.0 , 0.0 , 0.0-spacex  , 0. ,-1.0 , 0.0 -spacey},  // 2

		      {-0.0 , 1.0 , -0.+spacex , -1. , -0.0 , 0.-spacey},  // 3 
		     {-0.0 ,-1.0 , -0.-spacex ,  1. , -0.0 ,  0.0+spacey},  // 4

		      {-1.0 , 0.0 ,  -0.0-spacex , 0.  , 1.0 ,0.0+spacey},   // 5
		      {-0.0 ,-1.0 ,  -0.0-spacex ,-1. ,-0.0 , 0.0-spacey},  // 6

		       {-0.0 , 1.0 , -0.+spacex , 1. ,-0.0 , 0.0+spacey},   // 7
		       { 1.0 , 0.0 , -0.+spacex , 0. ,-1.0 , 0.0-spacey}    // 8
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
	  
	  transfs[0][2] = +spacex;
	  transfs[0][5] = +spacey;
	  transfs[1][2]=  -spacex;
	  transfs[1][5]=  -spacey;
	  transfs[2][2]=   spacex;
	  transfs[2][5]=  -spacey;
	  transfs[3][2]=  -spacex;
	  transfs[3][5]=   spacey;
	  transfs[4][2]=  -spacex;
	  transfs[4][5]=   spacey;
	  transfs[5][2]=  -spacex;
	  transfs[5][5]=  -spacey;
	  transfs[6][2]=   spacex;
	  transfs[6][5]=   spacey;
	  transfs[7][2]=   spacex;
	  transfs[7][5]=  -spacey;
	  
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
    return new Object[]{space, spacex,spacey};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SPACE.equalsIgnoreCase(pName))
      space = pValue;
    else if (PARAM_SPACEX.equalsIgnoreCase(pName))
      spacex = pValue;
    else if (PARAM_SPACEY.equalsIgnoreCase(pName))
        spacey = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "sym_ng12";
  }
  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D,VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

	@Override
	public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		
	    		+"  float spacex=__sym_ng12_spacex;"
	    		+"  float spacey=__sym_ng12_spacey;"
	    		
	    		+"Mathc Tx[8]={	{ 1.0 , 0.0 , 0.0+spacex  ,  0.0 ,  1.0  , 0.0+spacey  }, "
	    		+"		        {-1.0 , 0.0 , 0.0-spacex  ,  0.0 , -1.0  , 0.0-spacey  }, "
	    		+"		        { 0.0 , 1.0 , 0.0+spacex  , -1.0 ,  0.0  , 0.0-spacey  }, "
	    		+"		        { 0.0 ,-1.0 , 0.0-spacex  ,  1.  ,  0.0  , 0.0+spacey  }, "
	    		+"		        {-1.0 , 0.0 , 0.0-spacex  ,  0.  ,  1.0  , 0.0+spacey  }, "
	    		+"		        { 0.0 ,-1.0 , 0.0-spacex  , -1.  ,  0.0  , 0.0-spacey  }, "
	    		+"		        { 0.0 , 1.0 , 0.0+spacex  ,  1.  ,  0.0  , 0.0+spacey  }, "
	    		+"		        { 1.0 , 0.0 , 0.0+spacex  ,  0.  , -1.0  , 0.0-spacey  }, "
	    		+"		      };" 		
	    		+"	Tx[0].c =    spacex;"
	    		+"	Tx[0].f =    spacey;"
	    		+"	Tx[1].c =  - spacex;"
	    		+"	Tx[1].f =  - spacey;"
	    		+"	Tx[2].c =    spacex;"
	    		+"	Tx[2].f =  - spacey;"
	    		+"	Tx[3].c =  - spacex;"
	    		+"	Tx[3].f =    spacey;"	  
	    		+"	Tx[4].c =  - spacex;"
	    		+"	Tx[4].f =    spacey;"
	    		+"	Tx[5].c =  - spacex;"
	    		+"	Tx[5].f =  - spacey;"
	    		+"	Tx[6].c =    spacex;"
	    		+"	Tx[6].f =    spacey;"
	    		+"	Tx[7].c =    spacex;"
	    		+"	Tx[7].f =  - spacey;"	    
	    		+"    "
	    		+"	x= __x;"
	    		+"  y =__y;"
	    		+"	        "
	    		+"  float2 z =make_float2(x,y);"
	    		+"	z=z+(make_float2(__sym_ng12_space,__sym_ng12_space));"
	    		+"  int index=(int) sizeof(Tx)/sizeof(Tx[0])*RANDFLOAT();"
	    		+"  float2 f = transfhcf(z,Tx[index].a,Tx[index].b,Tx[index].c,Tx[index].d,Tx[index].e,Tx[index].f);"
	    		+"  __px += __sym_ng12 * (f.x);"
	    		+"  __py += __sym_ng12 * (f.y);"
	            + (context.isPreserveZCoordinate() ? "__pz += __sym_ng12 * __z;\n" : "");
	  }
}
