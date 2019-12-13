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



public class CutSpiralFunc  extends VariationFunc {

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
	
}

