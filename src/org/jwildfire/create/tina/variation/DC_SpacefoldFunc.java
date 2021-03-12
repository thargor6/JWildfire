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



public class DC_SpacefoldFunc  extends DC_BaseFunc {

	/*
	 * Variation :dc_spacefold
	 * Date: august 29, 2019
	 * Author: Jesus Sosa
	 * Reference & Credits:
	 *   https://www.shadertoy.com/view/lsGSzW
	 */


	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "randomize";
	private static final String PARAM_TIME= "time";
	private static final String PARAM_ZOOM = "zoom";

	

    double time=0.0;
    double zoom=2.5;
    
    int seed=1000;
	Random randomize=new Random(seed);
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_ZOOM};

	public mat2 rot(double angle) {
	    return new mat2(cos(angle), -sin(angle),
	                sin(angle), cos(angle));
	}

	public vec3 palette( double t )
	{
		// http://iquilezles.org/www/articles/palettes/palettes.htm
	    vec3 a = new vec3(0.5);
	    vec3 b= new vec3(0.5);
	    vec3 c= new vec3(1.0, 1.0, 0.5);
	    vec3 d= new vec3(0.8, 0.9, 0.3);
	    return a.plus(b.multiply(G.cos( (c.multiply(t).plus(d)).multiply(6.28318) )));
	}
	
	public vec3 getRGBColor(double xp,double yp)
	{
	    vec2 uv =new vec2(xp*zoom,yp*zoom);
        
        for(int i = 0; i < 64; i++) {
            uv = G.abs(uv);
            uv = uv.times(rot(time/30.0));
            uv = uv.minus(new vec2(0.5,0.5));
            uv = uv.multiply(1.03);
        }
        uv = G.pow(G.abs(G.sin(uv)),new vec2(0.3));
        vec3 color = palette(uv.x*uv.y*1.9);
          
        return color;
	 
	}
	
	    
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
	   }

	  
	public String getName() {
		return "dc_spacefold";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //
		return joinArrays(new Object[] { seed,time,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SEED)) {
			   seed =   (int)Tools.limitValue(pValue, 0 , 10000);
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
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

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE};
	}

}

