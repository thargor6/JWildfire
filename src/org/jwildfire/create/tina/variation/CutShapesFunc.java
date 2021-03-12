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



public class  CutShapesFunc  extends VariationFunc  {

	/*
	 * Variation : cut_shapes
	 * Autor: Jesus Sosa
	 * Date: August 20, 2019
	 * Reference 
	 */



	private static final long serialVersionUID = 1L;

	private static final String PARAM_MODE = "mode";
	private static final String PARAM_TYPE = "type";
	private static final String PARAM_CONCENTRICS = "Contour";
	private static final String PARAM_ZOOM = "zoom";
	private static final String PARAM_INVERT = "invert";
	private static final String PARAM_STARS_N = "n";
	private static final String PARAM_THICK = "thick";
	private static final String PARAM_TIME = "time";

    int mode=1;
    int type=0;
    int contour=1;
	double zoom=1.0;
	private int invert = 0;
	private int n=3;
	double thick=0.01;
	double time=0.50;


	private static final String[] additionalParamNames = { PARAM_MODE,PARAM_TYPE,PARAM_CONCENTRICS,PARAM_ZOOM,PARAM_INVERT,PARAM_STARS_N,PARAM_THICK,PARAM_TIME};


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


//signed distance to a n-star polygon with external angle en
double sdStar( vec2 p, double r,  int n, double m)
{
 // these 4 lines can be precomputed for a given shape
 double an = 3.141593/(double)(n);
 double en = 6.283185/m;
 vec2  acs = new vec2(Math.cos(an),Math.sin(an));
 vec2  ecs = new vec2(Math.cos(en),Math.sin(en)); // ecs=vec2(0,1) and simplify, for regular polygon,

 // reduce to first sector
 double bn = G.mod(G.atan2(p.x,p.y),2.0*an) - an;
 p = new vec2(cos(bn),G.abs(sin(bn))).multiply(G.length(p));

 // line sdf
 p = p.minus(acs.multiply(r));
 
 p = p.plus(ecs.multiply(G.clamp( -G.dot(p,ecs), 0.0, r*acs.y/ecs.y)));
 
 return G.length(p)*G.sign(p.x);
}


double sdCross( vec2 p,  vec2 b, double r ) 
{
    p = G.abs(p);
    p = (p.y>p.x) ? new vec2(p.y,p.x) : new vec2(p.x,p.y);
    
	vec2  q = p.minus(b);
    double k = G.max(q.y,q.x);
    vec2  w = (k>0.0) ? q : new vec2(b.y-p.x,-k);
    
    return G.sign(k)*G.length(G.max(w,0.0)) + r;
}

//signed distance to an equilateral triangle
double sdEquilateralTriangle(  vec2 p )
{
 double k = Math.sqrt(3.0);
 
 p.x = Math.abs(p.x) - 1.0;
 p.y = p.y + 1.0/k;
 
 if( p.x + k*p.y > 0.0 )
	 p = new vec2( (p.x - k*p.y)/2.0,( -k*p.x - p.y )/2.0);
 
 p.x -= G.clamp( p.x, -2.0, 0.0 );
 
 return -G.length(p)*G.sign(p.y);
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

//sca is the sin/cos of the orientation
//scb is the sin/cos of the aperture
double sdArc(  vec2 p,  vec2 sca, vec2 scb,  double ra,  double rb )
{
 p = new mat2(sca.x,sca.y,-sca.y,sca.x).times(p);
 p.x = G.abs(p.x);
 double k = (scb.y*p.x>scb.x*p.y) ? G.dot(new vec2(p.x,p.y),scb) : G.length(new vec2(p.x,p.y));
 return Math.sqrt( G.dot(p,p) + ra*ra - 2.0*ra*k ) - rb;
}


double sdVesica(vec2 p, double r, double d)
{
    p = G.abs(p);

    double b = Math.sqrt(r*r-d*d);  // can delay this sqrt by rewriting the comparison
    return ((p.y-b)*d > p.x*b) ? G.length(p.minus(new vec2(0.0,b)))
                               : G.length(p.minus(new vec2(-d,0.0)))-r;
}

double dot2( vec2 v ) { return G.dot(v,v); }

//trapezoid / capped cone, specialized for Y alignment
double sdTrapezoid( vec2 p, double r1, double r2, double he )
{
 vec2 k1 = new vec2(r2,he);
 vec2 k2 = new vec2(r2-r1,2.0*he);

	p.x = G.abs(p.x);
 vec2 ca = new vec2(G.max(0.0,p.x-((p.y<0.0)?r1:r2)), G.abs(p.y)-he);
 
 
 double t0=G.dot(k1.minus(p),k2)/dot2(k2);
 vec2 cb = p.minus(k1).plus( k2.multiply(G.clamp( t0, 0.0, 1.0 )));
 
 double s = (cb.x < 0.0 && ca.y < 0.0) ? -1.0 : 1.0;
 
 return s*Math.sqrt( Math.min(dot2(ca),dot2(cb)) );
}

//trapezoid / capped cone
double sdTrapezoid( vec2 p, vec2 a, vec2 b, double ra, double rb )
{
 double rba  = rb-ra;
 double baba = G.dot(b.minus(a),b.minus(a));
 double papa = G.dot(p.minus(a),p.minus(a));
 double paba = G.dot(p.minus(a),b.minus(a))/baba;
 double x = Math.sqrt( papa - paba*paba*baba );
 double cax = Math.max(0.0,x-((paba<0.5)?ra:rb));
 double cay = Math.abs(paba-0.5)-0.5;
 double k = rba*rba + baba;
 double f = G.clamp( (rba*(x-ra)+paba*baba)/k, 0.0, 1.0 );
 double cbx = x-ra - f*rba;
 double cby = paba - f;
 double s = (cbx < 0.0 && cay < 0.0) ? -1.0 : 1.0;
 return s*Math.sqrt( Math.min(cax*cax + cay*cay*baba,
                    cbx*cbx + cby*cby*baba) );
}

double ndot(vec2 a, vec2 b ) { return a.x*b.x - a.y*b.y; }

double sdRhombus( vec2 p, vec2 b ) 
{
    vec2 q = G.abs(p);

    double h = G.clamp( (-2.0*ndot(q,b) + ndot(b,b) )/G.dot(b,b), -1.0, 1.0 );
    double d = G.length( q.minus( b.multiply(new vec2(1.0-h,1.0+h)).multiply(0.5) ));
    d *= G.sign( q.x*b.y + q.y*b.x - b.x*b.y );
    
	return d;
}

double sdCircle( vec2 p, double r )
{
  return G.length(p) - r;
}

double sdBox( vec2 p, vec2 b )
{
    vec2 d = G.abs(p).minus(b);
    return G.length(G.max(d,new vec2(0))) + G.min(G.max(d.x,d.y),0.0);
}

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
  
			vec2 p=new vec2(x*zoom,y*zoom);
			double color;
			double d=0.;
			if(contour==1)
				color=1.0;
			else
				color=0.0;
			
			if(type==0)  // Hexagram
			{
				d = sdHexagram( p, 0.35 );
			}
			else if(type==1) // Equilateral Triangle 
			{
				d = sdEquilateralTriangle( p );
			}
			else if (type==2)  // Star
			{
				double a=G.fract(time/3.);
				double m = 4.0 + a*a*(n*2.0-4.0);  
				d = sdStar( p, 0.7,n,m );
			}else if(type==3)  // Cross
			{
				vec2 si = G.cos( new vec2(0.0,1.57).plus(time) ).multiply(0.3).plus(0.8); 	    
				 if( si.x<si.y ) 
				   	si=new vec2(si.y,si.x);
				    // corner radious
				 double ra = 0.1*sin(time*1.2);
				 d = sdCross( p, si, ra );
			}
			else if (type==4) // octogon
			{
				d= sdOctogon( p, 0.5 );
			}
			else if(type==5) // hexagon
			{
				 d= sdHexagon( p, 0.5 );
			}
			else if(type==6)  // pentagon
			{
				d= sdPentagon( p, 0.5 );
			}
			else if(type==7)  // Vesica
			{
				 d = sdVesica( p, 1.1, 0.8 + 0.2*sin(time) );
			}
			else if(type==8)  // trapezoid
			{
			    double ra=0.2+0.15*Math.sin(time*1.3+0.0);
			    double rb=0.2+0.15*Math.sin(time*1.4+0.1);
			    vec2  pa = new vec2(-0.6,0.0).plus(G.sin(new vec2(0.0,2.0).plus(time*1.1)).multiply(0.4));
			    vec2  pb = new vec2(-0.6,0.0).plus(G.sin(new vec2(1.0,2.5).plus(time*1.2)).multiply(0.4));
			    vec2  pc = new vec2(0.8,0.0);

			    // axis aligned trapezoid
				d = sdTrapezoid( p.minus(pc), ra, rb, 0.5+0.2*Math.sin(1.3*time) );
			    // aribitrary trapezoid
			    d = Math.min( d, sdTrapezoid( p, pa, pb , ra, rb ) );
			}
			else if(type==9)  // arc
			{
				double ta = 3.14*(0.5+0.5*Math.cos(time*0.52+2.0));
				double tb = 3.14*(0.5+0.5*Math.cos(time*0.31+2.0));
				double rb = 0.15*(0.5+0.5*Math.cos(time*0.41+3.0));
				//	    // distance
				d = sdArc(p,new vec2(sin(ta),cos(ta)),new vec2(sin(tb),cos(tb)), 0.5, rb);
			}
			else if(type==10)  // rhombus
			{
				vec2 ra = G.cos( new vec2(0.0,1.57).plus(time)  ).multiply(0.3).plus(0.4);
			    d = sdRhombus( p, ra );
			}
			else if(type==11)  // circle
			{
				vec2 ra = G.cos( new vec2(0.0,1.57).plus(time)  ).multiply(0.3).plus(0.4);
			    d = sdCircle( p, 0.5 );
			}
			else if(type==12)  // box
			{
				vec2 ra = G.cos( new vec2(0.0,1.57).plus(time)  ).multiply(0.3).plus(0.4);
			    d = sdBox( p, ra );
			}
			
			
		    if(contour==1)
			  color*=Math.cos(120.*d);
		    else
		    {
		    	color *= 1.0 - G.exp(-2.0*G.abs(d));
		    	color = G.mix( color, 1.0, 1.0-G.smoothstep(0.0,thick,G.abs(d)) );
		    }
	    
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (color>0.0)
		      { x=0.;
		        y=0.;
		        pVarTP.doHide = true;
		      }
		    } else
		    {
			      if (color<=0.0 )
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
		return "cut_shapes";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { mode, type,contour,zoom,invert,n,thick,time};
	}

	public void setParameter(String pName, double pValue) {
	    if(pName.equalsIgnoreCase(PARAM_MODE))
	    {
	    	mode= (int)Tools.limitValue(pValue, 0 ,1);
	    }
		else if (pName.equalsIgnoreCase(PARAM_TYPE)) {
			type = (int)Tools.limitValue(pValue, 0 , 12);
		}
		else if (pName.equalsIgnoreCase(PARAM_CONCENTRICS)) {
			contour = (int)Tools.limitValue(pValue, 0 ,1);
		}
		else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
			zoom = Tools.limitValue(pValue, 0.1 , 50.0);
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			   invert =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else if (pName.equalsIgnoreCase(PARAM_STARS_N)) {
			n = (int)Tools.limitValue(pValue, 3 ,9);
		}
		else if (pName.equalsIgnoreCase(PARAM_THICK)) {
			thick =Math.abs(pValue);
		}
		else if (pName.equalsIgnoreCase(PARAM_TIME)) {
			time = pValue;
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
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SIMULATION};
	}

}


