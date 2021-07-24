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
import js.glsl.vec4;



public class CutFunFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :cut_fun
	 * Date: august 29, 2019
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	

	int seed=1000;
	int mode=1;
    double zoom=10.0;
    int invert=0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    double x0=0.,y0=0.;
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_ZOOM,PARAM_INVERT};

	double rand (vec2 co)
	{ 
		return G.fract(G.sin(G.dot(new vec2(co.x,co.y) ,new vec2(12.9898,78.233))) * 43758.5453); 
	}

	double  pix(vec2 U)
	{
		return G.step(.5, rand(G.floor(U)));
	}

	double hm(vec2 uv){
		
		vec2 nuv = G.fract(uv).multiply(2.0).minus(1.),
		     pos = G.sign(nuv).multiply(.9);
		
		double d = G.length(nuv),	
		      v = pix(uv);
		
	    if (d<1.)
	    	return v;

		double v0 = v,
	          v1 = pix(new vec2(pos.x,0).plus(uv)),
		      v2 = pix(new vec2(0,pos.y).plus(uv));
		
		if (v1==v2)	v = v==pix(uv.plus(pos)) && v!=v1 ? 0. : v1;
			
		return G.mix(v0, v, G.smoothstep(d,1.,1.01));
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
		    	    
		    vec2 uv = new vec2(x*zoom,y*zoom);	
            double color=hm(new vec2(uv.x+x0,uv.y+y0));
		     
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
		  randomize=new Random(seed);
		  x0=seed*randomize.nextDouble();
		  y0=seed*randomize.nextDouble();
	   }

	  
	public String getName() {
		return "cut_fun";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed,mode, zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		if(pName.equalsIgnoreCase(PARAM_SEED))
		{
			   seed =   (int)pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
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
	    return  "		    float x,y,px_center,py_center,x0=0.0f,y0=0.0f;"
	    		+"		    "
	    		+"		    if( __cut_fun_mode ==0)"
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
	    		+"		    	    "
	    		+"		    float2 uv = make_float2(x* __cut_fun_zoom ,y* __cut_fun_zoom );	"
	    		+"          float color=cut_fun_hm (make_float2(uv.x+x0,uv.y+y0));"
	    		+"		     "
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_fun_invert ==0)"
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
	    		+"		    __px = __cut_fun * (x-px_center);"
	    		+"		    __py = __cut_fun * (y-py_center);"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_fun * __z;\n" : "");
	  }
	 
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return  "__device__	float  cut_fun_rand  (float2 co)"
	    		+"	{ "
	    		+"		return fract(sin(dot(make_float2(co.x,co.y) ,make_float2(12.9898,78.233))) * 43758.5453); "
	    		+"	}"
	    		+"__device__	float   cut_fun_pix (float2 U)"
	    		+"	{"
	    		+"		return step(.5,  cut_fun_rand (floorf(U)));"
	    		+"	}"
	    		+"__device__	float  cut_fun_hm (float2 uv){"
	    		+"		"
	    		+"		float2 nuv = fract(uv)*(2.0)-(1.),"
	    		+"		     pos = sign(nuv)*(.9);"
	    		+"		"
	    		+"		float d = length(nuv),	"
	    		+"		      v =  cut_fun_pix (uv);"
	    		+"		"
	    		+"	    if (d<1.)"
	    		+"	    	return v;"
	    		+"		float v0 = v,"
	    		+"	          v1 =  cut_fun_pix (make_float2(pos.x,0)+(uv)),"
	    		+"		      v2 =  cut_fun_pix (make_float2(0,pos.y)+(uv));"
	    		+"		"
	    		+"		if (v1==v2)	v = (v== cut_fun_pix (uv+(pos)) && v!=v1) ? 0.0f : v1;"
	    		+"			"
	    		+"		return mix(v0, v, smoothstep(d,1.,1.01));"
	    		+"	}" ;
	  }
}

