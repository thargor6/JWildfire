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



public class DC_VoronoiseFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_voronise
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_DC= "ColorOnly";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_DELTAX = "pX";
	private static final String PARAM_DELTAY = "pY";
	private static final String PARAM_GRADIENT = "Gradient"; 


	
	int colorOnly=0;

	double zoom = 8.0;
    double deltaX=0.5;
	double deltaY=0.5;

	int gradient=0;

	
	private static final String[] paramNames = { PARAM_DC,PARAM_ZOOM,PARAM_DELTAX,PARAM_DELTAY,PARAM_GRADIENT};

	    
	  
	public vec3 getRGBColor(double xp,double yp)
	{

		vec2 st=new vec2(xp,yp);
		vec3 col=new vec3(0.0);
	    st=st.multiply(zoom);
	    double n = G.iqnoise(st, deltaX, deltaY);

	    col = new vec3(n);
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
			uV.x=2.0*pContext.random()-1.0;
			uV.y=2.0*pContext.random()-1.0;
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
		return "dc_voronoise";
	}

	public String[] getParameterNames() {
		return paramNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { colorOnly,zoom,deltaX, deltaY, gradient};
	}


	
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_DC)) {
			colorOnly = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (PARAM_ZOOM.equalsIgnoreCase(pName)) 
		{	   zoom =  pValue;

	    }
		else if (pName.equalsIgnoreCase(PARAM_DELTAX)) {
			deltaX = Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_DELTAY)) {
			deltaY = Tools.limitValue(pValue, 0.0 , 1.0);
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

