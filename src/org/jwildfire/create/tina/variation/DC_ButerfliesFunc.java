package org.jwildfire.create.tina.variation;


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



public class DC_ButerfliesFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation : dc_butterflies
	 * Date: october 20, 2019
	 * Author: Jesus Sosa
	 * Reference & Credits :  https://www.shadertoy.com/view/Xd2GRh
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_TIME = "time";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_RED = "red";
	private static final String PARAM_GREEN = "green";
	private static final String PARAM_BLUE = "blue";
	protected static final String PARAM_DC = "ColorOnly";
	private static final String PARAM_GRADIENT = "Gradient";
	protected static final String PARAM_SCALE_Z = "scale_z"; 
	protected static final String PARAM_OFFSET_Z = "offset_z"; 
	protected static final String PARAM_RESET_Z = "reset_z"; 
	
	int seed=1000;

	double time=0.0;
    double zoom=1.0;
    double red=1.;
    double green=1.0;
    double blue=1.0;
	int colorOnly=0;
    int gradient=0;

    double scale_z=0.0;
    double offset_z=0.0;
    int reset_z=1;

	Random randomize=new Random(seed);
	
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
 
 	
  // complex number operations
  vec2 cadd( vec2 a, double s ) {
	  return new vec2( a.x+s, a.y ); 
	  }
  vec2 cmul( vec2 a, vec2 b )  {
	  return new vec2( a.x*b.x - a.y*b.y, a.x*b.y + a.y*b.x );
  }
  vec2 cdiv( vec2 a, vec2 b )  {
	  double d = G.dot(b,b); 
	  return new vec2( G.dot(a,b), a.y*b.x - a.x*b.y ).division(d);
  }
  vec2 csqr( vec2 a ) {
	  return new vec2(a.x*a.x-a.y*a.y, 2.0*a.x*a.y ); 
}
  vec2 csqrt( vec2 z ) {
	  double m = G.length(z);
	  return G.sqrt( new vec2(m+z.x, m-z.x).multiply(0.5) ).multiply(new vec2( 1.0, G.sign(z.y) )); 
}
  vec2 conj( vec2 z )
  { 
	  return new vec2(z.x,-z.y); 
  }
  vec2 cpow( vec2 z, double n ) { 
	  double r = G.length( z );
	  double a = G.atan2( z.y, z.x ); 
	  return new vec2( Math.cos(a*n), Math.sin(a*n) ).multiply(Math.pow( r, n )); 
}
  vec2 cexp( vec2 z) {
	  return new vec2( Math.cos(z.y), Math.sin(z.y) ).multiply(Math.exp( z.x )); 
}
  vec2 clog( vec2 z) {
	  return new vec2( 0.5*Math.log(z.x*z.x+z.y*z.y), G.atan2(z.y,z.x)); 
  }
  vec2 csin( vec2 z) { 
	  double r = Math.exp(z.y);
	  return new vec2((r+1.0/r)*Math.sin(z.x),(r-1.0/r)*Math.cos(z.x)).multiply(0.5);
  }
  vec2 ccos( vec2 z) {
	  double r = Math.exp(z.y);
	  return new vec2((r+1.0/r)*Math.cos(z.x),-(r-1.0/r)*Math.sin(z.x)).multiply(0.5);
  }
 
  vec2 z0;
  vec2 f( vec2 x ){
	  return csin(cpow(x.plus(z0),-4.)).minus( x.multiply(0.9*(1.0+2.0*Math.sin(0.1*time)))).plus( z0)  ;
	}
 	

	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_TIME,PARAM_ZOOM, PARAM_RED,PARAM_GREEN,PARAM_BLUE,PARAM_DC ,PARAM_GRADIENT,PARAM_SCALE_Z,PARAM_OFFSET_Z,PARAM_RESET_Z};
	 	
	public RGBColor findKey(RGBPalette palette, int p_red,int	p_green,int p_blue){
	    int diff=1000000000; 
        RGBColor ret=new RGBColor();
        for (int i = 0; i < 256; i++)
        {
        	RGBColor color=palette.getColor(i);
        	int red=color.getRed();
        	int green=color.getGreen();
        	int blue=color.getBlue();

        	int dvalue= distance(p_red,p_green,p_blue,red,green,blue);
        	if (diff >dvalue) {
        		diff = dvalue;
        		ret = new RGBColor(red,green,blue);
        	}
        }
	    return ret;
	    }

	public int distance(int p_red,int p_green,int p_blue,int red,int green,int blue)
	{
    
    int dist_r = Math.abs(p_red - red);
    int dist_g = Math.abs(p_green - green);
    int dist_b = Math.abs(p_blue - blue);
    int dist_3d_sqd = (dist_r * dist_r) + (dist_g * dist_g) + (dist_b * dist_b);
    return dist_3d_sqd;
}
 	public int[] dbl2int(vec3 theColor)
  	{
  		int[] color=new int[3];

  		color[0] =  Math.max(0, Math.min(255, (int)Math.floor(theColor.x * 256.0D)));
  		color[1] =  Math.max(0, Math.min(255, (int)Math.floor(theColor.y * 256.0D)));
  		color[2] =  Math.max(0, Math.min(255, (int)Math.floor(theColor.z * 256.0D)));
  		return color;
  	}
 	
	public double greyscale(int r,int g,int b)
	{
		int lum,red,green,blue;
	  red = (int)(r * 0.299);         
			  green = (int)(g * 0.587);         
			  blue = (int)(b * 0.114);    
			  lum = red + green + blue;    
			  return (double)lum/255;
	}
 	
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		double xp,yp;


	     if(colorOnly==1)
		 {
			xp=pAffineTP.x;
			yp=pAffineTP.y;
		 }
		 else
		 {
			xp=pContext.random()-0.5;
			yp=pContext.random()-0.5;
		}
		
		double range =4.0;	
		vec2 p = new vec2(xp*zoom,yp*zoom);
		// vec2 p = (q.multiply(range)).plus(-5.0*range);

		// iterate		

		vec2 z = p;
		z0=p;
		double g = 1e10;
		double k=100.0;

		double dz;

		for(int i=0; i<100; i++ )
		{
			vec2 prevz=z;
			// function		
			z = f( z );
			g = Math.min( g, G.dot(z.minus(1.0),z.minus(1.0)) );
			// bailout
			dz = G.dot(z.minus(prevz),z.minus(prevz));		
			if( dz<0.00001 ){
				k = dz/0.00001;
				z = z.multiply(k).plus(prevz.multiply(1.0-k));
				k= k+ (double)i;
				break;
			}
			if( dz>10000.0 ){
				k = 10000.0/dz;
				z = z.multiply(k).plus(prevz.multiply((1.0-k)));
				k= k+(double)i;
				break;
			} 
		}
		// double it = 1.0-k/100.0;
		vec3 color = G.sin(new vec3(red,green,blue).plus(2.3+Math.log(g*Math.abs(z.y*z.x)))).multiply(0.5).plus(0.5); 

		int[] tcolor=new int[3];
		tcolor=dbl2int(color);
		

      
        
        //z by color (normalized)
        double col=greyscale(tcolor[0],tcolor[1],tcolor[2]);
        
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
          	    RGBColor scol=findKey(palette,tcolor[0],tcolor[1],tcolor[2]);
          	    
          	  pVarTP.rgbColor  =true;;
          	  pVarTP.redColor  =scol.getRed();
          	  pVarTP.greenColor=scol.getGreen();
          	  pVarTP.blueColor =scol.getBlue();

        }
        else if(gradient==2)
        {
        	pVarTP.color=col;
        }


		
		pVarTP.x = pAmount * (xp);
		pVarTP.y = pAmount * (yp);
		
//		if (pContext.isPreserveZCoordinate()) {
//			pVarTP.z += pAmount * pAffineTP.z;
//		}
		
	    double delta_z = col * scale_z + offset_z;
	    if (reset_z == 1) {
	      pVarTP.z = delta_z;
	    }
	    else {
	      pVarTP.z += delta_z;
	    }
	}
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
	   }

	  
	public String getName() {
		return "dc_butterflies";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed, time,zoom,red,green,blue,colorOnly,gradient,scale_z,offset_z,reset_z});
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_TIME)) {
//	          long current_time = System.currentTimeMillis();
//	          elapsed_time += (current_time - last_time);
//	          last_time = current_time;
//	          time = (double) (elapsed_time / 1000.0);
	          time=pValue;
		}
		else if(pName.equalsIgnoreCase(PARAM_SEED))
		{
			   seed =   (int)pValue;
		       randomize=new Random(seed);
		          long current_time = System.currentTimeMillis();
		          elapsed_time += (current_time - last_time);
		          last_time = current_time;
		          time = (double) (elapsed_time / 1000.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_RED)) {
			red  =Tools.limitValue(pValue, -1.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_GREEN)) {
			green  =Tools.limitValue(pValue, -1.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_BLUE)) {
			blue  =Tools.limitValue(pValue, -1.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_DC)) {
			colorOnly = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_GRADIENT)) {
			gradient =(int)Tools.limitValue(pValue, 0 , 2);
		}
		else if (pName.equalsIgnoreCase(PARAM_SCALE_Z)) {
			scale_z =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_OFFSET_Z)) {
			offset_z =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_RESET_Z)) {
			reset_z  =(int)Tools.limitValue(pValue, 0 , 1);
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "float x,y;"
	    		+"float3 color=make_float3(1.0,1.0,1.0);"
	    		+"float z=0.5;"
	    		+"if( __dc_butterflies_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_butterflies_zoom;"
	    		+"color=dc_butterflies_getRGBColor(uv,__dc_butterflies_time,__dc_butterflies_red,__dc_butterflies_green,__dc_butterflies_blue);"
	    		+"if( __dc_butterflies_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_butterflies_Gradient ==1 )"
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
	    		+"else if( __dc_butterflies_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_butterflies*x;"
	    		+"__py+= __dc_butterflies*y;"
	    		+"float dz = z * __dc_butterflies_scale_z + __dc_butterflies_offset_z;"
	    		+"if ( __dc_butterflies_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {
		 return  "__device__  float2  dc_butterflies_cadd ( float2 a, float s ) {"
				 +"	  return make_float2( a.x+s, a.y ); "
				 +"	  }"
				 
				 +"__device__  float2  dc_butterflies_cmul ( float2 a, float2 b )  {"
				 +"	  return make_float2( a.x*b.x - a.y*b.y, a.x*b.y + a.y*b.x );"
				 +"  }"
				 
				 +"__device__  float2  dc_butterflies_cdiv ( float2 a, float2 b )  {"
				 +"	  float d = dot(b,b); "
				 +"	  return make_float2( dot(a,b), a.y*b.x - a.x*b.y )/(d);"
				 +"  }"
				 
				 +"__device__  float2  dc_butterflies_csqr ( float2 a ) {"
				 +"	  return make_float2(a.x*a.x-a.y*a.y, 2.0*a.x*a.y ); "
				 +"}"
				 
				 +"__device__  float2  dc_butterflies_csqrt ( float2 z ) {"
				 +"	  float m = length(z);"
				 +"	  return sqrt( make_float2(m+z.x, m-z.x)*(0.5) )*(make_float2( 1.0, sign(z.y) )); "
				 +"}"
				 
				 +"__device__  float2  dc_butterflies_conj ( float2 z )"
				 +"  { "
				 +"	  return make_float2(z.x,-z.y); "
				 +"  }"
				 
				 +"__device__  float2  dc_butterflies_cpow ( float2 z, float n ) { "
				 +"	  float r = length( z );"
				 +"	  float a = atan2( z.y, z.x ); "
				 +"	  return make_float2( cosf(a*n), sinf(a*n) )*(pow( r, n )); "
				 +"}"
				 
				 +"__device__ float2  dc_butterflies_cexp ( float2 z) {"
				 +"	  return make_float2( cosf(z.y), sinf(z.y) )*(exp( z.x )); "
				 +"}"
				 
				 +"__device__  float2  dc_butterflies_clog ( float2 z) {"
				 +"	  return make_float2( 0.5*logf(z.x*z.x+z.y*z.y), atan2(z.y,z.x)); "
				 +"  }"
				 
				 +"__device__  float2  dc_butterflies_csin ( float2 z) { "
				 +"	  float r = exp(z.y);"
				 +"	  return make_float2((r+1.0/r)*sinf(z.x),(r-1.0/r)*cosf(z.x))*(0.5);"
				 +"  }"
				 
				 +"__device__  float2  dc_butterflies_ccos ( float2 z) {"
				 +"	  float r = exp(z.y);"
				 +"	  return make_float2((r+1.0/r)*cosf(z.x),-(r-1.0/r)*sinf(z.x))*(0.5);"
				 +"  }"
				 
				 +"__device__  float2  dc_butterflies_func ( float2 x ,float time, float2 z0 ){"
				 +"	  return  dc_butterflies_csin ( dc_butterflies_cpow (x+(z0),-4.))-( x*(0.9*(1.0+2.0*sinf(0.1*time))))+( z0)  ;"
				 +"	}"
				 
				 +"__device__  float3 dc_butterflies_getRGBColor  (float2 uv, float time, float red, float green, float blue){"
				 +"     float range =4.0;"
				 +"		float2 z = uv;"
				 +"		float2 z0=uv;"
				 +"		float g = 1.0e10;"
				 +"		float k=100.0;"
				 +"		float dz;"
				 +"		for(int i=0; i<100; i++ )"
				 +"		{"
				 +"			float2 prevz=z;"
				 +"			"
				 +"			z = dc_butterflies_func ( z, time, z0 );"
				 +"			g = fminf( g, dot(z-(1.0),z-(1.0)) );"
				 +"			"
				 +"			dz = dot(z-(prevz),z-(prevz));		"
				 +"			if( dz<0.00001 ){"
				 +"				k = dz/0.00001;"
				 +"				z = z*(k)+(prevz*(1.0-k));"
				 +"				k= k+ (float)i;"
				 +"				break;"
				 +"			}"
				 +"			if( dz>10000.0 ){"
				 +"				k = 10000.0/dz;"
				 +"				z = z*(k)+(prevz*((1.0-k)));"
				 +"				k= k+(float)i;"
				 +"				break;"
				 +"			} "
				 +"		}"
				 +"		"
				 +"		float3 color = sinf(make_float3(red,green,blue)+(2.3+logf(g*abs(z.y*z.x))))*(0.5)+(0.5); "
				 +"	    return color;"
				 +"}";
	 }	
}

