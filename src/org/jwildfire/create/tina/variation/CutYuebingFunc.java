package org.jwildfire.create.tina.variation;



import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class  CutYuebingFunc  extends VariationFunc  implements SupportsGPU {

	/*
	 * Variation : cut_yuebing
	 * Autor: Jesus Sosa
	 * Date: August 20, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	private static final String PARAM_P1 = "p1";
	private static final String PARAM_P2 = "p2";


    int mode=1;
	double zoom=.25;
	private int invert = 0;
	double p2=25.;
	double p1=2.;



	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_ZOOM,PARAM_INVERT,PARAM_P1,PARAM_P2};


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

			vec2 position = new vec2 (x*zoom,y*zoom); 
			double duration = Math.sin(p1/2.0)*p2;

			double color=0;;
			color += Math.sin(position.x*position.y*10000. * duration);
		        
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color<0.3)
		      { x=0.;
		        y=0.;
		        pVarTP.doHide = true;
		      }
		    } else
		    {
			      if (color>=0.3 )
			      { x=0.;
			        y=0.;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * x;
		    pVarTP.y = pAmount * y;
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }

	public String getName() {
		return "cut_yuebing";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { mode,zoom,invert,p1,p2};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			   invert =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_P1)) {
			   p1 =   pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_P2)) {
			   p2 =   pValue;
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "	  float x,y;  "
	    		+"		  if( __cut_yuebing_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT()-0.5;"
	    		+"		     y=RANDFLOAT()-0.5;		     "
	    		+"		    }"
	    		+"			float2 position = make_float2 (x* __cut_yuebing_zoom ,y* __cut_yuebing_zoom ); "
	    		+"			float duration = sinf( __cut_yuebing_p1 /2.0)* __cut_yuebing_p2 ;"
	    		+"			float color=0;"
	    		+"			color += sinf(position.x*position.y*10000. * duration);"
	    		+"		        "
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_yuebing_invert ==0)"
	    		+"		    {"
	    		+"		      if (color<0.3)"
	    		+"		      { x=0.;"
	    		+"		        y=0.;"
	    		+"		        __doHide = true;"
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color>=0.3 )"
	    		+"			      { x=0.;"
	    		+"			        y=0.;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __cut_yuebing * x;"
	    		+"		    __py = __cut_yuebing * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_yuebing * __z;\n" : "");
	  }
}


