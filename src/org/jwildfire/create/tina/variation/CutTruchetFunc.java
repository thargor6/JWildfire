package org.jwildfire.create.tina.variation;


import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.vec2;



public class CutTruchetFunc  extends VariationFunc {

	/*
	 * Variation :cut_truchet
	 * Date: February 12, 2019
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	
	private static final String PARAM_TYPE = "type";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	

	int type=0;
	int seed=1000;
    double zoom=10.0;
    int invert=0;
    double time=0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    
	private static final String[] additionalParamNames = { PARAM_TYPE,PARAM_SEED,PARAM_ZOOM,PARAM_INVERT};

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
		    double x = pAffineTP.x;
		    double y = pAffineTP.y;
		    
		    
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
		return (new Object[] {  type,seed, zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_TYPE)) {
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
	
}

