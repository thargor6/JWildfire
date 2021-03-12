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



public class DC_DucksFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_ducks
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 */



	private static final long serialVersionUID = 1L;

	
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";



	

	private int seed = 10000;
	double time=10.0;
	double zoom=1.0;



	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_ZOOM};


	  public vec2 B(vec2 a)
	  {
	    return new vec2(Math.log(G.length(a)),G.atan2(a.y,a.x)-6.2);
	  }

		public vec3 F(vec2 E,double _time)
		{
			vec2 e_=E;
			double c=0.;
			int i_max=50;
			for(int i=0; i<i_max; i++)
			{
				vec2 tmp= B( new vec2(e_.x,Math.abs(e_.y)));
				e_=tmp.plus( new vec2(.1*Math.sin(_time/3.)-.1,5.+.1*Math.cos(_time/5.)));
				c += G.length(e_);
			}
			double d = G.log2(G.log2(c*.05))*6.;
			vec3 flori =new vec3(.1+.7*Math.sin(d),.1+.5*Math.sin(d-.7),.7+.7*Math.cos(d-.7));
			vec3 t0= G.floor(new vec3(0.5).add(flori.multiply(1.1)));
			return (t0.division(1.1));
		}
		
	public vec3 getRGBColor(double xp,double yp)
	{

		double t=time/65.*30.;;
		//vec2 delta=new vec2(0.5,4.0);
		vec2 delta=new vec2(0.);
		double xt1=(xp-delta.x);
		double yt1=(yp-delta.y);
		
		//vec2 p=new vec2(xt1*(9.1-9.*G.cos(t/9.)),yt1*(9.1-9.*G.cos(t/9.)));
		vec2 p=new vec2(xt1,yt1);
		p=p.multiply(zoom);
		vec3 col=new vec3(0.0);

   	   vec3 t1=new vec3(F(p,t));
   	   col=new vec3(t1.z,t1.x,t1.y);
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
		return "dc_ducks";
	}

	public String[] getParameterNames() {
	    return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed,time,zoom},super.getParameterValues());
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

