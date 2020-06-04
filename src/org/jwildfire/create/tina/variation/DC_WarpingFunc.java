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



public class DC_WarpingFunc  extends DC_BaseFunc {

	/*
	 * Variation :dc_warping
	 * Date: may 12, 2020
	 * Author: Jesus Sosa
	 * Reference & Credits:
	 *   
	 *   https://www.shadertoy.com/view/MdSXzz
	 */


	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED= "randomize";

//	private static final String PARAM_X0= "XShift";
//	private static final String PARAM_Y0= "YShift";
	private static final String PARAM_ZOOM = "zoom";

 	
 	int seed=0;

    double x0=0;
    double y0=0;
    double zoom=1.0;

    
	Random randomize=new Random(seed);
	
 	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_ZOOM};

	
	mat2 m = new mat2( 0.80,  0.60, -0.60,  0.80 );

	double hash( vec2 p )
	{
		double h = G.dot(p,new vec2(127.1,311.7));
	    return -1.0 + 2.0*G.fract(sin(h)*43758.5453123);
	}

	double noise( vec2 p )
	{
	    vec2 i = G.floor( p );
	    vec2 f = G.fract( p );
		vec2 t0= new vec2(3.0).minus(new vec2(2.0)).multiply(f);
		vec2 u = f.multiply(f).multiply(t0);  

	    return G.mix( G.mix( hash( i.plus( new vec2(0.0,0.0)) ), 
	                     hash( i.plus( new vec2(1.0,0.0)) ), u.x),
	                G.mix( hash( i.plus( new vec2(0.0,1.0)) ), 
	                     hash( i.plus(new vec2(1.0,1.0)) ), u.x), u.y);
	}

	double fbm( vec2 p )
	{
	    double f = 0.0;
	    f += 0.5000*noise( p ); 
	    p = m.times(p.multiply(new vec2(2.02)));
	    f += 0.2500*noise( p );
	    p = m.times(p.multiply(new vec2(2.03)));
	    f += 0.1250*noise( p );
	    p = m.times(p.multiply(new vec2(2.01)));
	    f += 0.0625*noise( p );
	    return f/0.9375;
	}

	vec2 fbm2( vec2 p )
	{
	    return new vec2( fbm(new vec2(p.x,p.y)), fbm(new vec2(p.y,p.x)) );
	}

	vec3 map( vec2 p )
	{   
	    p = p.multiply(new vec2(0.7));

	    vec2 t1=p.multiply(4.);
	    
	    //vec2 t2=  p.plus( fbm2((p.plus( fbm2(t1))).multiply(2.0)).plus(new vec2(-1.*time ))).plus(1.0*time);
	    vec2 t2=  p.plus( fbm2((p.plus( fbm2(t1))).multiply(2.0)));
	    
	    vec2 t0=fbm2(t2 );
	    double f=G.dot(t0 , new vec2(1.0,-1.0) );
	    double bl = G.smoothstep( -0.8, 0.8, f );

	    double ti = G.smoothstep( -1.0, 1.0, fbm(p) );

	    return G.mix( G.mix( new vec3(0.50,0.00,0.00), 
	                     new vec3(1.00,0.75,0.35), ti ), 
	                     new vec3(0.00,0.00,0.02), bl );
	}
	
	public vec3 getRGBColor(double xp,double yp)
	{
	    vec2 p =new vec2(xp*zoom,yp*zoom);
	    
        p=p.plus(new vec2(x0,y0));
        	    
	    double e = 0.0045;

	    vec3 colc = map( p               ); 
	    double gc = G.dot(colc,new vec3(0.333));
	    vec3 cola = map( p.plus(new vec2(e,0.0)) );
	    double ga = G.dot(cola,new vec3(0.333));
	    vec3 colb = map( p.plus(new vec2(0.0,e)) );
	    double gb = G.dot(colb,new vec3(0.333));
	    
	    vec3 nor = G.normalize( new vec3(ga-gc, e, gb-gc ) );

	    vec3 col = colc;
	    col = col.plus(new  vec3(1.0,0.7,0.6).multiply(8.0*G.abs(2.0*gc-ga-gb)));      
	    col = col.multiply(1.0+0.2*nor.y*nor.y); ;
	    col = col.plus(0.05*nor.y*nor.y*nor.y);  
	    
	    
	    vec2 q = new vec2((xp+1.)/2.0,(yp+1.)/2.0);
	    col = col.multiply(G.pow(16.0*q.x*q.y*(1.0-q.x)*(1.0-q.y),0.1)); 
	    
        return new vec3(col.x,col.y,col.z);
	}
	
	    
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
	   }

	  
	public String getName() {
		return "dc_warping";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //
		return joinArrays(new Object[] { seed,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SEED)) {
			   seed=(int)pValue;
			   randomize=new Random(seed);
               x0=2.0*randomize.nextDouble()-1.0;
               y0=2.0*randomize.nextDouble()-1.0;
		}
//		else if (pName.equalsIgnoreCase(PARAM_X0)) {
//			x0 =pValue;
//		}
//		else if (pName.equalsIgnoreCase(PARAM_Y0)) {
//			y0 =pValue;
//		}
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

