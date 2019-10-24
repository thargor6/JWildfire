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



public class  TruchetFlowFunc  extends VariationFunc  {

	/*
	 * Variation : truchetflow
	 * Autor: Jesus Sosa
	 * Date: September 25, 2019
	 * Reference & Credits: https://www.shadertoy.com/view/ts33R2
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED = "randomize";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";


	double zoom=5.0;
	private int invert = 0;
	double tol=0.0;


    int seed=0;
    double uvx =0.0;
    double uvy =0.0;
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_ZOOM,PARAM_INVERT};

	
	public double  N21(vec2 p) {
	    p = G.fract(p.multiply(new vec2(234.234, 823.923)));
	    p = p.plus(G.dot(p, p.plus(42.34)));
	    return G.fract(p.x*p.y);
	}

	
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y,cx,cy;  
		  
		  x=pContext.random();
		  y=pContext.random();
		  cx=0.5;
		  cy=0.5;
	    
		vec2 uv=new vec2(x-0.5,y-0.5);	
		vec2 UV=new vec2(x,y);
		vec2 UV2 = G.abs(new vec2(UV.x-.5,UV.y-.5));

	    uvx= seed*G.sin(seed*180./Math.PI);
	    uvy= seed*G.sin(seed*180.0/Math.PI); //*0.1+time*0.05;
		
       uv=new vec2(uv.x*zoom-uvx,uv.y*zoom-uvy);
		
	    vec2 gv = G.fract(uv).minus(.5);
	    vec2 id = G.floor(uv);
		  double n = N21(id);
		  
		  double band = -.03+G.pow(0.5*UV.y, 1.5);
		  double width = 0.01+0.1*UV.x;
		    
		    if (n > 0.5) gv.x *= -1.;
		    double s=.5*G.sign(gv.x+gv.y+.001);
		    double d = G.length(new vec2(gv.x-s,gv.y-s))-0.5;
		    d = G.abs(d);
		    d = G.abs(d-band);
		    d = G.abs(d-band);
		    d = G.abs(d-band); 
		    d = G.abs(d)-width;
		    
		    double fuzzy = 0.01+0.2*G.pow(UV2.x+UV2.y, 2.);
		    double col = G.smoothstep(fuzzy, 0.0, d);
		    

		pVarTP.doHide=false;
		if(invert==0)
		{
			if (col>tol)
			{ x=0.;
			y=0.;
			pVarTP.doHide = true;
			}
		} else
		{
			if (col<=tol)
			{ x=0.;
			y=0.;
			pVarTP.doHide = true;
			}
		}
		pVarTP.x = pAmount * (x-cx);
		pVarTP.y = pAmount * (y-cy);
		if (pContext.isPreserveZCoordinate()) {
			pVarTP.z += pAmount * pAffineTP.z;
		}
	}

	public String getName() {
		return "truchetflow";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { 
		return new Object[] { seed,zoom,invert};
	}

	public void setParameter(String pName, double pValue) {
		 if (pName.equalsIgnoreCase(PARAM_SEED)) {
			 seed=(int)pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
//		else if (pName.equalsIgnoreCase(PARAM_TOL)) {
//				   tol =   Tools.limitValue(pValue, 0.01 , 0.99);
//		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			   invert =   (int)Tools.limitValue(pValue, 0 , 1);
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


