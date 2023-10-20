package org.jwildfire.create.tina.variation;

import java.util.Random;
import js.glsl.G;
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

public class DC_LayersFunc  extends DC_BaseFunc implements SupportsGPU {


	/*
	 * Variation : dc_layers
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_N = "N";
	private static final String PARAM_AMPLITUDE = "Amplitude";
	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";


	int N=8;
	double Amplitude=0.995;
	private int seed = 10000;
	double time=0.0;
	double zoom=8.0;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	private static final String[] additionalParamNames = { PARAM_N,PARAM_AMPLITUDE ,PARAM_SEED,PARAM_TIME,PARAM_ZOOM};

	
	public double snoise(vec3 v) {
		return (G.sin(v.x*4.)*G.cos(v.y*4.)-G.sin(v.z*4.));
	}

	public vec3 rotate(vec3 v,vec2 r) 
	{
		mat3 rxmat = new mat3(1,   0    ,    0    ,
				  0,G.cos(r.y),-G.sin(r.y),
				  0,G.sin(r.y), G.cos(r.y));
		mat3 rymat = new mat3(G.cos(r.x), 0,-G.sin(r.x),
				     0    , 1,    0    ,
				  G.sin(r.x), 0,G.cos(r.x));
		
		
		return v.times(rxmat).times(rymat);
		
	}
	
	public vec3 getRGBColor(double xp,double yp)
	{


		vec2 p=new vec2(xp,yp).multiply(zoom);


		vec2 m = new vec2(2.,1.).multiply(Amplitude* Math.PI);

		
		vec3 color = new vec3(0.0);
		
		vec3 pos = G.normalize(rotate(new vec3(p,1.0),new vec2(m)));
		
		double dist = 0.0;
		
		for(int k = 1; k <= N ;k++)
		{
			double shell = G.abs(snoise(pos.multiply((double)k).add( new vec3(time,0,0).multiply(0.13))));
			
			shell = G.smoothstep(0.25,0.2,shell);
			
			dist = G.max(dist,shell*(1.-((double)k/8.)));
		}
		
		color = G.mix(new vec3(1., 1.0, 1.0),new vec3(.0,0.,0.),1.-dist);

		return color;
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
		return "dc_layers";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { N,Amplitude,seed,time,zoom},super.getParameterValues());
	}


	
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_N)) {
			
			N = (int)Tools.limitValue(pValue, 1 , 10);
		}
		else if (pName.equalsIgnoreCase(PARAM_AMPLITUDE)) {
			
			Amplitude = Tools.limitValue(pValue, 0.0 , 10.0);
		}
		else if (PARAM_SEED.equalsIgnoreCase(pName)) 
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
	    		+"if( __dc_layers_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_layers_zoom;"
	    		+" color=dc_layers_getRGBColor(uv,__dc_layers_time,__dc_layers_N,"
	    		+ "                                   __dc_layers_Amplitude);"
	    		+"if( __dc_layers_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
				+"   __colorA  = 1.0;"
	    		+"}"
				+"else if( __dc_layers_Gradient ==1 )"
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
	    		+"else if( __dc_layers_Gradient ==2 )"
	    		+"{"
				+"  int3 icolor=dbl2int(color);"
	    		+"  z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_layers*x;"
	    		+"__py+= __dc_layers*y;"
	    		+"float dz = z * __dc_layers_scale_z + __dc_layers_offset_z;"
	    		+"if ( __dc_layers_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";

	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {

	    return   "__device__ float  dc_layers_snoise (float3 v) {"
	    		+"	return (sinf(v.x*4.)*cosf(v.y*4.)-sinf(v.z*4.));"
	    		+"}"
	    		
	    		+"__device__ float3  dc_layers_rotate (float3 v,float2 r) "
	    		+"{"
	    		+"	Mat3 rxmat;"
	    		+ " Mat3_Init(&rxmat, 1.0,   0.0    ,    0.0    ,"
	    		+"	                  0.0 ,cosf(r.y),-sinf(r.y),"
	    		+"			          0.0 ,sinf(r.y), cosf(r.y));"
	    		+"	Mat3 rymat;"
	    		+ " Mat3_Init(&rymat, cosf(r.x), 0.0 ,-sinf(r.x),"
	    		+"			           0.0     , 1.0 ,    0.0  ,"
	    		+"			          sinf(r.x), 0.0 ,cosf(r.x));"
	    		+"  float3  vt=times(&rxmat,v);"
	    		+"	vt = times(&rymat,vt);"
	    		+"	return vt;"
	    		+"}"

	    		+"__device__ float3  dc_layers_getRGBColor (float2 p, float time, float N, float Amplitude)"
	    		+"{"
	    		+"	float2 m = make_float2(2.0,1.0)*(Amplitude* PI);"
	    		+"	float3 color = make_float3(0.0,0.0,0.0);"
	    		+"  float3 tmp=dc_layers_rotate (make_float3(p.x,p.y,1.0),make_float2(m.x,m.y));"
	    		+"	float3 pos = normalize( tmp );"
	    		+"	float dist = 0.0;"
	    		+"	for(int k = 1; k <= N ;k++)"
	    		+"	{"
	    		+"		float shell = abs( dc_layers_snoise (pos*((float)k) +  make_float3(time,0.0,0.0)*0.13) );"
	    		+"		shell = smoothstep(0.25,0.2,shell);"
	    		+"		dist = fmaxf(dist,shell*(1.-((float)k/8.)));"
	    		+"	}"
	    		+"	color = mix(make_float3(1.0, 1.0, 1.0),make_float3(0.0,0.0,0.0),1.0-dist);"
	    		+"	return color;"
	    		+"}";
	  }
}

