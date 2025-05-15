package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.sin;

import js.glsl.G;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class  CutTileIllusionFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation : cut_tileillusion
	 * Autor: Jesus Sosa
	 * Date: August 20, 2019
	 * Reference & Credits:  https://www.shadertoy.com/view/XsBXWR
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	private static final String PARAM_TIME = "time";

	double time=0;
    int mode=1;
	double zoom=15.0;
	private int invert = 0;



	private static final String[] additionalParamNames = { PARAM_TIME,PARAM_MODE,PARAM_ZOOM,PARAM_INVERT};

	
	double f(double x)
	{ 
		return x + .1*sin(1.6*x); 
	}

	double solve(double x0,double x1,double y) {
	    double y0=f(x0), y1=f(x1);
	    if (y1<y0)
	    { 
	    	double x2=x1;x1=x0;x0=x2;
	    	double y2=y1;y1=y0;y0=y2; 
	    }
	    double xn=0., yn=0.;
	    for (int i=0; i<20; i++) {
		    xn = x0 + (x1-x0)/(y1-y0)*(y-y0);
		    yn=f(xn);
	        if (yn>y) 
	        { x1=xn; 
	           y1=yn;
	        } else
	        { x0=xn;
	          y0=yn; 
	        }
	     }
	    return xn;
	}

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

   
			vec2 uv=new vec2(x*zoom,y*zoom);


		   double y0 = solve(uv.y-3.,uv.y,  G.floor(f(uv.y))),
		           y1 = solve(uv.y,uv.y+3., G.floor(f(uv.y))+1.);
		    uv.y = f(uv.y);
		    uv.x /= (y1-y0);
		    double s = G.mod(G.floor(uv.y),2.);
		    double color = 0.;
		    if (G.fract(uv.y)>.05) {
		    	uv.x += time*G.sign(s-.5);
		    	double c = G.mod(G.floor(uv.x)+G.floor(uv.y),2.);
		    	color = c;
		    }

		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color==0.0)
		      { x=0.;
		        y=0.;
		        pVarTP.doHide = true;
		      }
		    } else
		    {
			      if (color>0.0 )
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
		return "cut_tileillusion";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() {
		return new Object[] { time,mode,zoom,invert};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			   invert =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
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
		time = Math.random() * 2.0;
		zoom = Math.random() * 49.0 + 1.0;
		invert = (int) (Math.random() * 2);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		  float x,y;  "
	    		+"		  if( __cut_tileillusion_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT()-0.5;"
	    		+"		     y=RANDFLOAT()-0.5;		     "
	    		+"		    }"
	    		+"   "
	    		+"			float2 uv=make_float2(x* __cut_tileillusion_zoom ,y* __cut_tileillusion_zoom );"
	    		+"		   float y0 = cut_tileillusion_solve(uv.y-3.,uv.y,  floorf(cut_tileillusion_f(uv.y))),"
	    		+"		           y1 = cut_tileillusion_solve(uv.y,uv.y+3., floorf(cut_tileillusion_f(uv.y))+1.);"
	    		+"		    uv.y = cut_tileillusion_f(uv.y);"
	    		+"		    uv.x /= (y1-y0);"
	    		+"		    float s = mod(floorf(uv.y),2.);"
	    		+"		    float color = 0.;"
	    		+"		    if (fract(uv.y)>.05) {"
	    		+"		    	uv.x +=  __cut_tileillusion_time *sign(s-.5);"
	    		+"		    	float c = mod(floorf(uv.x)+floor(uv.y),2.);"
	    		+"		    	color = c;"
	    		+"		    }"
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_tileillusion_invert ==0)"
	    		+"		    {"
	    		+"		      if (color==0.0)"
	    		+"		      { x=0.;"
	    		+"		        y=0.;"
	    		+"		        __doHide = true;"
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color>0.0 )"
	    		+"			      { x=0.;"
	    		+"			        y=0.;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __cut_tileillusion * x;"
	    		+"		    __py = __cut_tileillusion * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_tileillusion * __z;\n" : "");
	  }
	 
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return  "__device__		float  cut_tileillusion_f (float x)"
	    		+"	{ "
	    		+"		return x + .1*sinf(1.6*x); "
	    		+"	}"
	    		+"__device__	float  cut_tileillusion_solve (float x0,float x1,float y) {"
	    		+"	    float y0= cut_tileillusion_f (x0), y1= cut_tileillusion_f (x1);"
	    		+"	    if (y1<y0)"
	    		+"	    { "
	    		+"	    	float x2=x1;x1=x0;x0=x2;"
	    		+"	    	float y2=y1;y1=y0;y0=y2; "
	    		+"	    }"
	    		+"	    float xn=0., yn=0.;"
	    		+"	    for (int i=0; i<20; i++) {"
	    		+"		    xn = x0 + (x1-x0)/(y1-y0)*(y-y0);"
	    		+"		    yn= cut_tileillusion_f (xn);"
	    		+"	        if (yn>y) "
	    		+"	        { x1=xn; "
	    		+"	           y1=yn;"
	    		+"	        } else"
	    		+"	        { x0=xn;"
	    		+"	          y0=yn; "
	    		+"	        }"
	    		+"	     }"
	    		+"	    return xn;"
	    		+"	}";
	  }
}


