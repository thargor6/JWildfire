package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.M_2PI;
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



public class DC_CairoTilesFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_cairotiles
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";



	double zoom=7.0;
	private int seed = 10000;
	double time=0.0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	

	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_SEED,PARAM_TIME};



	public vec3 getRGBColor(double xp,double yp)
	{

	    
	    double pi = Math.acos(-1.);
	    
		vec2 p=new vec2(xp,yp).multiply(zoom);

	    vec3 color=new vec3(0.);// n  

	    double th = G.mod(time * pi / 5., pi * 2.);
	    double gridsize = (.5 + G.abs(sin(th * 2.)) * (Math.sqrt(2.) / 2. - .5)) * 2.;

	    int flip = 0;

	    if(G.fract(th / pi + .25) > .5)
	    {
	        p = p.minus(.5);
	        flip = 1;
	    }

	    p = p.multiply(gridsize);

	    vec2 cp = G.floor(p.division(gridsize));

	    p = G.mod(p, gridsize).minus(gridsize / 2.);

	    p = p.multiply(G.mod(cp, 2.).multiply( 2.).minus( 1.));

	    p = p.times(new mat2(cos(th), sin(th), -sin(th), cos(th)));

	    double w = zoom / 2000. * 1.5;
	    
	    double a = G.smoothstep(-w, +w, G.max(Math.abs(p.x), Math.abs(p.y)) - .5);

	    if(flip==1)
	        a = 1. - a;

	    if(flip==1 && a < .5 && (Math.abs(p.x) - Math.abs(p.y)) * G.sign(G.fract(th / pi) - .5) > 0.)
	        a = .4;

	    if(flip==0 && a < .5 && (G.mod(cp.x + cp.y, 2.) - .5) > 0.)
	        a = .4;

	    color = G.pow(new vec3(a), new vec3(1. / 2.2));

		return color;
	}
 	

	

	public String getName() {
		return "dc_cairotiles";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,seed,time},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_SEED)) {
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

