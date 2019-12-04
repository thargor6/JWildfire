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
import js.glsl.vec4;



public class CutKaleidoFunc  extends VariationFunc {

	/*
	 * Variation :cut_kaleido
	 * Date: august 29, 2019
	 * Jesus Sosa
	 * Reference & Credits: https://www.shadertoy.com/view/MttSzS
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_MODE = "mode";	
	private static final String PARAM_TIME = "time";
	private static final String PARAM_N = "n";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	


	int seed=1000;
	int mode=1;
	double time=0.0;
	int n=6;
    double zoom=0.50;
    int invert=0;

    vec2[] c=new vec2[n];

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_TIME,PARAM_N,PARAM_ZOOM,PARAM_INVERT};


	void moveCenters()
	{
	    for(int i=0; i<n; i++)
	    {
	     double fi = 2.0*3.14*((double)i+0.02*time)/(double)(n);
	     c[i]= new vec2(0.5*Math.sin(fi), 0.5*Math.cos(fi));
	    }
	}

	double avgDistance(vec2 uv)
	{
	 	double d = 1.0;
	    double k = 100.0+10.0*Math.sin(time/5.0);
	    for(int i=0; i<n; i++)    
	     	d+= Math.sin(k*G.distance(uv,c[i])); 
	    return d/(double)n;  
	}

	double distToColor(double d)
	{
	  //  return vec4(1.,1.0-sin(d*12.0),1.0-cos(d*13.0),1.0);
	    // return vec4(1.0-sin(d*12.0));
	    return (0.0-Math.cos(d*13.0));
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
		     x=pContext.random()-0.5;
		     y=pContext.random()-0.5;
		      px_center=0.0;
		      py_center=0.0;		     
		    }
		    
		    
		    vec2 u =new vec2(x*zoom,y*zoom);
		    
		    moveCenters();
			double color = distToColor(avgDistance(u));
              	
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

		  c=new vec2[n];
	   }

	  
	public String getName() {
		return "cut_kaleido";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed,mode, time,n,zoom,invert});
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
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_N)) {
			n =(int)Tools.limitValue(pValue, 2 , 20);
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

