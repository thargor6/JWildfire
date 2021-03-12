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
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class DC_MandelBox2DFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_mandelbox2D
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

		vec2 I=new vec2(zoom*xp,zoom*yp);

		vec4 O=new vec4(I,-5.*G.cos(time*.1),1.);

	    double d=1.;
	    for(int i=0;i<20;i++){ //this mandelbox loop should also work in 3D(with a proper DE (initialize d at O.z,remove the +1.,and the factor 5 at the end)
	        I=G.clamp(I,-1.,1.).multiply(2.).minus(I);//boxfold
	        double b = (O.a=G.length(I))<.5?4.:O.a<1.?1./O.a:1.;//ballfold
	        I= I.multiply(O.z * b).plus(new vec2(O.x,O.y)); //scaling
	        d=b*d*G.abs(O.z)+1.;//bound distance estimation
	         }
	    d=Math.pow(G.length(I)/d,.1)*5.;
		O=new vec4(G.cos(d),G.sin(10.*d+1.),G.cos(3.*d+1.),0).multiply(0.5).add(0.5);
		return new vec3(O.r,O.g,O.b);
	}
 	

	

	public String getName() {
		return "dc_mandelbox2D";
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

