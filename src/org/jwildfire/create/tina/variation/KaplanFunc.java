package org.jwildfire.create.tina.variation;


import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class KaplanFunc  extends VariationFunc {

	/*
	 * Variation :kaplan
	 * Date: january 10, 2021
	 * Jesus Sosa
	 * Reference & Credits: https://www.shadertoy.com/view/4sfyzX
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_N = "Density";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_INVERT = "invert";
	


	int seed=1000;
	int mode=1;
	int N=800;
	double time=10.0;
    int invert=0;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_N,PARAM_TIME,PARAM_INVERT};
	
	 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    int x,y;

	        x =  (int)((N) * pContext.random());
	        y =  (int)((N) * pContext.random());  

		     double xv ;
		     double yv ;
		     
		    double zoom=G.floor(time);

		      xv = zoom* ( x  - (int) N/2.0);
		      yv = zoom* ( y  - (int) N/2.0);
    
		    double r0 = G.mod(time / 100.0 + Math.acos(-1.0) / 4.0, Math.acos(-1.0)*2.0);
		    r0 = G.atan(xv, yv);
		    mat2 rot = new mat2(Math.cos(r0), -Math.sin(r0), Math.sin(r0), Math.cos(r0));
			vec2 uv = new vec2(xv,yv).times(rot);
		    xv=uv.x;
		    yv=uv.y;

		    
		    double value = (xv*xv) + (yv*yv);
		    
		    double exponent =  G.floor(Math.log(value)/Math.log(2.));
		    double mantissa =  value*Math.pow(2.0, -exponent)-1.;
		    
		    double r = mantissa - G.floor(mantissa*Math.pow(2.,16.)+0.5)/Math.pow(2.,16.);
		    double color = G.sign((r));
		    		                  	
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
		    pVarTP.x = pAmount * ((double)x/N- 0.5);
		    pVarTP.y = pAmount * ((double)y/N- 0.5);
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	   }

	  
	public String getName() {
		return "kaplan";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		 return (new Object[] {  seed, N, time, invert});
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
		else if (pName.equalsIgnoreCase(PARAM_N)) {
			N =(int)Tools.limitValue(pValue, 50 , 1500);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
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
	
}

