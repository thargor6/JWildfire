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



public class  PostCropCrossFunc  extends VariationFunc  {

	/*
	 * Variation : crop_cross
	 * Autor: Jesus Sosa
	 * Date: february 3, 2020
	 * Reference & Credits : https://www.shadertoy.com/view/llVyWW
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_WIDTH = "width";
	private static final String PARAM_SIZE = "Size";
	private static final String PARAM_ROUND= "round";
	private static final String PARAM_INVERT = "invert";


	double width=0.5;
	double Size=0.5;
	double thick=0.001;
	private int invert = 0;




	private static final String[] additionalParamNames = { PARAM_WIDTH,PARAM_SIZE,PARAM_ROUND,PARAM_INVERT};


double sdCross( vec2 p,  vec2 b, double r ) 
{
    p = G.abs(p);
    p = (p.y>p.x) ? new vec2(p.y,p.x) : new vec2(p.x,p.y);
    
	vec2  q = p.minus(b);
    double k = G.max(q.y,q.x);
    vec2  w = (k>0.0) ? q : new vec2(b.y-p.x,-k);
    
    return G.sign(k)*G.length(G.max(w,0.0)) + r;
}



	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;
		  
	      x= pVarTP.x;
	      y =pVarTP.y;

  
			vec2 p=new vec2(x,y);

			double d=0.;

 // Cross
			{
				//vec2 si = G.cos( new vec2(0.0,1.57).plus(time) ).multiply(0.3).plus(0.8); 	    
				vec2 si=new vec2(width,Size);
				if( si.x<si.y ) 
				   	si=new vec2(si.y,si.x);
				    // corner radious
				 double ra = 0.1*sin(thick*1.2);
				 d = sdCross( p, si, ra );
			}
	
			


	    
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (d>0.0)
		      { x=0.;
		        y=0.;
		        pVarTP.doHide = true;
		      }
		    } else
		    {
			      if (d<=0.0 )
			      { x=0.;
			        y=0.;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * x;
		    pVarTP.y = pAmount * y;
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }

	public String getName() {
		return "post_crop_cross";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { width,Size,thick,invert};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_WIDTH)) {
			width = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_SIZE)) {
			Size = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_ROUND)) {
			thick = pValue;
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
	  @Override
	  public int getPriority() {
	    return 1;
	  }  
}


