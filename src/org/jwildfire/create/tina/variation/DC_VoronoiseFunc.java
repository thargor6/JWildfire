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



public class DC_VoronoiseFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_voronise
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_DELTAX = "deltaX";
	private static final String PARAM_DELTAY = "deltaY";


	double zoom = 8.0;
    double deltaX=0.5;
	double deltaY=0.5;

	
	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_DELTAX,PARAM_DELTAY};
	    
	  
	public vec3 getRGBColor(double xp,double yp)
	{

		vec2 st=new vec2(xp,yp);
		vec3 col=new vec3(0.0);
	    st=st.multiply(zoom);
	    double n = G.iqnoise(st, deltaX, deltaY);

	    col = new vec3(n);
		return col;
	}
 	
	public String getName() {
		return "dc_voronoise";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,deltaX, deltaY},super.getParameterValues());
	}
	
	public void setParameter(String pName, double pValue) {
		if (PARAM_ZOOM.equalsIgnoreCase(pName)) 
		{	   zoom =  pValue;

	    }
		else if (pName.equalsIgnoreCase(PARAM_DELTAX)) {
			deltaX = Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_DELTAY)) {
			deltaY = Tools.limitValue(pValue, 0.0 , 1.0);
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
	    		+"if( __dc_voronoise_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_voronoise_zoom;"
	    		+"color=dc_voronise_getRGBColor(uv, __dc_voronoise_deltaX, __dc_voronoise_deltaY);"
	    		+"if( __dc_voronoise_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_voronoise_Gradient ==1 )"
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
	    		+"else if( __dc_voronoise_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_voronoise*x;"
	    		+"__py+= __dc_voronoise*y;"
	    		+"float dz = z * __dc_voronoise_scale_z + __dc_voronoise_offset_z;"
	    		+"if ( __dc_voronoise_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {

		 return   "	__device__ float3  dc_voronise_hash3 ( float2 p ) {"
				 +"	   float3 q = make_float3(dot(p,make_float2(127.1,311.7)),"
				 +"		        dot(p,make_float2(269.5,183.3)),"
				 +"				dot(p,make_float2(419.2,371.9)) );"
				 +"	   return fract(sin(q)*43758.5453);"
				 +"}"
				 
				 +"	__device__ float  dc_voronise_iqnoise ( float2 x, float u, float v ) {"
				 +"			float2 p = floorf(x);"
				 +"			float2 f = fract(x);"
				 +"			float k = 1.0+63.0*powf(1.0-v,4.0);"
				 +"			float va = 0.0;"
				 +"			float wt = 0.0;"
				 +"			for (int j=-2; j<=2; j++) {"
				 +"				for (int i=-2; i<=2; i++) {"
				 +"					float2 g = make_float2((float)i,(float)j);"
				 +"					float3 o =  dc_voronise_hash3 (p+g)*( make_float3(u,u,1.0));"
				 +"					float2 r = g-f+(make_float2( o.x,o.y));"
				 +"					float d = dot(r,r);"
				 +"					float ww = powf( 1.0-smoothstep(0.0,1.414,sqrtf(d)), k );"
				 +"					va += o.z*ww;"
				 +"					wt += ww;"
				 +"				}"
				 +"			}"
				 +"			return va/wt;"
				 +"}"
				 
				 +"	__device__ float3 dc_voronise_getRGBColor (float2 st, float deltaX, float deltaY) {"
				 +"		float3 col=make_float3(0.0,0.0,0.0);"
				 +"	    float n = dc_voronise_iqnoise(st, deltaX, deltaY);"
				 +"	    col = make_float3(n,n,n);"
				 +"		return col;"
				 +"	}";
	 }	
}

