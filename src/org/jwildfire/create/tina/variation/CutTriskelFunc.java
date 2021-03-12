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



public class CutTriskelFunc  extends VariationFunc {

	/*
	 * Variation :cut_triskel
	 * Date: august 29, 2019
	 * Reference & Credits:  https://www.shadertoy.com/view/XlVXRW
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;

	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	

	int mode=1;
    double zoom=1.0;
    int invert=0;


    
	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_ZOOM,PARAM_INVERT};
 	
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		double x,y;
		
		
		double l,b;
		vec2 A;
	    double color=0.0;
	    
		if(mode==0)
		{
			x= pAffineTP.x;
			y =pAffineTP.y;

		}else
		{
			x=2.*pContext.random()-1.0;
			y=2.*pContext.random()-1.0;		     
		}

		vec2 U =new vec2(x*zoom,y*zoom);
		U.y+=0.1;

		color-=color;
		
		for (int i=0;i<3;i++)
		{
			U = new mat2(-.5,.866,-.866,-.5).times(U);
			A = U.plus(new vec2(.03,-.577)); 
			l = 3.*G.length(A);
			b = G.atan2(A.y,A.x);  
			color = G.max(color, l + G.fract(b/7.+.3) < 2. ? .5+.5*Math.sin(b+6.24*l) : 0.);
		}
		color = G.smoothstep(.0,.1,Math.abs(color-.5)) - G.smoothstep(.8,.9,color);
		
		pVarTP.doHide=false;
		if(invert==0)
		{
			if (color>0.50)
			{ x=0;
			y=0;
			pVarTP.doHide = true;	        
			}
		} else
		{
			if (color<=0.50)
			{ x=0;
			y=0;
			pVarTP.doHide = true;
			}
		}
		pVarTP.x = pAmount * x;
		pVarTP.y = pAmount * y;
		if (pContext.isPreserveZCoordinate()) {
			pVarTP.z += pAmount * pAffineTP.z;
		}
	}
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	   }

	  
	public String getName() {
		return "cut_triskel";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  mode,zoom,invert});
	}

	public void setParameter(String pName, double pValue) {

		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			invert =(int)Tools.limitValue(pValue, 0 , 1);
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

