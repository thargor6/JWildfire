package org.jwildfire.create.tina.variation;


import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
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



public class DC_FractColorFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation :dc_fractcolor
	 * Date: august 29, 2019
	 * Author: Jesus Sosa
	 * Reference & Credits:
	 *   http://glslsandbox.com/e#29611.0 
	 *   https://www.shadertoy.com/view/MsK3zG
	 */


	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED= "randomize";
	private static final String PARAM_TIME= "time";
	private static final String PARAM_XPAR = "xpar";
	private static final String PARAM_YPAR = "ypar";
	private static final String PARAM_ITERS= "iters";
	private static final String PARAM_ZOOM = "zoom";

 	
 	LocalTime now ;

 	int seed=0;
    double time=0;
 	double x_par=0.5,y_par=0.5;
    double zoom=1.0;
    int iters=88;
    
	Random randomize=new Random(seed);
	
 	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_XPAR,PARAM_YPAR,PARAM_ITERS,PARAM_ZOOM};

	
	public vec3 getRGBColor(double xp,double yp)
	{
	    vec2 U =new vec2(xp*zoom,yp*zoom);
        	    
	    vec2 m=new vec2(x_par,y_par);  // red & green
	    double blue=1.5-m.x+.04*sin(time);  // blue
	    
	    vec3 O = new vec3(U ,blue );  // start color
	    
	    for (int i = 0; i < iters; i++)
	    {
	       vec3 tmp=  G.abs(new vec3(O.x,O.y,O.z).division(G.dot(O,O)).minus(new vec3(1,1,m.y*.3))).multiply(new vec3(1.3, 1, .777));
	       O.x=tmp.x;
	       O.y=tmp.z;
	       O.z=tmp.y;
	    }         
        return new vec3(O.x,O.y,O.z);
	}
	
	    
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
	   }

	  
	public String getName() {
		return "dc_fractcolor";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //
		return joinArrays(new Object[] { seed,time,x_par,y_par,iters,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SEED)) {
			   seed=(int)pValue;
			   randomize=new Random(seed);
		       LocalTime now = LocalTime.now(ZoneId.systemDefault());
		       time=(double)now.toSecondOfDay();
		       x_par=randomize.nextDouble();
		       y_par=randomize.nextDouble();
		       
		}
	    else if (pName.equalsIgnoreCase(PARAM_TIME)) {
            time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_XPAR)) {
			x_par =Tools.limitValue(pValue, 0. , 1.);
		}
		else if (pName.equalsIgnoreCase(PARAM_YPAR)) {
			y_par =Tools.limitValue(pValue, 0. , 1.);
		}
		else if (pName.equalsIgnoreCase(PARAM_ITERS)) {
			iters =(int)pValue;
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
	public String getGPUCode(FlameTransformationContext context) {
		   return   "float x,y;"
		    		+"float3 color=make_float3(1.0,1.0,0.0);"
		    		+"float z=0.5;"
		    		+"if( varpar->dc_fractcolor_ColorOnly ==1)"
		    		+"{"
		    		+"  x=__x;"
		    		+"  y=__y;"
		    		+"}"
		    		+"else"
		    		+"{"
		    		+"  x=2.0*RANDFLOAT()-1.0;"
		    		+"  y=2.0*RANDFLOAT()-1.0;"
		    		+"}"
		    		+"float2 uv=make_float2(x,y)*varpar->dc_fractcolor_zoom;"
		    		+"color=dc_fractcolor_getRGBColor(uv,varpar->dc_fractcolor_xpar,varpar->dc_fractcolor_ypar,varpar->dc_fractcolor_time,varpar->dc_fractcolor_iters);"
		    		+"if( varpar->dc_fractcolor_Gradient ==0 )"
		    		+"{"
		    		+"   __useRgb  = true;"
		    		+"   __colorR  = color.x;"
		    		+"   __colorG  = color.y;"
		    		+"   __colorB  = color.z;"
		    		+"   __colorA  = 1.0;"
		    		+"}"
		    		+"else if( varpar->dc_fractcolor_Gradient ==1 )"  
		    		+"{"
		    		+"float4 pal_color=make_float4(color.x,color.y,color.z,1.0);"
		    		+"float4 simcol=pal_color;"
		    		+"float diff=1000000000.0f;"

		    		+" for(int index=0; index<numColors;index++)"
	                +" {      pal_color = read_imageStepMode(palette, numColors, (float)index/(float)numColors);"
		    		+"        float3 pal_color3=make_float3(pal_color.x,pal_color.y,pal_color.z);"
	                
		        	+"    float dvalue= distance_color(color.x,color.y,color.z,pal_color.x,pal_color.y,pal_color.z);"
		        	+ "   if (diff >dvalue) "
		        	+ "    {" 
		        	+"	     diff = dvalue;" 
		        	+"       simcol=pal_color;" 
		        	+"	   }"
	                +" }"

		    		+"   __useRgb  = true;"
		    		+"   __colorR  = simcol.x;"
		    		+"   __colorG  = simcol.y;"
		    		+"   __colorB  = simcol.z;"
		    		+"   __colorA  = 1.0;"
		    		+"}"
		    		+"else if( varpar->dc_fractcolor_Gradient ==2 )"
		    		+"{"
		    		+"  int3 icolor=dbl2int(color);"
		    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
		    		+"  __pal=z;"
		    		+"}"
		    		+"__px+= varpar->dc_fractcolor*x;"
		    		+"__py+= varpar->dc_fractcolor*y;"
		    		+"float dz = z * varpar->dc_fractcolor_scale_z + varpar->dc_fractcolor_offset_z;"
		    		+"if ( varpar->dc_fractcolor_reset_z  == 1) {"
		    		+"     __pz = dz;"
		    		+"}"
		    		+"else {"
		    		+"   __pz += dz;"
		    		+"}";
		}
	
		 public String getGPUFunctions(FlameTransformationContext context) {
			 return   "	__device__ float3  dc_fractcolor_getRGBColor (float2 uv, float xpar, float ypar, float time,float iters)"
					 +"	{"
					 +"		float2 m=make_float2(xpar,ypar);"
					 +"		float blue=1.5*m.x + 0.04*sinf(time);"
					 +"     float3 col=make_float3(uv.x,uv.y,blue);"	
					 +"      for(int i=0;i<(int)iters;i++)"
					 +"      {"
					 +"         float3 tmp=abs(make_float3(col.x,col.y,col.z)/(dot(col,col))-(make_float3(1.0,1.0,m.y*.3)))*(make_float3(1.3, 1.0, 0.777));"
					 +"         col.x=tmp.x;"
					 +"         col.y=tmp.z;"
					 +"         col.z=tmp.y;"
					 + "     }"
					 +"		return col;"
					 +"	}";
		 }	
}

