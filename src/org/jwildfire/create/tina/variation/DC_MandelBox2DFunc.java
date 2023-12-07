package org.jwildfire.create.tina.variation;


import java.util.Random;
import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;
import org.jwildfire.base.Tools;

public class DC_MandelBox2DFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_mandelbox2D
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

		vec2 I=new vec2(zoom*xp,zoom*yp);

		vec4 O=new vec4(I,-5.*G.cos(time*.1),1.);

	    double d=1.;
	    for(int i=0;i<20;i++){ //this mandelbox loop should also work in 3D(with a proper DE (initialize d at O.z,remove the +1.,and the factor 5 at the end)
	        I=G.clamp(I,-1.,1.).multiply(2.).minus(I);//boxfold
	        double b = (O.a=G.length(I))<.5?4.:O.a<1.?1./O.a:1.;//ballfold
	        I= I.multiply(O.z * b).plus(new vec2(O.x,O.y)); //scaling
	        d=b*d*G.abs(O.z)+1.;//bound distance estimation
	         }
	    d=Math.pow(G.length(I)/d,.1)*5.;
		O=new vec4(G.cos(d),G.sin(10.*d+1.),G.cos(3.*d+1.),0).multiply(0.5).add(0.5);
		return new vec3(O.r,O.g,O.b);
	}
 	

	

	public String getName() {
		return "dc_mandelbox2D";
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTS_BACKGROUND};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_mandelbox2D_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_mandelbox2D_zoom;"
	    		+"color=dc_mandelbox2D_getRGBColor(uv,__dc_mandelbox2D_time);"
	    		+"if( __dc_mandelbox2D_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_mandelbox2D_Gradient ==1 )"
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
	    		+"else if( __dc_mandelbox2D_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_mandelbox2D*x;"
	    		+"__py+= __dc_mandelbox2D*y;"
	    		+"float dz = z * __dc_mandelbox2D_scale_z + __dc_mandelbox2D_offset_z;"
	    		+"if ( __dc_mandelbox2D_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	  public String getGPUFunctions(FlameTransformationContext context) {
	   return   "	__device__ float3  dc_mandelbox2D_getRGBColor (float2 I, float time)"
			   +"	{"
			   +"		float4 O=make_float4(I.x,I.y,-5.0f*cosf(time*0.1f),1.0f);"
			   +"	    float d=1.0f,b;"
			   +"	    for(int i=0;i<20;i++){ "
			   +"	        I=clamp(I,-1.,1.)*2.0f - I;"
//			   +"	        float b = (O.w=length(I))<.5?4.:O.w<1.?1./O.w:1.;"
			   +"           O.w=length(I);"
			   +"           if(O.w < 0.5f)"
			   +"                b=4.0f;"
			   +"           else if(O.w< 1.0)"
			   +"                b=1.0f/O.w;"
			   +"           else"
			   +"                b=1.0;"

			   +"	        I= I*(O.z * b)+ make_float2(O.x,O.y); "
			   +"	        d=b*d*fabsf(O.z)+1.0f;"
			   +"	    }"
			   +"	    d=powf(length(I)/d,.1)*5.0;"
			   +"		O=make_float4(cosf(d),sinf(10.*d+1.),cosf(3.*d+1.),0.0)*0.5+0.5;"
			   +"		return make_float3(O.x,O.y,O.z);"
			   +"	}";
	  }
}

