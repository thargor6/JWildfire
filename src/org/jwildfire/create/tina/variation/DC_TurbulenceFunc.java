package org.jwildfire.create.tina.variation;

import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;



public class DC_TurbulenceFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_turbulence
	 * Autor: Jesus Sosa
	 * Date: October 31, 2018
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_LEVEL = "level";


	private int seed = 10000;
    double time=0.0;
    double zoom=8.0;
	int level=3;

	int gradient=0;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_ZOOM,PARAM_LEVEL};

	   
	  
	public vec3 getRGBColor(double xp,double yp)
	{

	//	vec2 p=new vec2(8.0*xp-20.0,8.0*yp-20.0);
		vec2 p=new vec2(xp,yp).multiply(zoom).minus(20.0);
		vec3 col=new vec3(0.0);


		vec2 ik = p;
		double c = 1.0;
		double inten = .05;

		for (int n = 0; n < level; n++) 
		{
			double t = time * (1.0 - (3.0 / (double)(n+1)));
			ik = p.plus(new  vec2(G.cos(t - ik.x) + G.sin(t + ik.y), G.sin(t - ik.y) + G.cos(t + ik.x)));
			c += 1.0/G.length(new vec2(p.x / (G.sin(ik.x+t)/inten),p.y / (G.cos(ik.y+t)/inten)));
		}
		c /= (double)level;
		c = 1.5-G.sqrt(c);
		return new vec3(c*c*c*c);
	}


	public String getName() {
		return "dc_turbulence";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed,time,zoom,level},super.getParameterValues());
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
			time = Tools.limitValue(pValue, 1.0 , 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_LEVEL)) {
			level = (int)Tools.limitValue(pValue, 1 , 5);
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
	    		+"if( varpar->dc_turbulence_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*varpar->dc_turbulence_zoom-20.0;"
	    		+"color=dc_turbulence_getRGBColor(uv,varpar->dc_turbulence_time,varpar->dc_turbulence_zoom);"
	    		+"if( varpar->dc_turbulence_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( varpar->dc_turbulence_Gradient ==1 )"  
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
	    		+"else if( varpar->dc_turbulence_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= varpar->dc_turbulence*x;"
	    		+"__py+= varpar->dc_turbulence*y;"
	    		+"float dz = z * varpar->dc_turbulence_scale_z + varpar->dc_turbulence_offset_z;"
	    		+"if ( varpar->dc_turbulence_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {
		 return   "	__device__ float3  dc_turbulence_getRGBColor (float2 p, float time, float level)"
				 +"	{"
				 +"		float2 ik = p;"
				 +"		float c = 1.0;"
				 +"		float inten = .05;"
				 +"		for (int n = 0; n < level; n++) "
				 +"		{"
				 +"			float t = time * (1.0 - (3.0 / (float)(n+1)));"
				 +"			ik = p+(make_float2(cosf(t - ik.x) + sinf(t + ik.y), sinf(t - ik.y) + cosf(t + ik.x)));"
				 +"			c += 1.0/length(make_float2(p.x / (sinf(ik.x+t)/inten),p.y / (cosf(ik.y+t)/inten)));"
				 +"		}"
				 +"		c /= level;"
				 +"		c = 1.5-sqrtf(c);"
				 +"     float color=c*c*c*c;"
				 +"		return make_float3(color,color,color);"
				 +"	}";
	 }	
}

