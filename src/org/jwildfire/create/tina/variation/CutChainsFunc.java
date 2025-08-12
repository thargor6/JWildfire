package org.jwildfire.create.tina.variation;



import js.glsl.G;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CutChainsFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :cut_chains
	 * Date: august 29, 2019
	 * Reference & Credits:  https://www.shadertoy.com/view/MdffWM
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SHIFTX = "shiftX";
	private static final String PARAM_SHIFTY = "shiftY";
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	
	double x0=0;
	double y0=0.0;
	int mode=1;
    double zoom=2.5;
    int invert=0;

 	
    
	private static final String[] additionalParamNames = { PARAM_SHIFTX,PARAM_SHIFTY,PARAM_MODE,PARAM_ZOOM,PARAM_INVERT};

	 	
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
		    
		    
		    vec2 u =new vec2(x*zoom,y*zoom);
            u=u.plus(new vec2(x0,y0));
    	    double wy = Math.cos(u.y * 12.);
    	    double wx = Math.sin(u.x * 15.); 
    	    double w  = Math.cos(u.x * 30.);

    	    double color = G.smoothstep(-0.25, 0.25,G.mix(wy + wx, wx*1.4, w * 3.));
			              	
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
		return "cut_chains";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  x0,y0, mode,zoom,invert});
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
	public void randomize() {
		// Don't change mode
		x0 = Math.random() * 0.42 - 0.21;
		y0 = Math.random() * 0.52 - 0.26;
		zoom = Math.random() * 4.5 + 0.5;
		invert = (int) (Math.random() * 2);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	
	@Override
	public String getGPUCode(FlameTransformationContext context) {
		return	"		    float x,y,px_center,py_center;"
				+"		    "
				+"		    if( __cut_chains_mode ==0)"
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
				+"		    "
				+"		    float2 u =make_float2(x* __cut_chains_zoom ,y* __cut_chains_zoom );"
				+"            u=u+(make_float2( __cut_chains_shiftX ,__cut_chains_shiftY));"
				+"    	    float wy = cos(u.y * 12.);"
				+"    	    float wx = sin(u.x * 15.); "
				+"    	    float w  = cos(u.x * 30.);"
				+"    	    float color = smoothstep(-0.25, 0.25,mix(wy + wx, wx*1.4, w * 3.));"
				+"			              	"
				+"		    __doHide=false;"
				+"		    if( __cut_chains_invert ==0)"
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
				+"		    __px = __cut_chains * (x-px_center);"
				+"		    __py = __cut_chains * (y-py_center);"
				+ (context.isPreserveZCoordinate() ? "__pz += __cut_chains * __z;" : "");
	}
}

