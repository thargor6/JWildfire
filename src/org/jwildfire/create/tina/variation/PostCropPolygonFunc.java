package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.M_2PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

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



public class  PostCropPolygonFunc  extends VariationFunc  implements SupportsGPU {

	/*
	 * Variation : crop_polygon
	 * Autor: Jesus Sosa
	 * Date: february 3, 2020
	 * Reference & Credits : https://www.shadertoy.com/view/llVyWW
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_TYPE = "type";
	private static final String PARAM_RADIUS = "radius";
	private static final String PARAM_INVERT = "invert";



    int type=4;
    double radius=0.35;
	private int invert = 0;
	


	private static final String[] additionalParamNames = { PARAM_TYPE,PARAM_RADIUS,PARAM_INVERT};


double sdHexagram( vec2 p, double r )
{
    vec4 k = new vec4(-0.5,0.86602540378,0.57735026919,1.73205080757);
    
    p = G.abs(p);
    
    double t0=G.dot(new vec2(k.x,k.y),p);
    vec2 t1= (new vec2(k.x,k.y)).multiply(new vec2(2.0)).multiply(G.min(t0,0.0));
    
    p = p.minus( t1);
    
    t0=G.dot(new vec2(k.y,k.x),p);
    t1=(new vec2(k.y,k.x)).multiply(2.0).multiply(G.min(t0,0));
    
    p = p.minus(t1);
    p = p.minus(new vec2(G.clamp(p.x,r*k.z,r*k.w),r));
    return G.length(p)*G.sign(p.y);
}





//signed distance to a regular octogon
double sdOctogon( vec2 p, double r )
{
// pi/8: cos, sin, tan.
 vec3 k = new vec3(-0.9238795325,   // sqrt(2+sqrt(2))/2 
                    0.3826834323,   // sqrt(2-sqrt(2))/2
                    0.4142135623 ); // sqrt(2)-1 
// reflections
p = G.abs(p);
p = p.minus(new vec2( k.x,k.y).multiply(2.0*G.min(G.dot(new vec2( k.x,k.y),p),0.0)));
p = p.minus(new vec2(-k.x,k.y).multiply(2.0*G.min(G.dot(new vec2(-k.x,k.y),p),0.0)));
// Polygon side.
p = p.minus( new vec2(G.clamp(p.x, -k.z*r, k.z*r), r));
return G.length(p)*G.sign(p.y);
}

double sdHexagon(  vec2 p, double r )
{
    vec3 k = new vec3(-0.866025404,0.5,0.577350269);
    p = G.abs(p);
    p = p.minus(new vec2(k.x,k.y).multiply(2.0*G.min(G.dot(new vec2(k.x,k.y),p),0.0)));
    p = p.minus(new vec2(G.clamp(p.x, -k.z*r, k.z*r), r));
    return G.length(p)*G.sign(p.y);
}


//signed distance to a regular pentagon
double sdPentagon(  vec2 p, double r )
{
  vec3 k = new vec3(0.809016994,0.587785252,0.726542528); // pi/5: cos, sin, tan
 p.y = -p.y;
 p.x = G.abs(p.x);
 p = p.minus(new vec2(-k.x,k.y).multiply(2.0*G.min(G.dot(new vec2(-k.x,k.y),p),0.0)));
 p = p.minus(new vec2( k.x,k.y).multiply(2.0*G.min(G.dot(new vec2( k.x,k.y),p),0.0)));
 p = p.minus(new vec2(G.clamp(p.x,-r*k.z,r*k.z),r));    
 return G.length(p)*G.sign(p.y);
}

double sdCircle( vec2 p, double r )
{
  return G.length(p) - r;
}



	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;
		  
	      x= pVarTP.x;
	      y =pVarTP.y;

  
			vec2 p=new vec2(x,y);

			double d=0.;

			if(type==4)  // Hexagram
			{
				d = sdHexagram( p, radius);
			}
			else if (type==3) // octogon
			{
				d= sdOctogon( p, radius );
			}
			else if(type==2) // hexagon
			{
				 d= sdHexagon( p,radius );
			}
			else if(type==1)  // pentagon
			{
				d= sdPentagon( p,radius );
			}
			else if(type==0)  // circle
			{
				d= sdCircle( p,radius );
			}


	    
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (d>0.0)
		      { x=0.;
		        y=0.;
		        pVarTP.doHide = true;
		      }
		    } else
		    {
			      if (d<=0.0 )
			      { x=0.;
			        y=0.;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * x;
		    pVarTP.y = pAmount * y;
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }

	public String getName() {
		return "post_crop_polygon";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { 
		return new Object[] { type,radius,invert};
	}

	public void setParameter(String pName, double pValue) {
       if (pName.equalsIgnoreCase(PARAM_TYPE)) {
			type = (int)Tools.limitValue(pValue, 0 , 4);
		}
		else if (pName.equalsIgnoreCase(PARAM_RADIUS)) {
			radius = pValue;
		}

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
	  public int getPriority() {
	    return 1;
	  }

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_POST, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	
	@Override
	public String getGPUCode(FlameTransformationContext context) {
	    return   "		  float x,y;"
	    		+"		  "
	    		+"	      x= __px;"
	    		+"	      y =__py;"
	    		+"  "
	    		+"			float2 p=make_float2(x,y);"
	    		+"			float d=0.;"
	    		+"			if( varpar->post_crop_polygon_type ==4)  "
	    		+"			{"
	    		+"				d = post_crop_polygon_sdHexagram( p,  varpar->post_crop_polygon_radius );"
	    		+"			}"
	    		+"			else if ( varpar->post_crop_polygon_type ==3) "
	    		+"			{"
	    		+"				d= post_crop_polygon_sdOctogon( p,  varpar->post_crop_polygon_radius  );"
	    		+"			}"
	    		+"			else if( varpar->post_crop_polygon_type ==2) "
	    		+"			{"
	    		+"				 d= post_crop_polygon_sdHexagon( p,varpar->post_crop_polygon_radius );"
	    		+"			}"
	    		+"			else if( varpar->post_crop_polygon_type ==1)  "
	    		+"			{"
	    		+"				d= post_crop_polygon_sdPentagon( p,varpar->post_crop_polygon_radius );"
	    		+"			}"
	    		+"			else if( varpar->post_crop_polygon_type ==0)  "
	    		+"			{"
	    		+"				d= post_crop_polygon_sdCircle( p,varpar->post_crop_polygon_radius );"
	    		+"			}"
	    		+"		    __doHide=false;"
	    		+"		    if( varpar->post_crop_polygon_invert ==0)"
	    		+"		    {"
	    		+"		      if (d>0.0)"
	    		+"		      { x=0.;"
	    		+"		        y=0.;"
	    		+"		        __doHide = true;"
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (d<=0.0 )"
	    		+"			      { x=0.;"
	    		+"			        y=0.;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = varpar->post_crop_polygon * x;"
	    		+"		    __py = varpar->post_crop_polygon * y;"
	            + (context.isPreserveZCoordinate() ? "__pz += varpar->cut_polygon * __z;\n" : "");
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return   "__device__ float  post_crop_polygon_sdHexagram ( float2 p, float r )"
	    		+"{"
	    		+"    float4 k = make_float4(-0.5,0.86602540378,0.57735026919,1.73205080757);"
	    		+"    "
	    		+"    p = abs(p);"
	    		+"    "
	    		+"    float t0=dot(make_float2(k.x,k.y),p);"
	    		+"    float2 t1= (make_float2(k.x,k.y))*(make_float2(2.0,2.0))*(fminf(t0,0.0));"
	    		+"    "
	    		+"    p = p-( t1);"
	    		+"    "
	    		+"    t0=dot(make_float2(k.y,k.x),p);"
	    		+"    t1=(make_float2(k.y,k.x))*(2.0)*(fminf(t0,0));"
	    		+"    "
	    		+"    p = p-(t1);"
	    		+"    p = p-(make_float2(clamp(p.x,r*k.z,r*k.w),r));"
	    		+"    return length(p)*sign(p.y);"
	    		+"}"
	    		+""
	    		+"__device__ float  post_crop_polygon_sdOctogon ( float2 p, float r )"
	    		+"{"
	    		+""
	    		+" float3 k = make_float3(-0.9238795325,   "
	    		+"                    0.3826834323,   "
	    		+"                    0.4142135623 ); "
	    		+""
	    		+"p = abs(p);"
	    		+"p = p-(make_float2( k.x,k.y)*(2.0*fminf(dot(make_float2( k.x,k.y),p),0.0)));"
	    		+"p = p-(make_float2(-k.x,k.y)*(2.0*fminf(dot(make_float2(-k.x,k.y),p),0.0)));"
	    		+""
	    		+"p = p-( make_float2(clamp(p.x, -k.z*r, k.z*r), r));"
	    		+"return length(p)*sign(p.y);"
	    		+"}"
	    		+"__device__ float  post_crop_polygon_sdHexagon (  float2 p, float r )"
	    		+"{"
	    		+"    float3 k = make_float3(-0.866025404,0.5,0.577350269);"
	    		+"    p = abs(p);"
	    		+"    p = p-(make_float2(k.x,k.y)*(2.0*fminf(dot(make_float2(k.x,k.y),p),0.0)));"
	    		+"    p = p-(make_float2(clamp(p.x, -k.z*r, k.z*r), r));"
	    		+"    return length(p)*sign(p.y);"
	    		+"}"
	    		+""
	    		+"__device__ float  post_crop_polygon_sdPentagon (  float2 p, float r )"
	    		+"{"
	    		+"  float3 k = make_float3(0.809016994,0.587785252,0.726542528); "
	    		+" p.y = -p.y;"
	    		+" p.x = abs(p.x);"
	    		+" p = p-(make_float2(-k.x,k.y)*(2.0*fminf(dot(make_float2(-k.x,k.y),p),0.0)));"
	    		+" p = p-(make_float2( k.x,k.y)*(2.0*fminf(dot(make_float2( k.x,k.y),p),0.0)));"
	    		+" p = p-(make_float2(clamp(p.x,-r*k.z,r*k.z),r));    "
	    		+" return length(p)*sign(p.y);"
	    		+"}"
	    		+"__device__ float  post_crop_polygon_sdCircle ( float2 p, float r )"
	    		+"{"
	    		+"  return length(p) - r;"
	    		+"}";
	  }
}


