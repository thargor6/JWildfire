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



public class TunnelFunc  extends VariationFunc {

	/*
	 * Variation : tunnel
	 * Date: march 5, 2021
	 * Jesus Sosa
	 * Reference & Credits: 
	 * https://www.shadertoy.com/view/3tSGRd
	 */


	private static final long serialVersionUID = 1L;




	private static final String PARAM_SX = "Sx";
	private static final String PARAM_SY = "Sy";

 	double sx=200.;
 	double sy=50.;

 	
	private static final String[] additionalParamNames = { PARAM_SX,PARAM_SY};
	
	
	double distance(vec2 p0,vec2 p1)
	{
	  return G.length(p0.minus(p1));
	}
	 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  
		    vec2 uv=new vec2( pAffineTP.x, pAffineTP.y).plus(new vec2(0.5));

		    vec4 deform = new vec4(0.1);
		    double DEFORM_SIZE = deform.z;
		    double MAX_DISTORTION = deform.w;

		    vec2 dist = new vec2(0.5).minus(uv);
		    vec2 sample_shift=new vec2(0.0);
		     
		    double distortion = -Math.sqrt(0.25 - Math.pow(uv.y * deform.x - deform.x * 0.5, 2.0)) * DEFORM_SIZE + DEFORM_SIZE * 0.5; 
		    
		    sample_shift.x = distortion * dist.x * sx;
		    
		    double deform_y_fixed = (MAX_DISTORTION - distortion) * deform.y;
		    sample_shift.y = sy* deform_y_fixed * dist.y;

		    pVarTP.x = pAmount * (pVarTP.x + sample_shift.x);
		    pVarTP.y = pAmount * (pVarTP.y + sample_shift.y);
		    
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	   }

	  
	public String getName() {
		return "tunnel";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		 return (new Object[] {sx,sy});
	}

	public void setParameter(String pName, double pValue) {
		if(pName.equalsIgnoreCase(PARAM_SX))
		{
			sx = pValue;
		}
		else 		if(pName.equalsIgnoreCase(PARAM_SY))
		{
			sy = pValue;
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

