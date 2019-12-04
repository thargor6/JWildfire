package org.jwildfire.create.tina.variation;


import static org.jwildfire.base.mathlib.MathLib.floor;
import static org.jwildfire.base.mathlib.MathLib.log;

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



public class CutAlienTextFunc  extends VariationFunc {

	/*
	 * Variation :cut_alientext
	 * Date: october 16, 2019
	 * Reference:  https://www.shadertoy.com/view/4lscz8
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_SUBDIVS = "subdivisons";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	
	int seed=1000;
	int mode=1;

	int maxSubdivisions=3;
	
    double zoom=1.0;
    int invert=0;

	Random randomize=new Random(seed);
 	
    double x0=0.,y0=0.;
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_SUBDIVS,PARAM_ZOOM,PARAM_INVERT};

	double random2d(vec2 n) 
	{ 
		return G.fract(Math.sin(G.dot(n, new vec2(129.9898, 4.1414))) * 2398.5453);
	}

	public vec2 getCellIJ(vec2 uv, double gridDims){
		return G.floor(uv.multiply(gridDims)).division(gridDims);
	}

	public double letter(vec2 coord, double size)
	{
		vec2 gp = G.floor(coord.division(size).multiply(7.)); // global
		vec2 rp = G.floor(G.fract(coord.division(size)).multiply(7.)); // repeated
		vec2 odd = G.fract(rp.multiply(0.5)).multiply( 2.);
		double rnd = random2d(gp);
		double c = Math.max(odd.x, odd.y) * G.step(0.5, rnd); // random lines
		c += Math.min(odd.x, odd.y); // fill corner and center points
		c *= rp.x * (6. - rp.x); // cropping
		c *= rp.y * (6. - rp.y);
		return G.clamp(c, 0., 1.);
	}
	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    double x,y,px_center,py_center;
		    
		    if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		      px_center=0.0;
		      py_center=0.0;
		    }else
		    {
		     x=pContext.random();
		     y=pContext.random();
		      px_center=0.5;
		      py_center=0.5;		     
		    }
		    
		    // Scaling and translation.
            vec2 uv=new vec2(x*zoom,y*zoom);
		    uv=uv.plus(new vec2(x0,y0));
	
		    uv.y -= y0;
		    double dims=2.0;
		    double cellRand;
		    vec2 ij;
		    
		   	for(int i = 0; i <= maxSubdivisions ; i++) { 
		        ij = getCellIJ(uv, dims);
		        cellRand = random2d(ij);
		        dims *= 2.0;
		        //decide whether to subdivide cells again
		        double cellRand2 = random2d(ij.plus( 454.4543));
		        if (cellRand2 > 0.3){
		        	break; 
		        }
		    }
		   
		    //draw letters    
		    double color = letter(uv, 1.0 / (dims));
            
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color>0.0)
		      { x=0;
		        y=0;
		        pVarTP.doHide = true;	        
		      }
		    } else
		    {
			      if (color<=0.0)
			      { x=0;
			        y=0;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * (x-px_center);
		    pVarTP.y = pAmount * (y-py_center);
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
		return "cut_alientext";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed, mode,maxSubdivisions,zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		 if(pName.equalsIgnoreCase(PARAM_SEED))
			{
				   seed =   (int)pValue;
			       randomize=new Random(seed);
			}
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_SUBDIVS)) {
			maxSubdivisions =(int)Tools.limitValue(pValue, 0 , 4);
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
	
}

