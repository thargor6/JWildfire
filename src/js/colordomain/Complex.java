// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 9/23/2018 10:56:18 AM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Complex.java

package js.colordomain;



public class Complex
{

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

    public Complex sin()
    {
        return new Complex(cosh(y) * Math.sin(x), sinh(y) * Math.cos(x));
    }

    public Complex Sin()
    {
        return new Complex(cosh(y) * Math.sin(x), sinh(y) * Math.cos(x));
    }

    public Complex cos()
    {
        return new Complex(cosh(y) * Math.cos(x), -sinh(y) * Math.sin(x));
    }

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

    public Complex chs()
    {
        return new Complex(-x, -y);
    }
  
	//JS added norm complex (ref  http://www.cplusplus.com/reference/complex/norm/)  
	  public double norm (Complex z) {
		  double u=z.re();
		  double v=z.im();
		  return (u*u + v*v);
	 }
	  

	   public Complex sinhcosh (double x) {
		      double ex = Math.exp(x);
		      double emx = Math.exp(-x);
		      return new Complex( ex + emx, ex - emx).scale(0.5);
		    }    
	   
	    public Complex acos () {
	        Complex t1 = new Complex(re() * im() - re() * re() + 1.0, -2.0 * re() * im()).sqrt();
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

    private double x;
    private double y;
}