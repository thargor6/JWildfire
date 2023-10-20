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

public class DC_InversionFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_inversion
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019 
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ITERS = "iterations";
	private static final String PARAM_VSCALEX = "scaleX";
	private static final String PARAM_VSCALEY = "scaleY";



	private int seed = 10000;
	double time=0.0;
	private int Iterations = 50;

	private double scalex = 1.5;
	private double scaley = 1.5;
	
	vec2 vScale=new vec2(1.5);
	


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	
 	
 	
 	
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_ITERS,PARAM_VSCALEX,PARAM_VSCALEY};

	
	//uniform vec2 vScale; slider[(1.1,1.1),(1.5,1.5),(3.,3.)]
	//		uniform int Iterations;  slider[0,50,100]

			public vec2 CircleInversion(vec2 vPos, vec2 vOrigin, double fRadius)
			{
				vec2 vOP = vPos.minus(vOrigin);

				vOrigin = vOrigin.minus( vOP.multiply( fRadius) .multiply( fRadius / G.dot(vOP, vOP)));


			        vOrigin.x += Math.sin(vOrigin.x * 0.001) / Math.cos(vOrigin.y * 0.001);
			        vOrigin.y +=Math. sin(vOrigin.x * 0.001) * Math.cos(vOrigin.y * 0.001);

			        return vOrigin;
			}

			double Parabola( double x, double n )
			{
				return Math.pow( 3.0*x*(1.0-x), n );
			}

	
	
	public vec3 getRGBColor(double xp,double yp)
	{

		vec2 v=new vec2(xp,yp);
		vec3 col=new vec3(0.0);

	   // double t = 0.05*time;
		vec2 vOffset = new vec2( Math.sin(time * 0.123), Math.atan(time * 0.0567));

		double l = 0.0;
		double minl = 10000.0;

		for(int i=0; i<Iterations; i++)
		{
			v.x = Math.abs(v.x);
			v = v.multiply(vScale).plus(  vOffset);

			v = CircleInversion(v, new vec2(0.5, 0.5), 0.9);

			l = G.length(v.multiply(v));
			minl = G.min(l, minl);
		}


		double t = 2.1 + time * 0.025;
		vec3 vBaseColour = G.normalize(new vec3(Math.sin(t * 1.790), Math.sin(t * 1.345), Math.sin(t * 1.123)).multiply(0.5).plus( 0.5));

		//vBaseColour = vec3(1.0, 0.15, 0.05);

		double fBrightness = 11.0;

		vec3 vColour = vBaseColour.multiply( l * l * fBrightness);

		minl = Parabola(minl, 5.0);

		vColour = vColour.multiply(minl + 0.14);

		vColour = new vec3(1.0).minus(  G.exp(vColour.multiply(-1.)));
		
	return vColour;

	}
	
	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) 
	{
      vScale=new vec2(scalex,scaley);
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
		return "dc_inversion";
	}

	public String[] getParameterNames() {
	    return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed,time,Iterations,scalex,scaley},super.getParameterValues());
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
		else if (pName.equalsIgnoreCase(PARAM_ITERS)) {
			Iterations = (int)Tools.limitValue(pValue, 0 , 100);
		}
		else if (pName.equalsIgnoreCase(PARAM_VSCALEX)) {
			scalex = Tools.limitValue(pValue, 1.1 , 3.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_VSCALEY)) {
			scaley =Tools.limitValue(pValue, 1.1 , 3.0);
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
	    		+"if( __dc_inversion_ColorOnly ==1)"
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
	    		+"color=dc_inversion_getRGBColor(uv,__dc_inversion_time,__dc_inversion_iterations,__dc_inversion_scaleX,__dc_inversion_scaleY);"
	    		+"if( __dc_inversion_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_inversion_Gradient ==1 )"
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
	    		+"else if( __dc_inversion_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_inversion*x;"
	    		+"__py+= __dc_inversion*y;"
	    		+"float dz = z * __dc_inversion_scale_z + __dc_inversion_offset_z;"
	    		+"if ( __dc_inversion_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {

		 return   "__device__ float2  dc_inversion_CircleInversion (float2 vPos, float2 vOrigin, float fRadius)"
				 +"{"
				 +"	float2 vOP = vPos-vOrigin;"
				 +"	   vOrigin = vOrigin- vOP * fRadius * fRadius / dot(vOP, vOP);"
				 +"    vOrigin.x += sinf(vOrigin.x * 0.001) / cosf(vOrigin.y * 0.001);"
				 +"    vOrigin.y += sinf(vOrigin.x * 0.001) * cosf(vOrigin.y * 0.001);"
				 +"    return vOrigin;"
				 +"}"
				 
				 +"__device__	float  dc_inversion_Parabola ( float x, float n )"
				 +"{"
				 +"	    return powf( 3.0*x*(1.0-x), n );"
				 +"}"

				 +"__device__ float3 dc_inversion_getRGBColor (float2 v, float time, float iterations, float scaleX, float scaleY)"
				 +"{"
				 +" float2 vScale  = make_float2(scaleX,scaleY);"
				 +"	float2 vOffset = make_float2( sinf(time * 0.123), atanf(time * 0.0567));"
				 +"	float l = 0.0;"
				 +"	float minl = 10000.0;"
				 +"	for(int i=0; i<iterations; i++)"
				 +"	{"
				 +"		v.x = abs(v.x);"
				 +"		v = v*vScale +  vOffset;"
				 +"		v =  dc_inversion_CircleInversion (v, make_float2(0.5f, 0.5f), 0.9f);"
				 +"		l = length(v*v);"
				 +"		minl = fminf(l, minl);"
				 +"	}"
				 +"	float t = 2.1 + time * 0.025;"
				 +"	float3 vBaseColour = normalize(make_float3(sinf(t * 1.790), sinf(t * 1.345), sinf(t * 1.123))*0.5+ 0.5);"
				 +"		"
				 +"	float fBrightness = 11.0;"
				 +"	float3 vColour = vBaseColour*( l * l * fBrightness);"
				 +"	minl =  dc_inversion_Parabola (minl, 5.0);"
				 +"	vColour = vColour*(minl + 0.14);"
				 +"	vColour = make_float3(1.0,1.0,1.0) - expf(vColour*(-1.0));"
				 +"	return vColour;"
				 +"}";
	 }
}

