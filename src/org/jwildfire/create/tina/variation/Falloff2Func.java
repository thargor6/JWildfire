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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Falloff2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCATTER = "scatter";
  private static final String PARAM_MINDIST = "mindist";
  private static final String PARAM_MUL_X = "mul_x";
  private static final String PARAM_MUL_Y = "mul_y";
  private static final String PARAM_MUL_Z = "mul_z";
  private static final String PARAM_MUL_C = "mul_c";
  private static final String PARAM_X0 = "x0";
  private static final String PARAM_Y0 = "y0";
  private static final String PARAM_Z0 = "z0";
  private static final String PARAM_INVERT = "invert";
  private static final String PARAM_TYPE = "type";

  private static final String[] paramNames = {PARAM_SCATTER, PARAM_MINDIST, PARAM_MUL_X, PARAM_MUL_Y, PARAM_MUL_Z, PARAM_MUL_C, PARAM_X0, PARAM_Y0, PARAM_Z0, PARAM_INVERT, PARAM_TYPE};

  private double scatter = 1.0;
  private double mindist = 0.5;
  private double mul_x = 1.0;
  private double mul_y = 1.0;
  private double mul_z = 0.0;
  private double mul_c = 0.0;
  private double x0 = 0.0;
  private double y0 = 0.0;
  private double z0 = 0.0;
  private int invert = 0;
  protected int type = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* falloff2 by Xyrus02 */
    switch (type) {
      case 1:
        calcFunctionRadial(pContext, pXForm, pAffineTP.x, pAffineTP.y, pAffineTP.z, pAffineTP, pVarTP, pAmount);
        break;
      case 2:
        calcFunctionGaussian(pContext, pXForm, pAffineTP.x, pAffineTP.y, pAffineTP.z, pAffineTP, pVarTP, pAmount);
        break;
      default:
        calcFunction(pContext, pXForm, pAffineTP.x, pAffineTP.y, pAffineTP.z, pAffineTP, pVarTP, pAmount);
    }
  }

  protected void calcFunctionRadial(FlameTransformationContext pContext, XForm pXForm, double pIn_x, double pIn_y, double pIn_z, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    double r_in = sqrt(sqr(pIn_x) + sqr(pIn_y) + sqr(pIn_z)) + 1e-6;
    double d = sqrt(sqr(pIn_x - x0) + sqr(pIn_y - y0) + sqr(pIn_z - z0));
    if (invert != 0)
      d = 1 - d;
    if (d < 0)
      d = 0;
    d = (d - mindist) * _rmax;
    if (d < 0)
      d = 0;

    double sigma = asin(pIn_z / r_in) + mul_z * pContext.random() * d;
    double phi = atan2(pIn_y, pIn_x) + mul_y * pContext.random() * d;
    double r = r_in + mul_x * pContext.random() * d;

    double sins = sin(sigma);
    double coss = cos(sigma);

    double sinp = sin(phi);
    double cosp = cos(phi);

    pVarTP.x += pAmount * (r * coss * cosp);
    pVarTP.y += pAmount * (r * coss * sinp);
    pVarTP.z += pAmount * (sins);
    pVarTP.color = fabs(frac(pVarTP.color + mul_c * pContext.random() * d));
  }

  protected void calcFunctionGaussian(FlameTransformationContext pContext, XForm pXForm, double pIn_x, double pIn_y, double pIn_z, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double d = sqrt(sqr(pIn_x - x0) + sqr(pIn_y - y0) + sqr(pIn_z - z0));
    if (invert != 0)
      d = 1 - d;
    if (d < 0)
      d = 0;
    d = (d - mindist) * _rmax;
    if (d < 0)
      d = 0;

    double sigma = d * pContext.random() * 2 * M_PI;
    double phi = d * pContext.random() * M_PI;
    double r = d * pContext.random();

    double sins = sin(sigma);
    double coss = cos(sigma);

    double sinp = sin(phi);
    double cosp = cos(phi);

    pVarTP.x += pAmount * (pIn_x + mul_x * r * coss * cosp);
    pVarTP.y += pAmount * (pIn_y + mul_y * r * coss * sinp);
    pVarTP.z += pAmount * (pIn_z + mul_z * r * sins);
    pVarTP.color = fabs(frac(pVarTP.color + mul_c * pContext.random() * d));
  }

  protected void calcFunction(FlameTransformationContext pContext, XForm pXForm, double pIn_x, double pIn_y, double pIn_z, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double d = sqrt(sqr(pIn_x - x0) + sqr(pIn_y - y0) + sqr(pIn_z - z0));
    if (invert != 0)
      d = 1 - d;
    if (d < 0)
      d = 0;
    d = (d - mindist) * _rmax;
    if (d < 0)
      d = 0;

    pVarTP.x += pAmount * (pIn_x + mul_x * pContext.random() * d);
    pVarTP.y += pAmount * (pIn_y + mul_y * pContext.random() * d);
    pVarTP.z += pAmount * (pIn_z + mul_z * pContext.random() * d);
    pVarTP.color = fabs(frac(pVarTP.color + mul_c * pContext.random() * d));
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{scatter, mindist, mul_x, mul_y, mul_z, mul_c, x0, y0, z0, invert, type};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCATTER.equalsIgnoreCase(pName)) {
      scatter = pValue;
      if (scatter < 1.0e-6)
        scatter = 1.0e-6;
    } else if (PARAM_MINDIST.equalsIgnoreCase(pName)) {
      mindist = pValue;
      if (mindist < 0.0)
        mindist = 0.0;
    } else if (PARAM_MUL_X.equalsIgnoreCase(pName))
      mul_x = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_MUL_Y.equalsIgnoreCase(pName))
      mul_y = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_MUL_Z.equalsIgnoreCase(pName))
      mul_z = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_MUL_C.equalsIgnoreCase(pName))
      mul_c = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_X0.equalsIgnoreCase(pName))
      x0 = pValue;
    else if (PARAM_Y0.equalsIgnoreCase(pName))
      y0 = pValue;
    else if (PARAM_Z0.equalsIgnoreCase(pName))
      z0 = pValue;
    else if (PARAM_INVERT.equalsIgnoreCase(pName))
      invert = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_TYPE.equalsIgnoreCase(pName))
      type = limitIntVal(Tools.FTOI(pValue), 0, 2);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "falloff2";
  }

  private double _rmax;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _rmax = 0.04 * scatter;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_BLUR, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float _rmax = 0.04 * __falloff2_scatter;\n"
        + "float pIn_x = __x;\n"
        + "float pIn_y = __y;\n"
        + "float pIn_z = __z;\n"
        + "switch (lroundf(__falloff2_type)) {\n"
        + "      case 1:\n"
        + "        {\n"
        + "float r_in = sqrtf(pIn_x*pIn_x + pIn_y*pIn_y + pIn_z*pIn_z) + 1e-6;\n"
        + "    float d = sqrtf(sqrf(pIn_x - __falloff2_x0) + sqrf(pIn_y - __falloff2_y0) + sqrf(pIn_z - __falloff2_z0));\n"
        + "    if (lroundf(__falloff2_invert) != 0)\n"
        + "      d = 1 - d;\n"
        + "    if (d < 0)\n"
        + "      d = 0;\n"
        + "    d = (d - __falloff2_mindist) * _rmax;\n"
        + "    if (d < 0)\n"
        + "      d = 0;\n"
        + "\n"
        + "    float sigma = asinf(pIn_z / r_in) + __falloff2_mul_z * RANDFLOAT() * d;\n"
        + "    float phi = atan2f(pIn_y, pIn_x) + __falloff2_mul_y * RANDFLOAT() * d;\n"
        + "    float r = r_in + __falloff2_mul_x * RANDFLOAT() * d;\n"
        + "\n"
        + "    float sins = sinf(sigma);\n"
        + "    float coss = cosf(sigma);\n"
        + "\n"
        + "    float sinp = sinf(phi);\n"
        + "    float cosp = cosf(phi);\n"
        + "\n"
        + "    __px += __falloff2 * (r * coss * cosp);\n"
        + "    __py += __falloff2 * (r * coss * sinp);\n"
        + "    __pz += __falloff2 * (sins);\n"
        + "    __pal = fabsf(fracf(__pal + __falloff2_mul_c * RANDFLOAT() * d));\n"
        + "        }\n"
        + "        break;\n"
        + "      case 2:\n"
        + "        {\n"
        + "float d = sqrtf(sqrf(pIn_x - __falloff2_x0) + sqrf(pIn_y - __falloff2_y0) + sqrf(pIn_z - __falloff2_z0));\n"
        + "    if (lroundf(__falloff2_invert) != 0)\n"
        + "      d = 1 - d;\n"
        + "    if (d < 0)\n"
        + "      d = 0;\n"
        + "    d = (d - __falloff2_mindist) * _rmax;\n"
        + "    if (d < 0)\n"
        + "      d = 0;\n"
        + "\n"
        + "    float sigma = d * RANDFLOAT() * 2 * PI;\n"
        + "    float phi = d * RANDFLOAT() * PI;\n"
        + "    float r = d * RANDFLOAT();\n"
        + "\n"
        + "    float sins = sinf(sigma);\n"
        + "    float coss = cosf(sigma);\n"
        + "\n"
        + "    float sinp = sinf(phi);\n"
        + "    float cosp = cosf(phi);\n"
        + "\n"
        + "    __px += __falloff2 * (pIn_x + __falloff2_mul_x * r * coss * cosp);\n"
        + "    __py += __falloff2 * (pIn_y + __falloff2_mul_y * r * coss * sinp);\n"
        + "    __pz += __falloff2 * (pIn_z + __falloff2_mul_z * r * sins);\n"
        + "    __pal = fabsf(fracf(__pal + __falloff2_mul_c * RANDFLOAT() * d));\n"
        + "        }\n"
        + "        break;\n"
        + "      default:\n"
        + "        {\n"
        + "float d = sqrtf(sqrf(pIn_x - __falloff2_x0) + sqrf(pIn_y - __falloff2_y0) + sqrf(pIn_z - __falloff2_z0));\n"
        + "    if (lroundf(__falloff2_invert) != 0)\n"
        + "      d = 1 - d;\n"
        + "    if (d < 0)\n"
        + "      d = 0;\n"
        + "    d = (d - __falloff2_mindist) * _rmax;\n"
        + "    if (d < 0)\n"
        + "      d = 0;\n"
        + "\n"
        + "    __px += __falloff2 * (pIn_x + __falloff2_mul_x * RANDFLOAT() * d);\n"
        + "    __py += __falloff2 * (pIn_y + __falloff2_mul_y * RANDFLOAT() * d);\n"
        + "    __pz += __falloff2 * (pIn_z + __falloff2_mul_z * RANDFLOAT() * d);\n"
        + "    __pal = fabsf(fracf(__pal + __falloff2_mul_c * RANDFLOAT() * d));\n"
        + "        }\n"
        + "    }\n";
  }
}
