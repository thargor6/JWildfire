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



public class CutBasicTruchetFunc  extends VariationFunc {

	/*
	 * Variation :cut_btruchet
	 * Date: November 19, 2019
	 * Author: Jesus Sosa
	 * Reference & credits: https://www.shadertoy.com/view/WsGSzD
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_MODE = "mode";
	private static final String PARAM_WIDTH = "width";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	
    int mode=1;
	double width=2.0;
	int seed=1000;
    double zoom=10.0;
    int invert=0;
    double time=0;


	Random randomize=new Random(seed);
    double x0=0.,y0=0.;
    

 	
    
	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_WIDTH,PARAM_SEED,PARAM_ZOOM,PARAM_INVERT};

	double hash1( int n ) {
	    // integer hash copied from Hugo Elias
		n = (n << 13) ^ n;
	    n = n * (n * n * 15731 + 789221) + 1376312589;
	    return (double)( n * 1.0)/(double)(0x7fffffff);
	}

	//cross
	double tile1(vec2 uv){
	    double d = Math.min(G.abs(uv.x),G.abs(uv.y));
	    return d;
	}

	//two circles
	double tile2(vec2 uv){
	    if(uv.y<-uv.x)
	    {
	    	uv.x = -uv.x;
	    	uv.y = -uv.y;
	    }
		double d = Math.abs(G.distance(uv,new vec2(1,1))-1.);  
	    return d;
	}

	//rotation of tile2
	double tile3(vec2 uv){
	    return tile2(new vec2(-uv.x,uv.y));
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
		    
		    
		    vec2 uv =new vec2(x*zoom,y*zoom);
            uv=uv.plus(new vec2(x0,y0));

		    double e = zoom*width/1000.0;
		    
		  	vec2  tile = G.floor(uv);
		    double id   = tile.x*10000.+tile.y;
			      uv   = (uv.minus(G.floor(uv)).minus(new vec2(.5)).multiply(2.));
		    
		    double color = 0.0;

		   
		    double t = hash1((int)id);
		    double d = 1e20;
		    if(t<0.33){
		        d = tile1(uv);
		    }else if(t<0.67){
		        d = tile2(uv);
		    }else{
		        d = tile3(uv);
		    }

		    color=G.smoothstep(e*5.,e,d);

              	
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
		return "cut_btruchet";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  mode,width,seed, zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_WIDTH)) {
			width = Tools.limitValue(pValue, 0.50 , 5.0);
		}
		else if(pName.equalsIgnoreCase(PARAM_SEED))
		{
			   seed =   (int)pValue;
		       randomize=new Random(seed);
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

