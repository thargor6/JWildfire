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



public class DC_VortexFunc  extends DC_BaseFunc {

	/*
	 * Variation :dc_vortex
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
	private static final String PARAM_SOFTNESS="Blending";
	private static final String PARAM_ZOOM = "zoom";

 	
 	int seed=0;
	double time=0.0;
	int strength=10;
	double softness=5.0;
    double zoom=1.0;

    

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
 	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_STRENGTH,PARAM_SOFTNESS,PARAM_ZOOM};

	 vec4 cHashA4 = new vec4 (0., 1., 57., 58.);
	 vec3 cHashA3 = new vec3 (1., 57., 113.);
	 double cHashM = 43758.54;

vec4 Hashv4f (double p)
{
  return G.fract (G.sin (cHashA4.add(p)).multiply( cHashM));
}

double Noisefv2 (vec2 p)
{
  vec2 i = G.floor (p);
  vec2 f = G.fract (p);
  f = f.multiply(f).multiply( (new vec2(3.).minus( f.multiply(2.))));
  vec4 t = Hashv4f (G.dot (i, new vec2(cHashA3.x,cHashA3.y)));
  return G.mix (G.mix (t.x, t.y, f.x), G.mix (t.z, t.w, f.x), f.y);
}

double Fbm2 (vec2 p)
{
  double s = 0.;
  double a = 1.;
  for (int i = 0; i < 6; i ++) {
    s += a * Noisefv2 (p);
    a *= 0.5;
    p =p.multiply(2.);
  }
  return s;
}
	


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
    vr = vr.plus(VortF ( q.multiply(4.), c).multiply(dir));
    c = new vec2 (c.x + 1., - c.y);
    dir = - dir;
  }
  return vr;
}
	
	public vec3 getRGBColor(double xp,double yp)
	{
	    vec2 uv =new vec2(xp*zoom,yp*zoom);
	    
 //       uv=uv.plus(new vec2(x0,y0));
        	    
        vec2 p = uv;
        for (int i = 0; i < strength ; i ++) 
        	p = p.minus(FlowField (p).multiply( 0.03));
        vec3 col =  new vec3 (0.5, 0.5, 0.5).multiply(Fbm2 (p.multiply(softness).plus(new vec2 (-0.1 * time, 0.))));
	    
        return new vec3(col.x,col.y,col.z);
	}
	
	    
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
	   }

	  
	public String getName() {
		return "dc_vortex";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //
		return joinArrays(new Object[] { seed,time,strength,softness,zoom},super.getParameterValues());
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
			strength =(int) Tools.limitValue(pValue, 1 , 15);
		}
		else if (pName.equalsIgnoreCase(PARAM_SOFTNESS)) {
			softness =Tools.limitValue(pValue, 1. , 5.);
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

