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

public class DC_Grid3DFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_grid3D
	 * Date: February 13, 2019
	 * Author:Jesus Sosa
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_SEED = "Seed";
	private static final String PARAM_TIME = "time";




	double zoom=1.;
	private int seed = 10000;
	double time=200.;


	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	



	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_SEED,PARAM_TIME};




		public vec3 field(vec3 p) {
			p = p.multiply(0.1);
			double f = .1;
			for (int i = 0; i < 3; i++) 
			{
				p = new vec3(p.y,p.z,p.x); //*mat3(.8,.6,0,-.6,.8,0,0,0,1);
//				p += vec3(.123,.456,.789)*float(i);
				p = G.abs(G.fract(p).minus(0.5));
				p = p.multiply(2.0);
				f *= 2.0;
			}
			p = p.multiply(p);
			return G.sqrt(p.add(new vec3(p.y,p.z,p.x))).division(f).minus(.05);
		}
		
	public vec3 getRGBColor(double xp,double yp)
	{

		
		// a raymarching experiment by kabuto
		//fork by tigrou ind (2013.01.22)
		// slow mod by kapsy1312.tumblr.com


	        int MAXITER = 30;

	        vec2 p=new vec2(xp,yp).multiply(zoom);
			vec3 dir = G.normalize(new vec3(p.x,p.y,1.));
			double a = time * 0.021;
			vec3 pos =new vec3(0.0,time*0.1,0.0);
			
			dir = dir.times(new mat3(1,0,0,0,G.cos(a),-G.sin(a),0,G.sin(a),G.cos(a)));
			dir = dir.times(new mat3(G.cos(a),0,-G.sin(a),0,1,0,G.sin(a),0,G.cos(a)));
			vec3 color = new vec3(0);
			
			for (int i = 0; i < MAXITER; i++) {
				vec3 f2 = field(pos);
				double f = G.min(G.min(f2.x,f2.y),f2.z);
				
				pos = pos.add(dir.multiply(f));
				vec3 t0=new vec3( ((double) MAXITER-i)).division(f2.add(0.1));
				color = color.add(t0);
			}
			vec3 color3=new vec3(1.0).minus(new vec3(1.0).division((new vec3(1.0).add(color.multiply(.09/(double)(MAXITER*MAXITER))))));

			// color3 = color3.multiply(color3);
			return new vec3( color3.r+color3.g+color3.b);
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
		return "dc_grid3D";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,seed, time},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.01 , 1000.0);
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
			time = Tools.limitValue(pValue, 1.0 , 1000.0);
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
	    		+"if( __dc_grid3D_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_grid3D_zoom;"
	    		+"color=dc_grid3D_getRGBColor(uv,__dc_grid3D_time);"
	    		+"if( __dc_grid3D_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_grid3D_Gradient ==1 )"
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
	    		+"else if( __dc_grid3D_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_grid3D*x;"
	    		+"__py+= __dc_grid3D*y;"
	    		+"float dz = z * __dc_grid3D_scale_z + __dc_grid3D_offset_z;"
	    		+"if ( __dc_grid3D_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";

	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "		__device__ float3  dc_grid3D_field (float3 p) {"
	    		+"			p = p*0.1;"
	    		+"			float f = .1;"
	    		+"			for (int i = 0; i < 3; i++) "
	    		+"			{"
	    		+"				p = make_float3(p.y,p.z,p.x); "
	    		+"				p = abs(fract(p)-0.5);"
	    		+"				p = p*(2.0);"
	    		+"				f *= 2.0;"
	    		+"			}"
	    		+"			p = p*p;"
	    		+"			return sqrt(p + make_float3(p.y,p.z,p.x) )/f - 0.05;"
	    		+"		}"
	    
	    		+"	__device__ float3  dc_grid3D_getRGBColor (float2 p,float time)"
	    		+"	{"
	    		+"			float3 dir = normalize(make_float3(p.x,p.y,1.));"
	    		+"			float a = -time * 0.021;"
	    		+"			float3 pos =make_float3(0.0,time*0.1,0.0);"
	    		+"			Mat3 M;"
	    		+ "         Mat3_Init(&M,1.0,0.0,0.0,0.0,cosf(a),-sinf(a),0.0,sinf(a),cosf(a));"
	    		+"			dir = times(&M, dir);"
	    		+"          Mat3 M1;"
	    		+"          Mat3_Init(&M1,cosf(a),0.0,-sinf(a),0.0,1.0,0.0,sinf(a),0.0,cosf(a));"
	    		+"			dir = times(&M1,dir);"
	    		+"			float3 color = make_float3(0.0,0.0,0.0);"
	    		+"			"
	    		+"			for (int i = 0; i < 30; i++) {"
	    		+"				float3 f2 =  dc_grid3D_field (pos);"
    		    +"				float f = fminf(fminf(f2.x,f2.y),f2.z);"
	    		+"				pos = pos + dir*f;"
	    		+"              float tmp=30.0f - (float)i;"
	    		+"				float3 t0=make_float3( tmp,tmp,tmp) / (f2+0.1);"
	    		+"				color = color + t0;"
	    		+"			}"
	    		+"			float3 color3=make_float3(1.0,1.0,1.0)- make_float3(1.0,1.0,1.0) / ( make_float3(1.0,1.0,1.0) + color*.09/(30.0*30.0) );"
	    		+"          float tmp1=color3.x + color3.y + color3.z;"
	    		+"			return make_float3( tmp1,tmp1,tmp1);"
	    		+"		}";
	  }
}

