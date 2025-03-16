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

import static org.jwildfire.base.Tools.FTOI;
import static org.jwildfire.base.mathlib.MathLib.*;

public class SuperShape3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RHO = "rho";
  private static final String PARAM_PHI = "phi";
  private static final String PARAM_M1 = "m1";
  private static final String PARAM_M2 = "m2";
  private static final String PARAM_A1 = "a1";
  private static final String PARAM_A2 = "a2";
  private static final String PARAM_B1 = "b1";
  private static final String PARAM_B2 = "b2";
  private static final String PARAM_N1_1 = "n1_1";
  private static final String PARAM_N1_2 = "n1_2";
  private static final String PARAM_N2_1 = "n2_1";
  private static final String PARAM_N2_2 = "n2_2";
  private static final String PARAM_N3_1 = "n3_1";
  private static final String PARAM_N3_2 = "n3_2";
  private static final String PARAM_SPIRAL = "spiral";
  private static final String PARAM_TOROIDMAP = "toroidmap";
  private static final String[] paramNames = {PARAM_RHO, PARAM_PHI, PARAM_M1, PARAM_M2, PARAM_A1, PARAM_A2, PARAM_B1, PARAM_B2, PARAM_N1_1, PARAM_N1_2, PARAM_N2_1, PARAM_N2_2, PARAM_N3_1, PARAM_N3_2, PARAM_SPIRAL, PARAM_TOROIDMAP};

  private double rho = 9.9;
  private double phi = 2.5;
  private double m1 = 6.0;
  private double m2 = 3.0;
  private double a1 = 1.0;
  private double a2 = 1.0;
  private double b1 = 1.0;
  private double b2 = 1.0;
  private double n1_1 = 1.0;
  private double n1_2 = 1.0;
  private double n2_1 = 1.0;
  private double n2_2 = 1.0;
  private double n3_1 = 1.0;
  private double n3_2 = 1.0;
  private double spiral = 0.0;
  private int toroidmap = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // SuperShape3d by David Young, http://fractal-resources.deviantart.com/gallery/24660058#/d1o8z8x
    double rho1 = pContext.random() * rho_pi;
    double phi1 = pContext.random() * phi_pi;
    int p = (int) fmod(pContext.random(Integer.MAX_VALUE), 2);
    if (p == 1) {
      phi1 = (-phi1);
    }
    double sinr = sin(rho1);
    double cosr = cos(rho1);
    double sinp = sin(phi1);
    double cosp = cos(phi1);

    double msinr, mcosr;
    {
      double a = m4_1 * rho1;
      msinr = sin(a);
      mcosr = cos(a);
    }
    double msinp, mcosp;
    {
      double a = m4_2 * phi1;
      msinp = sin(a);
      mcosp = cos(a);
    }
    double pr1 = an2_1 * pow(fabs(mcosr), n2_1) + bn3_1 * pow(fabs(msinr), n3_1);
    double pr2 = an2_2 * pow(fabs(mcosp), n2_2) + bn3_2 * pow(fabs(msinp), n3_2);
    double r1 = pow(pr1, n1n_1) + spiral * rho1;
    double r2 = pow(pr2, n1n_2);

    if (toroidmap == 1) {
      pVarTP.x += pAmount * cosr * (r1 + r2 * cosp);
      pVarTP.y += pAmount * sinr * (r1 + r2 * cosp);
      pVarTP.z += pAmount * r2 * sinp;
    } else {
      pVarTP.x += pAmount * r1 * cosr * r2 * cosp;
      pVarTP.y += pAmount * r1 * sinr * r2 * cosp;
      pVarTP.z += pAmount * r2 * sinp;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{rho, phi, m1, m2, a1, a2, b1, b2, n1_1, n1_2, n2_1, n2_2, n3_1, n3_2, spiral, toroidmap};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RHO.equalsIgnoreCase(pName))
      rho = pValue;
    else if (PARAM_PHI.equalsIgnoreCase(pName))
      phi = pValue;
    else if (PARAM_M1.equalsIgnoreCase(pName))
      m1 = pValue;
    else if (PARAM_M2.equalsIgnoreCase(pName))
      m2 = pValue;
    else if (PARAM_A1.equalsIgnoreCase(pName))
      a1 = pValue;
    else if (PARAM_A2.equalsIgnoreCase(pName))
      a2 = pValue;
    else if (PARAM_B1.equalsIgnoreCase(pName))
      b1 = pValue;
    else if (PARAM_B2.equalsIgnoreCase(pName))
      b2 = pValue;
    else if (PARAM_N1_1.equalsIgnoreCase(pName))
      n1_1 = pValue;
    else if (PARAM_N1_2.equalsIgnoreCase(pName))
      n1_2 = pValue;
    else if (PARAM_N2_1.equalsIgnoreCase(pName))
      n2_1 = pValue;
    else if (PARAM_N2_2.equalsIgnoreCase(pName))
      n2_2 = pValue;
    else if (PARAM_N3_1.equalsIgnoreCase(pName))
      n3_1 = pValue;
    else if (PARAM_N3_2.equalsIgnoreCase(pName))
      n3_2 = pValue;
    else if (PARAM_SPIRAL.equalsIgnoreCase(pName))
      spiral = pValue;
    else if (PARAM_TOROIDMAP.equalsIgnoreCase(pName)) {
      toroidmap = FTOI(pValue);
      if (toroidmap < 0) {
        toroidmap = 0;
      } else if (toroidmap > 1) {
        toroidmap = 1;
      }
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "superShape3d";
  }

  private double n1n_1, n1n_2, m4_1, m4_2;
  private double an2_1, an2_2, bn3_1, bn3_2;
  private double rho_pi, phi_pi;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    n1n_1 = (-1.0 / n1_1);
    n1n_2 = (-1.0 / n1_2);
    an2_1 = pow(fabs(1.0 / a1), n2_1);
    an2_2 = pow(fabs(1.0 / a2), n2_2);
    bn3_1 = pow(fabs(1.0 / b1), n3_1);
    bn3_2 = pow(fabs(1.0 / b2), n3_2);
    m4_1 = m1 / 4.0;
    m4_2 = m2 / 4.0;
    rho_pi = rho * M_2_PI;
    phi_pi = phi * M_2_PI;
  }
  
  @Override
  public void randomize() {
    rho = Math.random() * 17.272 + 2.467;
    phi = Math.random() * 17.272 + 2.467;
    m1 = Math.random() * 9.0 + 3.0;
    if (Math.random() < 0.5) m1 = (int) m1;
    m2 = Math.random() * 9.0 + 3.0;
    if (Math.random() < 0.5) m2 = (int) m2;
    a1 = Math.random() * 1.5 + 0.5;
    a2 = Math.random() * 1.5 + 0.5;
    b1 = Math.random() * 1.5 + 0.5;
    b2 = Math.random() * 1.5 + 0.5;
    n1_1 = Math.random() * 5.0 - 2.5;
    n1_2 = Math.random() * 5.0 - 2.5;
    n2_1 = Math.random() * 5.0 - 2.5;
    n2_2 = Math.random() * 5.0 - 2.5;
    n3_1 = Math.random() * 5.0 - 2.5;
    n3_2 = Math.random() * 5.0 - 2.5;
    spiral = (Math.random() < 0.5) ? 0.0 : Math.random() * 2.0 - 1.0;
    toroidmap = (int) (Math.random() * 2);
  }
  
  @Override
  public void mutate(double pAmount) {
    switch ((int) (Math.random() * 18)) {
    case 0:
      rho += mutateStep(rho, pAmount);
      break;
    case 1:
      phi += mutateStep(phi, pAmount);
      break;
    case 2:
      m1 = (int) (m1 + pAmount);
      break;
    case 3:
      m1 += mutateStep(m1, pAmount);
      break;
    case 4:
      m2 = (int) (m2 + pAmount);
      break;
    case 5:
      m2 += mutateStep(m2, pAmount);
      break;
    case 6:
      a1 += mutateStep(a1, pAmount);
      break;
    case 7:
      a2 += mutateStep(a2, pAmount);
      break;
    case 8:
      b1 += mutateStep(b1, pAmount);
      break;
    case 9:
      b2 += mutateStep(b2, pAmount);
      break;
    case 10:
      n1_1 += mutateStep(n1_1, pAmount);
      break;
    case 11:
      n1_2 += mutateStep(n1_2, pAmount);
      break;
    case 12:
      n2_1 += mutateStep(n2_1, pAmount);
      break;
    case 13:
      n2_2 += mutateStep(n2_2, pAmount);
      break;
    case 14:
      n3_1 += mutateStep(n3_1, pAmount);
      break;
    case 15:
      n3_2 += mutateStep(n3_2, pAmount);
      break;
    case 16:
      spiral += mutateStep(spiral, pAmount);
      break;
    case 17:
      toroidmap += 1;
      if (toroidmap > 1) toroidmap = 0;
      break;
    }
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float n1n_1, n1n_2, m4_1, m4_2;\n"
        + "float an2_1, an2_2, bn3_1, bn3_2;\n"
        + "float rho_pi, phi_pi;\n"
        + "n1n_1 = (-1.0f / __superShape3d_n1_1);\n"
        + "n1n_2 = (-1.0f / __superShape3d_n1_2);\n"
        + "an2_1 = pow(fabsf(1.0f / __superShape3d_a1), __superShape3d_n2_1);\n"
        + "an2_2 = pow(fabsf(1.0f / __superShape3d_a2), __superShape3d_n2_2);\n"
        + "bn3_1 = pow(fabsf(1.0f / __superShape3d_b1), __superShape3d_n3_1);\n"
        + "bn3_2 = pow(fabsf(1.0f / __superShape3d_b2), __superShape3d_n3_2);\n"
        + "m4_1 = __superShape3d_m1 / 4.0f;\n"
        + "m4_2 = __superShape3d_m2 / 4.0f;\n"
        + "rho_pi = __superShape3d_rho * 2.f / PI;\n"
        + "phi_pi = __superShape3d_phi * 2.f / PI;\n"
        + "float rho1 = RANDFLOAT() * rho_pi;\n"
        + "float phi1 = RANDFLOAT() * phi_pi;\n"
        + "if (RANDFLOAT()<0.5f) {\n"
        + "  phi1 = (-phi1);\n"
        + "}\n"
        + "float sinr = sinf(rho1);\n"
        + "float cosr = cosf(rho1);\n"
        + "float sinp = sinf(phi1);\n"
        + "float cosp = cosf(phi1);\n"

        + "    float msinr, mcosr;\n"
        + "    {\n"
        + "      float a = m4_1 * rho1;\n"
        + "      msinr = sinf(a);\n"
        + "      mcosr = cosf(a);\n"
        + "    }\n"
        + "    float msinp, mcosp;\n"
        + "    {\n"
        + "      float a = m4_2 * phi1;\n"
        + "      msinp = sinf(a);\n"
        + "      mcosp = cosf(a);\n"
        + "    }\n"
        + "    float pr1 = an2_1 * powf(fabsf(mcosr), __superShape3d_n2_1) + bn3_1 * powf(fabsf(msinr), __superShape3d_n3_1);\n"
        + "    float pr2 = an2_2 * powf(fabsf(mcosp), __superShape3d_n2_2) + bn3_2 * powf(fabsf(msinp), __superShape3d_n3_2);\n"
        + "    float r1 = powf(pr1, n1n_1) + __superShape3d_spiral * rho1;\n"
        + "    float r2 = powf(pr2, n1n_2);\n"
        + "\n"
        + "    if (lroundf(__superShape3d_toroidmap) == 1) {\n"
        + "      __px += __superShape3d * cosr * (r1 + r2 * cosp);\n"
        + "      __py += __superShape3d * sinr * (r1 + r2 * cosp);\n"
        + "      __pz += __superShape3d * r2 * sinp;\n"
        + "    } else {\n"
        + "      __px += __superShape3d * r1 * cosr * r2 * cosp;\n"
        + "      __py += __superShape3d * r1 * sinr * r2 * cosp;\n"
        + "      __pz += __superShape3d * r2 * sinp;\n"
        + "    }\n";
  }
}
