package org.jwildfire.create.tina.variation;

import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;


import js.glsl.G;
import js.glsl.mat2;
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class GLSLHyperbolicFunc  extends GLSLFunc {

	/*
	 * Variation : glsl_hyperbolictile
	 * Date: October 31, 2018
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_RESOLUTIONX = "Density Pixels";


	private static final String PARAM_GRADIENT = "Gradient"; 


	int resolutionX=1000000;
	int resolutionY=resolutionX;

	int gradient=0;


	



	private static final String[] paramNames = { PARAM_RESOLUTIONX,PARAM_GRADIENT};



	  public vec2 tocart(vec2 polar) {
		   return new vec2(polar.x * G.cos(polar.y), polar.x * G.sin(polar.y));
		}

		public vec2 topolar(vec2 cart) {
		   double r = G.sqrt(cart.x*cart.x + cart.y*cart.y);
		   double alpha = G.atan2(cart.y, cart.x);
		   return new vec2(r, alpha);
		}

		public vec2 mirror(vec2 line, vec2 point) {
		  double r1 = line.x;


		  if (r1 < 100.0) {
		    vec2 linecenter = tocart(line);
		    vec2 cart = tocart(point);
		    double dx = cart.x - linecenter.x;
		    double dy = cart.y - linecenter.y;
		    r1 = line.x*line.x - 1.0;
		    double r2 = dx*dx+dy*dy;
		    double rr = r1 / r2;
		    double dx2 = dx * rr;
		    double dy2 = dy * rr;
		    return	topolar(linecenter.plus(new vec2(dx2,dy2)));
		  } 
		  else 
		  {
		    return new vec2(point.x, -point.y + 2.0 * line.y + Math.PI);
		  }
		}

		
	public vec3 getRGBColor(int xp,int yp)
	{
		double x=(double)xp+0.5;
		double y=(double)yp+0.5;
		
		// Half the width and half the height gives the position of the center of the screen
		
		vec2 position = new vec2(2.0* x/resolutionX-1., 2.0*y/resolutionY-1.0);

		  vec2 l1 = new vec2(1000.0, 0.0);
		  vec2 l2 = new vec2(1000.0, Math.PI / 8.0);
		  vec2 l3 = new vec2(Math.sqrt(Math.sqrt(2.0) + 1.0), Math.PI / 2.0);



		  vec2 p = topolar(position);
		  double color;
		  if (p.x > 1.0) {
		     color = 0.0;
		  }
		  else 
		  {
		    int g2 = 0;
		    int k1 = 1;
		    for (int i = 0; i < 7; ++i) {
		      if (k1 == 0) {
		        continue;
		      }
		      int k = 1;
		      for (int j = 1; j < 8; ++j) {
		        if (k == 0 || (p.y >= (Math.PI / 2.0)) && (p.y < (Math.PI * 0.75)))
		        {
		          k = 0;
		        } else {
		          g2 = g2 + 1;
		          p = mirror(l2, mirror(l1, p));
		        }
		      }
		      vec2 p1 = mirror(l3, p);
		      if ((p1.y >= (Math.PI / 2.0)) && (p1.y < (Math.PI * 0.75))) {
		        k1 = 0;
		      }
		      p = p1;
		    }
		    color = G.mod((double)g2, 2.0) / 1.0;
//		    color = (double)(g2)/70.0;
		  }
		  return new vec3( color, color, color);
		}

 	
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) 
	{

        
        int i=(int) (pContext.random()*resolutionX);
        int j=(int) (pContext.random()*resolutionY);
        
        vec3 color=new vec3(0.0);   
        
        color=getRGBColor(i,j);
       
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
	    pVarTP.x+= pAmount*((double)(i)/resolutionX - 0.5 );
		pVarTP.y+= pAmount*((double)(j)/resolutionY - 0.5 );

	}
	

	public String getName() {
		return "glsl_hyperbolictile";
	}

	public String[] getParameterNames() {
		return paramNames;
	}

	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { resolutionX,gradient};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_RESOLUTIONX)) {
			resolutionX = (int)Tools.limitValue(pValue, 100 , 10000000);
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

