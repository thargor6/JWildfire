package org.jwildfire.create.tina.variation;


import java.util.Random;
import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class CutRandomTileFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation :cut_randomtile
	 * Date: october 15, 2019
	 * Reference:  https://www.shadertoy.com/view/wsBXDt
	 * Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_SEED = "seed";	
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	
	int seed=1000;
	int mode=1;
    double zoom=5.0;
    int invert=0;

	Random randomize=new Random(seed);
 	
    double x0=0.,y0=0.;
    
	private static final String[] additionalParamNames = { PARAM_SEED,PARAM_MODE,PARAM_ZOOM,PARAM_INVERT};

	// Shorthand.
 public double sstep(double sf, double d)
 {
	return (1. - G.smoothstep(0., sf, d));
 }


	// Standard 2D rotation formula.
	public mat2 rot2(double a)
	{ double c = Math.cos(a), s = Math.sin(a);
	  return new mat2(c, -s, s, c); 
	}


	// IQ's standard vec2 to double hash formula.
	public double hash21(vec2 p){
	 
	    double n = G.dot(p, new vec2(127.183, 157.927));
	    return G.fract(Math.sin(n)*43758.5453);
	}


	// Cheap and nasty 2D smooth noise function with inbuilt hash function -- based on IQ's 
	// original. Very trimmed down. In fact, I probably went a little overboard. I think it 
	// might also degrade with large time values.
	public  double n2D(vec2 p) {

		vec2 i = G.floor(p);
		p = p.minus(i);
		p = (new vec2(3.).minus( p.multiply(2.))).multiply(p).multiply(p);  
	    
		return G.dot(new vec2(1. - p.y, p.y).times(new mat2(G.fract(G.sin(new vec4(0, 1, 113, 114).plus(G.dot(i, new vec2(1, 113))).multiply(43758.5453)))))
	                , new vec2(1. - p.x, p.x) );

	}

	// FBM -- 4 accumulated noise layers of modulated amplitudes and frequencies.
	public double fbm(vec2 p)
	{ 
		return n2D(p)*.533 + n2D(p.multiply(2.))*.267 + n2D(p.multiply(4.))*.133 + n2D(p.multiply(8.))*.067;
    }


	// Distance field for the grid tile.
	public double TilePattern(vec2 p){
	      
	    vec2 ip = G.floor(p); // Cell ID.
	    p =p.minus( ip.plus( .5)); // Cell's local position. Range [vec2(-.5), vec2(.5)].
	    
	     
	    // Using the cell ID to generate a unique random number.
	    double rnd = hash21(ip);
	    double rnd2 = hash21(ip.plus(27.93));
	    //double rnd3 = hash21(ip + 57.75);
	     
	    // Random tile rotation.
	    double iRnd = G.floor(rnd*4.);
	    p = p.times(rot2(iRnd*3.14159/2.));
	    // Random tile flipping.
	    //p.y *= (rnd>.5)? -1. : 1.;
	    
	    // Rendering the arcs onto the tile.
	    //
	    double d = 1e5, d1 = 1e5, d2 = 1e5, d3 = 1e5, l;
	    	   
	    // Three top left arcs.
	    l = G.length(p.minus(new vec2(-.5, .5)));
	    d1 = Math.abs(l - .25);
	    d2 = Math.abs(l - .5);
	    d3 = Math.abs(l - .75);
	    if(rnd2>.33) 
	    	d3 = Math.abs(G.length(p.minus(new vec2(.125, .5))) - .125);
	    
	    d = Math.min(Math.min(d1, d2), d3);
	    
	    // Two small arcs on the bottom right.
	    d1 = 1e5;//abs(length(p - vec2(.5, .5)) - .25);
	    //if(rnd3>.35) d1 = 1e5;//
	    d2 = Math.abs(G.length(p.minus(new vec2(.5, .125))) - .125);
	    d3 = Math.abs(G.length(p.minus(new vec2(.5, -.5))) - .25);
	    d = Math.min(d, Math.min(d1, Math.min(d2, d3))); 
	    	    
	    // Three bottom left arcs.
	    l = G.length(p.plus(.5));
	    d = Math.max(d, -(l - .75)); // Outer mask.
	    
	    // Equivalent to the block below:
	    //
	    //d1 = abs(l - .75);
	    //d2 = abs(l - .5);
	    //d3 = abs(l - .25);
	    //d = min(d, min(min(d1, d2), d3));
		//
	    d1 = Math.abs(l - .5);
	    d1 = Math.min(d1, Math.abs(d1 - .25));
	    d = Math.min(d, d1);
	    
	    // Arc width. 
	    d -= .0625;
	     
	    // Return the distance field value for the grid tile.
	    return d; 
	}


	// Smooth fract function.
	public double sFract(double x, double sf){
	    
	    x = G.fract(x);
	    return Math.min(x, (1. - x)*x*sf);
	    
	}


	// The grungey texture -- Kind of modelled off of the metallic Shadertoy texture,
	// but not really. Most of it was made up on the spot, so probably isn't worth 
	// commenting. However, for the most part, is just a mixture of colors using 
	// noise variables.
	public vec3 GrungeTex(vec2 p){
	    
	 	// Some fBm noise.
	    //double c = n2D(p*4.)*.66 + n2D(p*8.)*.34;
	    double c = n2D(p.multiply(3.))*.57 + n2D(p.multiply(7.))*.28 + n2D(p.multiply(15.))*.15;
	   
	    // Noisey bluish red color mix.
	    vec3 col = G.mix(new vec3(.25, .1, .02), new vec3(.35, .5, .65), c);
	    // Running slightly stretched fine noise over the top.
	    col =col.multiply(n2D(p.multiply(new vec2(150., 350.)))*.5 + .5); 
	    
	    // Using a smooth fract formula to provide some splotchiness... Is that a word? :)
	    col = G.mix(col, col.multiply(new vec3(.75, .95, 1.2)), sFract(c*4., 12.));
	    col = G.mix(col, col.multiply(new vec3(1.2, 1, .8).multiply(.8)), sFract(c*5. + .35, 12.)*.5);
	    
	    // More noise and fract tweaking.
	    c = n2D(p.multiply(8.).plus( .5))*.7 + n2D(p.multiply(18.).plus(.5))*.3;
	    c = c*.7 + sFract(c*5., 16.)*.3;
	    col = G.mix(col.multiply(.6), col.multiply(1.4), c);
	    
	    // Clamping to a zero to one range.
	    return G.clamp(col, 0., 1.);
	    
	}


	 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		    double x,y,px_center,py_center;
		    
		    if(mode==0)
		    {
		      x= pAffineTP.x;
		      y =pAffineTP.y;
		      px_center=0.0;
		      py_center=0.0;
		    }else
		    {
		     x=pContext.random();
		     y=pContext.random();
		      px_center=0.5;
		      py_center=0.5;		     
		    }
		    
		    
		    vec2 u =new vec2(x,y);
		    
            vec2 p=new vec2(x*zoom,y*zoom);
		    p=p.plus(new vec2(x0,y0));
	
            // Taking a few distance field readings.    
            vec2 eps = new vec2(4, 6);
            double d = TilePattern(p); // Initial field value.
            double d2 = TilePattern(p.plus(eps)); // Slight sample distance, for highlighting,.
            double dS = TilePattern(p.plus(eps.multiply(3.))); // Larger distance, for shadows.
            
            // Calculating the sample difference.    
            double b = G.smoothstep(0., 15./450., d - .015);
            double b2 = G.smoothstep(0., 15./450., d2 - .015);
            
            // Bump value for the warm highlight (above), and the cool one (below).
            double bump = Math.max(b2 - b, 0.)/G.length(eps);
            double bump2 = Math.max(b - b2, 0.)/G.length(eps);

            // Smoothing factor, based on resolution.
            double sf = 5./1000.0;
             
            // The grungey texture.
            vec3 tx = GrungeTex(p.division(4.).plus( .5));
            tx = G.smoothstep(0., .5, tx);
            
             
            // Background texture.
            vec3 bg = tx.multiply(new vec3(.85, .68, .51));
           bg= new vec3(0.0);
            // Initiate the image color to the background.
            vec3 col = bg;
            // Dark edge line -- stroke.
            col = G.mix(col, new vec3(0), sstep(sf, d));
             
            
            // Pattern color -- just a brightly colored version of the background.   
            vec3 pCol = new vec3(2.5, .75, .25).multiply(tx);
            pCol=new vec3(1.0);
            
            // Apply the pattern color. Decrease the pattern width by the edge line width.
            col = G.mix(col, pCol, sstep(sf, d + .025));
             
             col = G.sqrt(G.max(col, 0.));
            double color=col.x;
            
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
		    pVarTP.x = pAmount * (x-px_center);
		    pVarTP.y = pAmount * (y-py_center);
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  randomize=new Random(seed);
		  x0=seed*randomize.nextDouble();
		  y0=seed*randomize.nextDouble();
	   }

	  
	public String getName() {
		return "cut_randomtile";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  seed, mode,zoom,invert});
	}

	public void setParameter(String pName, double pValue) {
		 if(pName.equalsIgnoreCase(PARAM_SEED))
			{
				   seed =   (int)pValue;
			       randomize=new Random(seed);
			}
		else if (pName.equalsIgnoreCase(PARAM_MODE)) {
			mode =(int)Tools.limitValue(pValue, 0 , 1);
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
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	 @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		    float x,y,px_center,py_center;"
	    		+"		    "
	    		+"		    if( __cut_randomtile_mode ==0)"
	    		+"		    {"
	    		+"		      x= __x;"
	    		+"		      y =__y;"
	    		+"		      px_center=0.0;"
	    		+"		      py_center=0.0;"
	    		+"		    }else"
	    		+"		    {"
	    		+"		     x=RANDFLOAT();"
	    		+"		     y=RANDFLOAT();"
	    		+"		      px_center=0.5;"
	    		+"		      py_center=0.5;		     "
	    		+"		    }"
	    		+"		    "
	    		+"		    float2 u =make_float2(x,y);"
	    		+"          float2 p =make_float2(x* __cut_randomtile_zoom ,y* __cut_randomtile_zoom );"
	    		+"          float2 eps = make_float2(4.0, 6.0);"
// test hash21 & rot2
//	    	    +"	    float2 ip = floorf(p); "
//	    	    +"	    float rnd  =  cut_randomtile_hash21 (ip);"
//	    	    +"	    float rnd2 =  cut_randomtile_hash21 (ip+(27.93));"
//	    	    +"	    "
//	    	    +"	    float iRnd = floorf(rnd*4.);"
//	    	    +"      Mat2 m;"
//	    	    +"      m=cut_randomtile_rot2 (iRnd*3.14159/2.);"
//	    	    +"	    p = times(&m ,p );"
// test sstep sFract & randomtile_n2D
//	    	    +"	    float c =  cut_randomtile_n2D (p*(3.))*.57 +  cut_randomtile_n2D (p*(7.))*.28 +  cut_randomtile_n2D (p*(15.))*.15;"
//	    		+"      float col = 1.0;"
//	    		+"      float sf = 5./1000.0;"
//	    		+"           "
//	    		+"      col = cut_randomtile_sstep(sf, 0.5);"
//	    	    +"	    col = cut_randomtile_sFract(c*5. + .35, 12.)*.5;"
// test GrungeTex 
//       		    +"          float3 tx = cut_randomtile_GrungeTex(p/4.0f + 0.5f);"
// Original code	    		
	    		+"          float d  = cut_randomtile_TilePattern(p);"
	    		+"          float d2 = cut_randomtile_TilePattern(p+eps); "
	    		+"          float dS = cut_randomtile_TilePattern(p+eps*3.0f);"
//	    		+"            "
	    		+"          float b = smoothstep(0., 15./450., d - .015);"
	    		+"          float b2 = smoothstep(0., 15./450., d2 - .015);"
	    		+"          float bump  = max(b2 - b, 0.)/length(eps);"
	    		+"          float bump2 = max(b - b2, 0.)/length(eps);"
	    		+"          float sf = 5.0f/1000.0f;"
	    		+"          float3 tx = cut_randomtile_GrungeTex(p/(4.)+( .5));"
	    		+"          tx = smoothstep(0., .5, tx);"
	    		+"          float3 bg= make_float3(0.0f,0.0f,0.0f);"
	    		+"          float3 col = bg;"
	    		+"          col = mix(col, make_float3(0.0f,0.0f,0.0f), cut_randomtile_sstep(sf, d));"
	    		+"          float3 pCol=make_float3(1.0f,1.0f,1.0f);"
	    		+"          col = mix(col, pCol, cut_randomtile_sstep(sf, d + .025));"
	    		+"          col.x = sqrtf(fmaxf(col.x, 0.0f));"
	    		+"          float color=col.x;"
	    		+"		    __doHide=false;"
	    		+"		    if( __cut_randomtile_invert ==0)"
	    		+"		    {"
	    		+"		      if (color>0.0)"
	    		+"		      { x=0;"
	    		+"		        y=0;"
	    		+"		        __doHide = true;	        "
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (color<=0.0)"
	    		+"			      { x=0;"
	    		+"			        y=0;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __cut_randomtile * (x-px_center);"
	    		+"		    __py = __cut_randomtile * (y-py_center);"
	            + (context.isPreserveZCoordinate() ? "__pz += __cut_randomtile * __z;\n" : "");
	  }
	 
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return 
	     "__device__ float  cut_randomtile_sstep (float sf, float d)"
	    +" {"
	    +"	return (1.0f - smoothstep(0.0f, sf, d));"
	    +" }"
	    +"	"
	    +"__device__ float cut_randomtile_sFract(float x, float sf){"
	    +"	    x = fract(x);"
	    +"	    return fminf(x, (1.0f - x)*x*sf);"
	    +"	    "
	    +"	}"
	    +"	"
	    +"__device__ Mat2  cut_randomtile_rot2 (float a)"
	    +"	{ "
		+"    Mat2 m;"
	    + "   float c = cos(a), s = sin(a);"
		+"    Mat2_Init(&m,c,-s,s,c);"
	    +"	  return m; "
	    +"	}"
	    +"	"
	    +"__device__ float  cut_randomtile_hash21 (float2 p){"
	    +"	 "
	    +"	    float n = dot(p, make_float2(127.183, 157.927));"
	    +"	    return fract(sin(n)*43758.5453);"
	    +"	}"
	    +"	"
	    +"__device__  float  cut_randomtile_n2D (float2 p) {"
	    +"		float2 i = floorf(p);"
	    +"		p = p-(i);"
	    +"		p = (make_float2(3.0f,3.0f)-( p*(2.)))*(p)*(p);  "
	    +"	    "
	    +"	    Mat2 m;"
	    +"      Mat2_Init(&m,fract(sin(make_float4(0.0f, 1.0f, 113.0f, 114.0f)+(dot(i, make_float2(1, 113)))*(43758.5453))));"
	    +"      float2 t0=times(&m,make_float2(1. - p.y, p.y));"
	    +"		return dot(t0 , make_float2(1. - p.x, p.x) );"
	    +"	}"
	    +"	"
	    +"__device__ float3 cut_randomtile_GrungeTex(float2 p){"
	    +"	    "
	    +"	    float c =  cut_randomtile_n2D (p*(3.))*.57 +  cut_randomtile_n2D (p*(7.))*.28 +  cut_randomtile_n2D (p*(15.))*.15;"
	    +"	    float3 col = mix(make_float3(.25, .1, .02), make_float3(.35, .5, .65), c);"
	    +"	    col =col*( cut_randomtile_n2D (p*(make_float2(150., 350.)))*.5 + .5); "
	    +"	    col = mix(col, col*(make_float3(.75, .95, 1.2))  , cut_randomtile_sFract(c*4., 12.));"
	    +"	    col = mix(col, col*(make_float3(1.2, 1, .8)*(.8)), cut_randomtile_sFract(c*5. + .35, 12.)*.5);"
	    +"	    c =  cut_randomtile_n2D (p*(8.)+( .5))*.7 +  cut_randomtile_n2D (p*(18.)+(.5))*.3;"
	    +"	    c = c*.7f + cut_randomtile_sFract(c*5., 16.)*.3;"
	    +"	    col = mix(col*(.6f), col*(1.4f), c);"
	    +"	    return clamp(col, 0.f, 1.f);"
	    +"	}"
	    +"   "
	    +"__device__ float cut_randomtile_TilePattern(float2 p){"
	    +"	      "
	    +"	    float2 ip = floorf(p); "
	    +"	    p =p-( ip+( .5)); "
	    +"	    "
	    +"	    float rnd  =  cut_randomtile_hash21 (ip);"
	    +"	    float rnd2 =  cut_randomtile_hash21 (ip+ 27.93);"
	    +"	    float iRnd = floorf(rnd*4.0f);"
	    +"      Mat2 m;"
	    +"      m=cut_randomtile_rot2 (iRnd*3.14159f/2.0f);"
	    +"	    p = times(&m ,p );"
	    +"	    float d = 1.0e5, d1 = 1.0e5, d2 = 1.0e5, d3 = 1.0e5, l;"
	    +"	    	   "
	    +"	    l = length(p-(make_float2(-0.5f, .5f)));"
	    +"	    d1 = fabsf(l - .25f);"
	    +"	    d2 = fabsf(l - .5f);"
	    +"	    d3 = fabsf(l - .75f);"
	    +"	    if(rnd2>0.33f)"
	    +"	    	d3 = fabsf(length(p-(make_float2(0.125f,0.5f))) - 0.125f);"
	    +"	    "
	    +"	    d = fminf(fminf(d1, d2), d3);"
	    +"	    d1 = 1.0e5;"
	    +"	    d2 = fabsf(length(p-(make_float2(0.5f, 0.125f))) - 0.125f);"
	    +"	    "
	    +"	    d3 = fabsf(length(p-(make_float2(0.5f, -0.5f))) - 0.25f);"
	    +"	    d  = fminf(d, fminf(d1, fminf(d2, d3))); "
	    +"	    	    "
	    +"	    l  = length(p+0.5f);"
	    +"	    d  = fmaxf(d, -(l - 0.75f));"
	    +"	    d1 = fabsf(l - 0.5f);"
	    +"	    d1 = fminf(d1, fabsf(d1 - 0.25f));"
	    +"	    d  = fminf(d, d1);"
	    +"	    d -= .0625f;"
	    +"	    return d;"
	    +"	}";
	  }
}

