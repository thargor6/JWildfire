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



public class  PostCropTrapezoidFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation : crop_trapezoid
	 * Autor: Jesus Sosa
	 * Date: february 3, 2020
	 * Reference & Credits : https://www.shadertoy.com/view/llVyWW
	 */



	private static final long serialVersionUID = 1L;


	
	private static final String PARAM_BM = "LMay";
	private static final String PARAM_Bm = "LMin";
	private static final String PARAM_HEIGHT= "height";
	private static final String PARAM_INVERT = "invert";
	
	double BM=0.5;
	double Bm=0.2;
	double height=0.50;
    private int invert = 0;
	
	private static final String[] additionalParamNames = { PARAM_BM,PARAM_Bm,PARAM_HEIGHT,PARAM_INVERT};




	double dot2( vec2 v ) { return G.dot(v,v); }

	//trapezoid / capped cone, specialized for Y alignment
	double sdTrapezoid( vec2 p, double r1, double r2, double he )
	{
		vec2 k1 = new vec2(r2,he);
		vec2 k2 = new vec2(r2-r1,2.0*he);

		p.x = G.abs(p.x);
		vec2 ca = new vec2(G.max(0.0,p.x-((p.y<0.0)?r1:r2)), G.abs(p.y)-he);


		double t0=G.dot(k1.minus(p),k2)/dot2(k2);
		vec2 cb = p.minus(k1).plus( k2.multiply(G.clamp( t0, 0.0, 1.0 )));

		double s = (cb.x < 0.0 && ca.y < 0.0) ? -1.0 : 1.0;

		return s*Math.sqrt( Math.min(dot2(ca),dot2(cb)) );
	}
	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;
		  
	        x= pVarTP.x;
	        y =pVarTP.y;
  
			vec2 p=new vec2(x,y);
			double d=0.;

			double ra=BM;
			double rb=Bm;

			d = sdTrapezoid( p, ra, rb, height );
	    
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
		return "post_crop_trapezoid";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { BM,Bm,height,invert};
	}

	public void setParameter(String pName, double pValue) {
       if (pName.equalsIgnoreCase(PARAM_BM)) {
			BM = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_Bm)) {
			Bm = pValue;
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
	    		+""
	    		+"			float2 p=make_float2(x,y);"
	    		+"			float d=0.0f;"
	    		+""
	    		+"				d = post_crop_trapezoid_sdTrapezoid( p, __post_crop_trapezoid_LMay, __post_crop_trapezoid_LMin,  __post_crop_trapezoid_height  );"
	    		+""
	    		+"		    __doHide=false;"
	    		+"		    if( __post_crop_trapezoid_invert ==0)"
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
	    		+"		    __px = __post_crop_trapezoid * x;"
	    		+"		    __py = __post_crop_trapezoid * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __post_crop_trapezoid * __z;\n" : "");
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__ 	float  post_crop_trapezoid_dot2 ( float2 v ) { return dot(v,v); }"
	    		+"	"
	    		+"__device__	float  post_crop_trapezoid_sdTrapezoid ( float2 p, float r1, float r2, float he )"
	    		+"{"
	    		+"	float2 k1 = make_float2(r2,he);"
	    		+"	float2 k2 = make_float2(r2-r1,2.0*he);"
	    		+"	p.x = fabsf(p.x);"
	    		+"  float tmp=(p.y<0.0)?r1:r2;"
	    		+"	float2 ca = make_float2(fmaxf(0.0,p.x-tmp), fabsf(p.y)-he);"
	    		+"	float t0=dot(k1-p,k2)/post_crop_trapezoid_dot2(k2);"
	    		+"	float2 cb = p - k1 +  k2*clamp( t0, 0.0, 1.0 );"
	    		+"	float s = (cb.x < 0.0) && (ca.y < 0.0) ? -1.0 : 1.0;"
	    		+"	return s*sqrtf( fminf( post_crop_trapezoid_dot2 (ca), post_crop_trapezoid_dot2(cb) ) );"
	    		+"}";
	  }
}


