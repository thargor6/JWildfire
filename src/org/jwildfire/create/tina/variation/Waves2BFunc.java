/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Waves2BFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQX = "freqx";
  private static final String PARAM_FREQY = "freqy";
  private static final String PARAM_PWX = "pwx";
  private static final String PARAM_PWY = "pwy";
  private static final String PARAM_SCALEX = "scalex";
  private static final String PARAM_SCALEINFX = "scaleinfx";
  private static final String PARAM_SCALEY = "scaley";
  private static final String PARAM_SCALEINFY = "scaleinfy";
  private static final String PARAM_UNITY = "unity";
  private static final String PARAM_JACOK = "jacok";

  private static final String[] paramNames = {PARAM_FREQX, PARAM_FREQY, PARAM_PWX, PARAM_PWY, PARAM_SCALEX, PARAM_SCALEINFX, PARAM_SCALEY, PARAM_SCALEINFY, PARAM_UNITY, PARAM_JACOK};

  private double freqx = 2.0;
  private double freqy = 2.0;
  private double pwx = 1.0;
  private double pwy = 1.0;
  private double scalex = 1.0;
  private double scaleinfx = 1.0;
  private double scaley = 1.0;
  private double scaleinfy = 1.0;
  private double unity = 1.0;
  private double jacok = 0.25;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Waves2B by dark-beam, http://dark-beam.deviantart.com/art/Waves2b-UPDATE-FIX-456744888
    double CsX = 1.0;
    double CsY = 1.0;
    double JCB_sn;

    CsX = safediv(unity, (unity + sqr(pAffineTP.x)));
    CsX = CsX * _six + scaleinfx;
    CsY = safediv(unity, (unity + sqr(pAffineTP.y)));
    CsY = CsY * _siy + scaleinfy;

    // x
    if (pwx >= 0 && pwx < 1e-4) { // jacobi sn
      JCB_sn = Jacobi_elliptic(pAffineTP.y * freqx, jacok);
      pVarTP.x += pAmount * (pAffineTP.x + CsX * JCB_sn);
    } else if (pwx < 0 && pwx > -1e-4) { // bessel j1
      pVarTP.x += pAmount * (pAffineTP.x + CsX * cern.jet.math.Bessel.j1(pAffineTP.y * freqx));
    } else { // NORMAL!
      pVarTP.x += pAmount * (pAffineTP.x + CsX * sin(sgn(pAffineTP.y) * pow(fabs(pAffineTP.y) + 1e-10, pwx) * freqx));
    }

    // y
    if (pwy >= 0 && pwy < 1e-4) { // jacobi sn
      JCB_sn = Jacobi_elliptic(pAffineTP.x * freqy, jacok);
      pVarTP.y += pAmount * (pAffineTP.y + CsY * JCB_sn);
    } else if (pwy < 0 && pwy > -1e-4) { // bessel j1
      pVarTP.y += pAmount * (pAffineTP.y + CsY * cern.jet.math.Bessel.j1(pAffineTP.x * freqy));
    } else { // NORMAL!
      pVarTP.y += pAmount * (pAffineTP.y + CsY * sin(sgn(pAffineTP.x) * pow(fabs(pAffineTP.x) + 1e-10, pwy) * freqy));
    }
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
    return new Object[]{freqx, freqy, pwx, pwy, scalex, scaleinfx, scaley, scaleinfy, unity, jacok};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQX.equalsIgnoreCase(pName))
      freqx = pValue;
    else if (PARAM_FREQY.equalsIgnoreCase(pName))
      freqy = pValue;
    else if (PARAM_PWX.equalsIgnoreCase(pName))
      pwx = limitVal(pValue, -10.0, 10.0);
    else if (PARAM_PWY.equalsIgnoreCase(pName))
      pwy = limitVal(pValue, -10.0, 10.0);
    else if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scalex = pValue;
    else if (PARAM_SCALEINFX.equalsIgnoreCase(pName))
      scaleinfx = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scaley = pValue;
    else if (PARAM_SCALEINFY.equalsIgnoreCase(pName))
      scaleinfy = pValue;
    else if (PARAM_UNITY.equalsIgnoreCase(pName))
      unity = pValue;
    else if (PARAM_JACOK.equalsIgnoreCase(pName))
      jacok = limitVal(pValue, -1.0, 1.0);
    else
      throw new IllegalArgumentException(pName);
  }

  private double safediv(double q, double r) {
    if (r < 1e-10)
      return 1 / r;
    return q / r;
  }

  private double sgn(double x) {
    // if (x == 0.0) return x;
    return (x < 0 ? -1.0 : 1.0);
  }

  @Override
  public String getName() {
    return "waves2b";
  }

  private double _six, _siy;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _six = scalex - scaleinfx;
    _siy = scaley - scaleinfy;
  }

  double Jacobi_elliptic(double uu, double emmc) {
    double sn = 0.0;
    // Code is taken from IROIRO++ library,
    // released under CC share-alike license.

    // less accurate for faster rendering (still very precise)
    final double CA = 0.0003; // (The accuracy is the square of CA.)

    double a, b, c = 0, d = 0, em[] = new double[13], en[] = new double[13];
    double dn, cn;
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
      dn = 1.0;

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
      sn = sin(u);
      cn = cos(u);

      if (sn != 0.0) {

        a = cn / sn;
        c = a * c;
        for (ii = l; ii >= 0; --ii) {
          b = em[ii];
          a = c * a;
          c = dn * c;
          dn = (en[ii] + a) / (b + a);
          a = c / b;
        }

        a = 1.0 / sqrt(c * c + 1.0);
        if (sn < 0.0)
          sn = -a;
        else
          sn = a;
        cn = c * sn;
      }
      if (bo != 0) {
        a = dn;
        dn = cn;
        cn = a;
        sn = sn / d;
      }
    } else {
      // cn = 1.0/cosh(u);
      // dn = cn;
      sn = tanh(u);
    }
    return sn;
  }
}
