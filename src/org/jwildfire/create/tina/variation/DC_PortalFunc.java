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
import js.glsl.vec3;
import js.glsl.vec4;



public class DC_PortalFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation :dc_portal
	 * Date: january 9, 2021
	 * Jesus Sosa
	 * Reference & Credits: https://www.shadertoy.com/view/4tBBRw
	 *                      https://www.shadertoy.com/view/Xt2BzD
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_TIME = "time";
	private static final String PARAM_WAVES = "waves";
	private static final String PARAM_ZOOM = "zoom";

	


	int seed=1000;

	double time=0.0;
	double waves=5.;
	double zoom=15.0;



	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 	
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_WAVES,PARAM_ZOOM};


	public vec3 getRGBColor(double xp,double yp)
	{
		vec2 uv = new vec2( xp, yp);
	    double d = 1.0 ;
	    d+= Math.sqrt(G.length(uv)) / waves;
	    double t = 1000. + time;
	    double value = d * t + (t * 0.125) * Math.cos(uv.x) * Math.cos(uv.y);
	    double color = Math.sin(value) * 3.0;
	    	
	    double low = Math.abs(color);
	    double med = Math.abs(color) - 1.0;
	    double medHigh = Math.abs(color) - 1.5;
	    double high = Math.abs(color) - 2.0;
	    
	    vec3 lifeColor;
	    vec3 metalColor;
	        
	    if(color > 0.) {
	      metalColor= new vec3(med,medHigh,high);	
	      lifeColor = new vec3(high, high, med);
	    } else {
	      metalColor=new vec3(medHigh,medHigh,medHigh);	
	      lifeColor = new vec3(med, high, high);
	    }	    
		return metalColor;
	}
	 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    double x,y;
		    
		     if(colorOnly==1)
			 {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		    } else
		    {
		     x=pContext.random()-0.5;
		     y=pContext.random()-0.5;		     
		    }
		    
		    
		    vec2 uv =new vec2(x*zoom,y*zoom);
		    
	        vec3 color=getRGBColor(uv.x,uv.y);
	        int[] tcolor=dbl2int(color);
	        
	        //z by color (normalized)
	        double z=greyscale(tcolor[0],tcolor[1],tcolor[2]);
		           
	        if(gradient==0)
	        {
	  	  	
	    	  pVarTP.rgbColor  =true;;
	    	  pVarTP.redColor  =tcolor[0];
	    	  pVarTP.greenColor=tcolor[1];
	    	  pVarTP.blueColor =tcolor[2];
	    		
	        }
	        else if(gradient==1)
	        {

	            	Layer layer=pXForm.getOwner();
	            	RGBPalette palette=layer.getPalette();      	  
	          	    RGBColor col=findKey(palette,tcolor[0],tcolor[1],tcolor[2]);
	          	    
	          	  pVarTP.rgbColor  =true;;
	          	  pVarTP.redColor  =col.getRed();
	          	  pVarTP.greenColor=col.getGreen();
	          	  pVarTP.blueColor =col.getBlue();

	        }
	        else 
	        {
	        	pVarTP.color=z;
	        }
	        
		    pVarTP.x = pAmount * (x);
		    pVarTP.y = pAmount * (y);
		    
	        
		    double dz = z * scale_z + offset_z;
		    if (reset_z == 1) {
		      pVarTP.z = dz;
		    }
		    else {
		      pVarTP.z += dz;
		    }
		    
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	   }

	  
	public String getName() {
		return "dc_portal";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //
		return joinArrays (new Object[] {  seed, time,waves, zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if(pName.equalsIgnoreCase(PARAM_SEED))
		{
			   seed =   (int)pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_WAVES)) {
			waves =pValue;
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
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( varpar->dc_portal_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*varpar->dc_portal_zoom;"
	    		+"color=dc_portal_getRGBColor(uv,varpar->dc_portal_time,varpar->dc_portal_waves);"
	    		+"if( varpar->dc_portal_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( varpar->dc_portal_Gradient ==1 )"  
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
	    		+"else if( varpar->dc_portal_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= varpar->dc_portal*x;"
	    		+"__py+= varpar->dc_portal*y;"
	    		+"float dz = z * varpar->dc_portal_scale_z + varpar->dc_portal_offset_z;"
	    		+"if ( varpar->dc_portal_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {
		return   "	__device__ float3  dc_portal_getRGBColor (float2 uv, float time, float waves)"
				+"	{"
				+"	    float d = 1.0 ;"
				+"	    d+= sqrtf(length(uv)) / waves;"
				+"	    float t = 1000. + time;"
				+"	    float value = d * t + (t * 0.125) * cosf(uv.x) * cosf(uv.y);"
				+"	    float color = sinf(value) * 3.0;"
				+"	    	"
				+"	    float low = fabsf(color);"
				+"	    float med = fabsf(color) - 1.0;"
				+"	    float medHigh = fabsf(color) - 1.5;"
				+"	    float high = fabsf(color) - 2.0;"
				+"	    "
				+"	    float3 lifeColor;"
				+"	    float3 metalColor;"
				+"	        "
				+"	    if(color > 0.0) {"
				+"	      metalColor= make_float3(med,medHigh,high);	"
				+"	      lifeColor = make_float3(high, high, med);"
				+"	    } else {"
				+"	      metalColor=make_float3(medHigh,medHigh,medHigh);	"
				+"	      lifeColor = make_float3(med, high, high);"
				+"	   }"
				+"		return metalColor;"
//				+"     return make_float3(1.0,0.0,0.0);"
				+"	}";
	 }	
}

