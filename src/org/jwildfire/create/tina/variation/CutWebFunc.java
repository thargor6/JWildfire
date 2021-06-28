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



public class CutWebFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :cut_web
	 * Date: october 19, 2019
	 * Author: Jesus Sosa
	 * Reference & Credits: https://www.shadertoy.com/view/td33RN
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_TIME = "time";
	private static final String PARAM_MODE = "mode";	
	private static final String PARAM_THICK = "thick";
	private static final String PARAM_INVERT = "invert";
	
	int seed=1000;
	double time=0.0;
    double zoom=0.5;
    double thick=0.05;
    int mode=1;
    int invert=1;


	Random randomize=new Random(seed);
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	

    
    
    double fac=10.0/(2.0*Math.PI);
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_MODE,PARAM_THICK,PARAM_INVERT};
	
	
	vec2 eval( vec2 p, vec2 c, double strength )
	{
	    p = p.minus(c);
	    double l = Math.log( G.length( p ) );
	    double ang = G.atan2( p.y, p.x );
	    return new vec2( l, ang ).multiply( strength );
	}

	double getColour( vec2 p )
	{


	    vec2 ep = eval( p, new vec2( 0.0, 0.0 ), 1.0 ); 
	    
	    double d = ep.x * 0.05*fac;
	    ep = ep.plus(new vec2(-ep.y,ep.x).multiply(4.0));
		vec2 si = G.smoothstep(new vec2(-.5*d),new vec2(.5*d),G.abs(G.mod(ep.multiply(fac).plus(time) ,2.0).minus(1.)).minus(thick));  

	    return 1.- (1.- (si.x+si.y));
	}
	
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
		    
    	    vec2 u =new vec2(x*zoom,y*zoom);
            double color=getColour(u);
              	
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

	   }

	  
	public String getName() {
		return "cut_web";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed, time, mode, thick,invert});
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
		else if (pName.equalsIgnoreCase(PARAM_THICK)) {
			thick =Tools.limitValue(pValue, 0.0 , 0.9);
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
	    		+"		    if( varpar->cut_web_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=2.0*RANDFLOAT()-1.0;"
	    		+"		     y=2.0*RANDFLOAT()-1.0;	     "
	    		+"		    }"
	    		+"		    "
	    		+"    	    float2 u =make_float2(x*0.5f,y*0.5f);"
	    		+"          float color=cut_web_getColour(u,varpar->cut_web_time,varpar->cut_web_thick);"
	    		+"              	"
	    		+"		    __doHide=false;"
	    		+"		    if( varpar->cut_web_invert ==0)"
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
	    		+"		    __px = varpar->cut_web * (x);"
	    		+"		    __py = varpar->cut_web * (y);"
	            + (context.isPreserveZCoordinate() ? "__pz += varpar->cut_web * __z;\n" : "");
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__	float2  cut_web_eval ( float2 p, float2 c, float strength )"
	    		+"	{"
	    		+"	    p = p-(c);"
	    		+"	    float l = log( length( p ) );"
	    		+"	    float ang = atan2( p.y, p.x );"
	    		+"	    return make_float2( l, ang )*( strength );"
	    		+"	}"
	    		+"__device__	float  cut_web_getColour ( float2 p , float time , float thick )"
	    		+"	{"
	    		+"      float fac=10.0/(2.0*PI);"
	    		+"	    float2 ep =  cut_web_eval ( p, make_float2( 0.0, 0.0 ), 1.0 ); "
	    		+"	    "
	    		+"	    float d = ep.x * 0.05*fac;"
	    		+"	    ep = ep+(make_float2(-ep.y,ep.x)*(4.0));"
	    		+"		float2 si = smoothstep(make_float2(-0.5f*d,-0.5f*d),make_float2(0.5f*d,0.5f*d),abs(mod(ep*(fac)+(time) ,2.0)-(1.))-(thick));  "
	    		+"	    return 1.- (1.- (si.x+si.y));"
	    		+"	}";
	  }	
}

