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
import js.glsl.vec4;



public class CutSqSplitsFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :cut_sqsplits
	 * Date: august 29, 2019
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_MODE = "mode";	
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	

	double time=0.9;
	int seed=1000;
	int mode=1;
    double zoom=2.0;
    int invert=0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_TIME,PARAM_ZOOM,PARAM_INVERT};


	 	
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
		    
		     vec2 U= new vec2(x*zoom,y*zoom);
	         vec2 Uf = G.fract(U).minus(.5);
	         vec2 Ui = G.floor(G.mod(U,2.));
	         
	         double t = time*.35,
	               a = G.atan2(Uf.x,Uf.y)/6.3 ,  
	               r = G.fract(t+t+ 2.* ( Ui.x!=Ui.y ? a : 1.-a )),
	           sharp = 180.* G.length(Uf),
	               k =   G.clamp((r-.5)*sharp,0.,1.)
	                   + G.clamp( 1. -r*sharp,0.,1.);
	         
	     	double color = 1.-k;
              	
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
		    pVarTP.x = pAmount * x;
		    pVarTP.y = pAmount * y;
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	   }

	  
	public String getName() {
		return "cut_sqsplits";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed, mode, time,zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
		}
		else if(pName.equalsIgnoreCase(PARAM_SEED))
		{
			   seed =   (int)pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);
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
	    return   "	  float x,y;  "
	    		+"		  if( varpar->cut_sqsplits_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT()-0.5;"
	    		+"		     y=RANDFLOAT()-0.5;		     "
	    		+"		    }"
	    		+"		    "
	    		+"		     float2 U= make_float2(x* varpar->cut_sqsplits_zoom ,y* varpar->cut_sqsplits_zoom );"
	    		+"	         float2 Uf = fract(U)-(.5);"
	    		+"	         float2 Ui = floorf(mod(U,2.));"
	    		+"	         "
	    		+"	         float t =  varpar->cut_sqsplits_time *.35,"
	    		+"	               a = atan2(Uf.x,Uf.y)/6.3 ,  "
	    		+"	               r = fract(t+t+ 2.* ( Ui.x!=Ui.y ? a : 1.-a )),"
	    		+"	           sharp = 180.* length(Uf),"
	    		+"	               k =   clamp((r-.5)*sharp,0.,1.)"
	    		+"	                   + clamp( 1. -r*sharp,0.,1.);"
	    		+"	         "
	    		+"	     	float color = 1.-k;"
	    		+"              	"
	    		+"		    __doHide=false;"
	    		+"		    if( varpar->cut_sqsplits_invert ==0)"
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
	    		+"		    __px = varpar->cut_sqsplits * x;"
	    		+"		    __py = varpar->cut_sqsplits * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += varpar->cut_sqsplits * __z;\n" : "");
	  }

}

