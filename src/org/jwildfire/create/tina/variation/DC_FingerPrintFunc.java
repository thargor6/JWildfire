package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.M_2PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

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



public class DC_FingerPrintFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_fingerprint
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_WIDTH = "width";




	double zoom=50.0;
	private int seed = 10000;
	double time=0.0;
	double width=0.8;



	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	

	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_SEED,PARAM_TIME,PARAM_WIDTH};


	public vec2 hash2( vec2 p )
	{
		p = new vec2( G.dot(p,new vec2(63.31,127.63)), G.dot(p,new vec2(395.467,213.799)) );
		return G.fract(G.sin(p).multiply(43141.59265)).multiply(2.0).minus(1.);
	}
	
	public vec3 getRGBColor(double xp,double yp)
	{

	    
		vec2 uv=new vec2(xp,yp).multiply(zoom);

	    vec3 color=new vec3(0.);// n  

	    double bounds = G.smoothstep(9.,10.,G.length(uv.multiply( new vec2(0.7,0.5))));

	    //cumulate information
	    double a=0.;
	    vec2 h = new vec2(G.floor(7.*time), 0.);
	    for(int i=0; i<50; i++){
	        double s=G.sign(h.x);
	        h = hash2(h).multiply(new vec2(15.,20.));
	    	a += s*G.atan2(uv.x-h.x, uv.y-h.y);
	    }
	    
	    //comment this out for static center
	    uv = uv.plus(G.abs(hash2(h)));
	    
	    a+=G.atan2(uv.y, uv.x); //spirallic center more likely

	    //double width = 0.8; //row width
	    double p=(1.-bounds)*width; //pressure
	    double s = G.min(0.3,p); //smooth
	    double l = G.length(uv)+0.319*a; //base rings plus information
	    
	    //dist -> alternate pattern
	    double m = G.mod(l,2.);
	    double v = (1.-G.smoothstep(2.-s,2.,m))*G.smoothstep(p,p+s,m);

		return new vec3(v);
	}
 	

	

	public String getName() {
		return "dc_fingerprint";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,seed,time,width},super.getParameterValues());
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
		else if (pName.equalsIgnoreCase(PARAM_WIDTH)) {
			width =Tools.limitValue(pValue, 0.0 , 1.0);
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

	public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( varpar->dc_fingerprint_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*varpar->dc_fingerprint_zoom;"
	    		+"color=dc_fingerprint_getRGBColor(uv,varpar->dc_fingerprint_time,varpar->dc_fingerprint_width);"
	    		+"if( varpar->dc_fingerprint_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( varpar->dc_fingerprint_Gradient ==1 )"  
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
	    		+"else if( varpar->dc_fingerprint_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= varpar->dc_fingerprint*x;"
	    		+"__py+= varpar->dc_fingerprint*y;"
	    		+"float dz = z * varpar->dc_fingerprint_scale_z + varpar->dc_fingerprint_offset_z;"
	    		+"if ( varpar->dc_fingerprint_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	}
		 public String getGPUFunctions(FlameTransformationContext context) {
//			 return "";
			 return   "	__device__ float2  dc_fingerprint_hash2 ( float2 p )"
					 +"	{"
					 +"		p = make_float2( dot(p,make_float2(63.31,127.63)), dot(p,make_float2(395.467,213.799)) );"
					 +"		return fract(sinf(p)*(43141.59265))*(2.0)-(1.);"
					 +"	}"
					 +"		"
					 +"	__device__ float3  dc_fingerprint_getRGBColor (float2 uv,float time, float width)"
					 +"	{"
					 +"	    float3 color=make_float3(0.0f,0.0f,0.0f);"
					 +"	    float bounds = smoothstep(9.,10.,length(uv*( make_float2(0.7,0.5))));"
					 +"	    "
					 +"	    float a=0.;"
					 +"	    float2 h = make_float2(floorf(7.*time), 0.);"
					 +"	    for(int i=0; i<50; i++){"
					 +"	        float s=sign(h.x);"
					 +"	        h =  dc_fingerprint_hash2 (h)*(make_float2(15.,20.));"
					 +"	    	a += s*atan2(uv.x-h.x, uv.y-h.y);"
					 +"	    }"
					 +"	    uv = uv+(abs( dc_fingerprint_hash2 (h)));"
					 +"	    "
					 +"	    a+=atan2(uv.y, uv.x); "
					 +"	    "
					 +"	    float p=(1.-bounds)*width; "
					 +"	    float s = fminf(0.3,p); "
					 +"	    float l = length(uv)+0.319*a; "
					 +"	    float m = mod(l,2.);"
					 +"	    float v = (1.-smoothstep(2.-s,2.,m))*smoothstep(p,p+s,m);"
					 +"		return make_float3(v,v,v);"
					 +"	}";
		 }	
}

