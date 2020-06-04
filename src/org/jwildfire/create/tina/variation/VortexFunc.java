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



public class VortexFunc  extends VariationFunc {

	/*
	 * Variation :vortex
	 * Date: may 12, 2020
	 * Author: Jesus Sosa
	 * Reference & Credits:
	 *   
	 *   https://www.shadertoy.com/view/MlS3Rh
	 */


	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED= "randomize";

	private static final String PARAM_TIME="time";
	private static final String PARAM_STRENGTH="Flow strength";
	private static final String PARAM_SIZE="size";
	
 	int seed=0;
	double time=0.0;
	int strength=10;
	double size=4.0;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
 	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_STRENGTH,PARAM_SIZE};
	
	vec2 VortF (vec2 q, vec2 c)
	{
		vec2 d = q.minus( c);
		return new vec2 (d.y, - d.x).division((G.dot (d, d) + 0.05)).multiply(-0.25);
	}

	vec2 FlowField (vec2 q)
	{
		vec2 vr, c;
		double dir = 1.;
		c = new vec2 (G.mod (time, 10.) - 20., 0.6 * dir);
		vr = new vec2 (0.);
		for (int k = 0; k < 30; k ++) {
			vr = vr.plus(VortF ( q.multiply(10.0-size), c).multiply(dir));
			c = new vec2 (c.x + 1., - c.y);
			dir = - dir;
		}
		return vr;
	}

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
	    double x,y;
	    
	    
	    vec2 uv =new vec2(pAffineTP.x,pAffineTP.y);

        vec2 p = uv;
        for (int i = 0; i < strength ; i ++) 
        	p = p.minus(FlowField (p).multiply( 0.03));             	

	    pVarTP.x += pAmount * p.x ;
	    pVarTP.y += pAmount * p.y ;
	    
	    if (pContext.isPreserveZCoordinate()) {
	      pVarTP.z += pAmount * pAffineTP.z;
	    }
	  }
	
	    
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
	   }

	  
	public String getName() {
		return "vortex";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed,time, strength,size});
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SEED)) {
			   seed =   (int)pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
		time =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_STRENGTH)) {
			strength =(int) Tools.limitValue(pValue, 1 , 30);
		}
		else if (pName.equalsIgnoreCase(PARAM_SIZE)) {
			size = Tools.limitValue(pValue, 0.0 , 9.0);
		}
		else
			  throw new IllegalArgumentException(pName);
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

