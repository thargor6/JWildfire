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



public class DC_TeslaFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_tesla
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_LEVELS = "levels";
	private static final String PARAM_STYLE = "style";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";



	int levels=20;
	double style=10.0;
	private int seed = 10000;
	double time=0.0;
	double zoom=4.0;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	private static final String[] additionalParamNames = { PARAM_LEVELS,PARAM_STYLE,PARAM_SEED,PARAM_TIME,PARAM_ZOOM};


public	double field21( vec3 p, double s) {
	double strength = 20. + .03 * Math.log(1.e-6d + G.fract(G.sin(time*10.) * 4500.0));
	double accum = s*3.;
	double prev = 0.;
	double tw = 0.;
	for (int i = 0; i < levels; ++i) {
		
		double mag = G.dot(p,p)*G.pow(s,1.0*G.abs(G.sin(time/8.0)));
		//remove comment for another effect
		//mag*=dot(p,1.0/p);
		p = G.abs(p).division( mag).add(new vec3(-.9, -.234560*G.abs(G.sin(time)), -1.));
		double w = G.exp(-(double)i / style );
		accum += w *G.exp(-strength * G.pow(G.abs(mag / prev),1.2));
		tw += w*w;
		prev = mag;
	}
//	return G.max(0.,4. * accum / tw - .92);
	return G.max(0.,6. * accum / tw - 2.);
}
	
	public vec3 getRGBColor(double xt,double yt)
	{

		vec2 pos=new vec2(xt,yt).multiply(zoom);

		double col=0.2;
		
		col=field21(new vec3(pos,1.0),0.5);
		return new vec3(col);
	}
 	
	

	public String getName() {
		return "dc_tesla";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { levels,style,seed,time,zoom},super.getParameterValues());
	}


	
	public void setParameter(String pName, double pValue) {
		 if (pName.equalsIgnoreCase(PARAM_LEVELS)) {
			
			levels = (int)Tools.limitValue(pValue, 1 , 25);
		}
		else if (pName.equalsIgnoreCase(PARAM_STYLE)) {
			
			style = Tools.limitValue(pValue, 5. , 100.);
		}
		else if (PARAM_SEED.equalsIgnoreCase(pName)) 
		{	   seed =  (int) pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);

	    }
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
	
			time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			
			zoom = pValue;
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
	    		+"if( __dc_tesla_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_tesla_zoom;"
	    		+"color=dc_tesla_getRGBColor(uv,__dc_tesla_time,__dc_tesla_style,__dc_tesla_levels);"
	    		+"if( __dc_tesla_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_tesla_Gradient ==1 )"
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
	    		+"else if( __dc_tesla_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_tesla*x;"
	    		+"__py+= __dc_tesla*y;"
	    		+"float dz = z * __dc_tesla_scale_z + __dc_tesla_offset_z;"
	    		+"if ( __dc_tesla_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {
//		 return "";
		 return   "__device__	float  dc_tesla_field21 ( float3 p, float s, float time, float style, float levels) {"
				 +"	float strength = 20. + .03 * logf(1.0e-6f + fract(sinf(time*10.) * 4500.0));"
				 +"	float accum = s*3.;"
				 +"	float prev = 0.;"
				 +"	float tw = 0.01;"
				 +"	for (int i = 0; i < levels; ++i) {"
				 +"		"
				 +"		float mag = dot(p,p)*powf(s,1.0*fabsf(sinf(time/8.0)));"
				 +"		p = abs(p)/( mag) + (make_float3(-.9, -.234560*fabsf(sinf(time)), -1.));"
				 +"		float w = expf(-(float)i / style );"
				 +"		accum += w *expf(-strength * powf(fabsf(mag / prev),1.2));"
				 +"		tw += w*w;"
				 +"		prev = mag;"
				 +"	}"
				 +"	return fmaxf(0. ,6. * accum / tw - 2.);"
				 +"}"

				 +"	__device__ float3  dc_tesla_getRGBColor (float2 pos, float time, float style, float levels)"
				 +"	{"
				 +"		float col=0.2;"
				 +"		col= dc_tesla_field21 (make_float3(pos.x,pos.y,1.0), 0.5, time,style, levels);"
				 +"		return make_float3(col,col,col);"
				 +"	}";
	 }	
}

