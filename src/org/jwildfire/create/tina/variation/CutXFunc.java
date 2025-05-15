package org.jwildfire.create.tina.variation;



import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class  CutXFunc  extends VariationFunc  implements SupportsGPU {

	/*
	 * Variation : cut_x
	 * Autor: Jesus Sosa
	 * Date: August 20, 2019
	 * Reference & Credits:  https://www.shadertoy.com/view/wtBXD3
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	private static final String PARAM_SIZE   = "size";
	


    int mode=1;
	double zoom=1.0;
	private int invert = 0;
	double size=0.1;




	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_ZOOM,PARAM_INVERT,PARAM_SIZE};

	public mat2 rot(double a)
	{
	 return new mat2 (Math.cos(a), -Math.sin(a), Math.sin(a), Math.cos(a));   
	}

	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;  
		  if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		    }else
		    {
		     x=pContext.random()-0.50;
		     y=pContext.random()-0.50;		     
		    }

			vec2 uv = new vec2 (x,y);
			uv=uv.multiply(zoom);
			
		    vec2 st = G.abs(uv);
		    double color = 0.;
		    
		    double line = 0.;	 
		    double movement = 0.;

		    movement = size;		          

		    st = rot(3.14159265359/4.).times(st);
		    st = G.abs(st);
		    st.y-= movement;
		    line = G.smoothstep(0.0,0.009, st.y) ;
		    color = G.mix(color,1.,line);    
		     
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color<0.1)
		      { x=0.;
		        y=0.;
		        pVarTP.doHide = true;
		      }
		    } else
		    {
			      if (color>=0.1 )
			      { x=0.;
			        y=0.;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * x;
		    pVarTP.y = pAmount * y;
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }

	public String getName() {
		return "cut_x";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { mode,zoom,invert,size};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			   invert =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_SIZE)) {
			   size =   pValue;
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
		if (Math.random() < 0.5) {
			zoom = 1.0;
			size = Math.random() * 0.25;
		} else {
			zoom = Math.random() * 10.0 + 0.5;
			size = 0.1;
		}
		zoom = Math.random() * 9.0 + 1.0;
		invert = (int) (Math.random() * 2);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		  float x,y;  "
	    		+"		  if( __cut_x_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT()-0.50;"
	    		+"		     y=RANDFLOAT()-0.50;		     "
	    		+"		    }"
	    		+"			float2 uv = make_float2 (x,y);"
	    		+"			uv=uv* __cut_x_zoom ;"
	    		+"			"
	    		+"		    float2 st = abs(uv);"
	    		+"		    float color = 0.0f;"
	    		+"		    "
	    		+"		    float line = 0.0f;	 "
	    		+"		    float movement = 0.0f;"
	    		+"		    movement =  __cut_x_size ;"
	    		+"          Mat2 mat;"
	    		+"          mat=cut_x_rot(PI/4.0f);"
	    		+"		    st = times(&mat,st);"
	    		+"		    st = abs(st);"
	    		+"		    st.y-= movement;"
	    		+"		    line = smoothstep(0.0,0.009, st.y) ;"
	    		+"		    color = mix(color,1.,line);    "
	    		+"		     "
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_x_invert ==0)"
	    		+"		    {"
	    		+"		      if (color<0.1)"
	    		+"		      { x=0.;"
	    		+"		        y=0.;"
	    		+"		        __doHide = true;"
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color>=0.1 )"
	    		+"			      { x=0.;"
	    		+"			        y=0.;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __cut_x * x;"
	    		+"		    __py = __cut_x * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_x * __z;\n" : "");
	  }
	 
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return  "__device__ Mat2  cut_x_rot (float a)"
	    		+"{"
	    		+ "  Mat2 m;"
	    		+"   Mat2_Init (&m, cosf(a), -sinf(a), sinf(a), cosf(a));"
	    		+"   return m;"
	    		+"}";
	  }
}


