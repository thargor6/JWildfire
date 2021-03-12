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
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;



public class DC_TeslaFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_tesla
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_LEVELS = "levels";
	private static final String PARAM_STYLE = "style";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";



	int levels=20;
	double style=10.0;
	private int seed = 10000;
	double time=0.0;
	double zoom=4.0;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	private static final String[] additionalParamNames = { PARAM_LEVELS,PARAM_STYLE,PARAM_SEED,PARAM_TIME,PARAM_ZOOM};


public	double field21( vec3 p, double s) {
	double strength = 20. + .03 * Math.log(1.e-6d + G.fract(G.sin(time*10.) * 4500.0));
	double accum = s*3.;
	double prev = 0.;
	double tw = 0.;
	for (int i = 0; i < levels; ++i) {
		
		double mag = G.dot(p,p)*G.pow(s,1.0*G.abs(G.sin(time/8.0)));
		//remove comment for another effect
		//mag*=dot(p,1.0/p);
		p = G.abs(p).division( mag).add(new vec3(-.9, -.234560*G.abs(G.sin(time)), -1.));
		double w = G.exp(-(double)i / style );
		accum += w *G.exp(-strength * G.pow(G.abs(mag / prev),1.2));
		tw += w*w;
		prev = mag;
	}
//	return G.max(0.,4. * accum / tw - .92);
	return G.max(0.,6. * accum / tw - 2.);
}
	
	public vec3 getRGBColor(double xt,double yt)
	{

		vec2 pos=new vec2(xt,yt).multiply(zoom);

		double col=0.2;
		
		col=field21(new vec3(pos,1.0),0.5);
		return new vec3(col);
	}
 	
	

	public String getName() {
		return "dc_tesla";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { levels,style,seed,time,zoom},super.getParameterValues());
	}


	
	public void setParameter(String pName, double pValue) {
		 if (pName.equalsIgnoreCase(PARAM_LEVELS)) {
			
			levels = (int)Tools.limitValue(pValue, 1 , 25);
		}
		else if (pName.equalsIgnoreCase(PARAM_STYLE)) {
			
			style = Tools.limitValue(pValue, 5. , 100.);
		}
		else if (PARAM_SEED.equalsIgnoreCase(pName)) 
		{	   seed =  (int) pValue;
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
			
			zoom = pValue;
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

