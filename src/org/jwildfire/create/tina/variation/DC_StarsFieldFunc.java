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

import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class DC_StarsFieldFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_starsfield
	 * Date: February 13, 2019
	 * Author:Jesus Sosa
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZDISTANCE = "zdistance";
	private static final String PARAM_GLOW = "glow";


	private int seed = 10000;


	double time=0.0;
    double zdistance=2.0;
    double glow=1.0;

	  
		Random randomize=new Random(seed);
		
	 	long last_time=System.currentTimeMillis();
	 	long elapsed_time=0;
	



	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_ZDISTANCE,PARAM_GLOW};


	
	  public mat2 rotate(double a) {
		    double c = G.cos(a);
		    double s = G.sin(a);
		    return new mat2(c, s, -s, c);
		}


		// one dimensional | 1 in 1 out
		public double hash11(double p) {
		    p = G.fract(p * 35.35);
		    p += G.dot(p, p + 45.85);
		    return G.fract(p * 7858.58);
		}

		// two dimensional | 2 in 1 out
		public double hash21(vec2 p) {
		    p = G.fract(p.multiply( new vec2(451.45, 231.95)));
		    p = p.plus(G.dot(p, p.plus( 78.78)));
		    return G.fract(p.x * p.y);
		}

		// two dimensional | 2 in 2 out
		public vec2 hash22(vec2 p) 
		{
			vec3 t1=new vec3(p.x,p.y,p.x);
			t1=t1.multiply(new vec3(451.45, 231.95, 7878.5));
		    vec3 q = G.fract(t1);
		    q = q.add(G.dot(q, q.add( 78.78)));
		    return G.fract(new vec2(q.x,q.z).multiply( q.y));
		}

		public double layer(vec2 uv) {

		    double c = 0.;

		    uv = uv.multiply(5.);

		    vec2 i = G.floor(uv);
		    vec2 f = G.fract(uv).multiply(zdistance).minus( 1.);

		    vec2 p = hash22(i).multiply(0.3); 
		    double d = G.length(f.minus(p) );
		    c += G.smoothstep(.1 + .8 * hash21(i), .01, d);
		    c *= (1. / d) * .2;

		    return c;
		}


		public vec3 render(vec2 uv) {

		    vec3 col = new vec3(0.);
		    uv = uv.times(rotate(time ));
			uv = uv.plus(new vec2(G.cos(time ), G.sin(time)).multiply(2.0));
		    
		    for (double i = 0.; i < 1.; i += .1) {

		        uv = uv.times(rotate(hash11(i) * 6.28));
		        
		        double t = G.fract(i - time );
		        double s = G.smoothstep(0., 1., t);
		        double f = G.smoothstep(0., 1., t);
		        f *= G.smoothstep(1., 0., t);
		        
		        vec2 k = hash22(new vec2(i, i * 5.)).multiply(0.1);
		        double l = layer((uv.minus(k)).multiply( s));
		        
		        col = col.add(G.mix(new vec3(.0, .0, 0), new vec3(1., 1.0, 1.0), l).multiply(f));

		    }

		    double t1= glow*hash21( uv.plus(time));
		    col = col.add( new vec3(t1));
		    return col;

		}


	public vec3 getRGBColor(double xp,double yp)
	{

		
		vec2 uv = new vec2( xp,yp);
		vec3 col= render(uv);

		return col;
	}
 
	

	public String getName() {
		return "dc_starsfield";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed, time,zdistance,glow},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (PARAM_SEED.equalsIgnoreCase(pName)) 
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
		else if (pName.equalsIgnoreCase(PARAM_ZDISTANCE)) {
			zdistance =Tools.limitValue(pValue, 2.0 , 150.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_GLOW)) {
			glow=Tools.limitValue(pValue, 0.0 , 1.0);
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
	    		+"if( varpar->dc_starsfield_ColorOnly ==1)"
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
	    		+"color=dc_starsfield_getRGBColor(uv,varpar->dc_starsfield_time,varpar->dc_starsfield_zdistance,varpar->dc_starsfield_glow);"
	    		+"if( varpar->dc_starsfield_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( varpar->dc_starsfield_Gradient ==1 )"  
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
	    		+"else if( varpar->dc_starsfield_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= varpar->dc_starsfield*x;"
	    		+"__py+= varpar->dc_starsfield*y;"
	    		+"float dz = z * varpar->dc_starsfield_scale_z + varpar->dc_starsfield_offset_z;"
	    		+"if ( varpar->dc_starsfield_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	
	 public String getGPUFunctions(FlameTransformationContext context) {

		 return   "	  __device__ Mat2  dc_starsfield_rotate (float a) {"
				 +"		    float c = cosf(a);"
				 +"		    float s = sinf(a);"
				 +"         Mat2 M;"
				 +"         Mat2_Init(&M,c, s, -s, c);"
				 +"         return M;"
				 +"		}"

				 +"		__device__ float  dc_starsfield_hash11 (float p) {"
				 +"		    p = fract(p * 35.35);"
				 +"		    p += dot(p, p + 45.85);"
				 +"		    return fract(p * 7858.58);"
				 +"		}"

				 +"		__device__ float hash21(float2 p) {"
				 +"		    p = fract(p*( make_float2(451.45, 231.95)));"
				 +"		    p = p+(dot(p, p+ 78.78));"
				 +"		    return fract(p.x * p.y);"
				 +"		}"

				 +"		__device__ float2  dc_starsfield_hash22 (float2 p) "
				 +"		{"
				 +"			float3 t1=make_float3(p.x,p.y,p.x);"
				 +"			t1=t1*make_float3(451.45, 231.95, 7878.5);"
				 +"		    float3 q = fract(t1);"
				 +"		    q = q + dot(q, q +  78.78);"
				 +"		    return fract(make_float2(q.x,q.z)*( q.y));"
				 +"		}"
				 
				 +"		__device__ float  dc_starsfield_layer (float2 uv, float zdistance) {"
				 +"		    float c = 0.0;"
				 +"		    uv = uv*5.0;"
				 +"		    float2 i = floorf(uv);"
				 +"		    float2 f = fract(uv)*zdistance - 1.0f;"
				 +"		    float2 p =  dc_starsfield_hash22 (i)*0.3; "
				 +"		    float d = length(f-p);"
				 +"		    c += smoothstep(.1 + .8 * hash21(i), .01, d);"
				 +"		    c *= (1.0f / d) * 0.2f;"
				 +"		    return c;"
				 +"		}"
				 
				 +"		__device__ float3  dc_starsfield_render (float2 uv, float time, float zdistance, float glow) {"
				 +"		    float3 col = make_float3(0.,0.,0.);"
				 +"         Mat2 mat=dc_starsfield_rotate (time );"
				 +"		    uv = times(&mat,uv);"
				 +"			uv = uv+(make_float2(cosf(time ), sinf(time))*(2.0));"
				 +"		    "
				 +"		    for (float i = 0.; i < 1.; i += .1) {"
				 +"             Mat2 mat1=dc_starsfield_rotate ( dc_starsfield_hash11 (i) * 6.28);"
				 +"		        uv = times(&mat1,uv);"
				 +"		        "
				 +"		        float t = fract(i - time );"
				 +"		        float s = smoothstep(0., 1., t);"
				 +"		        float f = smoothstep(0., 1., t);"
				 +"		        f *= smoothstep(1., 0., t);"
				 +"		        "
				 +"		        float2 k =  dc_starsfield_hash22 (make_float2(i, i * 5.))*0.1;"
				 +"		        float l =  dc_starsfield_layer ((uv-(k))*( s), zdistance);"
				 +"		        "
				 +"		        col = col + mix(make_float3(.0, .0, 0), make_float3(1., 1.0, 1.0), l)*f;"
				 +"		    }"
				 +"		    float t1= glow*hash21( uv+time);"
				 +"		    col = col +  make_float3(t1,t1,t1);"
				 +"		    return col;"
				 +"		}"
				 
				 +"	__device__ float3  dc_starsfield_getRGBColor (float2 uv, float time, float zdistance, float glow)"
				 +"	{"
				 +"		float3 col=  dc_starsfield_render (uv, time, zdistance, glow);"
				 +"		return col;"
				 +"	}";
	 }	
}

