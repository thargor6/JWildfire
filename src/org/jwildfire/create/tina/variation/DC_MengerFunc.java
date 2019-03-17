package org.jwildfire.create.tina.variation;

import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;



public class DC_MengerFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_menger
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_DC= "ColorOnly";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_LEVEL = "Level";
	private static final String PARAM_GRADIENT = "Gradient"; 


	
	int colorOnly=0;

	private double zoom = 1.0;
	int level=3;

	int gradient=0;


	
	
	private static final String[] paramNames = { PARAM_DC,PARAM_ZOOM,PARAM_LEVEL,PARAM_GRADIENT};

	    

	  public double sdBox(vec2 p, vec2 s) {
			p = G.abs(p).minus( s);
			return G.length(G.max(p, 0.0)) + G.min(G.max(p.x, p.y), 0.0);
		}

		public double sdCross(vec2 p) {
			p = G.abs(p);
			return G.min(p.x, p.y) - 1.0;
		}

		public double sierpinskiCarpet(vec2 p) {
			double d = sdBox(p, new vec2(1.0));
			
			double s = 1.0;
			for (int i = 0; i < level; i++) {
				vec2 a = G.mod(p.multiply( s), 2.0).minus( 1.0);
				vec2 r = new vec2(1.0).minus( G.abs(a).multiply(3.0));
				s *= 3.0;
				double c = sdCross(r) / s;
				d = G.max(d, c);
			}
			return d;
		}
	  
	public vec3 getRGBColor(double xp,double yp)
	{

		vec2 p=new vec2(xp,yp).multiply(zoom);
		vec3 col=new vec3(0.0);

        double d=sierpinskiCarpet(p.multiply(1.3));
		col= d > 0.0 ? new vec3(0.0) : new vec3(1.0);
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
	
/*	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) 
	{
        vec3 color=new vec3(0.0); 
		 vec2 uV=new vec2(0.),p=new vec2(0.);

	     if(colorOnly==1)
		 {
			 uV.x=pAffineTP.x;
			 uV.y=pAffineTP.y;
		 }
		 else
		 {
	   			 uV.x=2.*pContext.random()-1.0;
				 uV.y=2.*pContext.random()-1.0;
		}
        
        color=getRGBColor(uV.x,uV.y);
       
        if(gradient==0)
        {
       	  int[] tcolor=new int[3];    	
           tcolor=dbl2int(color);  
     	
    	  pVarTP.rgbColor  =true;;
    	  pVarTP.redColor  =tcolor[0];
    	  pVarTP.greenColor=tcolor[1];
    	  pVarTP.blueColor =tcolor[2];
    		
        }
        else
        {
        	double s=(color.x+color.y+color.z);
        	double red=color.x/s;

        	pVarTP.color=Math.sin(red);

        }

        pVarTP.x+= pAmount*(uV.x);
        pVarTP.y+= pAmount*(uV.y);

	}*/
	

	public String getName() {
		return "dc_menger";
	}

	public String[] getParameterNames() {
		return paramNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { colorOnly,zoom,level, gradient};
	}


	
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_DC)) {
			colorOnly = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (PARAM_ZOOM.equalsIgnoreCase(pName)) 
		{	   zoom =   pValue;

	    }
		else if (pName.equalsIgnoreCase(PARAM_LEVEL)) {
			level = (int)Tools.limitValue(pValue, 1 , 5);
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

