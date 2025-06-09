package org.jwildfire.create.tina.variation;



import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CutSpiralCBFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :cut_spiralcb
	 * Date: august 29, 2019
	 * Reference & Credits:  //https://www.shadertoy.com/view/MsfXRj
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;




	private static final String PARAM_TIME = "time";
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	

	double time=0.0;
	int mode=1;
    double zoom=1.0;
    int invert=0;

 	
    
	private static final String[] additionalParamNames = { PARAM_TIME,PARAM_MODE,PARAM_ZOOM,PARAM_INVERT};

	
	  public mat2 rotate(double spin)
	  {
		  return new mat2(-Math.sin(spin),Math.cos(spin),Math.cos(spin),Math.sin(spin));
	  }
		 
	  
	  double hill (double t, double w, double p) {	
			return Math.min (G.step (t-w/2.0,p), 1.0 - G.step (t+w/2.0,p));
		}



		double compute_spiral (vec2 uv, double iTime) {
			double fi = G.length (uv) * 50.0;
			double g = G.atan2 (uv.y, uv.x);
			uv = (rotate (time*7.0)).times(uv);
			uv =uv.multiply( Math.sin (g*15.0));
			uv = rotate (fi).times(uv);
						
			return G.mix (1.0, 0.0, Math.min (G.step (0.0, uv.x), hill (0.0, fi/25.0, uv.y)));
		}
		
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    double x,y,px_center,py_center;
		    
		    if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		    }else
		    {
		     x=2.0*pContext.random()-1.0;
		     y=2.0*pContext.random()-1.0;
		     
		    }
		    
		    vec2 uv =new vec2(x*zoom,y*zoom);
    
		    double color=compute_spiral (uv, time);	

			              	
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
		return "cut_spiralcb";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  time, mode,zoom,invert});
	}

	public void setParameter(String pName, double pValue) {

		if(pName.equalsIgnoreCase(PARAM_TIME))
		{
			   time=pValue;
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
		time = Math.random() * 9.0;
		zoom = Math.random() * 5.0 + 0.01;
		invert = (int) (Math.random() * 2);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		    float x,y,px_center,py_center;"
	    		+"		    "
	    		+"		    if( __cut_spiralcb_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=2.0*RANDFLOAT()-1.0;"
	    		+"		     y=2.0*RANDFLOAT()-1.0;"
	    		+"		     "
	    		+"		    }"
	    		+"		    "
	    		+"		    float2 uv =make_float2(x* __cut_spiralcb_zoom ,y* __cut_spiralcb_zoom );"
// test rotate & hill
//	    		+"   float color=1.0;"
//	    		+"			Mat2 m;"
//	    		+"			m= cut_spiralcb_rotate (__cut_spiralcb_time*7.0);"
//	    		+"			uv = times(&m,uv);"
//	    		+"	    	float fi = length (uv) * 50.0;"
//	    		+"          color=cut_spiralcb_hill (0.0, fi/25.0, uv.y);"
// original code
                +"		    float color=cut_spiralcb_compute_spiral (uv,  __cut_spiralcb_time );"
	    		+"              	"
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_spiralcb_invert ==0)"
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
	    		+"		    __px = __cut_spiralcb * x;"
	    		+"		    __py = __cut_spiralcb * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_spiralcb * __z;\n" : "");
	  }
	 
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return "__device__ Mat2  cut_spiralcb_rotate (float spin)"
	    		+" {"
	    		+"  Mat2 m;"
	    		+"  Mat2_Init(&m,-sinf(spin),cosf(spin),cosf(spin),sinf(spin));"
	    		+"  return m;"
	    		+" }"
	    		+"		 "
	    		+"__device__ float  cut_spiralcb_hill  (float t, float w, float p)"
	    		+"{	"
	    		+"	 return fminf (step (t-w/2.0,p), 1.0 - step (t+w/2.0,p));"
	    		+"}"
	    		+"	  "
	    		+"__device__ float  cut_spiralcb_compute_spiral  (float2 uv, float time)"
	    		+"{"
	    		+"			float fi = length (uv) * 50.0;"
	    		+"			float g = atan2 (uv.y, uv.x);"
	    		+"			Mat2 m;"
	    		+"			m= cut_spiralcb_rotate (time*7.0);"
	    		+"			uv = times(&m,uv);"
	    		+"			uv =uv*( sinf (g*15.0));"
	    		+"          Mat2 m1=cut_spiralcb_rotate  (fi);"
	    		+"			uv = times(&m1, uv);"
	    		+"			return mix (1.0, 0.0, fminf (step (0.0, uv.x),  cut_spiralcb_hill (0.0, fi/25.0, uv.y)));"
	    		+"}";
	  }
}

