package org.jwildfire.base.mathlib;

import odk.lang.FastMath;

// Complex class by dark-beam
/*
  *** Basic & Constructors ***

You can empty construct a Complex to get zero. Else, construct with one numerical parameter to get a real number. 
Construct with two doubles to build a "truly complex" number.
There is a copy constructor as well.

*** Utility, misc ***
Most functions are void, so they change the Complex without returning it...
One(), ImOne(), Zero() are obvious, ImOne sets the Complex to the imaginary unit.

Copy() is even more obvious :D

Flip(), Conj(), Neg() are the classical operators, if you need a cabs you can choose between Mag2() or Radius() but Mag2() is faster (no need of Math.Sqrt() ).
The inverse of the magnitude is given by MagInv.
You can need the sign of the Complex; given by Sig() according to the sign of the real part. Sig2() never returns zero, allowing you to use it safely in divisions.

*** Erase & rewind ***
The class has a memory. You can save a state to use it again later.
Save() and Restore() allow you to do some calculus with the Complex, then to restore it when you no longer need the result.
Keep() saves the content of another Complex. Recall() returns a new Complex built using the saved data.

Another use of the memory is when you need the powers of a Complex like z^1, z^2, z^3 ... way faster than doing the actual power.
You Save() the number, then do NextPow() to obtain z^2. NextPow() again, to get z^3 etc.

Accumulate a sum; calling DoSum() will change the save state to sum the current state. Useful to get a summation of terms (but do not mix with NextPow() ...).

You can even ... Switch() between saved & current state.

*** Proper functions ***
I explain just the not fully obvious ones. ;) z is the current state of course.
Most functions are void, so they change the Complex without returning it...

SubR(zz) subtracts z from zz (zz-z) , saving the result into z without altering zz. (Hope it's clear)
DivR() evaluates zz/z (instead of z/zz !!!), saving the result into z without altering zz.
Inc(), Dec() are obvious, while to switch between polar/rectangular you need ToP() and UnP(), that return the result as a new Complex without altering the current state (keep it in mind!).
To scale a complex by a real multiplier you use Scale().
You can do the average of two numbers, using respectively AMean, GMean and HMean() altering the current state. Arg() is the complex argument, a double angle is returned. Norm() normalizes the number, dividing it by the Radius() so it projects it into a circle. Points extremely near to the origin are unnormalized to avoid overflow, btw!!!
All other funcs do change current state (void)

Sqr() Mul() Div() Add() Sub() Pow() CPow() ... they need an argument of course. Others use z as the implicit argument.
Exp() Log() SinH() CosH()
Sin() Cos() obtained using hyperbolic ones
 */
public class Complex {
  private double per_fix;
  public double re;
  public double im;
  public double save_re;
  public double save_im;

  // Constructors
  public Complex() {
    re = 0.;
    im = 0.;
    save_re = 0.;
    save_im = 0.;
    per_fix = 0.;
  }

  public Complex(double Rp) {
    re = Rp;
    im = 0.;
    save_re = 0.;
    save_im = 0.;
    per_fix = 0.;
  }

  public Complex(int Rp) {
    re = (double) Rp;
    im = 0.;
    save_re = 0.;
    save_im = 0.;
    per_fix = 0.;
  }

  public Complex(double Rp, double Ip) {
    re = Rp;
    im = Ip;
    save_re = 0.;
    save_im = 0.;
    per_fix = 0.;
  }

  public Complex(Complex zz) {
    re = zz.re;
    im = zz.im;
    save_re = 0.;
    save_im = 0.;
    per_fix = 0.;
  }

  // Basic, utils etc
  public void One() {
    re = 1.0;
    im = 0.0;
  }

  public void ImOne() {
    re = 0.0;
    im = 1.0;
  }

  public void Zero() {
    re = 0.0;
    im = 0.0;
  }

  public void Copy(Complex zz) {
    re = zz.re;
    im = zz.im;
  }

  public void Flip() { // swap re <> im
    double r2 = im;
    double i2 = re;
    re = r2;
    im = i2;
  }

  public void Conj() {
    im = -im;
  }

  public void Neg() {
    re = -re;
    im = -im;
  }

  public double Sig() {
    if (re == 0)
      return 0.;
    if (re > 0)
      return 1.;
    return -1.;
  }

  public double Sig2() { // avoids returning 0
    if (re >= 0)
      return 1.;
    return -1.;
  }

  public double Mag2() {
    return re * re + im * im;
  }

  public double Mag2eps() { // imprecise for small magnitudes
    return re * re + im * im + 1e-20;
  }

  public double MagInv() {
    double M2 = this.Mag2();
    return (M2 < 1e-100 ? 1.0 : 1.0 / M2);
  }

  // Save / Restore utils
  public void Save() { // saves the current state
    save_re = re;
    save_im = im;
  }

  public void Restore() { // revert to prev state, 
    //keeping into save re & im the current
    re = save_re;
    im = save_im;
  }

  public void Switch() { // revert to prev state, 
    // keeping into save re & im the current
    double r2 = save_re;
    double i2 = save_im;
    save_re = re;
    save_im = im;
    re = r2;
    im = i2;
  }

  public void Keep(Complex zz) { // saves zz
    save_re = zz.re;
    save_im = zz.im;
  }

  public Complex Recall() { // gives you what was saved
    return new Complex(save_re, save_im);
  }

  public void NextPow() {
    this.Mul(this.Recall());
  }

  // Arith

  public void Sqr() {
    double r2 = re * re - im * im;
    double i2 = 2 * re * im;
    re = r2;
    im = i2;
  }

  public void Recip() {
    double mi = this.MagInv();
    re = re * mi;
    im = -im * mi;
  }

  public void Scale(double mul) {
    re = re * mul;
    im = im * mul;
  }

  public void Mul(Complex zz) {
    if (zz.im == 0.0) {
      this.Scale(zz.re);
      return;
    }
    double r2 = re * zz.re - im * zz.im;
    double i2 = re * zz.im + im * zz.re;
    re = r2;
    im = i2;
  }

  public void Div(Complex zz) {
    // divides by zz
    // except if zz is 0
    double r2 = im * zz.im + re * zz.re;
    double i2 = im * zz.re - re * zz.im;
    double M2 = zz.MagInv();
    re = r2 * M2;
    im = i2 * M2;
  }

  public void DivR(Complex zz) { // reverse
    double r2 = zz.im * im + zz.re * re;
    double i2 = zz.im * re - zz.re * im;
    double M2 = this.MagInv();
    re = r2 * M2;
    im = i2 * M2;
  }

  public void Add(Complex zz) {
    re += zz.re;
    im += zz.im;
  }

  public void AMean(Complex zz) {
    this.Add(zz);
    this.Scale(0.5);
  }

  public void RootMeanS(Complex zz) {
    Complex PF = new Complex(zz);
    PF.Sqr();
    this.Sqr();
    this.Add(PF);
    this.Scale(0.5);
    this.Pow(0.5);
  }

  public void GMean(Complex zz) {
    this.Mul(zz);
    this.Pow(0.5);
  }

  public void Heronian(Complex zz) {
    // Heronian mean
    Complex HM = new Complex(this);
    HM.GMean(zz);
    this.Add(zz);
    this.Add(HM);
    this.Scale(0.333333333333333333333333);

  }

  public void HMean(Complex zz) {
    // not infinite if one term is zero. expansion by Maxima
    double p2 = (zz.re + re);
    double q2 = (zz.im + im);
    double D = 0.5 * (p2 * p2 + q2 * q2);
    if (D == 0) {
      this.Zero();
      return;
    }
    D = 1.0 / D;
    p2 = this.Mag2();
    q2 = zz.Mag2();
    if (p2 * q2 == 0) {
      this.Zero();
      return;
    }
    re = D * (re * q2 + zz.re * p2);
    im = D * (im * q2 + zz.im * p2);
  }

  public void Sub(Complex zz) {
    re -= zz.re;
    im -= zz.im;
  }

  public void SubR(Complex zz) {
    re = zz.re - re;
    im = zz.im - im;
  }

  public void Inc() {
    re += 1.0;
  }

  public void Dec() {
    re -= 1.0;
  }

  public void PerFix(double v) {
    // fix atan2() period, set v to a random integer
    per_fix = Math.PI * v;
  }

  public void Pow(double exp) {
    // some obvious cases come at first
    // instant evaluation for (-2, -1, -0.5, 0, 0.5, 1, 2)
    if (exp == 0.0) {
      this.One();
      return;
    }
    double ex = MathLib.fabs(exp);
    if (exp < 0) {
      this.Recip();
    }
    if (ex == 0.5) {
      this.Sqrt();
      return;
    }
    if (ex == 1.0) {
      return;
    }
    if (ex == 2.0) {
      this.Sqr();
      return;
    }
    // In general we need sin, cos etc
    Complex PF = this.ToP();
    PF.re = Math.pow(PF.re, ex);
    PF.im = PF.im * ex;
    this.Copy(PF.UnP());
  }

  // Trascendent functions (slower than others)

  public double Radius() {
    return FastMath.hypot(re, im);
  }

  public double Arg() {
    return (per_fix + Math.atan2(im, re));
  }

  public Complex ToP() {
    return new Complex(this.Radius(), this.Arg());
  }

  public Complex UnP() {
    return new Complex(re * Math.cos(im), re * Math.sin(im));
  }

  public void Norm() {
    this.Scale(Math.sqrt(this.MagInv()));
  }

  public void Exp() {
    re = Math.exp(re);
    this.Copy(this.UnP());
  }

  public void SinH() {
    double rr = 0.0;
    double ri = 0.0;
    double er = 1.0;
    re = Math.exp(re);
    er /= re;
    rr = 0.5 * (re - er);
    ri = rr + er;
    re = Math.cos(im) * rr;
    im = Math.sin(im) * ri;
  }

  public void Sin() {
    this.Flip();
    this.SinH();
    this.Flip(); // Should be this simple!
  }

  public void CosH() {
    double rr = 0.0;
    double ri = 0.0;
    double er = 1.0;
    re = Math.exp(re);
    er /= re;
    rr = 0.5 * (re - er);
    ri = rr + er;
    re = Math.cos(im) * ri;
    im = Math.sin(im) * rr;
  }

  public void Cos() {
    this.Flip();
    this.CosH();
    this.Flip(); // Should be this simple!
  }

  public void Sqrt() {
    // uses the exact formula and the expansion...
    // sqrt(a+i b) = sqrt(Radius + re) + i * sqrt(Radius - re)
    double Rad = this.Radius();
    double sb = (im < 0) ? -1 : 1;
    im = sb * Math.sqrt(0.5 * (Rad - re));
    re = Math.sqrt(0.5 * (Rad + re));
    // Re always gt 0. Primary value...
    if (per_fix < 0)
      this.Neg();
  }

  public void Log() {
    // I use Mag2eps to tame singularity
    Complex L_eps = new Complex(0.5 * Math.log(this.Mag2eps()), this.Arg());
    this.Copy(L_eps);
  }

  public void LMean(Complex zz) {
    // Logarithmic mean is given by (b-a)/log(b/a)
    Complex dab = new Complex(this);
    Complex lab = new Complex(this);
    dab.Sub(zz);
    lab.Div(zz);
    lab.Log();
    dab.Div(lab);
    this.Copy(dab);
  }

  public void AtanH() {
    Complex D = new Complex(this);
    D.Dec();
    D.Neg();
    this.Inc();
    this.Div(D);
    this.Log();
    this.Scale(0.5);
  }

  public void AsinH() { // slower than AtanH!
    Complex D = new Complex(this);
    D.Sqr();
    D.Inc();
    D.Pow(0.5);
    this.Add(D);
    this.Log();
  }

  public void AcosH() { // slower than Atanh!
    Complex D = new Complex(this);
    D.Sqr();
    D.Dec();
    D.Pow(0.5);
    this.Add(D);
    this.Log();
  }

  public void AcotH() {
    this.Recip();
    this.AtanH();
  }

  public void AsecH() {
    this.Recip();
    this.AsinH();
  }

  public void AcosecH() {
    this.Recip();
    this.AcosH();
  }

  public void Atan() { // the Flip() cheat works in my tests
    this.Flip();
    this.AtanH();
    this.Flip();
  }

  public void Asin() {
    // the Flip() cheat works in my tests
    this.Flip();
    this.AsinH();
    this.Flip();
  }

  public void Acos() {
    // not so suitable for fractals I think 
    // There is another one for this ... shift the Asin()
    this.Flip();
    this.AsinH();
    this.Flip(); // this is Asin()
    re = (Math.PI/2) - re;
    im = -im; // Acos = pi/2 - Asin
  }

  public void CPow(Complex ex) {
    if (ex.im == 0.0) {
      this.Pow(ex.re);
      return;
    }
    this.Log();
    this.Mul(ex);
    this.Exp();
  }

}