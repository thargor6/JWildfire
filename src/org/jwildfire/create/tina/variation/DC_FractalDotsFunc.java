package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;

public class DC_FractalDotsFunc  extends DC_BaseFunc implements SupportsGPU {

	/*
	 * Variation : dc_fractaldots
	 * Date: February 13, 2019
	 * Author:Jesus Sosa
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_ITERATIONS = "iterations";
	private static final String PARAM_DOTSIZE = "dotsize";
	private static final String PARAM_MAXITER = "maxiterations";
	private static final String PARAM_COMPLEXITY = "complexity"; 
	private static final String PARAM_PATTERN = "pattern"; 
	private static final String PARAM_SPACING = "spacing"; 
	private static final String PARAM_ROTATE1 = "rotate1"; 
	private static final String PARAM_ROTATE2 = "rotate2"; 
	private static final String PARAM_ZOOM = "zoom"; 




	int iterations=9;
	double dotsize=400.;
	double maxiterations=10;
    double complexity=0.001245675;
    double pattern=2.5;
    double spacing=12.;
    double rotate1=1.5;
    double rotate2=64.;
    double zoom=64.;
    


	
	
	double circleSize=dotsize/(3.0*Math.pow(2.0,(double)maxiterations));
	
	

	private static final String[] additionalParamNames = { PARAM_ITERATIONS,PARAM_DOTSIZE,PARAM_MAXITER,PARAM_COMPLEXITY,PARAM_PATTERN,PARAM_SPACING,PARAM_ROTATE1,PARAM_ROTATE2,PARAM_ZOOM};

	    
	public vec2 rot(vec2 uv,double a){

		return new vec2(uv.x*G.cos(a)-uv.y*G.sin(a),uv.y*G.cos(a)+uv.x*G.sin(a));

	}
	

	
	public vec3 getRGBColor(double xp,double yp)
	{

		vec2 uv=new vec2(xp,yp);

		vec3 col=new vec3(0.0);
		//global rotation and zoom
		uv=rot(uv,rotate1);
		uv =uv.multiply( zoom);


		//mirror, rotate and scale
		double s=spacing;
		for(int i=0;i<maxiterations;i++){
			//uv=floor(abs(uv)-s);
			uv=G.abs(uv).minus(s);
			uv = uv.minus(complexity);
			uv=rot(uv,rotate2);
			s=s/pattern;
			if (iterations < i) 
				break;
		}

		//draw a circle
		double c=G.length(uv)>circleSize?0.0:1.0;		
		return new vec3(c);
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
		return "dc_fractaldots";
	}

	public String[] getParameterNames() {
		return joinArrays(additionalParamNames, paramNames);
	}



	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return joinArrays(new Object[] { iterations,dotsize,maxiterations,complexity,pattern,spacing,rotate1,rotate2,zoom},super.getParameterValues());
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_ITERATIONS)) {
			iterations = (int)Tools.limitValue(pValue, 1 , 100);
		}
		else if (pName.equalsIgnoreCase(PARAM_DOTSIZE)) {
			dotsize= pValue;;
		}  
		else if (pName.equalsIgnoreCase(PARAM_MAXITER)) {
			maxiterations= pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_COMPLEXITY)) { 
			complexity= pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_PATTERN)) {
			pattern= pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_SPACING)) {
			spacing= pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_ROTATE1)) {
			rotate1= pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_ROTATE2)) {
			rotate2= pValue;
		} 
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom= pValue;
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	

	
	public String getGPUCode(FlameTransformationContext context) {
	 return	 "float x,y;"
			+"	    		float3 color=make_float3(1.0,1.0,0.0);"
			+"	    		float z=0.5;"
			+"	    		if( varpar->dc_fractaldots_ColorOnly ==1)"
			+"	    		{"
			+"	    		  x=__x;"
			+"	    		  y=__y;"
			+"	    		}"
			+"	    		else"
			+"	    		{"
			+"	    		  x=2.0*RANDFLOAT()-1.0;"
			+"	    		  y=2.0*RANDFLOAT()-1.0;"
			+"	    		}"
			+"	    		float2 uv=make_float2(x,y);"
			+"	    		color=dc_fractaldots_getRGBColor(uv,varpar->dc_fractaldots_zoom,varpar->dc_fractaldots_iterations,"
			+"                 varpar->dc_fractaldots_dotsize,varpar->dc_fractaldots_maxiterations,varpar->dc_fractaldots_complexity,"
			+"                 varpar->dc_fractaldots_pattern,varpar->dc_fractaldots_spacing,varpar->dc_fractaldots_rotate1,"
			+"                 varpar->dc_fractaldots_rotate2);"
			+"	    		if( varpar->dc_fractaldots_Gradient ==0 )"
			+"	    		{"
			+"	    		   __useRgb  = true;"
			+"	    		   __colorR  = color.x;"
			+"	    		   __colorG  = color.y;"
			+"	    		   __colorB  = color.z;"
			+"	    		   __colorA  = 1.0;"
			+"	    		}"
			+"	    		else if( varpar->dc_fractaldots_Gradient ==1 )"
			+"	    		{"
			+"	    		float4 pal_color=make_float4(color.x,color.y,color.z,1.0);"
			+"	    		float4 simcol=pal_color;"
			+"	    		float diff=1000000000.0f;"
			+""
			+"	    		 for(int index=0; index<numColors;index++)"
			+"               {      pal_color = read_imageStepMode(palette, numColors, (float)index/(float)numColors);"
			+"	    		        float3 pal_color3=make_float3(pal_color.x,pal_color.y,pal_color.z);"
			+"               "
			+"	        	    float dvalue= distance_color(color.x,color.y,color.z,pal_color.x,pal_color.y,pal_color.z);"
			+"	        	   if (diff >dvalue) "
			+"	        	    {" 
			+"	        		     diff = dvalue;"
			+"	        	       simcol=pal_color;"
			+"	        		   }"
			+"                 }"
			+""
			+"	    		   __useRgb  = true;"
			+"	    		   __colorR  = simcol.x;"
			+"	    		   __colorG  = simcol.y;"
			+"	    		   __colorB  = simcol.z;"
			+"	    		   __colorA  = 1.0;"
			+"	    		}"
			+"	    		else if( varpar->dc_fractaldots_Gradient ==2 )"
			+"	    		{"
			+"	    		  int3 icolor=dbl2int(color);"
			+"	    		  float z=greyscale((float)icolor.x,(float)icolor.y,(float)icolor.z);"
			+"	    		  __pal=z;"
			+"	    		}"
			+"	    		__px+= varpar->dc_fractaldots*x;"
			+"	    		__py+= varpar->dc_fractaldots*y;"
			+"	    		float dz = z * varpar->dc_fractaldots_scale_z + varpar->dc_fractaldots_offset_z;"
			+"	    		if ( varpar->dc_fractaldots_reset_z  == 1) {"
			+"	    		     __pz = dz;"
			+"	    		}"
			+"	    		else {"
			+"	    		   __pz += dz;"
			+"	    		}";
	}

	 public String getGPUFunctions(FlameTransformationContext context) {
	return    "	__device__ float2 dc_fractaldots_rot(float2 uv,float a)"
			+ "{"
			+"  float c=cosf(a), s=sinf(a);"
			+"		return make_float2(uv.x*c-uv.y*s,uv.y*c+uv.x*s);"
			+"	}"
			
			+"	__device__ float3  dc_fractaldots_getRGBColor (float2 uv, float zoom, float iterations, float dotsize,"
			+ "                                                float maxiterations, float complexity,"
			+ "                                                float pattern, float spacing, float rotate1, float rotate2)"
			+"	{"
			+"	    float circleSize=dotsize/(3.0*powf(2.0,maxiterations));"
			+"		float3 col=make_float3(0.0,0.0,0.0);"
			+"		"
			+"		uv=dc_fractaldots_rot(uv,rotate1);"
			+"		uv =uv*zoom;"
			+"		"
		    +"		float s=spacing;"
		    +"		for(int i=0;i<maxiterations;i++){"
		    +"			"
		    +"			uv=abs(uv)-(s);"
		    +"			uv = uv-(complexity);"
		    +"			uv=dc_fractaldots_rot(uv,rotate2);"
		    +"			s=s/pattern;"
		    +"			if (iterations < i) "
		    +"				break;"
		    +"		}"
		    +"		"
		    +"		float c=(length(uv)>circleSize)?0.0:1.0;		"
		    +"		return make_float3(c,c,c);"
		    +"	 }";
	 }
}
