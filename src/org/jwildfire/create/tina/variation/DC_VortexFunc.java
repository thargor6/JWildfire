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



public class DC_VortexFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation :dc_vortex
	 * Date: may 12, 2020
	 * Author: Jesus Sosa
	 * Reference & Credits:
	 *   
	 *   https://www.shadertoy.com/view/MlS3Rh
	 */


	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED= "randomize";

	private static final String PARAM_TIME="time";
	private static final String PARAM_STRENGTH="flowStrength";
	private static final String PARAM_SOFTNESS="Blending";
	private static final String PARAM_ZOOM = "zoom";

 	
 	int seed=0;
	double time=0.0;
	int strength=10;
	double softness=5.0;
    double zoom=1.0;

    

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
 	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_STRENGTH,PARAM_SOFTNESS,PARAM_ZOOM};

	 vec4 cHashA4 = new vec4 (0., 1., 57., 58.);
	 vec3 cHashA3 = new vec3 (1., 57., 113.);
	 double cHashM = 43758.54;

vec4 Hashv4f (double p)
{
  return G.fract (G.sin (cHashA4.add(p)).multiply( cHashM));
}

double Noisefv2 (vec2 p)
{
  vec2 i = G.floor (p);
  vec2 f = G.fract (p);
  f = f.multiply(f).multiply( (new vec2(3.).minus( f.multiply(2.))));
  vec4 t = Hashv4f (G.dot (i, new vec2(cHashA3.x,cHashA3.y)));
  return G.mix (G.mix (t.x, t.y, f.x), G.mix (t.z, t.w, f.x), f.y);
}

double Fbm2 (vec2 p)
{
  double s = 0.;
  double a = 1.;
  for (int i = 0; i < 6; i ++) {
    s += a * Noisefv2 (p);
    a *= 0.5;
    p =p.multiply(2.);
  }
  return s;
}
	


vec2 VortF (vec2 q, vec2 c)
{
  vec2 d = q.minus( c);
  return new vec2 (d.y, - d.x).division((G.dot (d, d) + 0.05)).multiply(-0.25);
}

vec2 FlowField (vec2 q)
{
  vec2 vr, c;
  double dir = 1.;
  c = new vec2 (G.mod (time, 10.) - 20., 0.6 * dir);
  vr = new vec2 (0.);
  for (int k = 0; k < 30; k ++) {
    vr = vr.plus(VortF ( q.multiply(4.), c).multiply(dir));
    c = new vec2 (c.x + 1., - c.y);
    dir = - dir;
  }
  return vr;
}
	
	public vec3 getRGBColor(double xp,double yp)
	{
	    vec2 uv =new vec2(xp*zoom,yp*zoom);
	    
 //       uv=uv.plus(new vec2(x0,y0));
        	    
        vec2 p = uv;
        for (int i = 0; i < strength ; i ++) 
        	p = p.minus(FlowField (p).multiply( 0.03));
        vec3 col =  new vec3 (0.5, 0.5, 0.5).multiply(Fbm2 (p.multiply(softness).plus(new vec2 (-0.1 * time, 0.))));
	    
        return new vec3(col.x,col.y,col.z);
	}
	
	    
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
	   }

	  
	public String getName() {
		return "dc_vortex";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //
		return joinArrays(new Object[] { seed,time,strength,softness,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SEED)) {
			   seed =   (int)pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
		time =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_STRENGTH)) {
			strength =(int) Tools.limitValue(pValue, 1 , 15);
		}
		else if (pName.equalsIgnoreCase(PARAM_SOFTNESS)) {
			softness =Tools.limitValue(pValue, 1. , 5.);
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_vortex_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_vortex_zoom;"
	    		+"color=dc_vortex_getRGBColor(uv,__dc_vortex_time,__dc_vortex_flowStrength,__dc_vortex_Blending );"
	    		+"if( __dc_vortex_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_vortex_Gradient ==1 )"
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
	    		+"else if( __dc_vortex_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_vortex*x;"
	    		+"__py+= __dc_vortex*y;"
	    		+"float dz = z * __dc_vortex_scale_z + __dc_vortex_offset_z;"
	    		+"if ( __dc_vortex_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {

		 return   "__device__ float4  dc_vortex_Hashv4f  (float p)"
				 +"{"
				 +"	 float cHashM = 43758.54;"
				 +"	 float4 cHashA4 = make_float4 (0., 1., 57., 58.);"
				 +"  return fract (sinf (cHashA4 + p)* cHashM);"
				 +"}"
				 
				 +"__device__ float  dc_vortex_Noisefv2  (float2 p)"
				 +"{"
				 +"	 float4 cHashA4 = make_float4 (0., 1., 57., 58.);"
				 +"	 float3 cHashA3 = make_float3 (1., 57., 113.);"
				 +"  float2 i = floorf (p);"
				 +"  float2 f = fract (p);"
				 +"  f = f*f* (make_float2(3.,3.)- f*2.0);"
				 +"  float4 t =  dc_vortex_Hashv4f  (dot (i, make_float2(cHashA3.x,cHashA3.y)));"
				 +"  return mix (mix (t.x, t.y, f.x), mix (t.z, t.w, f.x), f.y);"
				 +"}"
				 
				 +"__device__ float  dc_vortex_Fbm2  (float2 p)"
				 +"{"
				 +"  float s = 0.0;"
				 +"  float a = 1.0;"
				 +"  for (int i = 0; i < 6; i ++) {"
				 +"    s += a *  dc_vortex_Noisefv2  (p);"
				 +"    a *= 0.5;"
				 +"    p =p*2.0;"
				 +"  }"
				 +"  return s;"
				 +"}"

				 +"__device__ float2  dc_vortex_VortF  (float2 q, float2 c)"
				 +"{"
				 +"  float2 d = q- c;"
				 +"  return make_float2 (d.y, - d.x)/((dot (d, d) + 0.05))*(-0.25);"
				 +"}"
				 
				 +"__device__ float2  dc_vortex_FlowField  (float2 q, float time)"
				 +"{"
				 +"  float2 vr, c;"
				 +"  float dir = 1.;"
				 +"  c = make_float2 (mod (time, 10.) - 20., 0.6 * dir);"
				 +"  vr = make_float2 (0.0,0.0);"
				 +"  for (int k = 0; k < 30; k ++) {"
				 +"    vr = vr+ dc_vortex_VortF  ( q*4., c)*dir;"
				 +"    c = make_float2 (c.x + 1., - c.y);"
				 +"    dir = - dir;"
				 +"  }"
				 +"  return vr;"
				 +"}"
				 
				 +"	__device__ float3  dc_vortex_getRGBColor (float2 uv, float time, float strength, float softness)"
				 +"	{"
				 +"        float2 p = uv;"
				 +"        for (int i = 0; i < strength ; i ++) "
				 +"        	p = p- dc_vortex_FlowField  (p, time)* 0.03;"
				 +"        float3 col =  make_float3 (0.5, 0.5, 0.5)* dc_vortex_Fbm2  ( p*softness+make_float2 (-0.1 * time, 0.) );"
				 +"        return col;"
				 +"	}";
	 }	
}

