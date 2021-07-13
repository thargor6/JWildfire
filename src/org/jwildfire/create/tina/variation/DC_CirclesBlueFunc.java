package org.jwildfire.create.tina.variation;

import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.render.FlameRenderer;

import js.glsl.G;
import js.glsl.mat2;
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class DC_CirclesBlueFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_circlesblue
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_RADIUS = "radius";
	private static final String PARAM_BUBLES = "bubles";





	private int seed = 10000;
	double time=1.0;
	double zoom=1.0;
	double fRadius = 0.04;
	int bubles = 40;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	



	private static final String[] additionalParamNames = { 
			
			PARAM_SEED,PARAM_TIME,PARAM_ZOOM,PARAM_RADIUS,PARAM_BUBLES};



		
	public vec3 getRGBColor(double xp,double yp)
	{

		
		// Half the width and half the height gives the position of the center of the screen
		
		vec2 uv = new vec2( xp,yp).multiply(zoom);

		    
		vec3 color = new vec3(0.0);

		    // bubbles
		    for (int i=0; i < bubles; i++ ) {
		            // bubble seeds
		        double pha = Math.tan((double)i*6.+1.0)*0.5 + 0.5;
		        double siz = G.pow( G.cos((double)i*2.4+5.0)*0.5 + 0.5, 4.0 );
		        double pox = G.cos((double)i*3.55+4.1);
		        
		            // buble size, position and color
		        double rad = fRadius + G.sin((double)i)*0.12+0.08;
		        
		        vec2  pos = new vec2( pox+G.sin(time/15.+pha+siz), -1.0-rad + (2.0+2.0*rad)
		                         *G.mod(pha+0.1*(time/5.)*(0.2+0.8*siz),1.0));
		        
		        
		        double dis = G.length( uv.minus( pos) );
		        vec3  col = G.mix( new vec3(0.1, 0.2, 0.8), new vec3(0.2,0.8,0.6), 0.5+0.5*G.sin((double)i*G.sin(time*pox*0.03)+1.9));
		        
		            // render
		       
		        color = color.add(col.multiply(1.- G.smoothstep( rad*(0.65+0.20*G.sin(pox*time)), rad, dis )).multiply( (1.0 - G.cos(pox*time))));
		    }

		    return color.multiply(0.3);
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
		return "dc_circlesblue";
	}

	public String[] getParameterNames() {
	    return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed, time,zoom,fRadius,bubles},super.getParameterValues());
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
			zoom = Tools.limitValue(pValue, 0.01 , 100.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_RADIUS)) {
			fRadius =Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_BUBLES)) {
			bubles=(int)Tools.limitValue(pValue, 1 , 100);
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
	    		+"if( varpar->dc_circlesblue_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*varpar->dc_circlesblue_zoom;"
	    		+"color=dc_circlesblue_getRGBColor(uv,varpar->dc_circlesblue_time,varpar->dc_circlesblue_bubles,varpar->dc_circlesblue_radius);"
	    		+"if( varpar->dc_circlesblue_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( varpar->dc_circlesblue_Gradient ==1 )"  
	    		+"{"
	    		+"float4 pal_color=make_float4(color.x,color.y,color.z,1.0);"
	    		+"float4 simcol=pal_color;"
	    		+"float diff=1000000000.0f;"
//// read palette colors to find the nearest color to pixel color
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
//// use nearest palette color as the pixel color                
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = simcol.x;"
	    		+"   __colorG  = simcol.y;"
	    		+"   __colorB  = simcol.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( varpar->dc_circlesblue_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= varpar->dc_circlesblue*x;"
	    		+"__py+= varpar->dc_circlesblue*y;"
	    		+"float dz = z * varpar->dc_circlesblue_scale_z + varpar->dc_circlesblue_offset_z;"
	    		+"if ( varpar->dc_circlesblue_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	}
	  public String getGPUFunctions(FlameTransformationContext context) {
		    return   "__device__ float3 dc_circlesblue_getRGBColor (float2 uv, float time,float bubbles, float radius)"
		    		+"{"
		    		+"      float3 color=make_float3(0.0f,0.0f,0.0f);"
					+"	    for (float i=0;i<bubbles;i++)"
					+"	    {"
					+"		   float pha = tan(i*6.+1.0)*0.5 + 0.5;"  
					+"		   float siz = pow( cosf(i*2.4+5.0)*0.5 + 0.5, 4.0 );"  
					+"		   float pox = cosf(i*3.55+4.1);"
					+"		   float rad = radius + sinf(i)*0.12+0.08;" 
					+"		   float2 pos = make_float2( pox+sinf(time/15.+pha+siz),"
					+ "                                  -1.0-rad + (2.0+2.0*rad)*mod(pha+0.1*(time/5.)*(0.2+0.8*siz),1.0));" 
					+"     	   float dis = length( uv- pos );" 
					+"		   float3  col = mix( make_float3(0.1, 0.2, 0.8), make_float3(0.2,0.8,0.6), 0.5+0.5*sinf(i*sinf(time*pox*0.03)+1.9));" 
					+"		   color = color + (col*(1.- smoothstep( rad*(0.65+0.20*sinf(pox*time)), rad, dis ))*( (1.0 - cosf(pox*time))));"				
	   				+"      }"  
	   				+"		return color*0.3;"
		    		+"}";
		  }
}

