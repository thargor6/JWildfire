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



public class DC_VoronoiseFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_voronise
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_DELTAX = "pX";
	private static final String PARAM_DELTAY = "pY";


	double zoom = 8.0;
    double deltaX=0.5;
	double deltaY=0.5;

	
	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_DELTAX,PARAM_DELTAY};
	    
	  
	public vec3 getRGBColor(double xp,double yp)
	{

		vec2 st=new vec2(xp,yp);
		vec3 col=new vec3(0.0);
	    st=st.multiply(zoom);
	    double n = G.iqnoise(st, deltaX, deltaY);

	    col = new vec3(n);
		return col;
	}
 	
	public String getName() {
		return "dc_voronoise";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,deltaX, deltaY},super.getParameterValues());
	}
	
	public void setParameter(String pName, double pValue) {
		if (PARAM_ZOOM.equalsIgnoreCase(pName)) 
		{	   zoom =  pValue;

	    }
		else if (pName.equalsIgnoreCase(PARAM_DELTAX)) {
			deltaX = Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_DELTAY)) {
			deltaY = Tools.limitValue(pValue, 0.0 , 1.0);
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

