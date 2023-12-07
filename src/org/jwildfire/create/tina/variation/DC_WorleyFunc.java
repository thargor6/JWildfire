package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.sin;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;

public class DC_WorleyFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_worley
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_PATTERN = "pattern";
    private static final String PARAM_DISTORT = "distort";

	double zoom=7.0;
	double pattern = 70.0;
	double distort = 1.0;
	
	private static final String[] additionalParamNames = { PARAM_ZOOM, PARAM_PATTERN, PARAM_DISTORT};

	vec2 H(vec2 n)
	{
	    return G.fract( new vec2(1,12.34).plus(1e4 * sin( n.x+n.y/(pattern*.01))  ) ).multiply(distort*0.75).plus(.3);
	}

	public vec3 getRGBColor(double xp,double yp)
	{

	    
		vec2 U=new vec2(xp,yp).multiply(zoom);
	    
	    vec2 p;
	    vec2 c;
	    vec3 O = new vec3(0.0);

	    double l;
	    
	    O = O.plus(1e9).minus(O);  // --- Worley noise: return O.xyz = sorted distance to first 3 nodes
	    
	    for (int k=0; k<9; k++) // replace loops i,j: -1..1
	    { // windows Angle bug with ,, instead of {}
	                p = G.ceil(U).plus( new vec2(k-k/3*3,k/3)).minus(2.); // cell id = floor(U)+vec2(i,j)
	                c = H(p).plus( p).minus(U);
	                l = G.dot(c , c);        // distance^2 to its node
	                
	                if ( l < O.x ) { 
	                	O.y=O.x;
	                    O.z=O.y;
	                    O.x=l;       // ordered 3 min distances
	                }
	                else if( l < O.y ) 
	    	        { O.z =O.y ;
	    	          O.y=l; 
	    	        }else if(l < O.z )
	                    O.z=l; 
//	                  else
//	            	     l;
	    }
	    O = G.sqrt(O).multiply(5.); 
	    
	    
	    // --- smooth distance to borders and nodes
	    
	 // l = 1./(1./(O.y-O.x)+1./(O.z-O.x)); // Formula (c) Fabrice NEYRET - BSD3:mention author.
	 // O += smoothstep(.0,.3, l-.5) -O;
	    O = O.minus(O.x);
	    O = O.plus(4.*( O.y/(O.y/O.z+1.) - .5 )).minus(O);  // simplified form


		return O;
	}
 	

	

	public String getName() {
		return "dc_worley";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom, pattern, distort },super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_PATTERN)) {
		  pattern = Tools.limitValue(pValue, 0.1 , 100.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_DISTORT)) {
		  distort = Tools.limitValue(pValue, 0.0 , 1.0);
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
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,0.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_worley_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_worley_zoom;"
	    		+"color=dc_worley_getRGBColor(uv, __dc_worley_pattern, __dc_worley_distort);"
	    		+"if( __dc_worley_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_worley_Gradient ==1 )"
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
	    		+"else if( __dc_worley_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_worley*x;"
	    		+"__py+= __dc_worley*y;"
	    		+"float dz = z * __dc_worley_scale_z + __dc_worley_offset_z;"
	    		+"if ( __dc_worley_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {

		 return   "__device__  float2  dc_worley_H (float2 n, float pattern, float distort)"
				 +"	{"
				 +"	    return fract(  make_float2(1.0,12.34) + 1.0e4 * sin( n.x+n.y/(pattern*.01)) )*distort*0.75 + 0.3;"
				 +"	}"
				 
				 +"	__device__ float3 dc_worley_getRGBColor (float2 U,float pattern, float distort)"
				 +"	{"
				 +"	    float2 p;"
				 +"	    float2 c;"
				 +"	    float3 O = make_float3(0.0,0.0,0.0);"
				 +"	    float l;"
				 +"	    O = O + 1.0e9- O;"
				 +"	    for (int k=0; k<9; k++)"
				 +"	    { "
				 +"	                p = ceil(U)+ make_float2( k-k/3*3 ,k/3)-2.0;"
				 +"	                c =  dc_worley_H (p,pattern,distort) + p -U;"
				 +"	                l = dot(c , c);"
				 +"	                if ( l < O.x ) { "
				 +"	                	O.y=O.x;"
				 +"	                    O.z=O.y;"
				 +"	                    O.x=l;"
				 +"	                }"
				 +"	                else if( l < O.y )"
				 +"	    	        { O.z =O.y ;"
				 +"	    	          O.y=l; "
				 +"	    	        }"
				 +"                 else if(l < O.z )"
				 +"	                  O.z=l; "
				 +"	    }"
				 +"	    O = sqrt(O)*5.0;"
				 +"	    O = O- O.x;"
				 +"	    O = O+4.*( O.y/ (O.y/O.z +1.) - 0.5 )- O;"
				 +"		return O;"
				 +"	}";
	 }	
}

