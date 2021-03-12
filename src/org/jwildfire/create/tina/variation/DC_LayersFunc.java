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



public class DC_LayersFunc  extends DC_BaseFunc {


	/*
	 * Variation : dc_layers
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_N = "N";
	private static final String PARAM_AMPLITUDE = "Amplitude";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_RED = "red";
	private static final String PARAM_GREEN = "green";
	private static final String PARAM_BLUE = "blue";



	int N=8;
	double Amplitud=0.995;
	private int seed = 10000;
	double time=0.0;
	double zoom=8.0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	private static final String[] additionalParamNames = { PARAM_N,PARAM_AMPLITUDE ,PARAM_SEED,PARAM_TIME,PARAM_ZOOM};

	
	public double snoise(vec3 v) {
		return (G.sin(v.x*4.)*G.cos(v.y*4.)-G.sin(v.z*4.));
	}

	public vec3 rotate(vec3 v,vec2 r) 
	{
		mat3 rxmat = new mat3(1,   0    ,    0    ,
				  0,G.cos(r.y),-G.sin(r.y),
				  0,G.sin(r.y), G.cos(r.y));
		mat3 rymat = new mat3(G.cos(r.x), 0,-G.sin(r.x),
				     0    , 1,    0    ,
				  G.sin(r.x), 0,G.cos(r.x));
		
		
		return v.times(rxmat).times(rymat);
		
	}
	
	public vec3 getRGBColor(double xp,double yp)
	{


		vec2 p=new vec2(xp,yp).multiply(zoom);


		vec2 m = new vec2(2.,1.).multiply(Amplitud* Math.PI);

		
		vec3 color = new vec3(0.0);
		
		vec3 pos = G.normalize(rotate(new vec3(p,1.0),new vec2(m)));
		
		double dist = 0.0;
		
		for(int k = 1; k <= N ;k++)
		{
			double shell = G.abs(snoise(pos.multiply((double)k).add( new vec3(time,0,0).multiply(0.13))));
			
			shell = G.smoothstep(0.25,0.2,shell);
			
			dist = G.max(dist,shell*(1.-((double)k/8.)));
		}
		
		color = G.mix(new vec3(1., 1.0, 1.0),new vec3(.0,0.,0.),1.-dist);

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
		return "dc_layers";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { N,Amplitud,seed,time,zoom},super.getParameterValues());
	}


	
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_N)) {
			
			N = (int)Tools.limitValue(pValue, 1 , 10);
		}
		else if (pName.equalsIgnoreCase(PARAM_AMPLITUDE)) {
			
			Amplitud = Tools.limitValue(pValue, 0.0 , 10.0);
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

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE};
	}

}

