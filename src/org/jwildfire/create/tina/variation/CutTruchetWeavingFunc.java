package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.M_2PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
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
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;



public class  CutTruchetWeavingFunc  extends VariationFunc  implements SupportsGPU {

	/*
	 * Variation : cut_truchetweaving
	 * Autor: Jesus Sosa
	 * Date: September 25, 2019
	 * Reference & Credits:  https://www.shadertoy.com/view/llByzz
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED = "randomize";
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_TYPE = "type";
	private static final String PARAM_WIDTH = "width";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";

    int seed=0;
    int mode=1;
    int type=0;
	double CURVE_WIDTH= .15;
	double zoom=8.0;
	private int invert = 0;
	double tol=0.0;

    double uvx =0.0;
    double uvy =0.0;    
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_TYPE,PARAM_WIDTH,PARAM_ZOOM,PARAM_INVERT};
	
	// some constants
	double I3  = 0.333333333;	// 1/3
	double I6  = 0.166666666;	// 1/6
	double R3  = 1.732050807;	// square root of 3
	double IR3 = 0.577350269; 	// the inverse of the square root of 3
	double HIR3= 0.288675134;	// half the inverse of the square root of 3
	double S60 = 0.866025404;	// sine of 60 degrees
	double C60 = 0.5;
	
	// weights for the different types of tiles.
	// actual weight is weight-previous weight
	double W111= .1;
	double W113= .2;
	double W122= .4;
	double W223= .8;
	double W333= 1.;
	

	public double N21(vec2 id) {
		return G.fract(G.sin(id.x*324.23+id.y*5604.342)*87654.53); 
	}

	public vec4 UvCirc(vec2 uv, double radius, double thickness) {
		vec2 st = new vec2(G.atan2(uv.x, uv.y), G.length(uv));
	    
	    double t = thickness/2.;
	    double w = .01;
	    
	    double r1 = radius-t;
	    double r2 = radius+t;
	    
	    double mask = G.smoothstep(t+w, t, G.abs(radius-st.y));
	    double alpha = G.smoothstep(t+.1, t, G.abs(radius-st.y));
	    alpha = alpha*alpha*G.mix(.5, 1., mask);
	    
	    return new vec4(st.x*radius, st.y, mask, alpha);
	}

	public vec4 UvBeam(vec2 uv, double thickness) {
		double t = thickness/2.;
	    double w = .01;
	    double mask = G.smoothstep(t+w, t, G.abs(uv.y));
	    double alpha =G.smoothstep(t+.1, t, G.abs(uv.y));
	    alpha = alpha*alpha*(.5+.5*mask);
	    
	    return new vec4(uv.x, uv.y, mask,alpha);
	}

	public vec3 Truchet(vec2 uv, double n) {
		uv= uv.minus(.5);
	    uv.x /= R3;
	    
	    vec4 v1 = new vec4(0);
	    vec4 v2 = new vec4(0);
	    vec4 v3 = new vec4(0);
	    
	    double w = .15;
	    
	    // get random rotation for each tile
	    // since its only six could probably precompute / do some trickery
	    double r = G.floor(G.fract(n*5.)*6.)/6.;
	    r *= 6.28;
	    double s = sin(r);
	    double c = cos(r);
	    mat2 rot = new mat2(c, -s, s, c);
	    uv = uv.times(rot);
			
	    if(n<W111) {
	        v1 = UvCirc(uv.minus(new vec2(0, I3)), I6, CURVE_WIDTH);		// jump 1
	    	v2 = UvCirc(uv.minus(new vec2(HIR3, -I6)), I6, CURVE_WIDTH);	// jump 1
	    	v3 = UvCirc(uv.minus(new vec2(-HIR3, -I6)), I6, CURVE_WIDTH);	// jump 1
	    }
	    else if(n<W113) {
	        v1 = UvCirc(uv.minus(new vec2(0, I3)), I6, CURVE_WIDTH);		// jump 1
	        v2 = UvCirc(uv.minus(new vec2(0, -I3)), I6, CURVE_WIDTH);		// jump 1
	        v3 = UvBeam(uv, CURVE_WIDTH);						// jump 3
	    }
	    else if(n<W122) {
	        v1 = UvCirc(uv.minus(new vec2(-HIR3, -I6)), I6, CURVE_WIDTH);	// jump 1
	        v2 = UvCirc(uv.minus(new vec2(IR3, 0)), .5, CURVE_WIDTH);		// jump 2
	        v3 = UvCirc(uv.minus(new vec2(HIR3, .5)), .5, CURVE_WIDTH);	// jump 2
	    }
	    else if(n<W223) {
	        v1 = UvCirc(uv.minus(new vec2(IR3, 0)), .5, CURVE_WIDTH);		// jump 2
	        v2 = UvCirc(uv.minus(new vec2(-IR3, 0)), .5, CURVE_WIDTH);   	// jump 2  
	        v3 = UvBeam(uv, CURVE_WIDTH);						// jump 3
	    } else {
	        mat2 rot60 = new mat2(C60, -S60, S60, C60);
	    	mat2 rot60i = new mat2(C60, S60, -S60, C60);
	        
	        v1 = UvBeam(uv, CURVE_WIDTH);						// jump 3
	 		v2 = UvBeam(uv.times(rot60), CURVE_WIDTH);					// jump 3
	    	v3 = UvBeam(uv.times(rot60i), CURVE_WIDTH); 				// jump 3
	    }
	    
	    double d1 = G.fract(n*10.);		// expand my random number by taking digits
	    
	    // composite in different orders
	    vec4 v = d1<.166 ? G.mix(v1, G.mix(v2, v3, v3.w), G.max(v2.w, v3.w))
	           : d1<.333 ? G.mix(v1, G.mix(v3, v2, v2.w), G.max(v2.w, v3.w))
	           : d1<.5   ? G.mix(v2, G.mix(v1, v3, v3.w), G.max(v1.w, v3.w))
	           : d1<.666 ? G.mix(v2, G.mix(v3, v1, v1.w), G.max(v1.w, v3.w))
	           : d1<.833 ? G.mix(v3, G.mix(v1, v2, v2.w), G.max(v1.w, v2.w))
	           :           G.mix(v3, G.mix(v2, v1, v1.w), G.max(v1.w, v2.w));     

	     return new vec3(v.b); // white

	}

	
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

		  double x,y,cx,cy;  
		  if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		      cx=0.0;
		      cy=0.0;
		    }else
		    {
		     x=pContext.random()-0.5;
		     y=pContext.random()-0.5;
		     cx=0.0;
		     cy=0.0;
		    }
	    
	    vec2 u=new vec2(x*zoom-uvx,y*zoom-uvy);
	    		
        if(type==1)
	       CURVE_WIDTH = G.mix(.05, .25, (sin(u.x+sin(u.y))+sin(u.y*.35))*.25+.5);

	    vec2 s = new vec2(1.,R3),
	         a = G.mod(u     ,s).multiply(2.).minus(s),
	         b = G.mod(u.plus(s.multiply(.5)),s).multiply(2.).minus(s);

	    u = u.division(s);
	    
	    double da = G.dot(a, a);
		double db = G.dot(b, b);
	    
	    vec2 id = da < db 
	                  ? G.floor(u) 
	                  : G.floor(u=u.plus(.5)).minus(.5);
	    	    
	    double n = N21(id);

	    vec3 O = Truchet(G.fract(u), n);
	    	    
        double col=0.;
	    col=O.b;   
	    
		pVarTP.doHide=false;
		if(invert==0)
		{
			if (col>tol)
			{ x=0.;
			  y=0.;
			pVarTP.doHide = true;
			}
		} else
		{
			if (col<=tol)
			{ x=0.;
			  y=0.;
			pVarTP.doHide = true;
			}
		}
		pVarTP.x = pAmount * (x-cx);
		pVarTP.y = pAmount * (y-cy);

		if (pContext.isPreserveZCoordinate()) {
			pVarTP.z += pAmount * pAffineTP.z;
		}
	}

	public String getName() {
		return "cut_truchetweaving";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { 
		return new Object[] { seed,mode,type, CURVE_WIDTH,zoom,invert};
	}

	public void setParameter(String pName, double pValue) {
		 if (pName.equalsIgnoreCase(PARAM_SEED)) {
			 seed=(int)pValue;
		     uvx= seed*G.sin(seed*180./Math.PI);
		     uvy= seed*G.sin(seed*180.0/Math.PI); //*0.1+time*0.05;
		}
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
				   mode =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_TYPE)) {
				type = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_WIDTH)) {
				CURVE_WIDTH = Tools.limitValue(pValue, 0.0 , 0.25);
			}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			   invert =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else
		      throw new IllegalArgumentException(pName);
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		  float x,y;"
	    		+"		  if( __cut_truchetweaving_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT()-0.5;"
	    		+"		     y=RANDFLOAT()-0.5;"
	    		+"		    }"
	    		+"	    float uvx=  __cut_truchetweaving_randomize *sinf( __cut_truchetweaving_randomize *180.0f/PI);"
	    		+"	    float2 u=make_float2(x* __cut_truchetweaving_zoom -uvx,y* __cut_truchetweaving_zoom -uvx);"
	    		+"	    float R3  = 1.732050807f;"
	    		+"	    float2 s = make_float2(1.0f,R3),"
	    		+"	         a = mod(u     ,s)*(2.)-(s),"
	    		+"	         b = mod(u+(s*(.5)),s)*(2.)-(s);"
	    		+"	    u = u/s;"
	    		+"	    float da = dot(a, a);"
	    		+"		float db = dot(b, b);"
	    		+"	    float2 id;"
	    		+ "      if(da < db) "
	    		+"	       id= floorf(u);"
	    		+"       else"
	    		+"       {"
	    		+"	       u= u + 0.5f;"
	    		+"         id=floorf(u) -0.5;"
	    		+"	     }"
	    		+"	    float n = cut_truchetweaving_N21(id);"
	    		+"	    float3 O = cut_truchetweaving_Truchet(fract(u), n, u , __cut_truchetweaving_type,__cut_truchetweaving_width);"
	    		+"	    	    "
	    		+"      float col=0.0f;"
	    		+"	    col=O.z;   "
	    		+"	    "
	    		+"		__doHide=false;"
	    		+"		if( __cut_truchetweaving_invert ==0)"
	    		+"		{"
	    		+"			if (col>0.0f)"
	    		+"			{ x=0.0f;"
	    		+"			  y=0.0f;"
	    		+"			__doHide = true;"
	    		+"			}"
	    		+"		} else"
	    		+"		{"
	    		+"			if (col<=0.0f)"
	    		+"			{ x=0.0f;"
	    		+"			  y=0.0f;"
	    		+"			__doHide = true;"
	    		+"			}"
	    		+"		}"
	    		+"		__px = __cut_truchetweaving * x;"
	    		+"		__py = __cut_truchetweaving * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_truchetweaving * __z;\n" : "");
	  }

	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return  "__device__ float  cut_truchetweaving_N21 (float2 id) {"
	    		+"	 return fract(sinf(id.x*324.23+id.y*5604.342)*87654.53);"
	    		+"}"
	    		+"__device__ float4  cut_truchetweaving_UvCirc (float2 uv, float radius, float thickness) {"
	    		+"		float2 st = make_float2(atan2(uv.x, uv.y), length(uv));"
	    		+"	    "
	    		+"	    float t = thickness/2.;"
	    		+"	    float w = .01;"
	    		+"	    "
	    		+"	    float r1 = radius-t;"
	    		+"	    float r2 = radius+t;"
	    		+"	    "
	    		+"	    float mask = smoothstep(t+w, t, abs(radius-st.y));"
	    		+"	    float alpha = smoothstep(t+.1, t, abs(radius-st.y));"
	    		+"	    alpha = alpha*alpha*mix(.5, 1., mask);"
	    		+"	    "
	    		+"	    return make_float4(st.x*radius, st.y, mask, alpha);"
	    		+"}"
	    		+"__device__ vec4  cut_truchetweaving_UvBeam (float2 uv, float thickness) {"
	    		+"		float t = thickness/2.;"
	    		+"	    float w = .01;"
	    		+"	    float mask = smoothstep(t+w, t, abs(uv.y));"
	    		+"	    float alpha =smoothstep(t+.1, t, abs(uv.y));"
	    		+"	    alpha = alpha*alpha*(.5+.5*mask);"
	    		+"	    "
	    		+"	    return make_float4(uv.x, uv.y, mask,alpha);"
	    		+"}"
	    		+"__device__ float3  cut_truchetweaving_Truchet (float2 uv, float n, float2 u, int type,float width) {"
	    		+"	"
	    		+"	float I3  = 0.333333333f;"
	    		+"	float I6  = 0.166666666f;"
	    		+"	float R3  = 1.732050807f;"
	    		+"	float IR3 = 0.577350269f;"
	    		+"	float HIR3= 0.288675134f;"
	    		+"	float S60 = 0.866025404f;"
	    		+"	float C60 = 0.5f;"
	    		+"	float CURVE_WIDTH = width;"
	    		+"	"
	    		+"	float W111= 0.1f;"
	    		+"	float W113= 0.2f;"
	    		+"	float W122= 0.4f;"
	    		+"	float W223= 0.8f;"
	    		+"	float W333= 1.0f;"
	    		+"   if( type ==1)"
	    		+"	       CURVE_WIDTH = mix(.05, .25, (sinf(u.x+sinf(u.y))+sinf(u.y*.35))*.25+.5);"
	    		+"	"
	    		+"		uv= uv-0.5f;"
	    		+"	    uv.x /= R3;"
	    		+"	    "
	    		+"	 float4 v1 = make_float4(0.0f,0.0f,0.0f,0.0f);"
	    		+"	 float4 v2 = make_float4(0.0f,0.0f,0.0f,0.0f);"
	    		+"	 float4 v3 = make_float4(0.0f,0.0f,0.0f,0.0f);"
	    		+"	    "
	    		+"	    float w = 0.15f;"
	    		+"	    "
	    		+"	    float r = floorf(fract(n*5.)*6.)/6.;"
	    		+"	    r *= 6.28f;"
	    		+"	    float s = sinf(r);"
	    		+"	    float c = cosf(r);"
	    		+"      Mat2 rot;"
	    		+"	    Mat2_Init(&rot,c, -s, s, c);"
	    		+"	    uv = times(&rot,uv);"
	    		+"			"
	    		+"	    if(n<W111) {"
	    		+"	        v1 =  cut_truchetweaving_UvCirc (uv-(make_float2( 0.0f,  I3)), I6, CURVE_WIDTH);"
	    		+"	    	v2 =  cut_truchetweaving_UvCirc (uv-(make_float2( HIR3, -I6)), I6, CURVE_WIDTH);"
	    		+"	    	v3 =  cut_truchetweaving_UvCirc (uv-(make_float2(-HIR3, -I6)), I6, CURVE_WIDTH);"
	    		+"	    }"
	    		+"	    else if(n<W113) {"
	    		+"	        v1 =  cut_truchetweaving_UvCirc (uv-(make_float2(0.0f,  I3)), I6, CURVE_WIDTH);"
	    		+"	        v2 =  cut_truchetweaving_UvCirc (uv-(make_float2(0.0f, -I3)), I6, CURVE_WIDTH);"
	    		+"	        v3 =  cut_truchetweaving_UvBeam (uv, CURVE_WIDTH);"
	    		+"	    }"
	    		+"	    else if(n<W122) {"
	    		+"	        v1 =  cut_truchetweaving_UvCirc (uv-(make_float2(-HIR3, -I6  )),   I6, CURVE_WIDTH);"
	    		+"	        v2 =  cut_truchetweaving_UvCirc (uv-(make_float2(  IR3,  0.0f)), 0.5f, CURVE_WIDTH);"
	    		+"	        v3 =  cut_truchetweaving_UvCirc (uv-(make_float2( HIR3,  0.5f)), 0.5f, CURVE_WIDTH);"
	    		+"	    }"
	    		+"	    else if(n<W223) {"
	    		+"	        v1 =  cut_truchetweaving_UvCirc (uv-(make_float2( IR3, 0.0f)), 0.5f, CURVE_WIDTH);"
	    		+"	        v2 =  cut_truchetweaving_UvCirc (uv-(make_float2(-IR3, 0.0f)), 0.5f, CURVE_WIDTH);"
	    		+"	        v3 =  cut_truchetweaving_UvBeam (uv, CURVE_WIDTH);						"
	    		+"	    } else "
	    		+ "     {"
	    		+"	      Mat2 rot60,rot60i;"
	    		+ "       Mat2_Init(&rot60 ,C60, -S60,  S60, C60);"
	    		+"	      Mat2_Init(&rot60i,C60,  S60, -S60, C60);"
	    		+"	        "
	    		+"	      v1 =  cut_truchetweaving_UvBeam (uv, CURVE_WIDTH);"
	    		+"	 	  v2 =  cut_truchetweaving_UvBeam (times(&rot60 ,uv) , CURVE_WIDTH);"
	    		+"	      v3 =  cut_truchetweaving_UvBeam (times(&rot60i,uv) , CURVE_WIDTH);"
	    		+"	    }"
	    		+"	    "
	    		+"	    float d1 = fract(n*10.0f);"
	    		+"	    "
	    		+"	    float4 v;"
	    		+"      if( d1<.166 )"
	    		+ "          v= mix(v1, mix(v2, v3, v3.w), fmaxf(v2.w, v3.w));"
	    		+"	    if(d1<.333 )"
	    		+"           v= mix(v1, mix(v3, v2, v2.w), fmaxf(v2.w, v3.w));"
	    		+"	    else if(d1<.5   )"
	    		+"           v= mix(v2, mix(v1, v3, v3.w), fmaxf(v1.w, v3.w));"
	    		+"	    else if(d1<.666 )"
	    		+"           v= mix(v2, mix(v3, v1, v1.w), fmaxf(v1.w, v3.w));"
	    		+"	    else if(d1<.833 )"
	    		+"           v= mix(v3, mix(v1, v2, v2.w), fmaxf(v1.w, v2.w));"
	    		+"	    else"
	    		+"           v= mix(v3, mix(v2, v1, v1.w), fmaxf(v1.w, v2.w));"
	    		+"	     return make_float3(v.z,v.z,v.z);"
	    		+"}";
	  }	
}


