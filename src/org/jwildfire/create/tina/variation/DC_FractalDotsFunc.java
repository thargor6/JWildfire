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

public class DC_FractalDotsFunc  extends DC_BaseFunc {

	/*
	 * Variation : dc_fractaldots
	 * Date: February 13, 2019
	 * Author:Jesus Sosa
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_ITERATIONS = "iterations";
	private static final String PARAM_DOTSIZE = "DotSize";
	private static final String PARAM_MAXITER = "MaxIterations";
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE};
	}

}

