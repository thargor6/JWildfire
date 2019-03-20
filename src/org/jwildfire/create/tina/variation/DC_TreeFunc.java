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



public class DC_TreeFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_trees
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_LEVELS = "levels";
	private static final String PARAM_THICKNESS = "thicknes";
	private static final String PARAM_STYLE = "style";
	private static final String PARAM_SHIFT = "shift";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";



	int levels=20;
	double thickness=1000.;
	double style=50.0;
	double shift=1.5;
	private int seed = 10000;
	double time=0.0;
	double zoom=4.0;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	private static final String[] additionalParamNames = { PARAM_LEVELS ,PARAM_THICKNESS,PARAM_STYLE,PARAM_SHIFT,PARAM_SEED,PARAM_TIME,PARAM_ZOOM};

	
	public vec2 po (vec2 v) {
		return new vec2(G.length(v),G.atan2(v.y,v.x));
	}
	
	public vec2 ca (vec2 u) {
		return new vec2(G.cos(u.y),G.sin(u.y)).multiply(u.x);
	}
	
	double ln (vec2 p, vec2 a, vec2 b) { 
	    double r = G.dot(p.minus(a),b.minus(a))/G.dot(b.minus(a),b.minus(a));
	    r = G.clamp(r,0.,1.);
	    p.x+=(0.7+0.5*G.sin(0.1*time))*0.2*G.smoothstep(1.,0.,G.abs(r*2.-1.))*G.sin(3.14159*(r-4.*time));

	    //      return (1.+0.5*r)*length(p-a-(b-a)*r);	     
	    vec2 t0=p.minus(a);
	    vec2 t1=(b.minus(a)).multiply(r);
	    
	    return (1.+0.5*r)*G.length( t0.minus(t1));
	}
	
	public vec3 getRGBColor(double xt,double yt)
	{

		vec2 U=new vec2(xt,yt).multiply(zoom);

	 	double r = 1e9;

		 	U.y += shift;
		 	vec3 col = new vec3(0.0);
		 	for (int k = 1; k < levels ; k++) {
		 		double s1=0.3*(G.sin(2.*time)+0.5*G.sin(4.53*time)+0.1*G.cos(12.2*time));
		 		vec2 t=po(U).add(new vec2(0,1).multiply(s1));
		        U = ca(t);
		        r = G.min(r,ln(U,new vec2(0),new vec2(0,1.)));
		        U.y-=1.;
		        
		        U.x=G.abs(U.x);
		        U= U.multiply(1.4+ 0.1*G.sin(time)+0.05*G.sin(0.2455*time)*((double)k));
		        U = po(U);
		        U.y += 1.+0.5*G.sin(0.553*time)*G.sin(G.sin(time)*(double)k)+0.1*G.sin(0.4*time)+0.05*G.sin(0.554*time);
		        U = ca(U);
		        double s0=G.exp(-thickness*r*r)*style;	        
		        col=col.add(G.sin(new vec3(1,-1.8,1.9).multiply(s0).add(time)));
	
		 	}
		 	col=col.division(18.);
		    return col;
	}
	

	public String getName() {
		return "dc_tree";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { levels,thickness,style,shift,seed,time,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_LEVELS)) {
			
			levels = (int)Tools.limitValue(pValue, 1 , 25);
		}
		else if (pName.equalsIgnoreCase(PARAM_THICKNESS)) {
			
			thickness = (int)Tools.limitValue(pValue, -1500. , 1500.);
		}
		else if (pName.equalsIgnoreCase(PARAM_STYLE)) {
			
			style = Tools.limitValue(pValue, 1. , 100.);
		}
		else if (pName.equalsIgnoreCase(PARAM_SHIFT)) {
			
			shift = Tools.limitValue(pValue, -100. , 100.);
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
	
}

