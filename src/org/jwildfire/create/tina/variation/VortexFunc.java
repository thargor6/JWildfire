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



public class VortexFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :vortex
	 * Date: may 12, 2020
	 * Author: Jesus Sosa
	 * Reference & Credits:
	 *   
	 *   https://www.shadertoy.com/view/MlS3Rh
	 *   Von Karman vortex shedding
	 *   https://encyclopediaofmath.org/wiki/Von_K%C3%A1rm%C3%A1n_vortex_shedding
	 */


	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED= "randomize";

	private static final String PARAM_TIME="time";
	private static final String PARAM_STRENGTH="flowVel";
	private static final String PARAM_ZOOM="zoom";
	private static final String PARAM_UPDOWN="UpDown";
	private static final String PARAM_P1="vSep";
	private static final String PARAM_P2="v_viDistance";
	private static final String PARAM_P3="vDiameter";

	
 	int seed=0;
	double time=0.0;
	int strength=20;
    double zoom=4.0;
    double updown=0.0;
    
    double p1=0.6;
    double p2=4.;
    double p3=1.;

    
	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
 	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_STRENGTH,PARAM_ZOOM,PARAM_UPDOWN,PARAM_P1,PARAM_P2,PARAM_P3};
	
	vec2 VortF (vec2 q, vec2 c)
	{
		vec2 d = q.minus( c);
		return new vec2 (d.y, - d.x).division((G.dot (d, d) + 0.05)).multiply(p3);
	}

	vec2 FlowField (vec2 q)
	{
		vec2 vr, c;
		double dir = 1.;
		c = new vec2 (G.mod (time, 10.) - 20., p1 * dir);
		vr = new vec2 (0.);
		for (int k = 0; k < 30; k ++) {
			vr = vr.plus(VortF ( q.multiply(10.0-zoom), c).multiply(dir));
			c = new vec2 (c.x + p2, - c.y);
			dir = - dir;
		}
		return vr;
	}

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
	    double x,y;
	    
	    
	    vec2 uv =new vec2(pAffineTP.x,pAffineTP.y);

        vec2 p = uv.add(new vec2(0.0,updown));
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
		return (new Object[] {  seed,time, strength,zoom, updown,p1,p2,p3});
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
			strength =(int) Tools.limitValue(pValue, 1 , 50);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.0 , 9.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_UPDOWN)) {
			updown = pValue;  //  Tools.limitValue(pValue, -2.0 , 2.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_P1)) {
		  p1 =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_P2)) {
			  p2 =pValue;
			}
		else if (pName.equalsIgnoreCase(PARAM_P3)) {
			  p3 =pValue;
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

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "   float2 uv =make_float2(__x,__y);"
	    		+"   float2 p = uv + make_float2(0.0,__vortex_UpDown);"
	    		+"   for (int i = 0; i < __vortex_flowVel ; i ++) "
	    		+"      	p = p-vortex_FlowField (p, __vortex_time, __vortex_zoom,__vortex_vSep,__vortex_v_viDistance,__vortex_vDiameter  )* 0.03;"
	    		+"   __px += __vortex * p.x ;"
	    		+"   __py += __vortex * p.y ;"
	            + (context.isPreserveZCoordinate() ? "__pz += __vortex * __z;\n" : "");
	  }
	 
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__	float2  vortex_VortF  (float2 q, float2 c, float p3)"
	    		+"	{"
	    		+"		float2 d = q- c;"
	    		+"		return make_float2 (d.y, - d.x)/(dot (d, d) + 0.05)*p3;"
	    		+"	}"
	    		
	    		+"__device__	float2  vortex_FlowField  (float2 q, float time , float zoom, float p1, float p2, float p3)"
	    		+"	{"
	    		+"		float2 vr, c;"
	    		+"		float dir = 1.;"
	    		+"		c = make_float2 (mod (time, 10.) - 20., p1 * dir);"
	    		+"		vr = make_float2 (0.0,0.0);"
	    		+"		for (int k = 0; k < 30; k ++) {"
	    		+"			vr = vr+ vortex_VortF  ( q*(10.0-zoom), c , p3)*dir;"
	    		+"			c = make_float2 (c.x + p2, - c.y);"
	    		+"			dir = - dir;"
	    		+"		}"
	    		+"		return vr;"
	    		+"	}";
	  }	
}

