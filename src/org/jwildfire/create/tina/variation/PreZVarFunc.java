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



public class PreZVarFunc  extends VariationFunc {

	/*
	 * Variation :pre_c_var
	 * Date: august 29, 2019
	 * Author: Jesus Sosa
	 * Reference & Credits:   Cosmic Pattern  https://www.shadertoy.com/view/3dyXWK
	 */



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
	

		
	String code_func ="import js.glsl.vec2;\n" + 
			"public vec2 f(vec2 z)\n" +
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
			// String code= code_func;	
			cf_runner = glslFuncRunner.compile(code_func);
			
		} catch (Throwable ex) {
			System.out.println("##############################################################");
			System.out.println(ex.getMessage());
			System.out.println("##############################################################");
			System.out.println(code_func);
			System.out.println("##############################################################");
		}
	}

    public void validate() {

    	  try {
    		  if (code_func != null) {
    			glslFuncRunner.compile( code_func);
    		  }
    	  }
    	  catch (Throwable ex) {
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
                          	

		    pAffineTP.x = pAmount * f.x ;
		    pAffineTP.y = pAmount * f.y ;
		    if (pContext.isPreserveZCoordinate()) {
		      pAffineTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  


	  
	public String getName() {
		return "pre_c_var";
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
	
	@Override
	public int getPriority() {
		return -1;
	}
}

