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
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class FresnelFunc  extends VariationFunc {

	/*
	 * Variation :fresnel
	 * Date: march 5, 2021
	 * Jesus Sosa
	 * Reference & Credits: 
	 * https://www.shadertoy.com/view/XtKSDm
	 */


	private static final long serialVersionUID = 1L;

	private static final String PARAM_X0 = "x0";
	private static final String PARAM_Y0 = "Y0";
	private static final String PARAM_RING = "ring";
	


 	
 	double x0=0.0;
 	double y0=0.0;
 	double ring = -2.0;
 	
	private static final String[] additionalParamNames = { PARAM_X0,PARAM_Y0,PARAM_RING};
	
	
	double distance(vec2 p0,vec2 p1)
	{
	  return G.length(p0.minus(p1));
	}
	 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    vec2 p=new vec2( pAffineTP.x, pAffineTP.y);

	        double r = distance(p, new vec2(x0,y0));
	        r = G.fract(r*ring);
	        p=  p.multiply(r);

		    pVarTP.x = pAmount * (p.x);
		    pVarTP.y = pAmount * (p.y);
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	   }

	  
	public String getName() {
		return "fresnel";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		 return (new Object[] { x0,y0,ring});
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_X0)) {
			x0 =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_Y0)) {
			y0 =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_RING)) {
			ring =pValue;
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
	    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE};
	  }
}

