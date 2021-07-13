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
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class DC_KaliSet2Func  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_kaliset2
	 * Date: February 12, 2019
	 * Jesus Sosa
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_N = "N";
	private static final String PARAM_R2 = "radio";
	private static final String PARAM_SHIFT_X = "shiftX";
	private static final String PARAM_SHIFT_Y = "shiftY";
	private static final String PARAM_FR = "redF";
	private static final String PARAM_FG = "greenF";
	private static final String PARAM_FB = "blueF";






	private int seed = 5000;
	double time=85.5;
	double zoom=20.0;
	int N=100;
	double rad2=1.0;
    double shiftx=-0.356,shifty=0.686;
    double FR=1.8,FG=1.9,FB=2.2;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	



	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_ZOOM,PARAM_N,PARAM_R2,PARAM_SHIFT_X,PARAM_SHIFT_Y,PARAM_FR,PARAM_FG,PARAM_FB};

	
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
			double PI2 = 6.2831853070;
			
			double angle = PI2*shiftx;
			
			double C = G.cos(angle);
			double S = G.sin(angle);
			
			vec2 shift = new vec2(0.0, 2.0+2.*G.sin(0.03*time));
			double zoom = 2.0 + 5.0*shifty;

			
			for ( int i = 0; i < N; i++ ){
				double rr = v.x*v.x+v.y*v.y;
				if ( rr > rad2 ){
					rr = rad2/rr;
					v.x = v.x * rr;
					v.y = v.y * rr;
				}
				rsum=G.max(rsum, rr);
				
				v = new vec2( C*v.x-S*v.y, S*v.x+C*v.y ).multiply(zoom).plus(shift);
			}
			
			double col = rsum*rsum * (500.0 / (double) N / rad2);
			return new vec3( G.cos(col*FR), G.cos(col*FG), G.cos(col*FB));
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
		return "dc_kaliset2";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed, time,zoom,N,rad2,shiftx,shifty,FR,FG,FB},super.getParameterValues());
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
			zoom = Tools.limitValue(pValue, 1.0 , 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_N)) {
			N =(int)Tools.limitValue(pValue, 1 , 100);
		}
		else if (pName.equalsIgnoreCase(PARAM_R2)) {
			rad2 =Tools.limitValue(pValue, 0.1 , 10.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_SHIFT_X)) {
			shiftx =Tools.limitValue(pValue, -1.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_SHIFT_Y)) {
			shifty =Tools.limitValue(pValue, -1.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_FR)) {
			FR =Tools.limitValue(pValue, 0.0 , 5.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_FG)) {
			FG =Tools.limitValue(pValue, 0.0 , 5.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_FB)) {
			FB =Tools.limitValue(pValue, 0.0 , 5.0);
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
	    		+"if( varpar->dc_kaliset2_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*varpar->dc_kaliset2_zoom;"
	    		+"color=dc_kaliset2_getRGBColor(uv,varpar->dc_kaliset2_time,varpar->dc_kaliset2_N,"
	    		+"                                     varpar->dc_kaliset2_radio, varpar->dc_kaliset2_shiftX,"
	    		+ "                                    varpar->dc_kaliset2_shiftY,varpar->dc_kaliset2_redF, "
	    		+ "                                    varpar->dc_kaliset2_greenF,varpar->dc_kaliset2_blueF);"
	    		+"if( varpar->dc_kaliset2_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( varpar->dc_kaliset2_Gradient ==1 )"  
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
	    		+"else if( varpar->dc_kaliset2_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= varpar->dc_kaliset2*x;"
	    		+"__py+= varpar->dc_kaliset2*y;"
	    		+"float dz = z * varpar->dc_kaliset2_scale_z + varpar->dc_kaliset2_offset_z;"
	    		+"if ( varpar->dc_kaliset2_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {

		 return   "	__device__ float3  dc_kaliset2_getRGBColor (float2 v, float time,float N, float rad2,  float shiftx, float shifty, float FR, float FG, float FB)"
				 +"	{"		
				 +"			float rsum = 0.0;"
				 +"			float PI2 = 2.0*PI;"
				 +"			"
				 +"			float angle = PI2*shiftx;"
				 +"			"
				 +"			float C = cosf(angle);"
				 +"			float S = sinf(angle);"
				 +"			"
				 +"			float2 shift = make_float2(0.0, 2.0+2.*sinf(0.03*time));"
				 +"			float zoom = 2.0 + 5.0*shifty;"
				 +"			"
				 +"			for ( int i = 0; i < N; i++ )"
				 + "        {"
				 +"				float rr = v.x*v.x+v.y*v.y;"
				 +"				if ( rr > rad2 )"
				 + "            {"
				 +"					rr = rad2/rr;"
				 +"					v.x = v.x * rr;"
				 +"					v.y = v.y * rr;"
				 +"				}"
				 +"				rsum=fmaxf(rsum, rr);"
				 +"				v = make_float2( C*v.x-S*v.y, S*v.x+C*v.y )*zoom + shift;"
				 +"			}"
				 +"			float col = rsum*rsum * (500.0 /  N / rad2);"
				 +"			return make_float3( cosf(col*FR), cosf(col*FG), cosf(col*FB));"
				 +"		}";
	 }
}

