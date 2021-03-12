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
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class DC_GaborNoiseFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_gabornoise
	 * Autor: Jesus Sosa
	 * Date: may 10, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";


	private int seed = 10000;
	double time=0.0;
	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;

	double zoom=20.0;


	
	

	private static final String[] additionalParamNames = {PARAM_SEED,PARAM_TIME, PARAM_ZOOM};


	
	// variant of https://shadertoy.com/view/3slXDf

	// simplified from skaplun's "AFC Ajax" https://shadertoy.com/view/tdsSzs
	// borrowing diviaki's "fingerprint" https://www.shadertoy.com/view/4t3SWN

	public vec3  hash(double p)
	{
		return (G.fract(G.sin( (new vec3(63.31,395.467,1).multiply(p)) ).multiply(43141.59265)).multiply(2.).minus(1.) );
	}

	public vec3 getRGBColor(double xp,double yp)
	{

	    
		vec2 V=new vec2(xp,yp).multiply(zoom);
        vec2 U = new vec2( G.abs(V.x),  V.y ).multiply(45.0);
        
	    vec3 O=new vec3(0.);// n  

	    double a = 0., s = 1.;

	    for(int i=0; i<100; i++){
	        vec3 h = hash((double)i).multiply(30.);
	        vec2 t=new vec2(h.x,h.y);
	        t= t.times(new mat2(new  vec4(0,33,11,0).plus(G.cos( h.z/1e2*time)) )); // https://www.shadertoy.com/view/XlsyWX
	        h.x=t.x;h.y=t.y;
	        //if (V.x>0.) s = sign(h.z);
	    	a += s * G.atan2(U.x-h.x, U.y-h.y); // + .05*h.z*iTime;
	    }
	    O = ( G.cos( new vec3(0,23,21).plus(a)  ).multiply(0.6).plus(0.6) ); // https://www.shadertoy.com/view/ll2cDc

		return O;
	}
 	

	

	public String getName() {
		return "dc_gabornoise";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed,time, zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 1.0 , 100.0);
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

