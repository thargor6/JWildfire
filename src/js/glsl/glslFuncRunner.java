package js.glsl;

import java.io.StringReader;

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.Scanner;


public class glslFuncRunner {


	public static glslFuncRunner compile(String pScript) throws Exception {
		glslFuncRunner res = (glslFuncRunner) ClassBodyEvaluator.createFastClassBodyEvaluator(new Scanner(null, new StringReader(pScript)), glslFuncRunner.class, (ClassLoader) null);
		return res;

	}

	public  vec2 Reflect(vec2 incidentVec, vec2 normal)
	{
	 vec2 out = incidentVec.minus(normal.multiply( 2.0 * dot(incidentVec, normal)));
	 return out;
	}
	
	public  vec3 Reflect(vec3 incidentVec, vec3 normal)
	{
	 vec3 out = incidentVec.minus(normal.multiply( 2.0 * dot(incidentVec, normal)));
	 return out;
	}
	
	public  vec2 refract(vec2 I, vec2 N, double eta)
	{
	//For a given incident vector I, surface normal N and ratio of 
	//indices of refraction, eta, refract returns the refraction vector, R. 
	//R is calculated as:
     vec2 R=new vec2(0.0);
     
	double k = 1.0 - eta * eta * (1.0 - dot(N, I) * dot(N, I));
	if (k < 0.0)
	    R = new vec2(0.0); // or genDType(0.0)
	else
	    R =  I.multiply(eta).minus(N.multiply((eta * dot(N, I) + Math.sqrt(k))));

	//The input parameters I and N should be normalized in order to 
	//achieve the desired result.
	  return R;
	}
	
	public  vec3 refract(vec3 I, vec3 N, double eta)
	{
	//For a given incident vector I, surface normal N and ratio of 
	//indices of refraction, eta, refract returns the refraction vector, R. 
	//R is calculated as:
     vec3 R=new vec3(0.0);
     
	double k = 1.0 - eta * eta * (1.0 - dot(N, I) * dot(N, I));
	if (k < 0.0)
	    R = new vec3(0.0); // or genDType(0.0)
	else
	    R =  I.multiply(eta).minus(N.multiply((eta * dot(N, I) + Math.sqrt(k))));

	//The input parameters I and N should be normalized in order to 
	//achieve the desired result.
	  return R;
	}
	
    public static float invSqrt(float x) {
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat(i);
        x *= (1.5f - xhalf * x * x);
        return x;
    }
    
    public static double invSqrt(double x) {
        double xhalf = 0.5d * x;
        long i = Double.doubleToLongBits(x);
        i = 0x5fe6ec85e7de30daL - (i >> 1);
        x = Double.longBitsToDouble(i);
        x *= (1.5d - xhalf * x * x);
        return x;
    }
	
	public  double atan(double n, double d)
	{
		return Math.atan(n/d);
	}

	public   double atan2(double y, double x) {
		double coeff_1 = Math.PI / 4d;
		double coeff_2 = 3d * coeff_1;
		double abs_y = Math.abs(y);
		double _angle;
		if (x >= 0d) {
			double r = (x - abs_y) / (x + abs_y);
			_angle = coeff_1 - coeff_1 * r;
		} else {
			double r = (x + abs_y) / (abs_y - x);
			_angle = coeff_2 - coeff_1 * r;
		}
		return y < 0d ? -_angle : _angle;
	}
	
	public   double sqrt(double a)
	{
		return Math.sqrt(a);
	}
	
	public   vec2 sqrt(vec2 a)
	{
		vec2 vret=new vec2(0.0);
		vret.x=Math.sqrt(a.x);
		vret.y=Math.sqrt(a.y);
		return vret;
	}
	
	public   vec3 sqrt(vec3 a)
	{
		vec3 vret=new vec3(0.0);
		vret.x=Math.sqrt(a.x);
		vret.y=Math.sqrt(a.y);
		vret.z=Math.sqrt(a.z);
		return vret;
	}
	
	public   vec4 sqrt(vec4 a)
	{
		vec4 vret=new vec4(0.0);
		vret.x=Math.sqrt(a.x);
		vret.y=Math.sqrt(a.y);
		vret.z=Math.sqrt(a.z);
		vret.w=Math.sqrt(a.w);
		return vret;
	}
	
	public   double length(vec2 a)
	{
		return Math.sqrt(a.x*a.x+a.y*a.y);
	}
	public   double length(vec3 a)
	{
		return Math.sqrt(a.x*a.x+a.y*a.y+a.z*a.z);
	}
	public   double length(vec4 a)
	{
		return  Math.sqrt(a.x*a.x+a.y*a.y+a.z*a.z+ a.w*a.w);
	}

	public   double abs(double v)
	{
		double vret=v>=0?v:-v;
		return vret;
	}

	public   vec2 abs(vec2 v)
	{
		vec2 vret=new vec2(0.0);
		vret.x=v.x>=0?v.x:-v.x;
		vret.y=v.y>=0?v.y:-v.y;
		return vret;
	}

	public   vec3 abs(vec3 v)
	{
		double x,y,z;
		x=v.x>=0?v.x:-v.x;
		y=v.y>=0?v.y:-v.y;
		z=v.z>=0?v.z:-v.z;
		return new vec3(x,y,z);
	}

	public   vec4 abs(vec4 v)
	{
		double x,y,z,w;
		x=v.x>=0?v.x:-v.x;
		y=v.y>=0?v.y:-v.y;
		z=v.z>=0?v.z:-v.z;
		w=v.w>=0?v.w:-v.w;
		return new vec4(x,y,z,w);
	}

	public double sign(double v)
	{
		return Math.signum(v);
	}
	

	public vec2 sign(vec2 v)
	{
		double x,y;
		x=Math.signum(v.x);
		y=Math.signum(v.y);
		return new vec2(x,y);
	}
		
		public vec3 sign(vec3 v)
		{
			double x,y,z;
			x=Math.signum(v.x);
			y=Math.signum(v.y);
			z=Math.signum(v.z);
			return new vec3(x,y,z);
		}
		
	public   double distance(vec2 v1,vec2 v2)
	{
		vec2 dif=v1.minus(v2);
		return Math.sqrt(dif.x*dif.x+dif.y*dif.y);
	}

	public   double distance(vec3 v1,vec3 v2)
	{
		vec3 dif=v1.minus(v2);
		return Math.sqrt(dif.x*dif.x+dif.y*dif.y + dif.z*dif.z);
	}
	
	public   double distance(vec4 v1,vec4 v2)
	{
		vec4 dif=v1.minus(v2);
		return Math.sqrt(dif.x*dif.x+dif.y*dif.y + dif.z*dif.z + dif.w*dif.w);
	}
	
	
	public vec2 normalize(vec2 v1)
	{

		double x= v1.x/length(v1);
		double y=v1.y/length(v1);
		return new vec2(x,y);
	}

	public   vec3 normalize(vec3 v1)
	{
		double x= v1.x/length(v1);
		double y= v1.y/length(v1);
		double z= v1.z/length(v1);
		return new vec3(x,y,z);
	}
	
	public   vec4 normalize(vec4 v1)
	{
		double x= v1.x/length(v1);
		double y= v1.y/length(v1);
		double z= v1.z/length(v1);
		double w= v1.w/length(v1);
		return new vec4(x,y,z,w);
	}
	
	public  double pow(double x,double y)
	{
		return  Math.pow(x,y);
	}
	
	public  vec2 pow(vec2 x,vec2 y)
	{

		double xr= Math.pow(x.x,y.x);
		double yr= Math.pow(x.y,y.y);
		return new vec2(xr,yr);
	}

	public    vec3 pow(vec3 x,vec3 y)
	{
		double xr= Math.pow(x.x,y.x);
		double yr= Math.pow(x.y,y.y);
		double zr= Math.pow(x.z,y.z);
		return new vec3(xr,yr,zr);
	}
	
	public    vec4 pow(vec4 x,vec4 y)
	{
		double xr= Math.pow(x.x,y.x);
		double yr= Math.pow(x.y,y.y);
		double zr= Math.pow(x.z,y.z);
		double wr= Math.pow(x.w,y.w);
		return new vec4(xr,yr,zr,wr);
	}
	
	public   double clamp(double x,double minVal, double maxVal)
	{
		return Math.min(Math.max(x, minVal), maxVal);
	}

	public   vec2 clamp(vec2 x,double minVal, double maxVal)
	{
		double vx,vy;
		vx=Math.min(Math.max(x.x, minVal), maxVal);
		vy=Math.min(Math.max(x.y, minVal), maxVal);
		return new vec2(vx,vy);
	}

	public   vec3 clamp(vec3 x,double minVal, double maxVal)
	{
		double vx,vy,vz;
		vx=Math.min(Math.max(x.x, minVal), maxVal);
		vy=Math.min(Math.max(x.y, minVal), maxVal);
		vz=Math.min(Math.max(x.z, minVal), maxVal);
		return new vec3(vx,vy,vz);
	}

	public   vec4 clamp(vec4 x,double minVal, double maxVal)
	{
		double vx,vy,vz,vw;
		vx=Math.min(Math.max(x.x, minVal), maxVal);
		vy=Math.min(Math.max(x.y, minVal), maxVal);
		vz=Math.min(Math.max(x.z, minVal), maxVal);
		vw=Math.min(Math.max(x.w, minVal), maxVal);
		return new vec4(vx,vy,vz,vw);
	}

	public   double mix(double x,double y, double a)
	{
		double z;
		z= (x*(1-a) + y*a);
		return z;
	}

	public   vec2 mix(vec2 x,vec2 y, double a)
	{
		vec2 z=new vec2(0.0,0.0);
		z.x= (x.x*(1-a) + y.x*a);
		z.y= (x.y*(1-a) + y.y*a);
		return z;
	}  

	public   vec3 mix(vec3 x,vec3 y, double a)
	{
		vec3 z=new vec3(0.0,0.0,0.0);
		z.x= (x.x*(1-a) + y.x*a);
		z.y= (x.y*(1-a) + y.y*a);
		z.z= (x.z*(1-a) + y.z*a);
		return z;
	}  

	public   vec4 mix(vec4 x,vec4 y, double a)
	{
		double vx,vy,vz,vw;
		vx= (x.x*(1-a) + y.x*a);
		vy= (x.y*(1-a) + y.y*a);
		vz= (x.z*(1-a) + y.z*a);
		vw= (x.w*(1-a) + y.w*a);
		return new vec4(vx,vy,vz,vw);
	}  



	public    double log2(double d) {
		return Math.log(d)/Math.log(2.0);
	}

	public   double smoothstep(double edge0,double edge1, double x)
	{
		double tval;
		tval = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
		return tval * tval * (3.0 - 2.0 * tval);
	}

	public   vec2 smoothstep(double e0,double  e1, vec2 x) // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
	{
		vec2 vret=new vec2(0.0);
		vret.x =	x.x<e0?0.0:(x.x>=e1)?1.0:0.0;
		vret.y =	x.y<e0?0.0:(x.y>=e1)?1.0:0.0;
		return vret;
	}
	
	public   vec2 smoothstep(vec2 e0,vec2 e1, vec2 x) // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
	{
		vec2 vret=new vec2(0.0);
		vret.x =	x.x<e0.x?0.0:(x.x>=e1.x)?1.0:0.0;
		vret.y =	x.y<e0.y?0.0:(x.y>=e1.y)?1.0:0.0;
		return vret;
	}
	public   vec3 smoothstep(double e0,double  e1, vec3 x) // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
	{
		vec3 vret=new vec3(0.0);
		vret.x =	x.x<=e0?0.0:(x.x>=e1)?1.0:0.0;
		vret.y =	x.y<=e0?0.0:(x.y>=e1)?1.0:0.0;
		vret.z =	x.z<=e0?0.0:(x.z>=e1)?1.0:0.0;
		return vret;
	}
	
	public   vec3 smoothstep(vec3 e0,vec3 e1, vec3 x) // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
	{
		vec3 vret=new vec3(0.0);
		vret.x =	x.x<=e0.x?0.0:(x.x>=e1.x)?1.0:0.0;
		vret.y =	x.y<=e0.y?0.0:(x.y>=e1.y)?1.0:0.0;
		vret.z =	x.z<=e0.z?0.0:(x.z>=e1.z)?1.0:0.0;
		return vret;
	}

	public   vec4 smoothstep(double e0,double e1, vec4 x) // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
	{
		vec4 vret=new vec4(0.0);
		vret.x =	x.x<=e0?0.0:(x.x>=e1)?1.0:0.0;
		vret.y =	x.y<=e0?0.0:(x.y>=e1)?1.0:0.0;
		vret.z =	x.z<=e0?0.0:(x.z>=e1)?1.0:0.0;
		vret.z =	x.w<=e0?0.0:(x.w>=e1)?1.0:0.0;
		return vret;
	}
	
	public   vec4 smoothstep(vec4 e0,vec4 e1, vec4 x) // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
	{
		vec4 vret=new vec4(0.0);
		vret.x =	x.x<=e0.x?0.0:(x.x>=e1.x)?1.0:0.0;
		vret.y =	x.y<=e0.y?0.0:(x.y>=e1.y)?1.0:0.0;
		vret.z =	x.z<=e0.z?0.0:(x.z>=e1.z)?1.0:0.0;
		vret.z =	x.w<=e0.w?0.0:(x.w>=e1.w)?1.0:0.0;
		return vret;
	}

	
	public   double dot(double v1,double v2)
	{
		return v1*v2;
	}

	public   double dot(vec2 v1,vec2 v2)
	{
		return v1.x*v2.x + v1.y*v2.y;
	}

	public   double dot(vec3 v1,vec3 v2)
	{
		return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
	}

	public   double dot(vec4 v1,vec4 v2)
	{
		return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z + v1.w*v2.w;
	}

	public vec3 cross(vec3 x,vec3 y)
	{
		double x1,y1,z1;
		x1=x.y*y.z -y.y*x.z;
		y1=x.z*y.x-y.z*x.x;
		z1=x.x*y.y-y.x*x.y;
		return new vec3(x1,y1,z1);
	}
	
	public   double mod(double x,double y)
	{
		//		 		x- y*floor(x/y)
		return x- y*Math.floor(x/y);
	}

	public   vec2 mod(vec2 x,double y)
	{
		vec2 vret=new vec2(0.0);
		//		 		x- y*floor(x/y)
		vret.x=	x.x- y*Math.floor(x.x/y);
		vret.y=	x.y- y*Math.floor(x.y/y);
		return vret;
	}

	public   vec2 mod(vec2 x,vec2 y)
	{
		vec2 vret=new vec2(0.0);
		//		 		x- y*floor(x/y)
		vret.x=	x.x- y.x*Math.floor(x.x/y.x);
		vret.y=	x.y- y.y*Math.floor(x.y/y.y);
		return vret;
	}

	public   vec3 mod(vec3 x,double y)
	{
		vec3 vret=new vec3(0.0);
		//		 		x- y*floor(x/y)
		vret.x=	x.x- y*Math.floor(x.x/y);
		vret.y=	x.y- y*Math.floor(x.y/y);
		vret.z=	x.z- y*Math.floor(x.z/y);
		return vret;
	}

	public   vec3 mod(vec3 x,vec3 y)
	{
		vec3 vret=new vec3(0.0);
		//		 		x- y*floor(x/y)
		vret.x=	x.x- y.x*Math.floor(x.x/y.x);
		vret.y=	x.y- y.y*Math.floor(x.y/y.y);
		vret.z=	x.z- y.z*Math.floor(x.z/y.z);
		return vret;
	}

	public   vec4 mod(vec4 x,double y)
	{
		vec4 vret=new vec4(0.0);
		//		 		x- y*floor(x/y)
		vret.x=	x.x- y*Math.floor(x.x/y);
		vret.y=	x.y- y*Math.floor(x.y/y);
		vret.z=	x.z- y*Math.floor(x.z/y);
		vret.w=	x.w- y*Math.floor(x.w/y);
		return vret;
	}

	public   double step(double lim, double x)
	{
		if(x < lim)
			return 0.0;
		else
			return 1.0;
	}

	public   vec2 step(double  lim, vec2 x)
	{
		vec2 vret=new vec2(0.0);

		if (x.x < lim)
			vret.x=0.0;
		else
			vret.x=1.0;

		if((x.y < lim))
			vret.y=0.0;
		else
			vret.y=1.0;
		return vret;
	}
	
	public   vec2 step(vec2 lim, vec2 x)
	{
		vec2 vret=new vec2(0.0);

		if (x.x < lim.x)
			vret.x=0.0;
		else
			vret.x=1.0;

		if((x.y < lim.y))
			vret.y=0.0;
		else
			vret.y=1.0;
		return vret;
	}

	public   vec3 step(double lim, vec3 x)
	{
		vec3 vret=new vec3(0.0);

		if (x.x < lim)
			vret.x=0.0;
		else
			vret.x=1.0;

		if((x.y < lim))
			vret.y=0.0;
		else
			vret.y=1.0;

		if((x.z < lim))
			vret.z=0.0;
		else
			vret.z=1.0;

		return vret;
	}
	
	public   vec3 step(vec3 lim, vec3 x)
	{
		vec3 vret=new vec3(0.0);

		if (x.x < lim.x)
			vret.x=0.0;
		else
			vret.x=1.0;

		if((x.y < lim.y))
			vret.y=0.0;
		else
			vret.y=1.0;

		if((x.z < lim.z))
			vret.z=0.0;
		else
			vret.z=1.0;

		return vret;
	}

	public   vec4 step(vec4 lim, vec4 x)
	{
		vec4 vret=new vec4(0.0);

		if (x.x < lim.x)
			vret.x=0.0;
		else
			vret.x=1.0;

		if((x.y < lim.y))
			vret.y=0.0;
		else
			vret.y=1.0;

		if((x.z < lim.z))
			vret.z=0.0;
		else
			vret.z=1.0;

		if((x.w < lim.w))
			vret.w=0.0;
		else
			vret.w=1.0;

		return vret;
	}

	public   double  floor(double v)
	{
		return Math.floor(v);
	}
	
	public   vec2 floor(vec2 v)
	{
		vec2 vret=new vec2(0.0);
		vret.x=Math.floor(v.x);
		vret.y=Math.floor(v.y);
		return vret;
	}

	public   vec3 floor(vec3 v)
	{
		vec3 vret=new vec3(0.0);
		vret.x=Math.floor(v.x);
		vret.y=Math.floor(v.y);
		vret.z=Math.floor(v.z);
		return vret;
	}

	public   vec4 floor(vec4 v)
	{
		vec4 vret=new vec4(0.0);
		vret.x=Math.floor(v.x);
		vret.y=Math.floor(v.y);
		vret.z=Math.floor(v.z);
		vret.w=Math.floor(v.w);
		return vret;
	}

	public  double trunc(double d) 
	{
		return (double)((int)d);
	}
	
	
	public   vec2 trunc(vec2 v)
	{
		vec2 vret=new vec2(0.0);
		vret.x=trunc(v.x);
		vret.y=trunc(v.y);
		return vret;
	}

	public   vec3 trunc(vec3 v)
	{
		vec3 vret=new vec3(0.0);
		vret.x=trunc(v.x);
		vret.y=trunc(v.y);
		vret.z=trunc(v.z);
		return vret;
	}

	public   vec4 trunc(vec4 v)
	{
		vec4 vret=new vec4(0.0);
		vret.x=trunc(v.x);
		vret.y=trunc(v.y);
		vret.z=trunc(v.z);
		vret.w=trunc(v.w);
		return vret;
	}
	
	public  double round(double d) 
	{
		return Math.round(d);
	}
	
	
	public   vec2 round(vec2 v)
	{
		vec2 vret=new vec2(0.0);
		vret.x=round(v.x);
		vret.y=round(v.y);
		return vret;
	}

	public   vec3 round(vec3 v)
	{
		vec3 vret=new vec3(0.0);
		vret.x=round(v.x);
		vret.y=round(v.y);
		vret.z=round(v.z);
		return vret;
	}

	public   vec4 round(vec4 v)
	{
		vec4 vret=new vec4(0.0);
		vret.x=round(v.x);
		vret.y=round(v.y);
		vret.z=round(v.z);
		vret.w=round(v.w);
		return vret;
	}
	
	
	public  double ceil(double d) 
	{
		return Math.ceil(d);
	}
	
	
	public   vec2 ceil(vec2 v)
	{
		vec2 vret=new vec2(0.0);
		vret.x=ceil(v.x);
		vret.y=ceil(v.y);
		return vret;
	}

	public   vec3 ceil(vec3 v)
	{
		vec3 vret=new vec3(0.0);
		vret.x=ceil(v.x);
		vret.y=ceil(v.y);
		vret.z=ceil(v.z);
		return vret;
	}

	public   vec4 ceil(vec4 v)
	{
		vec4 vret=new vec4(0.0);
		vret.x=ceil(v.x);
		vret.y=ceil(v.y);
		vret.z=ceil(v.z);
		vret.w=ceil(v.w);
		return vret;
	}
	public   double fract(double x)
	{
		return x-Math.floor(x);
	}

	public   vec2 fract(vec2 x)
	{
		double xr,yr;

		xr=x.x-Math.floor(x.x);
		yr=x.y-Math.floor(x.y);
		return new vec2(xr,yr);
	}

	public   vec3 fract(vec3 x)
	{
		double xr,yr,zr;

		xr=x.x-Math.floor(x.x);
		yr=x.y-Math.floor(x.y);
		zr=x.z-Math.floor(x.z);
		return new vec3(xr,yr,zr);
	}


	public   vec4 fract(vec4 x)
	{
		double xr,yr,zr,wr;

		xr=x.x-Math.floor(x.x);
		yr=x.y-Math.floor(x.y);
		zr=x.z-Math.floor(x.z);
		wr=x.w-Math.floor(x.w);
		return new vec4(xr,yr,zr,wr);
	}

	public   double min(double x, double y)
	{
		return (y<x)?y:x;
	}
	
	public   vec2 min(vec2 x, vec2 y)
	{
		vec2 vret=new vec2(0.0);
		vret.x=(y.x<x.x)?y.x:x.x;
		vret.y=(y.y<x.y)?y.y:x.y;
		return vret;
	}
	
	public   vec3 min(vec3 x, vec3 y)
	{
		vec3 vret=new vec3(0.0);
		vret.x=(y.x<x.x)?y.x:x.x;
		vret.y=(y.y<x.y)?y.y:x.y;
		vret.z=(y.z<x.z)?y.z:x.z;
		return vret;
	}

	public   vec4 min(vec4 x, vec4 y)
	{
		vec4 vret=new vec4(0.0);
		vret.x=(y.x<x.x)?y.x:x.x;
		vret.y=(y.y<x.y)?y.y:x.y;
		vret.z=(y.z<x.z)?y.z:x.z;
		vret.w=(y.w<x.w)?y.w:x.w;
		return vret;
	}
	
	public   double  max(double x,double y)
	{
		return (x<y)?y:x;
	}
	
	public   vec2 max(vec2 x,vec2 y)
	{
		vec2 vret=new vec2(0.0);
		vret.x=(x.x<y.x)?y.x:x.x;
		vret.y=(x.y<y.y)?y.y:x.y;
		return vret;
	}
	
	public   vec3 max(vec3 x,vec3 y)
	{
		vec3 vret=new vec3(0.0);
		vret.x=(x.x<y.x)?y.x:x.x;
		vret.y=(x.y<y.y)?y.y:x.y;
		vret.z=(x.z<y.z)?y.z:x.z;
		return vret;
	}
	
	public   vec4 max(vec4 x,vec4 y)
	{
		vec4 vret=new vec4(0.0);
		vret.x=(x.x<y.x)?y.x:x.x;
		vret.y=(x.y<y.y)?y.y:x.y;
		vret.z=(x.z<y.z)?y.z:x.z;
		vret.w=(x.w<y.w)?y.w:x.w;
		return vret;
	}

	public   double sin(double x)
	{
		return Math.sin(x);
	}

	public   vec2 sin(vec2 x)
	{
		vec2 vret=new vec2(0.0);
		vret.x=Math.sin(x.x);
		vret.y=Math.sin(x.y);
		return vret;
	}

	public   vec3 sin(vec3 a)
	{
		vec3 vret=new vec3(0.0);
		vret.x=Math.sin(a.x);
		vret.y=Math.sin(a.y);
		vret.z=Math.sin(a.z);
		return vret;
	}

	public   vec4 sin(vec4 a)
	{
		vec4 vret=new vec4(0.0);
		vret.x=Math.sin(a.x);
		vret.y=Math.sin(a.y);
		vret.z=Math.sin(a.z);
		vret.w=Math.sin(a.w);
		return vret;
	}
	
	public   double cos(double x)
	{
		return Math.cos(x);
	}
	
	public   vec2 cos(vec2 x)
	{
		vec2 vret=new vec2(0.0);
		vret.x=Math.cos(x.x);
		vret.y=Math.cos(x.y);
		return vret;
	}

	public   vec3 cos(vec3 a)
	{
		vec3 vret=new vec3(0.0);
		vret.x=Math.cos(a.x);
		vret.y=Math.cos(a.y);
		vret.z=Math.cos(a.z);
		return vret;
	}

	public   vec4 cos(vec4 a)
	{
		vec4 vret=new vec4(0.0);
		vret.x=Math.cos(a.x);
		vret.y=Math.cos(a.y);
		vret.z=Math.cos(a.z);
		vret.w=Math.cos(a.w);
		return vret;
	}
	
	public   double  plot(vec2 st, double pct)
	{
		return  smoothstep( pct-0.02, pct, st.y) -
				smoothstep( pct, pct+0.02, st.y);
	}

	public   double circle(vec2 _st, double _radius){
		vec2 dist = _st.minus( new vec2(0.5));
		return 1.-smoothstep(_radius-(_radius*0.01),
				_radius+(_radius*0.01),
				dot(dist,dist)*4.0);
	}

	public   vec2 rotate2D(vec2 _st, double _angle){
		vec2 tmp=_st;

		tmp=tmp.minus(new vec2(0.5));


		vec2 tmp1 =  new mat2(Math.cos(_angle),-Math.sin(_angle),
				Math.sin(_angle),Math.cos(_angle)).times(tmp); // m[2,2]*v[2]  linear algebra
		tmp = tmp1.plus(new vec2(0.5));
		return tmp;
	}

	public   vec2 tile(vec2 _st, double _zoom){
		vec2 tmp=_st.multiply(_zoom);
		return fract(tmp);
	}

	public   vec2 brickTile(vec2 _st, double _zoom)
	{
		vec2 tmp=_st.multiply( _zoom);

		// Here is where the offset is happening
		tmp.x += step(1., mod(tmp.y,2.0)) * 0.5;

		return fract(tmp);
	}

	public   vec2 truchetPattern( vec2 _st,  double _index)
	{
		_index = fract(((_index-0.5)*2.0));
		if (_index > 0.75) {
			_st = new vec2(1.0).minus(_st);
		} else if (_index > 0.5) {
			_st = new vec2(1.-_st.x,_st.y);
		} else if (_index > 0.25) {
			_st = new vec2(1.0).minus(new vec2(1.0-_st.x,_st.y));
		}
		return _st;
	}

	public   double  box(vec2 _st, vec2 _size, double _smoothEdges){
		_size= new vec2(0.5).minus(_size.multiply(0.5));
		vec2 aa = new vec2(_smoothEdges*0.5);
		vec2 uv = smoothstep(_size,_size.plus(aa),_st);  // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
		uv =uv.multiply( smoothstep(_size,_size.plus(aa),new vec2(1.0).minus(_st)));  // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
		return uv.x*uv.y;
	}

	public   vec2 skew (vec2 st) {
		vec2 r = new vec2(0.0);
		r.x = 1.1547*st.x;
		r.y = st.y+0.5*r.x;
		return r;
	}

	public   vec3 simplexGrid (vec2 st)
	{
		vec3 xyz = new vec3(0.0);
		vec2 tmp=new vec2(0.0);
		vec2 p = fract(skew(st));
		if (p.x > p.y) {
			tmp=new vec2(1.0).minus(new vec2(p.x,p.y-p.x));
			xyz.x = tmp.x;
			xyz.y = tmp.y;
			xyz.z = p.y;
		} else {
			tmp=new vec2(1.0).minus(new vec2(p.x-p.y,p.y));
			xyz.y = tmp.x;
			xyz.z=  tmp.y;
			xyz.x = p.x;
		}

		return fract(xyz);
	}

	public   double random (vec2 st)
	{
		return fract(Math.sin(dot(new vec2(st.x,st.y),new vec2(12.9898,78.233)))*43758.5453123);
	}

	public   vec2 random2( vec2 p ) {
		return fract(sin(new vec2(dot(p,new vec2(127.1,311.7)),dot(p,new vec2(269.5,183.3)))).multiply(43758.5453));
	}


	public   vec3 fractal(vec2 p)
	{    
		double ITER=50;
		vec2 z = new vec2(0.0);  

		for (int i = 0; i < ITER; ++i) {  
			z = new vec2(z.x * z.x - z.y * z.y, 2. * z.x * z.y).plus(p); 

			if (dot(z,z) > 4.) {
				double s = .125662 * (double) i;
				vec3 tmp = new vec3(Math.cos(s + .9), Math.cos(s + .3), Math.cos(s + .2));
				vec3 tmp1= new vec3( tmp.multiply(0.4).add( .6));
				return tmp1;
			}
		}
		return new vec3(0.0);
	}

	public   vec2 B(vec2 a) 
	{ 
		return new vec2(Math.log(length(a)),atan2(a.y,a.x)-6.3); 
	}

	public   vec3 F(vec2 E,double _time)
	{
		vec2 e_=E;
		double c=6.;
		int i_max=30;
		for(int i=0; i<i_max; i++)
		{
			vec2 tmp= B( new vec2(e_.x,Math.abs(e_.y)));
			e_=tmp.plus( new vec2(.1*Math.sin(_time/3.)-.1,5.+.1*Math.cos(_time/5.)));
			c += length(e_);
		}
		double d = log2(log2(c*.05))*6.;
		return new vec3(.7+Math.tan(.7*Math.cos(d)),.5+.5*Math.cos(d-.7),.7+Math.sin(.7*Math.cos(d-.7)));
	}



	// Created by inigo quilez - iq/2013
	// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
	// http://iquilezles.org/www/articles/voronoise/voronoise.htm
	public   vec3 hash3( vec2 p ) {
		vec3 q = new vec3( dot(p,new vec2(127.1,311.7)),
				dot(p,new vec2(269.5,183.3)),
				dot(p,new vec2(419.2,371.9)) );
		return fract(sin(q).multiply(43758.5453));
	}

	public   double iqnoise( vec2 x, double u, double v ) {
		vec2 p = floor(x);
		vec2 f = fract(x);

		double k = 1.0+63.0*Math.pow(1.0-v,4.0);

		double va = 0.0;
		double wt = 0.0;
		for (int j=-2; j<=2; j++) {
			for (int i=-2; i<=2; i++) {
				vec2 g = new vec2((double)i,(double)j);
				vec3 o = hash3(p.plus(g)).multiply( new vec3(u,u,1.0));
				vec2 r = g.minus(f).plus(new vec2( o.x,o.y));
				double d = dot(r,r);
				double ww = Math.pow( 1.0-smoothstep(0.0,1.414,Math.sqrt(d)), k );
				va += o.z*ww;
				wt += ww;
			}
		}

		return va/wt;
	}

	// Based on Morgan McGuire @morgan3d
	// https://www.shadertoy.com/view/4dS3Wd
	public   double noise ( vec2 st) {
		vec2 i = floor(st);
		vec2 f = fract(st);

		// Four corners in 2D of a tile
		double a = random(i);
		double b = random(i.plus(new vec2(1.0, 0.0)));
		double c = random(i.plus(new vec2(0.0, 1.0)));
		double d = random(i.plus( new vec2(1.0, 1.0)));

		vec2 u = f.multiply( f).multiply(new vec2(3.0).minus(new vec2(2.0).multiply(f)));

		return mix(a, b, u.x) +
				(c - a)* u.y * (1.0 - u.x) +
				(d - b) * u.x * u.y;
	}


	public   double fbm ( vec2 st) {
		// Initial values
		double value = 0.0;
		double amplitude = .5;
		double frequency = 0.;
		int OCTAVES=6;
		//
		// Loop of octaves
		for (int i = 0; i < OCTAVES; i++) {
			value += amplitude * noise(st);
			st =st.multiply(2.);
			amplitude *= .5;
		}
		return value;
	}

	public   double fbm2 ( vec2 _st) {
		double v = 0.0;
		double a = 0.5;
		vec2 shift = new vec2(100.0);
		// Rotate to reduce axial bias
		mat2 rot = new mat2(Math.cos(0.5), Math.sin(0.5),
				-Math.sin(0.5), Math.cos(0.50));
		for (int i = 0; i < 5; ++i) {
			v += a * noise(_st);

			_st = rot.times(_st).multiply(new vec2( 2.0)).plus( shift);
			a *= 0.5;
		}
		return v;
	}

	public   vec3 rgb2hsb(  vec3 c ){
		vec4 K = new vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
		vec4 p = mix(new vec4(c.b,c.g, K.w,K.z), new vec4(c.g,c.b, K.x,K.y),step(c.b, c.g));
		vec4 q = mix(new vec4(p.x,p.y,p.w, c.r),
				new vec4(c.r, p.y,p.z,p.x),
				step(p.x, c.r));
		double d = q.x - Double.min(q.w, q.y);
		double e = 1.0e-10;
		return new vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
	}

	//  Function from Iñigo Quiles
	//  https://www.shadertoy.com/view/MsS3Wc
	public   vec3 hsb2rgb( vec3 c )
	{
		vec3 rgb = clamp(
				abs( mod(new vec3(c.x*6.0).add( new vec3(0.0,4.0,2.0)) ,6.0).minus(3.0)).minus(1.0),
				0.0,
				1.0 );
		rgb = rgb.multiply(rgb).multiply((new vec3(3.0).minus(new vec3(2.0).multiply(rgb))));
		return new vec3(c.z).multiply( mix(new vec3(1.0), rgb, c.y));
	}

	public   vec4 rgb2hsv( vec4 c)
	{
		vec4 K = new vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
		vec4 p = mix(new vec4(c.b,c.g, K.w,K.z), new vec4(c.g,c.b, K.x,K.y), step(c.b, c.g));
		vec4 q = mix(new vec4(p.x, p.y,p.w, c.r),new vec4(c.r, p.y,p.z,p.x), step(p.x, c.r));
		double d = q.x - Double.min(q.w, q.y);
		double e = 1.0e-10;
		return new vec4(Math.abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x, c.a);
	}


	public   vec4 hsv2rgb( vec4 c)
	{
		vec4 K = new vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
		vec3 p = abs(fract(new vec3(c.x,c.x,c.x).add(new vec3(K.x,K.y,K.z))).multiply(6.0).minus(new vec3(K.w,K.w,K.w)));
		return new vec4( new vec3(c.z).multiply( mix(new vec3(K.x,K.x,K.x), clamp(p.minus(new vec3( K.x,K.x,K.x)) , 0.0, 1.0), c.y)), c.a);
	}

	public   vec2 ccon(vec2 z) {
		return new vec2(z.x, -z.y);   
	}

	public   vec2 cmul( vec2 u,  vec2 v) {
		return new vec2(u.x*v.x - u.y*v.y, u.y*v.x + u.x*v.y);
	}

	vec2 cdiv( vec2 u, vec2 v) {
		return new vec2(u.x*v.x + u.y*v.y, u.y*v.x - u.x*v.y).multiply( 1.0/(v.x*v.x + v.y*v.y));
	}

	public   double cabs( vec2 z) { return length(z);      }
	public   double carg( vec2 z) { return atan(z.y, z.x); }

	public   vec2 cpow(vec2 b, vec2 e){
		double ab = carg(b);
		double lgb = Math.log(b.x*b.x + b.y*b.y) / 2.0;
		double lr = Math.exp(lgb*e.x - ab*e.y);
		double cis = lgb*e.y + ab*e.x;
		return new vec2(Math.cos(cis)*lr, Math.sin(cis)*lr);
	}

	public   vec2 cexp(vec2 z){
		return new vec2(Math.cos(z.y),Math.sin(z.y)).multiply( Math.exp(z.x));
	}

	public   vec2 clog(vec2 z){
		return new vec2(Math.log(z.x*z.x + z.y*z.y) / 2.0, carg(z));
	}

	public   vec2 f( vec2 p, double _time) { // complex function to graph
		p = new vec2(2.0).multiply( cpow(p, new vec2(3., 0.))).minus( new vec2(0.1).multiply(p)).plus( new vec2(0.04 + 0.03*Math.sin(_time*0.2), +0.02*Math.cos(_time*0.46)));
		p = cmul(p, cexp(new vec2(0, _time)));
		return p;
	}

	public   vec2 Kscope(vec2 uv, double k) {
		double angle = Math.abs (mod (atan2 (uv.y, uv.x), 2.0 * k) - k) + 0.1*(0.0);
		return new vec2(length(uv)).multiply(new vec2(Math.cos(angle), Math.sin(angle)));
	} 	

	public vec3 colorize(double t, vec3 a, vec3 b, vec3 c, vec3 d) {
		vec3 t1=cos(new vec3 (0.4*Math.PI).multiply(c.multiply(t).add(d)));
	    vec3 col = new vec3(2.5).multiply(a).multiply( b).multiply(t1);  
	    return col;
	}

	public double v(vec2 coord, double k, double s, double rot) {
	    double cx = Math.cos(rot), sy = Math.sin(rot);
	    return 0.0 + 0.5 * Math.cos((cx * coord.x + sy * coord.y) * k + s);
	}
	
	public vec3 getRGBColor(int i,int j)
	{
		double xt=(double)i +0.5;
		double yt=(double)j +0.5;
		vec2 st=new vec2(xt/10000,yt/10000);
		vec3 color=new vec3(0.0);

		st.x=st.x*2.0-1.0;
		st.y=st.y*2.0-1.0;

		vec2 c = st.multiply(new vec2(10000/10000,1.0).multiply(2).minus(new vec2(0.5,0.0)));
		color = fractal(c);        
		return color;
	}	
	
	public mat3 rot (vec3 s) {
			double 	sa = sin(s.x),
					ca = cos(s.x),
					sb = sin(s.y),
					cb = cos(s.y),
					sc = sin(s.z),
					cc = cos(s.z);
			return new mat3 (
				new vec3(cb*cc, -cb*sc, sb),
				new vec3(sa*sb*cc+ca*sc, -sa*sb*sc+ca*cc, -sa*cb),
				new vec3(-ca*sb*cc+sa*sc, ca*sb*sc+sa*cc, ca*cb)
			);
		}
		
public vec3 app (vec3 v, double k, mat3 m)
	{
		for (int i = 0; i < 50; i++) 
		{
			mat3 m1=m.times(k);
			 v= abs(m1.times(v).division(dot(v,v)).multiply(0.5).minus(0.5)).multiply(2.0).minus(1.0); 
	    }return v;
	}

/*	public vec3 getRGBColor0(int i,int j)
	{
		double x=(double)i +0.5;
		double y=(double)j +0.5;
		vec2 uv=new vec2(x/resolutionX,y/resolutionY);
		vec3 col=new vec3(0.0);

	    double t = 0.05*time;
	    
	    mat3 m = rot(new vec3(t).add( new vec3(1,2,3)));
	    double k = 1.2+0.1*sin(0.1*time);
	    
	    double f1=(2.+0.25*sin(0.3*time));
	    vec3 v = m.times(f1).times( new vec3(new vec2(2.0).multiply(uv) ,0.0) );
	    
	    col = sin(app(v,k,m)).multiply(0.6).add(0.5);
		
		return col;
	}*/
	
/*	public vec3 hsv2rgb( vec3 c) 
	{
		
		 // vec3 rgb = clamp(abs(mod(c.x * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
		
		 vec3 t1= mod( new vec3(c.x * 6.0).add( new vec3(0.0, 4.0, 2.0)), 6.0);
		 vec3 t2=abs(t1.minus(3.0)).minus(1.0);
		 vec3 rgb = clamp(t2, 0.0, 1.0);
		 vec3 t3=new vec3(3.0).minus(new vec3(2.0).multiply(rgb));
		 
		rgb = rgb.multiply(rgb).multiply(t3);
		return new vec3(c.z).multiply( mix(new vec3(1.0), rgb, c.y));
	}*/
	
   
/*	public vec3 getRGBColor0(int i,int j)
	{
	        double x=(double)i +0.5;
	        double y=(double)j +0.5;
	 //       vec2 st=new vec2(x/resolutionX,y/resolutionY);
	        vec3 c1=new vec3 (0.4,0.33,0.5);
	        vec3 c2=new vec3 (0.9,0.9,0.9);
	        vec3 c3=new vec3 (0.9,0.5,0.33);
	        vec3 c4=new vec3 (0.8,0.6,0.9);
	        
	        double K=0.7;
	        double scale=1.75;
	        
	        vec2 uv=new vec2(resolutionX-0.5*resolutionX,resolutionY-0.5*resolutionY);

	        double t=0.0;
	        double l=Math.floor(K);

	    for (int k=0;k<12;k++)
	    {
	            if((double)k>=l)
	               break;
	            double s= (double)k*Math.PI/l;
	            double w= v(uv,2.1-scale,0.0,s);
	            t+=w/0.5;        
	    }
		vec3 color=colorize(t, c1, c2, c3, c4);
	    return color;
	}*/

	
/*	double rate=1.;
	double intensity=0.03;
	double focus=1.5;
	double pulse=10.;
	double glow=9.;
	double zoom=.926;
	double loops=15.;
	vec2 center=new vec2(0.0);
	
	double S=(101.0+glow)*intensity;
	vec3 color=new vec3(0.0);



	public vec3 formula(vec2 z, double t)
	{
	double M=0.0;
	double o,ot2,ot=ot2=1000.0;
	double K=floor(loops/4.0+floor(5.0*zoom));
	vec3 color=new vec3(0.0);

		for (int i=0; i<11; i++) {
			z = abs(z).division(clamp(dot(z, z), 0.1, 0.5)).minus( t);
			double l = length(z);
			o = min(max(abs(min(z.x, z.y)), -l + 0.25), abs(l - 0.25));
			ot = min(ot, o);
			ot2 = min(l * 0.1, ot2);
			M = max(M, (double)i * (1.0 - abs(sign(ot - o))));
			if (K <= 0.0) 
	           break;
			K -= 1.0;
		}
		M += 1.0;
		double w = (intensity * zoom) * M;
		double circ = Math.pow(max(0.0, w - ot2) / w, 6.0);
		S += max(Math.pow(max(0.0, w - ot) / w, 0.25), circ);
	    vec3 t1=new vec3(0.1).add(new vec3(0.45,0.75,M*0.1));
		vec3 col = normalize(t1);
	    vec3 t2=new vec3(0.4 + mod(M / 9.0 - t * pulse + ot2 * 2.0, 1.));
	    color=color.add(col.multiply(t2));
	    double f1=circ * (10.0 - M) * 3.0;
	    color=color.add(new vec3(1.0,0.7,0.3).multiply(f1));
	    return color;
	}

	public vec3 getRGBColor0(int i,int j)
	{
		
		
		
	  double xt=(double)i +0.5;
	  double yt=(double)j +0.5;
	  vec2 pos=new vec2(xt/resolutionX-0.5,yt/resolutionY-0.5);
	  vec3 color=new vec3(0.0);

		double R = 0.0;
		//float N = u_time * 0.01 * rate;
	    double N=0.0;
		double  T = 2.0 * rate;
		if (N > 6.0 * rate) { 
			R += 1.0;
			N -= (R * 8.0 * rate);
		}
		if (N < 4.0 * rate) T += N;
		else  T = 8.0 * rate - N;
		double Z = (1.05-zoom);

		vec2 uv = pos.plus(center);
		double sph = length(uv)*0.1; 
		sph = Math.sqrt(1.0 - sph * sph) * 2.0 ;
		double a = T * Math.PI;
		double b = a + T;
		double c = cos(a) + sin(b);
		uv = uv.times(new mat2(cos(b), sin(b), -sin(b), cos(b)));
		uv = uv.times(new mat2(cos(a),-sin(a), sin(a),cos(a)));
		uv = uv.minus(new vec2(sin(c), cos(c)).division( Math.PI));
		uv = uv.multiply(Z);
		double pix = 0.5 / resolutionX * Z / sph;
		double dof = (zoom * focus) + (T * 0.25);
		double L = floor(loops);
		for (int aa=0; aa<24; aa++) {
			vec2 aauv = floor(new vec2( (double)aa / 6.0, mod((double)aa, 6.0)));
			color=formula(uv.plus(aauv.multiply(pix).multiply( dof)), T);
			if (L <= 0.0) 
				break;
			L -= 1.0;
		}
		S /= floor(loops); 
		color = color.division(floor(loops));
		vec3 colo = mix(new vec3(0.15), color, S).multiply((1.0 - length(pos)));	
		colo =colo.multiply(new vec3(1.2, 1.1, 1.0));
	  
	 return colo;
	}*/

	
}
