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

public class DC_AcrilicFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_acrilic
	 * Date: February 12, 2019
	 * Jesus Sosa
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_STEPS = "steps";
	private static final String PARAM_P1 = "p1";
	private static final String PARAM_P2 = "p2";
	private static final String PARAM_P3 = "p3";
	private static final String PARAM_P4 = "p4";
	private static final String PARAM_P5 = "p5";
	private static final String PARAM_P6 = "p6";
	private static final String PARAM_FR = "redf";
	private static final String PARAM_FG = "greenf";
	private static final String PARAM_FB = "bluef";


	private int seed = 10000;
	double time=0.0;
	double zoom=0.7;
	int steps=10;
	double p1=12.0; 
	double p2=12.0;  
	double p3=12.0;
	double p4=12.0;
	double p5=75;
	double p6=75;
    double FR=1.0,FG=1.0,FB=1.0;

	
	  
	Random randomize=new Random(seed);

	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;

	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_ZOOM,PARAM_STEPS,PARAM_P1,PARAM_P2,PARAM_P3,PARAM_P4,PARAM_P5,PARAM_P6,
			PARAM_FR,PARAM_FG,PARAM_FB};


	  public double sq(double x) {
			return x*x;
		}

	  
	public vec3 getRGBColor(double xp,double yp)
	{

		vec2 p = new vec2(zoom* xp, zoom*yp);
        vec3 col=new vec3(0.0);
        
	    for(int j = 0; j < 3; j++){
	        for(int i = 1; i < steps; i++){
	            p.x += 0.1 / (i + j) * G.sin(i * p1 * p.y + time + G.cos((time / (p2 * i)) * i + j));
	            p.y += 0.1 / (i + j) * G.cos(i * p3 * p.x + time + G.sin((time / (p4 * i)) * i + j));
	        }
	        if(j==0)
	        	col.x=G.sin(p5*sq(p.x)) + G.sin(p6*sq(p.y));
	        if(j==1)
	        	col.y=G.sin(p5*sq(p.x)) + G.sin(p6*sq(p.y));
	        if(j==2)
	        	col.z = G.sin(p5*sq(p.x)) + G.sin(p6*sq(p.y));
	    }
	    col=new vec3( G.cos(col.x*FR), G.cos(col.y*FG), G.cos(col.z*FB));
		return col;	
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
	   			 uV.x=pContext.random()-0.5;
				 uV.y=pContext.random()-0.5;
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
		return "dc_acrilic";
	}

	@Override
	  public String[] getParameterNames() {
		    return joinArrays(additionalParamNames, paramNames);
		  }
	
	  @Override 
	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed, time,zoom,steps,p1,p2,p3,p4,p5,p6,FR,FG,FB},super.getParameterValues());
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
			time = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_STEPS)) {
			steps =(int)Tools.limitValue(pValue, 3 , 100);
		}
		else if (pName.equalsIgnoreCase(PARAM_P1)) {
			p1 =Tools.limitValue(pValue, 1. , 100.);
		}
		else if (pName.equalsIgnoreCase(PARAM_P2)) {
			p2=Tools.limitValue(pValue, 1. , 100.);
		}
		else if (pName.equalsIgnoreCase(PARAM_P3)) {
			p3=Tools.limitValue(pValue, 1. , 100.);
		}
		else if (pName.equalsIgnoreCase(PARAM_P4)) {
			p4=Tools.limitValue(pValue, 1. , 100.);
		}
		else if (pName.equalsIgnoreCase(PARAM_P5)) {
			p5=Tools.limitValue(pValue, 1. , 100.);
		}
		else if (pName.equalsIgnoreCase(PARAM_P6)) {
			p6= Tools.limitValue(pValue, 1. , 100.);
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTS_BACKGROUND};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_acrilic_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_acrilic_zoom;"
	    		+" color=dc_acrilic_getRGBColor(uv,__dc_acrilic_p1,__dc_acrilic_p2,"
	    		+ "                                   __dc_acrilic_p3,__dc_acrilic_p4,"
	    		+ "                                   __dc_acrilic_p5,__dc_acrilic_p6,"
	    		+ "                                   __dc_acrilic_redf,__dc_acrilic_greenf,"
	    		+ "                                   __dc_acrilic_bluef,"
	    		+ "                                   __dc_acrilic_steps,__dc_acrilic_time);"
	    		+"if( __dc_acrilic_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
				+"   __colorA  = 1.0;"
	    		+"}"
				+"else if( __dc_acrilic_Gradient ==1 )"
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
	    		+"else if( __dc_acrilic_Gradient ==2 )"
	    		+"{"
				+"  int3 icolor=dbl2int(color);"
	    		+"  z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_acrilic*x;"
	    		+"__py+= __dc_acrilic*y;"
	    		+"float dz = z * __dc_acrilic_scale_z + __dc_acrilic_offset_z;"
	    		+"if ( __dc_acrilic_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";

	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__ float  dc_acrilic_sq (float x) {"
	    		+"			return x*x;"
	    		+"}"
	    		+""
	    		+"__device__ float3  dc_acrilic_getRGBColor (float2 p, float p1,float p2, float p3,"
	    		+ "                                          float p4, float p5, float p6,float FR,"
	    		+ "                                          float FG, float FB,float steps,float time)"
	    		+"{"
	    		+"      float3 col=make_float3(0.0f,0.0f,0.0f);"
	    		+"        "
	    		+"	    for(int j = 0; j < 3; j++){"
	    		+"	        for(int i = 1; i < steps; i++){"
	    		+"	            p.x += 0.1 / (i + j) * sinf(i * p1 * p.y + time + cosf((time / (p2 * i)) * i + j));"
	    		+"	            p.y += 0.1 / (i + j) * cosf(i * p3 * p.x + time + sinf((time / (p4 * i)) * i + j));"
	    		+"	        }"
	    		+"	        if(j==0)"
	    		+"	        	col.x=sinf(p5* dc_acrilic_sq (p.x)) + sinf(p6* dc_acrilic_sq (p.y));"
	    		+"	        if(j==1)"
	    		+"	        	col.y=sinf(p5* dc_acrilic_sq (p.x)) + sinf(p6* dc_acrilic_sq (p.y));"
	    		+"	        if(j==2)"
	    		+"	        	col.z = sinf(p5* dc_acrilic_sq (p.x)) + sinf(p6* dc_acrilic_sq (p.y));"
	    		+"	    }"
	    		+"	    col=make_float3( cosf(col.x*FR), cosf(col.y*FG), cosf(col.z*FB));"
	    		+"		return col;	"
	    		+"}";
	  }
}

