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



public class DC_SunFlowerFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_sincos
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_STEP = "step";
	private static final String PARAM_N = "N";
	private static final String PARAM_POLAR = "Polar";
	private static final String PARAM_DOTS = "Dots";
	private static final String PARAM_GRIDX = "GridX";
	private static final String PARAM_GRIDY = "GridY";



	double zoom=7.0;
	double Step=0.1;
	int N=30;
	int  Polar=1;
	int  Dots=1;
	int  GridX=1;
	int  GridY=1;

	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_STEP,PARAM_N,PARAM_POLAR,PARAM_DOTS,PARAM_GRIDX,PARAM_GRIDY};


	


	public double S(double d,double r)
	{
		return G.smoothstep( r, 0., d); // antialiased draw. r >= 1.5
	}

	double line(vec2 p, vec2 a,vec2 b) {  // https://www.shadertoy.com/view/4dcfW8
	    p = p.minus(a);
	    b = b.minus(a);
		double h = G.clamp(G.dot(p, b) / G.dot(b, b), 0., 1.); // proj coord on line
		return G.length(p.minus(b.multiply( h)));                       // dist to segment
	}

	public double L(vec2 U, double x,double y)
	{ 
		return line( U, G.floor(U.plus(.5)).minus(new vec2(x,y)), G.floor(U.plus(.5)).plus(new vec2(x,y)));
	}

	public vec3 getRGBColor(double xp,double yp)
	{

	    
		vec2 U=new vec2(xp,yp).multiply(zoom);
        vec2 J=new vec2(1.0);

	    double r = Step, y, l, w; 
	    
	    vec3 O=new vec3(0.);// n  

	    l = G.length(U);
	    J.x = l*6.28;                    // jacobian for dots
	    U = new vec2( G.atan2(U.y,U.x)/6.28 , l ).multiply(N);          // polar coords
	      
	//    w = G.length(G.fwidth(new vec2( U.x + r*U.y, U.y) ));      // jacobian for lines width
	    w=.1;
	    U.x += r* G.floor(U.y+.5);                        // dot offsetting 
	    if(GridX==1)
	      O.x  = S( L(U,((r<0)?-1.:1.)-r,1), w );
	    if(GridY==1)
	       O.y  = S( L(U,  -r,1), w );
	   
	    if(GridX==0 && GridY==0)
	    {
	      O.z  = S( L(U,((r<0)?-.5:.5)-r,1), w );
	      U.x+=.5; O.z  += S( L(U,((r<0)?-.5:.5)-r,1), w ); U.x-=.5;
	    }
	    if(Polar==1)
	         O = O.plus(.5* S( L(U,1,0),    w ));
	    if(Dots==1)
	    {
	      U = G.fract( U.plus(.5) ).minus(.5);                         // draw dots
	      O = O.plus(S( G.length(U.multiply(J))-.3, N*2./2000. ));
	    }
		return O;
	}
 	

	

	public String getName() {
		return "dc_sunflower";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,Step,N,Polar,Dots,GridX,GridY},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_STEP)) {
			Step =Tools.limitValue(pValue, -1.00 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_N)) {
			N = (int)pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_POLAR)) {
			Polar = (int)Tools.limitValue(pValue, 0 , 1);;
		}
		else if (pName.equalsIgnoreCase(PARAM_DOTS)) {
			Dots = (int)Tools.limitValue(pValue, 0 , 1);;
		}
		else if (pName.equalsIgnoreCase(PARAM_GRIDX)) {
			GridX = (int)Tools.limitValue(pValue, 0 , 1);;
		}
		else if (pName.equalsIgnoreCase(PARAM_GRIDY)) {
			GridY = (int)Tools.limitValue(pValue, 0 , 1);;
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

