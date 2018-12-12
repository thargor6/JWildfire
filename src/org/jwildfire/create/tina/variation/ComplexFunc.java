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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class ComplexFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_COSPOW = "cospow";
  private static final String PARAM_COSX1 = "cosx1";
  private static final String PARAM_COSX2 = "cosx2";
  private static final String PARAM_COSY1 = "cosy1";
  private static final String PARAM_COSY2 = "cosy2";

  private static final String PARAM_COSHPOW = "coshpow";
  private static final String PARAM_COSHX1 = "coshx1";
  private static final String PARAM_COSHX2 = "coshx2";
  private static final String PARAM_COSHY1 = "coshy1";
  private static final String PARAM_COSHY2 = "coshy2";

  private static final String PARAM_COTPOW = "cotpow";
  private static final String PARAM_COTX1 = "cotx1";
  private static final String PARAM_COTX2 = "cotx2";
  private static final String PARAM_COTY1 = "coty1";
  private static final String PARAM_COTY2 = "coty2";

  private static final String PARAM_COTHPOW = "cothpow";
  private static final String PARAM_COTHX1 = "cothx1";
  private static final String PARAM_COTHX2 = "cothx2";
  private static final String PARAM_COTHY1 = "cothy1";
  private static final String PARAM_COTHY2 = "cothy2";

  private static final String PARAM_CSCPOW = "cscpow";
  private static final String PARAM_CSCX1 = "cscx1";
  private static final String PARAM_CSCX2 = "cscx2";
  private static final String PARAM_CSCY1 = "cscy1";
  private static final String PARAM_CSCY2 = "cscy2";

  private static final String PARAM_CSCHPOW = "cschpow";
  private static final String PARAM_CSCHX1 = "cschx1";
  private static final String PARAM_CSCHX2 = "cschx2";
  private static final String PARAM_CSCHY1 = "cschy1";
  private static final String PARAM_CSCHY2 = "cschy2";

  private static final String PARAM_EXPPOW = "exppow";
  private static final String PARAM_EXPX1 = "expx1";
  private static final String PARAM_EXPY1 = "expy1";
  private static final String PARAM_EXPY2 = "expy2";

  private static final String PARAM_SECPOW = "secpow";
  private static final String PARAM_SECX1 = "secx1";
  private static final String PARAM_SECX2 = "secx2";
  private static final String PARAM_SECY1 = "secy1";
  private static final String PARAM_SECY2 = "secy2";

  private static final String PARAM_SECHPOW = "sechpow";
  private static final String PARAM_SECHX1 = "sechx1";
  private static final String PARAM_SECHX2 = "sechx2";
  private static final String PARAM_SECHY1 = "sechy1";
  private static final String PARAM_SECHY2 = "sechy2";

  private static final String PARAM_SINPOW = "sinpow";
  private static final String PARAM_SINX1 = "sinx1";
  private static final String PARAM_SINX2 = "sinx2";
  private static final String PARAM_SINY1 = "siny1";
  private static final String PARAM_SINY2 = "siny2";

  private static final String PARAM_SINHPOW = "sinhpow";
  private static final String PARAM_SINHX1 = "sinhx1";
  private static final String PARAM_SINHX2 = "sinhx2";
  private static final String PARAM_SINHY1 = "sinhy1";
  private static final String PARAM_SINHY2 = "sinhy2";

  private static final String PARAM_TANPOW = "tanpow";
  private static final String PARAM_TANX1 = "tanx1";
  private static final String PARAM_TANX2 = "tanx2";
  private static final String PARAM_TANY1 = "tany1";
  private static final String PARAM_TANY2 = "tany2";

  private static final String PARAM_TANHPOW = "tanhpow";
  private static final String PARAM_TANHX1 = "tanhx1";
  private static final String PARAM_TANHX2 = "tanhx2";
  private static final String PARAM_TANHY1 = "tanhy1";
  private static final String PARAM_TANHY2 = "tanhy2";

  private static final String[] paramNames = {PARAM_COSPOW, PARAM_COSX1,
          PARAM_COSX2, PARAM_COSY1, PARAM_COSY2, PARAM_COSHPOW, PARAM_COSHX1,
          PARAM_COSHX2, PARAM_COSHY1, PARAM_COSHY2, PARAM_COTPOW,
          PARAM_COTX1, PARAM_COTX2, PARAM_COTY1, PARAM_COTY2, PARAM_COTHPOW,
          PARAM_COTHX1, PARAM_COTHX2, PARAM_COTHY1, PARAM_COTHY2,
          PARAM_CSCPOW, PARAM_CSCX1, PARAM_CSCX2, PARAM_CSCY1, PARAM_CSCY2,
          PARAM_CSCHPOW, PARAM_CSCHX1, PARAM_CSCHX2, PARAM_CSCHY1,
          PARAM_CSCHY2, PARAM_EXPPOW, PARAM_EXPX1, PARAM_EXPY1, PARAM_EXPY2,
          PARAM_SECPOW, PARAM_SECX1, PARAM_SECX2, PARAM_SECY1, PARAM_SECY2,
          PARAM_SECHPOW, PARAM_SECHX1, PARAM_SECHX2, PARAM_SECHY1,
          PARAM_SECHY2, PARAM_SINPOW, PARAM_SINX1, PARAM_SINX2, PARAM_SINY1,
          PARAM_SINY2, PARAM_SINHPOW, PARAM_SINHX1, PARAM_SINHX2,
          PARAM_SINHY1, PARAM_SINHY2, PARAM_TANPOW, PARAM_TANX1, PARAM_TANX2,
          PARAM_TANY1, PARAM_TANY2, PARAM_TANHPOW, PARAM_TANHX1,
          PARAM_TANHX2, PARAM_TANHY1, PARAM_TANHY2};
  private double cospow = 1.0;
  private double cosx1 = 1.0;
  private double cosx2 = 1.0;
  private double cosy1 = 1.0;
  private double cosy2 = 1.0;

  private double coshpow = 0.0;
  private double coshx1 = 1.0;
  private double coshx2 = 1.0;
  private double coshy1 = 1.0;
  private double coshy2 = 1.0;

  private double cotpow = 0.0;
  private double cotx1 = 2.0;
  private double cotx2 = 2.0;
  private double coty1 = 2.0;
  private double coty2 = 2.0;

  private double cothpow = 0.0;
  private double cothx1 = 2.0;
  private double cothx2 = 2.0;
  private double cothy1 = 2.0;
  private double cothy2 = 2.0;

  private double cscpow = 0.0;
  private double cscx1 = 1.0;
  private double cscx2 = 1.0;
  private double cscy1 = 1.0;
  private double cscy2 = 1.0;

  private double cschpow = 0.0;
  private double cschx1 = 1.0;
  private double cschx2 = 1.0;
  private double cschy1 = 1.0;
  private double cschy2 = 1.0;

  private double exppow = 0.0;
  private double expx1 = 1.0;
  private double expy1 = 1.0;
  private double expy2 = 1.0;

  private double secpow = 0.0;
  private double secx1 = 1.0;
  private double secx2 = 1.0;
  private double secy1 = 1.0;
  private double secy2 = 1.0;

  private double sechpow = 0.0;
  private double sechx1 = 1.0;
  private double sechx2 = 1.0;
  private double sechy1 = 1.0;
  private double sechy2 = 1.0;

  private double sinpow = 0.0;
  private double sinx1 = 1.0;
  private double sinx2 = 1.0;
  private double siny1 = 1.0;
  private double siny2 = 1.0;

  private double sinhpow = 0.0;
  private double sinhx1 = 1.0;
  private double sinhx2 = 1.0;
  private double sinhy1 = 1.0;
  private double sinhy2 = 1.0;

  private double tanpow = 0.0;
  private double tanx1 = 2.0;
  private double tanx2 = 2.0;
  private double tany1 = 2.0;
  private double tany2 = 2.0;

  private double tanhpow = 0.0;
  private double tanhx1 = 2.0;
  private double tanhx2 = 2.0;
  private double tanhy1 = 2.0;
  private double tanhy2 = 2.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm,
                        XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* complex vars by cothe */
    /* exp log sin cos tan sec csc cot sinh cosh tanh sech csch coth */
    /* Variables and combination by Brad Stefanov */

    double x = 0.0, y = 0.0;

    if (cospow != 0) {
      double cossin = sin(pAffineTP.x * cosx1);
      double coscos = cos(pAffineTP.x * cosx2);
      double cossinh = sinh(pAffineTP.y * cosy1);
      double coscosh = cosh(pAffineTP.y * cosy2);
      x += cospow * coscos * coscosh;
      y -= cospow * cossin * cossinh;
    }

    if (coshpow != 0) {
      double coshsin = sin(pAffineTP.y * coshy1);
      double coshcos = cos(pAffineTP.y * coshy2);
      double coshsinh = sinh(pAffineTP.x * coshx1);
      double coshcosh = cosh(pAffineTP.x * coshx2);
      x += coshpow * coshcosh * coshcos;
      y += coshpow * coshsinh * coshsin;
    }
    if (cotpow != 0) {
      double cotsin = sin(cotx1 * pAffineTP.x);
      double cotcos = cos(cotx2 * pAffineTP.x);
      double cotsinh = sinh(coty1 * pAffineTP.y);
      double cotcosh = cosh(coty2 * pAffineTP.y);
      double cotden = 1.0 / (cotcosh - cotcos);
      x += cotpow * cotden * cotsin;
      y -= cotpow * cotden * cotsinh;
    }
    if (cothpow != 0) {
      double cothsin = sin(cothy1 * pAffineTP.y);
      double cothcos = cos(cothy2 * pAffineTP.y);
      double cothsinh = sinh(cothx1 * pAffineTP.x);
      double cothcosh = cosh(cothx2 * pAffineTP.x);
      double cothd = (cothcosh - cothcos);
      if (cothd == 0)
        return;
      double cothden = 1.0 / cothd;
      x += cothpow * cothden * cothsinh;
      y += cothpow * cothden * cothsin;
    }
    if (cscpow != 0) {
      double cscsin = sin(pAffineTP.x * cscx1);
      double csccos = cos(pAffineTP.x * cscx2);
      double cscsinh = sinh(pAffineTP.y * cscy1);
      double csccosh = cosh(pAffineTP.y * cscy2);
      double cscd = (cosh(2.0 * pAffineTP.y) - cos(2.0 * pAffineTP.x));
      if (cscd == 0) {
        return;
      }
      double cscden = 2.0 / cscd;
      x += cscpow * cscden * cscsin * csccosh;
      y -= cscpow * cscden * csccos * cscsinh;

    }

    if (cschpow != 0) {
      double cschsin = sin(pAffineTP.y * cschy1);
      double cschcos = cos(pAffineTP.y * cschy2);
      double cschsinh = sinh(pAffineTP.x * cschx1);
      double cschcosh = cosh(pAffineTP.x * cschx2);
      double cschd = (cosh(2.0 * pAffineTP.x) - cos(2.0 * pAffineTP.y));
      if (cschd == 0) {
        return;
      }
      double cschden = 2.0 / cschd;
      x += cschpow * cschden * cschsinh * cschcos;
      y -= cschpow * cschden * cschcosh * cschsin;
    }

    if (exppow != 0) {
      double expe = exp(pAffineTP.x * expx1);
      double expsin = sin(pAffineTP.y * expy1);
      double expcos = cos(pAffineTP.y * expy2);
      x += exppow * expe * expcos;
      y += exppow * expe * expsin;

    }
    if (secpow != 0) {

      double secsin = sin(pAffineTP.x * secx1);
      double seccos = cos(pAffineTP.x * secx2);
      double secsinh = sinh(pAffineTP.y * secy1);
      double seccosh = cosh(pAffineTP.y * secy2);
      double secd = (cos(2.0 * pAffineTP.x) + cosh(2.0 * pAffineTP.y));
      if (secd == 0) {
        return;
      }
      double secden = 2.0 / secd;
      x += secpow * secden * seccos * seccosh;
      y += secpow * secden * secsin * secsinh;
    }

    if (sechpow != 0) {
      double sechsinh = sin(pAffineTP.y * sechy1);
      double sechcosh = cos(pAffineTP.y * sechy2);
      double sechsin = sinh(pAffineTP.x * sechx1);
      double sechcos = cosh(pAffineTP.x * sechx2);
      double sechd = (cos(2.0 * pAffineTP.y) + cosh(2.0 * pAffineTP.x));
      if (sechd == 0) {
        return;
      }
      double sechden = 2.0 / sechd;
      x += sechpow * sechden * sechcos * sechcosh;
      y -= sechpow * sechden * sechsin * sechsinh;
    }

    if (tanpow != 0) {
      double tansin = sin(tanx1 * pAffineTP.x);
      double tancos = cos(tanx2 * pAffineTP.x);
      double tansinh = sinh(tany1 * pAffineTP.y);
      double tancosh = cosh(tany2 * pAffineTP.y);
      double tand = (tancos + tancosh);
      if (tand == 0) {
        return;
      }
      double tanden = 1.0 / tand;
      x += tanpow * tanden * tansin;
      y += tanpow * tanden * tansinh;
    }
    if (tanhpow != 0) {
      double tanhsin = sin(pAffineTP.y * tanhy1);
      double tanhcos = cos(pAffineTP.y * tanhy2);
      double tanhsinh = sinh(pAffineTP.x * tanhx1);
      double tanhcosh = cosh(pAffineTP.x * tanhx2);
      double tanhd = (tanhcos + tanhcosh);
      if (tanhd == 0) {
        return;
      }
      double tanhden = 1.0 / tanhd;
      x += tanhpow * tanhden * tanhsinh;
      y += tanhpow * tanhden * tanhsin;
    }
    if (sinpow != 0) {
      double sinsin = sin(pAffineTP.x * sinx1);
      double sincos = cos(pAffineTP.x * sinx2);
      double sinsinh = sinh(pAffineTP.y * siny1);
      double sincosh = cosh(pAffineTP.y * siny2);
      x += sinpow * sinsin * sincosh;
      y += sinpow * sincos * sinsinh;
    }
    if (sinhpow != 0) {
      double sinhsin = sin(pAffineTP.y * sinhy1);
      double sinhcos = cos(pAffineTP.y * sinhy2);
      double sinhsinh = sinh(pAffineTP.x * sinhx1);
      double sinhcosh = cosh(pAffineTP.x * sinhx2);
      x += sinhpow * sinhsinh * sinhcos;
      y += sinhpow * sinhcosh * sinhsin;
    }
    pVarTP.x += pAmount * x;
    pVarTP.y += pAmount * y;

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
    return new Object[]{cospow, cosx1, cosx2, cosy1, cosy2, coshpow,
            coshx1, coshx2, coshy1, coshy2, cotpow, cotx1, cotx2, coty1,
            coty2, cothpow, cothx1, cothx2, cothy1, cothy2, cscpow, cscx1,
            cscx2, cscy1, cscy2, cschpow, cschx1, cschx2, cschy1, cschy2,
            exppow, expx1, expy1, expy2, secpow, secx1, secx2, secy1,
            secy2, sechpow, sechx1, sechx2, sechy1, sechy2, sinpow, sinx1,
            sinx2, siny1, siny2, sinhpow, sinhx1, sinhx2, sinhy1, sinhy2,
            tanpow, tanx1, tanx2, tany1, tany2, tanhpow, tanhx1, tanhx2,
            tanhy1, tanhy2};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_COSPOW.equalsIgnoreCase(pName))
      cospow = pValue;
    else if (PARAM_COSX1.equalsIgnoreCase(pName))
      cosx1 = pValue;
    else if (PARAM_COSX2.equalsIgnoreCase(pName))
      cosx2 = pValue;
    else if (PARAM_COSY1.equalsIgnoreCase(pName))
      cosy1 = pValue;
    else if (PARAM_COSY2.equalsIgnoreCase(pName))
      cosy2 = pValue;
    else if (PARAM_COSHPOW.equalsIgnoreCase(pName))
      coshpow = pValue;
    else if (PARAM_COSHX1.equalsIgnoreCase(pName))
      coshx1 = pValue;
    else if (PARAM_COSHX2.equalsIgnoreCase(pName))
      coshx2 = pValue;
    else if (PARAM_COSHY1.equalsIgnoreCase(pName))
      coshy1 = pValue;
    else if (PARAM_COSHY2.equalsIgnoreCase(pName))
      coshy2 = pValue;
    else if (PARAM_COTPOW.equalsIgnoreCase(pName))
      cotpow = pValue;
    else if (PARAM_COTX1.equalsIgnoreCase(pName))
      cotx1 = pValue;
    else if (PARAM_COTX2.equalsIgnoreCase(pName))
      cotx2 = pValue;
    else if (PARAM_COTY1.equalsIgnoreCase(pName))
      coty1 = pValue;
    else if (PARAM_COTY2.equalsIgnoreCase(pName))
      coty2 = pValue;
    else if (PARAM_COTHPOW.equalsIgnoreCase(pName))
      cothpow = pValue;
    else if (PARAM_COTHX1.equalsIgnoreCase(pName))
      cothx1 = pValue;
    else if (PARAM_COTHX2.equalsIgnoreCase(pName))
      cothx2 = pValue;
    else if (PARAM_COTHY1.equalsIgnoreCase(pName))
      cothy1 = pValue;
    else if (PARAM_COTHY2.equalsIgnoreCase(pName))
      cothy2 = pValue;
    else if (PARAM_CSCPOW.equalsIgnoreCase(pName))
      cscpow = pValue;
    else if (PARAM_CSCX1.equalsIgnoreCase(pName))
      cscx1 = pValue;
    else if (PARAM_CSCX2.equalsIgnoreCase(pName))
      cscx2 = pValue;
    else if (PARAM_CSCY1.equalsIgnoreCase(pName))
      cscy1 = pValue;
    else if (PARAM_CSCY2.equalsIgnoreCase(pName))
      cscy2 = pValue;
    else if (PARAM_CSCHPOW.equalsIgnoreCase(pName))
      cschpow = pValue;
    else if (PARAM_CSCHX1.equalsIgnoreCase(pName))
      cschx1 = pValue;
    else if (PARAM_CSCHX2.equalsIgnoreCase(pName))
      cschx2 = pValue;
    else if (PARAM_CSCHY1.equalsIgnoreCase(pName))
      cschy1 = pValue;
    else if (PARAM_CSCHY2.equalsIgnoreCase(pName))
      cschy2 = pValue;
    else if (PARAM_EXPPOW.equalsIgnoreCase(pName))
      exppow = pValue;
    else if (PARAM_EXPX1.equalsIgnoreCase(pName))
      expx1 = pValue;
    else if (PARAM_EXPY1.equalsIgnoreCase(pName))
      expy1 = pValue;
    else if (PARAM_EXPY2.equalsIgnoreCase(pName))
      expy2 = pValue;
    else if (PARAM_SECPOW.equalsIgnoreCase(pName))
      secpow = pValue;
    else if (PARAM_SECX1.equalsIgnoreCase(pName))
      secx1 = pValue;
    else if (PARAM_SECX2.equalsIgnoreCase(pName))
      secx2 = pValue;
    else if (PARAM_SECY1.equalsIgnoreCase(pName))
      secy1 = pValue;
    else if (PARAM_SECY2.equalsIgnoreCase(pName))
      secy2 = pValue;
    else if (PARAM_SECHPOW.equalsIgnoreCase(pName))
      sechpow = pValue;
    else if (PARAM_SECHX1.equalsIgnoreCase(pName))
      sechx1 = pValue;
    else if (PARAM_SECHX2.equalsIgnoreCase(pName))
      sechx2 = pValue;
    else if (PARAM_SECHY1.equalsIgnoreCase(pName))
      sechy1 = pValue;
    else if (PARAM_SECHY2.equalsIgnoreCase(pName))
      sechy2 = pValue;
    else if (PARAM_SINPOW.equalsIgnoreCase(pName))
      sinpow = pValue;
    else if (PARAM_SINX1.equalsIgnoreCase(pName))
      sinx1 = pValue;
    else if (PARAM_SINX2.equalsIgnoreCase(pName))
      sinx2 = pValue;
    else if (PARAM_SINY1.equalsIgnoreCase(pName))
      siny1 = pValue;
    else if (PARAM_SINY2.equalsIgnoreCase(pName))
      siny2 = pValue;
    else if (PARAM_SINHPOW.equalsIgnoreCase(pName))
      sinhpow = pValue;
    else if (PARAM_SINHX1.equalsIgnoreCase(pName))
      sinhx1 = pValue;
    else if (PARAM_SINHX2.equalsIgnoreCase(pName))
      sinhx2 = pValue;
    else if (PARAM_SINHY1.equalsIgnoreCase(pName))
      sinhy1 = pValue;
    else if (PARAM_SINHY2.equalsIgnoreCase(pName))
      sinhy2 = pValue;
    else if (PARAM_TANPOW.equalsIgnoreCase(pName))
      tanpow = pValue;
    else if (PARAM_TANX1.equalsIgnoreCase(pName))
      tanx1 = pValue;
    else if (PARAM_TANX2.equalsIgnoreCase(pName))
      tanx2 = pValue;
    else if (PARAM_TANY1.equalsIgnoreCase(pName))
      tany1 = pValue;
    else if (PARAM_TANY2.equalsIgnoreCase(pName))
      tany2 = pValue;
    else if (PARAM_TANHPOW.equalsIgnoreCase(pName))
      tanhpow = pValue;
    else if (PARAM_TANHX1.equalsIgnoreCase(pName))
      tanhx1 = pValue;
    else if (PARAM_TANHX2.equalsIgnoreCase(pName))
      tanhx2 = pValue;
    else if (PARAM_TANHY1.equalsIgnoreCase(pName))
      tanhy1 = pValue;
    else if (PARAM_TANHY2.equalsIgnoreCase(pName))
      tanhy2 = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "complex";
  }

}
