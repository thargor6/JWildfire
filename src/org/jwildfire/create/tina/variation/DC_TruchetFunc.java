package org.jwildfire.create.tina.variation;


import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;

public class DC_TruchetFunc  extends DC_BaseFunc implements SupportsGPU {

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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_truchet_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_truchet_zoom;"
	    		+"color=dc_truchet_getRGBColor(uv,__dc_truchet_type);"
	    		+"if( __dc_truchet_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_truchet_Gradient ==1 )"
	    		+"{"
	    		+"float4 pal_color=make_float4(color.x,color.y,color.z,1.0);"
	    		+"float4 simcol=pal_color;"
	    		+"float diff=1000000000.0f;"
////read palette colors to find the nearest color to pixel color
	    		+" for(int index=0; index<numColors;index++)"
             +" {      pal_color = read_imageStepMode(palette, numColors, (float)index/(float)numColors);"
	    		+"        float3 pal_color3=make_float3(pal_color.x,pal_color.y,pal_color.z);"
             // implement:  float distance(float,float,float,float,float,float) in GPU function
	        	+"    float dvalue= distance_color(color.x,color.y,color.z,pal_color.x,pal_color.y,pal_color.z);"
	        	+ "   if (diff >dvalue) "
	        	+ "    {" 
	        	+"	     diff = dvalue;" 
	        	+"       simcol=pal_color;" 
	        	+"	   }"
             +" }"
////use nearest palette color as the pixel color                
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = simcol.x;"
	    		+"   __colorG  = simcol.y;"
	    		+"   __colorB  = simcol.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_truchet_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_truchet*x;"
	    		+"__py+= __dc_truchet*y;"
	    		+"float dz = z * __dc_truchet_scale_z + __dc_truchet_offset_z;"
	    		+"if ( __dc_truchet_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {

		 return   "	__device__   float2  dc_truchet_truchetPattern ( float2 st,  float index)"
				 +"	{"
				 +"		index = fract((index-0.5)*2.0);"
				 +"		if (index > 0.75) {"
				 +"			st = make_float2(1.0,1.0)-st;"
				 +"		} else if (index > 0.5) {"
				 +"			st = make_float2(1.-st.x,st.y);"
				 +"		} else if (index > 0.25) {"
				 +"			st = make_float2(1.0,1.0) - make_float2(1.0-st.x,st.y);"
				 +"		}"
				 +"		return st;"
				 +"	}"
				 
				 +"__device__  float  dc_truchet_random (float2 st)"
				 +"	{"
				 +"		return fract(sinf(dot(make_float2(st.x,st.y),make_float2(12.9898,78.233)))*43758.5453123);"
				 +"	}"
				 
				 +"__device__ float3  dc_truchet_getRGBColor (float2 st , float type)"
				 +"{"
				 +"    float2 ipos = floorf(st);"
				 +"    float2 fpos = fract(st);"
				 +"    float index=dc_truchet_random(ipos);"
				 +"    float2 tile = dc_truchet_truchetPattern(fpos, index);"
				 +"    float icolor = 0.0;"
				 +"    if(type==0.0)"
				 +"    {"
				 +"       icolor = smoothstep(tile.x-0.3,tile.x,tile.y)-"
				 +"	               smoothstep(tile.x,tile.x+0.3,tile.y);"
				 +"	   } else if(type==1.0)"
				 +"	   { "
				 +"	      icolor = (step(length(tile),0.6) -"
				 +"	                step(length(tile),0.4) ) +"
				 +"	               (step(length(tile-(make_float2(1.,1.0))),0.6) -"
				 +"	                step(length( tile-make_float2(1.,1.0) ),0.4) );"
				 +"	   } else if(type==2.0)"
				 +"	   {"
				 +"	      icolor = step(tile.x,tile.y);"
				 +"	   }"
				 +"	   return make_float3(icolor,icolor,icolor);"
				 +"}";
	 }	
}

