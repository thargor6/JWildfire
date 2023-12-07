package org.jwildfire.create.tina.variation;

import java.util.Random;
import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;
import org.jwildfire.base.Tools;

public class DC_KaleidoComplexFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_kaleidocomplex
	 * Date: February 12, 2019
     * Author: Jesus Sosa
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_IMAX = "iMax";
	private static final String PARAM_COLOR = "color";
	private static final String PARAM_FR = "redF";
	private static final String PARAM_FG = "greenF";
	private static final String PARAM_FB = "blueF";



	int seed=1000000;

	double time=0.0;
    int iMax=12;
    double fc=2.0;
    double FR=1.0,FG=1.0,FB=1.0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;



	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_IMAX,PARAM_COLOR,
			PARAM_FR,PARAM_FG,PARAM_FB};

	
	public vec2 	cmult(vec2 a, vec2 b)
	{
	    return (new vec2(a.x * b.x - a.y * b.y, a.x * b.y + a.y * b.x));
	}

	public vec3 getRGBColor(double xp,double yp)
	{

		
		vec2 uv = new vec2( xp, yp);
		
        
	    vec4	z = new vec4(0.0, 0.0, 0.0, 0.0);
	    vec2	of = new vec2( G.abs(uv.x )/0.125, G.abs(uv.y )/0.125).multiply(1.0);
	    vec3	col = new vec3(0.0);
	    vec2	dist = new vec2(0.0);
	    // z.xy=of;
	    z.x = of.x;
	    z.y = of.y;
	    int ii = -1;

	    double	r = 1.;
	    for (int i = -1; i < iMax; ++i)
	    {
	        r*=-1.;
	        ++ii;
//	        z.xy = (double)ii*.1251*0.+cmult(new vec2(z.x,z.y), new vec2(1.,1.)).xy;
	        vec2 t1=cmult(new vec2(z.x,z.y), new vec2(1.,1.));
	        z.x = t1.x;
	        z.y = t1.y;
	        
//	        z.xy = abs(z.xy)-10.5+r;
	        t1=G.abs(new vec2(z.x,z.y)).plus(r-10.5);
	        z.x = t1.x;
	        z.y = t1.y;
	        
//			z.xy = cmult(z.xy, vec2(sin(t*1.), cos(t*1.) )-0.*vec2(1., -1.) );	
	        t1=cmult(new vec2(z.x,z.y), new vec2(Math.sin(time), Math.cos(time) ) );
	        z.x = t1.x;	
	        z.y = t1.y;	
	        z.z = fc * (z.x*z.z - z.y*z.w);
	        z.w = fc * (z.y*z.z - z.x*z.w);
	        dist.x = G.dot(new vec2(z.x,z.y),new vec2(z.x,z.y));
			dist.y = G.dot(new vec2(z.z,z.w),new vec2(z.z,z.w));
	        if ( (double) ii > 0.  && ( Math.sqrt(z.x*z.x ) < .51    ||   Math.sqrt(z.y*z.y ) < .51  ) )
	        {
	         col.x = Math.exp(-Math.abs(z.x*z.x*z.y) ); // expensive but pretty
	    	 col.y = Math.exp(-Math.abs(z.y*z.x*z.y) );
	    	 col.z = Math.exp(-Math.abs(G.min(z.x,z.y)));
	            break;
	        }

	     	col.x += fc*Math.exp(-Math.abs(-z.x/(double)ii+(double)ii/z.x)); // expensive but pretty
	    	col.y += fc*Math.exp(-Math.abs(-z.y/(double)ii+(double)ii/z.y));
	    	col.z += fc*Math.exp(-Math.abs((-z.x-z.y)/(double)ii+(double)ii/(z.x+z.y)));
	        if (dist.x > 10000000.0 || dist.y > 100000000000.0)
	            break;
	    }
	    col=new vec3( G.cos(col.x*FR), G.cos(col.y*FG), G.cos(col.z*FB));
		return col;
	}
 	
	

	public String getName() {
		return "dc_kaleidocomplex";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed, time,iMax,fc,FR,FG,FB},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		 if (PARAM_SEED.equalsIgnoreCase(pName)) 
		{	    seed =   (int)pValue;
	       randomize=new Random(seed);
	          long current_time = System.currentTimeMillis();
	          elapsed_time += (current_time - last_time);
	          last_time = current_time;
	          time = (double) (elapsed_time / 1000.0);
	    }
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_IMAX)) {
			iMax =(int)Tools.limitValue(pValue, 1 , 50);
		}
		else if (pName.equalsIgnoreCase(PARAM_COLOR)) {
			fc=pValue; 
		}
		else if (pName.equalsIgnoreCase(PARAM_FR)) {
			FR =Tools.limitValue(pValue, 0.0 , 5.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_FG)) {
			FG =Tools.limitValue(pValue, 0.0 , 5.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_FB)) {
			FB =Tools.limitValue(pValue, 0.0 , 5.0);
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
	    		+"if( __dc_kaleidocomplex_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y);"
	    		+"color=dc_kaleidocomplex_getRGBColor(uv,__dc_kaleidocomplex_time,__dc_kaleidocomplex_iMax,"
	    		+"                                     __dc_kaleidocomplex_color,"
	    		+ "                                    __dc_kaleidocomplex_redF,__dc_kaleidocomplex_greenF,__dc_kaleidocomplex_blueF);"
	    		+"if( __dc_kaleidocomplex_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_kaleidocomplex_Gradient ==1 )"
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
	    		+"else if( __dc_kaleidocomplex_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_kaleidocomplex*x;"
	    		+"__py+= __dc_kaleidocomplex*y;"
	    		+"float dz = z * __dc_kaleidocomplex_scale_z + __dc_kaleidocomplex_offset_z;"
	    		+"if ( __dc_kaleidocomplex_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {

		 return   "	__device__ float2 dc_kaleidocomplex_cmult (float2 a, float2 b)"
				 +"	{"
				 +"	    return make_float2(a.x * b.x - a.y * b.y, a.x * b.y + a.y * b.x);"
				 +"	}"
				 +"	__device__ float3  dc_kaleidocomplex_getRGBColor (float2 uv, float time, float iMax, float fc,float FR, float FG, float FB)"
				 +"	{"
				 +"	    vec4	z  =  make_float4(0.0, 0.0, 0.0, 0.0);"
				 +"	    float2	of =  make_float2( abs(uv.x )/0.125, abs(uv.y )/0.125);"
				 +"	    float3	col = make_float3(0.0,0.0,0.0);"
				 +"	    float2	dist= make_float2(0.0,0.0);"
				 +"	    "
				 +"	    z.x = of.x;"
				 +"	    z.y = of.y;"
				 +"	    int ii = -1;"
				 +"	    float	r = 1.0f;"
				 +"	    for (int i = -1; i < (int) iMax; ++i)"
				 +"	    {"
				 +"	        r=-r;"
				 +"	        ii=ii+1;"
				 +""
				 +"	        float2 t1= dc_kaleidocomplex_cmult (make_float2(z.x,z.y), make_float2(1.,1.));"
				 +"	        z.x = t1.x;"
				 +"	        z.y = t1.y;"
				 +"	        "
				 +""
				 +"	        t1=abs(make_float2(z.x,z.y))+(r-10.5);"
				 +"	        z.x = t1.x;"
				 +"	        z.y = t1.y;"
				 +"	        "
				 +""
				 +"	        t1= dc_kaleidocomplex_cmult (make_float2(z.x,z.y), make_float2(sinf(time), cosf(time) ) );"
				 +"	        z.x = t1.x;	"
				 +"	        z.y = t1.y;	"
				 +"	        z.z = fc * (z.x*z.z - z.y*z.w);"
				 +"	        z.w = fc * (z.y*z.z - z.x*z.w);"
				 +"	        dist.x = dot(make_float2(z.x,z.y),make_float2(z.x,z.y));"
				 +"			dist.y = dot(make_float2(z.z,z.w),make_float2(z.z,z.w));"
				 +"	        if ( (float) ii > 0.  && ( sqrtf(z.x*z.x ) < 0.51f    ||   sqrtf(z.y*z.y ) < 0.51f  ) )"
				 +"	        {"
				 +"	         col.x = expf(-abs(z.x*z.x*z.y) ); "
				 +"	    	 col.y = expf(-abs(z.y*z.x*z.y) );"
				 +"	    	 col.z = expf(-abs(fminf(z.x,z.y)));"
				 +"	            break;"
				 +"	        }"
				 +"	     	col.x += fc*expf(-abs(-z.x/(float)ii+(float)ii/z.x)); "
				 +"	    	col.y += fc*expf(-abs(-z.y/(float)ii+(float)ii/z.y));"
				 +"	    	col.z += fc*expf(-abs((-z.x-z.y)/(float)ii+(float)ii/(z.x+z.y)));"
				 +"	        if (dist.x > 10000000.0 || dist.y > 100000000000.0)"
				 +"	            break;"
				 +"	    }"
				 +"	    col=make_float3( cosf(col.x*FR), cosf(col.y*FG), cosf(col.z*FB));"
				 +"		return col;"
				 +"	}";
	 }
}

