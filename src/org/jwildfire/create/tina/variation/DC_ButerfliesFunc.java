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



public class DC_ButerfliesFunc  extends VariationFunc {

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
	private static final String PARAM_GRADIENT = "gradient";
	protected static final String PARAM_SCALE_Z = "z_scale"; 
	protected static final String PARAM_OFFSET_Z = "z_offset"; 
	protected static final String PARAM_RESET_Z = "reset_z"; 
	
	int seed=1000;

	double time=0.0;
    double zoom=1.0;
    double red=-1.;
    double green=-1.0;
    double blue=-1.0;
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE};
	}

}

