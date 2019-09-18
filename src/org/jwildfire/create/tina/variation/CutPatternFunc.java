package org.jwildfire.create.tina.variation;


import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec4;



public class CutPatternFunc  extends VariationFunc {

	/*
	 * Variation :cut_pattern
	 * Date: august 29, 2019
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	

	double time=0.9;
	int seed=1000;
    double zoom=0.50;
    int invert=0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_ZOOM,PARAM_INVERT};

	vec2 rotate2D (vec2 _st, double _angle) {
	    _st =_st.minus(0.5);
	    _st =  new mat2(G.cos(_angle),-G.sin(_angle),
	                G.sin(_angle),G.cos(_angle)).times(_st);
	    _st =_st.plus( 0.5);
	    return _st;
	}

	vec2 tile (vec2 _st, double _zoom) {
	    _st =_st.multiply(_zoom);
	    return G.fract(_st);
	}

	vec2 rotateTilePattern(vec2 _st){

	    //  Scale the coordinate system by 2x2
	    _st =_st.multiply( 2.0);

	    //  Give each cell an index number
	    //  according to its position
	    double index = 0.0;
	    index += G.step(1., G.mod(_st.x,2.0));
	    index += G.step(1., G.mod(_st.y,2.0))*2.0;

	    //      |
	    //  2   |   3
	    //      |
	    //--------------
	    //      |
	    //  0   |   1
	    //      |

	    // Make each cell between 0.0 - 1.0
	    _st = G.fract(_st);

	    // Rotate each cell according to the index
	    if(index == 0.0){
	    _st = rotate2D(_st,Math.PI*2.*1./4.);
	    }
	    else if(index == 1.0){
	        //  Rotate cell 1 by 90 degrees
	         _st = rotate2D(_st,Math.PI*2.*3./4.);
	         
	    } else if(index == 2.0){
	        //  Rotate cell 2 by -90 degrees
	         _st = rotate2D(_st,Math.PI*2.*1./4.);
	         
	    } else if(index == 3.0){
	        //  Rotate cell 3 by 180 degrees
	        _st = rotate2D(_st,Math.PI*2.*3./4.);
	    }

	    return _st;
	}
	 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    double x = pAffineTP.x;
		    double y = pAffineTP.y;
		    
		     vec2 uv = tile(new vec2(x,y),zoom);
		     uv = rotateTilePattern(uv);
		     
		     
		     vec2 pos = new vec2(0.,5.).minus( uv);
		     double radius = G.length(pos);
		     double angle = G.atan2(pos.x,pos.y);
		     double r = G.sin(radius*G.sin(uv.y*Math.PI*5.+time+G.cos(G.sin(uv.x*Math.PI*3.)*Math.PI*2.+G.sin(uv.y*Math.PI*15.)))
		                   *1.*G.sin(uv.y*Math.PI+G.sin(uv.x*Math.PI*5.)));
		     
		     //double r = G.sin(G.sin(uv.x*Math.PI*5.+));
		     //double g = G.cos(r*Math.PI*2.+Math.PI*2.)*0.9;
		     
		     double b = G.cos(r*Math.PI*2.+Math.PI*2.)*0.9+G.sin(r*Math.PI*2.+Math.PI*2.)*0.7;
            
		     double color=b;
		     
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
		return "cut_pattern";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed, time,zoom,invert});
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
	
}

