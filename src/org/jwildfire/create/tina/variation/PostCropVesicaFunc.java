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



public class  PostCropVesicaFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation : crop_shapes
	 * Autor: Jesus Sosa
	 * Date: february 3, 2020
	 * Reference & Credits : https://www.shadertoy.com/view/llVyWW
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_WIDTH = "width";
	private static final String PARAM_HEIGHT = "height";
	private static final String PARAM_INVERT = "invert";


	double width=0.2;
	double height=0.1;
	private int invert = 0;

	private static final String[] additionalParamNames = { PARAM_WIDTH,PARAM_HEIGHT,PARAM_INVERT};



//signed distance to an equilateral triangle
double sdEquilateralTriangle(  vec2 p )
{
	
 double k = Math.sqrt(3.0);
 
 p.x = Math.abs(p.x) - 1.0;
 p.y = p.y + 1.0/k;
 
 if( p.x + k*p.y > 0.0 )
	 p = new vec2( (p.x - k*p.y)/2.0,( -k*p.x - p.y )/2.0);
 
 p.x -= G.clamp( p.x, -2.0, 0.0 );
 
 return -G.length(p)*G.sign(p.y);
}




double sdVesica(vec2 p, double r, double d)
{
    p = G.abs(p);

    double b = Math.sqrt(r*r-d*d);  // can delay this sqrt by rewriting the comparison
    return ((p.y-b)*d > p.x*b) ? G.length(p.minus(new vec2(0.0,b)))
                               : G.length(p.minus(new vec2(-d,0.0)))-r;
}


	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;
		  
	      x= pVarTP.x;
	      y =pVarTP.y;

  
			vec2 p=new vec2(x,y);

			double d=0.;

 // Vesica
			{
				// d = sdVesica( p, 1.1, 0.8 + 0.2*sin(time) );
				d = sdVesica( p, 1.0 + height, 0.8 + width );
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
		return "post_crop_vesica";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] {  width,height,invert};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_WIDTH)) {
			width = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_HEIGHT)) {
			height = pValue;
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_POST,VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	@Override
	public String getGPUCode(FlameTransformationContext context) {
	    return   "		  float x,y;"
	    		+"		  "
	    		+"	      x= __px;"
	    		+"	      y =__py;"
	    		+"			float2 p=make_float2(x,y);"
	    		+"			float d=0.;"
	    		+"				d = post_crop_vesica_sdVesica( p, 1.0 +  varpar->post_crop_vesica_height , 0.8 +  varpar->post_crop_vesica_width  );"
	    		+"		    __doHide=false;"
	    		+"		    if( varpar->post_crop_vesica_invert ==0)"
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
	    		+"		    __px = varpar->post_crop_vesica * x;"
	    		+"		    __py = varpar->post_crop_vesica * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += varpar->post_crop_vesica * __z;\n" : "");
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__ 	float  post_crop_vesica_sdVesica (float2 p, float r, float d)"
	    		+"{"
	    		+"    p = abs(p);"
	    		+"    float b = sqrt(r*r-d*d);  "
	    		+"    return ((p.y-b)*d > p.x*b) ? length(p-(make_float2(0.0,b)))"
	    		+"                               : length(p-(make_float2(-d,0.0)))-r;"
	    		+"}";
	  }

}




