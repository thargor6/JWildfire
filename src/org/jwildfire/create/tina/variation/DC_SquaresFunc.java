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



public class DC_SquaresFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_squares
	 * Autor: Jesus Sosa
	 * Date: Februarey 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_DC = "ColorOnly";
	private static final String PARAM_SEED = "Seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_N = "N";

	private static final String PARAM_DIR = "Direction";
	private static final String PARAM_FR = "Red Fac.";
	private static final String PARAM_FG = "Green Fac.";
	private static final String PARAM_FB = "Blue Fac.";

	private static final String PARAM_GRADIENT = "Gradient"; 


	int colorOnly=0;

	private int seed = 5000;
	double time=85.5;
	int N=3;
	int dir=1;


    double FR=1.8,FG=1.9,FB=2.2;
	int gradient=0;

	  
		Random randomize=new Random(seed);
		
	 	long last_time=System.currentTimeMillis();
	 	long elapsed_time=0;
	



	private static final String[] paramNames = { PARAM_DC,PARAM_SEED,PARAM_TIME,PARAM_N,PARAM_DIR, PARAM_FR,PARAM_FG,PARAM_FB,PARAM_GRADIENT};



	  public boolean hit(vec2 p)
	  {
	      double direction; // -1.0 to zoom out
	      
	      if(dir==1)
	    	  direction=0.1;
	      else
	    	  direction=-1.0;
	      
	      vec2 sectors;
	      int lim=N;
	      vec2 coordIter = p.division(Math.pow(0.5, G.mod(direction*time, 1.0)));
	  	
	      for (int i=0; i < lim; i++) {
	          sectors = (G.floor(coordIter.multiply( 3.0)));
	          if (sectors.x == 1 && sectors.y == 1) {
	              // make a hole
	              return false;
	          } else {
	              // map current sector to whole carpet
	              coordIter = coordIter.multiply(3.0).minus(sectors);
	          }
	      }

	      return true;
	  }
		
	public vec3 getRGBColor(double xp,double yp)
	{

		// Half the width and half the height gives the position of the center of the screen
		
		vec2 coordOrig = new vec2( Math.abs(xp), Math.abs(yp));

	    coordOrig = G.mod(coordOrig, 1.0);
		vec3 color = new vec3(G.cos(time), Math.tan(time), G.sin(time));
		for(int i = 0; i < 4; i++) {
			if (hit( new vec2((double) i*0.1).plus( coordOrig)))
				color = new vec3(1.0).minus(color);
		}
		return new vec3( G.cos(color.x*FR), G.cos(color.y*FG), G.cos(color.z*FB));
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
			uV.x=pContext.random()-0.5;
			uV.y=pContext.random()-0.5;
		}

		color=getRGBColor(uV.x,uV.y);
		tcolor=dbl2int(color); 

		if(gradient==0)
		{
			pVarTP.rgbColor  =true;;
			pVarTP.redColor  =tcolor[0];
			pVarTP.greenColor=tcolor[1];
			pVarTP.blueColor =tcolor[2];
		}
		else
		{
			Layer layer=pXForm.getOwner();
			RGBPalette palette=layer.getPalette();      	  
			RGBColor col=findKey(palette,tcolor[0],tcolor[1],tcolor[2]);

			pVarTP.rgbColor  =true;;
			pVarTP.redColor  =col.getRed();
			pVarTP.greenColor=col.getGreen();
			pVarTP.blueColor =col.getBlue();
		}

		pVarTP.x+= pAmount*(uV.x);
		pVarTP.y+= pAmount*(uV.y);
	}
	

	public String getName() {
		return "dc_squares";
	}

	public String[] getParameterNames() {
		return paramNames;
	}

	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { colorOnly,seed, time,N,dir,FR,FG,FB,gradient};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_DC)) {
			colorOnly = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (PARAM_SEED.equalsIgnoreCase(pName)) 
		{	   seed =  (int)pValue;
	       randomize=new Random(seed);
	          long current_time = System.currentTimeMillis();
	          elapsed_time += (current_time - last_time);
	          last_time = current_time;
	          time = (double) (elapsed_time / 1000.0);
	    }
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = Tools.limitValue(pValue, 0.0 , 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_N)) {
			N =(int)Tools.limitValue(pValue, 1 , 20);
		}
		else if (pName.equalsIgnoreCase(PARAM_DIR)) {
			dir =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_FR)) {
			FR =Tools.limitValue(pValue, 0.0 , 5.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_FG)) {
			FG =Tools.limitValue(pValue, 0.0 , 5.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_FB)) {
			FB =Tools.limitValue(pValue, 0.0 , 5.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_GRADIENT)) {
			gradient = (int)Tools.limitValue(pValue, 0 , 1);
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

