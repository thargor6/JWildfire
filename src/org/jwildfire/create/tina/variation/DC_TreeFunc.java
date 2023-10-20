package org.jwildfire.create.tina.variation;

import java.util.Random;
import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;

public class DC_TreeFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_trees
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 * Credits: https://www.shadertoy.com/view/wslGz7
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_LEVELS = "levels";
	private static final String PARAM_THICKNESS = "thicknes";
	private static final String PARAM_STYLE = "style";
	private static final String PARAM_SHIFT = "shift";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";



	int levels=20;
	double thickness=1000.;
	double style=50.0;
	double shift=1.5;
	private int seed = 10000;
	double time=0.0;
	double zoom=4.0;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	private static final String[] additionalParamNames = { PARAM_LEVELS ,PARAM_THICKNESS,PARAM_STYLE,PARAM_SHIFT,PARAM_SEED,PARAM_TIME,PARAM_ZOOM};

	
	public vec2 po (vec2 v) {
		return new vec2(G.length(v),G.atan2(v.y,v.x));
	}
	
	public vec2 ca (vec2 u) {
		return new vec2(G.cos(u.y),G.sin(u.y)).multiply(u.x);
	}
	
	double ln (vec2 p, vec2 a, vec2 b) { 
	    double r = G.dot(p.minus(a),b.minus(a))/G.dot(b.minus(a),b.minus(a));
	    r = G.clamp(r,0.,1.);
	    p.x+=(0.7+0.5*G.sin(0.1*time))*0.2*G.smoothstep(1.,0.,G.abs(r*2.-1.))*G.sin(3.14159*(r-4.*time));

	    //      return (1.+0.5*r)*length(p-a-(b-a)*r);	     
	    vec2 t0=p.minus(a);
	    vec2 t1=(b.minus(a)).multiply(r);
	    
	    return (1.+0.5*r)*G.length( t0.minus(t1));
	}
	
	public vec3 getRGBColor(double xt,double yt)
	{

		vec2 U=new vec2(xt,yt).multiply(zoom);

	 	double r = 1e9;

		 	U.y += shift;
		 	vec3 col = new vec3(0.0);
		 	for (int k = 1; k < levels ; k++) {
		 		double s1=0.3*(G.sin(2.*time)+0.5*G.sin(4.53*time)+0.1*G.cos(12.2*time));
		 		vec2 t=po(U).add(new vec2(0,1).multiply(s1));
		        U = ca(t);
		        r = G.min(r,ln(U,new vec2(0),new vec2(0,1.)));
		        U.y-=1.;
		        
		        U.x=G.abs(U.x);
		        U= U.multiply(1.4+ 0.1*G.sin(time)+0.05*G.sin(0.2455*time)*((double)k));
		        U = po(U);
		        U.y += 1.+0.5*G.sin(0.553*time)*G.sin(G.sin(time)*(double)k)+0.1*G.sin(0.4*time)+0.05*G.sin(0.554*time);
		        U = ca(U);
		        double s0=G.exp(-thickness*r*r)*style;	        
//		        col=col.add(G.sin(new vec3(1,-1.8,1.9).multiply(s0).add(time)));
		        col=col.add(G.sin(new vec3(1.,1.,1.).multiply(s0)));
	
		 	}
//		 	col=col.division(18.);
		    return col;
	}
	

	public String getName() {
		return "dc_tree";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { levels,thickness,style,shift,seed,time,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_LEVELS)) {
			
			levels = (int)Tools.limitValue(pValue, 1 , 25);
		}
		else if (pName.equalsIgnoreCase(PARAM_THICKNESS)) {
			
			thickness = (int)Tools.limitValue(pValue, -1500. , 1500.);
		}
		else if (pName.equalsIgnoreCase(PARAM_STYLE)) {
			
			style = Tools.limitValue(pValue, 1. , 100.);
		}
		else if (pName.equalsIgnoreCase(PARAM_SHIFT)) {
			
			shift = Tools.limitValue(pValue, -100. , 100.);
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
	    		+"if( __dc_tree_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_tree_zoom;"
	    		+"color=dc_tree_getRGBColor(uv,__dc_tree_time,__dc_tree_shift,__dc_tree_levels,__dc_tree_style,__dc_tree_thicknes);"
	    		+"if( __dc_tree_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_tree_Gradient ==1 )"
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
	    		+"else if( __dc_tree_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_tree*x;"
	    		+"__py+= __dc_tree*y;"
	    		+"float dz = z * __dc_tree_scale_z + __dc_tree_offset_z;"
	    		+"if ( __dc_tree_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {
		 return   "	__device__ float2  dc_tree_po  (float2 v) {"
				 +"		return make_float2(length(v),atan2f(v.y,v.x));"
				 +"	}"

				 +"	__device__ float2  dc_tree_ca  (float2 u) {"
				 +"		return make_float2(cosf(u.y),sinf(u.y))*u.x;"
				 +"	}"

				 +"	__device__ float  dc_tree_ln  (float2 p, float2 a, float2 b,float time) { "
				 +"	    float r = dot(p-a,b-a)/dot(b-a,b-a);"
				 +"	    r = clamp(r,0.,1.);"
				 +"	    p.x += (0.7+0.5*sinf(0.1*time))*0.2*smoothstep(1.0 ,0.0 , fabsf(r*2.-1.))*sinf(3.14159*(r-4.*time));"
				 +"	    float2 t0=p-a;"
				 +"	    float2 t1=(b-a)*r;"
				 +"	    return (1.+ 0.5*r)*length( t0-t1);"
				 +"	}"

				 +"	__device__ float3  dc_tree_getRGBColor (float2 U, float time,float shift, float levels, float style, float thickness)"
				 +"	{"
				 +"	 	float r = 1.0e9;"
				 +"		 	U.y += shift;"
				 +"		 	float3 col = make_float3(0.0,0.0,0.0);"
				 +"		 	for (int k = 1; k <levels ; k++) {"
				 +"		 		float s1=0.3*(sinf(2.*time)+0.5*sinf(4.53*time)+0.1*cosf(12.2*time));"
				 +"		 		float2 t= dc_tree_po (U) + (make_float2(0.0,1.0)*(s1));"
				 +"		        U =  dc_tree_ca (t);"
				 +"		        r = fminf(r,dc_tree_ln(U,make_float2(0.0,0.0),make_float2(0.0,1.0),time));"
				 +"		        U.y-=1.;"
				 +"		        "
				 +"		        U.x=fabsf(U.x);"
				 +"		        U= U*(1.4+ 0.1*sinf(time)+0.05*sinf(0.2455*time)*((float)k));"
				 +"		        U =  dc_tree_po (U);"
				 +"		        U.y += 1.+0.5*sinf(0.553*time)*sinf(sinf(time)*(float)k)+0.1*sinf(0.4*time)+0.05*sinf(0.554*time);"
				 +"		        U =  dc_tree_ca (U);"
				 +"		        float s0=expf(-thickness*r*r)*style;"
				 +"		        col=col + sinf( make_float3(1.0,1.0,1.0)*s0 );"
				 +"		 	}"
				 +"		    return col;"
				 +"	}";
	 }	
}

