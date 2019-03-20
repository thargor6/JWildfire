package org.jwildfire.create.tina.variation;

import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;



public class DC_TurbulenceFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_turbulence
	 * Autor: Jesus Sosa
	 * Date: October 31, 2018
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_LEVEL = "Level";


	private int seed = 10000;
    double time=0.0;
    double zoom=8.0;
	int level=3;

	int gradient=0;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_ZOOM,PARAM_LEVEL};

	   
	  
	public vec3 getRGBColor(double xp,double yp)
	{

	//	vec2 p=new vec2(8.0*xp-20.0,8.0*yp-20.0);
		vec2 p=new vec2(xp,yp).multiply(zoom).minus(20.0);
		vec3 col=new vec3(0.0);


		vec2 ik = p;
		double c = 1.0;
		double inten = .05;

		for (int n = 0; n < level; n++) 
		{
			double t = time * (1.0 - (3.0 / (double)(n+1)));
			ik = p.plus(new  vec2(G.cos(t - ik.x) + G.sin(t + ik.y), G.sin(t - ik.y) + G.cos(t + ik.x)));
			c += 1.0/G.length(new vec2(p.x / (G.sin(ik.x+t)/inten),p.y / (G.cos(ik.y+t)/inten)));
		}
		c /= (double)level;
		c = 1.5-G.sqrt(c);
		return new vec3(c*c*c*c);
	}


	public String getName() {
		return "dc_turbulence";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed,time,zoom,level},super.getParameterValues());
	}


	
	public void setParameter(String pName, double pValue) {
		if (PARAM_SEED.equalsIgnoreCase(pName)) 
		{	   seed =  (int) pValue;
	       randomize=new Random(seed);
	          long current_time = System.currentTimeMillis();
	          elapsed_time += (current_time - last_time);
	          last_time = current_time;
	          time = (double) (elapsed_time / 1000.0);
	    }
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = Tools.limitValue(pValue, 1.0 , 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_LEVEL)) {
			level = (int)Tools.limitValue(pValue, 1 , 5);
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

