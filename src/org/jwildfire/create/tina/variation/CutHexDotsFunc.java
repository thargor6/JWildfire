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



public class  CutHexDotsFunc  extends VariationFunc  {

	/*
	 * Variation : cut_hexdots
	 * Autor: Jesus Sosa
	 * Date: September 25, 2019
	 * Reference & Credits: https://www.shadertoy.com/view/MlXyDl
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_MODE = "mode";
	private static final String PARAM_SIZE = "size";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";

	int mode=1;
	double size=0.5;
	double zoom=4.0;
	private int invert = 0;

	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_SIZE,PARAM_ZOOM,PARAM_INVERT};
		
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

		  double x,y,cx,cy;  
		  if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		      cx=0.0;
		      cy=0.0;
		    }else
		    {
		     x=pContext.random()-0.5;
		     y=pContext.random()-0.5;
		     cx=0.0;
		     cy=0.0;
		    }
	    
	    vec2 u=new vec2(x*zoom,y*zoom);
	    		
	    vec2 s = new vec2(1.,1.732);
	    vec2 a = G.mod(u     ,s).multiply(2.).minus(s);
	    vec2 b = G.mod(u.plus(s.multiply(.5)),s).multiply(2.).minus(s);
	    	    	    
        double col=0.;
        col= .8*G.min(G.dot(a,a),G.dot(b,b));   
	    
		pVarTP.doHide=false;
		if(invert==0)
		{
			if (col>size)
			{ x=0.;
			  y=0.;
			pVarTP.doHide = true;
			}
		} else
		{
			if (col<=size)
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
		return "cut_hexdots";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { 
		return new Object[] {mode,size,zoom,invert};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			   mode =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_SIZE)) {
				size = Tools.limitValue(pValue, 0.01 , 1.);
			}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
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


