package org.jwildfire.create.tina.variation;


import java.util.Random;
import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;

public class DC_MoebiusLogFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_moebiuslog
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_LOG = "Log";
	private static final String PARAM_MOEBIUS = "Moebius";
	private static final String PARAM_SCALE = "scale";
	private static final String PARAM_ANGLE = "angle";


	double zoom=7.0;
	private int seed = 10000;
	double time=0.0;
	private int Log = 1;
	private int Moebius = 1;
	double scale=5.0;
	double angle=5.0;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	

	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_SEED,PARAM_TIME,PARAM_LOG,PARAM_MOEBIUS,PARAM_SCALE,PARAM_ANGLE};



	public vec3 getRGBColor(double xp,double yp)
	{

		vec2 U=new vec2(xp,yp).multiply(zoom);

		vec2 z = U.minus( new vec2(-1.,0));
	       if(Moebius==1)
	       {
		      U.x -= .5;                         // Moebius transform
		      mat2 tmp= new mat2(z,new vec2(-z.y,z.x));
	          U = U.times( tmp ).division(G.dot(U, U));
	       }	             

       if(Log==1)               // offset   spiral, zoom       phase     // spiraling
	        U =   new vec2(0.5,-0.5).multiply(Math.log(G.length(U=U.add(.5)))).plus(new vec2(angle,1).multiply(G.atan2(U.y, U.x)/6.3));
	    	
	    vec3 color=new vec3(0.);// n  
	    
	    color =  color.plus(G.length(G.fract(U.multiply(scale))));
		
		return color;
	}
 	

	

	public String getName() {
		return "dc_moebiuslog";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,seed,time,Log,Moebius,scale,angle},super.getParameterValues());
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
		else if (pName.equalsIgnoreCase(PARAM_LOG)) {
			Log = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_MOEBIUS)) {
			Moebius = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_SCALE)) {
			scale = Tools.limitValue(pValue, 1.0 , 10.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_ANGLE)) {
			angle = Tools.limitValue(pValue, 1.0 , 10.0);
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
	    		+"if( __dc_moebiuslog_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_moebiuslog_zoom;"
	    		+"color=dc_moebiuslog_getRGBColor(uv,__dc_moebiuslog_Log,__dc_moebiuslog_Moebius,__dc_moebiuslog_angle,__dc_moebiuslog_scale);"
	    		+"if( __dc_moebiuslog_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_moebiuslog_Gradient ==1 )"
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
	    		+"else if( __dc_moebiuslog_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_moebiuslog*x;"
	    		+"__py+= __dc_moebiuslog*y;"
	    		+"float dz = z * __dc_moebiuslog_scale_z + __dc_moebiuslog_offset_z;"
	    		+"if ( __dc_moebiuslog_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	  public String getGPUFunctions(FlameTransformationContext context) {
	   return   "	__device__ float3  dc_moebiuslog_getRGBColor (float2 U,float Log, float Moebius,float angle, float scale)"
			   +"	{"
			   +"		float2 z = U- make_float2(-1.,0.0);"
			   +"	       if(Moebius==1.0)"
			   +"	       {"
			   +"		      U.x -= .5;"
			   +"             Mat2 tmp;"
			   +"		      Mat2_Init(&tmp,z.x,z.y,z.y,-z.x);"
			   +"	          U = times( &tmp,U )/(dot(U, U));"
			   +"	       }"
			   +"       if(Log==1.0)"
			   +"	        U =   make_float2(0.5,-0.5)*(logf(length(U=U+.5)))+(make_float2(angle,1.0)*(atan2(U.y, U.x)/6.3));"
			   +"	    float3 color=make_float3(0.,0.0,0.0);"
			   +"	    color =  color + length(fract(U*scale));"
			   +"		return color;"
			   +"	}";
	  }
}

