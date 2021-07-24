package org.jwildfire.create.tina.variation;

import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.mat2;
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class DC_SquaresFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_squares
	 * Autor: Jesus Sosa
	 * Date: Februarey 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_N = "N";

	private static final String PARAM_DIR = "direction";
	private static final String PARAM_FR = "redF";
	private static final String PARAM_FG = "greenF";
	private static final String PARAM_FB = "blueF";






	private int seed = 5000;
	double time=85.5;
	int N=3;
	int dir=1;


    double FR=1.8,FG=1.9,FB=2.2;


	  
		Random randomize=new Random(seed);
		
	 	long last_time=System.currentTimeMillis();
	 	long elapsed_time=0;
	



	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_N,PARAM_DIR, PARAM_FR,PARAM_FG,PARAM_FB};



	  public boolean hit(vec2 p)
	  {
	      double direction; // -1.0 to zoom out
	      
	      if(dir==1)
	    	  direction=0.1;
	      else
	    	  direction=-1.0;
	      
	      vec2 sectors;
	      int lim=N;
	      vec2 coordIter = p.division(Math.pow(0.5, G.mod(direction*time, 1.0)));
	  	
	      for (int i=0; i < lim; i++) {
	          sectors = (G.floor(coordIter.multiply( 3.0)));
	          if (sectors.x == 1 && sectors.y == 1) {
	              // make a hole
	              return false;
	          } else {
	              // map current sector to whole carpet
	              coordIter = coordIter.multiply(3.0).minus(sectors);
	          }
	      }

	      return true;
	  }
		
	public vec3 getRGBColor(double xp,double yp)
	{

		// Half the width and half the height gives the position of the center of the screen
		
		vec2 coordOrig = new vec2( Math.abs(xp), Math.abs(yp));

	    coordOrig = G.mod(coordOrig, 1.0);
		vec3 color = new vec3(G.cos(time), Math.tan(time), G.sin(time));
		for(int i = 0; i < 4; i++) {
			if (hit( new vec2((double) i*0.1).plus( coordOrig)))
				color = new vec3(1.0).minus(color);
		}
		return new vec3( G.cos(color.x*FR), G.cos(color.y*FG), G.cos(color.z*FB));
	}

 
	

	public String getName() {
		return "dc_squares";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed, time,N,dir,FR,FG,FB},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		 if (PARAM_SEED.equalsIgnoreCase(pName)) 
		{	   seed =  (int)pValue;
	       randomize=new Random(seed);
	          long current_time = System.currentTimeMillis();
	          elapsed_time += (current_time - last_time);
	          last_time = current_time;
	          time = (double) (elapsed_time / 1000.0);
	    }
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = Tools.limitValue(pValue, 0.0 , 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_N)) {
			N =(int)Tools.limitValue(pValue, 1 , 20);
		}
		else if (pName.equalsIgnoreCase(PARAM_DIR)) {
			dir =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_FR)) {
			FR =Tools.limitValue(pValue, 0.0 , 5.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_FG)) {
			FG =Tools.limitValue(pValue, 0.0 , 5.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_FB)) {
			FB =Tools.limitValue(pValue, 0.0 , 5.0);
		}
		else
			super.setParameter(pName, pValue);
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}



	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_squares_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y);"
	    		+"color=dc_squares_getRGBColor(uv,__dc_squares_time,__dc_squares_direction,__dc_squares_N,"
	    		+ "                     __dc_squares_redF,__dc_squares_greenF,__dc_squares_blueF);"
	    		+"if( __dc_squares_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_squares_Gradient ==1 )"
	    		+"{"
	    		+"float4 pal_color=make_float4(color.x,color.y,color.z,1.0);"
	    		+"float4 simcol=pal_color;"
	    		+"float diff=1000000000.0f;"
////read palette colors to find the nearest color to pixel color
	    		+" for(int index=0; index<numColors;index++)"
             +" {      pal_color = read_imageStepMode(palette, numColors, (float)index/(float)numColors);"
	    		+"        float3 pal_color3=make_float3(pal_color.x,pal_color.y,pal_color.z);"
             // implement:  float distance(float,float,float,float,float,float) in GPU function
	        	+"    float dvalue= distance_color(color.x,color.y,color.z,pal_color.x,pal_color.y,pal_color.z);"
	        	+ "   if (diff >dvalue) "
	        	+ "    {" 
	        	+"	     diff = dvalue;" 
	        	+"       simcol=pal_color;" 
	        	+"	   }"
             +" }"
////use nearest palette color as the pixel color                
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = simcol.x;"
	    		+"   __colorG  = simcol.y;"
	    		+"   __colorB  = simcol.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_squares_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_squares*x;"
	    		+"__py+= __dc_squares*y;"
	    		+"float dz = z * __dc_squares_scale_z + __dc_squares_offset_z;"
	    		+"if ( __dc_squares_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	
	 public String getGPUFunctions(FlameTransformationContext context) {

		 return   "	  __device__ bool  dc_squares_hit (float2 p, float time, float dir, float N )"
				 +"	  {"
				 +"	      float direction;"
				 +"	      if(dir==1.0)"
				 +"	    	  direction=0.1;"
				 +"	      else"
  			     +"	    	  direction=-1.0;"
				 +"	      float2 sectors;"
				 +"	      int lim=N;"
				 +"	      float2 coordIter = p/(powf(0.5, mod(direction*time, 1.0)));"
				 +"	      for (int i=0; i < lim; i++) {"
				 +"	          sectors = (floorf(coordIter* 3.0 ));"
				 +"	          if ( (sectors.x == 1.) && (sectors.y == 1.) ) {"
				 +"	              return false;"
				 +"	          } else {"
				 +"	              coordIter = coordIter*3.0-sectors;"
				 +"	          }"
				 +"	      }"
				 +"	      return true;"
				 +"	  }"

				 +"	__device__ float3  dc_squares_getRGBColor (float2 uv, float time, float dir, float N, float FR, float FG, float FB)"
				 +"	{"
				 +"		float2 coordOrig = make_float2( abs(uv.x), abs(uv.y));"
				 +"	    coordOrig = mod(coordOrig, 1.0);"
				 +"		float3 color = make_float3(cosf(time), tanf(time), sinf(time));"
				 +"		for(int i = 0; i < 4; i++) {"
				 +"			if ( dc_squares_hit ( make_float2( (float) i*0.1, (float) i*0.1 )+ coordOrig ,time, dir, N ) )"
				 +"				color = make_float3(1.0,1.0,1.0)-color;"
				 +"		}"
				 +"		return make_float3( cosf(color.x*FR), cosf(color.y*FG), cosf(color.z*FB));"
				 +"	}";
	 }	
}

