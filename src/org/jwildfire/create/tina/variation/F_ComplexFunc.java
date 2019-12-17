package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.fmod;

import java.io.StringReader;
import java.util.Random;

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.Scanner;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
//import org.jwildfire.create.tina.variation.DucksFunc.ComplexFuncRunner;

//import js.colordomain.Complex;
import org.jwildfire.base.mathlib.Complex;
import js.glsl.G;
//import js.glsl.glslFuncRunner;
//import js.glsl.glslFuncRunner;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;
import js.glsl.mat2;
import js.glsl.mat3;
import js.glsl.mat4;


public class F_ComplexFunc  extends DC_BaseFunc {

	/*
	 * Variation : f_complex
	 * 
	 * Autor: Jesus Sosa
	 * Date: September 1, 2019
	 * Reference  http://www.algorithmic-worlds.net/blog/blog.php?Post=20110227
	 * Complex function - Starting Code "Ducks & Buterflies" by Samuel Monnier
	 */

	private static final long serialVersionUID = 1L;

	  public static class ComplexFuncRunner 
	  {

		    public static ComplexFuncRunner compile(String pScript) throws Exception {
		      ComplexFuncRunner res = (ComplexFuncRunner) ClassBodyEvaluator.createFastClassBodyEvaluator(new Scanner(null, new StringReader(pScript)), ComplexFuncRunner.class, (ClassLoader) null);
		      return res;
		    }
		    
			public Complex abs(Complex z)
			{
				return new Complex(Math.abs(z.re),Math.abs(z.im));
			}
			
			public Complex iabs(Complex z)
			{
				return new Complex(z.re,Math.abs(z.im));
			}
			
			public Complex tan(Complex a)
			{
				Complex t1=new Complex(); 
				t1.Copy(a);
				Complex t2=new Complex();
				t2.Copy(a);
				t1.Sin();
				t2.Cos();
				t1.Div(t2);
				return t1;
			}
			
		    public Complex f(Complex z, Complex c) 
			{
		    	Complex a=iabs(z);
		    	a.Log();
		    	a.Add(c);
			   return a; 
			 };
			 
			 public int itfractal(Complex z0, Complex c, int maxIters,int nIters, double escape)
			 {
				Complex z= z0;
				double mean=0.0,d=0.0;
				int k=0;
	            for( k=0;k< maxIters;k++)
	             {
	             	//z= f( z,c);
	                z = f(z, c);
	             	if(k> nIters)
	             	{
	             		//mean+=z.mod(); 
	             		mean+=Math.sqrt(z.Mag2());
	             		//d += Complex.norm(z);
	             		d += z.Mag2();
	             		//if(Complex.norm(z)> escape && k>nIters)
	             		if(z.Mag2()> escape && k>nIters)
	             			break;
	             	}
	             }
	             return k;
			 }
			 
			 public double dfractal(Complex z0, Complex c, int maxIters,int nIters, double escape)
			 {
				Complex z= z0;
				double mean=0.0,d=0.0;
				int it=0;
	            for( int k=0;k< maxIters;k++)
	             {
	             	//z= f( z,c);
	                z = f(z, c);
	             	if(k> nIters)
	             	{
	             		//mean+=z.mod(); 
	             		mean+=Math.sqrt(z.Mag2());
	             		//d += Complex.norm(z);
	             		d += z.Mag2();
	             		//if(Complex.norm(z)> escape && k>nIters)
	             		if(z.Mag2()> escape && k>nIters)
	             		{
	             	        it=k;
	             			break;
	             	
	             		}
	             	}
	             }
	             return d;
			 }
			 
			  public int[] dbl2int(vec3 theColor) {
				    int[] color = new int[3];

				    int col = (int) (256D * theColor.x);
				    if (col > 255)
				      col = 255;
				    color[0] = col;
				    col = (int) (256D * theColor.y);
				    if (col > 255)
				      col = 255;
				    color[1] = col;
				    col = (int) (256D * theColor.z);
				    if (col > 255)
				      col = 255;
				    color[2] = col;
				    return color;
				  }
			  
				public vec3 SetHSV(double h, double s, double v) {
				    double r = 0.0D;
				    double g = 0.0D;
				    double b = 0.0D;
				    vec3 color;
				    if (s == 0.0D) {
				      r = g = b = v;
				    } else {
				      if (h == 1.0D)
				        h = 0.0D;
				      double z = Math.floor(h * 6D);
				      int i = (int) z;
				      double f = h * 6D - z;
				      double p = v * (1.0D - s);
				      double q = v * (1.0D - s * f);
				      double t = v * (1.0D - s * (1.0D - f));
				      switch (i) {
				        case 0: // '\0'
				          r = v;
				          g = t;
				          b = p;
				          break;

				        case 1: // '\001'
				          r = q;
				          g = v;
				          b = p;
				          break;

				        case 2: // '\002'
				          r = p;
				          g = v;
				          b = t;
				          break;

				        case 3: // '\003'
				          r = p;
				          g = q;
				          b = v;
				          break;

				        case 4: // '\004'
				          r = t;
				          g = p;
				          b = v;
				          break;

				        case 5: // '\005'
				          r = v;
				          g = p;
				          b = q;
				          break;
				      }
				    }
				    color=new vec3(r,g,b);
				    return color;
				  }
			  
			  public vec3 palette( double t, vec3 a, vec3 b, vec3 c, vec3 d )
			  {
			      return a.plus(b.multiply(G.cos( (c.multiply(t).plus(d)).multiply(6.28318) )));
			  }
			  
			  public int[] rgbColor0(Complex z)
			  {
	             double a;
	     	     vec3 theColor;
	             for (a = z.Arg(); a < 0.0D; a += 6.2831853071795862D) ;
	             a /= 6.2831853071795862D;
	             double m = Math.sqrt(z.Mag2());   // m=z.mod()
	             double ranges = 0.0D;
	             double rangee;
	             for (rangee = 1.0D; m > rangee; rangee *= 2.7182818284590451D)
	               ranges = rangee;
	             double k0 = (m - ranges) / (rangee - ranges);
	             double sat = k0 >= 0.5D ? 1.0D - (k0 - 0.5D) * 2D : k0 * 2D;
	             sat = 1.0D - Math.pow(1.0D - sat, 3D);
	             sat = 0.40000000000000002D + sat * 0.59999999999999998D;
	             double val = k0 >= 0.5D ? 1.0D - (k0 - 0.5D) * 2D : k0 * 2D;
	             val = 1.0D - val;
	             val = 1.0D - Math.pow(1.0D - val, 3D);
	             val = 0.59999999999999998D + val * 0.40000000000000002D;
	             theColor = SetHSV(a, sat, val);
	             int[] color = new int[3];
	             color = dbl2int(theColor);
	             return color;
			}
			  
			  
			public int[] rgbColor(Complex z, double contrast)
			{
         	  double t=z.Mag2();
         	  vec3 color= new vec3(t/64., t/32., t/24.).multiply(t*contrast);
              int[] tcolor = new int[3];
		      tcolor=dbl2int(color);
		      return tcolor;
			}
			
			public int[] rgbColor2(Complex z, int it, int maxIters, double contrast)
			{  double s = (it - G.log2(G.log2(G.dot(new vec2(z.re,z.im),new vec2(z.re,z.im)))) + 4.0)/maxIters; 

/*	   			vec3 tmp = new vec3(Math.cos(s + .9), Math.cos(s + .3), Math.cos(s + .2));
	   			vec3 color= new vec3( tmp.multiply(0.4).add( .6));*/
	   	  	   vec3 color= new vec3(1.0- s/64., 1.0- s/32., 1.0 - s/24.).multiply(s*contrast);
	           int[]  tcolor = new int[3];
			   tcolor=dbl2int(color);
	   	  	   return tcolor;
	   	  	 }
			
			public double rgbColor4(Complex z,int it,int maxIters)
			{
				// http://www.iquilezles.org/www/articles/mset_smooth/mset_smooth.htm
				// return color index with a smooth iteration count 
	           	 double ci = it - G.log2(G.log2(G.dot(new vec2(z.re,z.im),new vec2(z.re,z.im)))) + 4.0; 
	             return ci/maxIters;
			}
			
			public int[] rgbColorG(Complex z,int it, int maxIters)
			{
	           	 double ci = it - G.log2(G.log2(G.dot(new vec2(z.re,z.im),new vec2(z.re,z.im)))) + 4.0; 
				  // vec3(0.5,0.5,0.5),vec3(0.5,0.5,0.5),vec3(1.0,1.0,1.0),vec3(1.,1.,1.) );
				  vec3  color = palette(  ci/maxIters, new vec3(0.5,0.5,0.5),new vec3(0.5,0.5,0.5),new vec3(1.0,1.0,1.0),new vec3(1.0,1.,1.) );
		           int[]  tcolor = new int[3];
				   tcolor=dbl2int(color);
		   	  	   return tcolor;
			}
			
      }

	

	private static final String PARAM_RANDOMIZE = "randomize"; 
	private static final String PARAM_CONTRAST = "contrast"; 
	private static final String PARAM_ZOOM = "zoom"; 
	private static final String PARAM_STEPX = "Pan_Re"; 
	private static final String PARAM_STEPY = "Pan_Im"; 
	private static final String PARAM_ESCAPE = "escape";
	private static final String PARAM_MAXITERS = "Iters";
	private static final String PARAM_NITERS = "preIters"; 
	private static final String PARAM_JULIA = "Julia mode"; 
	private static final String PARAM_CRE = "c-real"; 
	private static final String PARAM_CIM = "c-imag"; 
	private static final String PARAM_COLORTYPE = "ColorType"; 
	private static final String PARAM_DC = "ColorOnly"; 




	protected static final String[] additionalParamNames = { PARAM_RANDOMIZE,PARAM_CONTRAST,PARAM_ZOOM,PARAM_STEPX,PARAM_STEPY,PARAM_ESCAPE,PARAM_MAXITERS,PARAM_NITERS,PARAM_JULIA,PARAM_CRE,PARAM_CIM,PARAM_COLORTYPE,PARAM_DC,PARAM_SCALE_Z,PARAM_OFFSET_Z,PARAM_RESET_Z};

	  private static final String RESSOURCE_CODE = "code";
	  private static final String[] ressourceNames = {RESSOURCE_CODE};	

	double zoom=2.5;
	double contrast=0.5;
	double stepx=0.0;
	double stepy=0.0;
	
	

    //int seed=10000;
    double escape=4.0;

    int maxIters=50;
    int nIters=1;
    
    int julia =1;
	double c_re=0.32;
	double c_im=-0.906;
    int colortype=2;
	 
	int seed=10000;
	Random rnd=new Random(seed);
	
	int colorOnly=0;
    double scale_z=0.0;
    double offset_z=0.0;
    int reset_z=1;
	

    String code_func =  "import org.jwildfire.base.mathlib.Complex;\r\n" +
		                "import js.glsl.vec3;\r\n" +
                        "public int[] rgbColor(Complex z, double contrast)\r\n" +
    					"{\r\n" +
    					" double t=z.Mag2();\r\n" +
    					"  vec3 color= new vec3(t/64., t/32., t/24.).multiply(t*contrast);\r\n" +
    					"  int[] tcolor = new int[3];\r\n" +
    					"  tcolor=dbl2int(color);\r\n" +
    					"  return tcolor;\r\n" +
    					"}\r\n" +
    					"\r\n" +
    		           "public Complex f(Complex z, Complex c)\r\n" +
                       "{\r\n" +
                       "  Complex a = iabs(z);\r\n" +
                       "  a.Log();\r\n"  +
                       "  a.Add(c);\r\n" +
                       "  return a;\r\n" +
                       "}";

    ComplexFuncRunner cf_runner = null;

    
    public void compile() {
        String code=code_func;	
        try {
          cf_runner = ComplexFuncRunner.compile(code);
        } catch (Throwable ex) {
          System.out.println("##############################################################");
          System.out.println(ex.getMessage());
          System.out.println("##############################################################");
          System.out.println(code);
          System.out.println("##############################################################");
        }
      }

     
      public void validate() {
    	  try {
    		  if (code_func != null) {

    			ComplexFuncRunner.compile( code_func);
    		  }
    	  }
    	  catch (Throwable ex) {
    		  throw new RuntimeException(ex);
    	  }
      }
      
	@Override
	public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		super.initOnce(pContext, pLayer, pXForm, pAmount);

	}	 

	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) 
	{

	    if (cf_runner == null) {
	        compile();
	      }
        	   
	}
	   
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) 
	{

	    vec2 uV=new vec2(0.),p=new vec2(0.);
	    double theColor[] = new double[3];
        double ci=0.0;
	 
        if(colorOnly==0)
        {
		uV.x=2.0*pContext.random()-1.0;
		uV.y=2.0*pContext.random()-1.0;
        }
        else
        {
        	uV.x=pAffineTP.x;
        	uV.y=pAffineTP.y;
        }
        
         Complex c=new Complex(uV.x*zoom,uV.y*zoom);
         c.Sub(new Complex(stepx,stepy));
         Complex z = c;  
        
         if(julia==1)
         {
        	 c=new Complex(c_re,c_im);
         }
        
     	double mean=0;
     	int it=0;
     	int k=0;
        double d=0.0;
		int[] tcolor=new int[3];
		
         if(colortype==0)
         { 
             for( k=0;k< maxIters;k++)
             {
                z = cf_runner.f(z, c);
             }
             tcolor=cf_runner.rgbColor0(z);
             pVarTP.rgbColor  = true;
             pVarTP.redColor  = tcolor[0];
             pVarTP.greenColor= tcolor[1];
             pVarTP.blueColor = tcolor[2];
         }

         if(colortype==1)
         {
        	
            d=cf_runner.dfractal(z,c,maxIters,nIters,escape);

			double s = .125662 * (double) d;
			vec3 tmp = new vec3(Math.cos(s + .2), Math.cos(s + .3), Math.cos(s + .6));
			vec3 color= new vec3( tmp.multiply(0.4).add( .6));

		     tcolor=dbl2int(color);
		     double gr=greyscale(tcolor[0],tcolor[1],tcolor[2]);
             pVarTP.rgbColor = false;
             pVarTP.color = gr;
         }   
         if(colortype==2)
         {
             for( k=0;k< maxIters;k++)
             {
             	z= cf_runner.f(z, c);
             	if(k> nIters)
             	{
             		d += z.Mag2();
             		if(z.Mag2()> escape && k>nIters)
             		{
             	        it=k;
             			break;
             	
             		}
             	}
             }
             tcolor=cf_runner.rgbColorG(z,it,maxIters);
             pVarTP.rgbColor = true;
             pVarTP.redColor = tcolor[0];
             pVarTP.greenColor = tcolor[1];
             pVarTP.blueColor = tcolor[2];

         }
         if(colortype==3)
         {
             for( k=0;k< maxIters;k++)
             {
             	z= cf_runner.f(z, c);
             	if(k> nIters)
             	{
             		d += z.Mag2();
             		if(z.Mag2()> escape && k>nIters)
             		{
             	        it=k;
             			break;
             		}
             	}
             }
 			double s = .125662 * (double) d;

 			vec3 tmp = new vec3(Math.cos(s + .9), Math.cos(s + .3), Math.cos(s + .2));
 			vec3 color= new vec3( tmp.multiply(0.4).add( .6));
            tcolor = new int[3];
 		    tcolor=dbl2int(color);
 		    
	        Layer layer=pXForm.getOwner();
	        RGBPalette palette=layer.getPalette();      	  
	       	RGBColor colgrad=findKey(palette,tcolor[0],tcolor[1],tcolor[2]);
	       	    
	       	pVarTP.rgbColor  =true;;
	       	pVarTP.redColor  =colgrad.getRed();
	       	pVarTP.greenColor=colgrad.getGreen();
	       	pVarTP.blueColor =colgrad.getBlue();
         }
         if(colortype==4)
         {
                 for( k=0;k< maxIters;k++)
                 {
                 	z= cf_runner.f(z, c);
                 	if(k> nIters)
                 	{
                        if(z.Mag2()> escape && k>nIters)
                 		{
                 	        it=k;
                 			break;
                 		}
                 	}
             }
             pVarTP.color=cf_runner.rgbColor4(z,it,maxIters);
             pVarTP.rgbColor = false;
         }
         if(colortype==5)
         {
             for( k=0;k< maxIters;k++)
             {
             	z=  cf_runner.f(z, c);
             	if(k> nIters)
             	{
                    if(z.Mag2()> escape && k>nIters)
             		{
             	        it=k;
             			break;
             		}
             	}
             }
                tcolor = new int[3];
                tcolor=cf_runner.rgbColor(z,contrast);
                pVarTP.rgbColor = true;
                pVarTP.redColor = tcolor[0];
                pVarTP.greenColor = tcolor[1];
                pVarTP.blueColor = tcolor[2];
         }
	     pVarTP.x+= pAmount*(uV.x);
	     pVarTP.y+= pAmount*(uV.y);
	     
	     double cz;
	     if(colortype==4)
	    	 cz=pVarTP.color;
	     else
	       cz=greyscale(tcolor[0],tcolor[1],tcolor[2]);
	     
		 double dz = cz * scale_z + offset_z;
		 if (reset_z == 1) {
		      pVarTP.z = dz;
		  }
		    else {
		      pVarTP.z += dz;
		    }
    }
	

	public String getName() {
		return "f_complex";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}

	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return (new Object[] { seed,contrast,zoom,stepx,stepy,escape,maxIters,nIters,julia,c_re,c_im,colortype,colorOnly,scale_z,offset_z,reset_z});
	}

	public int range(int min, int max) 
	{
		return rnd.nextInt((max - min) + 1) + min;
	}
	
	public double drange(double min, double max) 
	{
	  return min + rnd.nextDouble()*(max-min);
	}
	
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_RANDOMIZE)) {
			seed = (int)Tools.limitValue(pValue, 1 , 10000000);
			rnd=new Random(seed);
			rnd.nextDouble();
			julia=1;
		    colortype=range(0,5);
			escape=drange(0.1,7.5);
			nIters=range(1,7);
			maxIters=range(15,50);
			c_re=drange(0.0,1.0);
			c_im=drange(-1.0,0.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_CONTRAST)) {
			contrast=Tools.limitValue(pValue, 0.0 , 1.0);
		} 
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom=pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_STEPX)) {
			stepx=pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_STEPY)) {
			stepy=pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_ESCAPE)) {
			escape=pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_MAXITERS)) {

			maxIters=(int)Tools.limitValue(pValue, 1 , 250);
		} 
		else if (pName.equalsIgnoreCase(PARAM_NITERS)) {
			nIters= (int)Tools.limitValue(pValue, 1 , maxIters);
		} 
		else if (pName.equalsIgnoreCase(PARAM_JULIA)) {

			julia=(int)Tools.limitValue(pValue, 0 , 1);
		} 
		else if (pName.equalsIgnoreCase(PARAM_CRE)) {
			c_re=pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_CIM)) {
			c_im= pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_COLORTYPE)) {
			colortype= (int)Tools.limitValue(pValue, 0 , 5);
		} 
		else if (pName.equalsIgnoreCase(PARAM_DC)) {
			colorOnly= (int)Tools.limitValue(pValue, 0 , 1);
		} 
		else if (pName.equalsIgnoreCase(PARAM_SCALE_Z)) {
			scale_z =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_OFFSET_Z)) {
			offset_z =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_RESET_Z)) {
			reset_z  =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else
		    throw new IllegalArgumentException(pName);
	}
	
	  @Override
	  public String[] getRessourceNames() {
	    return ressourceNames;
	  }


	  @Override
	  public byte[][] getRessourceValues() {
	    return new byte[][]{(code_func != null ? code_func.getBytes() : null)};
	  }

	  @Override
	  public RessourceType getRessourceType(String pName) {
	    if (pName.equals(RESSOURCE_CODE)) {
	      return RessourceType.JAVA_CODE;
	    } else {
	      return super.getRessourceType(pName);
	    }
	  }

	  @Override
	  public void setRessource(String pName, byte[] pValue) {
	    if (RESSOURCE_CODE.equalsIgnoreCase(pName)) {
	      code_func = pValue != null ? new String(pValue) : "";
	    } else
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


