package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import java.util.Random;
import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;

public class DC_CairoTilesFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_cairotiles
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";



	double zoom=7.0;
	private int seed = 10000;
	double time=0.0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	

	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_SEED,PARAM_TIME};



	public vec3 getRGBColor(double xp,double yp)
	{

	    
	    double pi = Math.acos(-1.);
	    
		vec2 p=new vec2(xp,yp).multiply(zoom);

	    vec3 color=new vec3(0.);// n  

	    double th = G.mod(time * pi / 5., pi * 2.);
	    double gridsize = (.5 + G.abs(sin(th * 2.)) * (Math.sqrt(2.) / 2. - .5)) * 2.;

	    int flip = 0;

	    if(G.fract(th / pi + .25) > .5)
	    {
	        p = p.minus(.5);
	        flip = 1;
	    }

	    p = p.multiply(gridsize);

	    vec2 cp = G.floor(p.division(gridsize));

	    p = G.mod(p, gridsize).minus(gridsize / 2.);

	    p = p.multiply(G.mod(cp, 2.).multiply( 2.).minus( 1.));

	    p = p.times(new mat2(cos(th), sin(th), -sin(th), cos(th)));

	    double w = zoom / 2000. * 1.5;
	    
	    double a = G.smoothstep(-w, +w, G.max(Math.abs(p.x), Math.abs(p.y)) - .5);

	    if(flip==1)
	        a = 1. - a;

	    if(flip==1 && a < .5 && (Math.abs(p.x) - Math.abs(p.y)) * G.sign(G.fract(th / pi) - .5) > 0.)
	        a = .4;

	    if(flip==0 && a < .5 && (G.mod(cp.x + cp.y, 2.) - .5) > 0.)
	        a = .4;

	    color = G.pow(new vec3(a), new vec3(1. / 2.2));

		return color;
	}
 	

	

	public String getName() {
		return "dc_cairotiles";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,seed,time},super.getParameterValues());
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_cairotiles_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_cairotiles_zoom;"
	    		+"color=dc_cairotiles_getRGBColor(uv,__dc_cairotiles_time,__dc_cairotiles_zoom);"
	    		+"if( __dc_cairotiles_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_cairotiles_Gradient ==1 )"
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
	    		+"else if( __dc_cairotiles_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_cairotiles*x;"
	    		+"__py+= __dc_cairotiles*y;"
	    		+"float dz = z * __dc_cairotiles_scale_z + __dc_cairotiles_offset_z;"
	    		+"if ( __dc_cairotiles_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {
		 return   "__device__ float3 dc_cairotiles_getRGBColor (float2 uv, float time,float zoom)"
				+"{"				
			    +"  float3 color=make_float3(0.0,0.0,0.0);"  
			    +"  float th = mod(time * PI / 5., PI * 2.);"
			    +"  float gridsize = (.5 + abs(sinf(th * 2.)) * (sqrt(2.) / 2. - .5)) * 2.;"
			    +"  int flip = 0;"
			    +"  if(fract(th / PI + .25) > .5)"
			    +"  {"
			    +"    uv = uv-.5;"
			    +"    flip = 1;"
			    +"  }"
			    +"  uv = uv*gridsize;"
			    +"  float2 cp = floorf(uv/gridsize);"
			    +"  uv = mod(uv, gridsize)-(gridsize / 2.);"
			    +"  uv = uv*(mod(cp, 2.)* 2.- 1.);"
			    +"  Mat2 m;"
			    +"  Mat2_Init(&m,cosf(th), sinf(th), -sinf(th), cosf(th));"
			    +"  uv = times(&m,uv);"
				+"  float w = zoom / 2000. * 1.5;"
				+"  float a = smoothstep(-w, +w, fmaxf(abs(uv.x), abs(uv.y)) - .5);"
				+"  if(flip==1)"
				+"    a = 1. - a;"
				+"  if(flip==1 && a < .5 && (abs(uv.x) - abs(uv.y)) * sign(fract(th / PI) - .5) > 0.)"
				+"    a = .4;"
				+"  if(flip==0 && a < .5 && (mod(cp.x + cp.y, 2.) - .5) > 0.)"
				+"    a = .4;"
				+"  float frac=1.0f/2.2;"
				+"  color = pow(make_float3(a,a,a), make_float3(frac,frac,frac));"
				+"	return color;"
		        +"}";
	 }	
 }	 
	 


