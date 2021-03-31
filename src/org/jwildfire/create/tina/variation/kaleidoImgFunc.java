package org.jwildfire.create.tina.variation;



/*JWildfire - an image and animation processor written in Java 
Copyright (C) 1995-2012 Andreas Maschke

This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
General Public License as published by the Free Software Foundation; either version 2.1 of the 
License, or (at your option) any later version.

This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this software; 
if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.GradientCreator;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import js.glsl.G;
import js.glsl.mat2;
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;

import java.util.HashMap;
import java.util.Map;

import static org.jwildfire.base.mathlib.MathLib.*;


public class kaleidoImgFunc  extends VariationFunc{ 
private static final long serialVersionUID = 1L;


/*
 * Variation :  kaleidoimg
 * Date: january 9, 2021
 * Jesus Sosa
 * Reference: https://forum.openframeworks.cc/t/what-does-the-texture-function-in-glsl-do/21196
 * 	 Credits: https://www.shadertoy.com/view/tsSXzt
 *   Credits  https://www.shadertoy.com/view/Ms2XRc
 *   Credits: https://www.shadertoy.com/view/4lsGWj
 *   Credits: https://www.shadertoy.com/view/4tlGD2
 *   Credits  https://www.shadertoy.com/view/MdyBW3
 *   Credits: https://www.shadertoy.com/view/Ws33Wl
 *   Credits  https://www.shadertoy.com/view/3dVczK
 */

public static final String PARAM_ZOOM = "zoom";
public static final String PARAM_TIME = "time";

public static final String PARAM_BLEND = "blendcolor";

public static final String PARAM_DC = "dc_color";
public static final String PARAM_ANGLE = "Angle";
public static final String PARAM_SX = "scaleX";
public static final String PARAM_SY = "scaleY";

private static final String PARAM_SCALEZ = "scale_z";

public static final String PARAM_OX = "offsetX";
public static final String PARAM_OY = "offsetY";


private static final String PARAM_OFFSETZ = "offset_z";
private static final String PARAM_TILEX = "tile_x";
private static final String PARAM_TILEY = "tile_y";
private static final String PARAM_RESETZ = "reset_z";

public static final String PARAM_TYPE = "type";
public static final String PARAM_NRADIUS = "nr";
public static final String PARAM_A = "a";
public static final String PARAM_B = "b";


double zoom=2.0,time=0.0;
int blend_colormap=0;
int dc_color=0;
double angle=0.0;
double scaleX=1.0,scaleY=1.0,scaleZ=0.0;
double offsetX=0.0,offsetY=0.0,offsetZ=0.0;
int tileX = 1;
int tileY = 1;
int resetZ = 1;

int type=0;
int nradius=12;
double a=1.0;
double b=0.0;

private static final String RESSOURCE_IMAGE_FILENAME = "image_filename";
public static final String RESSOURCE_INLINED_IMAGE = "inlined_image";
public static final String RESSOURCE_IMAGE_SRC = "image_src";
public static final String RESSOURCE_IMAGE_DESC_SRC = "image_desc_src";

private static final String[] paramNames = {PARAM_ZOOM,PARAM_TIME,PARAM_BLEND,PARAM_DC,PARAM_ANGLE,PARAM_SX,PARAM_SY,PARAM_SCALEZ,PARAM_OX,PARAM_OY,PARAM_OFFSETZ,PARAM_TILEX,PARAM_TILEY,PARAM_RESETZ,PARAM_TYPE,PARAM_NRADIUS,PARAM_A,PARAM_B};
//private static final String[] paramNames = {PARAM_ZOOM,PARAM_TIME,PARAM_DC,PARAM_ANGLE,PARAM_SX,PARAM_SY,PARAM_OX,PARAM_OY,PARAM_TYPE,PARAM_NRADIUS};
private static final String[] ressourceNames = {RESSOURCE_IMAGE_FILENAME, RESSOURCE_INLINED_IMAGE, RESSOURCE_IMAGE_DESC_SRC, RESSOURCE_IMAGE_SRC};

private String imageFilename = null;
private byte[] inlinedImage = null;
private String imageDescSrc = null;
private String imageSrc = null;
private int inlinedImageHash = 0;

mat3 rotation(double angle)
{
	double c = Math.cos(angle);
    double s = Math.sin(angle);
    return new mat3( c, -s, 0.,  s, c, 0.,  0., 0., 1.);
}

vec2 rotate(vec2 p, double a){
	return new vec2(p.x*Math.cos(a)-p.y*Math.sin(a), p.x*Math.sin(a)+p.y*Math.cos(a));
}

vec3 hsv(double h,double s,double v) 
{
	vec3 t1=new vec3(3.,2.,1.);
	t1=t1.add(h).division(3.0);
	vec3 t2=G.fract(t1).multiply(6.0).minus(3.0);
	t2=G.abs(t2).minus(1.0);
	t2=G.clamp(t2,0.,1.);
	return G.mix(new vec3(3.1),t2,s).multiply(v);
}


int[] dbl2int(vec3 theColor)
	{
		int[] color=new int[3];

		color[0] =  Math.max(0, Math.min(255, (int)Math.floor(theColor.x * 256.0D)));
		color[1] =  Math.max(0, Math.min(255, (int)Math.floor(theColor.y * 256.0D)));
		color[2] =  Math.max(0, Math.min(255, (int)Math.floor(theColor.z * 256.0D)));
		return color;
	}

double mode0(vec2 p)
{
	
	  // Credits https://www.shadertoy.com/view/Ms2XRc
	vec2 fold = new vec2(-0.15, -0.15);
	vec2 translate = new vec2(1.5);
    double scale = 1.25;
    p = p.multiply(0.182);

double x = p.y;
p = G.abs(G.mod(p, 4.0).minus(2.0));
for(int i = nradius ; i > 0; i--){
	p = G.abs(p.minus( fold)).plus(fold);
	p = p.multiply(scale).minus( translate);
	p = rotate(p, 3.14159/(0.10+G.sin(time*0.0005+(double)i*0.5000001)*0.4999+0.5+(10./time)+G.sin(time)/100.));
}
double i = x*x + G.atan2(p.y, p.x) + time*0.02;
double h = G.floor(i*4.0)/8.0 + 1.107;
h += G.smoothstep(-0.1, 0.8, G.mod(i*2.0/5.0, 1.0/4.0)*900.0)/0.010 - 0.5;
//vec3 color=hsv(h, 1.0, G.smoothstep(-3.0, 3.0, G.length(p)*1.0));
 return h;
}
	
vec2 mode1(vec2 uv)
{
	// Credits: https://www.shadertoy.com/view/4lsGWj
	double r = 1.0;
	double a = time*.1;
	double c = Math.cos(a)*r;
	double s = Math.sin(a)*r;
	for ( int i=0; i<32; i++ )
	{
	    	uv = G.abs(uv);
	        uv = uv.minus(0.25);
	        uv = uv.multiply(c).plus(new vec2(uv.y,uv.x).multiply(s).multiply(new vec2(1,-1)));
	} 
	return uv;
}

vec2 mode2(vec2 uv)
{
	// Credits:  https://www.shadertoy.com/view/4tlGD2
	 double T = time*.3+10.0;
	 double r = 1.0;
	 double a = T*.1;
	 double c = Math.cos(a)*r;
	 double s = Math.sin(a)*r;
	 double q = T*.2 / 6.2831853;;
	 for ( int i=0; i<30; i++ )
	 {    
	        // higher period symmetry
	   double t = G.atan(uv.x,uv.y);
	   t *= q;
	   t = G.abs(G.fract(t*.5+.5)*2.0-1.0);
	   t /= q;
	        //q = q+.001;
	   uv = new vec2(Math.sin(t),Math.cos(t)).multiply(G.length(uv));
	        
	   uv = uv.minus(0.7);
	   uv = uv.multiply(c).plus(new vec2(uv.y,uv.x).multiply(new vec2(1,-1)).multiply(s)) ;
	 }
     return uv;
}

vec2 mode3(vec2 uv)
{
	// Credits https://www.shadertoy.com/view/MdyBW3
	 double t = nradius * G.atan(uv.y, uv.x);
	    vec2 suv = (new vec2(1).plus( new vec2(Math.cos(t), Math.sin(t)).multiply(G.pow(G.length(uv), 3.)))).division(2.0) ;
     return suv;
}

vec2 mode4(vec2 uv)
{
	// Credits: https://www.shadertoy.com/view/Ws33Wl
	 vec3 huv = new vec3(uv, 0.);
	    huv = huv.times(rotation(time*.2));
	    
	    vec3 axisOrigin = new vec3(0., 0., 1.);
	    vec3 axisDirection = new vec3(G.normalize(new vec2(1., 1.)), 0.);
	    
	    for(int i = 0; i < nradius ; i++)
	    {
	        double offset = (3.1415 * 2. / (double)nradius ) * (double)i;
	        double axisRotation = offset;
	    	vec3 tuv = (huv.minus(axisOrigin)).times( rotation(-axisRotation));
	    	if(tuv.y < 0.)
	    	{
	    		vec3 invuv = tuv;
	        	invuv.y = -invuv.y;
	        	invuv = (invuv.times( rotation(axisRotation))).plus( axisOrigin);
	        	huv = invuv;
	    	}
	    }
	    return new vec2(huv.x,huv.y);
}


vec2 N(double angle) {
    return new vec2(Math.sin(angle), Math.cos(angle));
}

vec2 mode5(vec2 uv)
{
	
	// Credits  https://www.shadertoy.com/view/3dVczK
    double a = 5./6. * 3.1415;

    uv.x = G.abs(uv.x);
    uv.y += Math.tan(a) * 0.5;

    vec2 n = N(a);
    double d = G.dot(uv.minus(new vec2(0.5, 0.)),n);
    uv = uv.minus( n.multiply( Math.max(0., d) *2.));

    //col += smoothstep(0.01, 0., abs(d));

    double scale = 1.;
    n = N(2./3. * 3.1415);
    uv.x += 1.5/3.;
    
    for (int i=0; i<4; i++) {    
        uv = uv.multiply(3.);
        scale *= 3.;
        uv.x -= 1.5;

        uv.x = Math.abs(uv.x);
        uv.x -= .5;
        uv =  uv.minus(n.multiply( 2*min(G.dot(uv, n), 0.)));
    }
    uv=uv.division(scale);
    return uv;	
}

vec2 atan2(vec2 a,vec2 b)
{
  vec2 tmp=new vec2(0.0);
  tmp.x=G.atan2(a.x,b.x);
  tmp.y=G.atan2(a.y,b.y);
  return tmp; 
}

vec2 mode6(vec2 uv)
{
	// Credits: https://www.shadertoy.com/view/ldd3Wn
	
    vec2 t1=atan2(uv,new vec2(uv.y,uv.x));
         t1=t1.division(6.28);
    double t2=Math.pow(2.0,-G.length(uv));
    vec2 t3=(new vec2(5.0,8.0)).multiply(t1.add(t2));
    return t3;
}

vec3 mode7(vec2 p)
{
	// Reference & Credits: 
	// https://www.iquilezles.org/www/articles/tunnel/tunnel.htm
	// https://www.shadertoy.com/view/Ms2SWW
	
	vec3 pixel=new vec3(0.);
	// angle of each pixel to the center of the screen
	double a = G.atan2(p.y,p.x);

	// cylindrical tunnel
	double r = G.length(p);

	// index texture by (animated inverse) radious and angle
	vec2 uv = new vec2( 0.3/r + 0.2*time, a/3.1415927 );

    pixel= G.texture(colorMap,uv);
    pixel= pixel.multiply(r);
	return pixel;
}

vec2 distortUV(vec2 uv, vec2 nUV)
{
    double intensity = 0.01;
    double scale = 0.01;
    double speed = 0.25;
    
    
    nUV.x += (time)*speed;
    nUV.y += (time)*speed;
    
    vec2 xy=nUV.multiply(scale);
    
    vec3 tmp= G.texture( colorMap, xy) ;
    vec2 noise=new vec2(tmp.x,tmp.y);
   
    vec2 tmp1=((noise.multiply(2.0)).minus(1.0)).multiply(intensity);
    uv = uv.plus(tmp1);
    
    return uv;
}

vec2 mode8(vec2 uv)
{

    vec2 nUV=uv;
	uv = distortUV(uv, nUV);
    uv = distortUV(uv, new vec2(nUV.x+0.1,nUV.y+0.1));
    uv = distortUV(uv, new vec2(nUV.x+0.2,nUV.y+0.2));
    uv = distortUV(uv, new vec2(nUV.x+0.3,nUV.y+0.3));
    uv = distortUV(uv, new vec2(nUV.x+0.4,nUV.y+0.4));
    uv = distortUV(uv, new vec2(nUV.x+0.5,nUV.y+0.5));
    uv = distortUV(uv, new vec2(nUV.x+0.6,nUV.y+0.6));
    uv = distortUV(uv, new vec2(nUV.x+0.7,nUV.y+0.7));
    uv = distortUV(uv, new vec2(nUV.x+0.8,nUV.y+0.8));
    uv = distortUV(uv, new vec2(nUV.x+0.9,nUV.y+0.9));
    uv = distortUV(uv, new vec2(nUV.x+0.15,nUV.y+0.15));
    uv = distortUV(uv, new vec2(nUV.x+0.25,nUV.y+0.25));
    uv = distortUV(uv, new vec2(nUV.x+0.35,nUV.y+0.35));
    uv = distortUV(uv, new vec2(nUV.x+0.45,nUV.y+0.45));
    uv = distortUV(uv, new vec2(nUV.x+0.55,nUV.y+0.55));
    uv = distortUV(uv, new vec2(nUV.x+0.65,nUV.y+0.65));
    uv = distortUV(uv, new vec2(nUV.x+0.75,nUV.y+0.75));
    uv = distortUV(uv, new vec2(nUV.x+0.85,nUV.y+0.85));
    uv = distortUV(uv, new vec2(nUV.x+0.95,nUV.y+0.95));
    return uv;
}

vec2 mode9(vec2 p)
{
	// Reference & Credits: 
	// https://stackoverflow.com/questions/5055625/image-warping-bulge-effect-algorithm
 //   double r=G.length(p);
	vec2 uv = (G.normalize(p).multiply(0.7*Math.log(G.length(p))));
	
	return p.plus(uv);
}


vec2 mode10(vec2 p)
{
	// Reference & Credits:  Variant rn=a*r^b
	// https://stackoverflow.com/questions/5055625/image-warping-bulge-effect-algorithm
    double r=G.length(p);
    double rn= a*Math.pow(r, b);
	return new vec2(rn*p.x/r,rn*p.y/rn);

}

double h00(double x) { return 2.*x*x*x - 3.*x*x + 1.; }
double h10(double x) { return x*x*x - 2.*x*x + x; }
double h01(double x) { return 3.*x*x - 2.*x*x*x; }
double h11(double x) { return x*x*x - x*x; }

double Hermite(double p0, double p1, double m0, double m1, double x)
{
	return p0*h00(x) + m0*h10(x) + p1*h01(x) + m1*h11(x);
}

vec2 mode11(vec2 uv)
{
	// Reference & Credits:  Hermite interpolation
	// https://www.shadertoy.com/view/4sS3Wc
	double a = sin(time * 1.0)*0.5 + 0.5;
	double b = sin(time * 1.5)*0.5 + 0.5;
	double c = sin(time * 2.0)*0.5 + 0.5;
	double d = sin(time * 2.5)*0.5 + 0.5;
	
	double y0 = G.mix(a, b, uv.x);
	double y1 = G.mix(c, d, uv.x);
	double x0 = G.mix(a, c, uv.y);
	double x1 = G.mix(b, d, uv.y);

	uv.x = Hermite(0., 1., 3.*x0, 3.*x1, uv.x);
	uv.y = Hermite(0., 1., 3.*y0, 3.*y1, uv.y);	
    
	return new vec2(uv.x,1.0-uv.y);
}


double distance(vec2 p0,vec2 p1)
{
  return G.length(p0.minus(p1));
}

vec2 mode12(vec2 uv)
{
 	//  Fresnel distortion 
	// Credits: // https://www.shadertoy.com/view/XtKSDm  
	
	double t=time*0.05;
    double r = distance(uv, new vec2(0.0));
	r -= t;
    r = G.fract(r*a)/b;
    
    uv =  uv.multiply(2.0).minus(1.0);
    uv = uv.multiply(r);
    uv = uv.multiply( 0.5).plus( 0.5);
	return uv;
}

vec2 mode13(vec2 uv)
{
 	//  Interference distortion 
	// Credits: // https://www.shadertoy.com/view/tsKXDh
	
	double x1= a/2.0;
    double adist = distance(uv, new vec2(-a/2.0,0.0));
    double bdist = distance(uv, new vec2( a/2.0,0.0));
    
    double a=(Math.sin((adist-time/20.0)*10.)+1.0)/2.0;
    double b=(Math.sin((bdist-time/20.0)*10.)+1.0)/2.0;
	
    return new vec2(a,b);	
}

double s = Math.sin(angle);
double c = Math.cos(angle);
	    
public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
		double pAmount) 
{
    double x,y;
	x=pContext.random()-0.5;
    y=pContext.random()-0.5;
    
    vec2 uv=new vec2(x*zoom,y*zoom);
	vec3 pixel=new vec3(0.);
	mat2 rotationMatrix = new mat2( c, s,
            -s,  c);
	
	if(type==-1)   
	{	
	// Credits: https://www.shadertoy.com/view/tsSXzt
	   uv=uv.times(rotationMatrix);              // Rotate 
	   uv=uv.multiply(new vec2(scaleX,scaleY));  // Scale
	   uv=uv.plus(new vec2(offsetX,offsetY));    // Shift
	   
	   pixel=G.texture(colorMap, uv);
	 
	}
	else if (type==0)
	{
		double hue=mode0(uv);
		pixel=hsv(hue, 1.0, G.smoothstep(-3.0, 3.0, G.length(uv)*1.0));
		int[] color=dbl2int(pixel);
		pixel.x=color[0];
		pixel.y=color[1];
		pixel.z=color[2];
		
	}
	else if (type==1)
	{
		uv=mode1(uv);
		uv=uv.times(rotationMatrix);              // Rotate 
		uv=uv.multiply(new vec2(scaleX,scaleY));  // Scale
		uv=uv.plus(new vec2(offsetX,offsetY));    // Shift
		pixel=G.texture(colorMap, uv);
	}
	else if (type==2)
	{
		uv=mode2(uv);
		uv=uv.times(rotationMatrix);              // Rotate 
		uv=uv.multiply(new vec2(scaleX,scaleY));  // Scale
		uv=uv.plus(new vec2(offsetX,offsetY));    // Shift
		pixel=G.texture(colorMap, uv);
	}	
	else if (type==3)
	{
		uv=mode3(uv);
		uv=uv.times(rotationMatrix);              // Rotate 
		uv=uv.multiply(new vec2(scaleX,scaleY));  // Scale
		uv=uv.plus(new vec2(offsetX,offsetY));    // Shift
		pixel=G.texture(colorMap, uv);
	}
	else if (type==4)
	{
		uv=mode4(uv);
		uv=uv.times(rotationMatrix);              // Rotate 
		uv=uv.multiply(new vec2(scaleX,scaleY));  // Scale
		uv=uv.plus(new vec2(offsetX,offsetY));    // Shift
		pixel=G.texture(colorMap, uv);
	}
	else if (type==5)
	{
        uv=mode5(uv);
	    uv=uv.times(rotationMatrix);              // Rotate 
	    uv=uv.multiply(new vec2(scaleX,scaleY));  // Scale
	    uv=uv.plus(new vec2(offsetX,offsetY));    // Shift
	    pixel= G.texture(colorMap,uv.multiply(2.0).plus(time*0.05));
	}
	else if (type==6)
	{
        uv=mode6(uv);
	    uv=uv.times(rotationMatrix);              // Rotate 
	    uv=uv.multiply(new vec2(scaleX,scaleY));  // Scale
	    uv=uv.plus(new vec2(offsetX,offsetY));    // Shift
	    pixel= G.texture(colorMap,uv);
	}
	else if (type==7)
	{
	    uv=uv.times(rotationMatrix);              // Rotate 
	    uv=uv.multiply(new vec2(scaleX,scaleY));  // Scale
	    uv=uv.plus(new vec2(offsetX,offsetY));    // Shift
        pixel=mode7(uv);
	}
	else if (type==8)
	{
	    uv=uv.times(rotationMatrix);              // Rotate 
	    uv=uv.multiply(new vec2(scaleX,scaleY));  // Scale
	    uv=uv.plus(new vec2(offsetX,offsetY));    // Shift
        //uv=mode8(uv);
        pixel= G.texture(colorMap,uv.plus(mode8(uv)));
	}	
	else if (type==9)
	{
	    uv=uv.times(rotationMatrix);              // Rotate 
	    uv=uv.multiply(new vec2(scaleX,scaleY));  // Scale
	    uv=uv.plus(new vec2(offsetX,offsetY));    // Shift
        uv=mode9(uv);
        pixel= G.texture(colorMap,uv);
	}
	else if (type==10)
	{
	    uv=uv.times(rotationMatrix);              // Rotate 
	    uv=uv.multiply(new vec2(scaleX,scaleY));  // Scale
	    uv=uv.plus(new vec2(offsetX,offsetY));    // Shift
        uv=mode10(uv);
        pixel= G.texture(colorMap,uv);
	}
	else if (type==11)
	{
	    uv=uv.times(rotationMatrix);              // Rotate 
	    uv=uv.multiply(new vec2(scaleX,scaleY));  // Scale
	    uv=uv.plus(new vec2(offsetX,offsetY));    // Shift
        uv=mode11(uv);
        pixel= G.texture(colorMap,uv);
        

	}
	else if (type==12)
	{
	    uv=uv.times(rotationMatrix);              // Rotate 
	    uv=uv.multiply(new vec2(scaleX,scaleY));  // Scale
	    uv=uv.plus(new vec2(offsetX,offsetY));    // Shift
        uv=mode12(uv);
        pixel= G.texture(colorMap,uv);
        

	}
	else if (type==13)
	{
	    uv=uv.times(rotationMatrix);              // Rotate 
	    uv=uv.multiply(new vec2(scaleX,scaleY));  // Scale
	    uv=uv.plus(new vec2(offsetX,offsetY));    // Shift
        uv=mode13(uv);
        pixel= G.texture(colorMap,uv);
        

	}
		
    pVarTP.x = pAmount * (x);
    pVarTP.y = pAmount * (y);
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }  
    double dz = offsetZ;
    if (fabs(scaleZ) > EPSILON) {
      double intensity = (0.299 * pixel.x + 0.588 * pixel.y + 0.113 * pixel.z) / 255.0;
      dz += scaleZ * intensity;
    }
    if (resetZ != 0) {
      pVarTP.z = dz;
    } else {
      pVarTP.z += dz;
    }
    if(dc_color==0)
    {
	  pVarTP.rgbColor  =true;
      pVarTP.redColor  =pixel.x;
	  pVarTP.greenColor=pixel.y;
	  pVarTP.blueColor =pixel.z;
    }
    else
	{
		pVarTP.rgbColor  =false;
	    pVarTP.color = getColorIdx(Tools.FTOI(pixel.x), Tools.FTOI(pixel.y), Tools.FTOI(pixel.z));
	}
}

@Override
public String getName() {
	return "kaleidoimg";
}

@Override
public String[] getParameterNames() {
  return paramNames;
}

@Override
public Object[] getParameterValues() {
  return new Object[]{zoom,time,blend_colormap,dc_color,angle,scaleX,scaleY,scaleZ,offsetX,offsetY,offsetZ,tileX,tileY,resetZ,type,nradius,a,b};
	
}

@Override
public void setParameter(String pName, double pValue) {
  if (PARAM_ZOOM.equalsIgnoreCase(pName))
    zoom = pValue;
  else if (PARAM_TIME.equalsIgnoreCase(pName))
    time = pValue;
  else if (PARAM_BLEND.equalsIgnoreCase(pName))
	    blend_colormap = (int) Tools.limitValue(pValue, 0 , 1);
  else if (PARAM_DC.equalsIgnoreCase(pName))
	    dc_color = (int) Tools.limitValue(pValue, 0 , 1);
  else if (PARAM_ANGLE.equalsIgnoreCase(pName))
	    angle = pValue;
  else if (PARAM_SX.equalsIgnoreCase(pName))
	    scaleX = pValue;
  else if (PARAM_SY.equalsIgnoreCase(pName))
	    scaleY = pValue;
  else if (PARAM_SCALEZ.equalsIgnoreCase(pName))
	    scaleZ = pValue;
  else if (PARAM_OX.equalsIgnoreCase(pName))
	    offsetX = pValue;
  else if (PARAM_OY.equalsIgnoreCase(pName))
	    offsetY = pValue;
  else if (PARAM_OFFSETZ.equalsIgnoreCase(pName))
	    offsetZ = pValue;
  else if (PARAM_TILEX.equalsIgnoreCase(pName))
	    tileX = (int) Tools.limitValue(pValue, 0 , 1);
  else if (PARAM_TILEY.equalsIgnoreCase(pName))
	    tileY = (int) Tools.limitValue(pValue, 0 , 1);
  else if (PARAM_RESETZ.equalsIgnoreCase(pName))
    resetZ = (int) Tools.limitValue(pValue, 0 , 1);
  else if (PARAM_TYPE.equalsIgnoreCase(pName))
	    type = (int) Tools.limitValue(pValue, -1 , 5);
  else if (PARAM_NRADIUS.equalsIgnoreCase(pName))
	    nradius = (int) Tools.limitValue(pValue, 1 , 50);
  else if (PARAM_A.equalsIgnoreCase(pName))
		    a = pValue;
	  else if (PARAM_B.equalsIgnoreCase(pName))
		    b = pValue;
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


private WFImage colorMap;
private RenderColor[] renderColors;
private Map<RenderColor, Double> colorIdxMap = new HashMap<RenderColor, Double>();

private double getColorIdx(int pR, int pG, int pB) {
    RenderColor pColor = new RenderColor(pR, pG, pB);
    Double res = colorIdxMap.get(pColor);
    if (res == null) {

      int nearestIdx = 0;
      RenderColor color = renderColors[0];
      double dr, dg, db;
      dr = (color.red - pR);
      dg = (color.green - pG);
      db = (color.blue - pB);
      double nearestDist = sqrt(dr * dr + dg * dg + db * db);
      for (int i = 1; i < renderColors.length; i++) {
        color = renderColors[i];
        dr = (color.red - pR);
        dg = (color.green - pG);
        db = (color.blue - pB);
        double dist = sqrt(dr * dr + dg * dg + db * db);
        if (dist < nearestDist) {
          nearestDist = dist;
          nearestIdx = i;
        }
      }
      res = (double) nearestIdx / (double) (renderColors.length - 1);
      colorIdxMap.put(pColor, res);
    }
    return res;
  }

@Override
public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
	s = Math.sin(angle);
	c = Math.cos(angle);
		    
	mat2 rotationMatrix = new mat2( c, s,
			                       -s,  c);
    colorMap = null;
    renderColors = pLayer.getPalette().createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    if (inlinedImage != null) {
      try {
        colorMap = RessourceManager.getImage(inlinedImageHash, inlinedImage);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (imageFilename != null && imageFilename.length() > 0) {
      try {
        colorMap = RessourceManager.getImage(getCurrImageFilename(pContext));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (colorMap == null) {
      colorMap = getDfltImage();
    }
//    imgWidth = colorMap.getImageWidth();
//    imgHeight = colorMap.getImageHeight();

}

private String getCurrImageFilename(FlameTransformationContext pContext) {

    return imageFilename;
}

private static SimpleImage dfltImage = null;

private synchronized SimpleImage getDfltImage() {
  if (dfltImage == null) {
    GradientCreator creator = new GradientCreator();
    dfltImage = creator.createImage(256, 256);
  }
  return dfltImage;
}

@Override
public String[] getRessourceNames() {
  return ressourceNames;
}

@Override
public byte[][] getRessourceValues() {
  return new byte[][]{(imageFilename != null ? imageFilename.getBytes() : null), inlinedImage, (imageDescSrc != null ? imageDescSrc.getBytes() : null), (imageSrc != null ? imageSrc.getBytes() : null)};
}

@Override
public void setRessource(String pName, byte[] pValue) {
  if (RESSOURCE_IMAGE_FILENAME.equalsIgnoreCase(pName)) {
    imageFilename = pValue != null ? new String(pValue) : "";
    if (imageFilename != null) {
      inlinedImage = null;
      inlinedImageHash = 0;
    }
    clearCurrColorMap();
  } else if (RESSOURCE_INLINED_IMAGE.equalsIgnoreCase(pName)) {
    inlinedImage = pValue;
    inlinedImageHash = RessourceManager.calcHashCode(inlinedImage);
    if (inlinedImage != null) imageFilename = null;
    clearCurrColorMap();
  } else if (RESSOURCE_IMAGE_DESC_SRC.equalsIgnoreCase(pName)) {
    imageDescSrc = pValue != null ? new String(pValue) : "";
  } else if (RESSOURCE_IMAGE_SRC.equalsIgnoreCase(pName)) {
    imageSrc = pValue != null ? new String(pValue) : "";
  } else
    throw new IllegalArgumentException(pName);
}

private void clearCurrColorMap() {
  colorMap = null;
}

@Override
public RessourceType getRessourceType(String pName) {
  if (RESSOURCE_IMAGE_FILENAME.equalsIgnoreCase(pName)) {
    return RessourceType.IMAGE_FILENAME;
  } else if (RESSOURCE_INLINED_IMAGE.equalsIgnoreCase(pName)) {
    return RessourceType.IMAGE_FILE;
  } else if (RESSOURCE_IMAGE_DESC_SRC.equalsIgnoreCase(pName)) {
    return RessourceType.HREF;
  } else if (RESSOURCE_IMAGE_SRC.equalsIgnoreCase(pName)) {
    return RessourceType.HREF;
  } else
    throw new IllegalArgumentException(pName);
}

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE};
  }


}
