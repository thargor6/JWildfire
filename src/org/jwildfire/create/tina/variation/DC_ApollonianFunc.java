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
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;



public class DC_ApollonianFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_apollonian
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019 
	 *  Reference: 	   https://www.shadertoy.com/view/Xdcyzl
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";


	private int seed = 10000;
	double time=0.0;

	


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME};

	public  mat3 rot (vec3 s) {
		double 	sa = G.sin(s.x),
				ca = G.cos(s.x),
				sb = G.sin(s.y),
				cb = G.cos(s.y),
				sc = G.sin(s.z),
				cc = G.cos(s.z);
		return new mat3 (
			new vec3(cb*cc, -cb*sc, sb),
			new vec3(sa*sb*cc+ca*sc, -sa*sb*sc+ca*cc, -sa*cb),
			new vec3(-ca*sb*cc+sa*sc, ca*sb*sc+sa*cc, ca*cb)
		);
	}
	
	public vec3 app (vec3 v, double k, mat3 m)
	{
		mat3 m1=m.times(k);
		for (int i = 0; i < 50; i++) 
		{
			 v= G.abs(m1.times(v).division(G.dot(v,v)).multiply(0.5).minus(0.5)).multiply(2.0).minus(1.0); 
	    }return v;
	}	
	
	
	public vec3 getRGBColor(double xp,double yp)
	{

		vec2 uv=new vec2(xp,yp);
		vec3 col=new vec3(0.0);

	    double t = 0.05*time;
	    
	    mat3 m = rot(new vec3(t).add( new vec3(1,2,3)));
	    double k = 1.2+0.1*G.sin(0.1*time);
	    
	    double f1=(2.+0.25*G.sin(0.3*time));
        vec2 v2=new vec2(2.0).multiply(uv);
	    vec3 v = m.times(f1).times( new vec3(v2 ,0.0) );
        vec3 v3=G.sin(app(v,k,m));	    
//	    col =v3.multiply(new vec3(0.6)).add(new vec3(0.5));
	    col =v3.multiply(0.6).add(0.5);
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
		return "dc_apollonian";
	}

	public String[] getParameterNames() {
	    return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed,time},super.getParameterValues());
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE,VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( varpar->dc_apollonian_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y);"
	    		+"color=dc_apollonian_getRGBColor(uv,varpar->dc_apollonian_time);"
	    		+"if( varpar->dc_apollonian_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( varpar->dc_apollonian_Gradient ==1 )"  
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
	    		+"else if( varpar->dc_apollonian_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= varpar->dc_apollonian*x;"
	    		+"__py+= varpar->dc_apollonian*y;"
	    		+"float dz = z * varpar->dc_apollonian_scale_z + varpar->dc_apollonian_offset_z;"
	    		+"if ( varpar->dc_apollonian_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	  public String getGPUFunctions(FlameTransformationContext context) {
//		  return "";
//	    return   "__device__ float3 dc_apollonian_getRGBColor (float2 uv, float time)"
//	    		+"{"
//	    		+"      float3 col=make_float3(1.0f,0.5f,0.30f);"
//	    		+"      float t=0.05*time;"
//	    		+"	    Mat3 m=rotEuler(make_float3(-(t+1.0),-(t+2.0),-(t+3.0)));"
//	    		+"	    float k = 1.2 + 0.1*sinf(0.1*time);" 
//	    		+"	    " 
//	    		+"	    float f1=2.0 + 0.25*sinf(0.3*time);" 
//	    		+"      float2 v2=uv*make_float2(2.0,2.0);"
//	    	    + "     times(&m,f1);" 
//	    		+"	    float3 v=times(&m, make_float3(v2.x,v2.y ,0.0));" 
////	    		+"      float3 ap=dc_apollonian_calcolor (v,k,m);"
//				+"	    float3 ap=make_float3(v.x, v.y, v.z);"  
//				+"	    for (int i=0;i<50;i++)"
//				+"	    {"
//				+"		  times(&m,k);" 
//				+"		  float3 v1=times(&m,v);"
//				+"		  ap= abs( v1/(dot(v,v))*0.5-0.5)*2.0-1.0;" 
//				+"      }"
////--------------------------				
//   				+"      float3 v3=sin(ap);"
//   				+"	    col =v3*0.6+0.5;"
//	    		+"		return col;"
//	    		+"}"
//	    		+"__device__ float3 dc_apollonian_calcolor (float3 v, float k, Mat3 m)" 
//	    		+"{"
//	    		+"	float3 vm=make_float3(v.x,v.y,v.z);"  
//	    		+"	for (int i = 0; i < 50; i++) "
//	    		+"	{"
//	    		+"		times(&m,k);" 
//	    		+"		float3 v1=times(&m,v);" 
//	    		+"		vm= abs( v1/(dot(v,v))*0.5-0.5)*2.0-1.0;" 
//	    		+"  }"
//   			    +"  return vm;"
//	    		+"}";
//	  }
	   return   "__device__  float3  dc_apollonian_app  (float3 v, float k, Mat3 m)"
			   +"{"
			   +"   float3 v1;"
			   +"   Mat3 m1;"

			   +"	for (int i =0;i<50;i++)"
			   +"	{"
			   +"       m1=m;"
			   +"	    times(&m1,k);"
			   +"		v1= abs(times(&m1,v)/(dot(v,v))*0.5-0.5)*2.0-1.0;"
			   +"	}"
               +"   return v1;"
			   +"}"
			   
			   +"__device__ Mat3  dc_apollonian_rot  (float3 s) {"
			   +"	float 	sa = sinf(s.x),"
			   +"			ca = cosf(s.x),"
			   +"			sb = sinf(s.y),"
			   +"			cb = cosf(s.y),"
			   +"			sc = sinf(s.z),"
			   +"			cc = cosf(s.z);"
			   +"   Mat3 M;"
			   +"   Mat3_Init(&M, make_float3(cb*cc, -cb*sc, sb),"
			   +"		make_float3(sa*sb*cc+ca*sc, -sa*sb*sc+ca*cc, -sa*cb)," 
			   +"		make_float3(-ca*sb*cc+sa*sc, ca*sb*sc+sa*cc, ca*cb)" 
			   +"	);"
			   +"	return M;"
			   +"}"
			   
			   +"__device__ float3  dc_apollonian_getRGBColor (float2 uv,float time)"
			   +"{"
			   +"	float3 col;"
			   +"    float t = 0.05*time;"
			   +"    Mat3 m = dc_apollonian_rot(make_float3((t+1.0),(t+2.0),(t+3.0)));"
			   +"    float k = 1.2+0.10*sinf(0.1*time);"
			   +"    float f1= 2.0+0.25*sinf(0.3*time);"
			   +"    float2 v2=uv*2.0;"
			   +"    Mat3 m1=m;"
			   +"    times(&m1,f1);"
			   + "   float3 v=times( &m1,make_float3(v2.x,v2.y,0.0) );"
			   +"    float3 v3=sinf(dc_apollonian_app(v,k,m));"
			   +"    col =v3*0.6+0.5;"
			   +"	return col;"
			   +"}";
	  }


}

