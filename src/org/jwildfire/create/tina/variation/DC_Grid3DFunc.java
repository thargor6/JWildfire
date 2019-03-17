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



public class DC_Grid3DFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_grid3D
	 * Date: February 13, 2019
	 * Author:Jesus Sosa
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_DC = "ColorOnly";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_SEED = "Seed";
	private static final String PARAM_TIME = "time";



	int colorOnly=0;
	double zoom=1.;
	private int seed = 10000;
	double time=200.;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	



	private static final String[] paramNames = { PARAM_DC,PARAM_ZOOM,PARAM_SEED,PARAM_TIME};




		public vec3 field(vec3 p) {
			p = p.multiply(0.1);
			double f = .1;
			for (int i = 0; i < 3; i++) 
			{
				p = new vec3(p.y,p.z,p.x); //*mat3(.8,.6,0,-.6,.8,0,0,0,1);
//				p += vec3(.123,.456,.789)*float(i);
				p = G.abs(G.fract(p).minus(0.5));
				p = p.multiply(2.0);
				f *= 2.0;
			}
			p = p.multiply(p);
			return G.sqrt(p.add(new vec3(p.y,p.z,p.x))).division(f).minus(.05);
		}
		
	public vec3 getRGBColor(double xp,double yp)
	{

		
		// a raymarching experiment by kabuto
		//fork by tigrou ind (2013.01.22)
		// slow mod by kapsy1312.tumblr.com


	        int MAXITER = 30;

	        vec2 p=new vec2(xp,yp).multiply(zoom);
			vec3 dir = G.normalize(new vec3(p.x,p.y,1.));
			double a = time * 0.021;
			vec3 pos =new vec3(0.0,time*0.1,0.0);
			
			dir = dir.times(new mat3(1,0,0,0,G.cos(a),-G.sin(a),0,G.sin(a),G.cos(a)));
			dir = dir.times(new mat3(G.cos(a),0,-G.sin(a),0,1,0,G.sin(a),0,G.cos(a)));
			vec3 color = new vec3(0);
			
			for (int i = 0; i < MAXITER; i++) {
				vec3 f2 = field(pos);
				double f = G.min(G.min(f2.x,f2.y),f2.z);
				
				pos = pos.add(dir.multiply(f));
				vec3 t0=new vec3( ((double) MAXITER-i)).division(f2.add(0.1));
				color = color.add(t0);
			}
			vec3 color3=new vec3(1.0).minus(new vec3(1.0).division((new vec3(1.0).add(color.multiply(.09/(double)(MAXITER*MAXITER))))));

			// color3 = color3.multiply(color3);
			return new vec3( color3.r+color3.g+color3.b);
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
			uV.x=2.*pContext.random()-1.0;
			uV.y=2.*pContext.random()-1.0;
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
		return "dc_grid3D";
	}

	public String[] getParameterNames() {
		return paramNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { colorOnly,zoom,seed, time};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_DC)) {
			colorOnly = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.01 , 1000.0);
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
			time = Tools.limitValue(pValue, 1.0 , 1000.0);
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

