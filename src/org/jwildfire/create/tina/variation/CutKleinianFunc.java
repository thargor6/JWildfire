package org.jwildfire.create.tina.variation;


import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class CutKleinianFunc  extends VariationFunc {

	/*
	 * Variation :cut_kleinian
	 * Date: december 15, 2020
	 * Jesus Sosa
	 * Reference & Credits: https://www.shadertoy.com/view/MtKXRh
	 */


	private static final long serialVersionUID = 1L;

	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_BOX = "boxSize";
	private static final String PARAM_TIME = "param1";
	private static final String PARAM_ITERS = "NIters";
	private static final String PARAM_DX = "Dx";
	private static final String PARAM_DY = "Dy";
	private static final String PARAM_INVERT = "invert";
	

	int mode=1;
	double time=0.0;
	double box_size=1.0;
	int NIters=150;
	double zoom=2.0;
    int invert=0;
	double Dx=0.0;
	double Dy=-0.955; 	
    
	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_ZOOM,PARAM_BOX,PARAM_TIME,PARAM_ITERS,PARAM_DX,PARAM_DY,PARAM_INVERT};


	double  backgroundColor=0.0;
	double  white=1.0;

	// double box_size_x=1.0;

	
	double wrap(double x, double a, double s){
		x -= s; 
		return (x-a*G.floor(x/a)) + s;
	}

	vec2 TransA( vec2 z, double a, double b){
		double iR = 1. / G.dot(z,z);
		z = z.multiply(-iR);
		z.x = -b - z.x; 
		z.y = a + z.y; 
		return z;
	}
	
	
	int  JosKleinian(vec2 z)
	{
	  vec2 lz =z.plus(new vec2(1.));
	  vec2 llz=z.plus( new vec2(-1.));
	  
	 int flag=0;
	  
	 double KleinR = 1.8462756+(1.958591-1.8462756)*0.5+0.5*(1.958591-1.8462756)*time;  
	 double KleinI = 0.09627581+(0.0112786-0.09627581)*0.5+0.5*(0.0112786-0.09627581)*time;
	      
		double a = KleinR;
	    double b = KleinI;
		double f = G.sign(b);    
		
		for (int i = 0; i < NIters ; i++) 
		{
	        z.x=z.x+f*b/a*z.y;
			z.x = wrap(z.x, 2. * box_size, - box_size);
			z.x=z.x-f*b/a*z.y;
	                       
			//If above the separation line, rotate by 180° about (-b/2, a/2)
	        if  (z.y >= a * 0.5 + f *(2.*a-1.95)/4. * G.sign(z.x + b * 0.5)* (1. - G.exp(-(7.2-(1.95-a)*15.)* G.abs(z.x + b * 0.5))))	
	        {
	        	z= new vec2(-b, a).minus(z);
	        }
	        
			//Apply transformation a
			z=TransA(z, a, b);
			
	        //
			//If the iterated points enters a 2-cycle , bail out.
	        if(G.dot(z.minus(llz),z.minus(llz)) < 1.e-6) 
	             break;
	        //if the iterated point gets outside z.y=0 and z.y=a
	        if(z.y<0. || z.y>a)
	        {
	        	flag=1;
	        	break;
	        }
	        //Store prévious iterates
			llz=lz; lz=z;
		}
		return flag;
	}
	
	
	
	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    double x,y;
		    
		    if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;

		    }else
		    {
		     x=pContext.random()-0.5;
		     y=pContext.random()-0.5;
	     
		    }
		    
		    
		    vec2 uv =new vec2((x)*zoom,(y)*zoom).minus(new vec2(Dx,Dy));
		    
		    int hit=JosKleinian(uv);
		                  	
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (hit==0)
		      { x=0;
		        y=0;
		        pVarTP.doHide = true;	        
		      }
		    } else
		    {
			      if (hit==1)
			      { x=0;
			        y=0;
			        pVarTP.doHide = true;
			      }
		    }
            pVarTP.x = pAmount * (x);
		    pVarTP.y = pAmount * (y);

		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	   }

	  
	public String getName() {
		return "cut_kleinian";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  mode,zoom,box_size,time,NIters,Dx,Dy,invert});
	}


	
	public void setParameter(String pName, double pValue) {

		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_BOX)) {
			box_size = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_ITERS)) {
			NIters =(int)Tools.limitValue(pValue, 0 , 150);
		}
		else if (pName.equalsIgnoreCase(PARAM_DX)) {
			Dx = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_DY)) {
			Dy = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			invert =(int)Tools.limitValue(pValue, 0 , 1);
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

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION};
	}

}

