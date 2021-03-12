package org.jwildfire.create.tina.variation;


import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;


public class DC_TruchetFunc  extends DC_BaseFunc {

	/*
	 * Variation :dc_truchet
	 * Date: February 12, 2019
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	
	private static final String PARAM_TYPE = "type";
	private static final String PARAM_ZOOM = "zoom";


	

	int type=0;
    double zoom=10.0;


	private static final String[] additionalParamNames = { PARAM_TYPE,PARAM_ZOOM};


		
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
	
	  public vec3 getRGBColor(double x,double y)
	  {

	      vec2 st=new vec2(x,y);
	          
	      st =st.multiply( zoom);
	      

	      vec2 ipos = G.floor(st);  // integer
	      vec2 fpos = G.fract(st);  // fraction

	      vec2 tile = G.truchetPattern(fpos, G.random( ipos ));

	      double icolor = 0.0;

	      // Maze
	      if(type==0)
	      { 
	        icolor = G.smoothstep(tile.x-0.3,tile.x,tile.y)-
	              G.smoothstep(tile.x,tile.x+0.3,tile.y);
	      } else if(type==1)
	      // Circles
	      { 
	    	  icolor = (G.step(G.length(tile),0.6) -
	                G.step(G.length(tile),0.4) ) +
	               (G.step(G.length(tile.minus(new vec2(1.))),0.6) -
	                G.step(G.length(tile.minus(new vec2(1.))),0.4) );
	      } else if(type==2)
	      // Truchet (2 triangles)
	      {
	    	  icolor = G.step(tile.x,tile.y);
	      }
	      return new vec3(icolor);
	  }
	 	
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {


	   }

	  
	public String getName() {
		return "dc_truchet";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //
		return joinArrays(new Object[] {  type,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_TYPE)) {
			type = (int)Tools.limitValue(pValue, 0 , 2);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
		}
		else
			super.setParameter(pName, pValue);
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE};
	}

}

