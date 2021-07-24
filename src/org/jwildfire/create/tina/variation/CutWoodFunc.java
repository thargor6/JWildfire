package org.jwildfire.create.tina.variation;


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



public class CutWoodFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :cut_wood
	 * Date: august 29, 2019
	 * Reference & Credits:  https://www.shadertoy.com/view/ll3Xzs
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SHIFTX = "shiftX";
	private static final String PARAM_SHIFTY = "shiftY";
	private static final String PARAM_NOISEFRQ = "freq";
	private static final String PARAM_SMOOTHING = "smooth";
	private static final String PARAM_LINECOUNT = "LineCount";
	private static final String PARAM_LINEWIDTH = "LineWidth";
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	
	double x0=0;
	double y0=0.0;
	double noise_freq=1.8;
	double noise_smoothing=1.0;
	double line_count=20.0;
	double line_width=0.2;
	int mode=1;
    double zoom=2.5;
    int invert=0;

 	
    
	private static final String[] additionalParamNames = { PARAM_SHIFTX,PARAM_SHIFTY,PARAM_NOISEFRQ,PARAM_SMOOTHING,PARAM_LINECOUNT,PARAM_LINEWIDTH,PARAM_MODE,PARAM_ZOOM,PARAM_INVERT};

	

	double random (vec2 st) {
	    return G.fract(Math.sin(G.dot(new vec2(st.x,st.y),new vec2(12.9898,78.233)))*43758.5453123);
	}
	
	double random(vec2 st, double seed) {
	    return G.fract(Math.sin(seed + G.dot(new vec2(st.x,st.y), new vec2(12.9898,78.233)))* 43758.5453123);
	}

	double noise (vec2 c, double density) {
	    vec2 cu = G.fract(c.multiply(density));
	    vec2 ci = G.floor(c.multiply(density));
	    double bl = random(ci);
	    double br = random(ci.plus(new vec2(1.0, 0.0)));
	    double tl = random(ci.plus(new vec2(0.0, 1.0)));
	    double tr = random(ci.plus(new vec2(1.0, 1.0)));
	    cu = G.smoothstep(0., 1., cu);
	    double b = G.mix(bl, br, cu.x);
	    double t = G.mix(tl, tr, cu.x);
	    double v = G.mix(b, t, cu.y);
	    return v;
	}

	double line (vec2 c, double width) {
	    double smoothing = .04;
	    return G.smoothstep(.5 - width, .5 - width + smoothing, c.y) - G.smoothstep(.5 + width - smoothing, .5 + width, c.y);
	}

	public mat2 rotate (double angle) {
	    double c = Math.cos(angle);
	    double s = Math.sin(angle);
	    return new mat2(c, -s, s, c);
	}

	vec2 twist (vec2 c, double angle) {
	    return (c.minus(.5)).times(rotate(angle)).plus(.5);
	}
		 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    double x,y,px_center,py_center;
		    
		    if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		      px_center=0.0;
		      py_center=0.0;
		    }else
		    {
		     x=pContext.random();
		     y=pContext.random();
		      px_center=0.5;
		      py_center=0.5;		     
		    }
		    
		    vec2 st =new vec2(x*zoom,y*zoom);
            st=st.plus(new vec2(x0,y0));

    	    vec2 sti = G.floor(st);

    	       
    	    st = twist(st, Math.pow(noise(st, noise_freq), noise_smoothing));
    	    vec2 c = G.fract(st.multiply(new vec2(1.0, line_count)));
    	    
    	    double background=0.0;
    	    double foreground=1.0;
    	    double color = G.mix(background, foreground, line(c, line_width));
			              	
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color>0.0)
		      { x=0;
		        y=0;
		        pVarTP.doHide = true;	        
		      }
		    } else
		    {
			      if (color<=0.0)
			      { x=0;
			        y=0;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * (x-px_center);
		    pVarTP.y = pAmount * (y-py_center);
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	   }

	  
	public String getName() {
		return "cut_wood";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  x0,y0,noise_freq,noise_smoothing,line_count,line_width, mode,zoom,invert});
	}

	public void setParameter(String pName, double pValue) {

		if(pName.equalsIgnoreCase(PARAM_SHIFTX))
		{
			   x0=pValue;
		}
		else if(pName.equalsIgnoreCase(PARAM_SHIFTY))
		{
			   y0=pValue;
		}
		else if(pName.equalsIgnoreCase(PARAM_NOISEFRQ))
		{
			   noise_freq=Tools.limitValue(pValue, 0.1 , 2.0);
		}
		else if(pName.equalsIgnoreCase(PARAM_SMOOTHING))
		{
			   noise_smoothing=Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if(pName.equalsIgnoreCase(PARAM_LINECOUNT))
		{
			   line_count=Tools.limitValue(pValue, 1. , 50.0);
		}
		else if(pName.equalsIgnoreCase(PARAM_LINEWIDTH))
		{
			   line_width=Tools.limitValue(pValue, 0.1 , .5);
		}
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			invert =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else
		      throw new IllegalArgumentException(pName);
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		    float x,y,px_center,py_center;"
	    		+"		    "
	    		+"		    if( __cut_wood_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		      px_center=0.0;"
	    		+"		      py_center=0.0;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT();"
	    		+"		     y=RANDFLOAT();"
	    		+"		      px_center=0.5;"
	    		+"		      py_center=0.5;		     "
	    		+"		    }"
	    		+"		    "
	    		+"		    float2 st =make_float2(x* __cut_wood_zoom ,y* __cut_wood_zoom );"
	    		+"            st=st+(make_float2(  __cut_wood_shiftX  ,  __cut_wood_shiftY ));"
	    		+"    	    float2 sti = floorf(st);"
	    		+"    	       "
	    		+"    	    st = cut_wood_twist(st, pow(cut_wood_noise(st,  __cut_wood_freq ),  __cut_wood_smooth ));"
	    		+"    	    float2 c = fract(st*(make_float2(1.0,  __cut_wood_LineCount )));"
	    		+"    	    "
	    		+"    	    float background=0.0;"
	    		+"    	    float foreground=1.0;"
	    		+"    	    float color = mix(background, foreground, cut_wood_line(c,  __cut_wood_LineWidth ));"
//	    		+"			float color=1.0;"
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_wood_invert ==0)"
	    		+"		    {"
	    		+"		      if (color>0.0)"
	    		+"		      { x=0;"
	    		+"		        y=0;"
	    		+"		        __doHide = true;	        "
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color<=0.0)"
	    		+"			      { x=0;"
	    		+"			        y=0;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __cut_wood * (x-px_center);"
	    		+"		    __py = __cut_wood * (y-py_center);"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_wood * __z;\n" : "");
	  }
	 
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return  "__device__	float  cut_wood_random  (float2 st) {"
	    		+"	    return fract(sinf(dot(make_float2(st.x,st.y),make_float2(12.9898,78.233)))*43758.5453123);"
	    		+"	}"
	    		+"	"
	    		+"__device__	float  cut_wood_random (float2 st, float seed) {"
	    		+"	    return fract(sinf(seed + dot(make_float2(st.x,st.y), make_float2(12.9898,78.233)))* 43758.5453123);"
	    		+"	}"
	    		+"__device__	float  cut_wood_noise  (float2 c, float density) "
	    		+ "{"
	    		+"	    float2 cu = fract(c*(density));"
	    		+"	    float2 ci = floorf(c*(density));"
	    		+"	    float bl =  cut_wood_random (ci);"
	    		+"	    float br =  cut_wood_random (ci+(make_float2(1.0, 0.0)));"
	    		+"	    float tl =  cut_wood_random (ci+(make_float2(0.0, 1.0)));"
	    		+"	    float tr =  cut_wood_random (ci+(make_float2(1.0, 1.0)));"
	    		+"	    cu = smoothstep(0., 1., cu);"
	    		+"	    float b = mix(bl, br, cu.x);"
	    		+"	    float t = mix(tl, tr, cu.x);"
	    		+"	    float v = mix(b, t, cu.y);"
	    		+"	    return v;"
	    		+"	}"
	    		+"__device__	float  cut_wood_line  (float2 c, float width) "
	    		+"  {"
	    		+"	    float smoothing = .04;"
	    		+"	    return smoothstep(.5 - width, .5 - width + smoothing, c.y) - smoothstep(.5 + width - smoothing, .5 + width, c.y);"
	    		+"	}"
	    		+"	__device__ Mat2  cut_wood_rotate  (float angle)"
	    		+"  {"
	    		+"	    float c = cosf(angle);"
	    		+"	    float s = sinf(angle);"
	    		+"      Mat2 mat;"
	    		+"	    Mat2_Init(&mat,c, -s, s, c);"
	    		+"      return mat;"
	    		+"	}"
	    		+"__device__ float2  cut_wood_twist  (float2 c, float angle)"
	    		+"  {"
	    		+"    Mat2 m;"
	    		+"    m = cut_wood_rotate (angle);"
	    		+ "   float2 t0= times(&m,c-0.5f);"
	    		+"	  return t0 + 0.5f;"
	    		+"	}";
	  }
}

