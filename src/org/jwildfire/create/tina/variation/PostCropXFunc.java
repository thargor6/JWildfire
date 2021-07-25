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



public class  PostCropXFunc  extends VariationFunc  implements SupportsGPU {

	/*
	 * Variation : post_crop_x
	 * Autor: Jesus Sosa
	 * Date: february 3, 2020
	 * Reference & Credits : https://www.shadertoy.com/view/3dKSDc
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_RADIUS = "radius";
	private static final String PARAM_THICK = "width";
	private static final String PARAM_INVERT = "invert";
	


    double radius=0.1;
    double w=0.5;
	private int invert = 0;
	
	

	private static final String[] additionalParamNames = { PARAM_RADIUS,PARAM_THICK,PARAM_INVERT};


	//signed distance to a Rounded X
	
	double sdRoundedX(  vec2 p, double w,  double r )
	{
	    p = G.abs(p);
	    return G.length( p.minus( new vec2(Math.min(p.x+p.y,w) *0.5))) - r;
	}

	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;
		  
	      x= pVarTP.x;
	      y =pVarTP.y;

  
			vec2 p=new vec2(x,y);

			double d=0.;

			d = sdRoundedX( p, w ,radius );
    
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
		return "post_crop_x";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { radius,w,invert};
	}

	public void setParameter(String pName, double pValue) {

		if (pName.equalsIgnoreCase(PARAM_RADIUS)) {
			radius = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_THICK)) {
			w = Tools.limitValue(pValue, 0. ,1.0);
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

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_POST, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	@Override
	public String getGPUCode(FlameTransformationContext context) {
	    return   "	  float x,y;"
	    		+"	      x= __px;"
	    		+"	      y =__py;"
	    		+"  "
	    		+"			float2 p=make_float2(x,y);"
	    		+"			float d=0.0f;"
	    		+"			d = post_crop_x_sdRoundedX( p, varpar->post_crop_x_width , varpar->post_crop_x_radius );"
	    		+"    "
	    		+"		    __doHide=false;"
	    		+"		    if( varpar->post_crop_x_invert ==0)"
	    		+"		    {"
	    		+"		      if (d>0.0)"
	    		+"		      { x=0.;"
	    		+"		        y=0.;"
	    		+"		        __doHide = true;"
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (d<=0.0 )"
	    		+"			      { x=0.;"
	    		+"			        y=0.;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = varpar->post_crop_x * x;"
	    		+"		    __py = varpar->post_crop_x * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += varpar->post_crop_x * __z;\n" : "");
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return  
	    		"__device__ float post_crop_x_sdRoundedX(  float2 p, float w,  float r )"
	    		+"{"
	    		+"    p = abs(p);"
	    		+"    float t=fminf(p.x+p.y,w) *0.5;"
	    		+"    return length( p-( make_float2(t,t))) - r;"
	    		+"}";
	  }

}


