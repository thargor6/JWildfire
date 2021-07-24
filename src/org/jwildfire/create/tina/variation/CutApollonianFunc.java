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



public class  CutApollonianFunc  extends VariationFunc   implements SupportsGPU {

	/*
	 * Variation : apollonian
	 * Autor: Jesus Sosa
	 * Date: August 20, 2019
	 * Reference & Credits:  https://www.shadertoy.com/view/Mt2fzR
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_MODE = "mode";
	private static final String PARAM_LEVELS = "levels";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";

	int mode=1;
	int levels=4;
	double zoom=2.0;
	private int invert = 0;



	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_LEVELS,PARAM_ZOOM,PARAM_INVERT};

	
	public double apollonian(vec2 p)
	{
	    double scale = 1.0;
	    double t0 = 1e20, t1 = 1e20;
	    for(int i = 0; i < levels; ++i)
	    {
	        p =  G.fract(p.multiply(0.5).plus(+0.5)).multiply(2.0).minus(1);
	        double k=(1.34)/G.dot(p,p);
	        p=p.multiply(k);
	        t0 = G.min(t0, G.dot(p,p));
	        t1 = G.min(t1, G.max(G.abs(p.x), G.abs(p.y)));
	        scale*=k;
	    }
	    double d=0.25*G.abs(p.y)/scale;
	    d=G.smoothstep(0.001, 0.002,d);
	    return d;
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
	    
		  vec2 p=new vec2(x,y);
	 p=new vec2(x*zoom,y*zoom);	

		double col=0.;
		col+= apollonian(p);

		pVarTP.doHide=false;
		if(invert==0)
		{
			if (col>0.0)
			{ x=0.;
			y=0.;
			pVarTP.doHide = true;
			}
		} else
		{
			if (col<=0.0 )
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
		return "cut_apollonian";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { 
		return new Object[] { mode,levels,zoom,invert};
	}

	public void setParameter(String pName, double pValue) {
		 if (pName.equalsIgnoreCase(PARAM_MODE)) {
			   mode =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		 else if (pName.equalsIgnoreCase(PARAM_LEVELS)) {
			levels = (int)Tools.limitValue(pValue, 1 , 10);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;  \n"
	    		+"		  if( __cut_apollonian_mode ==0)\n"
	    		+"		    {\n"
	    		+"		      x= __x;\n"
	    		+"		      y =__y;\n"
	    		+"		    }else\n"
	    		+"		    {\n"
	    		+"		     x=RANDFLOAT()-0.5;\n"
	    		+"		     y=RANDFLOAT()-0.5;\n"
	    		+"		    }\n"
	    		+"	    \n"
	    		+"	    float2 p=make_float2(x*__cut_apollonian_zoom,y* __cut_apollonian_zoom );	\n"
	    		+"		float col=0.0f;\n"
	    		+"		col =col + cut_apollonian_apollo(p,__cut_apollonian_levels);\n"
	    		+"		__doHide=false;\n"
	    		+"		if( __cut_apollonian_invert ==0)\n"
	    		+"		{\n"
	    		+"			if (col>0.0f)\n"
	    		+"			{ x=0.0f;\n"
	    		+"			  y=0.0f;\n"
	    		+"			__doHide = true;\n"
	    		+"			}\n"
	    		+"		} else\n"
	    		+"		{\n"
	    		+"			if (col<=0.0f )\n"
	    		+"			{ x=0.0f;\n"
	    		+"			  y=0.0f;\n"
	    		+"			__doHide = true;\n"
	    		+"			}\n"
	    		+"		}\n"
	    		+"		__px = __cut_apollonian * x;\n"
	    		+"		__py = __cut_apollonian * y;\n"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_apollonian * __z;\n" : "");
	  }
	 
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return "	__device__ float  cut_apollonian_apollo (float2 xy,int n)\n"
	    		+"	{\n"
	    		+"	    float scale = 1.0;\n"
	    		+"      float2 p=xy;\n"
	    		+"	    float t0 = 1.0e20, t1 = 1.0e20;\n"
	    		+"	    for(int i = 0; i < n; ++i)\n"
	    		+"	    {\n"
	    		+"	        p =  fract(p*(0.5)+(+0.5))*(2.0)-(1.0f);\n"
	    		+"	        float k=(1.34)/dot(p,p);\n"
	    		+"	        p=p*(k);\n"
	    		+"	        t0 = min(t0, dot(p,p));\n"
	    		+"	        t1 = min(t1, max(abs(p.x), abs(p.y)));\n"
	    		+"	        scale*=k;\n"
	    		+"	    }\n"
	    		+"	    float d=0.25*abs(p.y)/scale;\n"
	    		+"	    d=smoothstep(0.001, 0.002,d);\n"
	    		+"	    return d;\n"
	    		+"	}\n";
	  }
}


