package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.M_2PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.floor;
import static org.jwildfire.base.mathlib.MathLib.sin;

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



public class DC_HexagonsFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_hexagons
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 */



	private static final long serialVersionUID = 1L;

	public static final String PARAM_SHAPE = "shape";

	private static final String PARAM_SEED = "seed";
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";


	  private final static int SHAPE_SQUARE = 0;
	  private final static int SHAPE_DISC = 1;
	



	private int shape = 0;
	private int seed = 10000;
	double time=0.0;
	double zoom=4.0;

	
	double centre=0.0;
	double range=0.333;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
	
	private static final String[] additionalParamNames = { PARAM_SHAPE,PARAM_SEED,PARAM_TIME,PARAM_ZOOM};


	public double hex(vec2 p) 
	{
	  p.x *= 0.57735*2.0;
		p.y += G.mod(G.floor(p.x), 2.0)*0.5;
		p = G.abs((G.mod(p, 1.0).minus(0.5)));
		return G.abs(G.max(p.x*1.5 + p.y, p.y*2.0) - 1.0);
	}
		
	public vec3 getRGBColor(double xp,double yp)
	{
		vec2 p = new vec2(xp, yp).multiply(zoom);
        double col=0.2;
		double s = G.sin(G.dot(p, p) / -64. + time * 4.);
		s = G.pow(G.abs(s), 0.5) * G.sign(s);
		double  r = .35 + .25 * s;
		double t = G.pow(G.abs(G.sin(time * 4.)), 0.2) * G.sign(G.sin(time * 4.));
		t *= 10.25;
		 p = p.times(new mat2(G.cos(t), -G.sin(t), G.sin(t), G.cos(t)));
		vec3 color= new  vec3(G.smoothstep(r - 0.1, r + 0.1, hex(p)));
        return color;

	}
 	
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) 
	{
		 vec2 uV=new vec2(0.),p=new vec2(0.);
	     vec3 color=new vec3(0.0); 
			int[] tcolor=new int[3];  

	     if(colorOnly==1)
		 {
			 uV.x=pAffineTP.x;
			 uV.y=pAffineTP.y;
		 }
		 else
		 {		 
	          switch (shape) {
	            case SHAPE_SQUARE:
	   			 uV.x=2.0*pContext.random()-1.0;
				 uV.y=2.0*pContext.random()-1.0;
	              break;

	            case SHAPE_DISC:
	             double r = 2.*pContext.random() + 2.*pContext.random();
	              r = (r > 2.0) ? 4.0 - r : r;

	             double theta = pContext.random() * M_2PI;
	             double s = sin(theta);
	             double c = cos(theta);
	              uV.x = 0.5 * r * s;
	              uV.y = 0.5 * r * c;
	              break;
	          }
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
		return "dc_hexagons";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { shape,seed,time,zoom},super.getParameterValues());
	}


	
	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_SHAPE)) {
			shape = (int)Tools.limitValue(pValue, 0 , 1);
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_hexagons_ColorOnly ==1)"
	    		+"{"
	    		+"   x=__x;"
	    		+"   y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"   if(__dc_hexagons_shape==0)"
	    		+"   {"
	    		+"       x=2.0*RANDFLOAT()-1.0;"
	    		+"       y=2.0*RANDFLOAT()-1.0;"
	    		+"   }"
	    		+"   else"
	    		+"   {"  
                +"      float r= 2.0*RANDFLOAT() + 2.0*RANDFLOAT();"
	    		+"      r = (r > 2.0) ? 4.0 - r : r;"  
	    		+"      float theta= RANDFLOAT()*2.0*PI;"
	    		+"      float s=sinf(theta);"
	    		+"      float c=cosf(theta);"
	    		+"      x=0.5*r*s;"
	    		+"      y=0.5*r*c;"
	    		+"   }"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_hexagons_zoom;"
	    		+"color=dc_hexagons_getRGBColor(uv,__dc_hexagons_time);"
	    		+"if( __dc_hexagons_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_hexagons_Gradient ==1 )"
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
	    		+"else if( __dc_hexagons_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_hexagons*x;"
	    		+"__py+= __dc_hexagons*y;"
	    		+"float dz = z * __dc_hexagons_scale_z + __dc_hexagons_offset_z;"
	    		+"if ( __dc_hexagons_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";

	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "	__device__ float  dc_hexagons_hex (float2 p) "
	    		+"	{"
	    		+"	    p.x *= 0.57735*2.0;"
	    		+"		p.y += mod(floorf(p.x), 2.0)*0.5;"
	    		+"		p = abs((mod(p, 1.0)-(0.5)));"
	    		+"		return abs(fmaxf(p.x*1.5 + p.y, p.y*2.0) - 1.0);"
	    		+"	}"
	    		
	    		+"	__device__ float3  dc_hexagons_getRGBColor (float2 p, float time)"
	    		+"	{"
	    		+"      float col=0.2;"
	    		+"		float s = sinf(-dot(p, p) / 64.0 + time * 4.0);"
	    		+"		s = powf(abs(s), 0.5) * sign(s);"
	    		+"		float  r = .35 + .25 * s;"
	    		+"		float t = powf(abs(sinf(time * 4.)), 0.2) * sign(sinf(time * 4.));"
	    		+"		t *= 10.25;"
	    		+"       Mat2 M;"
	    		+ "      Mat2_Init(&M,cosf(t), -sinf(t), sinf(t), cosf(t));"
	    		+"		 p = times(&M,p);"
	    		+"      float tmp=smoothstep(r - 0.1 , r + 0.1 ,  dc_hexagons_hex (p));"
	    		+"		float3 color= make_float3(tmp,tmp,tmp);"
	    		+"      return color;"
	    		+"	}";
	  }
}

