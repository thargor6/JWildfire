/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.sqr;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

// converted by Jesus Sosa from
// Jacobi-elliptic-integral by dark-beam,
//  https://www.deviantart.com/dark-beam/art/Jacobi-incomp-ell-int-of-the-1st-kind-plugin-462461247

public class JacElkFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_K = "k";

  private static final String[] paramNames = { PARAM_K };

  private double k = 0.5;

  double EPS = 1.0e-20;

  double intpow(double X, int p) {
    double ipret = 1.0;
    double mulf = X;
    int i;
    int pwr = (int) (p * java.lang.Math.signum(p));

    if (p < 0)
      mulf = 1.0 / (1E-8 + Math.abs(mulf));

    for (i = 1; i <= pwr; i++) {
      ipret *= mulf;
    }
    return ipret;
  }

  double nonz(double nz) {
    if (Math.abs(nz) <= EPS) {
      return EPS * java.lang.Math.signum(nz);
    }
    return nz;
  }

  double safe_acot(double angle) {
    if (Math.abs(angle) <= EPS) {
      return 0.0;
    }
    return Math.atan2(1.0, angle);
  }

  double safe_cot(double angle) {
    double sinA, cosA;
    //fsincos(angle,sinA,cosA);
    sinA = Math.sin(angle);
    cosA = Math.cos(angle);
    return cosA / nonz(sinA);
  }

  double safe_tan(double angle) {
    double sinA, cosA;

    // fsincos(angle,&sinA,&cosA);
    sinA = Math.sin(angle);
    cosA = Math.cos(angle);
    return sinA / nonz(cosA);
  }

  double safe_sqrt(double ssq) {
    return java.lang.Math.signum(ssq) * Math.sqrt(Math.abs(ssq));
  }

  double safe_sqrt2(double ssq) {
    return Math.sqrt(Math.abs(ssq));
  }
  //Work made by Kenneth Haughland, modified by me. CPOL license.
  //http://www.codeproject.com/Articles/566614/Elliptic-integrals

  double RF(double X, double Y, double Z) // Computes the R_F from Carlson symmetric form
  {
    double result = 0;
    double A = 0;
    double lamda = 0;
    double dx = 0;
    double dy = 0;
    double dz = 0;
    double MinError = 1E-5; // lowered
    int MaxIt = 25; // speed
    int ItC = 0;

    do {
      lamda = Math.sqrt(X * Y) + Math.sqrt(Y * Z) + Math.sqrt(Z * X);

      X = (X + lamda) / 4.;
      Y = (Y + lamda) / 4.;
      Z = (Z + lamda) / 4.;

      A = (X + Y + Z) / 3.;

      dx = (1. - X / A);
      dy = (1. - Y / A);
      dz = (1. - Z / A);

      ItC++;
      if (ItC >= MaxIt)
        break;
    }
    while (Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz)) > MinError);

    double E2 = 0;
    double E3 = 0;
    E2 = dx * dy + dy * dz + dz * dx;
    E3 = dy * dx * dz;

    //http://dlmf.nist.gov/19.36#E1
    result = 1.0 - (1. / 10.) * E2 + (1. / 14.) * E3 + (1. / 24.) * intpow(E2, 2)
        - (3. / 44.) * E2 * E3 - (5. / 208.) * intpow(E2, 3)
        + (3. / 104.) * intpow(E3, 2)
        + (1. / 16.) * intpow(E2, 2) * E3;

    result *= (1.0 / Math.sqrt(A));
    return result;

  }

  //END OF Work made by Kenneth Haughland, modified by me. CPOL license.

  //C version of a Matlab code by Moiseev Igor
  //adapted, and modified some functions.
  //The result is far from the "exact" I know. Accept it as is...

  /* Copyright (c) 2007, Moiseev Igor
      All rights reserved.
      
      Redistribution and use in source and binary forms, with or without
      modification, are permitted provided that the following conditions are
      met:
      
          * Redistributions of source code must retain the above copyright
            notice, this list of conditions and the following disclaimer.
          * Redistributions in binary form must reproduce the above copyright
            notice, this list of conditions and the following disclaimer in
            the documentation and/or other materials provided with the distribution
      
      THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
      AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
      IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
      ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
      LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
      CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
      SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
      INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
      CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
      ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
      POSSIBILITY OF SUCH DAMAGE.
  */

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Jacobi-elliptic-integral by dark-beam, https://www.deviantart.com/dark-beam/art/Jacobi-incomp-ell-int-of-the-1st-kind-plugin-462461247

    double e_phi, e_psi;
    double result = 0.0;
    double sinA, cosA;
    double phi, psi;

    phi = pAffineTP.x;
    psi = pAffineTP.y;

    double cotphi = safe_cot(phi);

    double cotphi2 = sqr(cotphi);
    double b = -(cotphi2 + k * sqr(java.lang.Math.sinh(psi) / (EPS + Math.sin(phi))) - 1. + k);
    b = b / 2;
    double c = -(1. - k) * cotphi2;
    c = safe_sqrt2(sqr(b) - c);
    double X1 = Math.max(-b + c, -b - c);
    double mu = safe_sqrt2(
        (X1 / nonz(cotphi2) - 1.0) / nonz(k)); // Expanding all implicit stuff, it should be simply as that
    mu = java.lang.Math.signum(psi) * mu;
    double lambda = java.lang.Math.signum(phi) * safe_sqrt2(X1);

    // this is kinda lightweight.
    // I expanded sin and cos of atan and acot to rational formulas.

    // real part.
    sinA = java.lang.Math.signum(lambda) / Math.sqrt(sqr(lambda) + 1.0);
    cosA = lambda * sinA;
    e_phi = sinA * RF(sqr(cosA),
        1.0 - k * sqr(sinA), 1.0);

    // imag part.      
    cosA = 1.0 / Math.sqrt(sqr(mu) + 1.0);
    sinA = mu * cosA;
    e_psi = sinA * RF(sqr(cosA),
        1.0 - (1. - k) * sqr(sinA), 1.0);

    pVarTP.x += pAmount * e_phi;
    pVarTP.y += pAmount * e_psi;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { k };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_K.equalsIgnoreCase(pName))
      k = limitVal(pValue, -1.0, 1.0);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "jac_elk";
  }

  @Override
  public boolean dynamicParameterExpansion() {
    return true;
  }

  @Override
  public boolean dynamicParameterExpansion(String pName) {
    // preset_id doesn't really expand parameters, but it changes them; this will make them refresh
    return true;
  }
}
