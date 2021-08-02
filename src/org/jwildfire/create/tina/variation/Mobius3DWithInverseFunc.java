package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.sqrt;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

/**
 * uses parameters to create a 3D Mobius transformation M
 *    M(z) = (az + b) / (cz + d) where a,b,c,d are quaternions
 * and also creates the inverse 3D Mobius transformation M'
 *    such that M'(M(z)) = z
 */
public class Mobius3DWithInverseFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;



  private static final String PARAM_A_RE = "a_re";
  private static final String PARAM_A_IM = "a_im";
  private static final String PARAM_A_J =  "aj";
  private static final String PARAM_A_K =  "ak";
  private static final String PARAM_B_RE = "b_re";
  private static final String PARAM_B_IM = "b_im";
  private static final String PARAM_B_J =  "bj";
  private static final String PARAM_B_K =  "bk";

  private static final String PARAM_C_RE = "c_re";
  private static final String PARAM_C_IM = "c_im";
  private static final String PARAM_C_J =  "cj";
  private static final String PARAM_C_K =  "ck";
  private static final String PARAM_D_RE = "d_re";
  private static final String PARAM_D_IM = "d_im";
  private static final String PARAM_D_J =  "dj";
  private static final String PARAM_D_K =  "dk";
  private static final String PARAM_NORMALIZE = "normalize";


  private static final String[] paramNames = {
          PARAM_A_RE, PARAM_A_IM, PARAM_A_J, PARAM_A_K,
          PARAM_B_RE, PARAM_B_IM, PARAM_B_J, PARAM_B_K,
          PARAM_C_RE, PARAM_C_IM, PARAM_C_J, PARAM_C_K,
          PARAM_D_RE, PARAM_D_IM, PARAM_D_J, PARAM_D_K, PARAM_NORMALIZE
  };

  private double ar = 2;
  private double ai = 0;
  private double aj = 0;
  private double ak = 0;
  private double br = 0;
  private double bi = -1;
  private double bj = 0;
  private double bk = 0;
  private double cr = 0;
  private double ci = -1;
  private double cj = 0;
  private double ck = 0;
  private double dr = 0;
  private double di = 0;
  private double dj = 0;
  private double dk = 0;
  private boolean normalize = true;

  Quaternion real_minus1 = new Quaternion(-1, 0, 0, 0);

  /**
   * Inner class for quaternion math
   * Q(n0, n1, n2, n3) = n0 + n1*i + n2*j + n3*k
   */
  class Quaternion {

    double n0, n1, n2, n3;

    public Quaternion(double n0, double n1, double n2, double n3) {
      this.n0 = n0;
      this.n1 = n1;
      this.n2 = n2;
      this.n3 = n3;
    }

    public Quaternion add(Quaternion b) {
      return new Quaternion(n0+b.n0, n1+b.n1, n2+b.n2, n3+b.n3);
    }


    public Quaternion subtract(Quaternion b) {
      return new Quaternion(n0-b.n0, n1-b.n1, n2-b.n2, n3-b.n3);
    }


    public Quaternion mul(Quaternion b) {
      // m0 = a0b0?a1b1?a2b2?a3b3
      double new0 = n0*b.n0 - n1*b.n1 - n2*b.n2 - n3*b.n3;
      //  m1 = a0b1+a1b0+a2b3?a3b2
      double new1 = n0*b.n1 + n1*b.n0 + n2*b.n3 - n3*b.n2;
      //  m2 = a0b2?a1b3+a2b0+a3b1
      double new2 = n0*b.n2 - n1*b.n3 + n2*b.n0 + n3*b.n1;
      //  m3 = a0b3+a1b2?a2b1+a3b0
      double new3 = n0*b.n3 + n1*b.n2 - n2*b.n1 + n3*b.n0;
      return new Quaternion(new0, new1, new2, new3);
    }

    public Quaternion reciprocal() {
      double norm = sqrt(n0*n0 + n1*n1 + n2*n2 + n3*n3);
      double normsq = norm * norm;
      // reciprocal = conjugate / (norm^2)
      return new Quaternion(n0/normsq, -n1/normsq, -n2/normsq, -n3/normsq);
    }

    /**
     *  Two kinds of quaternion division, since quaternion multiplication is not commutative
     *  div1 = reciprocal(b) * this
     */
    public Quaternion div1(Quaternion b) {
      return b.reciprocal().mul(this);
    }

    /**
     *  Two kinds of quaternion division, since quaternion multiplication is not commutative
     *  div2 = this * reciprocal(b)
     */
    public Quaternion div2(Quaternion b) {
      return this.mul(b.reciprocal());
    }

  }

  private Quaternion[] mobius;
  private Quaternion[] mobius_inverse;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    // randomly select either the Mobius transformation or its inverse
    Quaternion[] mat;
    double rand = pContext.random();
    if (rand < 0.5) {
      mat = mobius;
    }
    else {
      mat = mobius_inverse;
    }
    // then use selected matrix for Mobius transformation:
    //    f(z) = (az + b) / (cz + d);
    // for the generator matrices
    //    [0, 1, 2, 3] = [a, b, c, d]  ==> f(z)= (az+b)/(cz+d)
    //
    Quaternion zin = new Quaternion(pAffineTP.x, pAffineTP.y, pAffineTP.z, 0);
    Quaternion a = mat[0];
    Quaternion b = mat[1];
    Quaternion c = mat[2];
    Quaternion d = mat[3];
    Quaternion zout = zin.mul(a).add(b).div1(zin.mul(c).add(d));

    pVarTP.x += pAmount * zout.n0;
    pVarTP.y += pAmount * zout.n1;
    pVarTP.z += pAmount * zout.n2;
  }


  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    // normalize mobius matrix
    Quaternion[] mobius_prenorm = new Quaternion[4];
    mobius_prenorm[0] = new Quaternion(ar, ai, aj, ak);
    mobius_prenorm[1] = new Quaternion(br, bi, bj, bk);
    mobius_prenorm[2] = new Quaternion(cr, ci, cj, ck);
    mobius_prenorm[3] = new Quaternion(dr, di, dj, dk);
    if (normalize) {
      mobius = normalize(mobius_prenorm);
    }
    else {
      mobius = mobius_prenorm;
    }
    // create inverse matrix
    mobius_inverse = matrixInverse(mobius);
  }

  /* assumes 2x2 matrix represented by 4-element array: [a b c d] */
  public Quaternion[] normalize(Quaternion[] mat) {
    Quaternion[] normalized = new Quaternion[4];
    Quaternion a = mat[0];
    Quaternion b = mat[1];
    Quaternion c = mat[2];
    Quaternion d = mat[3];
    // determinant = ad - bc
    Quaternion det = a.mul(d).subtract(b.mul(c));
    Quaternion denom = det.mul(det);
    normalized[0] = a.div1(denom);
    normalized[1] = b.div1(denom);
    normalized[2] = c.div1(denom);
    normalized[3] = d.div1(denom);
    return normalized;
  }



  /* assumes 2x2 matrix represented by 4-element array: [a b c d] */
  public Quaternion[] matrixInverse(Quaternion[] mat) {
    Quaternion[] matinv = new Quaternion[4];
    matinv[0] = mat[3];
    matinv[1] = mat[1].mul(real_minus1);
    matinv[2] = mat[2].mul(real_minus1);
    matinv[3] = mat[0];
    return matinv;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { ar, ai, aj, ak, br, bi, bj, bk, cr, ci, cj, ck, dr, di, dj, dk,
            normalize ? 1 : 0
    };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A_RE.equalsIgnoreCase(pName)) {
      ar = pValue;
    }
    else if (PARAM_A_IM.equalsIgnoreCase(pName)) {
      ai = pValue;
    }
    else if (PARAM_A_J.equalsIgnoreCase(pName)) {
      aj = pValue;
    }
    else if (PARAM_A_K.equalsIgnoreCase(pName)) {
      ak = pValue;
    }
    else if (PARAM_B_RE.equalsIgnoreCase(pName)) {
      br = pValue;
    }
    else if (PARAM_B_IM.equalsIgnoreCase(pName)) {
      bi = pValue;
    }
    else if (PARAM_B_J.equalsIgnoreCase(pName)) {
      bj = pValue;
    }
    else if (PARAM_B_K.equalsIgnoreCase(pName)) {
      bk = pValue;
    }
    else if (PARAM_C_RE.equalsIgnoreCase(pName)) {
      cr = pValue;
    }
    else if (PARAM_C_IM.equalsIgnoreCase(pName)) {
      ci = pValue;
    }
    else if (PARAM_C_J.equalsIgnoreCase(pName)) {
      cj = pValue;
    }
    else if (PARAM_C_K.equalsIgnoreCase(pName)) {
      ck = pValue;
    }
    else if (PARAM_D_RE.equalsIgnoreCase(pName)) {
      dr = pValue;
    }
    else if (PARAM_D_IM.equalsIgnoreCase(pName)) {
      di= pValue;
    }
    else if (PARAM_D_J.equalsIgnoreCase(pName)) {
      dj = pValue;
    }
    else if (PARAM_D_K.equalsIgnoreCase(pName)) {
      dk = pValue;
    }
    else if (PARAM_NORMALIZE.equalsIgnoreCase(pName)) {
      normalize = (pValue == 1);
    }
    else {
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "mobius3D_with_inverse";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D};
  }

}