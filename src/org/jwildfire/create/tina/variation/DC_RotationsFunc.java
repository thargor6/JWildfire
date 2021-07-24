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



public class DC_RotationsFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_rotations
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_LEVELS = "levels";

	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";



	int levels=20;

	private int seed = 10000;
	double time=0.0;
	double zoom=4.0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	private static final String[] additionalParamNames = { PARAM_LEVELS ,PARAM_SEED,PARAM_TIME,PARAM_ZOOM};

	public vec3 hsv2rgb_smooth( vec3 c )
	{
		vec3 t1=new vec3(0.0,4.0,2.0).plus(c.x*6.0);
		vec3 t2=G.mod(t1,6.0).minus(3.0);
	    vec3 rgb = G.clamp( G.abs(t2).minus(1.0), 0.0, 1.0 );
	    vec3 t0= new vec3 (3.0).minus( rgb.multiply(2.0));
		rgb = rgb.multiply(rgb).multiply(t0); // cubic smoothing	

		return  G.mix( new vec3(1.0), rgb, c.y).multiply(c.z);
	}
	
	public mat2 rotate(double a)
	{
		double c = G.cos(a);
		double s = G.sin(a);
		return new mat2(c, s, -s, c);
	}
	
	public mat2 rot(double a) {
		double c = G.cos(a);
		double s = G.sin(a);
		return new mat2(c, -s, s, c);
	}
	
	public vec3 getRGBColor(double xt,double yt)
	{


		vec2 uv=new vec2(xt,yt);

		uv=uv.multiply(zoom);
		
		double _d =  1.0-G.length(uv);
		double s = 0.5;
		for (int k = 0; k < levels; k++) {
			uv = (G.abs(uv).division( G.dot(uv, uv))).minus(s);
			uv = uv.times(rot(time * .1));
			s *= .955;
		}
		
		 uv = uv.times(rotate(time+uv.x*0.05));

		vec3 ch_color = hsv2rgb_smooth(new vec3(time*0.4+uv.y*.1,1.0,1.0));

		vec3 bg_color = new vec3(_d*0.1, _d*0.2, _d*0.1);
		uv.x += 0.5+G.sin(time+uv.y*0.7)*0.15;

		vec3 color = G.mix(ch_color, bg_color, 0.0);  // shading
		return color;
	}

	

	public String getName() {
		return "dc_rotations";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { levels,seed,time,zoom},super.getParameterValues());
	}


	
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_LEVELS)) {
			
			levels = (int)Tools.limitValue(pValue, 1 , 50);
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
	    		+"float3 color=make_float3(1.0,1.0,1.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_rotations_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_rotations_zoom;"
	    		+"color=dc_rotations_getRGBColor(uv,__dc_rotations_time,__dc_rotations_levels);"
	    		+"if( __dc_rotations_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_rotations_Gradient ==1 )"
	    		+"{"
	    		+"float4 pal_color=make_float4(color.x,color.y,color.z,1.0);"
	    		+"float4 simcol=pal_color;"
	    		+"float diff=1000000000.0f;"

	    		+" for(int index=0; index<numColors;index++)"
               +" {      pal_color = read_imageStepMode(palette, numColors, (float)index/(float)numColors);"
	    		+"        float3 pal_color3=make_float3(pal_color.x,pal_color.y,pal_color.z);"
               
	        	+"    float dvalue= distance_color(color.x,color.y,color.z,pal_color.x,pal_color.y,pal_color.z);"
	        	+ "   if (diff >dvalue) "
	        	+ "    {" 
	        	+"	     diff = dvalue;" 
	        	+"       simcol=pal_color;" 
	        	+"	   }"
               +" }"

	    		+"   __useRgb  = true;"
	    		+"   __colorR  = simcol.x;"
	    		+"   __colorG  = simcol.y;"
	    		+"   __colorB  = simcol.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_rotations_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_rotations*x;"
	    		+"__py+= __dc_rotations*y;"
	    		+"float dz = z * __dc_rotations_scale_z + __dc_rotations_offset_z;"
	    		+"if ( __dc_rotations_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {
		 return  "	__device__ float3  dc_rotations_hsv2rgb_smooth ( float3 c )"
				 +"	{"
				 +"		float3 t1=make_float3(0.0,4.0,2.0) + c.x*6.0;"
				 +"		float3 t2=mod(t1,6.0)-3.0;"
				 +"	    float3 rgb = clamp( abs(t2)-1.0, 0.0, 1.0 );"
				 +"	    float3 t0= make_float3 (3.0,3.0,3.0)- rgb*2.0;"
				 +"		rgb = rgb*rgb*t0;"
				 +"		return  mix( make_float3(1.0,1.0,1.0), rgb, c.y)*c.z;"
				 +"	}"

				 +"	__device__ Mat2  dc_rotations_rotate (float a)"
				 +"	{"
				 +"		float c = cosf(a);"
				 +"		float s = sinf(a);"
				 +"     Mat2 M;"
				 +"		Mat2_Init(&M, c, s, -s, c);"
				 +"     return M;"
				 +"	}"
				 +"	"
				 +"	__device__ Mat2  dc_rotations_rot (float a) {"
				 +"		float c = cosf(a);"
				 +"		float s = sinf(a);"
				 +"     Mat2 M;"
				 +"		Mat2_Init(&M, c, -s, s, c);"
				 +"     return M;"
				 +"	}"
				 
				 +"	__device__ float3  dc_rotations_getRGBColor (float2 uv,float time, float levels )"
				 +"	{"
				 +"		float _d =  1.0-length(uv);"
				 +"		float s = 0.5;"
				 +"		for (int k = 0; k < levels; k++) {"
				 +"			uv = (abs(uv)/( dot(uv, uv)))-(s);"
				 +"         Mat2 m1=dc_rotations_rot (time * .1);"
				 +"			uv = times(&m1,uv);"
				 +"			s *= .955;"
				 +"		}"
				 +"		Mat2 m2=dc_rotations_rotate (time+uv.x*0.05);"
				 +"		 uv = times(&m2,uv);"
				 +"		float3 ch_color =  dc_rotations_hsv2rgb_smooth (make_float3(time*0.4+uv.y*.1,1.0,1.0));"
				 +"		float3 bg_color = make_float3(_d*0.1, _d*0.2, _d*0.1);"
				 +"		uv.x += 0.5+sinf(time+uv.y*0.7)*0.15;"
				 +"		float3 color = mix(ch_color, bg_color, 0.0);"
				 +"		return color;"
				 +"	}";
	 }	
}

