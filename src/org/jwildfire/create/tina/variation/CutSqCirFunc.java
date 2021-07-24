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
import org.jwildfire.image.SimpleImage;

import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class  CutSqCirFunc  extends VariationFunc implements SupportsGPU  {
	  private static final long serialVersionUID = 1L;

	/*
	 * Variation : cut_sqrcir
	 * Autor: Jesus Sosa
	 * Date: August 20, 2019
	 * Reference 
	 */



	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	private static final String PARAM_POWER = "power";


    int mode=1;
	double zoom=2.;
	private int invert = 0;
	double power=2.0;


	private static final String[] paramNames = { PARAM_MODE,PARAM_ZOOM,PARAM_INVERT,PARAM_POWER};

	
    @Override
    public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    }


	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;  
		  if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		    }else
		    {
		     x=pContext.random()-0.5;
		     y=pContext.random()-0.5;		     
		    }
		       
			vec2 uv=new vec2(x*zoom,y*zoom);
		    int color=0;  

		    double lhs =  Math.pow(Math.abs(uv.x-uv.y), power) +   Math.pow(Math.abs(uv.y+uv.x), power);
		    double rhs = 1.0;   
		    color = (lhs <= rhs)?1:0;
		    
		    pVarTP.doHide=false;
		    
		    if(invert==0)
		    {
		      if ( color==0)
		      { x=0;
		        y=0;
		        pVarTP.doHide = true;
		      }
		    } else
		    {
			      if (color==1)
			      { x=0;
			        y=0;
			        pVarTP.doHide = true;
			        return;
			      }
		    }
		    pVarTP.x = pAmount * x;
		    pVarTP.y = pAmount * y;
		    
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }

	public String getName() {
		return "cut_sqcir";
	}

	public String[] getParameterNames() {
		return paramNames;
	}


	public Object[] getParameterValues() { 
		return new Object[] { mode,zoom,invert,power};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			   mode =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			   invert =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_POWER)) {
			power = Tools.limitValue(pValue, 0.0 , 2.0);
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		  float x,y;  "
	    		+"		  if( __cut_sqcir_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT()-0.5;"
	    		+"		     y=RANDFLOAT()-0.5;		     "
	    		+"		    }"
	    		+"		       "
	    		+"			float2 uv=make_float2(x* __cut_sqcir_zoom ,y* __cut_sqcir_zoom );"
	    		+"		    int color=0;"
	    		+"		    float lhs =  pow(abs(uv.x-uv.y),  __cut_sqcir_power ) +   pow(abs(uv.y+uv.x),  __cut_sqcir_power );"
	    		+"		    float rhs = 1.0f;   "
	    		+"		    color = (lhs <= rhs)?1:0;"
	    		+"		    "
	    		+"		    __doHide=false;"
	    		+"		    "
	    		+"		    if( __cut_sqcir_invert ==0)"
	    		+"		    {"
	    		+"		      if ( color==0)"
	    		+"		      { x=0.0f;"
	    		+"		        y=0.0f;"
	    		+"		        __doHide = true;"
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color==1)"
	    		+"			      { x=0.0f;"
	    		+"			        y=0.0f;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __cut_sqcir * x;"
	    		+"		    __py = __cut_sqcir * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_sqcir * __z;\n" : "");
	  }
}


