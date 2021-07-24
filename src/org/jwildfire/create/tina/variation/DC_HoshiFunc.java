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
import js.glsl.vec4;



public class DC_HoshiFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_hoshi
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_STEPS = "steps";
	private static final String PARAM_SCALE = "scale";
	private static final String PARAM_TRANSLATE = "translate";




	int seed=100000;
	double time=10.0;
	int steps=28;
	double sc=1.25;
	double translt=1.5;

	vec2 fold = new vec2(-0.5, -0.5);
	vec2 translate = new vec2(translt);
	double scale = sc;
	

	Random randomize=new Random(seed);
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_STEPS,PARAM_SCALE,PARAM_TRANSLATE};



	public vec3 hsv(double h,double s,double v) 
	{
		vec3 t1=new vec3(3.,2.,1.);
		t1=t1.add(h).division(3.0);
		vec3 t2=G.fract(t1).multiply(6.0).minus(3.0);
		t2=G.abs(t2).minus(1.0);
		t2=G.clamp(t2,0.,1.);
		return G.mix(new vec3(3.1),t2,s).multiply(v);
	}

	public vec2 rotate(vec2 p, double a){
		return new vec2(p.x*G.cos(a)-p.y*G.sin(a), p.x*G.sin(a)+p.y*G.cos(a));
	}
	

	
	public vec3 getRGBColor(double xp,double yp)
	{
		vec2 p = new vec2( xp, yp);
		
		p = p.multiply(0.182);
		
		double x = p.y;
		p = G.abs(G.mod(p, 4.0).minus(2.0));
		for(int i = steps ; i > 0; i--){
			p = G.abs(p.minus( fold)).plus(fold);
			p = p.multiply(scale).minus( translate);
			p = rotate(p, 3.14159/(0.10+G.sin(time*0.0005+(double)i*0.5000001)*0.4999+0.5+(10./time)+G.sin(time)/100.));
		}
		double i = x*x + G.atan2(p.y, p.x) + time*0.02;
		double h = G.floor(i*4.0)/8.0 + 1.107;
		h += G.smoothstep(-0.1, 0.8, G.mod(i*2.0/5.0, 1.0/4.0)*900.0)/0.010 - 0.5;
		vec3 color=hsv(h, 1.0, G.smoothstep(-3.0, 3.0, G.length(p)*1.0));
		return color;
	}
 	

	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) 
	{
		 translate = new vec2(translt);
		 scale = sc;
	}

	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) 
	{

        vec3 color=new vec3(0.0); 
		 vec2 uV=new vec2(0.),p=new vec2(0.);
	       int[] tcolor=new int[3];  


		 
	     if(colorOnly==1)
		 {
			 uV.x=pAffineTP.x;
			 uV.y=pAffineTP.y;
		 }
		 else
		 {
	   			 uV.x=2.0*pContext.random()-1.0;
				 uV.y=2.0*pContext.random()-1.0;
		}
        
        color=getRGBColor(uV.x,uV.y);
        tcolor=dbl2int(color);
        
        //z by color (normalized)
        double z=greyscale(tcolor[0],tcolor[1],tcolor[2]);
        
        if(gradient==0)
        {
  	  	
    	  pVarTP.rgbColor  =true;;
    	  pVarTP.redColor  =tcolor[0];
    	  pVarTP.greenColor=tcolor[1];
    	  pVarTP.blueColor =tcolor[2];
    		
        }
        else if(gradient==1)
        {

            	Layer layer=pXForm.getOwner();
            	RGBPalette palette=layer.getPalette();      	  
          	    RGBColor col=findKey(palette,tcolor[0],tcolor[1],tcolor[2]);
          	    
          	  pVarTP.rgbColor  =true;;
          	  pVarTP.redColor  =col.getRed();
          	  pVarTP.greenColor=col.getGreen();
          	  pVarTP.blueColor =col.getBlue();

        }
        else 
        {
        	pVarTP.color=z;
        }

        pVarTP.x+= pAmount*(uV.x);
        pVarTP.y+= pAmount*(uV.y);
        
        
	    double dz = z * scale_z + offset_z;
	    if (reset_z == 1) {
	      pVarTP.z = dz;
	    }
	    else {
	      pVarTP.z += dz;
	    }
	}
	

	public String getName() {
		return "dc_hoshi";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed, time,steps,sc,translt},super.getParameterValues());
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
		else if (pName.equalsIgnoreCase(PARAM_STEPS)) {
			steps = (int)Tools.limitValue(pValue, 1 , 60);
		}
		else if (pName.equalsIgnoreCase(PARAM_SCALE)) {
			sc = Tools.limitValue(pValue, 1. , 5.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_TRANSLATE)) {
			translt = Tools.limitValue(pValue, 0. , 5.0);
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
	    		+"if( __dc_hoshi_ColorOnly ==1)"
	    		+"{"
	    		+"   x=__x;"
	    		+"   y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"    x=2.0*RANDFLOAT()-1.0;"
	    		+"    y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y);"
	    		+"color=dc_hoshi_getRGBColor(uv,__dc_hoshi_time,__dc_hoshi_steps,__dc_hoshi_scale,__dc_hoshi_translate);"
	    		+"if( __dc_hoshi_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_hoshi_Gradient ==1 )"
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
	    		+"else if( __dc_hoshi_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_hoshi*x;"
	    		+"__py+= __dc_hoshi*y;"
	    		+"float dz = z * __dc_hoshi_scale_z + __dc_hoshi_offset_z;"
	    		+"if ( __dc_hoshi_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";

	  }
	 
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "	__device__ float3  dc_hoshi_hsv (float h,float s,float v) "
	    		+"	{"
	    		+"		float3 t1=make_float3(3.,2.,1.);"
	    		+"		t1=t1 + h/3.0;"
	    		+"		float3 t2=fract(t1)*6.0-3.0;"
	    		+"		t2=abs(t2)-1.0;"
	    		+"		t2=clamp(t2,0.,1.);"
	    		+"		return mix(make_float3(3.1,3.1,3.1),t2,s)*v;"
	    		+"	}"
	    		
	    		+"	__device__ float2  dc_hoshi_rotate (float2 p, float a){"
	    		+"		return make_float2(p.x*cosf(a)-p.y*sinf(a), p.x*sinf(a)+p.y*cosf(a));"
	    		+"	}"
	    		
	    		+"	__device__ float3  dc_hoshi_getRGBColor (float2 p,float time, float steps,float scale,float translate)"
	    		+"	{"
	    		+"		p = p*0.182;"
	    		+"		float2 fold=make_float2(-0.5,-0.5);"
	    		+"		float x = p.y;"
	    		+"		p = abs(mod(p, 4.0)-2.0);"
	    		+"		for(int i = steps ; i > 0; i--){"
	    		+"			p = abs(p- fold)+fold;"
	    		+"			p = p*(scale)-( translate);"
	    		+"			p =  dc_hoshi_rotate (p, -PI/(0.10+sinf(time*0.0005+(float)i*0.5000001)*0.4999+0.5+(10./time)+sinf(time)/100.));"
	    		+"		}"
	    		+"		float i = x*x + atan2(p.y, p.x) + time*0.02;"
	    		+"		float h = floorf(i*4.0)/8.0 + 1.107;"
	    		+"		h += smoothstep(-0.1, 0.8, mod(i*2.0/5.0, 1.0/4.0)*900.0)/0.010 - 0.5;"
	    		+"		float3 color= dc_hoshi_hsv (h, 1.0, smoothstep(-3.0, 3.0, length(p)*1.0));"
	    		+"		return color;"
	    		+"	}";
	  }
}

