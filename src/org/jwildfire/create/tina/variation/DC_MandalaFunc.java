package org.jwildfire.create.tina.variation;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

public class DC_MandalaFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_mandala
	 * Date: February 13, 2019
	 * Autor: Jesus Sosa
	 */


	private static final long serialVersionUID = 1L;


	private static final String PARAM_MX = "mX";

	private static final String PARAM_MY = "mY"; 
	private static final String PARAM_SCALE = "scale"; 
	private static final String PARAM_SIDES = "sides"; 
	private static final String PARAM_MULTIPLY = "multiply"; 
	private static final String PARAM_LOOPS = "loops"; 
	private static final String PARAM_IR = "iR"; 
	private static final String PARAM_IG = "iG"; 
	private static final String PARAM_IB = "iB"; 




	double mX=0.025;
    double mY=-0.001245675;
    double scale=2.;
    int sides=12;
    int multiply=1;
    int loops=64;
    
    int iR=0;
    int iG=0;
    int iB=1;


	private static final String[] additionalParamNames = { PARAM_MX,PARAM_MY,PARAM_SCALE,PARAM_SIDES,PARAM_MULTIPLY,PARAM_LOOPS,PARAM_IR,PARAM_IG,PARAM_IB};

	    



/*	@Override
	public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		super.initOnce(pContext, pLayer, pXForm, pAmount);

	}	 

	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) 
	{

	}*/
	

 	public vec3 getRGBColor(double xp,double yp)
 	{

        
        
        double rcpi=0.318309886183791;	
     
        vec2 uv=new vec2(0.0,0.0);
     	uv.x = (5.5-scale)*(xp);
     	uv.y = (5.5-scale)*( yp );
     		      	
    	double k = Math.PI / (double) sides;
      	vec2 s = G.Kscope(uv,k);
      	vec2 t = G.Kscope(s,k);
      	double v = G.dot(t,s);
    	vec2 u = G.mix(s,t,Math.cos(v));
    	
    	if (multiply>0)
    	{
          vec2 t1=new vec2(u.y,u.x);
          vec2 t2=G.mod(t1,(double) multiply);
          vec2 t3=new vec2(-u.x,-u.y);
          vec2 t4=new vec2(u.y,u.x);
          vec2 t5=t4.plus(G.mod(t2,t3));
          u=new vec2(t5.y,t5.x);
    	}
      	vec3 p = new vec3 (u, mX*v);
      	for (int l = 0; l < 73; l++) {
      		if ((double)l > loops)
      		{ 
      		  break;
      	    }
      		vec3 t1= new vec3(1.3,0.999, 0.678);
            vec3 t2= G.abs(p).division(G.dot(p,p)).minus(new vec3(1.0,1.02, mY*rcpi) );
        	vec3 t3= G.abs(t2);
        	vec3 t4=t1.multiply(t3);
            p=new vec3(t4.x,t4.z,t4.y);
      	}
      	if (iR==1)
      	{ 
      	  p.x = 1.0-p.x; 
      	}
      	if (iG==1)
      	{
      	  p.y = 1.0-p.y;
        }
      	if (iB==1)
      	{
      	 p.z = 1.0-p.z;
      	}      
        return p;
 	}

 	
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) 
	{

        vec3 color=new vec3(0.0); 
		 vec2 uV=new vec2(0.),p=new vec2(0.);
	       int[] tcolor=new int[3];  


		 
	     if(colorOnly==1)
		 {
			 uV.x=pAffineTP.x;
			 uV.y=pAffineTP.y;
		 }
		 else
		 {
	   			 uV.x=2.0*pContext.random()-1.0;
				 uV.y=2.0*pContext.random()-1.0;
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
		return "dc_mandala";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return  joinArrays(new Object[] { mX,mY,scale,sides,multiply,loops,iR,iG,iB},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		 if (pName.equalsIgnoreCase(PARAM_MX)) {
			mX = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_MY)) {
			mY= pValue;;
		}  
		else if (pName.equalsIgnoreCase(PARAM_SCALE)) {
			scale= pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_SIDES)) {
			sides= (int) pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_MULTIPLY)) {
			multiply= (int) pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_LOOPS)) {
			loops= (int) pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_IR)) {
			iR= (int)Tools.limitValue(pValue, 0 , 1);
		} 
		else if (pName.equalsIgnoreCase(PARAM_IG)) {
			iG= (int)Tools.limitValue(pValue, 0 , 1);
		} 
		else if (pName.equalsIgnoreCase(PARAM_IB)) {
			iB= (int)Tools.limitValue(pValue, 0 , 1);
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
	    		+"if( __dc_mandala_ColorOnly ==1)"
	    		+"{"
	    		+"  x=__x;"
	    		+"  y=__y;"
	    		+"}"
	    		+"else"
	    		+"{"
	    		+"  x=2.0*RANDFLOAT()-1.0;"
	    		+"  y=2.0*RANDFLOAT()-1.0;"
	    		+"}"
	    		+"float2 uv=make_float2(x,y);"
	    		+" color=dc_mandala_getRGBColor(uv,__dc_mandala_mX,__dc_mandala_mY,"
	    		+ "                                   __dc_mandala_scale,__dc_mandala_sides,__dc_mandala_multiply,"
	    		+ "                                   __dc_mandala_loops, __dc_mandala_iR,__dc_mandala_iG,"
	    		+ "                                   __dc_mandala_iB );"
	    		+"if( __dc_mandala_Gradient ==0 )"
	    		+"{"
	    		+"   __useRgb  = true;"
	    		+"   __colorR  = color.x;"
	    		+"   __colorG  = color.y;"
	    		+"   __colorB  = color.z;"
				+"   __colorA  = 1.0;"
	    		+"}"
				+"else if( __dc_mandala_Gradient ==1 )"
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
	    		+"else if( __dc_mandala_Gradient ==2 )"
	    		+"{"
				+"  int3 icolor=dbl2int(color);"
	    		+"  z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
	    		+"  __pal=z;"
	    		+"}"
	    		+"__px+= __dc_mandala*x;"
	    		+"__py+= __dc_mandala*y;"
	    		+"float dz = z * __dc_mandala_scale_z + __dc_mandala_offset_z;"
	    		+"if ( __dc_mandala_reset_z  == 1) {"
	    		+"     __pz = dz;"
	    		+"}"
	    		+"else {"
	    		+"   __pz += dz;"
	    		+"}";

	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {

	    return   "__device__ float2 dc_mandala_kscope(float2 uv, float k)"
	    		+"{"
	    		+"   float t= mod ( atan2 (uv.y, uv.x) , 2.0f * k);"
	    		+"	 float angle = fabsf ( t - k);"
	    		+"   float t4=length(uv);"
	    		+"   float2 t1=make_float2(t4,t4);"
	    		+"   float2 t3=make_float2(cosf(angle), sinf(angle) );"
	    		+"   float2 t2 = t1 * t3;"
	    		+"	 return t2;"
	    		+"}"
	    		
	    		+"__device__ float3  dc_mandala_getRGBColor (float2 xc,float mX, float mY, float scale, float sides, float multiply, float loops, float iR, float iG, float iB)"
	    		+" {"
	    		+"     float rcpi=0.318309886183791f;"
	    		+"     float2 uv=make_float2(0.0,0.0);"
	    		+"     	uv.x = (5.5-scale)*(xc.x);"
	    		+"     	uv.y = (5.5-scale)*(xc.y );"
	    		+"     		      	"
	    		+"    	float k = PI / sides;"
	    		+"      float2 s = dc_mandala_kscope(uv,k);"
	    		+"      float2 t = dc_mandala_kscope(s,k);"
	    		+"      float v = dot(t,s);"
	    		+"    	float2 u = mix(s,t,cosf(v));"
	    		+"    	"
	    		+"    	if (multiply>0.0)"
	    		+"    	{"
	    		+"          float2 t1=make_float2(u.y,u.x);"
	    		+"          float2 t2=mod(t1, multiply);"
	    		+"          float2 t3=make_float2(-u.x,-u.y);"
	    		+"          float2 t4=make_float2(u.y,u.x);"
	    		+"          float2 t5= t4+ (mod(t2,t3));"
	    		+"          u=make_float2(t5.y,t5.x);"
	    		+"    	}"
	    		+"      float3 p = make_float3 (u.x, u.y , mX*v);"
	    		+"      for (int l = 0; l < 73; l++) {"
	    		+"      	if ((float)l > loops)"
	    		+"      	{"
	    		+"      	  break;"
	    		+"          }"
	    		+"      	float3 t1= make_float3(1.3,0.999, 0.678);"
	    		+"          float3 t2= abs(p)/(dot(p,p))-(make_float3(1.0,1.02, mY*rcpi) );"
	    		+"          float3 t3= abs(t2);"
	    		+"          float3 t4=t1*(t3);"
	    		+"          p=make_float3(t4.x,t4.z,t4.y);"
	    		+"      }"
	    		+"      if (iR==1.)"
	    		+"      { "
	    		+"        p.x = 1.0-p.x; "
	    		+"      }"
	    		+"      if (iG==1.)"
	    		+"      {"
	    		+"        p.y = 1.0-p.y;"
	    		+"      }"
	    		+"      if (iB==1.)"
	    		+"      {"
	    		+"        p.z = 1.0-p.z;"
	    		+"      }      "
	    		+"      return p;"
	    		+" }";
	  }
}

