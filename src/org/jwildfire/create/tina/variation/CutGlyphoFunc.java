package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CutGlyphoFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation : cut_glypho
	 * Autor: Jesus Sosa
	 * Date: August 20, 2019
	 * Reference & Credits: 
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	private static final String PARAM_F1 = "f1";
	private static final String PARAM_F2 = "f2";
	private static final String PARAM_F3 = "f3";

	int mode=1;
	double zoom=1.0;
	int invert=0;
	double f1=0.115;
	double f2=0.75;
	double f3=1.5;
	
	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_ZOOM,PARAM_INVERT,PARAM_F1,PARAM_F2,PARAM_F3};


	public vec3 getRGBColor(double xp,double yp)
	{   
		vec2 uv=new vec2(xp,yp).multiply(zoom);
	    vec3 color=new vec3(0.);// n  

	    double m = 0.;
	    double stp = Math.PI/5.;
	    // double odd = -1.;
	    for(double n=1.; n<2.5; n+=0.5){        
	        // odd *= -1.;
	       // double t = time * odd * .3;
	        for(double i=0.0001; i<2.*Math.PI; i+=stp){
	            
	            //vec2 uvi = uv.multiply(n).plus(new vec2(cos(i + n*stp*.5 + t)*.4, sin(i + n*stp*.5 + t)*.4));
	            vec2 uvi = uv.multiply(n).plus(new vec2(cos(i + n*stp*.5 )*.4, sin(i + n*stp*.5 )*.4));
	            double l = G.length(uvi);
	            m += G.smoothstep(f1 * n, .0, l)*f3;        
	        }    
	    }
	    
	    m = G.step(f2, G.fract(m*f3));	    
	   color = new vec3(1.* m);
		return color;
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
		     x=pContext.random()-0.5;
		     y=pContext.random()-0.5;
		      px_center=0.0;
		      py_center=0.0;		     
		    }
		    vec3 color=getRGBColor(x,y);
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color.x==0.0)
		      { pVarTP.x=0;
		        pVarTP.y=0;
		        pVarTP.doHide = true;
		        return;
		      }
		    } else
		    {
			      if (color.x>0 )
			      { pVarTP.x=0;
			        pVarTP.y=0;
			        pVarTP.doHide = true;
			        return;
			      }
		    }
		    pVarTP.x = pAmount * (x-px_center);
		    pVarTP.y = pAmount * (y-py_center);
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	

	public String getName() {
		return "cut_glypho";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return (new Object[] { mode,zoom,invert,f1,f2,f3});
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode = (int) Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			invert = (int) Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_F1)) {
			f1 = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_F2)) {
			f2 = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_F3)) {
			f3 = pValue;
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
		zoom = Math.random() * 4.5 + 0.5;
		invert = (int) (Math.random() * 2);
		f1 = Math.random() * 0.175 + 0.075;
		f2 = Math.random() * 0.8 + 0.1;
		f3 = Math.random() * 1.75 + 0.75;
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		float x,y,px_center,py_center;"
	    		+"		    "
	    		+"		if( __cut_glypho_mode ==0)"
	    		+"		{"
	    		+"		     x= __x;"
	    		+"		     y =__y;"
	    		+"		     px_center=0.0;"
	    		+"		     py_center=0.0;"
	    		+"		}else"
	    		+"		{"
	    		+"		     x=RANDFLOAT()-0.5;"
	    		+"		     y=RANDFLOAT()-0.5;"
	    		+"		     px_center=0.0;"
	    		+"		     py_center=0.0;"
	    		+"		}"
	    		+"		float2 uv=make_float2(x,y)*( __cut_glypho_zoom );"
	    		+"	    float color=0.0f;"
	    		+"	    float m = 0.0f;"
	    		+"	    float stp = PI/5.0f;"
	    		+"	    for(float n=1.0f; n<2.5f; n+=0.5f){        "
	    		+"	        for(float i=0.0001f; i<2.0f*PI; i+=stp){"
	    		+"	            float2 uvi = uv*(n)+(make_float2(cos(i + n*stp*.5 )*.4, sin(i + n*stp*.5 )*.4));"
	    		+"	            float l = length(uvi);"
	    		+"	            m += smoothstep( __cut_glypho_f1  * n, 0.0f, l)* __cut_glypho_f3;"
	    		+"	        }"
	    		+"	    }"
	    		+"	    "
	    		+"	    m = step( __cut_glypho_f2 , fract(m* __cut_glypho_f3 ));"
	    		+"	   color = m;"
	    		+"	   __doHide=false;"
	    		+"	   if( __cut_glypho_invert ==0)"
	    		+"		{"
	    		+"	      if (color==0.0f)"
	    		+"	      { __px=0;"
	    		+"	        __py=0;"
	    		+"	        __doHide = true;"
	    		+"	      }"
	    		+"	    } else"
	    		+"	    {"
	    		+"	      if (color>0.0f )"
	    		+"	      { __px=0;"
	    		+"	        __py=0;"
	    		+"	        __doHide = true;"
	    		+"	      }"
	    		+"	    }"
	    		+"	    __px = __cut_glypho * (x-px_center);"
	    		+"	    __py = __cut_glypho * (y-py_center);"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_glypho * __z;\n" : "");
	  }
}

