package org.jwildfire.create.tina.variation;



import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;

public class DC_PentaTilesFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_sincos
	 * Autor: Jesus Sosa
	 * Date: February 13, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_BORDER = "border";
	private static final String PARAM_WIDTH = "width";




	double zoom=20.0;
	int  border=1;
	double width=0.1;

	
	

	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_BORDER,PARAM_WIDTH};


	// inspired by http://www.ianislallemand.com/projects/design/generative-tilings
	// http://mathworld.wolfram.com/PentagonTiling.html

	//#define S(v) smoothstep(3., 0., abs(v)/fwidth(v))
	public double s(double v)
	{
		return G.smoothstep(width, 0., v);                    // draw AA region v<0
	}
	
	public double S(double v)
	{
		return s(G.abs(v));                               // draw AA line v=0
	}
	
	public double l(double x,double y,double a,double b)
	{
		return G.dot( new vec2(x,y), G.normalize(new vec2(a,b)) ); // line equation
	}
	
	public double L(double x,double y,double a,double b)
	{
		return S(l(x,y,a,b));                     // draw line equation
	}
	
	//#define P(x,y,a,b) s(l(x,y,a,b))                   // draw region under line
	public double P(double x,double y,double a,double b)
	{
		return G.step(l(x,y,a,b),0.);               // draw region under line
	}

	
	public vec3 getRGBColor(double xp,double yp)
	{

	    
		vec2 U=new vec2(xp,yp).multiply(zoom);

	    vec3 O=new vec3(0.);// n  

		double h = (Math.sqrt(3.)+1.)/2.; 
		double c = Math.sqrt(3.)-1.;
		double r=h+c/2.; // NB: r = sqrt(3)
		double e =  width;
		double b, i,v;
		
		    vec2 T = G.mod(U, r+r).minus(r);
		    vec2 V = G.abs(T);              // first tile in bricks
		                                                     // center = 1 supertile, corners = 1/4 neighboor supertiles

		    O.r = P( V.x, V.y-h, -(c/2.-h), r );             // id 0-7 in supertile
		    O.g = P( V.x-c/2., V.y, -Math.sqrt(3.), 1 );
		    i = G.step(.5,O.r) + 2.*G.step(.5,O.g);
	//	    O.b=    ((i==1. && T.y>0.)?1.:0.)               // <><> not antialiased
	//	            || ((i==3. && T.x>0.)?1.:0.)                // -> re#def P()
	//	            || ((i==0. && T.x>0.)?1.:0.)
	//	            || ((i==2. && T.y>0.)?1.:0.);
            O.b=1.;
		    U = G.floor(U.division((r+r))).multiply( 2.);                          // supertile id
		    if (O.r==0.) U = U.plus(G.sign(T));
		    //O = vec4(U,0,0)/4.;

		    i = (O.r + 2.*O.g + 4.*O.b) + 8.*(U.x + 12.*U.y); // tile id
		  //i = (O.r + 2.*O.g + 4.*O.b)*8.1 +(U.x + 12.*U.y); 
		  //i = O.r + 1.7*O.g + 4.3*O.b + 8.7* (U.x + 12.7*U.y);
		    O = G.cos( new vec3(0,23,21).plus(i) ).multiply(0.6).plus(0.6);      // -> color
		    O =O.multiply( .4+.6*G.fract(1234.*Math.sin(43.*i)));

		    if(border==1)
		    { 
			    b = (double)(  S(V.x)   * G.step(h,V.y)               // tiles border
			              + S(r-V.x) * G.step(V.y,c/2.) 
			              + L( V.x, V.y-h, -(c/2.-h), r )
			              + S(V.y)   * G.step(V.x,c/2.)
			              + S(r-V.y)  * G.step (r-V.x,c/2.)
			              + L( V.x-c/2., V.y, -Math.sqrt(3.), 1 )
			             );
		        O = O.multiply(1.-b);                                      // draw border
		    }

		return O;
	}
 	

	

	public String getName() {
		return "dc_pentatiles";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,border,width},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 1.0 , 100.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_BORDER)) {
			border =(int) Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_WIDTH)) {
			width =Tools.limitValue(pValue, 0.01 , 0.25);
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
	    		+"if( __dc_pentatiles_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_pentatiles_zoom;"
	    		+"color=dc_pentatiles_getRGBColor(uv,__dc_pentatiles_width,__dc_pentatiles_border);"
	    		+"if( __dc_pentatiles_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_pentatiles_Gradient ==1 )"
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
	    		+"else if( __dc_pentatiles_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_pentatiles*x;"
	    		+"__py+= __dc_pentatiles*y;"
	    		+"float dz = z * __dc_pentatiles_scale_z + __dc_pentatiles_offset_z;"
	    		+"if ( __dc_pentatiles_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {
		 return   "	__device__ float  dc_pentatiles_sfun (float v, float width)"
				 +"{"
				 +"		return smoothstep(width, 0., v);"
				 +"}"

				 +"	__device__ float  dc_pentatiles_Sfun (float v, float width)"
				 +"	{"
				 +"		return  dc_pentatiles_sfun (abs(v),width);"
				 +"	}"

				 +"	__device__ float  dc_pentatiles_lfun  (float x,float y,float a,float b)"
				 +"	{"
				 +"		return dot( make_float2(x,y), normalize(make_float2(a,b)) );"
				 +"	}"

				 +"	__device__ float  dc_pentatiles_Lfun (float x,float y,float a,float b, float width)"
				 +"	{"
				 +"		return  dc_pentatiles_Sfun (dc_pentatiles_lfun(x,y,a,b), width);"
				 +"	}"

				 +"	__device__ float  dc_pentatiles_Pfun (float x,float y,float a,float b,float width)"
				 +"	{"
				 +"		return step(dc_pentatiles_lfun (x,y,a,b),0.);"
				 +"	}"

				 +"	__device__ float3  dc_pentatiles_getRGBColor (float2 U, float width,float border)"
				 +"	{"
				 +"	    float3 O=make_float3(0.,0.0,0.0);"
				 +"		float h = (sqrtf(3.)+1.)/2.; "
				 +"		float c = sqrtf(3.)-1.;"
				 +"		float r=h+c/2.; "
				 +"		float e =  width;"
				 +"		float b, i,v;"
				 +"		float2 T = mod(U, r+r)-r;"
				 +"		float2 V = abs(T);"
				 +"		O.x =  dc_pentatiles_Pfun ( V.x, V.y-h, -(c/2.-h), r ,width);"
				 +"		O.y =  dc_pentatiles_Pfun ( V.x-c/2., V.y, -sqrtf(3.), 1.0, width );"
				 +"		i = step(.5,O.x) + 2.*step(.5,O.y);"
				 +"     O.z=1.;"
				 +"		    U = floorf(U/((r+r)))*( 2.);"
				 +"		    if (O.x==0.0) U = U+(sign(T));"
				 +"		    i = (O.x + 2.*O.y + 4.*O.z) + 8.*(U.x + 12.*U.y); "
				 +"		    O = cosf( make_float3(0.,23.,21.) + i )*0.6+0.6;"
				 +"		    O =O*( .4+.6*fract(1234.*sinf(43.*i)));"
				 +"		    if(border==1.0)"
				 +"		    { "
				 +"			    b =       (  dc_pentatiles_Sfun (V.x,width)   * step(h,V.y)"
				 +"			              +  dc_pentatiles_Sfun (r-V.x,width) * step(V.y,c/2.) "
				 +"			              +  dc_pentatiles_Lfun ( V.x, V.y-h, -(c/2.-h), r ,width)"
				 +"			              +  dc_pentatiles_Sfun (V.y  ,width)  * step(V.x   ,c/2.)"
				 +"			              +  dc_pentatiles_Sfun (r-V.y,width)  * step (r-V.x,c/2.)"
				 +"			              +  dc_pentatiles_Lfun ( V.x-c/2. , V.y, -sqrtf(3.), 1.0 ,width)"
				 +"			             );"
				 +"		        O = O*(1.-b);                                      "
				 +"		    }"
				 +"		return O;"
				 +"	}";
	 }	
}

