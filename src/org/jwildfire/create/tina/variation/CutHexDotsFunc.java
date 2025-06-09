package org.jwildfire.create.tina.variation;



import js.glsl.G;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class  CutHexDotsFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation : cut_hexdots
	 * Autor: Jesus Sosa
	 * Date: September 25, 2019
	 * Reference & Credits: https://www.shadertoy.com/view/MlXyDl
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_MODE = "mode";
	private static final String PARAM_SIZE = "size";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";

	int mode=1;
	double size=0.5;
	double zoom=4.0;
	private int invert = 0;

	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_SIZE,PARAM_ZOOM,PARAM_INVERT};
		
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

		  double x,y,cx,cy;  
		  if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		      cx=0.0;
		      cy=0.0;
		    }else
		    {
		     x=pContext.random()-0.5;
		     y=pContext.random()-0.5;
		     cx=0.0;
		     cy=0.0;
		    }
	    
	    vec2 u=new vec2(x*zoom,y*zoom);
	    		
	    vec2 s = new vec2(1.,1.732);
	    vec2 a = G.mod(u     ,s).multiply(2.).minus(s);
	    vec2 b = G.mod(u.plus(s.multiply(.5)),s).multiply(2.).minus(s);
	    	    	    
        double col=0.;
        col= .8*G.min(G.dot(a,a),G.dot(b,b));   
	    
		pVarTP.doHide=false;
		if(invert==0)
		{
			if (col>size)
			{ x=0.;
			  y=0.;
			pVarTP.doHide = true;
			}
		} else
		{
			if (col<=size)
			{ x=0.;
			  y=0.;
			pVarTP.doHide = true;
			}
		}
		pVarTP.x = pAmount * (x-cx);
		pVarTP.y = pAmount * (y-cy);

		if (pContext.isPreserveZCoordinate()) {
			pVarTP.z += pAmount * pAffineTP.z;
		}
	}

	public String getName() {
		return "cut_hexdots";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { 
		return new Object[] {mode,size,zoom,invert};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			   mode =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_SIZE)) {
				size = Tools.limitValue(pValue, 0.01 , 1.);
			}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			   invert =   (int)Tools.limitValue(pValue, 0 , 1);
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
		size = Math.random() * 0.9 + 0.1;
		zoom = Math.random() * 49.0 + 1.0;
		invert = (int) (Math.random() * 2);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		  float x,y;"
	    		+"		  if( __cut_hexdots_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT()-0.5;"
	    		+"		     y=RANDFLOAT()-0.5;"
	    		+"		    }"
	    		+"	    "
	    		+"	    float2 u=make_float2(x* __cut_hexdots_zoom ,y* __cut_hexdots_zoom );"
	    		+"	    		"
	    		+"	    float2 s = make_float2(1.0f,1.732f);"
	    		+"	    float2 a = mod(u,s)*2.0f-s;"
	    		+"	    float2 b = mod(u+s*0.5f,s)*2.0f-s;"
	    		+"	    	    	    "
	    		+"      float col=0.0f;"
	    		+"      col= 0.8f*fminf(dot(a,a),dot(b,b));"
	    		+"	    "
	    		+"		__doHide=false;"
	    		+"		if( __cut_hexdots_invert ==0)"
	    		+"		{"
	    		+"			if (col>__cut_hexdots_size)"
	    		+"			{ x=0.0f;"
	    		+"			  y=0.0f;"
	    		+"			__doHide = true;"
	    		+"			}"
	    		+"		} else"
	    		+"		{"
	    		+"			if (col<=__cut_hexdots_size)"
	    		+"			{ x=0.0f;"
	    		+"			  y=0.0f;"
	    		+"			__doHide = true;"
	    		+"		    }"
	    		+"		}"
	    		+"		__px = __cut_hexdots * x;"
	    		+"		__py = __cut_hexdots * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_hexdots * __z;\n" : "");
	 }
}


