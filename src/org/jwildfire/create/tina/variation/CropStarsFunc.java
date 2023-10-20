package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import js.glsl.G;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class  CropStarsFunc  extends VariationFunc  {

	/*
	 * Variation : crop_star
	 * Autor: Jesus Sosa
	 * Date: february 3, 2020
	 * Reference & Credits : https://www.shadertoy.com/view/llVyWW
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_RADIUS = "radius";
	private static final String PARAM_STARS_N = "n";
	private static final String PARAM_R2 = "r2";
	private static final String PARAM_INVERT = "invert";
	


    double radius=0.35;
	private int n=3;
    double r2=0.333;
	private int invert = 0;
	double thick=0.0;
	

	private static final String[] additionalParamNames = { PARAM_RADIUS,PARAM_STARS_N,PARAM_R2,PARAM_INVERT};



//signed distance to a n-star polygon with external angle en
double sdStar( vec2 p, double r,  int n, double m)
{
 // these 4 lines can be precomputed for a given shape
 double an = 3.141593/(double)(n);
 double en = 6.283185/m;
 vec2  acs = new vec2(Math.cos(an),Math.sin(an));
  vec2  ecs = new vec2(Math.cos(en),Math.sin(en)); //
 //vec2 ecs=new vec2(0,1); // and simplify, for regular polygon,

 // reduce to first sector
 double bn = G.mod(G.atan2(p.x,p.y),2.0*an) - an;
 p = new vec2(cos(bn),G.abs(sin(bn))).multiply(G.length(p));

 // line sdf
 p = p.minus(acs.multiply(r));
 
 p = p.plus(ecs.multiply(G.clamp( -G.dot(p,ecs), 0.0, r*acs.y/ecs.y)));
 
 return G.length(p)*G.sign(p.x);
}



	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;
		  
	      x= pAffineTP.x;
	      y =pAffineTP.y;

  
			vec2 p=new vec2(x,y);
			double d=0.;

// Star
			{
				double a= r2;
				double m = 4.0 + a*a*(n*2.0-4.0);  
				d = sdStar( p,radius,n,m );
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
		return "crop_stars";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { radius,n,r2,invert};
	}

	public void setParameter(String pName, double pValue) {

		if (pName.equalsIgnoreCase(PARAM_RADIUS)) {
			radius = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_STARS_N)) {
			n = (int)Tools.limitValue(pValue, 3 ,9);
		}
		else if (pName.equalsIgnoreCase(PARAM_R2)) {
			r2 = Tools.limitValue(pValue, 0. ,1.0);
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
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_CROP};
	}

}


