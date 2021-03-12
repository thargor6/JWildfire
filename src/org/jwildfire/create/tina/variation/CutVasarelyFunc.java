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



public class  CutVasarelyFunc  extends VariationFunc  {

	/*
	 * Variation : cut_vasarely
	 * Autor: Jesus Sosa
	 * Date: August 20, 2019
	 * Reference & Credits:  https://www.shadertoy.com/view/ltdXR8
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	private static final String PARAM_SIZE   = "size";
	


    int mode=1;
	double zoom=1.0;
	private int invert = 0;
	double size=0.5;




	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_ZOOM,PARAM_INVERT,PARAM_SIZE};

	public mat2 rot(double a)
	{
	 return new mat2 (Math.cos(a), -Math.sin(a), Math.sin(a), Math.cos(a));   
	}

	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;  
		  if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		    }else
		    {
		     x=2.0*pContext.random()-1.0;
		     y=2.0*pContext.random()-1.00;		     
		    }

			vec2 uv = new vec2 (x,y);
			uv=uv.multiply(zoom);
			

		    
		    double l = G.length(uv);
		    double b = Math.max(0.,2.-l/size);
		    
		    double color=0.;
		    color=Math.abs(uv.x)<1.?color-.5-Math.sin( l * Math.sin( G.atan2(uv.y,uv.x) + 1.57 + 4.*b*b)*150.) :color;
		    color -= Math.abs(uv.x)<1.?color-.5-Math.sin( l * Math.sin( G.atan2(uv.y,uv.x) + 1.57 + 4.*b*b)*150.) :color;
			
		    		     
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color<0.1)
		      { x=0.;
		        y=0.;
		        pVarTP.doHide = true;
		      }
		    } else
		    {
			      if (color>=0.1 )
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
		return "cut_vasarely";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { mode,zoom,invert,size};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			   invert =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_SIZE)) {
			   size =   pValue;
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION};
	}

}


