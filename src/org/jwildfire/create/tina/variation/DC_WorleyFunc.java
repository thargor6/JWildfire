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
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class DC_WorleyFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_cairotiles
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

	vec2 H(vec2 n)
	{
	    return G.fract( new vec2(1,12.34).plus(1e4 * sin( n.x+n.y/.7)  ) ).multiply(.7).plus(.3);
	}

	public vec3 getRGBColor(double xp,double yp)
	{

	    
		vec2 U=new vec2(xp,yp).multiply(zoom);
	    
	    vec2 p;
	    vec2 c;
	    vec3 O = new vec3(0.0);

	    double l;
	    
	    O = O.plus(1e9).minus(O);  // --- Worley noise: return O.xyz = sorted distance to first 3 nodes
	    
	    for (int k=0; k<9; k++) // replace loops i,j: -1..1
	    { // windows Angle bug with ,, instead of {}
	                p = G.ceil(U).plus( new vec2(k-k/3*3,k/3)).minus(2.); // cell id = floor(U)+vec2(i,j)
	                c = H(p).plus( p).minus(U);
	                l = G.dot(c , c);        // distance^2 to its node
	                
	                if ( l < O.x ) { 
	                	O.y=O.x;
	                    O.z=O.y;
	                    O.x=l;       // ordered 3 min distances
	                }
	                else if( l < O.y ) 
	    	        { O.z =O.y ;
	    	          O.y=l; 
	    	        }else if(l < O.z )
	                    O.z=l; 
//	                  else
//	            	     l;
	    }
	    O = G.sqrt(O).multiply(5.); 
	    
	    
	    // --- smooth distance to borders and nodes
	    
	 // l = 1./(1./(O.y-O.x)+1./(O.z-O.x)); // Formula (c) Fabrice NEYRET - BSD3:mention author.
	 // O += smoothstep(.0,.3, l-.5) -O;
	    O = O.minus(O.x);
	    O = O.plus(4.*( O.y/(O.y/O.z+1.) - .5 )).minus(O);  // simplified form


		return O;
	}
 	

	

	public String getName() {
		return "dc_worley";
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
	
}

