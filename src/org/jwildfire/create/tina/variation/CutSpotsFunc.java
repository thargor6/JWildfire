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



public class CutSpotsFunc  extends VariationFunc {

	/*
	 * Variation :cut_spots
	 * Date: November 25, 2019
	 * Author: Jesus Sosa
	 * Reference & credits: https://www.shadertoy.com/view/WsyXRd
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_MODE = "mode";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	
    int mode=1;
	int seed=0;
    double zoom=0.30;
    int invert=0;
    double time=0;


	Random randomize=new Random(seed);
    double x0=0.,y0=0.;
    

 	
    
	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_SEED,PARAM_ZOOM,PARAM_INVERT};

	double random ( vec2 st) {
	    return G.fract(Math.sin(G.dot(new vec2(st.x,st.y), new vec2(12.9898,78.233)))* 43758.5453123);
	}

	double noise (vec2 st) {
	    vec2 i = G.floor(st);
	    vec2 f = G.fract(st);

	    double a = random(i);
	    double b = random(i.plus(new vec2(1.0, 0.0)));
	    double c = random(i.plus(new vec2(0.0, 1.0)));
	    double d = random(i.plus(new vec2(1.0, 1.0)));

	    vec2 u = f.multiply(f).multiply( (new vec2(3.0).minus( f.multiply(2.0))) );
	   
	    return G.mix(a, b, u.x) +
	            (c - a)* u.y * (1.0 - u.x) +
	            (d - b) * u.x * u.y;
	}
	
	double fbm ( vec2 st) {
	    double value = 0.0;
	    double amplitude = .5;
	    double frequency = 0.;
	    for (int i = 0; i < 6; i++) {
	        value += amplitude * noise(st);
	        st =st.multiply(2.);
	        amplitude *= .5;
	    }
	    return value;
	}

	double remap(double low1, double high1, double low2, double high2, double value) {
		return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
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
		    
		    
		    vec2 uv =new vec2(x*zoom*43.0,y*zoom*43.0);
            uv=uv.plus(new vec2(x0,y0));

            double black=0.0;
            double white=1.0;
            double f=fbm(uv);
            double color=0.0;
            
            if(f<0.4)
            {
            	double f2=fbm( new vec2(uv.y,uv.x).plus(new vec2( 1.0) )) / 2.25;
            	color=G.mix(white, white, remap(0.25, 0.38, 0.0, 1.0, f - 0.06));
            	if(f<f2)
            	  color=white;
            }
            else
            {
              color=black;	
            }
 	
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
		  x0=seed*randomize.nextDouble();
		  y0=seed*randomize.nextDouble();

	   }

	  
	public String getName() {
		return "cut_spots";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  mode,seed, zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if(pName.equalsIgnoreCase(PARAM_SEED))
		{
			   seed =   (int)pValue;
		       randomize=new Random(seed);
			   x0=seed*randomize.nextDouble();
			   y0=seed*randomize.nextDouble();
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

