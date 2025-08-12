package org.jwildfire.create.tina.variation;


import java.util.Random;
import js.glsl.G;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CutBricksFunc  extends VariationFunc   implements SupportsGPU {


	/*
	 * Variation :cut_bricks
	 * Date: august 29, 2019
	 * Reference:  https://thebookofshaders.com/09/
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_SIZE = "size";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	
	int seed=1000;
	int mode=1;
	double size=0.9;
    double zoom=3.0;
    int invert=0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    double x0=0.,y0=0.;
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_SIZE,PARAM_ZOOM,PARAM_INVERT};


	vec2 brickTile(vec2 _st){

	    // Here is where the offset is happening
	    _st.x += G.step(1., G.mod(_st.y,2.0)) * 0.5;

	    return G.fract(_st);
	}

	double box(vec2 _st, vec2 _size){
	    _size = new vec2(0.5).minus(_size.multiply(0.5));
	    vec2 uv = G.smoothstep(_size,_size.plus(new vec2(1e-4)),_st);
	    uv = uv.multiply(G.smoothstep(_size,_size.plus(new vec2(1e-4)),new vec2(1.0).minus(_st)));
	    return uv.x*uv.y;
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
		    
		    
		    vec2 u =new vec2(x*zoom,y*zoom);
            u=u.plus(new vec2(x0,y0));
		    u = brickTile(u);

			double color = box(u,new vec2(size));
              	
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
		return "cut_bricks";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed, mode, size,zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SIZE)) {
			size = Tools.limitValue(pValue, 0.1 , 0.9999);
		}
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if(pName.equalsIgnoreCase(PARAM_SEED))
		{
			   seed =   (int)pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		         // time = (double) (elapsed_time / 1000.0);
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
		size = Math.random() * 0.78 + 0.2;
		zoom = Math.random() * 9.0 + 1.0;
		invert = (int) (Math.random() * 2);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		    float x,y,px_center,py_center;"
	    		+"		    "
	    		+"		    if( __cut_bricks_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		      px_center=0.0;"
	    		+"		      py_center=0.0;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT();"
	    		+"		     y=RANDFLOAT();"
	    		+"		      px_center=0.5;"
	    		+"		      py_center=0.5;		     "
	    		+"		    }"
	    		+"		    "
	    		+"		    "
	    		+"		    float2 u =make_float2(x* __cut_bricks_zoom ,y* __cut_bricks_zoom );"
	    		+"		    u = cut_bricks_brickTile(u);"
	    		+"			float color = cut_bricks_box(u,make_float2( __cut_bricks_size,__cut_bricks_size ));"
	    		+"              	"
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_bricks_invert ==0)"
	    		+"		    {"
	    		+"		      if (color>0.0)"
	    		+"		      { x=0;"
	    		+"		        y=0;"
	    		+"		        __doHide = true;	        "
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color<=0.0)"
	    		+"			      { x=0;"
	    		+"			        y=0;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __cut_bricks * (x-px_center);"
	    		+"		    __py = __cut_bricks * (y-py_center);"
                + (context.isPreserveZCoordinate() ? "__pz += __cut_bricks * __z;\n" : "");	    
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__	float2  cut_bricks_brickTile (float2 _st){"
	    		+"	    _st.x += step(1., mod(_st.y,2.0)) * 0.5;"
	    		+"	    return fract(_st);"
	    		+"	}"
	    		+"__device__	float  cut_bricks_box (float2 _st, float2 _size){"
	    		+"	    _size = make_float2(0.5,0.5)-(_size*(0.5));"
	    		+"	    float2 uv = smoothstep(_size,_size+(make_float2(1.0e-4,1.0e-4)),_st);"
	    		+"	    uv = uv*(smoothstep(_size,_size+(make_float2(1e-4,1.0e-4)),make_float2(1.0,1.0)-(_st)));"
	    		+"	    return uv.x*uv.y;"
	    		+"	}";
	  }
}

