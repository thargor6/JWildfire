package org.jwildfire.create.tina.variation;


import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;

public class DC_PoincareDiscFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_poincaredisc
	 * Autor: Jesus Sosa
	 * Date: February 12, 2019
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_MAXITER = "MaxIter";
	private static final String PARAM_N = "N";
	private static final String PARAM_P = "P";
	
	private static final String PARAM_CHECKER0 = "checkers0";
	private static final String PARAM_CHECKER1 = "checkers1";
	private static final String PARAM_CHECKER2 = "checkers2";
	
	private double zoom = 2.0;
	
	int maxiter=10;
	int n=4;
	int p=5;
vec2 mba=new vec2(1.,0.);
vec2 mbb=new vec2(0.,0.);

int checkers0=1;
int checkers1=1;
int checkers2=1;

	double sx;
	double sr;
	vec2 nfp=new vec2(0.0);
	vec2 pcenter=new vec2(0.0);
	vec2 pscale=new vec2(1.0);
	double angle=29.9664;
	
	private static final String[] additionalParamNames = { PARAM_ZOOM,PARAM_MAXITER,
			PARAM_N,PARAM_P,PARAM_CHECKER0,PARAM_CHECKER1,PARAM_CHECKER2};


	void init() {
		double a=Math.PI/(double)(n);
		double b=Math.PI/(double)(p);
		//we should have: 1/n+1/p<1/2
		//otherwise the result is undefined
		if(a+b>=0.5*Math.PI) b=0.5*Math.PI-a-0.01;
		double ca=Math.cos(a), sa=Math.sin(a);
		double sb=Math.sin(b);
		double c1=Math.cos(a+b);
		double s=c1/Math.sqrt(1.-sa*sa-sb*sb);
		sx=(s*s+1.)/(2.*s*ca);
		sr=Math.sqrt(sx*sx-1.);
		nfp=new vec2(sa,-ca);
	}
	
	vec2 mbtpc(vec2 z){//Möbius Transform Peserving (unit) Circle
		vec2 zn=new vec2(z.x*mba.x-z.y*mba.y+mbb.x,
						z.x*mba.y+z.y*mba.x+mbb.y);
		vec2 zd=new vec2(z.x*mbb.x+z.y*mbb.y+mba.x,
						-z.x*mbb.y+z.y*mbb.x-mba.y);
		double idn2=1./G.dot(zd,zd);
		z=new vec2(G.dot(zn,zd),G.dot(zn,new vec2(zd.y,zd.x).multiply(new vec2(-1.,1.)))).multiply(idn2);
		return z;
	}
	
	public double radians(double angle)
	{
		return Math.PI*angle/180.0;
	}
	
	public vec3 getRGBColor(double xp,double yp)
	{
		vec2 z=new vec2(xp,yp).multiply(zoom);
		
		if(G.dot(z,z)>1.) 
			return new vec3(0.);//if you don't want to see the patternes outside the unit disc
		z=mbtpc(z);
		vec2 az=z.plus(1.);
		vec3 fc=new vec3(0.);//captures the number of folds performed
		for(int i=0; i<maxiter && az!=z; i++){
			az=z;
			//first fold
			z.y=G.abs(z.y);
			fc.x+=(az.y!=z.y?1.d:0.d);
			//second fold
			double t=2.*G.min(0.,G.dot(z,nfp));
			fc.y+=(t<0.?1.d:0.d);
			z=z.minus(nfp.multiply(t));
			//third fold
			z.x-=sx;
			double r2=G.dot(z,z);
			double k=G.max(sr*sr/r2,1.);
			fc.z+=(k!=1.?1.d:0.d);
			z=z.multiply(k);
			z.x+=sx;
		}
		
		double r=G.abs(G.length(new vec2(z.x-sx,z.y))-sr);
		r=Math.pow(r,0.25);
		double k=G.mod((checkers0==1?1:0)*fc.x+(checkers1==1?1:0)*fc.y+(checkers2==1?1:0)*fc.z,2.);
		vec3 col1=new	vec3(r).multiply(0.5*k+0.5);

		vec2 ptex=z;
		ptex.y*=k*2.-1.;
		ptex=ptex.multiply(pscale);
		ptex=new mat2(Math.cos(radians(angle)),Math.sin(radians(angle)),-Math.sin(radians(angle)),Math.cos(radians(angle))).times(ptex);
		ptex=ptex.minus(pcenter);
		ptex=ptex.multiply(0.5).plus(0.5);
		//vec3 col= texture2DLod(texture,ptex,0).xyz;
		return col1;
		//return mix(col,col1,fade);
	}

 	
	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) 
	{
      init();
	}

	public String getName() {
		return "dc_poincaredisc";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}

	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { zoom,maxiter,n,p,checkers0,checkers1,checkers2},super.getParameterValues());
	}


	public void setParameter(String pName, double pValue) {
		if (PARAM_ZOOM.equalsIgnoreCase(pName)) 
		{	   zoom =   pValue;

	    }
		else if (pName.equalsIgnoreCase(PARAM_MAXITER)) {
			maxiter = (int)Tools.limitValue(pValue, 0 , 20);
		}
		else if (pName.equalsIgnoreCase(PARAM_N)) {
			n = (int)Tools.limitValue(pValue, 3 , 20);
		}
		else if (pName.equalsIgnoreCase(PARAM_P)) {
			p = (int)Tools.limitValue(pValue, 3 , 20);
		}
		else if (pName.equalsIgnoreCase(PARAM_CHECKER0)) {
			checkers0 = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_CHECKER1)) {
			checkers1 = (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_CHECKER2)) {
			checkers2 = (int)Tools.limitValue(pValue, 0 , 1);
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
	    		+"if( __dc_poincaredisc_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=RANDFLOAT()-0.5;"
	    		+"  y=RANDFLOAT()-0.5;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y)*__dc_poincaredisc_zoom;"
	    		+"color=dc_poincaredisc_getRGBColor(uv,__dc_poincaredisc_N,__dc_poincaredisc_P,__dc_poincaredisc_MaxIter,"
	    		+ "                                    __dc_poincaredisc_checkers0,"
	    		+ "                                    __dc_poincaredisc_checkers1,__dc_poincaredisc_checkers2);"
	    		+"if( __dc_poincaredisc_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
	    		+"   __colorA  = 1.0;"
	    		+"}"
	    		+"else if( __dc_poincaredisc_Gradient ==1 )"
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
	    		+"else if( __dc_poincaredisc_Gradient ==2 )"
	    		+"{"
	    		+"  int3 icolor=dbl2int(color);"
	    		+"  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_poincaredisc*x;"
	    		+"__py+= __dc_poincaredisc*y;"
	    		+"float dz = z * __dc_poincaredisc_scale_z + __dc_poincaredisc_offset_z;"
	    		+"if ( __dc_poincaredisc_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";
	  }
	 public String getGPUFunctions(FlameTransformationContext context) {
		return   "__device__ float  dc_poincaredisc_radians (float angle)"
				+"	{"
				+"		return PI*angle/180.0;"
				+"	}"
			
		   		 +"	__device__ float2  dc_poincaredisc_mbtpc (float2 z){"
				 +"   float2 mba=make_float2(1.,0.);" 
				 +"   float2 mbb=make_float2(0.,0.);"
				 +"		float2 zn=make_float2(z.x*mba.x-z.y*mba.y+mbb.x,"
				 +"						z.x*mba.y+z.y*mba.x+mbb.y);"
				 +"		float2 zd=make_float2(z.x*mbb.x+z.y*mbb.y+mba.x,"
				 +"						-z.x*mbb.y+z.y*mbb.x-mba.y);"
				 +"		float idn2=1.0/dot(zd,zd);"
				 +"		z=make_float2(dot(zn,zd),dot(zn,make_float2(zd.y,zd.x)*(make_float2(-1.,1.))))*idn2;"
				 +"		return z;"
				 +"	}"

				 +"	__device__ float3  dc_poincaredisc_getRGBColor (float2 z, float N, float P, float maxiter, float checkers0, float checkers1, float checkers2)"
				 +"	{"
				 +"	    float2 nfp=make_float2(0.0,0.0);"  
				 +"	    float2 pcenter=make_float2(0.0,0.0);"  
				 +"	    float2 pscale=make_float2(1.0,1.0);"  
				 +"	    float angle=29.9664;"
				 +"		float a=PI/N;"
				 +"		float b=PI/P;"
				 +"		if((a+b)>=0.5*PI)"
				 +"           b=0.5*PI-a-0.01;"
				 +"		float ca=cosf(a), sa=sinf(a);"
				 +"		float sb=sinf(b);"
				 +"		float c1=cosf(a+b);"
				 +"		float s=c1/sqrtf(1.-sa*sa-sb*sb);"
				 +"		float sx=(s*s+1.)/(2.*s*ca);"
				 +"		float sr=sqrtf(sx*sx-1.);"
				 +"		nfp=make_float2(sa,-ca);"
				 
				 +"		if(dot(z,z)>1.0f)"
				 +"			return make_float3(0.,0.0,0.0);"
				 +"		z= dc_poincaredisc_mbtpc (z);"
				 +"		float2 az=z+1.;"
				 +"		float3 fc=make_float3(0.,0.,0.);"
				 +"		for(int i=0; i<maxiter && (az.x!=z.x) && (az.y!=z.y) ; i++){"
				 +"			az=z;"
				 +"			z.y=fabsf(z.y);"
				 +"         float t0=0.0;"
				 +"         if(az.y!=z.y)"
				 +"           t0=1.0;"
				 +"			fc.x+=t0;"
				 +"			float t=2.*fminf(0.,dot(z,nfp));"
				 +"         t0=0.0;"
				 +"         if(t<0.0)"
				 +"             t0=1.0;"
				 +"			fc.y+=t0;"
				 +"			z=z-nfp*t;"
				 +"			z.x -= sx;"
				 +"			float r2=dot(z,z);"
				 +"			float k=fmaxf(sr*sr/r2,1.);"
				 +"         t0=0.0;"
				 +"         if(k!=1.0)"
				 +"             t0=1.0;"
				 +"			fc.z+=t0;"
				 +"			z=z*k;"
				 +"			z.x+=sx;"
				 +"		}"
				 +"		float r=fabsf(length(make_float2(z.x-sx,z.y))-sr);"
				 +"		r=powf(r,0.25);"
				 +"		float k=mod((checkers0==1?1:0)*fc.x+(checkers1==1?1:0)*fc.y+(checkers2==1?1:0)*fc.z,2.);"
				 +"		float3 col1=make_float3(r,r,r)*(0.5*k+0.5);"
				 +"		float2 ptex=z;"
				 +"		ptex.y*=k*2.-1.;"
				 +"		ptex=ptex*(pscale);"
				 +"     Mat2 M;"
				 +"		Mat2_Init(&M,cosf( dc_poincaredisc_radians (angle)),sinf( dc_poincaredisc_radians (angle)),-sinf( dc_poincaredisc_radians (angle)),cosf( dc_poincaredisc_radians (angle)));"
				 +"     ptex=times(&M,ptex);"
				 +"		ptex=ptex-(pcenter);"
				 +"		ptex=ptex*0.5+0.5;"
				 +"		return col1;"
				 +"	}";
	 }	
}

