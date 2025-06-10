package org.jwildfire.create.tina.variation;



import js.glsl.mat2;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CutSpiralFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :cut_spiral
	 * Date: august 29, 2019
	 * Reference & Credits:  https://www.shadertoy.com/view/4ly3zc
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;




	private static final String PARAM_TIME = "time";
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	

	double time=0.0;
	int mode=1;
    double zoom=2.5;
    int invert=0;

 	
    
	private static final String[] additionalParamNames = { PARAM_TIME,PARAM_MODE,PARAM_ZOOM,PARAM_INVERT};

	
	  public mat2 rot2(double spin)
	  {
		  return new mat2(1.1*Math.sin(spin),1.1*Math.cos(spin),-1.1*Math.cos(spin),1.1*Math.sin(spin));
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
    
		    int  l = 0;
		    mat2 rot = rot2(time);
		    
		    for(int i = 0; i < 64; i++) {
		        
		        uv = rot.times(uv);
		        
		        if(uv.y > 1.0) {
		            break;
		        }
		        l = l^1;
		    }
    	    double color =l; 
			              	
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
		return "cut_spiral";
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
		time = Math.random() * 2.0 * Math.PI;
		zoom = Math.random() * 5.0 + 0.01;
		invert = (int) (Math.random() * 2);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return  "		    float x,y;"
	    		+"		    "
	    		+"		    if( __cut_spiral_mode ==0)"
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
	    		+"		    float2 uv =make_float2(x* __cut_spiral_zoom ,y* __cut_spiral_zoom );"
	    		+"    "
	    		+"		    int  l = 0;"
	    		+"		    Mat2 rot = cut_spiral_rot2( __cut_spiral_time );"
	    		+"		    "
	    		+"		    for(int i = 0; i < 64; i++) {"
	    		+"		        "
	    		+"		        uv = times(&rot, uv);"
	    		+"		        "
	    		+"		        if(uv.y > 1.0) {"
	    		+"		            break;"
	    		+"		        }"
	    		+"		        l = l^1;"
	    		+"		    }"
	    		+"    	    float color =l; "
	    		+"			              	"
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_spiral_invert ==0)"
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
	    		+"		    __px = __cut_spiral * x;"
	    		+"		    __py = __cut_spiral * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_spiral * __z;\n" : "");
	  }
	 
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return  "__device__ Mat2  cut_spiral_rot2 (float spin)"
	    		+" {"
	    		+"   Mat2 m;"
	    		+"	 Mat2_Init(&m,1.1*sin(spin),1.1*cos(spin),-1.1*cos(spin),1.1*sin(spin));"
	    		+"	 return m;"
	    		+" }";
	  }
}

