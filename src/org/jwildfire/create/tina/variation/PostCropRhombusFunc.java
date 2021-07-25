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



public class  PostCropRhombusFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation : crop_rhombus
	 * Autor: Jesus Sosa
	 * Date: february 3, 2020
	 * Reference & Credits : https://www.shadertoy.com/view/llVyWW
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_WIDTH = "width";
	private static final String PARAM_HEIGHT = "height";
	private static final String PARAM_INVERT = "invert";



	double width=0.5;
	double height=0.5;
	private int invert = 0;

	double thick=0.001;

	private static final String[] additionalParamNames = { PARAM_WIDTH,PARAM_HEIGHT,PARAM_INVERT,};


double ndot(vec2 a, vec2 b ) { return a.x*b.x - a.y*b.y; }

double sdRhombus( vec2 p, vec2 b ) 
{
    vec2 q = G.abs(p);

    double h = G.clamp( (-2.0*ndot(q,b) + ndot(b,b) )/G.dot(b,b), -1.0, 1.0 );
    double d = G.length( q.minus( b.multiply(new vec2(1.0-h,1.0+h)).multiply(0.5) ));
    d *= G.sign( q.x*b.y + q.y*b.x - b.x*b.y );
    
	return d;
}

	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;
		  
	      x= pVarTP.x;
	      y =pVarTP.y;

  
			vec2 p=new vec2(x,y);

			double d=0.;

			// rhombus
			{
				// vec2 ra = G.cos( new vec2(0.0,1.57).plus(time)  ).multiply(0.3).plus(0.4);
				vec2 ra=new vec2(width,height);
			    d = sdRhombus( p, ra );
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
		return "post_crop_rhombus";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { width,height,invert};
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_POST, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	@Override
	public String getGPUCode(FlameTransformationContext context) {
	    return   "		  float x,y;"
	    		+"		  "
	    		+"	      x= __px;"
	    		+"	      y =__py;"
	    		+"  "
	    		+"			float2 p=make_float2(x,y);"
	    		+"			float d=0.;"
	    		+"			float2 ra=make_float2( __post_crop_rhombus_width ,__post_crop_rhombus_height);"
	    		+"			d = post_crop_rhombus_sdRhombus( p, ra );"
	    		+"		    __doHide=false;"
	    		+"		    if( __post_crop_rhombus_invert ==0)"
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
	    		+"		    __px = __post_crop_rhombus * x;"
	    		+"		    __py = __post_crop_rhombus * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_rhombus * __z;\n" : "");
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__ float  post_crop_rhombus_ndot (float2 a, float2 b ) { return a.x*b.x - a.y*b.y; }"
	    		+"__device__ float  post_crop_rhombus_sdRhombus ( float2 p, float2 b ) "
	    		+"{"
	    		+"    float2 q = abs(p);"
	    		+"    float h = clamp( (-2.0* post_crop_rhombus_ndot (q,b) +  post_crop_rhombus_ndot (b,b) )/dot(b,b), -1.0, 1.0 );"
	    		+"    float d = length( q-( b*(make_float2(1.0-h,1.0+h))*(0.5) ));"
	    		+"    d *= sign( q.x*b.y + q.y*b.x - b.x*b.y );"
	    		+"	return d;"
	    		+"}";
	  }
}


