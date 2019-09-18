package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.M_2PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

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
import js.glsl.vec3;
import js.glsl.vec4;



public class  CutMetaBallsFunc  extends VariationFunc  {

	/*
	 * Variation : cut_metaballs
	 * Autor: Jesus Sosa
	 * Date: August 20, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	private static final String PARAM_TIME = "time";
	


	double zoom=7.0;
	private int invert = 0;
	double time=0.0;
	

	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_INVERT,PARAM_TIME};



    vec2 random2( vec2 p ) {
      return G.fract(G.sin(new vec2(G.dot(p,new vec2(127.1,311.7)),G.dot(p,new vec2(269.5,183.3)))).multiply(43758.5453));
    }
	
	   
	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    double x = pAffineTP.x;
		    double y = pAffineTP.y;
  
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
		    pVarTP.x = pAmount * x;
		    pVarTP.y = pAmount * y;
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
		return new Object[] { zoom,invert,time};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
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
	
}


