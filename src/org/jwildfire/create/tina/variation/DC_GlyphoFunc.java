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



public class DC_GlyphoFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_sincos
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_F1 = "f1";
	private static final String PARAM_F2 = "f2";
	private static final String PARAM_F3 = "f3";



	double zoom=7.0;
	private int seed = 10000;
	double time=0.0;
	double f1=0.115;
	double f2=0.75;
	double f3=1.5;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	

	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_SEED,PARAM_TIME,PARAM_F1,PARAM_F2,PARAM_F3};



	public vec3 getRGBColor(double xp,double yp)
	{

	    
		vec2 uv=new vec2(xp,yp).multiply(zoom);

	    vec3 color=new vec3(0.);// n  

	    double m = 0.;
	    double stp = Math.PI/5.;
	    double odd = -1.;
	    for(double n=1.; n<2.5; n+=0.5){        
	        odd *= -1.;
	        double t = time * odd * .3;
	        for(double i=0.0001; i<2.*Math.PI; i+=stp){
	            
	            vec2 uvi = uv.multiply(n).plus(new vec2(cos(i + n*stp*.5 + t)*.4, sin(i + n*stp*.5 + t)*.4));
	            double l = G.length(uvi);
	            m += G.smoothstep(f1 * n, .0, l)*f3;        
	        }    
	    }
	    
	    m = G.step(f2, G.fract(m*f3));
	    
	   color = new vec3(1.* m);

		return color;
	}
 	

	

	public String getName() {
		return "dc_glypho";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,seed,time,f1,f2,f3},super.getParameterValues());
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
		else if (pName.equalsIgnoreCase(PARAM_F1)) {
			f1 = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_F2)) {
			f2 = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_F3)) {
			f3 = pValue;
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

