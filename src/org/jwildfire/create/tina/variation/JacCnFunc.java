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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class JacCnFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_K = "k";

  private static final String[] paramNames = {PARAM_K};

  private double k = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Jacobi-elliptic-CN by dark-beam, http://dark-beam.deviantart.com/art/Jacobi-elliptic-sn-cn-and-dn-Apoplugins-460783612
    double NumX, NumY, Denom;
    Jacobi_elliptic_result jac_x = new Jacobi_elliptic_result();
    Jacobi_elliptic(pAffineTP.x, k, jac_x);
    Jacobi_elliptic_result jac_y = new Jacobi_elliptic_result();
    Jacobi_elliptic(pAffineTP.y, 1.0 - k, jac_y);

    NumX = jac_x.cn * jac_y.cn;
    NumY = -jac_x.dn * jac_x.sn * jac_y.dn * jac_y.sn;
    Denom = sqr(jac_x.sn) * sqr(jac_y.sn) * k + sqr(jac_y.cn);
    Denom = pAmount / (SMALL_EPSILON + Denom);
    pVarTP.x += Denom * NumX;
    pVarTP.y += Denom * NumY;
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
    return new Object[]{k};
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
    return "jac_cn";
  }

  public static class Jacobi_elliptic_result {
    public double sn, cn, dn;

    public void clear() {
      sn = cn = dn = 0.0;
    }
  }

  public static void Jacobi_elliptic(double uu, double emmc, Jacobi_elliptic_result res) {
    // Code is taken from IROIRO++ library,
    // released under CC share-alike license.

    res.clear();

    // less accurate for faster rendering (still very precise)
    final double CA = 0.0003; // (The accuracy is the square of CA.)

    double a, b, c = 0.0, d = 0.0, em[] = new double[13], en[] = new double[13];
    int bo;
    int l = 0;
    int ii;
    int i;
    // LOGICAL bo

    // main
    double emc = emmc;
    double u = uu;

    if (emc != 0.0) {
      bo = 0;
      if (emc < 0.0)
        bo = 1;

      if (bo != 0) {
        d = 1.0 - emc;
        emc = -emc / d;
        d = sqrt(d);
        u = d * u;
      }
      a = 1.0;
      res.dn = 1.0;

      // for(i=0; i<13; i++){ original
      for (i = 0; i < 8; i++) {
        l = i;
        em[i] = a;
        emc = sqrt(emc);
        en[i] = emc;
        c = 0.5 * (a + emc);
        if (fabs(a - emc) <= CA * a)
          break;
        emc = a * emc;
        a = c;
      }

      u = c * u;
      res.sn = sin(u);
      res.cn = cos(u);

      if (res.sn != 0.0) {

        a = res.cn / res.sn;
        c = a * c;
        for (ii = l; ii >= 0; --ii) {
          b = em[ii];
          a = c * a;
          c = res.dn * c;
          res.dn = (en[ii] + a) / (b + a);
          a = c / b;
        }

        a = 1.0 / sqrt(c * c + 1.0);
        if (res.sn < 0.0)
          (res.sn) = -a;
        else
          res.sn = a;
        res.cn = c * (res.sn);
      }
      if (bo != 0) {
        a = res.dn;
        res.dn = res.cn;
        res.cn = a;
        res.sn = (res.sn) / d;
      }
    } else {
      res.cn = 1.0 / cosh(u);
      res.dn = res.cn;
      (res.sn) = tanh(u);
    }
  }
}
