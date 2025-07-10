package org.jwildfire.create.tina.variation;



import js.glsl.G;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class  CutMetaBallsFunc  extends VariationFunc  implements SupportsGPU {

	/*
	 * Variation : cut_metaballs
	 * Autor: Jesus Sosa
	 * Date: August 20, 2019
	 * Reference & Credits:  https://thebookofshaders.com/edit.php#12/metaballs.frag
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	private static final String PARAM_TIME = "time";
	

    int mode=1;
	double zoom=7.0;
	private int invert = 0;
	double time=0.0;
	

	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_ZOOM,PARAM_INVERT,PARAM_TIME};



    vec2 random2( vec2 p ) {
      return G.fract(G.sin(new vec2(G.dot(p,new vec2(127.1,311.7)),G.dot(p,new vec2(269.5,183.3)))).multiply(43758.5453));
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
  
		    vec2 st=new vec2(x*zoom,y*zoom);
		    
		    double color=0.;
		    // Tile the space
		    vec2 i_st = G.floor(st);
		    vec2 f_st = G.fract(st);

		    double m_dist = 1.;  // minimun distance
		    for (int j= -1; j <= 1; j++ ) {
		        for (int i= -1; i <= 1; i++ ) {
		            // Neighbor place in the grid
		            vec2 neighbor = new vec2((double)i,(double)j);

		            // Random position from current + neighbor place in the grid
		            vec2 offset = random2(i_st.plus(neighbor));

		            // Animate the offset
		            offset = G.sin(offset.multiply(6.2831).plus(time)).multiply(0.5).plus(0.5);

		            // Position of the cell
		            vec2 pos = neighbor.plus(offset).minus( f_st);

		            // Cell distance
		            double dist = G.length(pos);

		            // Metaball it!
		            m_dist = G.min(m_dist, m_dist*dist);
		        }
		    }

		    // Draw cells
		    color += G.step(0.060, m_dist);
		    
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color==0.0)
		      { x=0.;
		        y=0.;
		        pVarTP.doHide = true;
		      }
		    } else
		    {
			      if (color>0.0 )
			      { x=0.;
			        y=0.;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * (x-px_center);
		    pVarTP.y = pAmount * (y-py_center);
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }

	public String getName() {
		return "cut_metaballs";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { 
		return new Object[] { mode,zoom,invert,time};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			   mode =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			   invert =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
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
		time = Math.random() * 100000;
		zoom = Math.random() * 49.0 + 1.0;
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
	    		+"		    if( __cut_metaballs_mode ==0)"
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
	    		+"  "
	    		+"		    float2 st=make_float2(x* __cut_metaballs_zoom ,y* __cut_metaballs_zoom );"
	    		+"		    "
// test random2() method
//	    		+"		    float2 t0 = cut_metaballs_random2(st);"    		
//	    		+"		    float color=t0.y;"
// original code	    		
   		        +"          float color=0.0f;"
	    		+"		    float2 i_st = floorf(st);"
	    		+"		    float2 f_st = fract(st);"
	    		+"		    float m_dist = 1.0f;  "
	    		+"		    for (int j= -1; j <= 1; j++ ) {"
	    		+"		        for (int i= -1; i <= 1; i++ ) {"
    		    +"		            float2 neighbor = make_float2((float)i,(float)j);"
	    		+"		            float2 offset = cut_metaballs_random2(i_st+neighbor);"
	    		+"		            float2 tmp = offset*6.2831f + __cut_metaballs_time;"
	    		+"		            offset = sinf(tmp)*0.5f + 0.5f;"
	    		+"		            float2 pos = neighbor + offset - f_st;"
	    		+"		            float dist = length(pos);"
	    		+"		            m_dist = fminf(m_dist, m_dist*dist);"
	    		+"		        }"
	    		+"		    }"
	    		+"		    color += step(0.060, m_dist);"
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_metaballs_invert ==0)"
	    		+"		    {"
	    		+"		      if (color==0.0f)"
	    		+"		      { x=0.0f;"
	    		+"		        y=0.0f;"
	    		+"		        __doHide = true;"
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color>0.0f )"
	    		+"			      { x=0.0f;"
	    		+"			        y=0.0f;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __cut_metaballs * (x-px_center);"
	    		+"		    __py = __cut_metaballs * (y-py_center);"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_metaballs * __z;\n" : "");
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__ float2  cut_metaballs_random2 ( float2 p ) {"
	    		+"  float2 tm=make_float2( dot(p,make_float2(127.1f,311.7f)) , dot(p,make_float2(269.5f,183.3f)) );"
	    		+"      return fract(sinf(tm)*43758.5453f);"
	    		+"    }";
	  }
}


