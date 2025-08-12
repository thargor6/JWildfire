package org.jwildfire.create.tina.variation;




import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CutTrianTessFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :cut_triantess
	 * Date: october 17, 2019
	 * Author: Jesus Sosa
	 * Reference & Credits:  https://www.shadertoy.com/view/4sf3zX
	 */


	private static final long serialVersionUID = 1L;


	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ITERATIONS = "Iters";
	private static final String PARAM_SRADIUS = "SRadius";
	private static final String PARAM_PPARAM = "pParam";
	private static final String PARAM_QPARAM = "qParam";
	private static final String PARAM_RPARAM = "rParam";
	private static final String PARAM_UPARAM = "uBaryc";
	private static final String PARAM_VPARAM = "vBaryc";
	private static final String PARAM_WPARAM = "wBaryc";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	

	int mode=1;

	int Iterations=20;
	
    double zoom=2.0;
    int invert=0;


 	

    
    double SRadius=0.01;//Thikness of the lines
    
  //these are the p, q and r parameters that define the coxeter/triangle group
     int pParam=3;// Pi/p: angle beween reflexion planes a and b .
     int qParam=3;// Pi/q: angle beween reflexion planes b and c .
     int rParam=4;// Pi/r: angle beween reflexion planes c and a .
    
 // U,V,W are the 'barycentric' coordinate for the vertex.
    double U=1.;
    double V=1.;
    double W=0.;
    
    vec3 nb,nc,p,q;
	vec3 pA,pB,pC;
    
	
	double spaceType=0.;
	double aaScale = 0.0005;//anti-aliasing scale == half of pixel size.
	
	
	//Colors
	double segColor=0.0;
	double backGroundColor=1.0;
	
	double hdott(vec3 a, vec3 b){//dot product for "time like" vectors.
		return G.dot(new vec2(a.x,a.y),new vec2(b.x,b.y))*(spaceType) + a.z*b.z;
	}
	
	double hdots(vec3 a, vec3 b){//dot product for "space like" vectors (these are duals of the "time like" vectors).
		return G.dot(new vec2(a.x,a.y),new vec2(b.x,b.y))+spaceType*a.z*b.z;
	}
	double hlengtht(vec3 v){
		return Math.sqrt(G.abs(hdott(v,v)));
	}
	double hlengths(vec3 v){
		return Math.sqrt(G.abs(hdots(v,v)));
	}

	vec3 hnormalizet(vec3 v){//normalization of "time like" vectors.
		double l=1./hlengtht(v);
		return v.multiply(l);
	}
	
	void init()
{
		spaceType=(double)(G.sign(qParam*rParam+pParam*rParam+pParam*qParam-pParam*qParam*rParam));//1./pParam+1./qParam+1./rParam-1.;

		double cospip=Math.cos(Math.PI/(double)(pParam)), sinpip=Math.sin(Math.PI/(double)(pParam));
		double cospiq=Math.cos(Math.PI/(double)(qParam)), sinpiq=Math.sin(Math.PI/(double)(qParam));
		double cospir=Math.cos(Math.PI/(double)(rParam)), sinpir=Math.sin(Math.PI/(double)(rParam));
		double ncsincos=(cospiq+cospip*cospir)/sinpip;

		//na is simply vec3(1.,0.,0.).
		nb=new vec3(-cospip,sinpip,0.);
		nc=new vec3(-cospir,-ncsincos,G.sqrt(G.abs((ncsincos+sinpir)*(-ncsincos+sinpir))));

		if(spaceType==0.){//This case is a little bit special
			nc.z=0.25;
		}

		pA=new vec3(nb.y*nc.z,-nb.x*nc.z,nb.x*nc.y-nb.y*nc.x);
		pB=new vec3(0.,nc.z,-nc.y);
		pC=new vec3(0.,0.,nb.y);

		q=pA.multiply(U).plus(pB.multiply(V)).plus(pC.multiply(W));
		p=hnormalizet(q);//p=q;
	}
	
	public vec3 fold(vec3 pos) {
		for(int i=0;i<Iterations;i++){
			pos.x=Math.abs(pos.x);
			double t=-2.*Math.min(0.,G.dot(nb,pos));
			pos=pos.plus(nb.multiply(new vec3(1.,1.,spaceType)).multiply(t));
			t=-2.*Math.min(0.,G.dot(nc,pos));
			pos=pos.plus(nc.multiply(new vec3(1.,1.,spaceType)).multiply(t));		
		}
		return pos;
	}
	
	double DD(double tha, double r){
		return tha*(1.+spaceType*r*r)/(1.+spaceType*spaceType*r*tha);
	}
	
	double dist2Segment(vec3 z, vec3 n, double r){
		//pmin is the orthogonal projection of z onto the plane defined by p and n
		//then pmin is projected onto the unit sphere
		
		//we are assuming that p and n are normalized. If not, we should do: 
		//mat2 smat=mat2(vec2(hdots(n,n),-hdots(p,n)),vec2(-hdott(p,n),hdott(p,p)));

		mat2 smat=new mat2(new vec2(1.,-hdots(p,n)),new vec2(-hdott(p,n),1.));//should be sent as uniform
		vec2 v=smat.times(new vec2(hdott(z,p),hdots(z,n)));//v is the componenents of the "orthogonal" projection (depends on the metric) of z on the plane defined by p and n wrt to the basis (p,n)
		v.y=Math.min(0.,v.y);//crops the part of the segment past the point p
		
		vec3 pmin=hnormalizet(  (p.multiply(v.x)).plus(n.multiply(v.y)) );
		double tha=hlengths(pmin.minus(z))/hlengtht(pmin.plus(z));
		return DD((tha-SRadius)/(1.+spaceType*tha*SRadius),r);
	}
	

double dist2Segments(vec3 z, double r){
	double da=dist2Segment(z, new vec3(1.,0.,0.), r);
	double db=dist2Segment(z, nb, r);
	double dc=dist2Segment(z, nc.multiply(new vec3(1.,1.,spaceType)), r);	
	return Math.min(Math.min(da,db),dc);
}


double getColor(vec2 pos){
	//todo: add here a möbius transform.
	double r=G.length(pos);
	vec3 z3=new vec3(pos.multiply(2.),1.-spaceType*r*r).multiply( 1./(1.+spaceType*r*r));
	if(spaceType==-1. && r>=1.) 
		return backGroundColor;//We are outside Poincaré disc.
	
	z3=fold(z3);
	
	double color=backGroundColor;
	
	//antialiasing using distance de segments and vertices (ds and dv) (see:http://www.iquilezles.org/www/articles/distance/distance.htm)
	{
		double ds=dist2Segments(z3, r);
		color=G.mix(segColor,color,G.smoothstep(-1.,1.,ds*0.5/aaScale));//clamp(ds/aaScale.y,0.,1.));
	}
	
	//final touch in order to remove jaggies at the edge of the circle (for hyperbolic case)
	if(spaceType==-1.)
		color=G.mix(backGroundColor,color,G.smoothstep(0.,1.,(1.-r)*0.5/aaScale));//clamp((1.-r)/aaScale.y,0.,1.));
	return color;
}


	

	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_ITERATIONS,PARAM_SRADIUS,PARAM_PPARAM,PARAM_QPARAM,PARAM_RPARAM,
			PARAM_UPARAM,PARAM_VPARAM,PARAM_WPARAM,PARAM_ZOOM,PARAM_INVERT};

	
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		double x,y;

		if(mode==0)
		{
			x= pAffineTP.x;
			y =pAffineTP.y;
		}else
		{
			x=pContext.random()-0.5;
			y=pContext.random()-0.5;		     
		}

		// Scaling.
		vec2 uv=new vec2(x*zoom,y*zoom);

		init();
		double color=getColor(uv);

		pVarTP.doHide=false;
		if(invert==0)
		{
			if (color>0.0)
			{ x=0;
			y=0;
			pVarTP.doHide = true;	        
			}
		} else
		{
			if (color<=0.0)
			{ x=0;
			y=0;
			pVarTP.doHide = true;
			}
		}
		pVarTP.x = pAmount * x;
		pVarTP.y = pAmount * y;
		if (pContext.isPreserveZCoordinate()) {
			pVarTP.z += pAmount * pAffineTP.z;
		}
	}
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

	   }

	  
	public String getName() {
		return "cut_triantess";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  mode,Iterations,SRadius,pParam,qParam,rParam,U,V,W,zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		 if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ITERATIONS)) {
			Iterations =(int)Tools.limitValue(pValue, 0 , 20);
		}
		else if (pName.equalsIgnoreCase(PARAM_SRADIUS)) {
			SRadius =Tools.limitValue(pValue, 0.001 , 0.1);
		}
		else if (pName.equalsIgnoreCase(PARAM_PPARAM)) {
			pParam =(int)Tools.limitValue(pValue, 1 , 12);
		}
		else if (pName.equalsIgnoreCase(PARAM_QPARAM)) {
			qParam =(int)Tools.limitValue(pValue, 1 , 12);
		}
		else if (pName.equalsIgnoreCase(PARAM_RPARAM)) {
			rParam =(int)Tools.limitValue(pValue, 1 , 12);
		}
		else if (pName.equalsIgnoreCase(PARAM_UPARAM)) {
			U =Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_VPARAM)) {
			V =Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_WPARAM)) {
			W =Tools.limitValue(pValue, 0.0 , 1.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom =pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			invert =(int)Tools.limitValue(pValue, 0 , 1);
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
	public void randomize() {
		// Don't change mode
		Iterations = (int) (Math.random() * 20 + 1);
		SRadius = Math.random() * 0.098 + 0.002;
		pParam = (int) (Math.random() * 11 + 2);
		qParam = (int) (Math.random() * 11 + 2);
		rParam = (int) (Math.random() * 11 + 2);
		U = Math.random();
		V = Math.random();
		W = Math.random();
		zoom = Math.random() + 1.0;
		invert = (int) (Math.random() * 2);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		float x,y;"
	    	    +"      float SRadius=0.01f;"
	            +"      int pParam=3;"
	            +"      int qParam=3;"
	            +"      int rParam=4;"    
//    U,V,W are the 'barycentric' coordinate for the vertex.
	            +"      float U=__cut_triantess_uBaryc;"
	            +"      float V=__cut_triantess_vBaryc;"
	            +"      float W=__cut_triantess_wBaryc;"
	            +"      float3 nb,nc,p,q;"
	  	        +"      float3 pA,pB,pC;"
	  	        +"      float spaceType=0.0f;"
	  	        +"      float aaScale = 0.0005;"
	  	        +"      float backGroundColor=1.0f;"
	  	        +"      float segColor = 0.0f;"
	  	        
	    		+"		if( __cut_triantess_mode ==0)"
	    		+"		{"
	    		+"			x= __x;"
	    		+"			y =__y;"
	    		+"		}else"
	    		+"		{"
	    		+"			x=RANDFLOAT()-0.5;"
	    		+"			y=RANDFLOAT()-0.5;		     "
	    		+"		}"
	    		+"		"
	    		+"		float2 uv=make_float2(x* __cut_triantess_zoom ,y* __cut_triantess_zoom );"
// init()
	    		+"		spaceType=(float)(sign(__cut_triantess_qParam*__cut_triantess_rParam+__cut_triantess_pParam*__cut_triantess_rParam+__cut_triantess_pParam*__cut_triantess_qParam-__cut_triantess_pParam*__cut_triantess_qParam*__cut_triantess_rParam));"
	    		+"		float cospip=cosf(PI/(float)(__cut_triantess_pParam)), sinpip=sinf(PI/(float)(__cut_triantess_pParam));"
	    		+"		float cospiq=cosf(PI/(float)(__cut_triantess_qParam)), sinpiq=sinf(PI/(float)(__cut_triantess_qParam));"
	    		+"		float cospir=cosf(PI/(float)(__cut_triantess_rParam)), sinpir=sinf(PI/(float)(__cut_triantess_rParam));"
	    		+"		float ncsincos=(cospiq+cospip*cospir)/sinpip;"
//	    		+"		"
	    		+"		nb=make_float3(-cospip,sinpip,0.);"
	    		+"		nc=make_float3(-cospir,-ncsincos,sqrt(abs((ncsincos+sinpir)*(-ncsincos+sinpir))));"
	    		+"		if(spaceType==0.){"
	    		+"			nc.z=0.25;"
	    		+"		}"
	    		+"		pA=make_float3(nb.y*nc.z,-nb.x*nc.z,nb.x*nc.y-nb.y*nc.x);"
	    		+"		pB=make_float3(0.,nc.z,-nc.y);"
	    		+"		pC=make_float3(0.,0.,nb.y);"
	    		+"		q=pA*(U)+(pB*(V))+(pC*(W));"
	    		+"		p= cut_triantess_hnormalizet (q,spaceType);"
//	    		
//	   		    +"		float color=cut_triantess_getColor(uv);"
                +"	    float color=backGroundColor;"	    		
	    		+"	    float r=length(uv);"
	    		+"	    float3 z3=make_float3(uv.x*2.0f , uv.y*2.0f, 1.-spaceType*r*r)*( 1./(1.+spaceType*r*r));"
	    		+"	    if(spaceType==-1. && r>=1.) "
	    		+"		   color=backGroundColor;"
	    		+"	"
//	    		+"	z3= cut_triantess_fold (z3);"
	    		
	    		+"		for(int i=0;i<__cut_triantess_Iters;i++){"
	    		+"			z3.x=abs(z3.x);"
	    		+"			float t=-2.*fminf(0.,dot(nb,z3));"
	    		+"			z3=z3+(nb*(make_float3(1.,1.,spaceType))*(t));"
	    		+"			t=-2.*fminf(0.,dot(nc,z3));"
	    		+"			z3=z3+(nc*(make_float3(1.,1.,spaceType))*(t));		"
	    		+"		}"	    		
	    		
	    		+"	"
//  end fold(z3)
	    		+"	"
	    		+"		float ds= cut_triantess_dist2Segments (z3, r , nb, nc,spaceType,p,__cut_triantess_SRadius);"
	    		+"		color=mix(segColor,color,smoothstep(-1.,1.,ds*0.5/aaScale));"
	    		+"	"
	    		+"	if(spaceType==-1.)"
	    		+"		color=mix(backGroundColor,color,smoothstep(0.,1.,(1.-r)*0.5/aaScale));"	    		

// end getColor(uv)
	    		+"		__doHide=false;"
	    		+"		if( __cut_triantess_invert ==0)"
	    		+"		{"
	    		+"			if (color>0.0)"
	    		+"			{ x=0;"
	    		+"			y=0;"
	    		+"			__doHide = true;	        "
	    		+"			}"
	    		+"		} else"
	    		+"		{"
	    		+"			if (color<=0.0)"
	    		+"			{ x=0;"
	    		+"			y=0;"
	    		+"			__doHide = true;"
	    		+"			}"
	    		+"		}"
	    		+"		__px = __cut_triantess * x;"
	    		+"		__py = __cut_triantess * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_triantess * __z;\n" : "");
	  }
	 
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return  
	    		 "__device__ float  cut_triantess_hdott (float3 a, float3 b,float spaceType)"
	    		+"{"
	    		+"		return dot(make_float2(a.x,a.y),make_float2(b.x,b.y))*(spaceType) + a.z*b.z;"
	    		+"	}"
	    		+"__device__ float hlengtht(float3 v, float spaceType){"
	    		+"		return sqrt(abs( cut_triantess_hdott (v,v,spaceType)));"
	    		+"	}"
	    		+ "__device__ float3  cut_triantess_hnormalizet (float3 v,float spaceType)"
	    		+"  {"  
	    		+"		float l=1.0f/hlengtht(v,spaceType);"  
	    		+"		return v*(l);" 
	    		+"	}"
	    		+"__device__	float  cut_triantess_hdots (float3 a, float3 b,float spaceType)"
	    		+ "{"
	    		+"		return dot(make_float2(a.x,a.y),make_float2(b.x,b.y))+spaceType*a.z*b.z;"
	    		+"	}"
	    		+""
	    		+"__device__ float  cut_triantess_hlengths (float3 v,float spaceType){"
	    		+"		return sqrt(abs( cut_triantess_hdots (v,v,spaceType)));"
	    		+"	}"
	    		+""
	    		+"__device__	float  cut_triantess_DD (float tha, float r,float spaceType){"
	    		+"		return tha*(1.+spaceType*r*r)/(1.+spaceType*spaceType*r*tha);"
	    		+"	}"
	    		+"__device__	float  cut_triantess_dist2Segment (float3 z, float3 n, float r,float3 p, float SRadius,float spaceType)"
	    		+ "{"
	    		+"		Mat2 smat;"
	    		+"      float2 v1=make_float2( 1.0f                                ,-cut_triantess_hdots (p,n,spaceType));"
	    		+"      float2 v2=make_float2(-cut_triantess_hdott (p,n,spaceType) ,1.0f                                );"
	    		+"		Mat2_Init(&smat,v1.x ,v1.y, v2.x,v2.y);"
	    		+"      float w1=cut_triantess_hdott (z,p,spaceType);"
	    		+"      float w2=cut_triantess_hdots(z,n,spaceType);"
	    		+"		float2 v=times(&smat, make_float2(w1,w2));"
	    		+"		v.y=fminf(0.0f,v.y);"
	    		+"		"
	    		+"		float3 pmin= cut_triantess_hnormalizet (  (p*(v.x))+(n*(v.y)), spaceType );"
	    		+"		float tha= cut_triantess_hlengths (pmin-z,spaceType)/hlengtht(pmin+z , spaceType);"
	    		+"		return  cut_triantess_DD ((tha-SRadius)/(1.+spaceType*tha*SRadius),r, spaceType);"
	    		+"	}"
	    		+"	"
	    		+"__device__ float  cut_triantess_dist2Segments (float3 z, float r, float3 nb,float3 nc, float spaceType,float3 p,float SRadius)"
	    		+ "{"
	    		+"	float da= cut_triantess_dist2Segment (z, make_float3(1.0f,0.0f,0.0f), r,p,SRadius,spaceType);"
	    		+"	float db= cut_triantess_dist2Segment (z, nb, r,p,SRadius,spaceType);"
	    		+"	float dc= cut_triantess_dist2Segment (z, nc*(make_float3(1.0f,1.0f,spaceType)), r,p,SRadius,spaceType);	"
	    		+"	return fminf(fminf(da,db),dc);"
	    		+"}";
	  }
}

