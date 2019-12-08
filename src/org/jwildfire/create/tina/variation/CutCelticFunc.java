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



public class CutCelticFunc  extends VariationFunc {

	/*
	 * Variation :cut_celtic
	 * Date: august 29, 2019
	 * Reference & Credits:  https://www.shadertoy.com/view/3syXDz
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;

	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	

	int mode=1;
    double zoom=5.0;
    int invert=0;

 	
    
	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_ZOOM,PARAM_INVERT};

	double circ(vec2 uv, double r){
	    double d = G.length(uv);
	    double c = G.smoothstep(d, d+0.02, r);
	    return c;
	}

	double celticShit(vec2 uv, double r){
	    double r1 = .38;
	    
	    double r2 = .45;
	    
	    // used for Mix
	    double c1 = circ(uv, r1);
	    
	    // Add the following
	    double c5 = circ(uv, r2);
	    
	    vec2 uvs = new vec2(.5, -.288675);
	    double c2 = circ(uv.plus(uvs), r2);
	    
	    vec2 uvm = new vec2(-.5, -.288675);
	    double c3 = circ(uv.plus(uvm), r2);
	    
	    vec2 uvv = new vec2(0, .57735);
	    double c4 = circ(uv.plus( uvv), r2);
	    
	    double d = c5 - c2 - c3 - c4;
	   
	    return G.mix(0., d, c1);
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
		     x=pContext.random();
		     y=pContext.random();
		      px_center=0.5;
		      py_center=0.5;		     
		    }
		    
		    vec2 uv1 =new vec2(x*1.0,y*1.155);


            // uv1
            uv1 = uv1.multiply(zoom);
            double m = G.mod(uv1.y, 2.);
            uv1.x += G.step(1., m)*.5;
        	uv1 = G.fract(uv1);	
        	uv1.y /= 1.155;
            uv1.x -=1.155/2.;
            uv1.y -=.5;
            
            
            
            // uv2
            vec2 uv2 = new vec2(x*1.0,y*1.155);
            uv2 = uv2.multiply(zoom);
            uv2.x -= .5;
            uv2.y -= .288675;
            double n = G.mod(uv2.y, 2.);
            uv2.x += G.step(1., n)*.5;
        	uv2 = G.fract(uv2);	
        	uv2.y /= 1.155;
            uv2.x -=1.155/2.;
            uv2.y -=.5;
            

            // uv3
            vec2 uv3 = new vec2(x*1.0,y*1.155);
            uv3 = uv3.multiply(zoom);
            uv3.x += 1.;
            uv3.y -= .65;
            double o = G.mod(uv3.y, 2.);
            uv3.x += G.step(1., o)*.5;
        	uv3 = G.fract(uv3);	
        	uv3.y /= 1.155;
            uv3.x -=1.155/2.;
            uv3.y -=.5;
            
            double f = celticShit(uv1, .5);
            double g = celticShit(uv2, .5);
            double h = celticShit(uv3, .5);
            
            // Isolate f, g, h, to see ind. uv
            double color = f + g + h;
			              	
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
		return "cut_celtic";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  mode,zoom,invert});
	}

	public void setParameter(String pName, double pValue) {

		if (pName.equalsIgnoreCase(PARAM_MODE)) {
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

