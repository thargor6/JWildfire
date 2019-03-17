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



public class DC_MandBrotFunc  extends  DC_BaseFunc {

	/*
	 * Variation : dc_mandbrot
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_DC = "ColorOnly";
	private static final String PARAM_NITERS = "nIters";
	private static final String PARAM_N = "N";
	private static final String PARAM_COMPLEXITY = "Complexity";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_RED = "red";
	private static final String PARAM_GREEN = "green";
	private static final String PARAM_BLUE = "blue";
	private static final String PARAM_GRADIENT = "Gradient"; 


	
	int colorOnly=0;

	int nIters=250;
	int N=8;
	double complexity=0.995;
	private int seed = 10000;
	double time=0.0;
	double zoom=4.0;

	double red=0.0314;
	double green= 0.02;
	double blue=0.011;
	
	int gradient=0;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	private static final String[] paramNames = { PARAM_DC,PARAM_NITERS,PARAM_N,PARAM_COMPLEXITY ,PARAM_SEED,PARAM_TIME,PARAM_ZOOM,PARAM_RED,PARAM_GREEN,PARAM_BLUE,PARAM_GRADIENT};



      public vec3 mandelbrot(vec2 p) {
    	  vec2 s = p;
    	  double d = 0.0, l;
    		
    	  for (int i = 0; i < nIters; i++) {
    	    s = new vec2(s.x * s.x - s.y * s.y + p.x, 2.0 * s.x * s.y + p.y);
    	    l = G.length(s);
    	    d += l + 0.5;
    	    if (l > 2.0)
    	    	return new vec3(G.sin(d * red), G.sin(d *green ), G.sin(d * blue));
    	  }
    		
    	  return new vec3(0.0);
    	}
      
      public vec2 FRACTALIZE2(vec2 p, double time) {
    		double s = .5;
    		double cs = G.cos(time);
    		double sn = G.sin(time);
    		mat2 rot = new mat2(cs, sn, -sn, cs);
    		for (int i = 0; i < N ; i++) {
    			p = G.abs(p).division( G.dot(p, p)).minus(s);
    			p = p.times( rot);
    			s *= complexity;
    		}
    		return p;
    	}

	public vec3 getRGBColor(double xp,double yp)
	{

		double t=time/65.*10.;;
		
		vec2 p=new vec2(xp,yp).multiply(zoom);


		vec3 col=new vec3(0.0);

			p = p.multiply(G.fract(t * .0001) * 100. + 1.);
			p = FRACTALIZE2(p,t);
		  double f = G.sin(t * 0.10 + 99.0) * 0.5 + 0.5;
		  p = p.multiply(G.pow(1.5, f * (-31.0)));
		  p = p.plus(new vec2(-1.002029, 0.303864));
			
		  col = new vec3( new vec3(1.).minus( mandelbrot(p)));

		return col;
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
		return "dc_mandbrot";
	}

	public String[] getParameterNames() {
		return paramNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { colorOnly,nIters,N,complexity,seed,time,zoom, red,green,blue, gradient};
	}


	
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_DC)) {
			colorOnly = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_NITERS)) {
			
			nIters = (int)Tools.limitValue(pValue, 10 , 1000);
		}
		else if (pName.equalsIgnoreCase(PARAM_N)) {
			
			N = (int)Tools.limitValue(pValue, 2 , 10);
		}
		else if (pName.equalsIgnoreCase(PARAM_COMPLEXITY)) {
			
			complexity = Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (PARAM_SEED.equalsIgnoreCase(pName)) 
		{	   seed =  (int) pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);

	    }
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
	
			time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			
			zoom = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_RED)) {
			
			red = Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_GREEN)) {
			
			green = Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_BLUE)) {
			
			blue = Tools.limitValue(pValue, 0.0 , 1.0);
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

