package hehl.gl;

/**
complex number, arithmetics, and complex functions.
author    hehl@tfh-berlin.de
version   17-May-2005
 */
public final class Complex extends Number {

/** complex unit i; it holds i*i = -1 */
  public static final Complex i = new Complex(0.0,1.0);
/** complex unit j; it holds j*j = -1 */
  public static final Complex j = new Complex(0.0,1.0);

  private double u;
  private double v;

/**
constructs a complex number from real part and imaginary part.
@param x real part
@param y imaginary part
*/
  public Complex (double x, double y) {
    u=x;
    v=y;
  }
/**
constructs complex number zero.
*/
  public Complex() {
    this(0.0,0.0);
  }
/**
constructs a complex number with imaginary part zero.
@param r real part
*/
  public Complex(double r) {
    this(r,0.0);
  }
/**
copy constructor.
@param z complex number
*/
  public Complex(Complex z) {
    this(z.re(),z.im());
  }
/**
constructs a complex number with magnitude one and arbitrary argument.
@param phi argument
*/
  public Complex(Angle phi) {
          this(Angle.cos(phi),Angle.sin(phi));
  }
/**
constructs a complex number from radius/magnitude and argument.
@param r radius
@param phi argument
*/
  public Complex(double r, Angle phi) {
          this(r*Angle.cos(phi),r*Angle.sin(phi));
  }
/**
@return real part of the complex number
*/
  public double re () {
    return u;
  }

/**
@return imaginary part of the complex number
*/
  public double im () {
    return v;
  }
/**
@return conjugate complex number.
*/
  public Complex conj() {
    return new Complex(re(),-im());
  }
/**
@return true, if imaginary part of complex number is (numerically) zero.*/
  public boolean isReal() {
    return 1.E-12 > Math.abs(im());
  }
/**
@return magnitude of complex number.
*/
  public double mag () {
    return Math.sqrt(u*u + v*v);
  }

        public double doubleValue() {
                if(!isReal()) return 0.0;
                 // better:  throw new Msg("Complex.doubleValue","must be  pure real");
                return re();
        }
        public float floatValue() {
                if(!isReal()) return 0.0f;
                 //  throw new Msg("Complex.doubleValue","must be pure real");
                return (float)re();
        }
        public long longValue() {
                if(!isReal()) return 0;
                 //  throw new Msg("Complex.doubleValue","must be pure real");
                return (long)re();
        }
        public int intValue() {
                        if(!isReal()) return 0;
                 //  throw new Msg("Complex.doubleValue","must be pure real");
                return (int)re();
}

/**
@return argument
*/
  public Angle arg () {
    Angle result = new Angle();
    try {
                result = new Angle(Math.atan2(v, u),Angle.RAD);
        }
        catch(Exception e) { }
        return result;
  }
/**
 * Provides sum of this and right hand side.
 * @param z right hand side
 * @return sum of this and z
 */
  public Complex plus (Complex z) {
    return new Complex(u + z.re(), v + z.im());
  }
  public Complex plus(double x) {
          return new Complex(re()+x,im());
          }
/**
 * Provides difference of this and right hand side.
 * @param z right hand side
 * @return difference of this and z
 */
  public Complex minus (Complex z) {
    return new Complex(u - z.re(), v - z.im());
  }
  public Complex minus(double x) {
          return new Complex(re()-x,im());
          }
/**
 * Provides product of this and complex right hand side.
 * @param z right hand side
 * @return product of this and z
 */
  public Complex times (Complex z) {
    return new Complex(u*z.re() - v*z.im(),u*z.im() + v*z.re());
  }
/**
 * Provides product of this and float right hand side.
 * @param z right hand side
 * @return product of this and z
 */
  public Complex times(double x) {
        return new Complex(x*u,x*v);
        }
/**
 * Computes fraction between this und rhs.
 * @param z complex right hand side
 * @return fraction of this and z
 * @throws Msg if mag(z) == 0
 */
  public Complex divideBy (Complex z) throws Msg {
    double rz = z.mag();
    if(Math.abs(rz) < 1.E-12)
        throw new Msg("Complex.divideBy","denominator is zero");
    return new Complex((u * z.re() + v * z.im())/(rz * rz),
                       (v * z.re() - u * z.im())/(rz * rz));
  }
/**
 * Computes linear combination 'a times x plus b'.
 * @param a real factor
 * @param b real number
 * @return a times this plus b
 */
  public Complex axpb(double a, double b) {
          return new Complex(times(a).plus(b));
  }
/**
 * Computes linear combination 'a times x plus b'.
 * @param a complex factor
 * @param b complex number
 * @return a times this plus b
 */
  public Complex axpb(Complex a, Complex b) {
          return new Complex(times(a).plus(b));
  }
/**
 * Computes complex sine.
 * @param z complex argument
 * @return complexer sine
 */
  public static Complex sin(Complex z) {
    double r = Math.sin(z.re()) * Math.cosh(z.im());
    double i = Math.cos(z.re()) * Math.sinh(z.im());
    return new Complex(r,i);
  }

/**
 * Computes complex cosine.
 * @param z complex argument
 * @return complexer cosine
 */
  public static Complex cos(Complex z) {
    double r =  Math.cos(z.re()) * Math.cosh(z.im());
    double i = -Math.sin(z.re()) * Math.sinh(z.im());
    return new Complex(r,i);
  }
  public static Complex tan(Complex z) {
        double nenner = Math.cos(2.*z.re()) + Math.cosh(2*z.im());
    double r = Math.sin(2.*z.re()) / nenner;
    double i = Math.sinh(2.*z.im()) / nenner;
    return new Complex(r,i);
  }
/**
 * Computes complex power.
 * @param z complex argument
 * @return complex power
 */
  public static Complex pow(Complex z) {
          double ex = Math.pow(Math.E,z.re());
          return new Complex(ex*Math.cos(z.im()),ex*Math.sin(z.im()));
  }

  public static Complex sinh(Complex z) {
    return new Complex((pow(z).minus(pow(z.times(-1.)))).times(0.5));
  }

  public static Complex cosh(Complex z) {
    double r = cosh(z.re()) * Math.cos(z.im());
    double i = sinh(z.re()) * Math.sin(z.im());
    return new Complex(r,i);
  }

  public static Complex tanh(Complex z) throws Msg {
    return new Complex(sinh(z).divideBy(cosh(z)));
  }

  public static double tanh(double x) throws Msg {
   //throw new FatalMsg("Complex.tanh"," tanh() einbauen");
                return new Complex(x,0.0).re();
  }

  public static Complex atan(Complex z) throws Msg {
                Complex frac = z.times(j).plus(1.).divideBy(z.times(j).times(-1.).plus(1.));
                return new Complex(j.times(-0.5).times(Complex.ln(frac)));
  }

  public static double sinh(double x) {
    return(0.5*(Math.exp(x)-Math.exp(-x)));
  }

  public static double cosh(double x) {
    return(0.5*(Math.exp(x)+Math.exp(-x)));
  }

  public static Complex exp(Complex z) {
          double rr = Math.exp(z.re())*Math.cos(z.im());
          double ii = Math.exp(z.re())*Math.sin(z.im());
          return new Complex(rr,ii);
  }

  public static Complex ln(Complex z) {
          double rr = Math.log(z.mag())/Math.log(Math.E);
          double ii = z.arg().rad();
          return new Complex(rr,ii);
  }

        public static Complex sqrt(Complex z) {
                double r = Math.sqrt(z.mag());
                double phi = z.arg().rad()/2.;
                return new Complex(r*Math.cos(phi),r*Math.sin(phi));
        }


  public static Complex acos(Complex z) {
      Complex zz = (z.times(z).minus(new Complex(1.,0.0)));
      zz = sqrt(zz);
      zz = z.plus(zz);
      zz = j.times(ln(zz)).times(-1.);
      return zz;
}
  
  
  public static Complex atanh(Complex zz) throws Msg {
          Complex z = zz;
          z = z.plus(1.).divideBy(z.minus(1.)).times(-1.);
          z = ln(z).times(0.5);
          return z;
  }

  public static double atanh(double x) throws Msg {
          return atanh(new Complex(x,0.0)).re();
  }

/**
* @param ell a reference ellipsoid.
* @param phi the ellipsoidal latitude
* @param dlam delta lambda, the difference between an ellipsoidal longitude
*   and the longitude of the central meridian of a given GK/UTM strip.
* @return the complex latitude (argument for complex arc-length computation)
*/
 /* static Complex complexLatitude(Ellipsoid ell, Angle phi, Angle dlam)
                throws Msg {
                double q = atanh(Math.sin(phi.rad()))-
                                                        ell.e() * atanh(ell.e()*Math.sin(phi.rad()));
                Complex w = new Complex(q,dlam.rad());
                Complex b = new Complex(0.0,0.0);
                for(int i=0;i<10;i++)
                        b = asin(tanh(w.plus(atanh(sin(b).times(ell.e())).times(ell.e()))));
                return b;
        }

*//**
* The inverse problem to methode complexLatitude(): compute ellipsoidal latitude
phi (in real part) and delta_lambda (in imaginary part) from complex
latitude.
* @param the reference ellipsoid
* @param b the complex latitude
* @param a complex number containing phi/delta_lambda (in radians)
*//*
        static Complex phi_dlam(Ellipsoid ell, Complex b) throws Msg {
                Complex w = atanh(sin(b)).minus(atanh(sin(b).times(ell.e())).times(ell.e()));
                double phi=0.0;
                for(int i=0;i<10;i++)
                 phi = Math.asin(Math.tanh(w.re()+ell.e()*atanh(ell.e()*Math.sin(phi))));
                return new Complex(phi,w.im());
        }
*/
/**
@return string representation of this.
*/
  public String toString() {
    String info;
    info = new String("["+re()+(im() < 0.0 ? " - " : " + ")+
      Math.abs(im())+" * j]");
    return info;
  }
/**
test function.
*/
  /*public static void demo() {
          try {
                System.out.println("*******************************");
                System.out.println("*** Test of Class 'Complex' ***");
                System.out.println("*******************************");
                System.out.println();

                System.out.println("complex arithmetics");
                Complex a = new Complex(1.0,2.0);
                Complex b = new Complex(3.0,4.0);
                Complex e = new Complex(new Angle(45.,Angle.DEG));
                Complex r = new Complex(3.0*Math.sqrt(2.),new Angle(45.0,Angle.DEG));
                System.out.println("a= "+a+", b= "+b);
                System.out.println("e= "+e);
                System.out.println("r= "+r);
                System.out.println("a+b= "+a.plus(b));
                System.out.println("a*b= "+a.times(b));
                System.out.println("a*b+r= "+b.axpb(a,r));
                System.out.println();
                System.out.print("sample function calls");
                Complex z = new Complex(0.9,0.05);
                System.out.println("; given: z= "+z);
                System.out.println("(1) trigonometric functions");
                System.out.println(" sin(z)= "+sin(z));
                System.out.println(" cos(z)= "+cos(z));
                System.out.println(" tan(z)= "+tan(z));
                System.out.println("(2) hyp functions");
                System.out.println(" sinh(z)= "+sinh(z));
                System.out.println(" cosh(z)= "+cosh(z));
                System.out.println(" tanh(z)= "+tanh(z));
                System.out.println("(3) power and log functions");
                System.out.println(" exp(z)= "+exp(z));
                System.out.println(" ln(z)= "+ln(z));
                System.out.println(" asin(z)= "+asin(z));
                System.out.println(" atanh(z)= "+atanh(z));
                Ellipsoid ell = Ellipsoid.BESSEL;
                System.out.println("(4) complex latitude on "+ell.fullname());
                Angle phi = new Angle(54.,Angle.DEG);
                Angle dlam = new Angle(-1.5,Angle.DEG);
                System.out.println(" phi= "+phi+", dlam= "+dlam);
                Complex bb = complexLatitude(ell,phi,dlam);
                System.out.println(" b(phi, dlam)=  "+
                          new Complex(new Angle(bb.re(),Angle.RAD).deg(),
                          new Angle(bb.im(),Angle.RAD).deg())+" deg");
                bb = phi_dlam(ell,bb);
                System.out.println(" phi_dlam(b)= "+
                        new Complex(new Angle(bb.re(),Angle.RAD).deg(),
                                                                new Angle(bb.im(),Angle.RAD).deg())+" deg");
                System.out.println();
        }
        catch(Exception e) { }
  }*/
} // end class Complex