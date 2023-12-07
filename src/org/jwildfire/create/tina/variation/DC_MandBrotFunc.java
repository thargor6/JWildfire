package org.jwildfire.create.tina.variation;

import java.util.Random;
import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;

public class DC_MandBrotFunc  extends  DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_mandbrot
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_NITERS = "nIters";
	private static final String PARAM_N = "N";
	private static final String PARAM_COMPLEXITY = "Complexity";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_RED = "red";
	private static final String PARAM_GREEN = "green";
	private static final String PARAM_BLUE = "blue";



	


	int nIters=250;
	int N=8;
	double complexity=0.995;
	private int seed = 10000;
	double time=0.0;
	double zoom=4.0;

	double red=0.0314;
	double green= 0.02;
	double blue=0.011;
	


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	private static final String[] additionalParamNames = { PARAM_NITERS,PARAM_N,PARAM_COMPLEXITY ,PARAM_SEED,PARAM_TIME,PARAM_ZOOM,PARAM_RED,PARAM_GREEN,PARAM_BLUE};



      public vec3 mandelbrot(vec2 p) {
    	  vec2 s = p;
    	  double d = 0.0, l;
    		
    	  for (int i = 0; i < nIters; i++) {
    	    s = new vec2(s.x * s.x - s.y * s.y + p.x, 2.0 * s.x * s.y + p.y);
    	    l = G.length(s);
    	    d += l + 0.5;
    	    if (l > 2.0)
    	    	return new vec3(G.sin(d * red), G.sin(d *green ), G.sin(d * blue));
    	  }
    		
    	  return new vec3(0.0);
    	}
      
      public vec2 FRACTALIZE2(vec2 p, double time) {
    		double s = .5;
    		double cs = G.cos(time);
    		double sn = G.sin(time);
    		mat2 rot = new mat2(cs, sn, -sn, cs);
    		for (int i = 0; i < N ; i++) {
    			p = G.abs(p).division( G.dot(p, p)).minus(s);
    			p = p.times( rot);
    			s *= complexity;
    		}
    		return p;
    	}

	public vec3 getRGBColor(double xp,double yp)
	{

		double t=time/65.*10.;;
		
		vec2 p=new vec2(xp,yp).multiply(zoom);


		vec3 col=new vec3(0.0);

			p = p.multiply(G.fract(t * .0001) * 100. + 1.);
			p = FRACTALIZE2(p,t);
		  double f = G.sin(t * 0.10 + 99.0) * 0.5 + 0.5;
		  p = p.multiply(G.pow(1.5, f * (-31.0)));
		  p = p.plus(new vec2(-1.002029, 0.303864));
			
		  col = new vec3( new vec3(1.).minus( mandelbrot(p)));

		return col;
	}
 	
	

	public String getName() {
		return "dc_mandbrot";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { nIters,N,complexity,seed,time,zoom, red,green,blue},super.getParameterValues());
	}


	
	public void setParameter(String pName, double pValue) {
		 if (pName.equalsIgnoreCase(PARAM_NITERS)) {
			
			nIters = (int)Tools.limitValue(pValue, 10 , 1000);
		}
		else if (pName.equalsIgnoreCase(PARAM_N)) {
			
			N = (int)Tools.limitValue(pValue, 2 , 10);
		}
		else if (pName.equalsIgnoreCase(PARAM_COMPLEXITY)) {
			
			complexity = Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (PARAM_SEED.equalsIgnoreCase(pName)) 
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
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			
			zoom = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_RED)) {
			
			red = Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_GREEN)) {
			
			green = Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_BLUE)) {
			
			blue = Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else
			super.setParameter(pName,pValue);
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
	    		+"if( __dc_mandbrot_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_mandbrot_zoom;"
	    		+" color=dc_mandbrot_getRGBColor(uv,__dc_mandbrot_time,__dc_mandbrot_N,"
	    		+ "                                   __dc_mandbrot_nIters,__dc_mandbrot_Complexity,"
	    		+ "                                   __dc_mandbrot_red,__dc_mandbrot_green,"
	    		+ "                                   __dc_mandbrot_blue );"
	    		+"if( __dc_mandbrot_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
				+"   __colorA  = 1.0;"
	    		+"}"
				+"else if( __dc_mandbrot_Gradient ==1 )"
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
	    		+"else if( __dc_mandbrot_Gradient ==2 )"
	    		+"{"
				+"  int3 icolor=dbl2int(color);"
	    		+"  z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_mandbrot*x;"
	    		+"__py+= __dc_mandbrot*y;"
	    		+"float dz = z * __dc_mandbrot_scale_z + __dc_mandbrot_offset_z;"
	    		+"if ( __dc_mandbrot_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";

	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {

	    return   "__device__ float3 dc_mandbrot_mandelbrot(float2 p, float nIters, float red, float green, float blue) {"
	    		+"    	  float2 s = p;"
	    		+"    	  float d = 0.0, l;"
	    		+"    	  for (int i = 0; i < nIters; i++) {"
	    		+"    	    s = make_float2(s.x * s.x - s.y * s.y + p.x , 2.0 * s.x * s.y + p.y);"
	    		+"    	    l = length(s);"
	    		+"    	    d += l + 0.5;"
	    		+"    	    if (l > 2.0)"
	    		+"    	    	return make_float3(sinf(d *red), sinf(d *green ), sinf(d *blue));"
	    		+"    	  }"
	    		+"    	  return make_float3(0.0,0.0,0.0);"
	    		+"}"
	    		
	    		+"__device__ float2 dc_mandbrot_fractalize2 (float2 p, float time, float N, float complexity)"
	    		+"{"
	    		+"    float s = 0.5f;"
	    		+" 	  float cs = cosf(time);"
	    		+"    float sn = sinf(time);"
	    		+"    Mat2 rot;"
	    		+"    Mat2_Init(&rot,cs, sn, -sn, cs);"
	    		+"    for (int i = 0; i < (int) N ; i++)"
	    		+ "   {"
	    		+"    	p = abs(p)/ dot(p, p) - s;"
	    		+"    	p = times(&rot,p);"
	    		+"    	s *= complexity;"
	    		+"    }"
	    		+"    return p;"
	    		+"}"
	    		
	    		+"__device__ float3  dc_mandbrot_getRGBColor (float2 p,float time, float N, float nIters, float Complexity, float red,float green, float blue )"
	    		+"{"
	    		+"		float t=time/65.*10.;"
	    		+"		float3 col=make_float3(0.0,0.0,0.0);"
	    		+"			p = p*(fract(t * .0001) * 100. + 1.);"
	    		+"			p =  dc_mandbrot_fractalize2 (p,t, N, Complexity);"
	    		+"		  float f = sinf(t * 0.10 + 99.0) * 0.5 + 0.5;"
	    		+"		  p = p*(powf(1.5, f * (-31.0)));"
	    		+"		  p = p+make_float2(-1.002029, 0.303864);"
	    		+"		  col =  make_float3(1.0,1.0,1.0)- dc_mandbrot_mandelbrot(p, nIters, red,green,blue);"
	    		+"		return col;"
	    		+"}";
	  }
}

