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



public class DC_GlyphoFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_glypho
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_F1 = "f1";
	private static final String PARAM_F2 = "f2";
	private static final String PARAM_F3 = "f3";



	double zoom=7.0;
	private int seed = 10000;
	double time=0.0;
	double f1=0.115;
	double f2=0.75;
	double f3=1.5;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	

	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_SEED,PARAM_TIME,PARAM_F1,PARAM_F2,PARAM_F3};



	public vec3 getRGBColor(double xp,double yp)
	{

	    
		vec2 uv=new vec2(xp,yp).multiply(zoom);

	    vec3 color=new vec3(0.);// n  

	    double m = 0.;
	    double stp = Math.PI/5.;
	    double odd = -1.;
	    for(double n=1.; n<2.5; n+=0.5){        
	        odd *= -1.;
	        double t = time * odd * .3;
	        for(double i=0.0001; i<2.*Math.PI; i+=stp){
	            
	            vec2 uvi = uv.multiply(n).plus(new vec2(cos(i + n*stp*.5 + t)*.4, sin(i + n*stp*.5 + t)*.4));
	            double l = G.length(uvi);
	            m += G.smoothstep(f1 * n, .0, l)*f3;        
	        }    
	    }
	    
	    m = G.step(f2, G.fract(m*f3));
	    
	   color = new vec3(1.* m);

		return color;
	}
 	

	

	public String getName() {
		return "dc_glypho";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,seed,time,f1,f2,f3},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
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
		else if (pName.equalsIgnoreCase(PARAM_F1)) {
			f1 = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_F2)) {
			f2 = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_F3)) {
			f3 = pValue;
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
	    		+"if( varpar->dc_glypho_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*varpar->dc_glypho_zoom;"
	    		+"color=dc_glypho_getRGBColor(uv,varpar->dc_glypho_time,varpar->dc_glypho_f1,varpar->dc_glypho_f2,varpar->dc_glypho_f3);"
	    		+"if( varpar->dc_glypho_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( varpar->dc_glypho_Gradient ==1 )"  
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
	    		+"else if( varpar->dc_glypho_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= varpar->dc_glypho*x;"
	    		+"__py+= varpar->dc_glypho*y;"
	    		+"float dz = z * varpar->dc_glypho_scale_z + varpar->dc_glypho_offset_z;"
	    		+"if ( varpar->dc_glypho_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";

	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "	__device__ float3  dc_glypho_getRGBColor (float2 uv, float time, float f1,float f2,float f3)"
	    		+"	{"
	    		+"	    float3 color=make_float3(0.0,0.0,0.0);"
	    		+"	    float m = 0.;"
	    		+"	    float stp = PI/5.;"
	    		+"	    float odd = -1.;"
	    		+"	    for(float n=1.; n<2.5; n+=0.5){        "
	    		+"	        odd *= -1.;"
	    		+"	        float t = time * odd * .3;"
	    		+"	        for(float i=0.0001; i<2.*PI; i+=stp){"
	    		+"	            float2 uvi = uv*(n)+(make_float2(cosf(i + n*stp*.5 + t)*.4, sinf(i + n*stp*.5 + t)*.4));"
	    		+"	            float l = length(uvi);"
	    		+"	            m += smoothstep(f1 * n, .0, l)*f3;        "
	    		+"	        }"
	    		+"	    }"
	    		+"	    m = step(f2, fract(m*f3));"
	    		+"	   color = make_float3(1.* m, 1.0*m , 1.0*m);"
	    		+"	   return color;"
	    		+"	}";
	  }
}

