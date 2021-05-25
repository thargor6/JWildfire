/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

  Copyright (C) 2016 Nicolaus Anderson
  
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

/**
 * @author eralex61, transcribed by chronologicaldot, fixed by thargor6
 */
public class MobiusNFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RE_A = "re_a";
  private static final String PARAM_RE_B = "re_b";
  private static final String PARAM_RE_C = "re_c";
  private static final String PARAM_RE_D = "re_d";
  private static final String PARAM_IM_A = "im_a";
  private static final String PARAM_IM_B = "im_b";
  private static final String PARAM_IM_C = "im_c";
  private static final String PARAM_IM_D = "im_d";
  private static final String PARAM_POWER = "power";
  private static final String PARAM_DIST = "dist";

  private static final String[] params = {PARAM_RE_A, PARAM_RE_B, PARAM_RE_C, PARAM_RE_D, PARAM_IM_A, PARAM_IM_B, PARAM_IM_C, PARAM_IM_D, PARAM_POWER, PARAM_DIST};

  private double re_a = 1.0, re_b = 0.0, re_c = 0.0, re_d = 1.0, im_a = 0.0, im_b = 0.0, im_c = 0.0, im_d = 0.0, power = 1.0, dist = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    double realU, imagU, realV, imagV, radV, x, y, z, r, alpha, sina, cosa, n;

    // Transformation into N-power space
    z = 4.0 * dist / power;
    r = pow(pAffineTP.getPrecalcSqrt(), z);

    alpha = atan2(pAffineTP.y, pAffineTP.x) * power;
    sina = sin(alpha);
    cosa = cos(alpha);
    x = r * cosa;
    y = r * sina;

    // We perform what appears to be a rotation matrix and shift,
    // letting x and y form a matrix through which we modify the points [realA,imagA] and [realC,imagC]
    //[Ux,Uy] = [x,-y; y,-x]*[realA, imagA] + [realB, imagB]
    realU = re_a * x - im_a * y + re_b;
    imagU = re_a * y + im_a * x + im_b;
    realV = re_c * x - im_c * y + re_d;
    imagV = re_c * y + im_c * x + im_d;
    radV = sqr(realV) + sqr(imagV);

    x = (realU * realV + imagU * imagV) / radV;
    y = (imagU * realV - realU * imagV) / radV;

    // Reverse transformation into N-power space
    z = 1.0 / z;
    r = pow(sqrt(sqr(x) + sqr(y)), z);

    n = floor(power * pContext.random());
    alpha = (atan2(y, x) + n * M_2PI) / floor(power);
    sina = sin(alpha);
    cosa = cos(alpha);

    pVarTP.x += pAmount * r * cosa;
    pVarTP.y += pAmount * r * sina;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "mobiusN";
  }

  @Override
  public String[] getParameterNames() {
    return params;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{re_a, re_b, re_c, re_d, im_a, im_b, im_c, im_d, power, dist};
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"MobiusNRe_A", "MobiusNRe_B", "MobiusNRe_C", "MobiusNRe_D", "MobiusNIm_A", "MobiusNIm_B", "MobiusNIm_C", "MobiusNIm_D", "MobiusN_Power", "MobiusN_Dist"};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RE_A.equalsIgnoreCase(pName)) {
      re_a = pValue;
    } else if (PARAM_RE_B.equalsIgnoreCase(pName)) {
      re_b = pValue;
    } else if (PARAM_RE_C.equalsIgnoreCase(pName)) {
      re_c = pValue;
    } else if (PARAM_RE_D.equalsIgnoreCase(pName)) {
      re_d = pValue;
    } else if (PARAM_IM_A.equalsIgnoreCase(pName)) {
      im_a = pValue;
    } else if (PARAM_IM_B.equalsIgnoreCase(pName)) {
      im_b = pValue;
    } else if (PARAM_IM_C.equalsIgnoreCase(pName)) {
      im_c = pValue;
    } else if (PARAM_IM_D.equalsIgnoreCase(pName)) {
      im_d = pValue;
    } else if (PARAM_POWER.equalsIgnoreCase(pName)) {
      power = pValue;
    } else if (PARAM_DIST.equalsIgnoreCase(pName)) {
      dist = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    if (fabs(power) < 1.0)
      power = 1.0;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float power = varpar->mobiusN_power;\n"
        + "    if (fabsf(power) < 1.0)\n"
        + "      power = 1.0;\n"
        + "float realU, imagU, realV, imagV, radV, x, y, z, r, alpha, sina, cosa, n;\n"
        + "    z = 4.0f * varpar->mobiusN_dist / power;\n"
        + "    r = powf(__r, z);\n"
        + "    alpha = atan2f(__y, __x) * power;\n"
        + "    sina = sinf(alpha);\n"
        + "    cosa = cosf(alpha);\n"
        + "    x = r * cosa;\n"
        + "    y = r * sina;\n"
        + "    realU = varpar->mobiusN_re_a * x - varpar->mobiusN_im_a * y + varpar->mobiusN_re_b;\n"
        + "    imagU = varpar->mobiusN_re_a * y + varpar->mobiusN_im_a * x + varpar->mobiusN_im_b;\n"
        + "    realV = varpar->mobiusN_re_c * x - varpar->mobiusN_im_c * y + varpar->mobiusN_re_d;\n"
        + "    imagV = varpar->mobiusN_re_c * y + varpar->mobiusN_im_c * x + varpar->mobiusN_im_d;\n"
        + "    radV = realV*realV + imagV*imagV;\n"
        + "\n"
        + "    x = (realU * realV + imagU * imagV) / radV;\n"
        + "    y = (imagU * realV - realU * imagV) / radV;\n"
        + "    z = 1.0f / z;\n"
        + "    r = powf(sqrtf(x*x + y*y), z);\n"
        + "    n = floorf(power * RANDFLOAT());\n"
        + "    alpha = (atan2f(y, x) + n * 2.0f*PI) / floorf(power);\n"
        + "    sina = sinf(alpha);\n"
        + "    cosa = cosf(alpha);\n"
        + "    __px += varpar->mobiusN * r * cosa;\n"
        + "    __py += varpar->mobiusN * r * sina;\n"
        + (context.isPreserveZCoordinate() ? "__pz += varpar->mobiusN * __z;\n" : "");
  }
}