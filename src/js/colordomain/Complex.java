
package js.colordomain;

import org.jwildfire.base.mathlib.MathLib;

import odk.lang.FastMath;

public class Complex
{

    double x;
    double y;
    
    public Complex(double u, double v)
    {
        x = u;
        y = v;
    }

    public double real()
    {
        return x;
    }

    public double re()
    {
        return real();
    }

    public double Re()
    {
        return real();
    }

    public double Real()
    {
        return real();
    }

    public double imag()
    {
        return y;
    }

    public double Imag()
    {
        return imag();
    }

    public double Im()
    {
        return imag();
    }

    public double im()
    {
        return imag();
    }

    public double mag () {
        return Math.sqrt(x*x + y*y);
      }
    
    public double mod()
    {
        if(x != 0.0D || y != 0.0D)
            return Math.sqrt(x * x + y * y);
        else
            return 0.0D;
    }

    public double modulus()
    {
        return mod();
    }

    public double Modulus()
    {
        return mod();
    }

    public double Mod()
    {
        return mod();
    }

    public double arg()
    {
        return Math.atan2(y, x);
    }

    public double argument()
    {
        return arg();
    }

    public double Arg()
    {
        return Math.atan2(y, x);
    }

    public double Argument()
    {
        return arg();
    }

    public Complex conj()
    {
        return new Complex(x, -y);
    }

    public Complex conjugate()
    {
        return conj();
    }

    public Complex Conjugate()
    {
        return conj();
    }

    public Complex Conj()
    {
        return conj();
    }

    public Complex plus(Complex w)
    {
        return new Complex(x + w.real(), y + w.imag());
    }

    public Complex add(Complex w)
    {
        return new Complex(x + w.real(), y + w.imag());
    }

    public Complex Plus(Complex w)
    {
        return new Complex(x + w.real(), y + w.imag());
    }

    public Complex Add(Complex w)
    {
        return new Complex(x + w.real(), y + w.imag());
    }

    public Complex minus(Complex w)
    {
        return new Complex(x - w.real(), y - w.imag());
    }

    public Complex Minus(Complex w)
    {
        return new Complex(x - w.real(), y - w.imag());
    }

    public Complex sub(Complex w)
    {
        return new Complex(x - w.real(), y - w.imag());
    }

    public Complex Subtract(Complex w)
    {
        return new Complex(x - w.real(), y - w.imag());
    }

    public Complex times(Complex w)
    {
        return new Complex(x * w.real() - y * w.imag(), x * w.imag() + y * w.real());
    }

    public Complex mul(Complex w)
    {
        return new Complex(x * w.real() - y * w.imag(), x * w.imag() + y * w.real());
    }

    public Complex mult(Complex w)
    {
        return new Complex(x * w.real() - y * w.imag(), x * w.imag() + y * w.real());
    }

    public Complex multiply(Complex w)
    {
        return new Complex(x * w.real() - y * w.imag(), x * w.imag() + y * w.real());
    }

    public Complex Times(Complex w)
    {
        return new Complex(x * w.real() - y * w.imag(), x * w.imag() + y * w.real());
    }

    public Complex Mul(Complex w)
    {
        return new Complex(x * w.real() - y * w.imag(), x * w.imag() + y * w.real());
    }

    public Complex Mult(Complex w)
    {
        return new Complex(x * w.real() - y * w.imag(), x * w.imag() + y * w.real());
    }

    public Complex Multiply(Complex w)
    {
        return new Complex(x * w.real() - y * w.imag(), x * w.imag() + y * w.real());
    }

    public Complex scale(double s)
    {
        return new Complex(x * s, y * s);
    }

    public Complex div(Complex w)
    {
        double den = Math.pow(w.mod(), 2D);
        return new Complex((x * w.real() + y * w.imag()) / den, (y * w.real() - x * w.imag()) / den);
    }

    public Complex divide(Complex w)
    {
        double den = Math.pow(w.mod(), 2D);
        return new Complex((x * w.real() + y * w.imag()) / den, (y * w.real() - x * w.imag()) / den);
    }

    public Complex Div(Complex w)
    {
        double den = Math.pow(w.mod(), 2D);
        return new Complex((x * w.real() + y * w.imag()) / den, (y * w.real() - x * w.imag()) / den);
    }

    public Complex Divide(Complex w)
    {
        double den = Math.pow(w.mod(), 2D);
        return new Complex((x * w.real() + y * w.imag()) / den, (y * w.real() - x * w.imag()) / den);
    }

    public Complex exp()
    {
        return new Complex(Math.exp(x) * Math.cos(y), Math.exp(x) * Math.sin(y));
    }

    public Complex Exp()
    {
        return new Complex(Math.exp(x) * Math.cos(y), Math.exp(x) * Math.sin(y));
    }

    public Complex log()
    {
        return new Complex(Math.log(mod()), arg());
    }

    public Complex Log()
    {
        return new Complex(Math.log(mod()), arg());
    }

    public Complex sqrt()
    {
        double r = Math.sqrt(mod());
        double theta = arg() / 2D;
        return new Complex(r * Math.cos(theta), r * Math.sin(theta));
    }

    public Complex Sqrt()
    {
        return sqrt();
    }

    private double cosh(double theta)
    {
        return (Math.exp(theta) + Math.exp(-theta)) / 2D;
    }

    private double sinh(double theta)
    {
        return (Math.exp(theta) - Math.exp(-theta)) / 2D;
    }
// fix   Ref: https://www.euclideanspace.com/maths/algebra/realNormedAlgebra/complex/functions/index.htm
    public Complex sin()
    {
        return new Complex(cosh(y) * Math.sin(x), sinh(y) * Math.cos(x));
    }
 // fix   Ref: https://www.euclideanspace.com/maths/algebra/realNormedAlgebra/complex/functions/index.htm
    public Complex Sin()
    {
        return new Complex(cosh(y) * Math.sin(x), sinh(y) * Math.cos(x));
    }
 // fix   Ref: https://www.euclideanspace.com/maths/algebra/realNormedAlgebra/complex/functions/index.htm
    public Complex cos()
    {
        return new Complex(cosh(y) * Math.cos(x),  -sinh(y) * Math.sin(x));
    }
 // fix   Ref: https://www.euclideanspace.com/maths/algebra/realNormedAlgebra/complex/functions/index.htm
    //http://www.stumblingrobot.com/2016/02/27/prove-some-properties-of-the-complex-sine-and-cosine-functions/
    public Complex Cos()
    {
        return new Complex(cosh(y) * Math.cos(x), -sinh(y) * Math.sin(x));
    }

    public Complex sinh()
    {
        return new Complex(sinh(x) * Math.cos(y), cosh(x) * Math.sin(y));
    }

    public Complex Sinh()
    {
        return new Complex(sinh(x) * Math.cos(y), cosh(x) * Math.sin(y));
    }

    public Complex cosh()
    {
        return new Complex(cosh(x) * Math.cos(y), sinh(x) * Math.sin(y));
    }

    public Complex Cosh()
    {
        return new Complex(cosh(x) * Math.cos(y), sinh(x) * Math.sin(y));
    }

    public Complex tan()
    {
        return sin().div(cos());
    }

    public Complex Tan()
    {
        return sin().div(cos());
    }
    
///////  more Complex Functions     
    
    public Complex One() {
        return new Complex(1.0,0.0);
      }

      public Complex ImOne() {
          return new Complex(0.0,1.0);
      }

      public Complex Zero() {
          return new Complex(0.0,0.0);
      }
      public Complex Neg() {
    	    double re = -this.re();
    	    double im = -this.im();
    	    return new Complex(re,im);
    	  }
      
      public double Mag2() {
    	    return this.re() * this.re() + this.im() * this.im();
    	  }

    	  public double Mag2eps() { // imprecise for small magnitudes
    	    return this.re() * this.re() + this.im() * this.im() + 1e-20;
    	  }

    	  public double MagInv() {
    	    double M2 = this.Mag2();
    	    return (M2 < 1e-100 ? 1.0 : 1.0 / M2);
    	  }

    	  public Complex Recip() {
    		    double mi = this.MagInv();
    		    double re = this.re() * mi;
    		    double im = -this.im() * mi;
    		    return new Complex(re,im);
    	}

    	public Complex Scale(double mul) {
    	    double re = this.re() * mul;
    	    double im = this.im() * mul;
    	    return new Complex(re,im);
       }
    
       public Complex Inc() {
    	 double     re = this.re()+1.0;
    	 return new Complex(re,this.im());
       }

      public Complex Dec() {
    	double	    re = this.re()- 1.0;
    	return new Complex(re,this.im());
	  }
    	
      public Complex Sqr() {
    	    double r2 = this.re() * this.re() - this.im() * this.im();
    	    double i2 = 2 * this.re() * this.im();
    	    return new Complex(r2,i2);
    	  }
      
//      Complex D = new Complex(this);
//      D.Dec();
//      D.Neg();
//      this.Inc();
//      this.Div(D);
//      this.Log();
//      this.Scale(0.5);
      
    public Complex atanh() {
        Complex d = new Complex(this.Re(),this.im());
        d=d.Dec();
        d=d.Neg();
        Complex e= this.Inc();
        Complex r=e.Div(d);
        Complex x=r.Log().Scale(0.5);
        return x;
      }
    
    public Complex acoth() {
    	
        return this.Recip().atanh();
      }

    // Trascendent functions (slower than others)

    public double Radius() {
      return FastMath.hypot(this.re(), this.im());
    }


    public Complex ToP() {
      return new Complex(this.Radius(), this.Arg());
    }

    public Complex UnP() {
      return new Complex(this.re() * Math.cos(this.im()), this.re() * Math.sin(this.im()));
    }

    public void Norm() {
      this.Scale(Math.sqrt(this.MagInv()));
    }

    public Complex Pow(double exp) {
        // some obvious cases come at first
        // instant evaluation for (-2, -1, -0.5, 0, 0.5, 1, 2)
        if (exp == 0.0) {
          return this.One();
        }
        double ex = MathLib.fabs(exp);
        if (exp < 0) {
          Complex a=Recip();
          this.x=a.re();
          this.y=a.Im();
        }
        if (ex == 0.5) {
          return Sqrt();
        }
        if (ex == 1.0) {
          return this;
        }
        if (ex == 2.0) {
          return Sqr();
        }
        // In general we need sin, cos etc
        Complex PF = ToP();
        PF.x = Math.pow(PF.re(), ex);
        PF.y = PF.im() * ex;
        return PF.UnP();
      }
    
    public Complex CPow(Complex ex) {
        if (ex.im() == 0.0) {
          return this.Pow(ex.re());          
        }
        return this.Log().Mul(ex).Exp();
      }
    
    public Complex asinh() { // slower than AtanH!
        Complex D = new Complex(this.re(),this.im());
        Complex x=D.Sqr().Inc().Pow(0.5);
        return this.Add(x).Log();
      }

      public Complex acosh() { // slower than Atanh!
        Complex D = new Complex(this.Re(),this.im());
        Complex x=D.Sqr().Dec().Pow(0.5);
        return this.Add(x).Log();
      }
      
   
      public Complex asech() {
        return this.Recip().asinh();
      }

      public Complex acosech() {
        return this.Recip().acosh();        
      }
      
///////   end of more Complex Functions       
      
    public Complex chs()
    {
        return new Complex(-x, -y);
    }
  
	//JS added norm complex (ref  http://www.cplusplus.com/reference/complex/norm/)  
	  public static double norm (Complex z) {
		  double u=z.re();
		  double v=z.im();
		  return (u*u + v*v);
	 }
	  

	   public Complex sinhcosh (double x) {
		      double ex = Math.exp(x);
		      double emx = Math.exp(-x);
		      return new Complex( ex + emx, ex - emx).scale(0.5);
		    }    
// bug fix	   
	    public Complex acos () {
	        Complex t1 = new Complex(im() * im() - re() * re() + 1.0, -2.0 * re() * im()).sqrt();
	        Complex t2 = new Complex(t1.x - im(), t1.y + re()).log();
	        return new Complex(Math.PI/2 - t2.y, t2.x);
	      }

	      public Complex asin () {
	        Complex t1 = new Complex(im() * im() - re() * re() + 1.0, -2.0 * re() * im()).sqrt();
	        Complex t2 = new Complex(t1.x - im(), t1.y + re()).log();
	        return new Complex(t2.y, -t2.x);
	      }
	      
	        
	   public Complex tanh() {
	        Complex ez = exp();
	        Complex emz = scale(-1.0).exp();
	        return ez.minus(emz).div(ez.plus(emz));
	      }
	   
	    public Complex atan () {
	        float d = (float) (re() * re() + (1.0 - im()) * (1.0 - im()));
	        Complex t1 = (new Complex(1.0 - im() * im() - re() * re(), -2.0 * re()).scale(1.0 / d)).Log();
	        return new Complex(-t1.im(), t1.re()).scale(0.5);
	      }
	    
    public String toString()
    {
        if(x != 0.0D && y > 0.0D)
            return (new StringBuilder()).append(x).append(" + ").append(y).append("i").toString();
        if(x != 0.0D && y < 0.0D)
            return (new StringBuilder()).append(x).append(" - ").append(-y).append("i").toString();
        if(y == 0.0D)
            return String.valueOf(x);
        if(x == 0.0D)
            return (new StringBuilder()).append(y).append("i").toString();
        else
            return (new StringBuilder()).append(x).append(" + i*").append(y).toString();
    }

}