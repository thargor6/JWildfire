package org.jwildfire.create.tina.variation;


import java.util.Random;
import js.glsl.G;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CutTruchetFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :cut_truchet
	 * Date: February 12, 2019
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_MODE = "mode";
	private static final String PARAM_TYPE = "type";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	
    int mode=1;
	int type=0;
	int seed=1000;
    double zoom=10.0;
    int invert=0;
    double time=0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    
	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_TYPE,PARAM_SEED,PARAM_ZOOM,PARAM_INVERT};

	public static   double random (vec2 st)
	{
		return G.fract(Math.sin(G.dot(new vec2(st.x,st.y),new vec2(12.9898,78.233)))*43758.5453123);
	}

	public static   vec2 random2( vec2 p ) {
		return G.fract(G.sin(new vec2(G.dot(p,new vec2(127.1,311.7)),G.dot(p,new vec2(269.5,183.3)))).multiply(43758.5453));
	}
	
		
	public   vec2 truchetPattern( vec2 _st,  double _index)
	{
		_index = G.fract(((_index-0.5)*2.0));
		if (_index > 0.75) {
			_st = new vec2(1.0).minus(_st);
		} else if (_index > 0.5) {
			_st = new vec2(1.-_st.x,_st.y);
		} else if (_index > 0.25) {
			_st = new vec2(1.0).minus(new vec2(1.0-_st.x,_st.y));
		}
		return _st;
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
		    
		    
		    vec2 st =new vec2(x*zoom,y*zoom);
		    vec2 h = new vec2(G.floor(7.*seed),G.floor(7.*seed));
	//	      vec2 ipos = G.floor(st.multiply(random(new vec2(seed,0))));  // integer
	//	      vec2 fpos = G.fract(st.multiply(random(new vec2(seed,0))));  // fraction
			  vec2 ipos = G.floor(st.plus(h.multiply(random2(h))));  // integer
			  vec2 fpos = G.fract(st.plus(h.multiply(random2(h))));  // fraction
		    
		      vec2 tile = G.truchetPattern(fpos, random( ipos));

		      double color = 0.0;

		      // Maze
		      if(type==0)
		      { 
		        color = G.smoothstep(tile.x-0.3,tile.x,tile.y)-
		              G.smoothstep(tile.x,tile.x+0.3,tile.y);
		      } else if(type==1)
		      // Circles
		      { 
		    	  color = (G.step(G.length(tile),0.6) -
		                G.step(G.length(tile),0.4) ) +
		               (G.step(G.length(tile.minus(new vec2(1.))),0.6) -
		                G.step(G.length(tile.minus(new vec2(1.))),0.4) );
		      } else if(type==2)
		      // Truchet (2 triangles)
		      {
		    	  color = G.step(tile.x,tile.y);
		      }
              	
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color==0.0)
		      { x=0;
		        y=0;
		        pVarTP.doHide = true;	        
		      }
		    } else
		    {
			      if (color>0.0)
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


	   }

	  
	public String getName() {
		return "cut_truchet";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  mode,type,seed, zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_TYPE)) {
			type = (int)Tools.limitValue(pValue, 0 , 2);
		}
		else if(pName.equalsIgnoreCase(PARAM_SEED))
		{
			   seed =   (int)pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);
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
		type = (int) (Math.random() * 3);
		zoom = Math.random() * 49.0 + 1.0;
		invert = (int) (Math.random() * 2);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		  float x,y;  "
	    		+"		  if( __cut_truchet_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT()-0.5;"
	    		+"		     y=RANDFLOAT()-0.5;		     "
	    		+"		    }"
	    		+"		 "
	    		+"		    float2 st =make_float2(x* __cut_truchet_zoom ,y* __cut_truchet_zoom );"
	    		+"		    float2 h = make_float2(floor(7.* __cut_truchet_seed ),floor(7.* __cut_truchet_seed ));"
	    		+"			float2 ipos = floor(st+(h*(cut_truchet_random2(h)))); "
	    		+"			float2 fpos = fract(st+(h*(cut_truchet_random2(h)))); "
	    		+"		    "
	    		+"		      float2 tile = cut_truchet_truchetPattern(fpos, cut_truchet_random( ipos));"
	    		+"		      float color = 0.0;"
	    		+"		      if( __cut_truchet_type ==0)"
	    		+"		      { "
	    		+"		        color = smoothstep(tile.x-0.3,tile.x,tile.y)-"
	    		+"		                smoothstep(tile.x,tile.x+0.3,tile.y);"
	    		+"		      } else if( __cut_truchet_type ==1)"
	    		+"		      { "
	    		+"		    	  color = (step(length(tile),0.6) -"
	    		+"		                   step(length(tile),0.4) ) +"
	    		+"		                  (step(length(tile-(make_float2(1.0f,1.0f))),0.6) -"
	    		+"		                   step(length(tile-(make_float2(1.0f,1.0f))),0.4) );"
	    		+"		      } else if( __cut_truchet_type ==2)"
	    		+"		      {"
	    		+"		    	  color = step(tile.x,tile.y);"
	    		+"		      }"
	    		+"              	"
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_truchet_invert ==0)"
	    		+"		    {"
	    		+"		      if (color==0.0)"
	    		+"		      { x=0;"
	    		+"		        y=0;"
	    		+"		        __doHide = true;	        "
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color>0.0)"
	    		+"			      { x=0;"
	    		+"			        y=0;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __cut_truchet * x;"
	    		+"		    __py = __cut_truchet * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_truchet * __z;\n" : "");
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__  float  cut_truchet_random  (float2 st)"
	    		+"	{"
	    		+"		return fract(sinf(dot(make_float2(st.x,st.y),make_float2(12.9898,78.233)))*43758.5453123);"
	    		+"	}"
	    		+"__device__  float2  cut_truchet_random2 ( float2 p ) {"
	    		+"		return fract(sinf(make_float2(dot(p,make_float2(127.1,311.7)),dot(p,make_float2(269.5,183.3))))*(43758.5453));"
	    		+"	}"
	    		+"	"
	    		+"		"
	    		+"__device__ float2  cut_truchet_truchetPattern ( float2 _st,  float _index)"
	    		+"	{"
	    		+"		_index = fract(((_index-0.5)*2.0));"
	    		+"		if (_index > 0.75) {"
	    		+"			_st = make_float2(1.0f,1.0f)-(_st);"
	    		+"		} else if (_index > 0.5) {"
	    		+"			_st = make_float2(1.-_st.x,_st.y);"
	    		+"		} else if (_index > 0.25) {"
	    		+"			_st = make_float2(1.0f,1.0f)-(make_float2(1.0-_st.x,_st.y));"
	    		+"		}"
	    		+"		return _st;"
	    		+"	} ";
	  }	

}

