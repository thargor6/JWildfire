package org.jwildfire.create.tina.variation;


import java.util.Random;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CutBooleansFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :cut_booleans
	 * Date: august 29, 2019
	 * Reference & Credits:  https://www.shadertoy.com/view/4ldcW8
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_TYPE = "type";


	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	
	int seed=1000;
	int mode=1;
	
	int type=0;

	
    double zoom=250.0;
    int invert=0;


	Random randomize=new Random(seed);
	
 	
    double x0=0.,y0=0.;
    double temp;
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_TYPE,PARAM_ZOOM,PARAM_INVERT};
	
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
            
            if(type==0)   //XOR
              temp=((int)u.x )^((int)u.y);            
            else if(type==1)  // AND
            	temp=((int)u.x )&((int)u.y);
            else if(type==2)  // OR
            	temp=((int)u.x )|((int)u.y);
            
            double color=Math.sin(temp);
              	
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
		return "cut_booleans";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed, mode, type,zoom,invert});
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
		else if (pName.equalsIgnoreCase(PARAM_TYPE)) {
			type = (int)Tools.limitValue(pValue, 0 , 2);
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
		zoom = Math.random() * 450.0 + 50.0;
		invert = (int) (Math.random() * 2);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return  "	    float x,y,px_center,py_center,temp;"
	    		+"		    "
	    		+"		    if( __cut_booleans_mode ==0)"
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
	    		+"		    float2 u =make_float2(x,y );"
	    		+"          u=u*__cut_booleans_zoom;"
	    		+"          if( __cut_booleans_type ==0)   "
	    		+"             temp=((int)u.x )^((int)u.y);            "
	    		+"          else if( __cut_booleans_type ==1)  "
	    		+"             temp=((int)u.x )&((int)u.y);"
	    		+"          else if( __cut_booleans_type ==2)  "
	    		+"             temp=((int)u.x )|((int)u.y);"
	    		+"            "
	    		+"            float color=sin(temp);"
	    		+"              	"
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_booleans_invert ==0)"
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
	    		+"		    __px = __cut_booleans * (x-px_center);"
	    		+"		    __py = __cut_booleans * (y-py_center);"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_booleans * __z;\n" : "");
	  }
}

