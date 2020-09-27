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



public class DC_QuadtreeFunc  extends DC_BaseFunc {

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
	private static final String PARAM_WIDTH = "width";
	private static final String PARAM_LEVELS = "Leves";




	double zoom=1.;
	private int seed = 10000;
	double time=0.0;
	double width=1000.0;
	int levels=30;



	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	

	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_SEED,PARAM_TIME,PARAM_WIDTH,PARAM_LEVELS};


	
	// inspired from https://www.shadertoy.com/view/lljSDy


double s(double a) {
	return  Math.sin(a);
}

public double rnd(double p)
{
	return .5+.5*s(p+time)*s(p*2.17-time)*s(p*5.7+time);
}


	public vec3 getRGBColor(double xp,double yp)
	{
	    double r=1., id=1.;
	    
	//vec2 U=new vec2(xp,yp).multiply(zoom);
		vec2 U=new vec2(xp,yp).division(zoom).plus(new vec2(0.5,0.5));
	    vec2 fU;  
	    vec3 O=new vec3(0.);// n  
        O.b=1.0;   //Background dark blue
	    for (int i=0; i<levels; i++) {             // to the infinity, and beyond ! :-)
	        fU = G.min(U,new vec2(1.).minus(U));
	        if (G.min(fU.x,fU.y) < 0.3*r/width)
	        { 
	        	O=O.minus(1.);
	        	break;
	        } // cell border
	        if (rnd(id) > .7)
	        	break; // cell is out of the shape
	                // --- iterate to child cell
	        fU = G.step(.5,U);                  // select child
	        id = 4.*id + 2.*fU.y+fU.x;
	        U = U.multiply(2.).minus(fU);                    // go to new local frame
	        r *= 2.; 
	        if (r>width) 
	        	break;
	        O = O.plus(0.13);                        // getting closer, getting hotter           
	    }
		return O;
	}

	public String getName() {
		return "dc_quadtree";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,seed,time,width,levels},super.getParameterValues());
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
		else if (pName.equalsIgnoreCase(PARAM_WIDTH)) {
			width =Tools.limitValue(pValue, 100. , 1000.);
		}
		else if (pName.equalsIgnoreCase(PARAM_LEVELS)) {
			levels =(int)Tools.limitValue(pValue, 1 , 50);
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

