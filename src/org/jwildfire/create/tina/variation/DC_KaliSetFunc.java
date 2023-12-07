package org.jwildfire.create.tina.variation;

import java.util.Random;
import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

public class DC_KaliSetFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_kaliset
	 * Date: February 13, 2019
	 * Jesus Sosa
	 * 	Credits:	
	 * inspired by http://www.fractalforums.com/new-theories-and-research/very-simple-formula-for-fractal-patterns/
	 * a slight(?) different public domain
	 */




	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "Seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_N = "N";

	private static final String PARAM_SHIFT_X = "shiftX";
	private static final String PARAM_SHIFT_Y = "shiftY";






	private int seed = 10000;
	double time=200.;
	double zoom=0.5;
	int N=60;

    double shiftx=-0.22,shifty=-0.21;

	


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	



	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_ZOOM,PARAM_N,PARAM_SHIFT_X,PARAM_SHIFT_Y};



		
		public vec3 getRGBColor(double xp,double yp)
	{

		
		// Half the width and half the height gives the position of the center of the screen
		
		vec2 v = new vec2( xp, yp).multiply(zoom);

		/*
		 * inspired by http://www.fractalforums.com/new-theories-and-research/very-simple-formula-for-fractal-patterns/
		 * a slight(?) different 
		 * public domain
		 */


			double rsum = 0.0;
			double pi2 = 3.6 * 2.0;
			double C = G.cos(time/603. * pi2);
			double S = G.sin(time/407.4* pi2);
			vec2 shift = new vec2(shiftx,shifty);
			double zoom = (time/184.0 + 1.0);
			
			for ( int i = 0; i < N; i++ ){
				double rr = v.x*v.x+v.y*v.y;
				if ( rr > 1.0 ){
					rr = 1.0/rr;
					v.x = v.x * rr;
					v.y = v.y * rr;
				}
				rsum *= 0.99;
				rsum += rr;
				
				v = new vec2( C*v.x-S*v.y, S*v.x+C*v.y ).multiply(zoom).plus(shift);
			}
			
			double col = rsum * 0.5;
			return new vec3( G.cos(col*1.0), G.cos(col*2.0), G.cos(col*4.0));
		}

 	
		public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) 
		{

	        vec3 color=new vec3(0.0); 
			 vec2 uV=new vec2(0.),p=new vec2(0.);
		       int[] tcolor=new int[3];  


			 
		     if(colorOnly==1)
			 {
				 uV.x=pAffineTP.x;
				 uV.y=pAffineTP.y;
			 }
			 else
			 {
		   			 uV.x=2.0*pContext.random()-1.0;
					 uV.y=2.0*pContext.random()-1.0;
			}
	        
	        color=getRGBColor(uV.x,uV.y);
	        tcolor=dbl2int(color);
	        
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

	        pVarTP.x+= pAmount*(uV.x);
	        pVarTP.y+= pAmount*(uV.y);
	        
	        
		    double dz = z * scale_z + offset_z;
		    if (reset_z == 1) {
		      pVarTP.z = dz;
		    }
		    else {
		      pVarTP.z += dz;
		    }
		}
	

	public String getName() {
		return "dc_kaliset";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed, time,zoom,N,shiftx,shifty},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		 if (PARAM_SEED.equalsIgnoreCase(pName)) 
		{	   seed =  (int) pValue;
	       randomize=new Random(seed);
	          long current_time = System.currentTimeMillis();
	          elapsed_time += (current_time - last_time);
	          last_time = current_time;
	          time = (double) (elapsed_time / 1000.0);
	    }
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = Tools.limitValue(pValue, 1.0 , 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.01 , 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_N)) {
			N =(int)Tools.limitValue(pValue, 1 , 100);
		}
		else if (pName.equalsIgnoreCase(PARAM_SHIFT_X)) {
			shiftx =Tools.limitValue(pValue, -5.0 , 5.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_SHIFT_Y)) {
			shifty =Tools.limitValue(pValue, -5.0 , 5.0);
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTS_BACKGROUND};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_kaliset_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_kaliset_zoom;"
	    		+"color=dc_kaliset_getRGBColor(uv,__dc_kaliset_time,__dc_kaliset_N,"
                +"                                __dc_kaliset_shiftX,__dc_kaliset_shiftY);"
	    		+"if( __dc_kaliset_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_kaliset_Gradient ==1 )"  
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
	    		+"else if( __dc_kaliset_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_kaliset*x;"
	    		+"__py+= __dc_kaliset*y;"
	    		+"float dz = z * __dc_kaliset_scale_z + __dc_kaliset_offset_z;"
	    		+"if ( __dc_kaliset_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {

		 return   "	__device__ float3  dc_kaliset_getRGBColor (float2 v,float time, float N, float shiftx, float shifty)"
				 +"	{"
				 +"			float rsum = 0.0;"
				 +"			float pi2 = 3.6 * 2.0;"
				 +"			float C = cosf(time/603. * pi2);"
				 +"			float S = sinf(time/407.4* pi2);"
				 +"			float2 shift = make_float2(shiftx,shifty);"
				 +"			float zoom = (time/184.0 + 1.0);"
				 +"			"
				 +"			for ( int i = 0; i < N; i++ ){"
				 +"				float rr = v.x*v.x+v.y*v.y;"
				 +"				if ( rr > 1.0 ){"
				 +"					rr = 1.0/rr;"
				 +"					v.x = v.x * rr;"
				 +"					v.y = v.y * rr;"
				 +"				}"
				 +"				rsum *= 0.99;"
				 +"				rsum += rr;"
				 +"				"
				 +"				v = make_float2( C*v.x-S*v.y, S*v.x+C*v.y )*(zoom)+(shift);"
				 +"			}"
				 +"			"
				 +"			float col = rsum * 0.5;"
				 +"			return make_float3( cosf(col*1.0), cosf(col*2.0), cosf(col*4.0));"
				 +"		}";
	 }
}

