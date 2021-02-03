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
import js.glsl.vec3;
import js.glsl.vec4;



public class DC_PortalFunc  extends DC_BaseFunc {

	/*
	 * Variation :dc_portal
	 * Date: january 9, 2021
	 * Jesus Sosa
	 * Reference & Credits: https://www.shadertoy.com/view/4tBBRw
	 *                      https://www.shadertoy.com/view/Xt2BzD
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_TIME = "time";
	private static final String PARAM_WAVES = "waves";
	private static final String PARAM_ZOOM = "zoom";

	


	int seed=1000;

	double time=0.0;
	double waves=5.;
	double zoom=15.0;



	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_WAVES,PARAM_ZOOM};


	public vec3 getRGBColor(double xp,double yp)
	{
		vec2 uv = new vec2( xp, yp);
	    double d = 1.0 ;
	    d+= Math.sqrt(G.length(uv)) / waves;
	    double t = 1000. + time;
	    double value = d * t + (t * 0.125) * Math.cos(uv.x) * Math.cos(uv.y);
	    double color = Math.sin(value) * 3.0;
	    	
	    double low = Math.abs(color);
	    double med = Math.abs(color) - 1.0;
	    double medHigh = Math.abs(color) - 1.5;
	    double high = Math.abs(color) - 2.0;
	    
	    vec3 lifeColor;
	    vec3 metalColor;
	        
	    if(color > 0.) {
	      metalColor= new vec3(med,medHigh,high);	
	      lifeColor = new vec3(high, high, med);
	    } else {
	      metalColor=new vec3(medHigh,medHigh,medHigh);	
	      lifeColor = new vec3(med, high, high);
	    }	    
		return metalColor;
	}
	 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    double x,y;
		    
		     if(colorOnly==1)
			 {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		    } else
		    {
		     x=pContext.random()-0.5;
		     y=pContext.random()-0.5;		     
		    }
		    
		    
		    vec2 uv =new vec2(x*zoom,y*zoom);
		    
	        vec3 color=getRGBColor(uv.x,uv.y);
	        int[] tcolor=dbl2int(color);
	        
	        //z by color (normalized)
	        double z=greyscale(tcolor[0],tcolor[1],tcolor[2]);
		           
	        if(gradient==0)
	        {
	  	  	
	    	  pVarTP.rgbColor  =true;;
	    	  pVarTP.redColor  =tcolor[0];
	    	  pVarTP.greenColor=tcolor[1];
	    	  pVarTP.blueColor =tcolor[2];
	    		
	        }
	        else if(gradient==1)
	        {

	            	Layer layer=pXForm.getOwner();
	            	RGBPalette palette=layer.getPalette();      	  
	          	    RGBColor col=findKey(palette,tcolor[0],tcolor[1],tcolor[2]);
	          	    
	          	  pVarTP.rgbColor  =true;;
	          	  pVarTP.redColor  =col.getRed();
	          	  pVarTP.greenColor=col.getGreen();
	          	  pVarTP.blueColor =col.getBlue();

	        }
	        else 
	        {
	        	pVarTP.color=z;
	        }
	        
		    pVarTP.x = pAmount * (x);
		    pVarTP.y = pAmount * (y);
		    
	        
		    double dz = z * scale_z + offset_z;
		    if (reset_z == 1) {
		      pVarTP.z = dz;
		    }
		    else {
		      pVarTP.z += dz;
		    }
		    
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	   }

	  
	public String getName() {
		return "dc_portal";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //
		return joinArrays (new Object[] {  seed, time,waves, zoom},super.getParameterValues());
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
		else if (pName.equalsIgnoreCase(PARAM_WAVES)) {
			waves =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
		}
		else
			super.setParameter(pName, pValue);
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

