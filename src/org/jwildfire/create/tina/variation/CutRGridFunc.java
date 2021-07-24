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



public class  CutRGridFunc  extends VariationFunc  implements SupportsGPU {

	/*
	 * Variation : cut_rgrid
	 * Autor: Jesus Sosa
	 * Date: August 20, 2019
	 * Reference & Credits:  https://www.shadertoy.com/view/3tfGW7
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	private static final String PARAM_ANGLE = "angle";


    int mode=1;
	double zoom=7.0;
	private int invert = 0;
	int angle=0;


	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_ZOOM,PARAM_INVERT,PARAM_ANGLE};


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

		    double pi = Math.acos(-1.);   
			vec2 p=new vec2(x*zoom,y*zoom);

		    double th = G.mod(angle * pi / 180., pi * 2.);
		    double gridsize = (.5 + G.abs(sin(th * 2.)) * (Math.sqrt(2.) / 2. - .5)) * 2.;

		    int flip = 0;

		    if(G.fract(th / pi + .25) > .5)
		    {
		        p = p.minus(.5);
		        flip = 1;
		    }

		    p = p.multiply(gridsize);
		    vec2 cp = G.floor(p.division(gridsize));
		    p = G.mod(p, gridsize).minus(gridsize / 2.);
		    p = p.multiply(G.mod(cp, 2.).multiply( 2.).minus( 1.));
		    p = p.times(new mat2(cos(th), sin(th), -sin(th), cos(th)));

		    double w = zoom / 2000. * 1.5;   
		    double color = G.smoothstep(-w, +w, G.max(Math.abs(p.x), Math.abs(p.y)) - .5);

		    if(flip==1)
		        color = 1. - color;
		    if(flip==1 && color < .5 && (Math.abs(p.x) - Math.abs(p.y)) * G.sign(G.fract(th / pi) - .5) > 0.)
		        color = .4;
		    if(flip==0 && color < .5 && (G.mod(cp.x + cp.y, 2.) - .5) > 0.)
		        color = .4;
		    
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color==0.0)
		      { x=0.;
		        y=0.;
		        pVarTP.doHide = true;
		      }
		    } else
		    {
			      if (color>0.0 )
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
		return "cut_rgrid";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { mode,zoom,invert,angle};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			   invert =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ANGLE)) {
			angle = (int)Tools.limitValue(pValue, 0 , 90);
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
	    		+"		  if( __cut_rgrid_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT()-0.5;"
	    		+"		     y=RANDFLOAT()-0.5;"
	    		+"		    }"
	    		+"		    float pi = acosf(-1.);"
	    		+"			float2 p=make_float2(x* __cut_rgrid_zoom ,y* __cut_rgrid_zoom );"
	    		+"		    float th = mod( __cut_rgrid_angle  * PI / 180., PI * 2.);"
	    		+"		    float gridsize = (.5 + fabsf(sin(th * 2.)) * (sqrt(2.) / 2. - .5)) * 2.;"
	    		+"		    int flip = 0;"
	    		+"		    if(fract(th / pi + .25) > .5)"
	    		+"		    {"
	    		+"		        p = p-(.5);"
	    		+"		        flip = 1;"
	    		+"		    }"
	    		+"		    p = p*(gridsize);"
	    		+"		    float2 cp = floorf(p/(gridsize));"
	    		+"		    p = mod(p, gridsize)-(gridsize / 2.);"
	    		+"		    p = p*(mod(cp, 2.)*( 2.)-( 1.));"
	    		+"          Mat2 m;"
	    		+"          Mat2_Init(&m, cos(th), sin(th), -sin(th), cos(th));"
	    		+"		    p = times(&m,p);"
	    		+"		    float w =  __cut_rgrid_zoom  / 2000. * 1.5;   "
	    		+"		    float color = smoothstep(-w, +w, fmaxf(fabsf(p.x), fabsf(p.y)) - .5);"
	    		+"		    if(flip==1)"
	    		+"		        color = 1. - color;"
	    		+"		    if(flip==1 && color < .5 && (fabsf(p.x) - fabsf(p.y)) * sign(fract(th / pi) - .5) > 0.)"
	    		+"		        color = .4;"
	    		+"		    if(flip==0 && color < .5 && (mod(cp.x + cp.y, 2.) - .5) > 0.)"
	    		+"		        color = .4;"
	    		+"		    "
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_rgrid_invert ==0)"
	    		+"		    {"
	    		+"		      if (color==0.0)"
	    		+"		      { x=0.;"
	    		+"		        y=0.;"
	    		+"		        __doHide = true;"
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color>0.0 )"
	    		+"			      { x=0.;"
	    		+"			        y=0.;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __cut_rgrid * x;"
	    		+"		    __py = __cut_rgrid * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_rgrid * __z;\n" : "");
	  }
}


