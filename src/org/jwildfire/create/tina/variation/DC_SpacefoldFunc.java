package org.jwildfire.create.tina.variation;


import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import java.util.Random;
import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;

public class DC_SpacefoldFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation :dc_spacefold
	 * Date: august 29, 2019
	 * Author: Jesus Sosa
	 * Reference & Credits:
	 *   https://www.shadertoy.com/view/lsGSzW
	 */


	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "randomize";
	private static final String PARAM_TIME= "time";
	private static final String PARAM_ZOOM = "zoom";

	

    double time=0.0;
    double zoom=2.5;
    
    int seed=1000;
	Random randomize=new Random(seed);
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_ZOOM};

	public mat2 rot(double angle) {
	    return new mat2(cos(angle), -sin(angle),
	                sin(angle), cos(angle));
	}

	public vec3 palette( double t )
	{
		// http://iquilezles.org/www/articles/palettes/palettes.htm
	    vec3 a = new vec3(0.5);
	    vec3 b= new vec3(0.5);
	    vec3 c= new vec3(1.0, 1.0, 0.5);
	    vec3 d= new vec3(0.8, 0.9, 0.3);
	    return a.plus(b.multiply(G.cos( (c.multiply(t).plus(d)).multiply(6.28318) )));
	}
	
	public vec3 getRGBColor(double xp,double yp)
	{
	    vec2 uv =new vec2(xp*zoom,yp*zoom);
        
        for(int i = 0; i < 64; i++) {
            uv = G.abs(uv);
            uv = uv.times(rot(time/30.0));
            uv = uv.minus(new vec2(0.5,0.5));
            uv = uv.multiply(1.03);
        }
        uv = G.pow(G.abs(G.sin(uv)),new vec2(0.3));
        vec3 color = palette(uv.x*uv.y*1.9);
          
        return color;
	 
	}
	
	    
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
	   }

	  
	public String getName() {
		return "dc_spacefold";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //
		return joinArrays(new Object[] { seed,time,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SEED)) {
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
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
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
	    		+"if( __dc_spacefold_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_spacefold_zoom;"
	    		+"color=dc_spacefold_getRGBColor(uv,__dc_spacefold_time);"
	    		+"if( __dc_spacefold_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_spacefold_Gradient ==1 )"
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
	    		+"else if( __dc_spacefold_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_spacefold*x;"
	    		+"__py+= __dc_spacefold*y;"
	    		+"float dz = z * __dc_spacefold_scale_z + __dc_spacefold_offset_z;"
	    		+"if ( __dc_spacefold_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {
		 return   "	__device__ Mat2  dc_spacefold_rot (float angle) {"
				 +"     Mat2 M;"
				 +"	    Mat2_Init(&M,cosf(angle), -sinf(angle),"
				 +"	                sinf(angle), cosf(angle));"
				 +"     return M;"
				 +"	}"
				 
				 +"	__device__ float3  dc_spacefold_palette ( float t )"
				 +"	{"
				 +"	    float3 a = make_float3(0.5,0.5,0.5);"
				 +"	    float3 b= make_float3(0.5,0.5,0.5);"
				 +"	    float3 c= make_float3(1.0, 1.0, 0.5);"
				 +"	    float3 d= make_float3(0.8, 0.9, 0.3);"
				 +"	    return a+(b*(cosf( (c*t+d)*6.28318 )));"
				 +"	}"

				 +"	__device__ float3  dc_spacefold_getRGBColor (float2 uv, float time)"
				 +"	{"
				 +"        for(int i = 0; i < 64; i++) {"
				 +"            uv = abs(uv);"
				 +"            Mat2 M= dc_spacefold_rot (time/30.0);"
				 +"            uv = times(&M,uv);"
				 +"            uv = uv-(make_float2(0.5,0.5));"
				 +"            uv = uv*1.03;"
				 +"        }"
				 +"        uv = pow(abs(sinf(uv)),make_float2(0.3,0.3));"
				 +"        float3 color =  dc_spacefold_palette (uv.x*uv.y*1.9);"
				 +"        return color;"
				 +"	}";
	 }	
}

