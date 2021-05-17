/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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

public class Waves2BFunc extends VariationFunc implements SupportsGPU {
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

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float _six, _siy;\n"
        + "_six = varpar->waves2b_scalex - varpar->waves2b_scaleinfx;\n"
        + "_siy = varpar->waves2b_scaley - varpar->waves2b_scaleinfy;\n"
        + "float CsX = 1.0f;\n"
        + "float CsY = 1.0f;\n"
        + "float JCB_sn;\n"
        + "CsX = waves2b_safediv(varpar->waves2b_unity, (varpar->waves2b_unity + __x*__x));\n"
        + "CsX = CsX * _six + varpar->waves2b_scaleinfx;\n"
        + "CsY = waves2b_safediv(varpar->waves2b_unity, (varpar->waves2b_unity + __y*__y));\n"
        + "CsY = CsY * _siy + varpar->waves2b_scaleinfy;\n"
        + "if (varpar->waves2b_pwx >= 0.f && varpar->waves2b_pwx < 1.0e-4f) {\n"
        + "  JCB_sn = waves2b_Jacobi_elliptic(__y * varpar->waves2b_freqx, varpar->waves2b_jacok);\n"
        + "  __px += varpar->waves2b * (__x + CsX * JCB_sn);\n"
        + "} else if (varpar->waves2b_pwx < 0.f && varpar->waves2b_pwx > -1.0e-4f) {\n"
        + "  __px += varpar->waves2b * (__x + CsX * j1f(__y * varpar->waves2b_freqx));\n"
        + "} else {\n"
        + "  __px += varpar->waves2b * (__x + CsX * sinf((__y<0.f ? -1 : __y>0.f ? 1 : 0) * powf(fabsf(__y) + 1e-10f, varpar->waves2b_pwx) * varpar->waves2b_freqx));\n"
        + "}\n"
        + "if (varpar->waves2b_pwy >= 0.f && varpar->waves2b_pwy < 1.0e-4f) {\n"
        + "   JCB_sn = waves2b_Jacobi_elliptic(__x * varpar->waves2b_freqy, varpar->waves2b_jacok);\n"
        + "   __py += varpar->waves2b * (__y + CsY * JCB_sn);\n"
        + "} else if (varpar->waves2b_pwy < 0.f && varpar->waves2b_pwy > -1.0e-4f) {\n"
        + "   __py += varpar->waves2b * (__y + CsY * j1f(__x * varpar->waves2b_freqy));\n"
        + "} else {\n"
        + "   __py += varpar->waves2b * (__y + CsY * sinf((__x<0.f ? -1 : __x>0.f ? 1 : 0) * powf(fabsf(__x) + 1e-10f, varpar->waves2b_pwy) * varpar->waves2b_freqy));\n"
        + "}\n"
        + (context.isPreserveZCoordinate() ? "__pz += varpar->waves2b * __z;\n" : "");
  }
  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return "__device__ float waves2b_safediv(float q, float r) {\n"
        + "    if (r < 1.0e-10f)\n"
        + "      return 1.0f / r;\n"
        + "    return q / r;\n"
        + "  }\n"
        +"__device__ float waves2b_Jacobi_elliptic(float uu, float emmc) {\n"
        + "    float sn = 0.0f;\n"
        + "    float CA = 0.0003f; \n"
        + "    float a, b, c = 0, d = 0, em[13], en[13];\n"
        + "    float dn, cn;\n"
        + "    int bo;\n"
        + "    int l = 0;\n"
        + "    int ii;\n"
        + "    int i;\n"
        + "    float emc = emmc;\n"
        + "    float u = uu;\n"
        + "    if (emc != 0.0f) {\n"
        + "      bo = 0;\n"
        + "      if (emc < 0.0f)\n"
        + "        bo = 1;\n"
        + "      if (bo != 0) {\n"
        + "        d = 1.0f - emc;\n"
        + "        emc = -emc / d;\n"
        + "        d = sqrtf(d);\n"
        + "        u = d * u;\n"
        + "      }\n"
        + "      a = 1.0f;\n"
        + "      dn = 1.0f;\n"
        + "      for (i = 0; i < 8; i++) {\n"
        + "        l = i;\n"
        + "        em[i] = a;\n"
        + "        emc = sqrtf(emc);\n"
        + "        en[i] = emc;\n"
        + "        c = 0.5f * (a + emc);\n"
        + "        if (fabsf(a - emc) <= CA * a)\n"
        + "          break;\n"
        + "        emc = a * emc;\n"
        + "        a = c;\n"
        + "      }\n"
        + "      u = c * u;\n"
        + "      sn = sinf(u);\n"
        + "      cn = cosf(u);\n"
        + "\n"
        + "      if (sn != 0.0f) {\n"
        + "        a = cn / sn;\n"
        + "        c = a * c;\n"
        + "        for (ii = l; ii >= 0; --ii) {\n"
        + "          b = em[ii];\n"
        + "          a = c * a;\n"
        + "          c = dn * c;\n"
        + "          dn = (en[ii] + a) / (b + a);\n"
        + "          a = c / b;\n"
        + "        }\n"
        + "        a = 1.0f / sqrtf(c * c + 1.0f);\n"
        + "        if (sn < 0.0f)\n"
        + "          sn = -a;\n"
        + "        else\n"
        + "          sn = a;\n"
        + "        cn = c * sn;\n"
        + "      }\n"
        + "      if (bo != 0) {\n"
        + "        a = dn;\n"
        + "        dn = cn;\n"
        + "        cn = a;\n"
        + "        sn = sn / d;\n"
        + "      }\n"
        + "    } else {\n"
        + "      sn = tanhf(u);\n"
        + "    }\n"
        + "    return sn;\n"
        + "  }\n";
  }
}
