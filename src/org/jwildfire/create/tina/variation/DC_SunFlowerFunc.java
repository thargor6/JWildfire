package org.jwildfire.create.tina.variation;



import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;

public class DC_SunFlowerFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_sincos
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_STEP = "step";
	private static final String PARAM_N = "N";
	private static final String PARAM_POLAR = "Polar";
	private static final String PARAM_DOTS = "Dots";
	private static final String PARAM_GRIDX = "GridX";
	private static final String PARAM_GRIDY = "GridY";



	double zoom=7.0;
	double Step=0.1;
	int N=30;
	int  Polar=1;
	int  Dots=1;
	int  GridX=1;
	int  GridY=1;

	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_STEP,PARAM_N,PARAM_POLAR,PARAM_DOTS,PARAM_GRIDX,PARAM_GRIDY};


	


	public double S(double d,double r)
	{
		return G.smoothstep( r, 0., d); // antialiased draw. r >= 1.5
	}

	double line(vec2 p, vec2 a,vec2 b) {  // https://www.shadertoy.com/view/4dcfW8
	    p = p.minus(a);
	    b = b.minus(a);
		double h = G.clamp(G.dot(p, b) / G.dot(b, b), 0., 1.); // proj coord on line
		return G.length(p.minus(b.multiply( h)));                       // dist to segment
	}

	public double L(vec2 U, double x,double y)
	{ 
		return line( U, G.floor(U.plus(.5)).minus(new vec2(x,y)), G.floor(U.plus(.5)).plus(new vec2(x,y)));
	}

	public vec3 getRGBColor(double xp,double yp)
	{

	    
		vec2 U=new vec2(xp,yp).multiply(zoom);
        vec2 J=new vec2(1.0);

	    double r = Step, y, l, w; 
	    
	    vec3 O=new vec3(0.);// n  

	    l = G.length(U);
	    J.x = l*6.28;                    // jacobian for dots
	    U = new vec2( G.atan2(U.y,U.x)/6.28 , l ).multiply(N);          // polar coords
	      
	//    w = G.length(G.fwidth(new vec2( U.x + r*U.y, U.y) ));      // jacobian for lines width
	    w=.1;
	    U.x += r* G.floor(U.y+.5);                        // dot offsetting 
	    if(GridX==1)
	      O.x  = S( L(U,((r<0)?-1.:1.)-r,1), w );
	    if(GridY==1)
	       O.y  = S( L(U,  -r,1), w );
	   
	    if(GridX==0 && GridY==0)
	    {
	      O.z  = S( L(U,((r<0)?-.5:.5)-r,1), w );
	      U.x+=.5; O.z  += S( L(U,((r<0)?-.5:.5)-r,1), w ); U.x-=.5;
	    }
	    if(Polar==1)
	         O = O.plus(.5* S( L(U,1,0),    w ));
	    if(Dots==1)
	    {
	      U = G.fract( U.plus(.5) ).minus(.5);                         // draw dots
	      O = O.plus(S( G.length(U.multiply(J))-.3, N*2./2000. ));
	    }
		return O;
	}
 	

	

	public String getName() {
		return "dc_sunflower";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,Step,N,Polar,Dots,GridX,GridY},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_STEP)) {
			Step =Tools.limitValue(pValue, -1.00 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_N)) {
			N = (int)pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_POLAR)) {
			Polar = (int)Tools.limitValue(pValue, 0 , 1);;
		}
		else if (pName.equalsIgnoreCase(PARAM_DOTS)) {
			Dots = (int)Tools.limitValue(pValue, 0 , 1);;
		}
		else if (pName.equalsIgnoreCase(PARAM_GRIDX)) {
			GridX = (int)Tools.limitValue(pValue, 0 , 1);;
		}
		else if (pName.equalsIgnoreCase(PARAM_GRIDY)) {
			GridY = (int)Tools.limitValue(pValue, 0 , 1);;
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
	    		+"if( __dc_sunflower_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_sunflower_zoom;"
	    		+"color=dc_sunflower_getRGBColor(uv,__dc_sunflower_N, __dc_sunflower_step, __dc_sunflower_GridX,"
	    		+ "    __dc_sunflower_GridY, __dc_sunflower_Polar, __dc_sunflower_Dots);"
	    		+"if( __dc_sunflower_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_sunflower_Gradient ==1 )"  
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
	    		+"else if( __dc_sunflower_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_sunflower*x;"
	    		+"__py+= __dc_sunflower*y;"
	    		+"float dz = z * __dc_sunflower_scale_z + __dc_sunflower_offset_z;"
	    		+"if ( __dc_sunflower_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {
		 return   "	__device__ float  dc_sunflower_S (float d,float r)"
				 +"	{"
				 +"		return smoothstep( r, 0., d); "
				 +"	}"
				 
				 +"	__device__ float  dc_sunflower_line (float2 p, float2 a,float2 b) {  "
				 +"	    p = p-a;"
				 +"	    b = b-a;"
				 +"		float h = clamp(dot(p, b) / dot(b, b), 0.0, 1.0); "
				 +"		return length(p - b*h);"
				 +"	}"
				 
				 +"	__device__ float  dc_sunflower_L (float2 U, float x, float y)"
				 +"	{ "
				 +"		return  dc_sunflower_line ( U, floorf(U + 0.5)-make_float2(x,y), floorf(U+ 0.5)+make_float2(x,y));"
				 +"	}"
				 
				 +"	__device__ float3  dc_sunflower_getRGBColor (float2 U, float N, float step, float GridX, float GridY, float Polar, float Dots)"
				 +"	{"
				 +"     float2 J=make_float2(1.0,1.0);"
				 +"	    float r = step, y, l, w;"
				 +"	    float3 O=make_float3(0.,0.,0.);"
				 +"	    l = length(U);"
				 +"	    J.x = l*6.28;                    "
				 +"	    U = make_float2( atan2f(U.y,U.x)/6.28 , l )*N;"
				 +"	    w=0.1;"
				 +"	    U.x += r* floorf(U.y+.5);"
				 +"	    if(GridX==1.0)"
				 +"     { float val=((r<0)?-1.:1.);"
				 +"       float val1=dc_sunflower_L (U , val - r,1.0);"
				 +"	      O.x  =  dc_sunflower_S ( val1, w );"
				 +"     }"
				 +"	    if(GridY==1.0)"
				 +"     {  float val=dc_sunflower_L (U,  -r,1.0);"
				 +"	       O.y  =  dc_sunflower_S (val , w );"
				 +"     }"
				 +"	    if(GridX==0.0 && GridY==0.0)"
				 +"	    {"
				 +"	      O.z  =  dc_sunflower_S (  dc_sunflower_L (U,((r<0)?-.5:.5)-r,1), w );"
				 +"	      U.x+=.5;"
				 +"       O.z  +=  dc_sunflower_S (  dc_sunflower_L (U,((r<0)?-.5:.5)-r,1), w ); U.x-=.5;"
				 +"	    }"
				 +"	    if(Polar==1.0)"
				 +"	         O = O+(.5*  dc_sunflower_S (  dc_sunflower_L (U,1,0),    w ));"
				 +"	    if(Dots==1.0)"
				 +"	    {"
				 +"	      U = fract( U+(.5) )-(.5);"
				 +"	      O = O+( dc_sunflower_S ( length(U*J)-0.3, N*2./2000. ));"
				 +"	    }"
				 +"		return O;"
				 +"	}";
	 }	
}

