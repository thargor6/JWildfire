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



public class DC_MoebiusLogFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_moebiuslog
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_LOG = "Log";
	private static final String PARAM_MOEBIUS = "Moebius";
	private static final String PARAM_SCALE = "scale";
	private static final String PARAM_ANGLE = "angle";


	double zoom=7.0;
	private int seed = 10000;
	double time=0.0;
	private int Log = 1;
	private int Moebius = 1;
	double scale=5.0;
	double angle=5.0;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	

	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_SEED,PARAM_TIME,PARAM_LOG,PARAM_MOEBIUS,PARAM_SCALE,PARAM_ANGLE};



	public vec3 getRGBColor(double xp,double yp)
	{

		vec2 U=new vec2(xp,yp).multiply(zoom);

		vec2 z = U.minus( new vec2(-1.,0));
	       if(Moebius==1)
	       {
		      U.x -= .5;                         // Moebius transform
		      mat2 tmp= new mat2(z,new vec2(-z.y,z.x));
	          U = U.times( tmp ).division(G.dot(U, U));
	       }	             

       if(Log==1)               // offset   spiral, zoom       phase     // spiraling
	        U =   new vec2(0.5,-0.5).multiply(Math.log(G.length(U=U.add(.5)))).plus(new vec2(angle,1).multiply(G.atan2(U.y, U.x)/6.3));
	    	
	    vec3 color=new vec3(0.);// n  
	    
	    color =  color.plus(G.length(G.fract(U.multiply(scale))));
		
		return color;
	}
 	

	

	public String getName() {
		return "dc_moebiuslog";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,seed,time,Log,Moebius,scale,angle},super.getParameterValues());
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
		else if (pName.equalsIgnoreCase(PARAM_LOG)) {
			Log = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_MOEBIUS)) {
			Moebius = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_SCALE)) {
			scale = Tools.limitValue(pValue, 1.0 , 10.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_ANGLE)) {
			angle = Tools.limitValue(pValue, 1.0 , 10.0);
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

