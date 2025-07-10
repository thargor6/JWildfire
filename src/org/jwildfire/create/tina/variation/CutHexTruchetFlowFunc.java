package org.jwildfire.create.tina.variation;



import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class  CutHexTruchetFlowFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation : cut_hextruchetflow
	 * Autor: Jesus Sosa
	 * Date: September 25, 2019
	 * Reference & Credits: https://www.shadertoy.com/view/ltVBDw
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_SEED = "randomize";
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_GRID = "grid";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";


	int mode=1;
	double zoom=10.0;
	private int invert = 0;
	double tol=0.9;


    int seed=0;
    int grid=0;
    double uvx =0.0;
    double uvy =0.0;
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_GRID,PARAM_ZOOM,PARAM_INVERT};

	double pi = 3.14159, sqrt3 = 1.73205;
	double cHashM = 43758.54;
	
	public vec2 PixToHex (vec2 p)
	{
	  vec3 c = new vec3(0.0), r, dr;
	  vec2 t0=new vec2 ((1./sqrt3) * p.x - (1./3.) * p.y, (2./3.) * p.y);
	  
	  c.x =t0.x;
	  c.z= t0.y;
	  c.y = - c.x - c.z;
	  
	  r = G.floor (c.plus( 0.5));
	  dr = G.abs (r.minus(c));
	  vec3 t1=new vec3(dr.y,dr.z,dr.x);
	  vec3 t2=new vec3(dr.z,dr.x,dr.y);
	  	  r = r.minus(G.step (t1, dr).multiply( G.step (t2, dr)).multiply( G.dot (r, new vec3 (1.))));
	  
	  return new vec2(r.x,r.z);
	}

	public vec2 HexToPix (vec2 h)
	{
	  return new vec2 (sqrt3 * (h.x + 0.5 * h.y), 1.5 * h.y);
	}

	public double HexEdgeDist (vec2 p)
	{
	  p = G.abs (p);
	  return (sqrt3/2.) - p.x + 0.5 * G.min (p.x - sqrt3 * p.y, 0.);
	}

	vec2 Rot2D (vec2 q, float a)
	{
	  vec2 cs;
	  cs = G.sin (new vec2 (0.5 * pi, 0.).plus(a));
	  return new vec2 (G.dot (q, new vec2 (cs.x, - cs.y)), G.dot (new vec2(q.y,q.x), cs));
	}



	double Hashfv2 (vec2 p)
	{
	  return G.fract (G.sin (G.dot (p, new vec2 (37., 39.))) * cHashM);
	}

	
	public vec3 ShowScene (vec2 p,int grid)
	{
	  vec3 col=new vec3(0.0), w=new vec3(0.0);
	  vec2 cId, pc;
	  vec2 q;
	  double dir, a, d;
	  cId = PixToHex (p);
	  pc = HexToPix (cId);
	  dir = 2. * G.step (Hashfv2 (cId), 0.5) - 1.;
	  vec2 t0=pc.plus(new vec2 (0., - dir));
	  w.x=t0.x;
	  w.y=t0.y;
	  w.z = G.dot (new vec2(w.x,w.y).minus(p) , new vec2(w.x,w.y).minus(p) );
	  
	  q = pc.plus(new vec2 (sqrt3/2., 0.5 * dir));
	  d = G.dot (q.minus( p), q.minus(p));
	  if (d < w.z) w = new vec3 (q, d);
	  q = pc.plus(new vec2 (- sqrt3/2., 0.5 * dir));
	  d = G.dot (q.minus(p) , q.minus(p));
	  if (d < w.z) w = new vec3 (q, d);
	  w.z = Math.abs (Math.sqrt (w.z) - 0.5);
	  d = HexEdgeDist (p.minus( pc));
	  if(grid==1)
	    col = new vec3 (1., 1., 1.).multiply( G.mix (1., G.smoothstep (1.0, 1., d),G.smoothstep (0.01, 0.02, d)));	  
	  if (w.z < 0.25) {
	    col = new vec3 (1., 1., 1.); 
	  }
	  return col;
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

	    uvx= seed*G.sin(seed*180./Math.PI);
	    uvy= uvx;
			    
        vec2 uv=new vec2(x*zoom-uvx,y*zoom-uvy);
        vec3 color=new vec3(0.0);
	    color =  color.plus( ShowScene ( (uv.plus( G.step (1.5, 1.0)) ),grid));
	    double col=color.x;   
	    
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
		return "cut_hextruchetflow";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { 
		return new Object[] { seed,mode,grid,zoom,invert};
	}

	public void setParameter(String pName, double pValue) {
		 if (pName.equalsIgnoreCase(PARAM_SEED)) {
			 seed=(int)pValue;
		}
			else if (pName.equalsIgnoreCase(PARAM_MODE)) {
				   mode =   (int)Tools.limitValue(pValue, 0 , 1);
			}
			else if (pName.equalsIgnoreCase(PARAM_GRID)) {
				grid = (int)Tools.limitValue(pValue, 0 , 1);
			}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
//		else if (pName.equalsIgnoreCase(PARAM_TOL)) {
//				   tol =   Tools.limitValue(pValue, 0.01 , 0.99);
//		}
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
	public void randomize() {
		// Don't change mode
		seed = (int) (Math.random() * 1000000);
		grid = (int) (Math.random() * 2);
		zoom = Math.random() * 49.0 + 1.0;
		invert = (int) (Math.random() * 2);
	}

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		  float x,y;"
	    		+"		  if( __cut_hextruchetflow_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT()-0.5;"
	    		+"		     y=RANDFLOAT()-0.5;"
	    		+"		    }"
	    		+"			    "
	    	    +"      float shiftxy= __cut_hextruchetflow_randomize*sin(__cut_hextruchetflow_randomize*180.0f/PI);"
	    		+"      float2 uv=make_float2(x,y)* __cut_hextruchetflow_zoom - make_float2(shiftxy,shiftxy);"
 	    		+"      float3 color=make_float3(0.0,0.0,0.0);"
//	    		+"      float t1= cut_hextruchetflow_Hashfv2  (uv);"
//	    		+"      float2 t2=cut_hextruchetflow_PixToHex(uv);"
//	    		+"             uv=cut_hextruchetflow_HexToPix(t2);"
//	    		+"             t1=cut_hextruchetflow_HexEdgeDist  (uv);"
//              +"      color=cut_hextruchetflow_ShowScene(uv , __cut_hextruchetflow_grid);"
   		        +"	    color =  color +  cut_hextruchetflow_ShowScene ( uv + step (1.5, 1.0) ,__cut_hextruchetflow_grid);"
	    		+"		__doHide=false;"
	    		+"		if( __cut_hextruchetflow_invert ==0)"
	    		+"		{"
	    		+"			if (color.x > 0.9f)"
	    		+"			{ x=0.0f;"
	    		+"			  y=0.0f;"
	    		+"			__doHide = true;"
	    		+"			}"
	    		+"		} else"
	    		+"		{"
	    		+"			if (color.x<=0.9f)"
	    		+"			{ x=0.0f;"
	    		+"			  y=0.0f;"
	    		+"			__doHide = true;"
	    		+"			}"
	    		+"		}"
	    		+"		__px = __cut_hextruchetflow * x;"
	    		+"		__py = __cut_hextruchetflow * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_hextruchetflow * __z;\n" : "");
	  }
	 
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__ float2  cut_hextruchetflow_PixToHex  (float2 p)"
	    		+"	{"
	    		+"	float sqrt3=1.73205;"
	    		+"	  float3 c = make_float3(0.0f,0.0f,0.0f), r, dr;"
	    		+"	  float2 t0=make_float2 ((1./sqrt3) * p.x - (1./3.) * p.y, (2./3.) * p.y);"
	    		+"	  "
	    		+"	  c.x =t0.x;"
	    		+"	  c.z= t0.y;"
	    		+"	  c.y = - c.x - c.z;"
	    		+"	  "
	    		+"	  r = floorf (c+( 0.5));"
	    		+"	  dr = abs (r-(c));"
	    		+"	  float3 t1=make_float3(dr.y,dr.z,dr.x);"
	    		+"	  float3 t2=make_float3(dr.z,dr.x,dr.y);"
	    		+"	  	  r = r-(step (t1, dr)*( step (t2, dr))*( dot (r, make_float3 (1.0f,1.0f,1.0f))));"
	    		+"	  "
	    		+"	  return make_float2(r.x,r.z);"
	    		+"	}"
	    		+"   "
	    		+"__device__ float2  cut_hextruchetflow_HexToPix  (float2 h)"
	    		+"	{"
	    		+"	  float sqrt3=1.73205;"
	    		+"	  return make_float2 (sqrt3 * (h.x + 0.5 * h.y), 1.5 * h.y);"
	    		+"	}"
	    		+"   "
	    		+"__device__ float  cut_hextruchetflow_HexEdgeDist  (float2 p)"
	    		+"	{"
	    		+"		float sqrt3=1.73205;"
	    		+"	    p = abs (p);"
	    		+"	    return (sqrt3/2.) - p.x + 0.5 * min (p.x - sqrt3 * p.y, 0.);"
	    		+"	}"
	    		+"   "
	    		+"__device__ float  cut_hextruchetflow_Hashfv2  (float2 p)"
	    		+"	{"
	    		+"	    return fract (sin (dot (p, make_float2 (37., 39.))) * 43758.54);"
	    		+"	}"
	    		+"	"
	    		+"	__device__ float3  cut_hextruchetflow_ShowScene  (float2 p,int grid)"
	    		+"	{"
	    		+"	  float sqrt3=1.73205;"
	    		+"	  float3 col=make_float3(0.0f,0.0f,0.0f), w=make_float3(0.0f,0.0f,0.0f);"
	    		+"	  float2 cId, pc;"
	    		+"	  float2 q;"
	    		+"	  float dir, a, d;"
	    		+"	  cId =  cut_hextruchetflow_PixToHex  (p);"
	    		+"	  pc =  cut_hextruchetflow_HexToPix  (cId);"
	    		+"	  dir = 2. * step ( cut_hextruchetflow_Hashfv2  (cId), 0.5) - 1.;"
	    		+"	  float2 t0=pc+(make_float2 (0., - dir));"
	    		+"	  w.x=t0.x;"
	    		+"	  w.y=t0.y;"
	    		+"	  w.z = dot (make_float2(w.x,w.y)-(p) , make_float2(w.x,w.y)-(p) );"
	    		+"	  "
	    		+"	  q = pc+ make_float2 (sqrt3/2., 0.5 * dir);"
	    		+"	  d = dot (q- p, q-p);"
	    		+"	  if (d < w.z) w = make_float3 (q.x,q.y, d);"
	    		+"	  q = pc+(make_float2 (- sqrt3/2., 0.5 * dir));"
	    		+"	  d = dot (q-(p) , q-(p));"
	    		+"	  if (d < w.z) w = make_float3 (q.x,q.y, d);"
	    		+"	  w.z = abs (sqrt (w.z) - 0.5);"
	    		+"	  d =  cut_hextruchetflow_HexEdgeDist  (p-( pc));"
	    		+"	  if(grid==1)"
	    		+"	    col = make_float3 (1., 1., 1.)*( mix (1., smoothstep (1.0, 1., d),smoothstep (0.01, 0.02, d)));	  "
	    		+"	  if (w.z < 0.25) {"
	    		+"	    col = make_float3 (1., 1., 1.); "
	    		+"	  }"
	    		+"	  return col;"
	    		+"	}";
	  }	
}


