package org.jwildfire.create.tina.variation;


import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.vec2;



public class CutMagFieldFunc  extends VariationFunc {

	/*
	 * Variation :cut_magfield
	 * Date: November 19, 2019
	 * Author: Jesus Sosa
	 * Reference & credits: https://www.shadertoy.com/view/3lc3WN
	 */


	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED = "randomize";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_DENSITY = "linesDensity";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_INVERT = "invert";
	
    int mode=1;
	double time=0.0;
	double density=3.0;
	int seed=0;
    double zoom=3.0;
    int invert=0;
    
	Random randomize=new Random(seed);
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;

    double spacing = 1./10.*density;
 	
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_DENSITY,PARAM_ZOOM,PARAM_MODE,PARAM_INVERT};

	
	double rand(double n)
	{
		return G.fract(Math.sin(n) * 43758.5453123);
	}
	
	public vec2 force(vec2 p, vec2 a) {
	    // return normalize(p - a) / distance(p, a);
	    // optim by Fabrice:
	  	p = p.minus(a);
		return p.division( G.dot(p,p));
	}

	vec2 calcVelocity(vec2 p) {
	  	vec2 v = new vec2(0);
	  	vec2 a;
	  	double o, r, m;
	 	double s = 1.;
	  	double limit = 15.;
	  	for (double i = 0.; i < limit; i++) {
	    	r = rand(i/limit)-.5;
	    	m = rand(i+1.)-.5;
	    	m *= (time+(23.78*1000.))*2.;
	    	o = i + r + m;
	    	a = new vec2(
	      		Math.sin(o / limit * Math.PI * 2.),
	      		Math.cos(o / limit * Math.PI * 2.)
	    	);
	    	s *= -1.;
	    	v = v.minus(force(p, a).multiply( s));
	  	}  
	  	v = G.normalize(v);
	  	return v;
	}

	double calcDerivitive(vec2 v, vec2 p) {
	    double d = 2. / 600.0;
		return (
	          G.length(v.minus(calcVelocity(p.plus(new vec2(0,d)) )))
	        + G.length(v.minus(calcVelocity(p.plus(new  vec2(d,0)))))
	        + G.length(v.minus(calcVelocity(p.plus(new vec2(d,d)))))
	        + G.length(v.minus(calcVelocity(p.plus(new vec2(d,-d)))))
	    ) / 4.;
	}

	
	public double saturate(double x)
	{
		return G.clamp(x, 0., 1.);
	}
	 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;  
		  if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		    }else
		    {
		     x=2.0*pContext.random()-1.0;
		     y=2.0*pContext.random()-1.0;		     
		    }
		    

		    vec2 p =new vec2(x*zoom,y*zoom);

		    spacing = 1./(10.*density);
            vec2 v = calcVelocity(p);
            double a = G.atan2(v.x, v.y) / Math.PI / 2.;
            double lines = G.fract(a / spacing);
            // create stripes
            lines = Math.min(lines, 1. - lines) * 2.;
            // thin stripes into lines
           	lines /= calcDerivitive(v, p) / spacing;
            // maintain constant line width across different screen sizes
           	lines -= 1000.0 * .0005;
            // don't blow out contrast when blending below
            lines = saturate(lines);

            double disc = G.length(p) - 1.;
     //       disc /= fwidth(disc);
            disc=disc/0.001;
            disc = saturate(disc);
            lines = G.mix(1. - lines, lines, disc);
            lines = Math.pow(lines, 1./2.2);
		    
		    double color = lines;
              	
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color==0.0)
		      { x=0;
		        y=0;
		        pVarTP.doHide = true;	        
		      }
		    } else
		    {
			      if (color>0.0)
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
		  randomize=new Random(seed);

	   }

	  
	public String getName() {
		return "cut_magfield";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed,time, density, zoom,mode ,invert});
	}

	public void setParameter(String pName, double pValue) {
		if(pName.equalsIgnoreCase(PARAM_SEED))
		{
			   seed =   (int)pValue;
		       randomize=new Random(seed);
	          long current_time = System.currentTimeMillis();
              elapsed_time += (current_time - last_time);
	         last_time = current_time;
	         time = (double) (elapsed_time / 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_DENSITY)) {
			density =Tools.limitValue(pValue, 1.0 , 10.);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
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
	
}

