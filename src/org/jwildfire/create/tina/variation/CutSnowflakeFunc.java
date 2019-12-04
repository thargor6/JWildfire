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



public class CutSnowflakeFunc  extends VariationFunc {

	/*
	 * Variation :cut_snowflake
	 * Date: august 30, 2019
	 * Jesus Sosa
	 * Credits & Reference:
	 * https://www.shadertoy.com/view/4t23DD
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";
	private static final String PARAM_MODE = "mode";	
	private static final String PARAM_TIME = "time";	
	private static final String PARAM_LEVEL = "level";	
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	

	int seed=1000;
	int mode=1;
	double time=0.;
	int level=10;
    double zoom=10.0;
    int invert=0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_TIME,PARAM_LEVEL,PARAM_ZOOM,PARAM_INVERT};


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
		    	    
		    vec2 uv = new vec2(x*zoom,y*zoom);	

		    double s = .25, f = .0, k = f;
		    vec3 p = new vec3(uv, Math.sin(time * .4) * .5 - .5).multiply(s);    
		        
		    for( int i=0; i< level; i++ )
		    {
		           p = G.abs(p).division(G.dot(p,p)).minus(1.65);
		           k = G.length(p) ;
		           p = p.multiply(k).plus(k);
		   }
		   
		   f = G.dot(p,p)* s;
		    double color=f;
		    
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color>0.5)
		      { x=0;
		        y=0;
		        pVarTP.doHide = true;	        
		      }
		    } else
		    {
			      if (color<=0.5)
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
		  randomize=new Random(seed);
	   }

	  
	public String getName() {
		return "cut_snowflake";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed,mode, time,level, zoom,invert});
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
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_LEVEL)) {
			level =(int)Tools.limitValue(pValue, 1 , 15);
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
	
}

