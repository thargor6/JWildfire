package org.jwildfire.create.tina.variation;

import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.render.FlameRenderer;

import js.glsl.G;
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;



public class DC_InversionFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_apollonian
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019 
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ITERS = "Iterations";
	private static final String PARAM_VSCALEX = "ScaleX";
	private static final String PARAM_VSCALEY = "ScaleY";



	private int seed = 10000;
	double time=0.0;
	private int Iterations = 50;

	private double scalex = 1.5;
	private double scaley = 1.5;
	
	vec2 vScale=new vec2(1.5);
	


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	
 	
 	
 	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_ITERS,PARAM_VSCALEX,PARAM_VSCALEY};

	
	//uniform vec2 vScale; slider[(1.1,1.1),(1.5,1.5),(3.,3.)]
	//		uniform int Iterations;  slider[0,50,100]

			public vec2 CircleInversion(vec2 vPos, vec2 vOrigin, double fRadius)
			{
				vec2 vOP = vPos.minus(vOrigin);

				vOrigin = vOrigin.minus( vOP.multiply( fRadius) .multiply( fRadius / G.dot(vOP, vOP)));


			        vOrigin.x += Math.sin(vOrigin.x * 0.001) / Math.cos(vOrigin.y * 0.001);
			        vOrigin.y +=Math. sin(vOrigin.x * 0.001) * Math.cos(vOrigin.y * 0.001);

			        return vOrigin;
			}

			double Parabola( double x, double n )
			{
				return Math.pow( 3.0*x*(1.0-x), n );
			}

	
	
	public vec3 getRGBColor(double xp,double yp)
	{

		vec2 v=new vec2(xp,yp);
		vec3 col=new vec3(0.0);

	   // double t = 0.05*time;
		vec2 vOffset = new vec2( Math.sin(time * 0.123), Math.atan(time * 0.0567));

		double l = 0.0;
		double minl = 10000.0;

		for(int i=0; i<Iterations; i++)
		{
			v.x = Math.abs(v.x);
			v = v.multiply(vScale).plus(  vOffset);

			v = CircleInversion(v, new vec2(0.5, 0.5), 0.9);

			l = G.length(v.multiply(v));
			minl = G.min(l, minl);
		}


		double t = 2.1 + time * 0.025;
		vec3 vBaseColour = G.normalize(new vec3(Math.sin(t * 1.790), Math.sin(t * 1.345), Math.sin(t * 1.123)).multiply(0.5).plus( 0.5));

		//vBaseColour = vec3(1.0, 0.15, 0.05);

		double fBrightness = 11.0;

		vec3 vColour = vBaseColour.multiply( l * l * fBrightness);

		minl = Parabola(minl, 5.0);

		vColour = vColour.multiply(minl + 0.14);

		vColour = new vec3(1.0).minus(  G.exp(vColour.multiply(-1.)));
		
	return vColour;

	}
	
	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) 
	{
      vScale=new vec2(scalex,scaley);
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
		return "dc_inversion";
	}

	public String[] getParameterNames() {
	    return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed,time,Iterations,scalex,scaley},super.getParameterValues());
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
			time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_ITERS)) {
			Iterations = (int)Tools.limitValue(pValue, 0 , 100);
		}
		else if (pName.equalsIgnoreCase(PARAM_VSCALEX)) {
			scalex = Tools.limitValue(pValue, 1.1 , 3.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_VSCALEY)) {
			scaley =Tools.limitValue(pValue, 1.1 , 3.0);
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

