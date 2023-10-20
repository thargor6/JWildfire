package org.jwildfire.create.tina.variation;


import java.util.Random;
import js.glsl.G;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CutSWarpFunc  extends VariationFunc implements SupportsGPU{

	/*
	 * Variation :cut_swarp
	 * Date: august 30, 2019
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_MODE = "mode";	
	private static final String PARAM_TIME = "time";	
	private static final String PARAM_TYPE = "type";	
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	

	int seed=1000;
	int mode=1;
	double time=0.;
	int type=0;
    double zoom=1.0;
    int invert=0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_TIME,PARAM_TYPE,PARAM_ZOOM,PARAM_INVERT};

	double dist(vec2 p)
	{   
		double distance=0.;
		if(type==0)
	      distance= G.length(p); // Circular.
		p = G.abs(p); // Uncomment first.
		if(type==1)
			distance= Math.max(p.x, p.y); // Square.
		if(type==2)
			distance=Math.max(p.x*.8660254 + p.y*.5, p.y); // Hexagon.
		if(type==3)
		    distance=Math.max(Math.max(p.x, p.y), (p.x + p.y)*.7071); // Octagon.
		return distance;
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
		    	    
		    vec2 uv = new vec2(x*zoom,y*zoom);	

		    // Coordinate warp.
		    vec2 v0=uv.multiply(5.).plus(  G.cos(uv.multiply(11.).plus(time))).plus(time);
		    vec2 v1=G.sin(v0);
		    double t0=G.dot(v1, new vec2(.5));
		    uv = uv.plus(t0*.02);    
		    uv.y += Math.sin(uv.x*7. + Math.cos(uv.x*4. + time*2.)*1. + time)*.05;


		    // Vertical screen split mapping.
		    double s = (Math.ceil(uv.x)*2. - 1.);

		    double m = 6.; // All non-zero rational numbers.
		    uv.y += s/m;

		    // Positive odd integers: 1, 3, 5, 7, etc.
		    double n = 5.; 
		    double k = Math.sin(s*3.14159/2. + dist(uv)*n*m*3.14159/2.);
		    


		    double color=Math.sqrt(Math.max(k, 0.));
		    
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color>0.5)
		      { x=0;
		        y=0;
		        pVarTP.doHide = true;	        
		      }
		    } else
		    {
			      if (color<=0.5)
			      { x=0;
			        y=0;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * x;
		    pVarTP.y = pAmount * y;
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
	   }

	  
	public String getName() {
		return "cut_swarp";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed,mode, time,type, zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		if(pName.equalsIgnoreCase(PARAM_SEED))
		{
			   seed =   (int)pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_TYPE)) {
			type =(int)Tools.limitValue(pValue, 0 , 3);
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
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		  float x,y;  "
	    		+"		  if( __cut_swarp_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT()-0.5;"
	    		+"		     y=RANDFLOAT()-0.5;		     "
	    		+"		    }"
	    		+"		    	    "
	    		+"		    float2 uv = make_float2(x* __cut_swarp_zoom ,y* __cut_swarp_zoom );	"
	    		+"		    "
	    		+"		    float2 v0=uv*(5.)+(  cosf(uv*(11.)+( __cut_swarp_time )))+( __cut_swarp_time );"
	    		+"		    float2 v1=sinf(v0);"
	    		+"		    float t0=dot(v1, make_float2(0.5,0.5));"
	    		+"		    uv = uv+(t0*.02);    "
	    		+"		    uv.y += sinf(uv.x*7. + cos(uv.x*4. +  __cut_swarp_time *2.)*1. +  __cut_swarp_time )*.05;"
	    		+"		    "
	    		+"		    float s = (ceilf(uv.x)*2. - 1.);"
	    		+"		    float m = 6.; "
	    		+"		    uv.y += s/m;"
	    		+"		    "
	    		+"		    float n = 5.; "
	    		+"		    float k = sinf(s*3.14159/2. + cut_swarp_dist(uv,__cut_swarp_type)*n*m*3.14159/2.);"
	    		+"		    "
	    		+"		    float color=sqrt(max(k, 0.));"
	    		+"		    "
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_swarp_invert ==0)"
	    		+"		    {"
	    		+"		      if (color>0.5)"
	    		+"		      { x=0;"
	    		+"		        y=0;"
	    		+"		        __doHide = true;	        "
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color<=0.5)"
	    		+"			      { x=0;"
	    		+"			        y=0;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __cut_swarp * x;"
	    		+"		    __py = __cut_swarp * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_swarp * __z;\n" : "");
	  }
	 
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return  "__device__	float  cut_swarp_dist (float2 p,int type)"
	    		+"	{   "
	    		+"		float distance=0.0f;"
	    		+"		if(type==0)"
	    		+"	      distance= length(p); "
	    		+"		p = abs(p); "
	    		+"		if(type==1)"
	    		+"			distance= max(p.x, p.y); "
	    		+"		if(type==2)"
	    		+"			distance=max(p.x*.8660254 + p.y*.5, p.y); "
	    		+"		if(type==3)"
	    		+"		    distance=max(max(p.x, p.y), (p.x + p.y)*.7071); "
	    		+"		return distance;"
	    		+"	}";
	  }
}

