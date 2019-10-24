package org.jwildfire.create.tina.variation;


import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

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



public class DC_BooleansFunc  extends DC_BaseFunc {

	/*
	 * Variation :dc_booleans
	 * Date: august 29, 2019
	 * Author: Jesus Sosa
	 * Reference & Credits:
	 *   https://www.shadertoy.com/view/4ldcW8
	 */


	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "randomize";
	private static final String PARAM_TYPE = "^/&/|";
	private static final String PARAM_ZOOM = "zoom";

	
	int type=1;

	
	int mode=1;
    double zoom=1000.0;
    int invert=0;
    
    int seed=1000;
	Random randomize=new Random(seed);
 	
    double x0=0.,y0=0.;
    double temp;
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TYPE,PARAM_ZOOM};



	public vec3 hsv2rgb(vec3 c) {
		  vec4 K = new vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
		  vec3 p = G.abs(G.fract(new vec3(c.x,c.x,c.x).plus(new vec3(K.x,K.y,K.z))).multiply(6.0).minus(new vec3(K.w)));
		  return G.mix(new vec3(K.x), G.clamp(p.minus( new vec3(K.x)), 0.0, 1.0), c.y).multiply(c.z);
		}

	 	
	
	public vec3 getRGBColor(double xp,double yp)
	{

	    vec2 u =new vec2(xp*zoom,yp*zoom);
        u=u.plus(new vec2(x0,y0));
        
        if(type==0)   //XOR
          temp=((int)u.x )^((int)u.y);            
        else if(type==1)  // AND
        	temp=((int)u.x )&((int)u.y);
        else if(type==2)  // OR
        	temp=((int)u.x )|((int)u.y);
        
        vec3 color=hsv2rgb(new vec3(sin(temp), 1.0, cos(temp)));  
        return color;
	 
	}
	
	    
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
		  x0=seed*randomize.nextDouble();
		  y0=seed*randomize.nextDouble();
	   }

	  
	public String getName() {
		return "dc_booleans";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //
		return joinArrays(new Object[] { seed,type,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SEED)) {
			seed =   (int)pValue;
		    randomize=new Random(seed);
		}
		else if (pName.equalsIgnoreCase(PARAM_TYPE)) {
			type = (int)Tools.limitValue(pValue, 0 , 2);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
		}
		else
			super.setParameter(pName, pValue);
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

