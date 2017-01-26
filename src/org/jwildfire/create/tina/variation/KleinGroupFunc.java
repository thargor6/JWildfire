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
  private static final String PARAM_RECIPE = "recipe";
  private static final String PARAM_AVOID_REVERSAL = "avoid_reversal";
  
  static Complex zero = new Complex(0, 0);
  static Complex re1 = new Complex(1, 0);
  static Complex re2 = new Complex(2, 0);
  static Complex re4 = new Complex(4, 0);
  static Complex im1 = new Complex(0, 1);
  static Complex im2 = new Complex(0, 2);
  static Complex im4 = new Complex(0, 4);
  
  private static int GRANDMA_STANDARD = 0;
  private static int MASKIT_MU = 1;
  private static int JORGENSEN = 2;
  

  private static final String[] paramNames = { PARAM_A_RE, PARAM_A_IM, PARAM_B_RE, PARAM_B_IM, PARAM_RECIPE, PARAM_AVOID_REVERSAL };
  
  private double a_re = 2;
  private double a_im = 0;
  private double b_re = 2;
  private double b_im = 0;
  private int recipe = GRANDMA_STANDARD;
  private boolean avoid_reversal = false;

  // private Complex traceAB = new Complex();
  // for the generator matrices, 
  //    [0, 1, 2, 3] = [a, b, c, d]  ==> f(z)= (az+b)/(cz+d)
  //     
  protected Complex[] mat_a = new Complex[4];
  protected Complex[] mat_A = new Complex[4];  // mat_A = inverse(mat_a)
  protected Complex[] mat_b = new Complex[4];
  protected Complex[] mat_B = new Complex[4];  // mat_B = inverse(mat_b);
  
  protected Complex[] prev_matrix;

  // mtransforms is four element array, all of a, A, b, B
  protected Complex[][] all_matrices;
  // not_x is a three element array, all of a, A, b, B transforms except x
  //    so not_A = [mat_a, mat_b, mat_b_inv]
  protected Complex[][] not_a;
  protected Complex[][] not_A;
  protected Complex[][] not_b;
  protected Complex[][] not_B;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // randomly pick one of the four calculated Mobius transforms matrices, a, b, A, B ahere A = inverse(a), B = inverse(b)
    // TODO (next version?): 
    //       give the variation some internal "memory"
    //       for a first pass, remember last used matrix (out of a, b, A, B) 
    //       and use that to choose different mtransforms sets of matrices to randomly sample from 
    //       for example to avoid getting successive pairs of matrix m and inverse M, which 
    //       would cancel each other out: m(z)M(z) = z
    Complex[][] mtransforms;

    if (avoid_reversal) {
      if      (prev_matrix == mat_a) { mtransforms = not_a; }
      else if (prev_matrix == mat_A) { mtransforms = not_A; }
      else if (prev_matrix == mat_b) { mtransforms = not_b; }
      else if (prev_matrix == mat_B) { mtransforms = not_B; }
      // shouldn't get here...
      else  { mtransforms = all_matrices; }
    }
    else {
      mtransforms = all_matrices;
    }
    
    // get an integer from 0 to mtransforms.length-1
    int mindex = pContext.random(mtransforms.length);
    Complex[] mat = mtransforms[mindex];
    // then use selected matrix for Mobius transformation:
    //  f(z) = (az + b) / (cz + d);

    Complex a = mat[0];
    Complex b = mat[1];
    Complex c = mat[2];
    Complex d = mat[3];
    
    Complex zin = new Complex(pAffineTP.x, pAffineTP.y);
    // zin.mult(a).add(b)).div(zin.mult(c).add(d)))
    Complex zout = zin.mul(a).add(b).div(zin.mul(c).add(d));
    
    pVarTP.x += pAmount * zout.re();
    pVarTP.y += pAmount * zout.im();
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
    prev_matrix = mat;
  }
  
  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    Complex generators[][];
    if (recipe == GRANDMA_STANDARD) {
      generators = calcGrandmaGenerators();
    }
    else if (recipe == MASKIT_MU) {
      generators = calcMaskitGenerators();
    }
    else if (recipe == JORGENSEN) {
      generators = calcJorgensenGenerators(); 
    }
    else {
      // default to GRANDMA_STANDARD
      generators = calcGrandmaGenerators();
    }
    
    mat_a = generators[0];
    mat_b = generators[1];
    Complex[] mat_A = matrixInverse(mat_a);
    Complex[] mat_B = matrixInverse(mat_b);
    all_matrices = new Complex[][] {mat_a, mat_A, mat_b, mat_B };    
    not_a = new Complex[][] { mat_A, mat_b, mat_B };
    not_A = new Complex[][] { mat_a, mat_b, mat_B };
    not_b = new Complex[][] { mat_a, mat_A, mat_B };
    not_B = new Complex[][] { mat_a, mat_A, mat_b };
    // arbitrarily initialize prev_matrix to mat_a
    prev_matrix = mat_a;
  }

  public Complex[][] calcJorgensenGenerators() {
    Complex traceA = new Complex(a_re, a_im);
    Complex traceB = new Complex(b_re, b_im);
    // solve for traceAB: 
    //     traceAB^2 - (traceA * traceB * traceAB) + traceA^2 + traceB^2 = 0
    // x = (-b +- sqrt(b^2 - 4ac)) / 2a
    // x = traceAB
    // a = 1
    // b = -1 * traceA * traceB
    // c = traceA^2 + traceB^2
    Complex b = traceA.mul(traceB).mul(-1);
    Complex c = traceA.power(2).add(traceB.power(2));
    Complex bsq = b.power(2);
    Complex ac4 = c.mul(4);
    Complex trABplus  = b.mul(-1).add( bsq.sub(ac4).sqrt()).div(re2);
    Complex trABminus = b.mul(-1).sub( bsq.sub(ac4).sqrt()).div(re2);
    Complex traceAB = trABminus;

    System.out.println("trABplus:  " + trABplus);
    System.out.println("trABminus: " + trABminus);
    System.out.println("traceAB: " + traceAB);
    
    // a0 = ta - (tb/tab)
    Complex a0 = traceA.sub(traceB.div(traceAB));
    // a1 = ta / tab^2
    Complex a1 = traceA.div(traceAB.power(2));
    Complex a2 = traceA;
    Complex a3 = traceB.div(traceAB);
    
    // b0 = tb - (ta/tab)
    Complex b0 = traceB.sub(traceA.div(traceAB));
    // b1 = -tb/tab^2
    Complex b1 = traceB.mul(-1).div(traceAB.power(2));
    Complex b2 = traceB.mul(-1);
    Complex b3 = traceA.div(traceAB);
    
    mat_a = new Complex[]{a0, a1, a2, a3};
    mat_b = new Complex[]{b0, b1, b2, b3};
    
    Complex[][] generators = new Complex[2][4];
    generators[0] = mat_a;
    generators[1] = mat_b;
    return generators;
  }
  
  /*
  *   for Maskit recipe using complex parameter a for mu rather than for Trace(generator_a)
  *      Trace(generator_a) = ta = -i*mu 
  */
  public Complex[][] calcMaskitGenerators() {
    Complex mu = new Complex(a_re, a_im);
    Complex[] mat_a = new Complex[]{ mu.mul(-1).mul(im1), im1.mul(-1), im1.mul(-1), zero};
    Complex[] mat_b = new Complex[]{ re1, re2, zero, re1};
    Complex[][] generators = new Complex[2][4];
    generators[0] = mat_a;
    generators[1] = mat_b;
    return generators;
  }
  
  public Complex[][] calcGrandmaGenerators() {
    
    Complex traceA = new Complex(a_re, a_im);
    Complex traceB = new Complex(b_re, b_im);
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
    Complex traceAB = trABminus;

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
  
    Complex b0 = traceB.sub(im2).div(re2);
    Complex b1 = traceB.div(re2);
    Complex b2 = traceB.div(re2);
    Complex b3 = traceB.add(im2).div(re2);
 
    mat_a = new Complex[]{a0, a1, a2, a3};
    mat_b = new Complex[]{b0, b1, b2, b3};
    Complex[][] generators = new Complex[2][4];
    generators[0] = mat_a;
    generators[1] = mat_b;
    return generators;
  }
  
  public Complex[] matrixInverse(Complex[] mat) {
      Complex[] matinv = new Complex[4];
      matinv[0] = mat[3];
      matinv[1] = mat[1].mul(-1);
      matinv[2] = mat[2].mul(-1);
      matinv[3] = mat[0];
      return matinv;
  }
  
  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { a_re, a_im, b_re, b_im, recipe, avoid_reversal ? 1 : 0 };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A_RE.equalsIgnoreCase(pName)) {
      a_re = pValue;
    }
    else if (PARAM_A_IM.equalsIgnoreCase(pName)) {
      a_im = pValue;
    }
    else if (PARAM_B_RE.equalsIgnoreCase(pName)) {
      b_re = pValue;
    }
    else if (PARAM_B_IM.equalsIgnoreCase(pName)) {
      b_im = pValue;
    }
    else if (PARAM_RECIPE.equalsIgnoreCase(pName)) {
      recipe = (int)Math.floor(pValue);
    }
    else if (PARAM_AVOID_REVERSAL.equalsIgnoreCase(pName)) {
      avoid_reversal = (pValue == 1);
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
