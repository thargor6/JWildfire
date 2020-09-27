package org.jwildfire.create.tina.variation;


import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
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



public class DC_FractColorFunc  extends DC_BaseFunc {

	/*
	 * Variation :dc_fractcolor
	 * Date: august 29, 2019
	 * Author: Jesus Sosa
	 * Reference & Credits:
	 *   http://glslsandbox.com/e#29611.0 
	 *   https://www.shadertoy.com/view/MsK3zG
	 */


	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED= "randomize";
	private static final String PARAM_TIME= "time";
	private static final String PARAM_XPAR = "xPar";
	private static final String PARAM_YPAR = "yPar";
	private static final String PARAM_ITERS= "Iters";
	private static final String PARAM_ZOOM = "zoom";

 	
 	LocalTime now ;

 	int seed=0;
    double time=0;
 	double x_par=0.5,y_par=0.5;
    double zoom=1.0;
    int iters=88;
    
	Random randomize=new Random(seed);
	
 	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_XPAR,PARAM_YPAR,PARAM_ITERS,PARAM_ZOOM};

	
	public vec3 getRGBColor(double xp,double yp)
	{
	    vec2 U =new vec2(xp*zoom,yp*zoom);
        	    
	    vec2 m=new vec2(x_par,y_par);  // red & green
	    double blue=1.5-m.x+.04*sin(time);  // blue
	    
	    vec3 O = new vec3(U ,blue );  // start color
	    
	    for (int i = 0; i < iters; i++)
	    {
	       vec3 tmp=  G.abs(new vec3(O.x,O.y,O.z).division(G.dot(O,O)).minus(new vec3(1,1,m.y*.3))).multiply(new vec3(1.3, 1, .777));
	       O.x=tmp.x;
	       O.y=tmp.z;
	       O.z=tmp.y;
	    }         
        return new vec3(O.x,O.y,O.z);
	}
	
	    
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
	   }

	  
	public String getName() {
		return "dc_fractcolor";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //
		return joinArrays(new Object[] { seed,time,x_par,y_par,iters,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SEED)) {
			   seed=(int)pValue;
			   randomize=new Random(seed);
		       LocalTime now = LocalTime.now(ZoneId.systemDefault());
		       time=(double)now.toSecondOfDay();
		       x_par=randomize.nextDouble();
		       y_par=randomize.nextDouble();
		       
		}
	    else if (pName.equalsIgnoreCase(PARAM_TIME)) {
            time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_XPAR)) {
			x_par =Tools.limitValue(pValue, 0. , 1.);
		}
		else if (pName.equalsIgnoreCase(PARAM_YPAR)) {
			y_par =Tools.limitValue(pValue, 0. , 1.);
		}
		else if (pName.equalsIgnoreCase(PARAM_ITERS)) {
			iters =(int)pValue;
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
	
}

