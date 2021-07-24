package org.jwildfire.create.tina.variation;


import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class DC_WarpingFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation :dc_warping
	 * Date: may 12, 2020
	 * Author: Jesus Sosa
	 * Reference & Credits:
	 *   
	 *   https://www.shadertoy.com/view/MdSXzz
	 */


	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED= "randomize";

	private static final String PARAM_X0= "x0";

	private static final String PARAM_ZOOM = "zoom";

 	
 	int seed=0;

    double x0=0;
//    double y0=0;
    double zoom=1.0;

    
	Random randomize=new Random(seed);
	
 	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_ZOOM,PARAM_X0};

	
	mat2 m = new mat2( 0.80,  0.60, -0.60,  0.80 );

	double hash( vec2 p )
	{
		double h = G.dot(p,new vec2(127.1,311.7));
	    return -1.0 + 2.0*G.fract(sin(h)*43758.5453123);
	}

	double noise( vec2 p )
	{
	    vec2 i = G.floor( p );
	    vec2 f = G.fract( p );
		vec2 t0= new vec2(3.0).minus(new vec2(2.0)).multiply(f);
		vec2 u = f.multiply(f).multiply(t0);  

	    return G.mix( G.mix( hash( i.plus( new vec2(0.0,0.0)) ), 
	                     hash( i.plus( new vec2(1.0,0.0)) ), u.x),
	                G.mix( hash( i.plus( new vec2(0.0,1.0)) ), 
	                     hash( i.plus(new vec2(1.0,1.0)) ), u.x), u.y);
	}

	double fbm( vec2 p )
	{
	    double f = 0.0;
	    f += 0.5000*noise( p ); 
	    p = m.times(p.multiply(new vec2(2.02)));
	    f += 0.2500*noise( p );
	    p = m.times(p.multiply(new vec2(2.03)));
	    f += 0.1250*noise( p );
	    p = m.times(p.multiply(new vec2(2.01)));
	    f += 0.0625*noise( p );
	    return f/0.9375;
	}

	vec2 fbm2( vec2 p )
	{
	    return new vec2( fbm(new vec2(p.x,p.y)), fbm(new vec2(p.y,p.x)) );
	}

	vec3 map( vec2 p )
	{   
	    p = p.multiply(new vec2(0.7));

	    vec2 t1=p.multiply(4.);
	    
	    //vec2 t2=  p.plus( fbm2((p.plus( fbm2(t1))).multiply(2.0)).plus(new vec2(-1.*time ))).plus(1.0*time);
	    vec2 t2=  p.plus( fbm2((p.plus( fbm2(t1))).multiply(2.0)));
	    
	    vec2 t0=fbm2(t2 );
	    double f=G.dot(t0 , new vec2(1.0,-1.0) );
	    double bl = G.smoothstep( -0.8, 0.8, f );

	    double ti = G.smoothstep( -1.0, 1.0, fbm(p) );

	    return G.mix( G.mix( new vec3(0.50,0.00,0.00), 
	                     new vec3(1.00,0.75,0.35), ti ), 
	                     new vec3(0.00,0.00,0.02), bl );
	}
	
	public vec3 getRGBColor(double xp,double yp)
	{
	    vec2 p =new vec2(xp*zoom,yp*zoom);
	    
        p=p.plus(new vec2(x0,x0));
        	    
	    double e = 0.0045;

	    vec3 colc = map( p               ); 
	    double gc = G.dot(colc,new vec3(0.333));
	    vec3 cola = map( p.plus(new vec2(e,0.0)) );
	    double ga = G.dot(cola,new vec3(0.333));
	    vec3 colb = map( p.plus(new vec2(0.0,e)) );
	    double gb = G.dot(colb,new vec3(0.333));
	    
	    vec3 nor = G.normalize( new vec3(ga-gc, e, gb-gc ) );

	    vec3 col = colc;
	    col = col.plus(new  vec3(1.0,0.7,0.6).multiply(8.0*G.abs(2.0*gc-ga-gb)));      
	    col = col.multiply(1.0+0.2*nor.y*nor.y); ;
	    col = col.plus(0.05*nor.y*nor.y*nor.y);  
	    
	    
	    vec2 q = new vec2((xp+1.)/2.0,(yp+1.)/2.0);
	    col = col.multiply(G.pow(16.0*q.x*q.y*(1.0-q.x)*(1.0-q.y),0.1)); 
	    
        return new vec3(col.x,col.y,col.z);
	}
	
	    
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
	   }

	  
	public String getName() {
		return "dc_warping";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //
		return joinArrays(new Object[] { seed,zoom,x0},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SEED)) {
			   seed=(int)pValue;
			   randomize=new Random(seed);
               x0=2.0*randomize.nextDouble()-1.0;
//               y0=2.0*randomize.nextDouble()-1.0;
		}
		else if (pName.equalsIgnoreCase(PARAM_X0)) {
			x0 =pValue;
		}
//		else if (pName.equalsIgnoreCase(PARAM_Y0)) {
//			y0 =pValue;
//		}
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_warping_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_warping_zoom + make_float2(__dc_warping_x0,__dc_warping_x0);"
	    		+"color=dc_warping_getRGBColor(uv);"
	    		+"if( __dc_warping_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_warping_Gradient ==1 )"
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
	    		+"else if( __dc_warping_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_warping*x;"
	    		+"__py+= __dc_warping*y;"
	    		+"float dz = z * __dc_warping_scale_z + __dc_warping_offset_z;"
	    		+"if ( __dc_warping_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {

		 return   "	__device__ float  dc_warping_hash ( float2 p )"
				 +"	{"
				 +"		float h = dot(p,make_float2(127.1,311.7));"
				 +"	    return -1.0 + 2.0*fract(sinf(h)*43758.5453123);"
				 +"	}"
				 
				 +"	__device__ float  dc_warping_noise ( float2 p )"
				 +"	{"
				 +"	    float2 i = floorf( p );"
				 +"	    float2 f = fract( p );"
				 +"		float2 t0= make_float2(3.0,3.0)-make_float2(2.0,2.0)*f;"
				 +"		float2 u = f*f*t0;"
				 +"	    return mix( mix(  dc_warping_hash ( i+( make_float2(0.0,0.0)) ), "
				 +"	                      dc_warping_hash ( i+( make_float2(1.0,0.0)) ), u.x),"
				 +"	                mix(  dc_warping_hash ( i+( make_float2(0.0,1.0)) ), "
				 +"	                      dc_warping_hash ( i+(make_float2(1.0,1.0)) ), u.x), u.y);"
				 +"	}"
				 
				 +"	__device__ float  dc_warping_fbm ( float2 p )"
				 +"	{"
				 +"	    float f = 0.0;"
				 +"	    Mat2 m;"
				 +"     Mat2_Init(&m, 0.80,  0.60, -0.60,  0.80 );"
				 +"	    f += 0.5000* dc_warping_noise ( p ); "
				 +"     float2 tmp=p*make_float2(2.02,2.02);"
				 +"	    p = times(&m,tmp);"
				 +"	    f += 0.2500* dc_warping_noise ( p );"
				 +"     tmp=p*make_float2(2.03,2.03);"
				 +"	    p = times(&m,tmp);"
				 +"	    f += 0.1250* dc_warping_noise ( p );"
				 +"     tmp=p*make_float2(2.01,2.01);"
				 +"	    p = times(&m,tmp);"
				 +"	    f += 0.0625* dc_warping_noise ( p );"
				 +"	    return f/0.9375;"
				 +"	}"
				 
				 +"	__device__ float2  dc_warping_fbm2 ( float2 p )"
				 +"	{"
				 +"	    return make_float2(  dc_warping_fbm (make_float2(p.x,p.y)),  dc_warping_fbm (make_float2(p.y,p.x)) );"
				 +"	}"
				 
				 +"	__device__ float3  dc_warping_map ( float2 p )"
				 +"	{   "
				 +"	    p = p*make_float2(0.7,0.7);"
				 +"	    float2 t1=p*4.0;"
				 +"	    float2 t2=  p+(  dc_warping_fbm2 ((p+(  dc_warping_fbm2 (t1)))*(2.0)));"
				 +"	    float2 t0= dc_warping_fbm2 (t2 );"
				 +"	    float f=dot(t0 , make_float2(1.0,-1.0) );"
				 +"	    float bl = smoothstep( -0.8, 0.8, f );"
				 +"	    float ti = smoothstep( -1.0, 1.0,  dc_warping_fbm (p) );"
				 +"	    return mix( mix( make_float3(0.50,0.00,0.00), "
				 +"	                     make_float3(1.00,0.75,0.35), ti ), "
				 +"	                     make_float3(0.00,0.00,0.02), bl );"
				 +"	}"

				 +"	__device__ float3 dc_warping_getRGBColor (float2 p) {"
				 +"     float3 col=make_float3(0.0,1.0,1.0);"
				 +"	    float e = 0.0045;"
				 +"	    float3 colc =  dc_warping_map (p);"
				 +"	    float gc = dot(colc,make_float3(0.333,0.333,0.333));"
				 +"	    float3 cola =  dc_warping_map ( p+make_float2(e,0.0) );"
				 +"	    float ga = dot(cola,make_float3(0.333,0.333,0.333));"
				 +"	    float3 colb =  dc_warping_map ( p+(make_float2(0.0,e)) );"
				 +"	    float gb = dot(colb,make_float3(0.333,0.333,0.333));"
				 +"	    float3 nor = normalize( make_float3(ga-gc, e, gb-gc ) );"
				 +"	    col = colc;"
				 +"	    col = col+make_float3(1.0,0.7,0.6)*8.0*fabsf(2.0*gc-ga-gb);"
				 +"	    col = col*(1.0 + 0.2*nor.y*nor.y);"
				 +"	    col = col+(0.05*nor.y*nor.y*nor.y);"
				 +"	    float2 q = make_float2((p.x+1.)/2.0,(p.y+1.)/2.0);"
				 +"	    col = col*powf(16.0*q.x*q.y*(1.0-q.x)*(1.0-q.y),0.1);"
				 +"     return col;"
				 +"	}";
	 }	
}

