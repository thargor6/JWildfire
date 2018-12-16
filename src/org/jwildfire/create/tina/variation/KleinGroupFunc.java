/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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

/**
 * KleinGroup variation by CozyG
 * Copyright 2017- Gregg Helt
 * (released under same GNU Lesser General Public License as above)
 * KleinGroup variation is meant to make it easier to create "interesting" Kleinian group limit sets from Mobius transformations
 * internally uses two Mobius transformations as generators (plus their inverses)
 * Takes the input parameters and uses strategies from "Indra's Pearls" book
 * (https://en.wikipedia.org/wiki/Indra%27s_Pearls_(book)),
 * to create transform groups that have a reasonable chance of being "interesting"
 */

package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.nfunk.jep.type.Complex;

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;

/**
 * Using org.nfunk.jep.type.Complex for complex numbers
 * //     instead of org.jwildfire.base.mathlib.Complex because
 * //     JEP's methods return new complex numbers (instead of modifying in place like ...mathlib.Complex)
 * //     which makes chaining methods together for long calcs possible
 * //  Note that there is a performance hit for JEP relative to ...mathlib.Complex, since creating lots of
 * //     intermediary complex objects. But in a performance comparison within transform() loop to
 * //     using ...mathlib.Complex instead, JEP was only ~20% slower. If really becomes a problem
 * //     could later convert to using ...mathlib.Complex
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
  private static int RILEY = 3;
  private static int RILEY_MODIFIED = 4;
  private static int MASKIT_MU_MODIFIED = 5;
  private static int MASKIT_LEYS_MODIFIED = 6;

  private static final String[] paramNames = {PARAM_A_RE, PARAM_A_IM, PARAM_B_RE, PARAM_B_IM, PARAM_RECIPE, PARAM_AVOID_REVERSAL};

  private double a_re = 2;
  private double a_im = 0;
  private double b_re = 2;
  private double b_im = 0;
  private int recipe = GRANDMA_STANDARD;
  private boolean avoid_reversal = false;

  protected Complex[] mat_a = new Complex[4];
  protected Complex[] mat_inv_a = new Complex[4];  // mat_inv_a = inverse(mat_a)
  protected Complex[] mat_b = new Complex[4];
  protected Complex[] mat_inv_b = new Complex[4];  // mat_inv_b = inverse(mat_b);

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

    // if avoid_reversals is false, 
    // randomly pick one of the four calculated Mobius transformation matrices, a, b, A, B where A = inverse(a), B = inverse(b)
    // 
    //  if avoid_reversals is true trying to give the variation some internal "memory"
    //       for a first pass, remember last used matrix (out of a, b, A, B) 
    //       and use that to choose different sets of matrices to randomly sample from 
    //       for example to avoid getting successive pairs of matrix m and inverse M, which 
    //       would cancel each other out: m(z)M(z) = z
    //       so selecting randomly each time from the set of transforms that won't cause a reversal
    Complex[][] mtransforms;

    // first attempt to give variation some internal "memory" to avoid 
    //    successive transforms that use a matrix and its inverse 
    //    (aA, bB, Aa, Bb)
    if (avoid_reversal) {
      if (prev_matrix == mat_a) {
        mtransforms = not_A;
      } else if (prev_matrix == mat_inv_a) {
        mtransforms = not_a;
      } else if (prev_matrix == mat_b) {
        mtransforms = not_B;
      } else if (prev_matrix == mat_inv_b) {
        mtransforms = not_b;
      }
      // shouldn't get here...
      else {
        mtransforms = all_matrices;
      }
    } else {
      mtransforms = all_matrices;
    }

    // randomly select a matrix from the list of matrices
    int mindex = pContext.random(mtransforms.length);
    Complex[] mat = mtransforms[mindex];
    // then use selected matrix for Mobius transformation:
    //    f(z) = (az + b) / (cz + d);
    // for the generator matrices 
    //    [0, 1, 2, 3] = [a, b, c, d]  ==> f(z)= (az+b)/(cz+d)
    //     
    double xin = pAffineTP.x;
    double yin = pAffineTP.y;
    xin /= pAmount;
    yin /= pAmount;
    Complex win = new Complex(xin, yin);
    Complex a = mat[0];
    Complex b = mat[1];
    Complex c = mat[2];
    Complex d = mat[3];
    Complex wout = win.mul(a).add(b).div(win.mul(c).add(d));

    pVarTP.x += pAmount * wout.re();
    pVarTP.y += pAmount * wout.im();
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
    } else if (recipe == MASKIT_MU) {
      generators = calcMaskitGenerators();
    } else if (recipe == MASKIT_MU_MODIFIED) {
      generators = calcModifiedMaskitGenerators();
    } else if (recipe == JORGENSEN) {
      generators = calcJorgensenGenerators();
    } else if (recipe == RILEY) {
      generators = calcRileyGenerators();
    } else if (recipe == RILEY_MODIFIED) {
      generators = calcModifiedRileyGenerators();
    } else if (recipe == MASKIT_LEYS_MODIFIED) {
      generators = calcMaskitLeysModifiedGenerators();
    } else {
      // default to GRANDMA_STANDARD
      generators = calcGrandmaGenerators();
    }

    mat_a = generators[0];
    mat_b = generators[1];
    mat_inv_a = matrixInverse(mat_a);
    mat_inv_b = matrixInverse(mat_b);
    all_matrices = new Complex[][]{mat_a, mat_inv_a, mat_b, mat_inv_b};
    not_a = new Complex[][]{mat_inv_a, mat_b, mat_inv_b};
    not_A = new Complex[][]{mat_a, mat_b, mat_inv_b};
    not_b = new Complex[][]{mat_a, mat_inv_a, mat_inv_b};
    not_B = new Complex[][]{mat_a, mat_inv_a, mat_b};
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
    Complex trABplus = b.mul(-1).add(bsq.sub(ac4).sqrt()).div(re2);
    Complex trABminus = b.mul(-1).sub(bsq.sub(ac4).sqrt()).div(re2);
    Complex traceAB = trABminus;

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
   *    Riley recipe only uses one complex parameter, c
   *    So using a_re and a_im to construct c, and ignoring b_re and b_im parameters
   */
  public Complex[][] calcRileyGenerators() {
    Complex c = new Complex(a_re, a_im);
    Complex[] mat_a = new Complex[]{re1, zero, c, re1};
    Complex[] mat_b = new Complex[]{re1, re2, zero, re1};
    Complex[][] generators = new Complex[2][4];
    generators[0] = mat_a;
    generators[1] = mat_b;
    return generators;
  }

  public Complex[][] calcModifiedRileyGenerators() {
    Complex c = new Complex(a_re, a_im);
    // modifying Riley to utilize b_re and b_im
    Complex b1 = new Complex(b_re, b_im);
    Complex[] mat_a = new Complex[]{re1, zero, c, re1};
    // Complex[] mat_b = new Complex[]{ re1, re2, zero, re1 };
    Complex[] mat_b = new Complex[]{re1, b1, zero, re1};
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
    Complex[] mat_a = new Complex[]{mu.mul(-1).mul(im1), im1.mul(-1), im1.mul(-1), zero};
    Complex[] mat_b = new Complex[]{re1, re2, zero, re1};
    Complex[][] generators = new Complex[2][4];
    generators[0] = mat_a;
    generators[1] = mat_b;
    return generators;
  }

  public Complex[][] calcModifiedMaskitGenerators() {
    Complex mu = new Complex(a_re, a_im);
    // modifying Maskit to utilize b_re and b_im
    Complex b1 = new Complex(b_re, b_im);
    Complex[] mat_a = new Complex[]{mu.mul(-1).mul(im1), im1.mul(-1), im1.mul(-1), zero};
    // Complex[] mat_b = new Complex[]{ re1, re2, zero, re1};
    Complex[] mat_b = new Complex[]{re1, b1, zero, re1};
    Complex[][] generators = new Complex[2][4];
    generators[0] = mat_a;
    generators[1] = mat_b;
    return generators;
  }

  /**
   * based on modified Maskit parameterization discussed in Jos Leys' article
   * "A fast algorithm for limit sets of Kleinian groups with the Maskit parametrisation",
   * section 4.1
   * replaces b(z) = z + 2
   * with b(z) = z + k
   * where k = 2*cos(PI/n) and n is integer
   * here we also allow imaginary component for k, to be set by b_im
   */
  public Complex[][] calcMaskitLeysModifiedGenerators() {
    Complex mu = new Complex(a_re, a_im);
    Complex[] mat_a = new Complex[]{mu.mul(-1).mul(im1), im1.mul(-1), im1.mul(-1), zero};
    Complex b1 = new Complex(2 * cos(M_PI / b_re), b_im);
    Complex[] mat_b = new Complex[]{re1, b1, zero, re1};
    Complex[][] generators = new Complex[2][4];
    generators[0] = mat_a;
    generators[1] = mat_b;
    return generators;
  }

  /**
   * using "Grandma's special parabolic commutator groups" recipe
   * from the book "Indra's Pearls: the Vision of Felix Klein"
   */
  public Complex[][] calcGrandmaGenerators() {
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
    // Complex bsq = traceA.mul(traceB).power(2);
    Complex bsq = b.power(2);
    Complex ac4 = c.mul(4);
    Complex trABplus = b.mul(-1).add(bsq.sub(ac4).sqrt()).div(re2);
    Complex trABminus = b.mul(-1).sub(bsq.sub(ac4).sqrt()).div(re2);
    Complex traceAB = trABminus;

    // z0 = ((traceAB - 2) * traceB) / ((traceB * traceAB) - (2 * traceA) + (2i * traceAB))
    // z0 = traceAB.sub(2).mult(traceB).div(traceB.mult(traceAB).sub(traceA.mult(2)).add(traceAB.mult(Complex.I).mult(2)) ) 
    Complex znum = traceAB.sub(re2).mul(traceB);
    Complex zdenom = traceB.mul(traceAB).sub(traceA.mul(re2)).add(traceAB.mul(im2));
    Complex z0 = znum.div(zdenom);

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
    return new Object[]{a_re, a_im, b_re, b_im, recipe, avoid_reversal ? 1 : 0};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A_RE.equalsIgnoreCase(pName)) {
      a_re = pValue;
    } else if (PARAM_A_IM.equalsIgnoreCase(pName)) {
      a_im = pValue;
    } else if (PARAM_B_RE.equalsIgnoreCase(pName)) {
      b_re = pValue;
    } else if (PARAM_B_IM.equalsIgnoreCase(pName)) {
      b_im = pValue;
    } else if (PARAM_RECIPE.equalsIgnoreCase(pName)) {
      recipe = (int) Math.floor(pValue);
    } else if (PARAM_AVOID_REVERSAL.equalsIgnoreCase(pName)) {
      avoid_reversal = (pValue == 1);
    } else {
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "klein_group";
  }

}
