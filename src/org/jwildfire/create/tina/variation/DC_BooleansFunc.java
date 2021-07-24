package org.jwildfire.create.tina.variation;


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



public class DC_BooleansFunc  extends DC_BaseFunc  implements SupportsGPU {

	/*
	 * Variation :dc_booleans
	 * Date: august 29, 2019
	 * Author: Jesus Sosa
	 * Reference & Credits:
	 *   https://www.shadertoy.com/view/4ldcW8
	 */


	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "randomize";
	private static final String PARAM_TYPE = "type";
	private static final String PARAM_ZOOM = "zoom";

	
	int type=1;

	
	int mode=1;
    double zoom=1000.0;
    int invert=0;
    
    int seed=1000;
	Random randomize=new Random(seed);
 	
    double x0=0.,y0=0.;
    double temp;
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TYPE,PARAM_ZOOM};



	public vec3 hsv2rgb(vec3 c) {
		  vec4 K = new vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
		  vec3 p = G.abs(G.fract(new vec3(c.x,c.x,c.x).plus(new vec3(K.x,K.y,K.z))).multiply(6.0).minus(new vec3(K.w)));
		  return G.mix(new vec3(K.x), G.clamp(p.minus( new vec3(K.x)), 0.0, 1.0), c.y).multiply(c.z);
		}

	 	
	
	public vec3 getRGBColor(double xp,double yp)
	{
		double temp=0.0f;
	    vec2 u =new vec2(xp*zoom,yp*zoom);
        u=u.plus(new vec2(x0,y0));
        
        if(type==0)   //XOR
          temp=((int)u.x )^((int)u.y);            
        else if(type==1)  // AND
        	temp=((int)u.x )&((int)u.y);
        else if(type==2)  // OR
        	temp=((int)u.x )|((int)u.y);
        
        vec3 color=hsv2rgb(new vec3(sin(temp), 1.0, cos(temp)));  
        return color;
	 
	}
	
	    
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
		  x0=seed*randomize.nextDouble();
		  y0=seed*randomize.nextDouble();
	   }

	  
	public String getName() {
		return "dc_booleans";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //
		return joinArrays(new Object[] { seed,type,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SEED)) {
			seed =   (int)pValue;
		    randomize=new Random(seed);
		}
		else if (pName.equalsIgnoreCase(PARAM_TYPE)) {
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return  " "
	    		+"float x,y;\n"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"\n"
	    		+"if( __dc_booleans_ColorOnly ==1)\n"
	    		+"{\n"
	    		+"  x=__x;\n"
	    		+"  y=__y;\n"
	    		+"}\n"
	    		+"else\n"
	    		+"{\n"
	    		+"  x=RANDFLOAT()-0.5;\n"
	    		+"  y=RANDFLOAT()-0.5;\n"
	    		+"}\n"
	    		+"float2 uv=make_float2(x*__dc_booleans_zoom,y*__dc_booleans_zoom);\n"
	    		+"color=dc_booleans_getRGBColor(uv,__dc_booleans_type);\n"
	    		+"if( __dc_booleans_Gradient ==0 )\n"
	    		+"{\n"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}\n"
	    		+"else if( __dc_booleans_Gradient ==1 )"
	    		+"{"
	    		+"float4 pal_color=make_float4(color.x,color.y,color.z,1.0);"
	    		+"float4 simcol=pal_color;"
	    		+"float diff=1000000000.0f;"
//// read palette colors to find the nearest color to pixel color
	    		+" for(int index=0; index<numColors;index++)"
                +" {      pal_color = read_imageStepMode(palette, numColors, (float)index/(float)numColors);"
	    		+"        float3 pal_color3=make_float3(pal_color.x,pal_color.y,pal_color.z);"
//                +"        int3 palcolor=dbl2int(pal_color3);"
                // implement:  float distance(float,float,float,float,float,float) in GPU function
	        	+"    float dvalue= distance_color(color.x,color.y,color.z,pal_color.x,pal_color.y,pal_color.z);"
	        	+ "   if (diff >dvalue) "
	        	+ "    {" 
	        	+"	     diff = dvalue;" 
	        	+"       simcol=pal_color;" 
	        	+"	   }"
                +" }"
//// use nearest palette color as the pixel color                
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = simcol.x;"
	    		+"   __colorG  = simcol.y;"
	    		+"   __colorB  = simcol.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_booleans_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_booleans*(x);\n"
	    		+"__py+= __dc_booleans*(y);\n"
	    		+"float dz = z * __dc_booleans_scale_z + __dc_booleans_offset_z;\n"
	    		+"if ( __dc_booleans_reset_z  == 1) {\n"
	    		+"     __pz = dz;\n"
	    		+"}\n"
	    		+"else {\n"
	    		+"   __pz += dz;\n"
	    		+"}\n";

	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__ float3  dc_booleans_getRGBColor (float2 uv, int type)\n"
	    		+"{\n"
	    		+"  float2 u =uv;\n"
	    		+"  float temp=0.0f;\n"
	    		+"  if(type==0)\n"
	    		+"     temp=((int)u.x )^((int)u.y);\n"
	    		+"  else if(type==1)\n"
	    		+" 	   temp=((int)u.x )&((int)u.y);\n"
	    		+"  else if(type==2)\n"
	    		+" 	   temp=((int)u.x )|((int)u.y);\n"
	    		+"   float3 color= hsv2rgb(make_float3(sin(temp), 1.0f, cos(temp)));\n"
	    		+"   return color;\n"
	    		+"}\n";
	  }
}

