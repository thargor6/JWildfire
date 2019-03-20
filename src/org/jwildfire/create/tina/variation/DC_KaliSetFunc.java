package org.jwildfire.create.tina.variation;

import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.mat2;
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class DC_KaliSetFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_kaliset
	 * Date: February 13, 2019
	 * Jesus Sosa
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "Seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_N = "N";

	private static final String PARAM_SHIFT_X = "ShiftX";
	private static final String PARAM_SHIFT_Y = "ShiftY";






	private int seed = 10000;
	double time=200.;
	double zoom=0.5;
	int N=60;

    double shiftx=-0.22,shifty=-0.21;

	


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	



	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_ZOOM,PARAM_N,PARAM_SHIFT_X,PARAM_SHIFT_Y};



		
		public vec3 getRGBColor(double xp,double yp)
	{

		
		// Half the width and half the height gives the position of the center of the screen
		
		vec2 v = new vec2( xp, yp).multiply(zoom);

		/*
		 * inspired by http://www.fractalforums.com/new-theories-and-research/very-simple-formula-for-fractal-patterns/
		 * a slight(?) different 
		 * public domain
		 */


			double rsum = 0.0;
			double pi2 = 3.6 * 2.0;
			double C = G.cos(time/603. * pi2);
			double S = G.sin(time/407.4* pi2);
			vec2 shift = new vec2(shiftx,shifty);
			double zoom = (time/184.0 + 1.0);
			
			for ( int i = 0; i < N; i++ ){
				double rr = v.x*v.x+v.y*v.y;
				if ( rr > 1.0 ){
					rr = 1.0/rr;
					v.x = v.x * rr;
					v.y = v.y * rr;
				}
				rsum *= 0.99;
				rsum += rr;
				
				v = new vec2( C*v.x-S*v.y, S*v.x+C*v.y ).multiply(zoom).plus(shift);
			}
			
			double col = rsum * 0.5;
			return new vec3( G.cos(col*1.0), G.cos(col*2.0), G.cos(col*4.0));
		}

 	
		public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) 
		{

	        vec3 color=new vec3(0.0); 
			 vec2 uV=new vec2(0.),p=new vec2(0.);
		       int[] tcolor=new int[3];  


			 
		     if(colorOnly==1)
			 {
				 uV.x=pAffineTP.x;
				 uV.y=pAffineTP.y;
			 }
			 else
			 {
		   			 uV.x=2.0*pContext.random()-1.0;
					 uV.y=2.0*pContext.random()-1.0;
			}
	        
	        color=getRGBColor(uV.x,uV.y);
	        tcolor=dbl2int(color);
	        
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

	        pVarTP.x+= pAmount*(uV.x);
	        pVarTP.y+= pAmount*(uV.y);
	        
	        
		    double dz = z * scale_z + offset_z;
		    if (reset_z == 1) {
		      pVarTP.z = dz;
		    }
		    else {
		      pVarTP.z += dz;
		    }
		}
	

	public String getName() {
		return "dc_kaliset";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed, time,zoom,N,shiftx,shifty},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		 if (PARAM_SEED.equalsIgnoreCase(pName)) 
		{	   seed =  (int) pValue;
	       randomize=new Random(seed);
	          long current_time = System.currentTimeMillis();
	          elapsed_time += (current_time - last_time);
	          last_time = current_time;
	          time = (double) (elapsed_time / 1000.0);
	    }
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = Tools.limitValue(pValue, 1.0 , 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.01 , 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_N)) {
			N =(int)Tools.limitValue(pValue, 1 , 100);
		}
		else if (pName.equalsIgnoreCase(PARAM_SHIFT_X)) {
			shiftx =Tools.limitValue(pValue, -5.0 , 5.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_SHIFT_Y)) {
			shifty =Tools.limitValue(pValue, -5.0 , 5.0);
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

