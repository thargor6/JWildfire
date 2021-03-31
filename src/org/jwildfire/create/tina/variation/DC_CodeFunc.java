package org.jwildfire.create.tina.variation;

import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.glslFuncRunner;
import js.glsl.vec2;
import js.glsl.vec3;


public class DC_CodeFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_code
	 * 
	 * Autor: Jesus Sosa
	 * Date: may 22, 2019
	 * Reference 
	 */
	
public static class randomizeCode
{
	double rotationProbability=0.5;
	double pointRotationProb=0.5;
	double argRotationProb=0.9;
	
	String[] singleArgument= {"","","","","","","c_sin","c_cos"};
	String[] pArgFunctions= {"","","","c_sin","c_ln","c_sqrt"};
	String[] lengthFunc=   {  "","","","","","","","",
			              "c_tan","c_atan","c_tanh",
			              "c_sin","c_asin","c_sinh",
			              "c_exp","c_ln",
			              "c_ln","c_inv","c_sinh","c_tanh","c_asinh","c_acosh"};
	
	String firstArg=new String();
	String secondArg=new String();
	String pArg=new String();
	FlameTransformationContext pContext;
	
	String frameDef=new String();
	  
	long seed=10000L;
	Random rnd=new Random(seed);
	
	public randomizeCode(long seed)
	{
	   this.seed=seed;
	   rnd=new Random(seed);
	}
	
	public boolean prob(double p)
	{ 
		return rnd.nextDouble() < p; 
	}
	
	public boolean evalRotate()
	{
		if(!prob(rotationProbability))
			return false;
		return true;
	}
	
	public String RotateZ(String arg)
	{
		if(prob(argRotationProb))
		{
			argRotationProb=0.1;
			String Suffix= prob(0.8) ? "sin(a)" : "a";
			return arg + ".multiply(" + Suffix + ")";
		}
		return arg;
	}
	
	public String getDef()
	{
		String frameDef="   double a=3.14*t*0.5;\n";
		if (prob(pointRotationProb)) {
			frameDef += "   c = new vec2(p.x*cos(a) - p.y * sin(a), p.y*cos(a) + p.x * sin(a));";
			argRotationProb = 0.2;
		}
		return frameDef;
	}
	
	public String randomPicker(String[] arr)
	{
		  if (arr.length == 1) 
			  return arr[0];

		  int index = (int) Math.round(rnd.nextDouble() * arr.length);
		  if (index > arr.length - 1) 
			  index = arr.length - 1;
		  return arr[index];
	}
	
	public String applyArgument(String func, String arg)
	{
		  return (func!="")?   func + "(" + arg +")" : arg;
	}
	
	
    public String combiner(String fArg,String sArg)
    {
    	return "c_mul(" + fArg + "," + sArg + ")";
    }
	
    public boolean canInv(String zLine) {
    	  // For some reason these function are not working well with c_inv
    	  return (zLine.indexOf("tan") == -1 && zLine.indexOf("sin") == -1);
    }
    
	public String generateRandom(double stepX, double stepY)
	{
		 String Lines="public vec3 getRGBColor(double xp,double yp)\n" +
		      "{\n" +
			  "  vec2 p=new vec2(xp,yp);\n"+
			  "  p=p.multiply(zoom).minus(new vec2(stepX,stepY));\n"+
			  "  double t=" + rnd.nextDouble()*30. + ";\n" +
			  "  vec3 color=new vec3(0.0);\n";
		 
		 
		 firstArg = applyArgument(randomPicker(singleArgument), "z");
		 secondArg = applyArgument(randomPicker(singleArgument), "z");
		 pArg = applyArgument(randomPicker(pArgFunctions), "c");
		 
		 boolean invertZ = prob(0.2);
		 if(prob(0.4)) 
		 {
			 Lines+=  "   vec2 z= p;\n" +
			          "   vec2 c = new vec2(" + rnd.nextDouble() + "," + rnd.nextDouble() + ");\n";
		 }
		 else 
		 {
			 if(invertZ)
			 {
				Lines+= "  vec2 z = new vec2(1.0);\n"; 
			 }
			 else
			 {
					Lines+= "  vec2 z = new vec2(0.0);\n";  
			 }
			 Lines+="   vec2 c=p;\n";
		 }

		 if( evalRotate())
		 {  String rotation=getDef();
		    Lines+=rotation;
		    firstArg=RotateZ(firstArg);
		    secondArg=RotateZ(secondArg);
		 }
		 
		 String zLine=combiner(firstArg,secondArg);
		 
		  if (invertZ && canInv(zLine)) zLine = "c_inv(" + zLine +")";
		  if (prob(0.1))
			  pArg = "c_inv(" + pArg + ")";
		  
		 Lines+="   for(int k = 0; k < 32; ++k) {\n" +
			    "      if (length(z) > 2.) break;\n" +
			    "          z = c_add(" + zLine + "," + pArg + ");\n" +
			    "      t = (double)k;\n" +
			    "   }\n";
		 
		  String lLine =  applyArgument(randomPicker(lengthFunc), "z");
		  
		  String[] colors = {   " new vec3(t/64., t/32., t/24.)",
		    " new vec3(t/64., t/32., t/16.)",
		    " new vec3(t/14., t/42., t/22.)",
		    " new vec3(t/32., t/64., t/24.)",
		    " new vec3(t/32., t/48., t/24.)"
		               };
		  String selectedColor = randomPicker(colors);
           Lines+="   double t1=length(" + lLine  + ");\n";
		   Lines+= "   color = " + selectedColor + ".multiply(t1);\n";
		   Lines+= "   return color;\n";
		  Lines+="}\n";
		  return Lines;
	}
}

	private static final long serialVersionUID = 1L;



	private static final String PARAM_RANDOMIZE = "randomize"; 
	private static final String PARAM_PANX = "PanX";
	private static final String PARAM_PANY = "PanY";
	private static final String PARAM_ZOOM = "zoom"; 




	private static final String[] additionalParamNames = { PARAM_RANDOMIZE,PARAM_PANX,PARAM_PANY,PARAM_ZOOM};

	
    private static final String RESSOURCE_CODE = "code";
	private static final String[] ressourceNames = { RESSOURCE_CODE };

	double panning=0.0;
	

    int seed=10000;
    double zoom=4.0;

    double stepX=0.5;
    double stepY=0.0;
    

	
	glslFuncRunner cf_runner=null;
	 
    String header0="import js.glsl.vec2;\n"+
                   "import js.glsl.vec3;\n"+
                   "import js.glsl.vec4;\n"+
                   "import js.glsl.mat2;\n"+
                   "import js.glsl.mat3;\n"+
                   "import js.glsl.mat4;\n";
    
	String code_func="public vec3 getRGBColor(double xp,double yp)\n" +
               "{\n" +
               "  vec3 color=new vec3(0.0);\n" +
               "  vec2 st=new vec2(xp,yp);\n" + 
	           "  vec2 c = st.multiply(zoom).minus(new vec2(stepX,stepY));\n" +
	           "  color = fractal(c);\n" +        
               "  return color;\n"+
               "}\n";
	 

	@Override
	public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		super.initOnce(pContext, pLayer, pXForm, pAmount);

	}	 

	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) 
	{

           compile();
        	   
	}
	
	public String AddParams()
	{
		String sReturn= "double zoom=" + zoom + ";\n"+
				"double stepX=" + stepX + ";\n"+
		"double stepY=" + stepY + ";\n";

		return sReturn;
	}
	


	
    public void compile() {

  	  try {
  		  cf_runner = glslFuncRunner.compile(header0+ AddParams() + code_func);
  	  }
  	  catch (Throwable ex) {
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
  			glslFuncRunner.compile(header0 + AddParams() + code_func);
  		  }
  	  }
  	  catch (Throwable ex) {
  		  throw new RuntimeException(ex);
  	  }
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
	     
		 
	        color=cf_runner.getRGBColor(uV.x,uV.y);
	     //color=getRGBColor(uV.x,uV.y);
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
		return "dc_code";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed,stepX,stepY,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_RANDOMIZE)) {
			seed = (int)Tools.limitValue(pValue, 0 , 10000000);
			if(seed>0)
			{
			   randomizeCode rc=new randomizeCode((long)seed);
			  //  System.out.println(rc.generateRandom(stepX,stepY));
				code_func=rc.generateRandom(stepX,stepY);
				setRessource(RESSOURCE_CODE,code_func.getBytes());
			}
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
    public String[] getRessourceNames() {
      return ressourceNames;
    }

    
    @Override
    public byte[][] getRessourceValues() {
      return new byte[][] { (code_func != null ? code_func.getBytes() : null)};
    }

    @Override
    public RessourceType getRessourceType(String pName) {
	  if (pName.equals(RESSOURCE_CODE)) {
		   return RessourceType.JAVA_CODE;
	  }
      else {
        return super.getRessourceType(pName);
      }
    }

    @Override
    public void setRessource(String pName, byte[] pValue) {
	  if (RESSOURCE_CODE.equalsIgnoreCase(pName)) {
		        code_func = pValue != null ?  new String(pValue) : "";
	  }
      else
    	  throw new IllegalArgumentException(pName);
    }

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_EDIT_FORMULA};
	}

}

