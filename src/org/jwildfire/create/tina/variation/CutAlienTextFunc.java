package org.jwildfire.create.tina.variation;



import java.util.Random;
import js.glsl.G;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CutAlienTextFunc  extends VariationFunc  implements SupportsGPU {

	/*
	 * Variation :cut_alientext
	 * Date: october 16, 2019
	 * Reference:  https://www.shadertoy.com/view/4lscz8
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_SUBDIVS = "subdivisions";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	
	int seed=1000;
	int mode=1;

	int maxSubdivisions=3;
	
    double zoom=1.0;
    int invert=0;

	Random randomize=new Random(seed);
 	
    double x0=0.,y0=0.;
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_SUBDIVS,PARAM_ZOOM,PARAM_INVERT};

	double random2d(vec2 n) 
	{ 
		return G.fract(Math.sin(G.dot(n, new vec2(129.9898, 4.1414))) * 2398.5453);
	}

	public vec2 getCellIJ(vec2 uv, double gridDims){
		return G.floor(uv.multiply(gridDims)).division(gridDims);
	}

	public double letter(vec2 coord, double size)
	{
		vec2 gp = G.floor(coord.division(size).multiply(7.)); // global
		vec2 rp = G.floor(G.fract(coord.division(size)).multiply(7.)); // repeated
		vec2 odd = G.fract(rp.multiply(0.5)).multiply( 2.);
		double rnd = random2d(gp);
		double c = Math.max(odd.x, odd.y) * G.step(0.5, rnd); // random lines
		c += Math.min(odd.x, odd.y); // fill corner and center points
		c *= rp.x * (6. - rp.x); // cropping
		c *= rp.y * (6. - rp.y);
		return G.clamp(c, 0., 1.);
	}
	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    double x,y,px_center,py_center;
		    
		    if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		      px_center=0.0;
		      py_center=0.0;
		    }else
		    {
		     x=pContext.random();
		     y=pContext.random();
		      px_center=0.5;
		      py_center=0.5;		     
		    }
		    
		    // Scaling and translation.
            vec2 uv=new vec2(x*zoom,y*zoom);
		    uv=uv.plus(new vec2(x0,y0));
	
		    uv.y -= y0;
		    double dims=2.0;
		    double cellRand;
		    vec2 ij;
		    
		   	for(int i = 0; i <= maxSubdivisions ; i++) { 
		        ij = getCellIJ(uv, dims);
		        cellRand = random2d(ij);
		        dims *= 2.0;
		        //decide whether to subdivide cells again
		        double cellRand2 = random2d(ij.plus( 454.4543));
		        if (cellRand2 > 0.3){
		        	break; 
		        }
		    }
		   
		    //draw letters    
		    double color = letter(uv, 1.0 / (dims));
            
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color>0.0)
		      { x=0;
		        y=0;
		        pVarTP.doHide = true;	        
		      }
		    } else
		    {
			      if (color<=0.0)
			      { x=0;
			        y=0;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * (x-px_center);
		    pVarTP.y = pAmount * (y-py_center);
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
		  x0=seed*randomize.nextDouble();
		  y0=seed*randomize.nextDouble();
	   }

	  
	public String getName() {
		return "cut_alientext";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed, mode,maxSubdivisions,zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		 if(pName.equalsIgnoreCase(PARAM_SEED))
			{
				   seed =   (int)pValue;
			       randomize=new Random(seed);
			}
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_SUBDIVS)) {
			maxSubdivisions =(int)Tools.limitValue(pValue, 0 , 4);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
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
	public void randomize() {
		// Don't change mode
		seed = (int) (Math.random() * 1000000);
		maxSubdivisions = (int) (Math.random() * 5);
		zoom = Math.random() * 3.9 + 0.1;
		invert = (int) (Math.random() * 2);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}

	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return    "	    float x,y,px_center,py_center,x0=0.0,y0=0.0;\r\n"
	    		+"		    if( __cut_alientext_mode ==0)\n"
	    		+"		    {\n"
	    		+"		      x= __x;\n"
	    		+"		      y =__y;\n"
	    		+"		      px_center=0.0;\n"
	    		+"		      py_center=0.0;\n"
	    		+"		    }else\n"
	    		+"		    {\n"
	    		+"		     x=RANDFLOAT();\n"
	    		+"		     y=RANDFLOAT();\n"
	    		+"		      px_center=0.5;\n"
	    		+"		      py_center=0.5;\n"
	    		+"		    }\n"
	    		+"		    \n"
	    		+"          float2 uv=make_float2(x* __cut_alientext_zoom ,y * __cut_alientext_zoom);\n"
	    		+"          uv=uv+make_float2(x0,y0);\n"
	    		+"          uv.y=uv.y-y0;\n"	    		
	    		+"		    float dims=2.0;\n"
	    		+"		    float cellRand;\n"
	    		+"		    float2 ij;\n"
	    		+"		    \n"
	    		+"		   	for(int i = 0; i <= __cut_alientext_subdivisions ; i++) { \n"
	    		+"		        ij =cut_alientext_getCellIJ (uv, dims);\n"
	    		+"		        cellRand =cut_alientext_random2d (ij);\n"
	    		+"		        dims *= 2.0;\n"
	    		+"		        float cellRand2 =cut_alientext_random2d (ij+( 454.4543));\n"
	    		+"		        if (cellRand2 > 0.3){\n"
	    		+"		        	break; \n"
	    		+"		        }\n"
	    		+"		    }\n"
	    		+"		   \n"
	    		+"		    float color =cut_alientext_letter (uv, 1.0 / (dims));\n"
	    		+"            \n"
	    		+"		    __doHide=false;\n"
	    		+"		    if( __cut_alientext_invert ==0)\n"
	    		+"		    {\n"
	    		+"		      if (color>0.0)\n"
	    		+"		      { x=0.0;\n"
	    		+"		        y=0.0;\n"
	    		+"		        __doHide = true;	        \n"
	    		+"		      }\n"
	    		+"		    } else\n"
	    		+"		    {\n"
	    		+"			      if (color<=0.0)\n"
	    		+"			      { x=0.0;\n"
	    		+"			        y=0.0;\n"
	    		+"			        __doHide = true;\n"
	    		+"			      }\n"
	    		+"		    }\n"
	    		+"		    __px = __cut_alientext * (x-px_center);\n"
	    		+"		    __py = __cut_alientext * (y-py_center);\n"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_alientext * __z;\n" : "");
	  }
	 
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__ float cut_alientext_random2d (float2 n) \n"
	    		+"	{ \n"
	    		+"		return fract(sin(dot(n, make_float2(129.9898, 4.1414))) * 2398.5453);\n"
	    		+"	}\n"
	    		+"\n"
	    		+"	__device__ float2 cut_alientext_getCellIJ (float2 uv, float gridDims){\n"
	    		+"		return floorf(uv*(gridDims))/(gridDims);\n"
	    		+"	}\n"
	    		+"\n"
	    		+"__device__ float cut_alientext_letter (float2 coord, float size)\n"
	    		+"	{\n"
	    		+"		float2 gp = floorf(coord/(size)*(7.));\n"
	    		+"		float2 rp = floorf(fract(coord/(size))*(7.)); \n"
	    		+"		float2 odd = fract(rp*(0.5))*( 2.);\n"
	    		+"		float rnd = cut_alientext_random2d (gp);\n"
	    		+"		float c = fmaxf(odd.x, odd.y) * step(0.5, rnd);\n"
	    		+"		c += fminf(odd.x, odd.y); \n"
	    		+"		c *= rp.x * (6. - rp.x); \n"
	    		+"		c *= rp.y * (6. - rp.y);\n"
	    		+"		return clamp(c, 0., 1.);\n"
	    		+"	}\n";
	  }	
}

