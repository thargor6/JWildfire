package org.jwildfire.create.tina.variation;

import static java.lang.Math.random;
import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.M_2PI;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.Layer;

// import org.jwildfire.base.mathlib.Complex;
import org.nfunk.jep.type.Complex;

/* using org.nfunk.jep.type.Complex for complex numbers 
//     instead of org.jwildfire.base.mathlib.Complex because
//     its methods return new complex numbers (instead of modifying in place like ...mathlib.Complex)
//     which makes chaining methods together for long calcs possible 
// there is a performance hit relative to ...mathlib.Complex, since creating lots of 
//     intermediary complex objects. But compared performance within transform() loop to 
//     using ...mathlib.Complex instead, and only get ~20% slower. If really becomes a problem 
//     could later convert to using ...mathlib.Complex
*/    

/**
 * 
 * KleinGroupFunc is meant to make it easier to create "interesting" Kleinian group limit sets 
 *     internally uses two Mobius transform as generators (plus their inverses)
 *     Takes the input parameters and uses strategies from "Indra's Pearls" book 
 *     (https://en.wikipedia.org/wiki/Indra%27s_Pearls_(book)) 
 *     to create transform groups that have a reasonable chance of being "interesting" 
 * 
 *  Author: Gregg Helt
 */
public class KleinGroupFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A_RE = "a_re";
  private static final String PARAM_A_IM = "a_im";
  private static final String PARAM_B_RE = "b_re";
  private static final String PARAM_B_IM = "b_im";

  private static final String[] paramNames = { PARAM_A_RE, PARAM_A_IM, PARAM_B_RE, PARAM_B_IM };

  // set defaults to traces for standard Apollonian gasket
  private Complex traceA = new Complex(2, 0);
  private Complex traceB = new Complex(2, 0);
  // private Complex traceAB = new Complex();
  // for the generator matrices, 
  //    [0, 1, 2, 3] = [a, b, c, d]  ==> f(z)= (az+b)/(cz+d)
  //     
  protected Complex[] mat_a = new Complex[4];
  protected Complex[] mat_a_inv = new Complex[4];
  protected Complex[] mat_b = new Complex[4];
  protected Complex[] mat_b_inv = new Complex[4];
  protected Complex zin = new Complex();
  protected Complex num = new Complex();
  protected Complex denom = new Complex();
  // protected Complex zout = new Complex();

  protected Object[] mtransforms;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // randomly pick one of the four calculated Mobius transforms matrices, a, b, A, B ahere A = inverse(a), B = inverse(b)
    int mindex = pContext.random(4);
    Complex[] mat = (Complex[]) mtransforms[mindex];
    // then use selected matrix for Mobius transformation:
    //  f(z) = (az + b) / (cz + d);

    Complex a = mat[0];
    Complex b = mat[1];
    Complex c = mat[2];
    Complex d = mat[3];
    
    zin = new Complex(pAffineTP.x, pAffineTP.y);
   // zin.mult(a).add(b)).div(zin.mult(c).add(d)))
    Complex zout = zin.mul(a).add(b).div(zin.mul(c).add(d));
    
    pVarTP.x += pAmount * zout.re();
    pVarTP.y += pAmount * zout.im();
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }
  
  // test by setting transforms for Apollonian gasket using params 
  //   from book Indra's Pearls:
  //  matrix a = ( 1, 0, -2i, 1)
  //  matrix b = ( 1-i, 1, 1, 1+i)
  public void testWithApollonian() {
    
    mat_a[0] = new Complex( 1,  0);
    mat_a[1] = new Complex( 0,  0);
    mat_a[2] = new Complex( 0, -2);
    mat_a[3] = new Complex( 1,  0);
    
    mat_a_inv[0] = new Complex( 1,  0);
    mat_a_inv[1] = new Complex( 0,  0);
    mat_a_inv[2] = new Complex( 0,  2);
    mat_a_inv[3] = new Complex( 1,  0);
    
    mat_b[0] = new Complex( 1, -1);
    mat_b[1] = new Complex( 1,  0);
    mat_b[2] = new Complex( 1,  0);
    mat_b[3] = new Complex( 1,  1);
    
    mat_b_inv[0] = new Complex( 1,  1);
    mat_b_inv[1] = new Complex(-1,  0);
    mat_b_inv[2] = new Complex(-1,  0);
    mat_b_inv[3] = new Complex( 1, -1);

    mtransforms = new Object[] {mat_a, mat_a_inv, mat_b, mat_b_inv };    

  }
  
  public boolean TEST_APOLLONIAN = false;
  
  static Complex re1 = new Complex(1, 0);
  static Complex re2 = new Complex(2, 0);
  static Complex re4 = new Complex(4, 0);
  static Complex im1 = new Complex(0, 1);
  static Complex im2 = new Complex(0, 2);
  static Complex im4 = new Complex(0, 4);
  
  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    // using "Grandma's special parabolic commutator groups" recipe
    // solve for traceAB: 
    //     traceAB^2 - (traceA * traceB * traceAB) + traceA^2 + traceB^2 = 0
    // x = (-b +- sqrt(b^2 - 4ac)) / 2a
    // x = traceAB
    // a = 1
    // b = -1 * traceA * traceB
    // c = traceA^2 + traceB^2
    Complex b = traceA.mul(traceB).mul(-1);
    Complex c = traceA.power(2).add(traceB.power(2));
    // Complex bsq = traceA.mul(traceB).power(2);
    Complex bsq = b.power(2);
    Complex ac4 = c.mul(4);
    Complex trABplus  = b.mul(-1).add( bsq.sub(ac4).sqrt()).div(re2);
    Complex trABminus = b.mul(-1).sub( bsq.sub(ac4).sqrt()).div(re2);
    // Complex traceAB = trABminus;
    Complex traceAB = trABplus;
    // System.out.println("bsq: " + bsq);
    // System.out.println("ac4: " + ac4);
    System.out.println("trABplus:  " + trABplus);
    System.out.println("trABminus: " + trABminus);
    
    System.out.println("traceAB: " + traceAB);
    
    // z0 = ((traceAB - 2) * traceB) / ((traceB * traceAB) - (2 * traceA) + (2i * traceAB))
    // z0 = traceAB.sub(2).mult(traceB).div(traceB.mult(traceAB).sub(traceA.mult(2)).add(traceAB.mult(Complex.I).mult(2)) ) 
    Complex znum = traceAB.sub(re2).mul(traceB);
    Complex zdenom = traceB.mul(traceAB).sub(traceA.mul(re2)).add(traceAB.mul(im2));
    Complex z0 = znum.div(zdenom);
    System.out.println("z0: " + z0);
    
    Complex a0 = traceA.div(re2);
    Complex a1num = traceA.mul(traceAB).sub(traceB.mul(re2)).add(im4);
    Complex a1denom = traceAB.mul(re2).add(re4).mul(z0);
    Complex a1 = a1num.div(a1denom);
    Complex a2num = traceA.mul(traceAB).sub(traceB.mul(re2)).sub(im4).mul(z0);
    Complex a2denom = traceAB.mul(re2).sub(re4);
    Complex a2 = a2num.div(a2denom);
    Complex a3 = traceA.div(re2);
    System.out.println("a0: " + a0);
    System.out.println("a1: " + a1);
    System.out.println("a2: " + a2);
    System.out.println("a3: " + a3);
    
    Complex b0 = traceB.sub(im2).div(re2);
    Complex b1 = traceB.div(re2);
    Complex b2 = traceB.div(re2);
    Complex b3 = traceB.add(im2).div(re2);
    
   /*
    System.out.println("b0: " + b0);
    System.out.println("b1: " + b1);
    System.out.println("b2: " + b2);
    System.out.println("b3: " + b3);
    */

    if (TEST_APOLLONIAN) {
      testWithApollonian();
    }
    else {
      mat_a[0] = a0;
      mat_a[1] = a1;
      mat_a[2] = a2;
      mat_a[3] = a3;
      mat_b[0] = b0;
      mat_b[1] = b1;
      mat_b[2] = b2;      
      mat_b[3] = b3;
      mat_b_inv[0] = b3;
      mat_b_inv[1] = b1.mul(-1);
      mat_b_inv[2] = b2.mul(-1);
      mat_b_inv[3] = b0;
      
      mat_a_inv[0] = a3;
      mat_a_inv[1] = a1.mul(-1);
      mat_a_inv[2] = a2.mul(-1);
      mat_a_inv[3] = a0;
      
      mtransforms = new Object[] {mat_a, mat_a_inv, mat_b, mat_b_inv };    
    }

  }
  
  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { traceA.re(), traceA.im(), traceB.re(), traceB.im() };
    
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A_RE.equalsIgnoreCase(pName)) {
      traceA.setRe(pValue);
    }
    else if (PARAM_A_IM.equalsIgnoreCase(pName)) {
      traceA.setIm(pValue);
    }
    else if (PARAM_B_RE.equalsIgnoreCase(pName)) {
      traceB.setRe(pValue);
    }
    else if (PARAM_B_IM.equalsIgnoreCase(pName)) {
      traceB.setIm(pValue);
    }
    else {
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "klein_group";
  }

}
