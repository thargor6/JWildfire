package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.M_2PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.floor;
import static org.jwildfire.base.mathlib.MathLib.sin;

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



public class DC_HexagonsFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_hexagons
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 */



	private static final long serialVersionUID = 1L;

	public static final String PARAM_SHAPE = "shape";

	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";


	  private final static int SHAPE_SQUARE = 0;
	  private final static int SHAPE_DISC = 1;
	



	private int shape = 0;
	private int seed = 10000;
	double time=0.0;
	double zoom=4.0;

	
	double centre=0.0;
	double range=0.333;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	private static final String[] additionalParamNames = { PARAM_SHAPE,PARAM_SEED,PARAM_TIME,PARAM_ZOOM};


	public double hex(vec2 p) 
	{
	  p.x *= 0.57735*2.0;
		p.y += G.mod(G.floor(p.x), 2.0)*0.5;
		p = G.abs((G.mod(p, 1.0).minus(0.5)));
		return G.abs(G.max(p.x*1.5 + p.y, p.y*2.0) - 1.0);
	}
		
	public vec3 getRGBColor(double xp,double yp)
	{
		vec2 p = new vec2(xp, yp).multiply(zoom);
        double col=0.2;
		double s = G.sin(G.dot(p, p) / -64. + time * 4.);
		s = G.pow(G.abs(s), 0.5) * G.sign(s);
		double  r = .35 + .25 * s;
		double t = G.pow(G.abs(G.sin(time * 4.)), 0.2) * G.sign(G.sin(time * 4.));
		t *= 10.25;
		 p = p.times(new mat2(G.cos(t), -G.sin(t), G.sin(t), G.cos(t)));
		vec3 color= new  vec3(G.smoothstep(r - 0.1, r + 0.1, hex(p)));
        return color;

	}
 	
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) 
	{
		 vec2 uV=new vec2(0.),p=new vec2(0.);
	     vec3 color=new vec3(0.0); 
			int[] tcolor=new int[3];  

	     if(colorOnly==1)
		 {
			 uV.x=pAffineTP.x;
			 uV.y=pAffineTP.y;
		 }
		 else
		 {		 
	          switch (shape) {
	            case SHAPE_SQUARE:
	   			 uV.x=2.0*pContext.random()-1.0;
				 uV.y=2.0*pContext.random()-1.0;
	              break;

	            case SHAPE_DISC:
	             double r = 2.*pContext.random() + 2.*pContext.random();
	              r = (r > 2.0) ? 4.0 - r : r;

	             double theta = pContext.random() * M_2PI;
	             double s = sin(theta);
	             double c = cos(theta);
	              uV.x = 0.5 * r * s;
	              uV.y = 0.5 * r * c;
	              break;
	          }
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
		return "dc_hexagons";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { shape,seed,time,zoom},super.getParameterValues());
	}


	
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SHAPE)) {
			shape = (int)Tools.limitValue(pValue, 0 , 1);
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

