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



public class DC_PentaTilesFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_sincos
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_BORDER = "border";
	private static final String PARAM_WIDTH = "width";




	double zoom=20.0;
	int  border=1;
	double width=0.1;

	
	

	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_BORDER,PARAM_WIDTH};


	// inspired by http://www.ianislallemand.com/projects/design/generative-tilings
	// http://mathworld.wolfram.com/PentagonTiling.html

	//#define S(v) smoothstep(3., 0., abs(v)/fwidth(v))
	public double s(double v)
	{
		return G.smoothstep(width, 0., v);                    // draw AA region v<0
	}
	
	public double S(double v)
	{
		return s(G.abs(v));                               // draw AA line v=0
	}
	
	public double l(double x,double y,double a,double b)
	{
		return G.dot( new vec2(x,y), G.normalize(new vec2(a,b)) ); // line equation
	}
	
	public double L(double x,double y,double a,double b)
	{
		return S(l(x,y,a,b));                     // draw line equation
	}
	
	//#define P(x,y,a,b) s(l(x,y,a,b))                   // draw region under line
	public double P(double x,double y,double a,double b)
	{
		return G.step(l(x,y,a,b),0.);               // draw region under line
	}

	
	public vec3 getRGBColor(double xp,double yp)
	{

	    
		vec2 U=new vec2(xp,yp).multiply(zoom);

	    vec3 O=new vec3(0.);// n  

		double h = (Math.sqrt(3.)+1.)/2.; 
		double c = Math.sqrt(3.)-1.;
		double r=h+c/2.; // NB: r = sqrt(3)
		double e =  width;
		double b, i,v;
		
		    vec2 T = G.mod(U, r+r).minus(r);
		    vec2 V = G.abs(T);              // first tile in bricks
		                                                     // center = 1 supertile, corners = 1/4 neighboor supertiles

		    O.r = P( V.x, V.y-h, -(c/2.-h), r );             // id 0-7 in supertile
		    O.g = P( V.x-c/2., V.y, -Math.sqrt(3.), 1 );
		    i = G.step(.5,O.r) + 2.*G.step(.5,O.g);
	//	    O.b=    ((i==1. && T.y>0.)?1.:0.)               // <><> not antialiased
	//	            || ((i==3. && T.x>0.)?1.:0.)                // -> re#def P()
	//	            || ((i==0. && T.x>0.)?1.:0.)
	//	            || ((i==2. && T.y>0.)?1.:0.);
            O.b=1.;
		    U = G.floor(U.division((r+r))).multiply( 2.);                          // supertile id
		    if (O.r==0.) U = U.plus(G.sign(T));
		    //O = vec4(U,0,0)/4.;

		    i = (O.r + 2.*O.g + 4.*O.b) + 8.*(U.x + 12.*U.y); // tile id
		  //i = (O.r + 2.*O.g + 4.*O.b)*8.1 +(U.x + 12.*U.y); 
		  //i = O.r + 1.7*O.g + 4.3*O.b + 8.7* (U.x + 12.7*U.y);
		    O = G.cos( new vec3(0,23,21).plus(i) ).multiply(0.6).plus(0.6);      // -> color
		    O =O.multiply( .4+.6*G.fract(1234.*Math.sin(43.*i)));

		    if(border==1)
		    { 
			    b = (double)(  S(V.x)   * G.step(h,V.y)               // tiles border
			              + S(r-V.x) * G.step(V.y,c/2.) 
			              + L( V.x, V.y-h, -(c/2.-h), r )
			              + S(V.y)   * G.step(V.x,c/2.)
			              + S(r-V.y)  * G.step (r-V.x,c/2.)
			              + L( V.x-c/2., V.y, -Math.sqrt(3.), 1 )
			             );
		        O = O.multiply(1.-b);                                      // draw border
		    }

		return O;
	}
 	

	

	public String getName() {
		return "dc_pentatiles";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,border,width},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 1.0 , 100.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_BORDER)) {
			border =(int) Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_WIDTH)) {
			width =Tools.limitValue(pValue, 0.01 , 0.25);
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

