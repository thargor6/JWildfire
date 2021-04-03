package org.jwildfire.create.tina.variation;

import java.util.Random;


import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.glslFuncRunner;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;
import js.glsl.mat2;
import js.glsl.mat3;
import js.glsl.mat4;


public class DC_GenericMandelbrootFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_gmandelbroot
	 * 
	 * Autor: Jesus Sosa
	 * Date: march 24, 2021
	 * Reference : http://myzeta.125mb.com/Mandelbrot.html
	 */
	

	private static final long serialVersionUID = 1L;



	private static final String PARAM_RANDOMIZE = "randomize"; 
	private static final String PARAM_COLORMODE = "color mode"; 
	private static final String PARAM_ITERS = "Iters"; 
	private static final String PARAM_EXPRE = "exp_re";
	private static final String PARAM_EXPIM = "exp_im";
	private static final String PARAM_N = "n";
	private static final String PARAM_M = "m";
	private static final String PARAM_BSHIP = "burnigShip";
	private static final String PARAM_CONJ = "Conjugate";
	private static final String PARAM_JULIA = "Julia";
	private static final String PARAM_JRE = "Jre";
	private static final String PARAM_JIM = "Jim";
	private static final String PARAM_PANX = "PanX";
	private static final String PARAM_PANY = "PanY";
	private static final String PARAM_ZOOM = "zoom"; 




	private static final String[] additionalParamNames = { PARAM_RANDOMIZE,PARAM_COLORMODE,PARAM_ITERS,PARAM_EXPRE,PARAM_EXPIM,PARAM_N,PARAM_M,PARAM_BSHIP,PARAM_CONJ,PARAM_JULIA,PARAM_JRE,PARAM_JIM,PARAM_PANX,PARAM_PANY,PARAM_ZOOM};

	
    private static final String RESSOURCE_CODE = "code";
	private static final String[] ressourceNames = { RESSOURCE_CODE };

    int seed=10000;
    int colmode=0;
    int ITERS=128;
    double zoom=4.0;

    double stepX=0.5;
    double stepY=0.0;
    
    double re_d=2.0;  
    double im_k=0.0;
    
    int burningShip=0;
    int conjugate=0;
    int julia=0;
    
    double jre=0.0;
    double jim=0.0;
    
    double n=0.0;
    double m=0.0;
    
    
    // Function to calculate the
    // log base 2 of an integer
    public static int log2(int N)
    {
  
        // calculate log2 N indirectly
        // using log() method
        int result = (int)(Math.log(N) / Math.log(2));
  
        return result;
    }
    
    public static Double log(double num, int base) {
        return (Math.log10(num) / Math.log10(base));
     }
    
    public static Double log2(double num) {
        return (Math.log10(num) / Math.log10(2.0));
     }
    
	public vec3 hsv(double h,double s,double v) 
	{
		vec3 t1=new vec3(3.,2.,1.);
		t1=t1.add(h).division(3.0);
		vec3 t2=G.fract(t1).multiply(6.0).minus(3.0);
		t2=G.abs(t2).minus(1.0);
		t2=G.clamp(t2,0.,1.);
		return G.mix(new vec3(3.1),t2,s).multiply(v);
	}
	
	public   vec3 gen_mandelbroot(vec2 p, vec2 exp)
	{    
		
		double d=exp.x;
		double k=exp.y;
		double ii=0;
		double r=0.0;
		double f=1e20;
		
        vec2 z=new vec2(p.x,p.y);
		
		for (int i = 0; i < ITERS; ++i) {  

			if(burningShip==1)
				z=new vec2(Math.abs(z.x),Math.abs(z.y));
			if(conjugate==1)
				z.y=-z.y;
            double q1=Math.atan2(z.y,z.x);
             r= z.x*z.x + z.y*z.y;
             
            double q2=q1*d + (k/2.0)*Math.log(r+m) + 2*Math.PI*n;
            double q3=Math.pow(r, d/2.0)/Math.exp(k*q1);
            			
			z = new vec2(q3*Math.cos(q2),q3*Math.sin(q2)).plus(p); 


			
			if (m==0 && julia==0) {
				if (i==0) {
				r=0;
				z.x = z.x;
				z.y = z.y; ;
				}
			}
			
			if(julia==1)
			{
				z=new vec2(q3*Math.cos(q2),q3*Math.sin(q2)).plus(new vec2(jre,jim));
			}
			
			if (r > ITERS) {
				ii=i;
				break;
			}
		}

		double q=0;
		double rrr=0.0;
		double ggg=0.0;
		double bbb=0.0;
		vec3 color=new vec3(0.0);
		
		if (d==0) 
			q=ii*Math.pow(r,0.5)*Math.pow(ii,0.75)/255.;
		else 
			q=ii*Math.pow(r,Math.abs(d)/d*0.5)*Math.pow(ii,0.75)/255.;
		
		if(colmode==0)
		{
		   double s = .125662 * (double) ii;
		   color = new vec3(Math.cos(s + .9), Math.cos(s + .3), Math.cos(s + .2));
		   color= new vec3( color.multiply(0.4).add( .6));
		
		}
		else if(colmode==1) {
			double ss  = Math.round((Math.pow(1.12,ii)-1)/(1.12-1));

			 rrr = Math.round(ss/256/256);
			 ggg = Math.round((ss-rrr*256*256)/256);
			 bbb = Math.round((ss-rrr*256*256-ggg*256));

			double hue = Math.floor(255*ii/ITERS);
			double saturation = 255;
			double value = 0;
			if (ii <= ITERS) value = 255;
			color=hsv(hue/255,saturation/255,value/255); 
			
		}
		else if(colmode==2) {
			 
			color= new vec3((rrr*q+z.x*z.x*ii/255)/255 , (ggg*q-z.y*z.y*ii/255)/255 , (bbb*q-ii*ii*ii/255)/255 );
		}
		else if(colmode==3) {
			color= new vec3((rrr*q+Math.pow(z.x*z.x*ii,0.5)/255)/255  , (ggg*q+Math.pow(z.y*z.y*ii,0.5)/255)/255 , (bbb*q+Math.pow(ii*ii*ii,3)/255));
		}
		else if(colmode==4) {
			color=new  vec3(ii*Math.pow(r*ii/ITERS,0.45)/255 , ii*Math.pow(r*ii/ITERS,0.35)/255 , ii*Math.pow(r*ii/ITERS,0.25)/255);
		}
		else if(colmode==5)
		{
			f=Math.min(f, G.dot(z, z));
			f=1.0+log2(f)/16.0;
		    color=new vec3(f,f*f,f*f*f);
		}

		return color;
	}
	
    public vec3 getRGBColor(double xp,double yp)
    {
      vec3 color=new vec3(0.0);
      vec2 st=new vec2(xp,yp);
      vec2 c = st.multiply(zoom).minus(new vec2(stepX,stepY));
      color = gen_mandelbroot(c,new vec2(re_d,im_k));        
      return color;
    }
	

	 

	@Override
	public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		super.initOnce(pContext, pLayer, pXForm, pAmount);

	}	 

	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) 
	{


        	   
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
		
    	 pVarTP.doHide =false;
	     if(color.x==0.0 && color.y==0.0 && color.z==0.0)
	    	 pVarTP.doHide =true;
	    	 
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
		return "dc_gmandelbroot";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed,colmode,ITERS,re_d,im_k,n,m,burningShip,conjugate,julia,jre,jim,stepX,stepY,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_RANDOMIZE)) {
			seed = (int)Tools.limitValue(pValue, 0 , 10000000);
			if(seed>0)
			{

			}
		}
		else if (pName.equalsIgnoreCase(PARAM_COLORMODE)) {

			colmode=(int)Tools.limitValue(pValue, 0 , 5);
		} 
		else if (pName.equalsIgnoreCase(PARAM_ITERS)) {

			ITERS=(int)Tools.limitValue(pValue, 1 , 255);
		} 
		else if (pName.equalsIgnoreCase(PARAM_EXPRE)) {

			re_d=pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_EXPIM)) {
			im_k=pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_N)) {

			n=pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_M)) {
			m=pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_BSHIP)) {

			burningShip=(int)Tools.limitValue(pValue, 0 , 1);
		} 
		else if (pName.equalsIgnoreCase(PARAM_CONJ)) {

			conjugate=(int)Tools.limitValue(pValue, 0 , 1);
		} 
		else if (pName.equalsIgnoreCase(PARAM_JULIA)) {
			julia=(int)Tools.limitValue(pValue, 0 , 1);
		} 
		else if (pName.equalsIgnoreCase(PARAM_JRE)) {

			jre=pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_JIM)) {
			jim=pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_PANX)) {

			stepX=Tools.limitValue(pValue, -2 , 2);
		} 
		else if (pName.equalsIgnoreCase(PARAM_PANY)) {

			stepY=Tools.limitValue(pValue, -2 , 2);
		} 
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom= Tools.limitValue(pValue, 0 , 100);
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

