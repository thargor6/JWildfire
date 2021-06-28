package org.jwildfire.create.tina.variation;


import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class CutJigsawFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :cut_jigsaw
	 * Date: october 19, 2019
	 * Author: Jesus Sosa
	 * Reference & Credits: https://www.shadertoy.com/view/wddGzj
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_TIME = "time";
	private static final String PARAM_MODE = "mode";	
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	
	int seed=1000;
	double time=0.0;
    double zoom=4.0;
    int mode=1;
    int invert=0;


	Random randomize=new Random(seed);
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    double x0=0.,y0=0.;
    double temp;
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_MODE,PARAM_ZOOM,PARAM_INVERT};
	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    double x,y;
		    
		    if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		    }else
		    {
		     x=pContext.random()-0.5;
		     y=pContext.random()-0.5;	     
		    }
		    
    	    vec2 u =new vec2(x*zoom,y*zoom).plus(new vec2(1.,0.0).multiply(time));
    	    vec2 i = G.ceil(u),m = G.mod(i,2.).minus(.5);
    	    u=u.minus(i.minus(0.5));
            vec2 D = G.dot(m, u) < 0. ? m.multiply(-1.0) : m;           
    	    
            double f = G.dot( G.abs(u.minus(D)), new vec2(1.0)) *.7 -.65; 
            double c = G.length(u.minus(D.multiply(.2))) - .2; 
            double d=Math.sin(G.dot(i,new vec2(27, 57)))*1e5;
            boolean q= (G.fract(d)-.5)*D.x < 0.;
            double t= q ? Math.max(f,.1-c) : f;
            double s=Math.min(t, c);
        	            
            double color=1.0-s*1e4/8.0;
              	
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
		    pVarTP.x = pAmount * (x);
		    pVarTP.y = pAmount * (y);
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
		return "cut_jigsaw";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed, time, mode, zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		if(pName.equalsIgnoreCase(PARAM_SEED))
		{
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
	    return   "		    float x,y;"
	    		+"		    "
	    		+"		    if( varpar->cut_jigsaw_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT()-0.5;"
	    		+"		     y=RANDFLOAT()-0.5;"
	    		+"		    }"
	    		+"		    "
	    		+"    	    float2 u =make_float2(x* varpar->cut_jigsaw_zoom ,y* varpar->cut_jigsaw_zoom )+(make_float2(1.,0.0)*( varpar->cut_jigsaw_time ));"
	    		+"    	    float2 i = ceilf(u),m = mod(i,2.)-(.5);"
	    		+"    	    u=u-(i-(0.5));"
	    		+"            float2 D = dot(m, u) < 0. ? m*(-1.0) : m;           "
	    		+"    	    "
	    		+"            float f = dot( abs(u-(D)), make_float2(1.0,1.0)) *.7 -.65; "
	    		+"            float c = length(u-(D*(.2))) - .2; "
	    		+"            float d=sin(dot(i,make_float2(27.0f, 57.0f)))*1.0e5;"
	    		+"            short q= (fract(d)-.5)*D.x < 0.;"
	    		+"            float t= q ? fmaxf(f,.1-c) : f;"
	    		+"            float s=fminf(t, c);"
	    		+"        	            "
	    		+"            float color=1.0f-s*1.0e4/8.0f;"
	    		+"              	"
	    		+"		    __doHide=false;"
	    		+"		    if( varpar->cut_jigsaw_invert ==0)"
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
	    		+"		    __px = varpar->cut_jigsaw * (x);"
	    		+"		    __py = varpar->cut_jigsaw * (y);"
	            + (context.isPreserveZCoordinate() ? "__pz += varpar->cut_jigsaw * __z;\n" : "");
	  }
}

