package org.jwildfire.create.tina.variation;

import java.util.Random;
import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

public class DC_CircuitsFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_circuits
	 * Date: February 13, 2019
	 * Author:Jesus Sosa
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_SEED = "Seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_RATE = "rate";
	private static final String PARAM_INTENSITY = "intensity";
	private static final String PARAM_FOCUS = "focus"; 
	private static final String PARAM_PULSE = "pulse"; 
	private static final String PARAM_GLOW = "glow"; 
	private static final String PARAM_LOOPS = "loops"; 
	private static final String PARAM_ZOOM = "zoom"; 



	int seed=1000000;
	double time=0.0;
	double rate=.8;
	double intensity=0.9;
	double focus=1.5;
	double pulse=10.;
	double glow=2.;
	double loops=15.;
	double zoom=1.0;

	
	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;

	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_RATE,PARAM_INTENSITY,PARAM_FOCUS,PARAM_PULSE,PARAM_GLOW,PARAM_LOOPS,PARAM_ZOOM};

	    
	double S=(101.0+glow)*intensity;
	
	public vec3 formula(vec2 z, double t)
	{
	double M=0.0;
	double o,ot2,ot=ot2=1000.0;
	double K=G.floor(loops/4.0+G.floor(5.0*zoom));
	vec3 color=new vec3(0.0);

		for (int i=0; i<11; i++) {
			z = G.abs(z).division(G.clamp(G.dot(z, z), 0.1, 0.5)).minus( t);
			double l = G.length(z);
			o = G.min(G.max(G.abs(G.min(z.x, z.y)), -l + 0.25), G.abs(l - 0.25));
			ot = G.min(ot, o);
			ot2 = G.min(l * 0.1, ot2);
			M = G.max(M, (double)i * (1.0 - G.abs(G.sign(ot - o))));
			if (K <= 0.0) 
	           break;
			K -= 1.0;
		}
		M += 1.0;
		double w = (intensity * zoom) * M;
		double circ = Math.pow(G.max(0.0, w - ot2) / w, 6.0);
		S += G.max(Math.pow(G.max(0.0, w - ot) / w, 0.25), circ);
	    vec3 t1=new vec3(0.1).add(new vec3(0.45,0.75,M*0.1));
		vec3 col = G.normalize(t1);
	    vec3 t2=new vec3(0.4 + G.mod(M / 9.0 - t * pulse + ot2 * 2.0, 1.));
	    color=color.add(col.multiply(t2));
	    double f1=circ * (10.0 - M) * 3.0;
	    color=color.add(new vec3(1.0,0.7,0.3).multiply(f1));
	    return color;
	}
	

	
	public vec3 getRGBColor(double xp,double yp)
	{

		  vec2 pos=new vec2(xp,yp);
		  vec3 color=new vec3(0.0);
			vec2 center=new vec2(0.0);
			double R = 0.0;
			double N = time * 0.01 * rate;

			double  T = 2.0 * rate;
			if (N > 6.0 * rate) { 
				R += 1.0;
				N -= (R * 8.0 * rate);
			}
			if (N < 4.0 * rate) T += N;
			else  T = 8.0 * rate - N;
			double Z = (1.05-zoom);

			vec2 uv = pos.plus(center);
			double sph = G.length(uv)*0.1; 
			sph = Math.sqrt(1.0 - sph * sph) * 2.0 ;
			double a = T * Math.PI;
			double b = a + T;
			double c = G.cos(a) + G.sin(b);
			uv = uv.times(new mat2(G.cos(b), G.sin(b), -G.sin(b), G.cos(b)));
			uv = uv.times(new mat2(G.cos(a),-G.sin(a), G.sin(a),G.cos(a)));
			uv = uv.minus(new vec2(G.sin(c), G.cos(c)).division( Math.PI));
			uv = uv.multiply(Z);
			double pix = 0.5 / 1000.0 * Z / sph;
			double dof = (zoom * focus) + (T * 0.25);
			double L = G.floor(loops);
			for (int aa=0; aa<24; aa++) {
				vec2 aauv = G.floor(new vec2( (double)aa / 6.0, G.mod((double)aa, 6.0)));
				color=formula(uv.plus(aauv.multiply(pix).multiply( dof)), T);
				if (L <= 0.0) 
					break;
				L -= 1.0;
			}
			S /= G.floor(loops); 
			color = color.division(G.floor(loops));
			vec3 colo = G.mix(new vec3(0.15), color, S).multiply((1.0 - G.length(pos)));	
			colo =colo.multiply(new vec3(1.2, 1.1, 1.0));
		  
		 return colo;
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
		return "dc_circuits";
	}

	public String[] getParameterNames() {
	    return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { seed,time,rate,intensity,focus,pulse,glow,loops,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SEED)) {
			seed= (int)pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);
		}  
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;

		}
		else if (pName.equalsIgnoreCase(PARAM_RATE)) {
			rate= pValue;;
		}  
		else if (pName.equalsIgnoreCase(PARAM_INTENSITY)) {
			intensity= pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_FOCUS)) { // 0.0- 2*Math.PI radians
			focus= pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_PULSE)) {
			pulse= pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_GLOW)) {
			glow= pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_LOOPS)) {
			loops= pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom= pValue;
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
	public String getGPUCode(FlameTransformationContext context) {
	return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_circuits_ColorOnly ==1)"
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
	    		+"color=dc_circuits_getRGBColor(uv,__dc_circuits_time,__dc_circuits_rate,__dc_circuits_zoom,"
	    		+ "                             __dc_circuits_focus,__dc_circuits_loops,__dc_circuits_glow,__dc_circuits_intensity,__dc_circuits_pulse);"
	    		+"if( __dc_circuits_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_circuits_Gradient ==1 )"
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
	    		+"else if( __dc_circuits_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_circuits*x;"
	    		+"__py+= __dc_circuits*y;"
	    		+"float dz = z * __dc_circuits_scale_z + __dc_circuits_offset_z;"
	    		+"if ( __dc_circuits_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	}
	  public String getGPUFunctions(FlameTransformationContext context) {
		    return   "	__device__ float3  dc_circuits_formula (float2 z, float t, float loops, float zoom,float intensity,float glow, float pulse )"
		    		+"	{"
		    		+"	float M=0.0;"
		    		+"	float S=(101.0+glow)*intensity;"
		    		+"	float o,ot2,ot=ot2=1000.0;"
		    		+"	float K=floorf(loops/4.0+floorf(5.0*zoom));"
		    		+"	float3 color=make_float3(0.0,0.0,0.0);"
		    		+"		for (int i=0; i<11; i++) {"
		    		+"			z = abs(z)/(clamp(dot(z, z), 0.1, 0.5))-( t);"
		    		+"			float l = length(z);"
		    		+"			o = fminf(fmaxf(abs(fminf(z.x, z.y)), -l + 0.25), abs(l - 0.25));"
		    		+"			ot = fminf(ot, o);"
		    		+"			ot2 = fminf(l * 0.1, ot2);"
		    		+"			M = fmaxf(M, (float)i * (1.0 - abs(sign(ot - o))));"
		    		+"			if (K <= 0.0) "
		    		+"	           break;"
		    		+"			K -= 1.0;"
		    		+"		}"
		    		+"		M += 1.0;"
		    		+"		float w = (intensity * zoom) * M;"
		    		+"		float circ = powf(fmaxf(0.0, w - ot2) / w, 6.0);"
		    		+"      float t99=powf(fmaxf(0.0, w - ot) / w ,  0.25);"
		    		+"		S = S + fmaxf(t99 , circ);"
		    		+"	    float3 t1=make_float3(0.1,0.1,0.1) + make_float3(0.45,0.75,M*0.1);"
		    		+"		float3 col = normalize(t1);"
		    		+"      float t100=0.4 + mod(M / 9.0 - t * pulse + ot2 * 2.0, 1.);"
		    		+"	    float3 t2=make_float3(t100,t100,t100);"
		    		+"	    color=color + (col*t2);"
		    		+"	    float f1=circ * (10.0 - M) * 3.0;"
		    		+"	    color=color + (make_float3(1.0,0.7,0.3)*f1);"
		    		+"	    return color;"
		    		+"	}"
		    		+"	__device__ float3  dc_circuits_getRGBColor (float2 pos, float time,float rate, float zoom,float focus, float loops, float glow, float intensity,float pulse )"
		    		+"	{"
		    		+"		  float3 color=make_float3(0.0,0.0,0.0);"
		    		+"	       float S=(101.0+glow)*intensity;"
		    		+"			float2 center=make_float2(0.0,0.0);"
		    		+"			float R = 0.0;"
		    		+"			float N = time * 0.01 * rate;"
		    		+"			float  T = 2.0 * rate;"
		    		+"			if (N > 6.0 * rate) { "
		    		+"				R += 1.0;"
		    		+"				N -= (R * 8.0 * rate);"
		    		+"			}"
		    		+"			if (N < 4.0 * rate) T += N;"
		    		+"			else  T = 8.0 * rate - N;"
		    		+"			float Z = (1.05-zoom);"
		    		+"			float2 uv = pos+(center);"
		    		+"			float sph = length(uv)*0.1; "
		    		+"			sph = sqrt(1.0 - sph * sph) * 2.0 ;"
		    		+"			float a = T * PI;"
		    		+"			float b = a + T;"
		    		+"			float c = cosf(a) + sinf(b);"
		    		+"          Mat2 M;"
		    		+"          Mat2_Init(&M,cosf(b), sinf(b), -sinf(b), cosf(b));"
		    		+"			uv = times(&M,uv);"
		    		+"          Mat2 M1;"
		    		+"          Mat2_Init(&M1,cosf(a),-sinf(a), sinf(a),cosf(a));"
		    		+"			uv = times(&M1,uv);"
		    		+"			uv = uv-make_float2(sinf(c), cosf(c)) / PI;"
		    		+"			uv = uv*Z;"
		    		+"			float pix = 0.5 / 1000.0 * Z / sph;"
		    		+"			float dof = (zoom * focus) + (T * 0.25);"
		    		+"			float L = floorf(loops);"
		    		+"			for (int aa=0; aa<24; aa++) {"
		    		+"				float2 aauv = floorf(make_float2( (float)aa / 6.0, mod((float)aa, 6.0)));"
		    		+"				color= dc_circuits_formula (uv+(aauv*pix* dof), T,loops,zoom,intensity,glow,pulse);"
		    		+"				if (L <= 0.0)"
		    		+"					break;"
		    		+"				L = L - 1.0;"
		    		+"			}"
		    		+"			S = S / floorf(loops);"
		    		+"			color = color/(floorf(loops));"
		    		+"			float3 colo = mix(make_float3(0.15,0.15,0.15), color, S)*((1.0 - length(pos)));	"
		    		+"			colo =colo*(make_float3(1.2, 1.1, 1.0));"
		    		+"		  "
		    		+"		 return colo;"
		    		+"	}";
		  }
}

