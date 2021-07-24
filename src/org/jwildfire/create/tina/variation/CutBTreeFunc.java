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



public class CutBTreeFunc  extends VariationFunc  implements SupportsGPU {


	/*
	 * Variation :cut_btree
	 * Date: November 19, 2019
	 * Author: Jesus Sosa
	 * Reference & credits:  https://www.shadertoy.com/view/ltKcW1
	 */


	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED = "randomize";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_THICKNESS = "thickness";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_INVERT = "invert";
	
    
	int seed=0;
	double time=1.0;
	double thickness=5.0;
	int mode=1;
    double zoom=1.0;
    int invert=1;
    
	Random randomize=new Random(seed);
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;

 	
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_THICKNESS,PARAM_ZOOM,PARAM_MODE,PARAM_INVERT};

	 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;  
		  if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		    }else
		    {
		     x=2.0*pContext.random()-1.0;
		     y=2.0*pContext.random()-1.0;		     
		    }
		    
		    vec2 w =new vec2(x*zoom,y*zoom);    
		    vec2 t = w.division(w).multiply(G.fract(time)) ;
		    vec2 l = w.multiply(w).multiply(5.).minus(t) ;
		    t=t.plus(1.0);
		    vec2 m = G.exp2( G.ceil(l) ).multiply(t);  
		    w= G.fract( m.multiply(w.x)).minus(.5);
		    vec2 t0=G.sign(w).division(4.);
			vec2 t1=G.abs( G.smoothstep(0., 1., G.fract(l)).multiply(t0).minus(w));
		    vec2 t2=( m.division(t1).division(100.0*thickness ));
		    
		    double color = t2.y;
              	
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color>0.5)
		      { x=0;
		        y=0;
		        pVarTP.doHide = true;	        
		      }
		    } else
		    {
			      if (color<=0.5)
			      { x=0;
			        y=0;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * x;
		    pVarTP.y = pAmount * y;
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);

	   }

	  
	public String getName() {
		return "cut_btree";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed,time, thickness, zoom,mode ,invert});
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
			time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_THICKNESS)) {
			thickness = Tools.limitValue(pValue, 1. , 10.);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
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
	    return  "float x,y;  "
	    		+"		  if( __cut_btree_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=2.0*RANDFLOAT()-1.0;"
	    		+"		     y=2.0*RANDFLOAT()-1.0;"
	    		+"		    }"
	    		+"		    "
	    		+"		    float2 w =make_float2(x* __cut_btree_zoom ,y* __cut_btree_zoom );"
	    		+"		    float2 t = w/w*(fract( __cut_btree_time )) ;"
	    		+"		    float2 l = w*w*(5.0f)-t;"
	    		+"		    t=t+1.0;"
	    		+"		    float2 m = exp2f( ceilf(l) )*t;  "
	    		+"		    w= fract( m*w.x)-0.5;"
	    		+"		    float2 t0=sign(w)/(4.);"
	    		+"			float2 t1=abs( smoothstep(0., 1., fract(l))*t0-w);"
	    		+"		    float2 t2=( m/t1/(100.0* __cut_btree_thickness));"
	    		+"		    "
	    		+"		    float color = t2.y;"
	    		+"              	"
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_btree_invert ==0)"
	    		+"		    {"
	    		+"		      if (color>0.5)"
	    		+"		      { x=0;"
	    		+"		        y=0;"
	    		+"		        __doHide = true;	        "
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color<=0.5)"
	    		+"			      { x=0;"
	    		+"			        y=0;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __cut_btree * x;"
	    		+"		    __py = __cut_btree * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_btree * __z;\n" : "");
	  }
}

