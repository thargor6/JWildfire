package org.jwildfire.create.tina.variation;


import java.util.Random;
import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;
import org.jwildfire.base.Tools;

public class DC_GaborNoiseFunc  extends DC_BaseFunc implements SupportsGPU  {

	/*
	 * Variation : dc_gabornoise
	 * Autor: Jesus Sosa
	 * Date: may 10, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";


	private int seed = 10000;
	double time=0.0;
	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;

	double zoom=20.0;


	
	

	private static final String[] additionalParamNames = {PARAM_SEED,PARAM_TIME, PARAM_ZOOM};


	
	// variant of https://shadertoy.com/view/3slXDf

	// simplified from skaplun's "AFC Ajax" https://shadertoy.com/view/tdsSzs
	// borrowing diviaki's "fingerprint" https://www.shadertoy.com/view/4t3SWN

	public vec3  hash(double p)
	{
		return (G.fract(G.sin( (new vec3(63.31,395.467,1).multiply(p)) ).multiply(43141.59265)).multiply(2.).minus(1.) );
	}

	public vec3 getRGBColor(double xp,double yp)
	{

	    
		vec2 V=new vec2(xp,yp).multiply(zoom);
        vec2 U = new vec2( G.abs(V.x),  V.y ).multiply(45.0);
        
	    vec3 O=new vec3(0.);// n  

	    double a = 0., s = 1.;

	    for(int i=0; i<100; i++){
	        vec3 h = hash((double)i).multiply(30.);
	        vec2 t=new vec2(h.x,h.y);
	        t= t.times(new mat2(new  vec4(0,33,11,0).plus(G.cos( h.z/1e2*time)) )); // https://www.shadertoy.com/view/XlsyWX
	        h.x=t.x;h.y=t.y;
	        //if (V.x>0.) s = sign(h.z);
	    	a += s * G.atan2(U.x-h.x, U.y-h.y); // + .05*h.z*iTime;
	    }
	    O = ( G.cos( new vec3(0,23,21).plus(a)  ).multiply(0.6).plus(0.6) ); // https://www.shadertoy.com/view/ll2cDc

		return O;
	}
 	

	

	public String getName() {
		return "dc_gabornoise";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed,time, zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 1.0 , 100.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_SEED)) {
			   seed =   (int)Tools.limitValue(pValue, 0 , 10000);
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTS_BACKGROUND};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_gabornoise_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_gabornoise_zoom;"
	    		+"color=dc_gabornoise_getRGBColor(uv,__dc_gabornoise_time);"
	    		+"if( __dc_gabornoise_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_gabornoise_Gradient ==1 )"
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
	    		+"else if( __dc_gabornoise_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_gabornoise*x;"
	    		+"__py+= __dc_gabornoise*y;"
	    		+"float dz = z * __dc_gabornoise_scale_z + __dc_gabornoise_offset_z;"
	    		+"if ( __dc_gabornoise_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {
		 return  "	__device__ float3  dc_gabornoise_hash(float p)"
				 +"	{"
				 +"		return (fract(sinf( (make_float3(63.31,395.467,1)*p) )*(43141.59265))*(2.)-(1.) );"
				 +"	}"
				 
				 +"	__device__ float3  dc_gabornoise_getRGBColor (float2 V,float time)"
				 +"	{"
				 +""
				 +"     float2 U = make_float2( abs(V.x),  V.y )*45.0f;"
				 +" "
				 +"	    float3 O=make_float3(0.0f,0.0f,0.0f);"
				 +"	    float a = 0.0f, s = 1.0f;"
				 +"	    for(int i=0; i<100; i++){"
				 +"	        float3 h = dc_gabornoise_hash((float)i)*30.0f;"
				 +"	        float2 t=make_float2(h.x,h.y);"
				 +"         Mat2 M;"
				 +"         float4 ve= make_float4(0.0f,33.0f,11.0f,0.0f)+(cosf( h.z/1.0e2*time));"
				 +"         Mat2_Init(&M,ve.x,ve.y,ve.z,ve.w);"
				 +"	        t= times(&M,t);"
				 +"	        h.x=t.x;h.y=t.y;"
				 +"	    	a += s * atan2(U.x-h.x, U.y-h.y); "
				 +"	    }"
				 +"	    O = cos( make_float3(0.0f,23.0f,21.0f)+a )*0.6+0.6;"
				 +"		return O;"
				 +"	}";
	 }	
}

