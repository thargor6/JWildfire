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



public class DC_RotationsFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_rotations
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_DC= "ColorOnly";

	private static final String PARAM_LEVELS = "levels";

	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";

	private static final String PARAM_GRADIENT = "Gradient"; 


	
	int colorOnly=0;


	int levels=20;

	private int seed = 10000;
	double time=0.0;
	double zoom=4.0;
	int gradient=0;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	private static final String[] paramNames = { PARAM_DC,PARAM_LEVELS ,PARAM_SEED,PARAM_TIME,PARAM_ZOOM,PARAM_GRADIENT};

	public vec3 hsv2rgb_smooth( vec3 c )
	{
		vec3 t1=new vec3(0.0,4.0,2.0).plus(c.x*6.0);
		vec3 t2=G.mod(t1,6.0).minus(3.0);
	    vec3 rgb = G.clamp( G.abs(t2).minus(1.0), 0.0, 1.0 );
	    vec3 t0= new vec3 (3.0).minus( rgb.multiply(2.0));
		rgb = rgb.multiply(rgb).multiply(t0); // cubic smoothing	

		return  G.mix( new vec3(1.0), rgb, c.y).multiply(c.z);
	}
	
	public mat2 rotate(double a)
	{
		double c = G.cos(a);
		double s = G.sin(a);
		return new mat2(c, s, -s, c);
	}
	
	public mat2 rot(double a) {
		double c = G.cos(a);
		double s = G.sin(a);
		return new mat2(c, -s, s, c);
	}
	
	public vec3 getRGBColor(double xt,double yt)
	{


		vec2 uv=new vec2(xt,yt);

		uv=uv.multiply(zoom);
		
		double _d =  1.0-G.length(uv);
		double s = 0.5;
		for (int k = 0; k < levels; k++) {
			uv = (G.abs(uv).division( G.dot(uv, uv))).minus(s);
			uv = uv.times(rot(time * .1));
			s *= .955;
		}
		
		 uv = uv.times(rotate(time+uv.x*0.05));

		vec3 ch_color = hsv2rgb_smooth(new vec3(time*0.4+uv.y*.1,1.0,1.0));

		vec3 bg_color = new vec3(_d*0.1, _d*0.2, _d*0.1);
		uv.x += 0.5+G.sin(time+uv.y*0.7)*0.15;

		vec3 color = G.mix(ch_color, bg_color, 0.0);  // shading
		return color;
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
		return "dc_rotations";
	}

	public String[] getParameterNames() {
		return paramNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { colorOnly,levels,seed,time,zoom, gradient};
	}


	
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_DC)) {
			colorOnly = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_LEVELS)) {
			
			levels = (int)Tools.limitValue(pValue, 1 , 50);
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

