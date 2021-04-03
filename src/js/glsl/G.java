package js.glsl;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

public class G {

	  public static vec3 texture(WFImage colorMap,vec2 uv)
	  {
//	    Credits:  
//		  https://forum.openframeworks.cc/t/what-does-the-texture-function-in-glsl-do/21196
//	    Based with code from JWildfire AbstractColorWFFunc	
		  
		int imgWidth =colorMap.getImageWidth();
		int imgHeight=colorMap.getImageHeight();  
		int blend_colormap=1;
		Pixel toolPixel = new Pixel();
		float[] rgbArray = new float[3];
		
	  	double pInputX=uv.x;
	  	double pInputY=uv.y;
	      double x = (pInputX - 0.5)  * (double) (imgWidth  - 2);
	      double y = (pInputY - 0.5)  * (double) (imgHeight - 2);
	      int ix, iy;
	      if (blend_colormap > 0) {
	        ix = (int) MathLib.trunc(x);
	        iy = (int) MathLib.trunc(y);
	      } else {
	        ix = Tools.FTOI(x);
	        iy = Tools.FTOI(y);
	      }

	        if (ix < 0) {
	          int nx = ix / imgWidth - 1;
	          ix -= nx * imgWidth;
	        } else if (ix >= imgWidth) {
	          int nx = ix / imgWidth;
	          ix -= nx * imgWidth;
	        }


	        if (iy < 0) {
	          int ny = iy / imgHeight - 1;
	          iy -= ny * imgHeight;
	        } else if (iy >= imgHeight) {
	          int ny = iy / imgHeight;
	          iy -= ny * imgHeight;
	        }


	      double r, g, b;
	      if (ix >= 0 && ix < (imgWidth-1) && iy >= 0 && iy < (imgHeight-1)) {
	        if (colorMap instanceof SimpleImage) {
	          if (blend_colormap > 0) {
	            double iufrac = MathLib.frac(x);
	            double ivfrac = MathLib.frac(y);
	            toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
	                    ix, iy));
	            int lur = toolPixel.r;
	            int lug = toolPixel.g;
	            int lub = toolPixel.b;
	            toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
	                    ix + 1, iy));
	            int rur = toolPixel.r;
	            int rug = toolPixel.g;
	            int rub = toolPixel.b;
	            toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
	                    ix, iy + 1));
	            int lbr = toolPixel.r;
	            int lbg = toolPixel.g;
	            int lbb = toolPixel.b;
	            toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
	                    ix + 1, iy + 1));
	            int rbr = toolPixel.r;
	            int rbg = toolPixel.g;
	            int rbb = toolPixel.b;
	            r = GfxMathLib.blerp(lur, rur, lbr, rbr, iufrac, ivfrac);
	            g = GfxMathLib.blerp(lug, rug, lbg, rbg, iufrac, ivfrac);
	            b = GfxMathLib.blerp(lub, rub, lbb, rbb, iufrac, ivfrac);
	          } else {
	            toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
	                    ix, iy));
	            r = toolPixel.r;
	            g = toolPixel.g;
	            b = toolPixel.b;
	          }
	        } else {
	          if (blend_colormap > 0) {
	            double iufrac = MathLib.frac(x);
	            double ivfrac = MathLib.frac(y);

	            ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy);
	            double lur = rgbArray[0] * 255;
	            double lug = rgbArray[1] * 255;
	            double lub = rgbArray[2] * 255;
	            ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix + 1, iy);
	            double rur = rgbArray[0] * 255;
	            double rug = rgbArray[1] * 255;
	            double rub = rgbArray[2] * 255;
	            ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy + 1);
	            double lbr = rgbArray[0] * 255;
	            double lbg = rgbArray[1] * 255;
	            double lbb = rgbArray[2] * 255;
	            ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix + 1, iy + 1);
	            double rbr = rgbArray[0] * 255;
	            double rbg = rgbArray[1] * 255;
	            double rbb = rgbArray[2] * 255;
	            r = GfxMathLib.blerp(lur, rur, lbr, rbr, iufrac, ivfrac);
	            g = GfxMathLib.blerp(lug, rug, lbg, rbg, iufrac, ivfrac);
	            b = GfxMathLib.blerp(lub, rub, lbb, rbb, iufrac, ivfrac);
	          } else {
	            ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy);
	            r = rgbArray[0] * 255;
	            g = rgbArray[1] * 255;
	            b = rgbArray[2] * 255;
	          }
	        }
	      } else {
	        r = g = b = 0.0;

	      }
	      return new vec3(r,g,b);
	  }	  
	
	
	public static  vec2 Reflect(vec2 incidentVec, vec2 normal)
	{
	 vec2 out = incidentVec.minus(normal.multiply( 2.0 * dot(incidentVec, normal)));
	 return out;
	}
	
	public static vec3 Reflect(vec3 incidentVec, vec3 normal)
	{
	 vec3 out = incidentVec.minus(normal.multiply( 2.0 * dot(incidentVec, normal)));
	 return out;
	}
	
	public static vec2 refract(vec2 I, vec2 N, double eta)
	{
	//For a given incident vector I, surface normal N and ratio of 
	//indices of refraction, eta, refract returns the refraction vector, R. 
	//R is calculated as:
     vec2 R=new vec2(0.0);
     
	double k = 1.0 - eta * eta * (1.0 - dot(N, I) * dot(N, I));
	if (k < 0.0)
	    R = new vec2(0.0); // or genDType(0.0)
	else
	    R =  I.multiply(eta).minus(N.multiply((eta * dot(N, I) + MathLib.sqrt(k))));

	//The input parameters I and N should be normalized in order to 
	//achieve the desired result.
	  return R;
	}
	
	public static vec3 refract(vec3 I, vec3 N, double eta)
	{
	//For a given incident vector I, surface normal N and ratio of 
	//indices of refraction, eta, refract returns the refraction vector, R. 
	//R is calculated as:
     vec3 R=new vec3(0.0);
     
	double k = 1.0 - eta * eta * (1.0 - dot(N, I) * dot(N, I));
	if (k < 0.0)
	    R = new vec3(0.0); // or genDType(0.0)
	else
	    R =  I.multiply(eta).minus(N.multiply((eta * dot(N, I) + MathLib.sqrt(k))));

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
		
		public static  double atan(double n, double d)
		{
			return MathLib.atan(n/d);
		}

		public static   double atan2(double y, double x) {
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
		
		public  static double sqrt(double a)
		{
			return MathLib.sqrt(a);
		}
		
		public  static vec2 sqrt(vec2 a)
		{
			vec2 vret=new vec2(0.0);
			vret.x=MathLib.sqrt(a.x);
			vret.y=MathLib.sqrt(a.y);
			return vret;
		}
		
		public  static vec3 sqrt(vec3 a)
		{
			vec3 vret=new vec3(0.0);
			vret.x=MathLib.sqrt(a.x);
			vret.y=MathLib.sqrt(a.y);
			vret.z=MathLib.sqrt(a.z);
			return vret;
		}
		
		public  static vec4 sqrt(vec4 a)
		{
			vec4 vret=new vec4(0.0);
			vret.x=MathLib.sqrt(a.x);
			vret.y=MathLib.sqrt(a.y);
			vret.z=MathLib.sqrt(a.z);
			vret.w=MathLib.sqrt(a.w);
			return vret;
		}
		
		
		public static   double length(vec2 a)
		{
			return MathLib.sqrt(a.x*a.x+a.y*a.y);
		}
		public static   double length(vec3 a)
		{
			return MathLib.sqrt(a.x*a.x+a.y*a.y+a.z*a.z);
		}
		public static   double length(vec4 a)
		{
			return  MathLib.sqrt(a.x*a.x+a.y*a.y+a.z*a.z+ a.w*a.w);
		}

		public static   double abs(double v)
		{
			double vret=v>=0?v:-v;
			return vret;
		}

		public static   vec2 abs(vec2 v)
		{
			vec2 vret=new vec2(0.0);
			vret.x=v.x>=0?v.x:-v.x;
			vret.y=v.y>=0?v.y:-v.y;
			return vret;
		}

		public static   vec3 abs(vec3 v)
		{
			double x,y,z;
			x=v.x>=0?v.x:-v.x;
			y=v.y>=0?v.y:-v.y;
			z=v.z>=0?v.z:-v.z;
			return new vec3(x,y,z);
		}

		public static   vec4 abs(vec4 v)
		{
			double x,y,z,w;
			x=v.x>=0?v.x:-v.x;
			y=v.y>=0?v.y:-v.y;
			z=v.z>=0?v.z:-v.z;
			w=v.w>=0?v.w:-v.w;
			return new vec4(x,y,z,w);
		}

		public static double sign(double v)
		{
			return Math.signum(v);
		}
		

		public static vec2 sign(vec2 v)
		{
			double x,y;
			x=Math.signum(v.x);
			y=Math.signum(v.y);
			return new vec2(x,y);
		}
			
			public static vec3 sign(vec3 v)
			{
				double x,y,z;
				x=Math.signum(v.x);
				y=Math.signum(v.y);
				z=Math.signum(v.z);
				return new vec3(x,y,z);
			}
			
		public static   double distance(vec2 v1,vec2 v2)
		{
			vec2 dif=v1.minus(v2);
			return MathLib.sqrt(dif.x*dif.x+dif.y*dif.y);
		}

		public static   double distance(vec3 v1,vec3 v2)
		{
			vec3 dif=v1.minus(v2);
			return MathLib.sqrt(dif.x*dif.x+dif.y*dif.y + dif.z*dif.z);
		}
		
		public static   double distance(vec4 v1,vec4 v2)
		{
			vec4 dif=v1.minus(v2);
			return MathLib.sqrt(dif.x*dif.x+dif.y*dif.y + dif.z*dif.z + dif.w*dif.w);
		}
		
		
		public static vec2 normalize(vec2 v1)
		{

			double x= v1.x/length(v1);
			double y=v1.y/length(v1);
			return new vec2(x,y);
		}

		public static   vec3 normalize(vec3 v1)
		{
			double x= v1.x/length(v1);
			double y= v1.y/length(v1);
			double z= v1.z/length(v1);
			return new vec3(x,y,z);
		}
		
		public static   vec4 normalize(vec4 v1)
		{
			double x= v1.x/length(v1);
			double y= v1.y/length(v1);
			double z= v1.z/length(v1);
			double w= v1.w/length(v1);
			return new vec4(x,y,z,w);
		}
		
		public static double pow(double x,double y)
		{
			return  MathLib.pow(x,y);
		}
		
		public static vec2 pow(vec2 x,vec2 y)
		{

			double xr= MathLib.pow(x.x,y.x);
			double yr= MathLib.pow(x.y,y.y);
			return new vec2(xr,yr);
		}

		public static   vec3 pow(vec3 x,vec3 y)
		{
			double xr= MathLib.pow(x.x,y.x);
			double yr= MathLib.pow(x.y,y.y);
			double zr= MathLib.pow(x.z,y.z);
			return new vec3(xr,yr,zr);
		}
		
		public static   vec4 pow(vec4 x,vec4 y)
		{
			double xr= MathLib.pow(x.x,y.x);
			double yr= MathLib.pow(x.y,y.y);
			double zr= MathLib.pow(x.z,y.z);
			double wr= MathLib.pow(x.w,y.w);
			return new vec4(xr,yr,zr,wr);
		}
		
		public static double exp(double x)
		{
			return  MathLib.exp(x);
		}
		
		public static vec2 exp(vec2 x)
		{

			double xr= MathLib.exp(x.x);
			double yr= MathLib.exp(x.y);
			return new vec2(xr,yr);
		}

		public static   vec3 exp(vec3 x)
		{
			double xr= MathLib.exp(x.x);
			double yr= MathLib.exp(x.y);
			double zr= MathLib.exp(x.z);
			return new vec3(xr,yr,zr);
		}
		
		public static   vec4 exp(vec4 x)
		{
			double xr= MathLib.exp(x.x);
			double yr= MathLib.exp(x.y);
			double zr= MathLib.exp(x.z);
			double wr= MathLib.exp(x.w);
			return new vec4(xr,yr,zr,wr);
		}
		
		 public static double exp2(double x)
		 {
		 	return  MathLib.pow(2.,x);
		 }

		  public static vec2 exp2(vec2 x)
		 {

		 	double xr= MathLib.pow(2.0,x.x);
		 	double yr= MathLib.pow(2.0,x.y);
		 	return new vec2(xr,yr);
		 }

		  public static vec3 exp2(vec3 x)
		 {
		 	double xr= MathLib.pow(2.0,x.x);
		 	double yr= MathLib.pow(2.0,x.y);
		 	double zr= MathLib.pow(2.0,x.z);
		 	return new vec3(xr,yr,zr);
		 }

		  public static vec4 exp2(vec4 x)
		 {
		 	double xr= MathLib.pow(2.0,x.x);
		 	double yr= MathLib.pow(2.0,x.y);
		 	double zr= MathLib.pow(2.0,x.z);
		 	double wr= MathLib.pow(2.0,x.w);
		 	return new vec4(xr,yr,zr,wr);
		 }
		
		public static   double clamp(double x,double minVal, double maxVal)
		{
			return Math.min(Math.max(x, minVal), maxVal);
		}

		public static   vec2 clamp(vec2 x,double minVal, double maxVal)
		{
			double vx,vy;
			vx=Math.min(Math.max(x.x, minVal), maxVal);
			vy=Math.min(Math.max(x.y, minVal), maxVal);
			return new vec2(vx,vy);
		}

		public static   vec3 clamp(vec3 x,double minVal, double maxVal)
		{
			double vx,vy,vz;
			vx=Math.min(Math.max(x.x, minVal), maxVal);
			vy=Math.min(Math.max(x.y, minVal), maxVal);
			vz=Math.min(Math.max(x.z, minVal), maxVal);
			return new vec3(vx,vy,vz);
		}

		public static   vec4 clamp(vec4 x,double minVal, double maxVal)
		{
			double vx,vy,vz,vw;
			vx=Math.min(Math.max(x.x, minVal), maxVal);
			vy=Math.min(Math.max(x.y, minVal), maxVal);
			vz=Math.min(Math.max(x.z, minVal), maxVal);
			vw=Math.min(Math.max(x.w, minVal), maxVal);
			return new vec4(vx,vy,vz,vw);
		}

		public static   double mix(double x,double y, double a)
		{
			double z;
			z= (x*(1-a) + y*a);
			return z;
		}

		public static   vec2 mix(vec2 x,vec2 y, double a)
		{
			vec2 z=new vec2(0.0,0.0);
			z.x= (x.x*(1-a) + y.x*a);
			z.y= (x.y*(1-a) + y.y*a);
			return z;
		}  

		public static   vec3 mix(vec3 x,vec3 y, double a)
		{
			vec3 z=new vec3(0.0,0.0,0.0);
			z.x= (x.x*(1-a) + y.x*a);
			z.y= (x.y*(1-a) + y.y*a);
			z.z= (x.z*(1-a) + y.z*a);
			return z;
		}  

		public static   vec4 mix(vec4 x,vec4 y, double a)
		{
			double vx,vy,vz,vw;
			vx= (x.x*(1-a) + y.x*a);
			vy= (x.y*(1-a) + y.y*a);
			vz= (x.z*(1-a) + y.z*a);
			vw= (x.w*(1-a) + y.w*a);
			return new vec4(vx,vy,vz,vw);
		}  



		public static    double log2(double d) {
			return MathLib.log(d)/MathLib.log(2.0);
		}

		public static   double smoothstep(double edge0,double edge1, double x)
		{
			double tval;
			tval = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
			return tval * tval * (3.0 - 2.0 * tval);
		}

		public static   vec2 smoothstep(double e0,double  e1, vec2 x) // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
		{
			vec2 vret=new vec2(0.0);
/*			vret.x =	x.x<e0?0.0:(x.x>=e1)?1.0:0.0;
			vret.y =	x.y<e0?0.0:(x.y>=e1)?1.0:0.0;*/
			vret.x=smoothstep(e0,e1,x.x);
			vret.y=smoothstep(e0,e1,x.y);
			return vret;
		}
		
		
		public static  vec2 smoothstep(vec2 e0,vec2 e1, vec2 x) // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
		{
			vec2 vret=new vec2(0.0);
	/*		vret.x =	x.x<e0.x?0.0:(x.x>=e1.x)?1.0:0.0;
			vret.y =	x.y<e0.y?0.0:(x.y>=e1.y)?1.0:0.0;*/
			vret.x =smoothstep(e0.x,e1.x,x.x);
			vret.y =smoothstep(e0.y,e1.y,x.y);
			return vret;
		}
		public  static vec3 smoothstep(double e0,double  e1, vec3 x) // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
		{
			vec3 vret=new vec3(0.0);
	/*		vret.x =	x.x<=e0?0.0:(x.x>=e1)?1.0:0.0;
			vret.y =	x.y<=e0?0.0:(x.y>=e1)?1.0:0.0;
			vret.z =	x.z<=e0?0.0:(x.z>=e1)?1.0:0.0;*/
			vret.x=smoothstep(e0,e1,x.x);
			vret.y=smoothstep(e0,e1,x.y);
			vret.z=smoothstep(e0,e1,x.z);
			return vret;
		}
		
		public  static vec3 smoothstep(vec3 e0,vec3 e1, vec3 x) // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
		{
			vec3 vret=new vec3(0.0);
	/*		vret.x =	x.x<=e0.x?0.0:(x.x>=e1.x)?1.0:0.0;
			vret.y =	x.y<=e0.y?0.0:(x.y>=e1.y)?1.0:0.0;
			vret.z =	x.z<=e0.z?0.0:(x.z>=e1.z)?1.0:0.0;*/
			vret.x=smoothstep(e0.x,e1.x,x.x);
			vret.y=smoothstep(e0.y,e1.y,x.y);
			vret.z=smoothstep(e0.z,e1.z,x.z);
			return vret;
		}

		public static vec4 smoothstep(double e0,double e1, vec4 x) // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
		{
			vec4 vret=new vec4(0.0);
	/*		vret.x =	x.x<=e0?0.0:(x.x>=e1)?1.0:0.0;
			vret.y =	x.y<=e0?0.0:(x.y>=e1)?1.0:0.0;
			vret.z =	x.z<=e0?0.0:(x.z>=e1)?1.0:0.0;
			vret.w =	x.w<=e0?0.0:(x.w>=e1)?1.0:0.0;*/
			vret.x=smoothstep(e0,e1,x.x);
			vret.y=smoothstep(e0,e1,x.y);
			vret.z=smoothstep(e0,e1,x.z);
			vret.w=smoothstep(e0,e1,x.w);
			return vret;
		}
		
		public static  vec4 smoothstep(vec4 e0,vec4 e1, vec4 x) // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
		{
			vec4 vret=new vec4(0.0);
	/*		vret.x =	x.x<=e0.x?0.0:(x.x>=e1.x)?1.0:0.0;
			vret.y =	x.y<=e0.y?0.0:(x.y>=e1.y)?1.0:0.0;
			vret.z =	x.z<=e0.z?0.0:(x.z>=e1.z)?1.0:0.0;
			vret.w =	x.w<=e0.w?0.0:(x.w>=e1.w)?1.0:0.0;*/
			vret.x=smoothstep(e0.x,e1.x,x.x);
			vret.y=smoothstep(e0.y,e1.y,x.y);
			vret.z=smoothstep(e0.z,e1.z,x.z);
			vret.w=smoothstep(e0.w,e1.w,x.w);
			return vret;
		}
		
		public static   double dot(double v1,double v2)
		{
			return v1*v2;
		}

		public static   double dot(vec2 v1,vec2 v2)
		{
			return v1.x*v2.x + v1.y*v2.y;
		}

		public static   double dot(vec3 v1,vec3 v2)
		{
			return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
		}

		public static   double dot(vec4 v1,vec4 v2)
		{
			return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z + v1.w*v2.w;
		}

		public static vec3 cross(vec3 x,vec3 y)
		{
			double x1,y1,z1;
			x1=x.y*y.z -y.y*x.z;
			y1=x.z*y.x-y.z*x.x;
			z1=x.x*y.y-y.x*x.y;
			return new vec3(x1,y1,z1);
		}
		
		public static   double mod(double x,double y)
		{
			//		 		x- y*floor(x/y)
			return x- y*Math.floor(x/y);
		}

		public static   vec2 mod(vec2 x,double y)
		{
			vec2 vret=new vec2(0.0);
			//		 		x- y*floor(x/y)
			vret.x=	x.x- y*Math.floor(x.x/y);
			vret.y=	x.y- y*Math.floor(x.y/y);
			return vret;
		}

		public static   vec2 mod(vec2 x,vec2 y)
		{
			vec2 vret=new vec2(0.0);
			//		 		x- y*floor(x/y)
			vret.x=	x.x- y.x*Math.floor(x.x/y.x);
			vret.y=	x.y- y.y*Math.floor(x.y/y.y);
			return vret;
		}

		public static   vec3 mod(vec3 x,double y)
		{
			vec3 vret=new vec3(0.0);
			//		 		x- y*floor(x/y)
			vret.x=	x.x- y*Math.floor(x.x/y);
			vret.y=	x.y- y*Math.floor(x.y/y);
			vret.z=	x.z- y*Math.floor(x.z/y);
			return vret;
		}

		public static   vec3 mod(vec3 x,vec3 y)
		{
			vec3 vret=new vec3(0.0);
			//		 		x- y*floor(x/y)
			vret.x=	x.x- y.x*Math.floor(x.x/y.x);
			vret.y=	x.y- y.y*Math.floor(x.y/y.y);
			vret.z=	x.z- y.z*Math.floor(x.z/y.z);
			return vret;
		}

		public static   vec4 mod(vec4 x,double y)
		{
			vec4 vret=new vec4(0.0);
			//		 		x- y*floor(x/y)
			vret.x=	x.x- y*Math.floor(x.x/y);
			vret.y=	x.y- y*Math.floor(x.y/y);
			vret.z=	x.z- y*Math.floor(x.z/y);
			vret.w=	x.w- y*Math.floor(x.w/y);
			return vret;
		}

		public static   double step(double lim, double x)
		{
			if(x < lim)
				return 0.0;
			else
				return 1.0;
		}

		public static   vec2 step(double  lim, vec2 x)
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
		
		public static   vec2 step(vec2 lim, vec2 x)
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

		public static   vec3 step(double lim, vec3 x)
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
		
		public static   vec3 step(vec3 lim, vec3 x)
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

		public static   vec4 step(vec4 lim, vec4 x)
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

		public static   double  floor(double v)
		{
			return Math.floor(v);
		}
		
		public static   vec2 floor(vec2 v)
		{
			vec2 vret=new vec2(0.0);
			vret.x=Math.floor(v.x);
			vret.y=Math.floor(v.y);
			return vret;
		}

		public static   vec3 floor(vec3 v)
		{
			vec3 vret=new vec3(0.0);
			vret.x=Math.floor(v.x);
			vret.y=Math.floor(v.y);
			vret.z=Math.floor(v.z);
			return vret;
		}

		public static   vec4 floor(vec4 v)
		{
			vec4 vret=new vec4(0.0);
			vret.x=Math.floor(v.x);
			vret.y=Math.floor(v.y);
			vret.z=Math.floor(v.z);
			vret.w=Math.floor(v.w);
			return vret;
		}

		public static  double trunc(double d) 
		{
			return (double)((int)d);
		}
		
		
		public static   vec2 trunc(vec2 v)
		{
			vec2 vret=new vec2(0.0);
			vret.x=trunc(v.x);
			vret.y=trunc(v.y);
			return vret;
		}

		public static   vec3 trunc(vec3 v)
		{
			vec3 vret=new vec3(0.0);
			vret.x=trunc(v.x);
			vret.y=trunc(v.y);
			vret.z=trunc(v.z);
			return vret;
		}

		public static   vec4 trunc(vec4 v)
		{
			vec4 vret=new vec4(0.0);
			vret.x=trunc(v.x);
			vret.y=trunc(v.y);
			vret.z=trunc(v.z);
			vret.w=trunc(v.w);
			return vret;
		}
		
		public static  double round(double d) 
		{
			return Math.round(d);
		}
		
		
		public static   vec2 round(vec2 v)
		{
			vec2 vret=new vec2(0.0);
			vret.x=round(v.x);
			vret.y=round(v.y);
			return vret;
		}

		public static   vec3 round(vec3 v)
		{
			vec3 vret=new vec3(0.0);
			vret.x=round(v.x);
			vret.y=round(v.y);
			vret.z=round(v.z);
			return vret;
		}

		public static   vec4 round(vec4 v)
		{
			vec4 vret=new vec4(0.0);
			vret.x=round(v.x);
			vret.y=round(v.y);
			vret.z=round(v.z);
			vret.w=round(v.w);
			return vret;
		}
		
		
		public static  double ceil(double d) 
		{
			return Math.ceil(d);
		}
		
		
		public static   vec2 ceil(vec2 v)
		{
			vec2 vret=new vec2(0.0);
			vret.x=ceil(v.x);
			vret.y=ceil(v.y);
			return vret;
		}

		public static   vec3 ceil(vec3 v)
		{
			vec3 vret=new vec3(0.0);
			vret.x=ceil(v.x);
			vret.y=ceil(v.y);
			vret.z=ceil(v.z);
			return vret;
		}

		public static   vec4 ceil(vec4 v)
		{
			vec4 vret=new vec4(0.0);
			vret.x=ceil(v.x);
			vret.y=ceil(v.y);
			vret.z=ceil(v.z);
			vret.w=ceil(v.w);
			return vret;
		}
		public static   double fract(double x)
		{
			return x-Math.floor(x);
		}

		public static   vec2 fract(vec2 x)
		{
			double xr,yr;

			xr=x.x-Math.floor(x.x);
			yr=x.y-Math.floor(x.y);
			return new vec2(xr,yr);
		}

		public static   vec3 fract(vec3 x)
		{
			double xr,yr,zr;

			xr=x.x-Math.floor(x.x);
			yr=x.y-Math.floor(x.y);
			zr=x.z-Math.floor(x.z);
			return new vec3(xr,yr,zr);
		}


		public static   vec4 fract(vec4 x)
		{
			double xr,yr,zr,wr;

			xr=x.x-Math.floor(x.x);
			yr=x.y-Math.floor(x.y);
			zr=x.z-Math.floor(x.z);
			wr=x.w-Math.floor(x.w);
			return new vec4(xr,yr,zr,wr);
		}

		public static   double min(double x, double y)
		{
			return (y<x)?y:x;
		}
		
		public static   vec2 min(vec2 x, vec2 y)
		{
			vec2 vret=new vec2(0.0);
			vret.x=(y.x<x.x)?y.x:x.x;
			vret.y=(y.y<x.y)?y.y:x.y;
			return vret;
		}
		
		public static   vec3 min(vec3 x, vec3 y)
		{
			vec3 vret=new vec3(0.0);
			vret.x=(y.x<x.x)?y.x:x.x;
			vret.y=(y.y<x.y)?y.y:x.y;
			vret.z=(y.z<x.z)?y.z:x.z;
			return vret;
		}

		public static   vec4 min(vec4 x, vec4 y)
		{
			vec4 vret=new vec4(0.0);
			vret.x=(y.x<x.x)?y.x:x.x;
			vret.y=(y.y<x.y)?y.y:x.y;
			vret.z=(y.z<x.z)?y.z:x.z;
			vret.w=(y.w<x.w)?y.w:x.w;
			return vret;
		}
		
		public static   vec2 min(vec2 x, double y)
		{
			vec2 vret=new vec2(0.0);
			vret.x=(y<x.x)?y:x.x;
			vret.y=(y<x.y)?y:x.y;
			return vret;
		}
		
		public static   vec3 min(vec3 x, double y)
		{
			vec3 vret=new vec3(0.0);
			vret.x=(y<x.x)?y:x.x;
			vret.y=(y<x.y)?y:x.y;
			vret.z=(y<x.z)?y:x.z;
			return vret;
		}

		public static   vec4 min(vec4 x, double y)
		{
			vec4 vret=new vec4(0.0);
			vret.x=(y<x.x)?y:x.x;
			vret.y=(y<x.y)?y:x.y;
			vret.z=(y<x.z)?y:x.z;
			vret.w=(y<x.w)?y:x.w;
			return vret;
		}
		
		public static   double  max(double x,double y)
		{
			return (x<y)?y:x;
		}
		
		public static   vec2 max(vec2 x,vec2 y)
		{
			vec2 vret=new vec2(0.0);
			vret.x=(x.x<y.x)?y.x:x.x;
			vret.y=(x.y<y.y)?y.y:x.y;
			return vret;
		}
		
		public static   vec3 max(vec3 x,vec3 y)
		{
			vec3 vret=new vec3(0.0);
			vret.x=(x.x<y.x)?y.x:x.x;
			vret.y=(x.y<y.y)?y.y:x.y;
			vret.z=(x.z<y.z)?y.z:x.z;
			return vret;
		}
		
		public static   vec4 max(vec4 x,vec4 y)
		{
			vec4 vret=new vec4(0.0);
			vret.x=(x.x<y.x)?y.x:x.x;
			vret.y=(x.y<y.y)?y.y:x.y;
			vret.z=(x.z<y.z)?y.z:x.z;
			vret.w=(x.w<y.w)?y.w:x.w;
			return vret;
		}

		
		public static   vec2 max(vec2 x,double y)
		{
			vec2 vret=new vec2(0.0);
			vret.x=(x.x<y)?y:x.x;
			vret.y=(x.y<y)?y:x.y;
			return vret;
		}
		
		public static   vec3 max(vec3 x,double y)
		{
			vec3 vret=new vec3(0.0);
			vret.x=(x.x<y)?y:x.x;
			vret.y=(x.y<y)?y:x.y;
			vret.z=(x.z<y)?y:x.z;
			return vret;
		}
		
		public static   vec4 max(vec4 x,double y)
		{
			vec4 vret=new vec4(0.0);
			vret.x=(x.x<y)?y:x.x;
			vret.y=(x.y<y)?y:x.y;
			vret.z=(x.z<y)?y:x.z;
			vret.w=(x.w<y)?y:x.w;
			return vret;
		}

		
		public static   double sin(double x)
		{
			return MathLib.sin(x);
		}

		public static   vec2 sin(vec2 x)
		{
			vec2 vret=new vec2(0.0);
			vret.x=MathLib.sin(x.x);
			vret.y=MathLib.sin(x.y);
			return vret;
		}

		public static   vec3 sin(vec3 a)
		{
			vec3 vret=new vec3(0.0);
			vret.x=MathLib.sin(a.x);
			vret.y=MathLib.sin(a.y);
			vret.z=MathLib.sin(a.z);
			return vret;
		}

		public static   vec4 sin(vec4 a)
		{
			vec4 vret=new vec4(0.0);
			vret.x=MathLib.sin(a.x);
			vret.y=MathLib.sin(a.y);
			vret.z=MathLib.sin(a.z);
			vret.w=MathLib.sin(a.w);
			return vret;
		}
		
		public static   double cos(double x)
		{
			return MathLib.cos(x);
		}
		
		public static   vec2 cos(vec2 x)
		{
			vec2 vret=new vec2(0.0);
			vret.x=MathLib.cos(x.x);
			vret.y=MathLib.cos(x.y);
			return vret;
		}

		public static   vec3 cos(vec3 a)
		{
			vec3 vret=new vec3(0.0);
			vret.x=MathLib.cos(a.x);
			vret.y=MathLib.cos(a.y);
			vret.z=MathLib.cos(a.z);
			return vret;
		}

		public static   vec4 cos(vec4 a)
		{
			vec4 vret=new vec4(0.0);
			vret.x=MathLib.cos(a.x);
			vret.y=MathLib.cos(a.y);
			vret.z=MathLib.cos(a.z);
			vret.w=MathLib.cos(a.w);
			return vret;
		}
		
		public static   double  plot(vec2 st, double pct)
		{
			return  smoothstep( pct-0.02, pct, st.y) -
					smoothstep( pct, pct+0.02, st.y);
		}

		public static   double circle(vec2 _st, double _radius){
			vec2 dist = _st.minus( new vec2(0.5));
			return 1.-smoothstep(_radius-(_radius*0.01),
					_radius+(_radius*0.01),
					dot(dist,dist)*4.0);
		}

		public static   vec2 rotate2D(vec2 _st, double _angle){
			vec2 tmp=_st;

			tmp=tmp.minus(new vec2(0.5));


			vec2 tmp1 =  new mat2(MathLib.cos(_angle),-MathLib.sin(_angle),
					MathLib.sin(_angle),MathLib.cos(_angle)).times(tmp); // m[2,2]*v[2]  linear algebra
			tmp = tmp1.plus(new vec2(0.5));
			return tmp;
		}

		public static   vec2 tile(vec2 _st, double _zoom){
			vec2 tmp=_st.multiply(_zoom);
			return fract(tmp);
		}

		public static   vec2 brickTile(vec2 _st, double _zoom)
		{
			vec2 tmp=_st.multiply( _zoom);

			// Here is where the offset is happening
			tmp.x += step(1., mod(tmp.y,2.0)) * 0.5;

			return fract(tmp);
		}

		public static   vec2 truchetPattern( vec2 _st,  double _index)
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

		public static   double  box(vec2 _st, vec2 _size, double _smoothEdges){
			_size= new vec2(0.5).minus(_size.multiply(0.5));
			vec2 aa = new vec2(_smoothEdges*0.5);
			vec2 uv = smoothstep(_size,_size.plus(aa),_st);  // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
			uv =uv.multiply( smoothstep(_size,_size.plus(aa),new vec2(1.0).minus(_st)));  // v2 smoothstep(v2 e0,v2 e1,v2 x)   x<e0?0.0:1.0
			return uv.x*uv.y;
		}

		public static   vec2 skew (vec2 st) {
			vec2 r = new vec2(0.0);
			r.x = 1.1547*st.x;
			r.y = st.y+0.5*r.x;
			return r;
		}

		public static   vec3 simplexGrid (vec2 st)
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

		public static   double random (vec2 st)
		{
			return fract(MathLib.sin(dot(new vec2(st.x,st.y),new vec2(12.9898,78.233)))*43758.5453123);
		}

		public static   vec2 random2( vec2 p ) {
			return fract(sin(new vec2(dot(p,new vec2(127.1,311.7)),dot(p,new vec2(269.5,183.3)))).multiply(43758.5453));
		}


		public static   vec3 fractal(vec2 p)
		{    
			double ITER=50;
			vec2 z = new vec2(0.0);  

			for (int i = 0; i < ITER; ++i) {  
				z = new vec2(z.x * z.x - z.y * z.y, 2. * z.x * z.y).plus(p); 

				if (dot(z,z) > 4.) {
					double s = .125662 * (double) i;
					vec3 tmp = new vec3(MathLib.cos(s + .9), MathLib.cos(s + .3), MathLib.cos(s + .2));
					vec3 tmp1= new vec3( tmp.multiply(0.4).add( .6));
					return tmp1;
				}
			}
			return new vec3(0.0);
		}

		public static   vec2 B(vec2 a) 
		{ 
			return new vec2(MathLib.log(length(a)),atan2(a.y,a.x)-6.3); 
		}

		public static   vec3 F(vec2 E,double _time)
		{
			vec2 e_=E;
			double c=6.;
			int i_max=30;
			for(int i=0; i<i_max; i++)
			{
				vec2 tmp= B( new vec2(e_.x,Math.abs(e_.y)));
				e_=tmp.plus( new vec2(.1*MathLib.sin(_time/3.)-.1,5.+.1*MathLib.cos(_time/5.)));
				c += length(e_);
			}
			double d = log2(log2(c*.05))*6.;
			return new vec3(.7+MathLib.tan(.7*MathLib.cos(d)),.5+.5*MathLib.cos(d-.7),.7+MathLib.sin(.7*MathLib.cos(d-.7)));
		}



		// Created by inigo quilez - iq/2013
		// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
		// http://iquilezles.org/www/articles/voronoise/voronoise.htm
		public static   vec3 hash3( vec2 p ) {
			vec3 q = new vec3( dot(p,new vec2(127.1,311.7)),
					dot(p,new vec2(269.5,183.3)),
					dot(p,new vec2(419.2,371.9)) );
			return fract(sin(q).multiply(43758.5453));
		}

		public static   double iqnoise( vec2 x, double u, double v ) {
			vec2 p = floor(x);
			vec2 f = fract(x);

			double k = 1.0+63.0*MathLib.pow(1.0-v,4.0);

			double va = 0.0;
			double wt = 0.0;
			for (int j=-2; j<=2; j++) {
				for (int i=-2; i<=2; i++) {
					vec2 g = new vec2((double)i,(double)j);
					vec3 o = hash3(p.plus(g)).multiply( new vec3(u,u,1.0));
					vec2 r = g.minus(f).plus(new vec2( o.x,o.y));
					double d = dot(r,r);
					double ww = MathLib.pow( 1.0-smoothstep(0.0,1.414,MathLib.sqrt(d)), k );
					va += o.z*ww;
					wt += ww;
				}
			}

			return va/wt;
		}

		// Based on Morgan McGuire @morgan3d
		// https://www.shadertoy.com/view/4dS3Wd
		public static   double noise ( vec2 st) {
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


		public static   double fbm ( vec2 st) {
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

		public static   double fbm2 ( vec2 _st) {
			double v = 0.0;
			double a = 0.5;
			vec2 shift = new vec2(100.0);
			// Rotate to reduce axial bias
			mat2 rot = new mat2(MathLib.cos(0.5), MathLib.sin(0.5),
					-MathLib.sin(0.5), MathLib.cos(0.50));
			for (int i = 0; i < 5; ++i) {
				v += a * noise(_st);

				_st = rot.times(_st).multiply(new vec2( 2.0)).plus( shift);
				a *= 0.5;
			}
			return v;
		}

		public static   vec3 rgb2hsb(  vec3 c ){
			vec4 K = new vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
			vec4 p = mix(new vec4(c.b,c.g, K.w,K.z), new vec4(c.g,c.b, K.x,K.y),step(c.b, c.g));
			vec4 q = mix(new vec4(p.x,p.y,p.w, c.r),
					new vec4(c.r, p.y,p.z,p.x),
					step(p.x, c.r));
			double d = q.x - Double.min(q.w, q.y);
			double e = 1.0e-10;
			return new vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
		}

		//  Function from Inigo Quiles
		//  https://www.shadertoy.com/view/MsS3Wc
		public static   vec3 hsb2rgb( vec3 c )
		{
			vec3 rgb = clamp(
					abs( mod(new vec3(c.x*6.0).add( new vec3(0.0,4.0,2.0)) ,6.0).minus(3.0)).minus(1.0),
					0.0,
					1.0 );
			rgb = rgb.multiply(rgb).multiply((new vec3(3.0).minus(new vec3(2.0).multiply(rgb))));
			return new vec3(c.z).multiply( mix(new vec3(1.0), rgb, c.y));
		}

		public static   vec4 rgb2hsv( vec4 c)
		{
			vec4 K = new vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
			vec4 p = mix(new vec4(c.b,c.g, K.w,K.z), new vec4(c.g,c.b, K.x,K.y), step(c.b, c.g));
			vec4 q = mix(new vec4(p.x, p.y,p.w, c.r),new vec4(c.r, p.y,p.z,p.x), step(p.x, c.r));
			double d = q.x - Double.min(q.w, q.y);
			double e = 1.0e-10;
			return new vec4(Math.abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x, c.a);
		}


		public static   vec4 hsv2rgb( vec4 c)
		{
			vec4 K = new vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
			vec3 p = abs(fract(new vec3(c.x,c.x,c.x).add(new vec3(K.x,K.y,K.z))).multiply(6.0).minus(new vec3(K.w,K.w,K.w)));
			return new vec4( new vec3(c.z).multiply( mix(new vec3(K.x,K.x,K.x), clamp(p.minus(new vec3( K.x,K.x,K.x)) , 0.0, 1.0), c.y)), c.a);
		}

		public static   vec2 ccon(vec2 z) {
			return new vec2(z.x, -z.y);   
		}

		public static   vec2 cmul( vec2 u,  vec2 v) {
			return new vec2(u.x*v.x - u.y*v.y, u.y*v.x + u.x*v.y);
		}

		vec2 cdiv( vec2 u, vec2 v) {
			return new vec2(u.x*v.x + u.y*v.y, u.y*v.x - u.x*v.y).multiply( 1.0/(v.x*v.x + v.y*v.y));
		}

		public static   double cabs( vec2 z) { return length(z);      }
		public static   double carg( vec2 z) { return atan(z.y, z.x); }

		public static   vec2 cpow(vec2 b, vec2 e){
			double ab = carg(b);
			double lgb = MathLib.log(b.x*b.x + b.y*b.y) / 2.0;
			double lr = MathLib.exp(lgb*e.x - ab*e.y);
			double cis = lgb*e.y + ab*e.x;
			return new vec2(MathLib.cos(cis)*lr, MathLib.sin(cis)*lr);
		}

		public static   vec2 cexp(vec2 z){
			return new vec2(MathLib.cos(z.y),MathLib.sin(z.y)).multiply( MathLib.exp(z.x));
		}

		public static   vec2 clog(vec2 z){
			return new vec2(MathLib.log(z.x*z.x + z.y*z.y) / 2.0, carg(z));
		}

		public static   vec2 f( vec2 p, double _time) { // complex function to graph
			p = new vec2(2.0).multiply( cpow(p, new vec2(3., 0.))).minus( new vec2(0.1).multiply(p)).plus( new vec2(0.04 + 0.03*MathLib.sin(_time*0.2), +0.02*MathLib.cos(_time*0.46)));
			p = cmul(p, cexp(new vec2(0, _time)));
			return p;
		}

		public static   vec2 Kscope(vec2 uv, double k) {
			double angle = Math.abs (mod (atan2 (uv.y, uv.x), 2.0 * k) - k) + 0.1*(0.0);
			return new vec2(length(uv)).multiply(new vec2(MathLib.cos(angle), MathLib.sin(angle)));
		} 	

		public static vec3 colorize(double t, vec3 a, vec3 b, vec3 c, vec3 d) {
			vec3 t1=cos(new vec3 (0.4*Math.PI).multiply(c.multiply(t).add(d)));
		    vec3 col = new vec3(2.5).multiply(a).multiply( b).multiply(t1);  
		    return col;
		}

		public static double v(vec2 coord, double k, double s, double rot) {
		    double cx = MathLib.cos(rot), sy = MathLib.sin(rot);
		    return 0.0 + 0.5 * MathLib.cos((cx * coord.x + sy * coord.y) * k + s);
		}
		
/*		public vec3 getRGBColor(int i,int j)
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
		}	*/
		
		public static mat3 rot (vec3 s) {
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
			
	public static vec3 app (vec3 v, double k, mat3 m)
		{
			for (int i = 0; i < 50; i++) 
			{
				mat3 m1=m.times(k);
				 v= abs(m1.times(v).division(dot(v,v)).multiply(0.5).minus(0.5)).multiply(2.0).minus(1.0); 
		    }return v;
		}	
	
	

	   
	public static double cosh(double val) {
		  double tmp = MathLib.exp(val);
		  return (tmp + 1.0 / tmp) / 2.0;
		}
		 
	public static double tanh(double val) {
		  double tmp = exp(val);
		  return (tmp - 1.0 / tmp) / (tmp + 1.0 / tmp);
		}
		 
	public static double sinh(double val) {
		  double tmp = MathLib.exp(val);
		  return (tmp - 1.0 / tmp) / 2.0;
		}

	public static vec2 cosh(vec2 val) {
		  vec2 ep = exp(val);

		  vec2 tmp= c_add(ep , c_div(c_one(),ep));
		  return  new vec2 ( tmp.x/ 2.0, tmp.y/2.0);
		}
		 
	public static vec2 tanh(vec2 val) {
		  vec2 ep = exp(val);
		  vec2 tmp1 = c_sub(ep, c_div(c_one(),ep ));
		  vec2 tmp2 = c_add(ep, c_div(c_one(),ep ));
		  return c_div(tmp1,tmp2);
		}
		 
	public static vec2 sinh(vec2 val) {
		  vec2 ep = exp(val);
		  vec2 tmp1 = c_sub(ep, c_div(c_one(),ep ));
		  return new vec2(tmp1.x/ 2.0, tmp1.y/2.0);
		}

	public static vec2 c_one()
        { 
			 return new vec2(1., 0.); 
	    }
		
	public static vec2 c_i()
		{ 
			return new vec2(0., 1.); 
		}

	public static vec2 c_ni()
		{ 
			return new vec2(0., -1.); 
		}
		
	public static double arg(vec2 c)
		{
		  return atan(c.y, c.x);
		}

	public static vec2 c_conj(vec2 c)
       {
		  return new vec2(c.x, -c.y);
		}

	public static vec2 c_from_polar(double r, double theta) 
		{
		  return new vec2(r * cos(theta), r * sin(theta));
		}

	public static vec2 c_to_polar(vec2 c) 
		{
		  return new vec2(length(c), atan(c.y, c.x));
		}

		/// Computes `e^(c)`, where `e` is the base of the natural logarithm.
	public static vec2 c_exp(vec2 c) 
		{
		  return c_from_polar(MathLib.exp(c.x), c.y);
		}


		/// Raises a doubleing point number to the complex power `c`.
	public static vec2 c_exp(double base, vec2 c) 
		{
		  return c_from_polar(pow(base, c.x), c.y * MathLib.log(base));
		}

		/// Computes the principal value of natural logarithm of `c`.
	public static vec2 c_ln(vec2 c) {
		  vec2 polar = c_to_polar(c);
		  return new vec2(MathLib.log(polar.x), polar.y);
		}

		/// Returns the logarithm of `c` with respect to an arbitrary base.
	public static vec2 c_log(vec2 c, double base) {
		  vec2 polar = c_to_polar(c);
		  return new vec2(MathLib.log(polar.x)/MathLib.log(base), polar.y/MathLib.log(base));
		}

	public static vec2 c_sqrt(vec2 c) {
		  vec2 p = c_to_polar(c);
		  return c_from_polar(sqrt(p.x), p.y/2.);
		}

		/// Raises `c` to a doubleing point power `e`.
	public static vec2 c_pow(vec2 c, double e) {
		  vec2 p = c_to_polar(c);
		  return c_from_polar(pow(p.x, e), p.y*e);
		}

		/// Raises `c` to a complex power `e`.
	public static vec2 c_pow(vec2 c, vec2 e) {
		  vec2 polar = c_to_polar(c);
		  return c_from_polar(
		     pow(polar.x, e.x) * exp(-e.y * polar.y),
		     e.x * polar.y + e.y * MathLib.log(polar.x)
		  );
		}

	public static vec2 c_add(vec2 a, vec2 b) {
			   
			   return new vec2(a.x+b.x,a.y+b.y);

			}
		   
	public static vec2 c_sub(vec2 a,vec2 b) {

			   return new vec2(a.x-b.x,a.y-b.y);
	       }
		
	public static vec2 c_mul(vec2 self, vec2 other) {
		    return new vec2(self.x * other.x - self.y * other.y, 
		                self.x * other.y + self.y * other.x);
		}

	public static vec2 c_div(vec2 self, vec2 other) {
		    double norm = length(other);
		    return new vec2( (self.x * other.x + self.y * other.y)/(norm * norm),
		                     (self.y * other.x - self.x * other.y)/(norm * norm) );
		}

	public static vec2 c_sin(vec2 c) {
		  return new vec2(sin(c.x) * cosh(c.y), cos(c.x) * sinh(c.y));
		}

	public static vec2 c_cos(vec2 c) {
		  // formula: cos(a + bi) = cos(a)cosh(b) - i*sin(a)sinh(b)
		  return new vec2(cos(c.x) * cosh(c.y), -sin(c.x) * sinh(c.y));
		}

	public static vec2 c_tan(vec2 c) {
		  vec2 c2 = c.multiply(2.0);
		  return new vec2(sin(c2.x), sinh(c2.y)).division((cos(c2.x) + cosh(c2.y)));
		}

		
	public static boolean c_eq(vec2 a, vec2 b)
		{
		  return (a.x==b.x && a.y==b.y);	
		}
		
	public static vec2 c_atan(vec2 c) {
		  // formula: arctan(z) = (ln(1+iz) - ln(1-iz))/(2i)
		  vec2 i = c_i();
		  vec2 ni= c_ni();
		  
		  vec2 one = c_one();
		  vec2 two = one.plus( one);
		  if (c_eq(c,i))
		    return new vec2(0., 1./0.0);
		  else if (c_eq(c,ni)) 
		    return new vec2(0., -1./0.0);

		  return c_div( c_sub( c_ln (c_add( one, c_mul(i,c))), c_ln(c_sub( one, c_mul(i,c)))), c_mul(two,i));
		    
		}

	public static vec2 c_asin(vec2 c) {
		 // formula: arcsin(z) = -i ln(sqrt(1-z^2) + iz)
		  vec2 i = c_i();
		  vec2 ni = c_ni();
		  vec2 one = c_one();
		  return c_mul(ni, c_ln(  c_add( c_sqrt(c_sub( c_one() , c_mul(c, c))) ,  c_mul(i, c)) ));
		}

	public static vec2 c_acos(vec2 c) {
		  // formula: arccos(z) = -i ln(i sqrt(1-z^2) + z)
		  vec2 i = c_i();
		  vec2 ni = c_ni();

		  return c_mul(ni, c_ln(  c_add( c_mul(i, c_sqrt( c_sub(c_one() , c_mul(c, c)))) , c)  ));
		}

	public static vec2 c_sinh(vec2 c) {
		  return new vec2(sinh(c.x) * cos(c.y), cosh(c.x) * sin(c.y));
		}

	public static vec2 c_cosh(vec2 c) {
		  return new vec2(cosh(c.x) * cos(c.y), sinh(c.x) * sin(c.y));
		}

	public static vec2 c_tanh(vec2 c) {
		  vec2 c2 = new vec2(2. * c.x, 2.*c.y);
		  return new vec2( sinh(c2.x)/(cosh(c2.x) + cos(c2.y)) , sin(c2.y)/(cosh(c2.x) + cos(c2.y)));
		}

	public static vec2 c_asinh(vec2 c) {
		  // formula: arcsinh(z) = ln(z + sqrt(1+z^2))
		  vec2 one = c_one();
		  return c_ln(c_add( c , c_sqrt(  c_add( one , c_mul(c, c))    )));
		}

	public static vec2 c_acosh(vec2 c) {
		  // formula: arccosh(z) = 2 ln(sqrt((z+1)/2) + sqrt((z-1)/2))
		  vec2 one = c_one();
		  vec2 two = c_add(one, one);
		  return c_mul(two, c_ln( c_add(c_sqrt(c_div(( c_add(c , one)), two)) , c_sqrt(c_div((c_sub(c , one)), two)) )));
		}

	public static vec2 c_atanh(vec2 c) {
		  // formula: arctanh(z) = (ln(1+z) - ln(1-z))/2
		  vec2 one = c_one();
		  vec2 m_one=new vec2(-one.x,-one.y);
		  vec2 two = one.plus( one);
		  if (c_eq(c, one) )
		  {
		      return new vec2(1./0., 0.);
		  }
		  else if (c_eq(c,m_one))
		  {
		      return new vec2(-1./0., 0.);
		  }
		  return c_div( c_sub( c_ln( c_add(one, c)) , c_ln(c_sub( one ,c))), two);
		}

		// Attempts to identify the gaussian integer whose product with `modulus`
		// is closest to `c`
	public static vec2 c_rem(vec2 c, vec2 modulus) {
		  vec2 c0 = c_div(c, modulus);
		  // This is the gaussian integer corresponding to the true ratio
		  // rounded towards zero.
		  vec2 c1 = new vec2(c0.x - mod(c0.x, 1.), c0.y - mod(c0.y, 1.));
		  return c.minus( c_mul(modulus, c1));
		}

	public static vec2 c_inv(vec2 c) {
		  double norm = length(c);
			return new vec2( c.x/(norm * norm), -c.y/(norm * norm));
		}
}

