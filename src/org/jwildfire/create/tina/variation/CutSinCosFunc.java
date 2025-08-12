package org.jwildfire.create.tina.variation;


import java.util.Random;
import js.glsl.G;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CutSinCosFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :cut_sincos
	 * Date: august 29, 2019
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";
	private static final String PARAM_MODE = "mode";	
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	
	int mode=1;
	double time=0.9;
	int seed=1000;
    double zoom=0.50;
    int invert=0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_TIME,PARAM_ZOOM,PARAM_INVERT};


	 	
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
		    
		     vec2 uv= new vec2(x*zoom,y*zoom);

		     uv.x+=1.5;  
		     uv.y += Math.cos(uv.x*10.0)*0.15;
		     uv.y*=1.5;
		     double r = 7.3,

		     e = uv.x-time*0.5,
		     t = e + Math.abs(uv.y),

		    f = (1.0+Math.sin(uv.x*3.0))*0.4+0.1, 
		    d = f - Math.abs(G.fract(t*r) - .5)*2.,
		    s = G.smoothstep(0., .05/G.max(d + .5, 0.), d + .1); 
            double color=Math.sqrt(s);
            
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
		return "cut_sincos";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed, mode, time,zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
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
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
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
		setParameter(PARAM_SEED, (int) (Math.random() * 1000000));
		zoom = Math.random() * 5.0 + 0.1;
		invert = (int) (Math.random() * 2);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}

	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return "float x,y;\n"  
	  		  +"if(__cut_sincos_mode==0)\n"
			  +"{\n"
			  +"  x= __x;\n"
			  +"  y =__y;\n"
			  +"}else\n"
			  +"{\n"
			  +" x=RANDFLOAT()-0.5;\n"
			  +" y=RANDFLOAT()-0.5;\n"
			  +"}\n"
			  +"float2 uv= make_float2(x*__cut_sincos_zoom,y*__cut_sincos_zoom);\n"
		      +"uv.x+=1.5;\n"
			  +"uv.y += cos(uv.x*10.0)*0.15;\n"
			  +"uv.y*=1.5;\n"
			  +"float r = 7.3,\n"
			  +"e = uv.x-__cut_sincos_time*0.5,\n"
			  +"t = e + abs(uv.y),\n"
	  		  +"f = (1.0+sin(uv.x*3.0))*0.4+0.1,\n"
			  +"d = f - abs(fract(t*r) - .5)*2.,\n"
			  +"s = smoothstep(0., .05/max(d + .5, 0.), d + .1);\n"
	          +"  float color=sqrt(s);\n"
	            
			  +"  __doHide=false;\n"
			  +"  if(__cut_sincos_invert==0)\n"
			   +" {\n"
			   +"   if (color>0.0)\n"
			   +"   { x=0;\n"
			   +"     y=0;\n"
			    +"    __doHide = true;\n"
			   +"   }\n"
			   +" } else\n"
			    +"{\n"
				+"      if (color<=0.0)\n"
				+"      { x=0;\n"
				+"        y=0;\n"
			    +"        __doHide = true;\n"
				+"      }\n"
			    +"}\n"
			    +"__px = __cut_sincos * x;\n"
			    +"__py = __cut_sincos * y;\n"
			    + (context.isPreserveZCoordinate() ? "__pz += __cut_sincos * __z;" : "");
	  }
}

