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



public class DC_MengerFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_menger
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_LEVEL = "level";


	private double zoom = 1.0;
	int level=3;

	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_LEVEL};

	    

	  public double sdBox(vec2 p, vec2 s) {
			p = G.abs(p).minus( s);
			return G.length(G.max(p, 0.0)) + G.min(G.max(p.x, p.y), 0.0);
		}

		public double sdCross(vec2 p) {
			p = G.abs(p);
			return G.min(p.x, p.y) - 1.0;
		}

		public double sierpinskiCarpet(vec2 p) {
			double d = sdBox(p, new vec2(1.0));
			
			double s = 1.0;
			for (int i = 0; i < level; i++) {
				vec2 a = G.mod(p.multiply( s), 2.0).minus( 1.0);
				vec2 r = new vec2(1.0).minus( G.abs(a).multiply(3.0));
				s *= 3.0;
				double c = sdCross(r) / s;
				d = G.max(d, c);
			}
			return d;
		}
	  
	public vec3 getRGBColor(double xp,double yp)
	{

		vec2 p=new vec2(xp,yp).multiply(zoom);
		vec3 col=new vec3(0.0);

        double d=sierpinskiCarpet(p.multiply(1.3));
		col= d > 0.0 ? new vec3(0.0) : new vec3(1.0);
		return col;
	}
 	

	public String getName() {
		return "dc_menger";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,level},super.getParameterValues());
	}

	
	public void setParameter(String pName, double pValue) {
		if (PARAM_ZOOM.equalsIgnoreCase(pName)) 
		{	   zoom =   pValue;

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
	    		+"if( varpar->dc_menger_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*varpar->dc_menger_zoom;"
	    		+"color=dc_menger_getRGBColor(uv,varpar->dc_menger_level);"
	    		+"if( varpar->dc_menger_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( varpar->dc_menger_Gradient ==1 )"  
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
	    		+"else if( varpar->dc_menger_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= varpar->dc_menger*x;"
	    		+"__py+= varpar->dc_menger*y;"
	    		+"float dz = z * varpar->dc_menger_scale_z + varpar->dc_menger_offset_z;"
	    		+"if ( varpar->dc_menger_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	  public String getGPUFunctions(FlameTransformationContext context) {
	   return   "	  __device__ float  dc_menger_sdBox (float2 p, float2 s) {"
			   +"			p = abs(p)-( s);"
			   +"			return length(fmaxf(p, 0.0)) + fminf(fmaxf(p.x, p.y), 0.0);"
			   +"		}"
			   
			   +"		__device__ float  dc_menger_sdCross (float2 p) {"
			   +"			p = abs(p);"
			   +"			return fminf(p.x, p.y) - 1.0;"
			   +"		}"
			   
			   +"		__device__ float  dc_menger_sierpinskiCarpet (float2 p, float level ) {"
			   +"			float d =  dc_menger_sdBox (p, make_float2(1.0,1.0));"
			   +"			float s = 1.0;"
			   +"			for (int i = 0; i < level; i++) {"
			   +"				float2 a = mod(p*( s), 2.0)-( 1.0);"
			   +"				float2 r = make_float2(1.0,1.0)-( abs(a)*(3.0));"
			   +"				s *= 3.0;"
			   +"				float c =  dc_menger_sdCross (r) / s;"
			   +"				d = fmaxf(d, c);"
			   +"			}"
			   +"			return d;"
			   +"		}"
			   +"	  "
			   +"	__device__ float3  dc_menger_getRGBColor (float2 p, float level )"
			   +"	{"
			   +"		float3 col=make_float3(0.0,0.0,0.0);"
			   +"        float d= dc_menger_sierpinskiCarpet (p*(1.3), level);"
			   +"		col= d > 0.0 ? make_float3(0.0,0.0,0.0) : make_float3(1.0,1.0,1.0);"
			   +"		return col;"
			   +"	}";
	  }
}

