package org.jwildfire.create.tina.variation;


import java.io.StringReader;
import java.util.Random;

//import org.codehaus.janino.ClassBodyEvaluator;
//import org.codehaus.janino.Scanner;
import org.jwildfire.base.Tools;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
// import org.jwildfire.create.tina.variation.F_ComplexFunc.ComplexFuncRunner;

import js.colordomain.Complex;

//import js.glsl.G;
import js.glsl.glslFuncRunner;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class ZVarFunc  extends VariationFunc {

	/*
	 * Variation :zvar
	 * Date: august 29, 2019
	 * Author: Jesus Sosa
	 * Reference & Credits:   Cosmic Pattern  https://www.shadertoy.com/view/3dyXWK
	 */

/*	  public static class ComplexFuncRunner 
	  {

		    public static ComplexFuncRunner compile(String pScript) throws Exception {
		      ComplexFuncRunner res = (ComplexFuncRunner) ClassBodyEvaluator.createFastClassBodyEvaluator(new Scanner(null, new StringReader(pScript)), ComplexFuncRunner.class, (ClassLoader) null);
		      return res;
		    }
		    
			public vec2 abs(vec2 z)
			{
				return new vec2(G.abs(z.x),G.abs(z.y));
			}
			
			public vec2 iabs(vec2 z)
			{
				return new vec2(z.x,G.abs(z.y));
			}
			
			public vec2 re(vec2 a)
			{
				return new vec2((a).x, 0.);
			}
			
			public vec2 neg(vec2 a)
			{
				return new vec2(-a.x,-a.y);
			}
			
			public vec2 im(vec2 a)
			{
				return new vec2(0., (a).y);
			}

			public vec2 cmul(vec2 a,vec2 b)
			{
				return (new mat2(a.x,a.y, -(a).y, (a).x )).times(b);
			}
			
			public vec2 conj(vec2 a)
			{
				return new vec2( (a).x, -(a).y);
			}
			
			public vec2 cinv(vec2 a)
			{
				return conj(a).division( G.dot(a, a));
			}
			

		    public vec2 cdiv(vec2 a,vec2 b)
			{
				return  cmul(a,cinv(b));
			}
			
			public vec2 cexp(vec2 a)
			{
				return new vec2(Math.cos(a.y), Math.sin(a.y)).multiply(Math.exp(a.x));
			}
			
			public vec2 clog(vec2 a)
			{
				return new vec2( Math.log(G.length(a)), G.atan2((a).y,(a).x) );
			}
			
			public double arg(vec2 a) {
				return G.atan2(a.y, a.x);
			}
			
			public vec2 cpow(vec2 a,double n) {
				return cexp( clog(a).multiply(n));
			}
		    public vec2 csinh(vec2 z)
		    {
		        return new vec2(Math.sinh(z.x) * Math.cos(z.y), Math.cosh(z.x) * Math.sin(z.y));
		    }

		    public vec2 ccosh(vec2 z)
		    {
		        return new vec2(Math.cosh(z.x) * Math.cos(z.y), Math.sinh(z.x) * Math.sin(z.y));
		    }
		    
			public vec2 csin(vec2 z)
			{
			   return new vec2(Math.cosh(z.y) * Math.sin(z.x), Math.sinh(z.y) * Math.cos(z.x));
			}
			
		    public vec2 ccos(vec2 z)
		    {
		        return new vec2(Math.cosh(z.y) * Math.cos(z.x), -Math.sinh(z.y) * Math.sin(z.x));
		    }
		    
		    public vec2 chs(vec2 z)
		    {
		    	return new vec2(-z.x,-z.y);
		    }
		    
		    public vec2 i()
		    {
		    	return new vec2(1.0,0.0);
		    }
		    
		    public vec2 j()
		    {
		    	return new vec2(0.0,1.0);
		    }
		    public double mod(vec2 z)
		    {
		        if(z.x != 0.0D || z.y != 0.0D)
		            return Math.sqrt(z.x * z.x + z.y * z.y);
		        else
		            return 0.0D;
		    }
		    
		    public vec2 cscale(vec2 z, double s)
		    {
		        return new vec2(z.x * s, z.y * s);
		    }
		    
			   public vec2 sinhcosh (double x) {
				      double ex = Math.exp(x);
				      double emx = Math.exp(-x);
				      return cscale(new vec2( ex + emx, ex - emx), 0.5);
				    }    
			    public vec2 csqrt(vec2 z)
			    {
			        double r = Math.sqrt(mod(z));
			        double theta = arg(z) / 2D;
			        return new vec2(r * Math.cos(theta), r * Math.sin(theta));
			    }
			   
			    public vec2 cacos (vec2 z) {
			        vec2 t1 = csqrt(new vec2(z.x * z.y - z.x * z.x + 1.0, -2.0 * z.x * z.y));
			        vec2 t2 = clog(new vec2(t1.x - z.y, t1.y + z.x));
			        return new vec2(Math.PI/2 - t2.y, t2.x);
			      }

			      public vec2 casin (vec2 z) {
			        vec2 t1 = csqrt(new vec2(z.y * z.y - z.x * z.x + 1.0, -2.0 * z.x * z.y));
			        vec2 t2 = clog(new vec2(t1.x - z.y, t1.y + z.x));
			        return new vec2(t2.y, -t2.x);
			      }
			      
			      public vec2 recip (vec2 z) 
			      {
                     return cdiv(i(),z);
				   } 
			        
			   public vec2 ctanh(vec2 z) {
			        vec2 ez = cexp(z);
			        vec2 emz = cexp(cscale(z,-1.0));
			        return cdiv(ez.minus(emz),ez.plus(emz));
			      }
			   
			    public vec2 catan (vec2 z) {
			        float d = (float) (z.x * z.x + (1.0 - z.y) * (1.0 - z.y));
			        vec2 t1 = clog(cscale((new vec2(1.0 - z.y * z.y - z.x * z.x, -2.0 * z.x)),(1.0 / d)));
			        return cscale (new vec2(-t1.y, t1.x),0.5);
			      }

		    public vec2 f(vec2 z) 
			{
		    	vec2 a=iabs(z);		    	
			   return clog(a); 
			 };			
      }*/

	private static final long serialVersionUID = 1L;



	private static final String PARAM_SHIFTX = "shiftX";
	private static final String PARAM_SHIFTY = "shiftY";

	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";

	
	double x0=0;
	double y0=0.0;
	int mode=0;
    double zoom=1.;


 	
    
	private static final String[] additionalParamNames = { PARAM_SHIFTX,PARAM_SHIFTY,PARAM_MODE,PARAM_ZOOM};

	  private static final String RESSOURCE_CODE = "code";
	  private static final String[] ressourceNames = {RESSOURCE_CODE};	
	

		
	String code_func = "public vec2 f(vec2 z)\n" +
			"{\n" +
			"  //vec2 a= c_add(z,new vec2(0.0,0.0));\n" +
	        "  //vec2 b= c_sub(z,new vec2(0.0,0.0));\n" +
	        "  //  return c_mul(a,b);\n" +
	        "\n" +
			"  // return c_exp(z);\n" +
	        "\n" +
			"  // return c_log(z,10.0);\n" +
	        "\n" +
			"  // return c_sqrt(z);\n" +
	        "\n" +
			"  // return c_pow(z,-2.0);\n" +
	        "\n" +
			"  // return c_sin(z);\n" +
	        "\n" +
			"  // return sinh(z);\n" +
	        "\n" +
			"  // return c_cos(z);\n" +
	        "\n" +
			"  // return c_sinh(z);\n" +
	        "\n" +
			"  // return c_cosh(z);\n" +
	        "\n" +
			"  // return cosh(z);\n" +
	        "\n" +
			"  // return c_acos(z);\n" +
	        "\n" +
			"  // return c_asin(z);\n" +
	        "\n" +
			"  // return c_atan(z);\n" +
	        "\n" +
			"  // return tanh(z);\n" +
	        "\n" +
			"  // vec2 pow=new vec2(-2.0,-.60);\n" +
			"  // return c_pow(z,pow);\n" +
	        "\n" +
			"  // return c_conj(z);\n" +
	        "\n" +
			"  // return c_inv(z);\n" +
	        "\n" +
			"  vec2 a=c_add(c_inv(z),c_exp(c_inv(z)));\n"+
			  "return c_add(a,c_exp(new vec2(0.0, 0.0)));\n" +
			"}";

	private glslFuncRunner cf_runner = null;


	public void compile() {
		try {
			String code="import js.glsl.vec2;\n" +
  					     code_func;	
			cf_runner = glslFuncRunner.compile(code);
		} catch (Throwable ex) {
			System.out.println("##############################################################");
			System.out.println(ex.getMessage());
			System.out.println("##############################################################");
			System.out.println(code_func);
			System.out.println("##############################################################");
		}
	}

	@Override
	public void validate() {
		try {
			if (code_func != null) {
				compile();
			}
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) 
	{

		if (cf_runner == null) {
			compile();
		}

	}
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    double x,y;
		    
		    if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		    }else
		    {
		     x=2.0*pContext.random()-1.0;
		     y=2.0*pContext.random()-1.0; 
		    }
		    
		    vec2 z =new vec2(x*zoom,y*zoom);
            z=z.plus(new vec2(x0,y0));

//            vec2 f =  clog(z).plus(cexp(neg(z))).plus( cexp(new vec2(0.0, 0.0)));
            
            vec2 f = cf_runner.f(z);
            
//            double color=G.length(f.minus(G.round(f)));
//            if(color<0.5) color=0.0;
//            else 
//            	color=1.0;
                          	

		    pVarTP.x = pAmount * f.x ;
		    pVarTP.y = pAmount * f.y ;
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  


	  
	public String getName() {
		return "zvar";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  x0,y0, mode,zoom});
	}

	public void setParameter(String pName, double pValue) {

		if(pName.equalsIgnoreCase(PARAM_SHIFTX))
		{
			   x0=pValue;
		}
		else if(pName.equalsIgnoreCase(PARAM_SHIFTY))
		{
			   y0=pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
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

